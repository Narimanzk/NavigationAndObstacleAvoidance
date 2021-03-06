package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.*;

import ca.mcgill.ecse211.playingfield.Point;
import java.io.IOException;
import java.lang.Thread;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import simlejos.ExecutionController;

import ca.mcgill.ecse211.project.Navigation.*;




/**
 * Main class of the program.
 */
public class Main {
  public static List<Point> waypoints;
  public static final int WALL_DIST = 20;
  public static final int WALL_DIST_ERR_THRESH = 0;
  private static int[] motorSpeeds = new int[2];
  private static final int LEFT = 0;
  private static final int RIGHT = 1;
  public static final int MOTOR_LOW = 80;
  public static final int MOTOR_HIGH = 300;
  private static int distance_error;
  private static int prevDistance;
  private static int distance;
  private static int invalidSampleCount;
  private static float[] usData = new float[usSensor.sampleSize()];
  
  private static boolean usRotate = true;
  
  private static double[] params = {0,0};
  private static int counter  = 0;
  
  private static boolean diffFlag = false;
  private static Point startPoint;
  
  private static final double EPSILON = 0.012; 
  
  /** The main entry point. */
  public static void main(String[] args) {
    //waypoints = loadWaypointsFromFile(WAYPOINTS_PATH);
    
    // Run a few physics steps to make sure everything is initialized and has settled properly
    ExecutionController.performPhysicsSteps(INITIAL_NUMBER_OF_PHYSICS_STEPS);
    ExecutionController.setNumberOfParties(NUMBER_OF_THREADS);
    ExecutionController.performPhysicsStepsInBackground(PHYSICS_STEP_PERIOD);
    new Thread(odometer).start();
    
    leftMotor.setSpeed(300);
    rightMotor.setSpeed(300);
    while(true) {
        leftMotor.forward();
        rightMotor.forward();
        System.out.println(readUsDistance());
        if (readUsDistance() < 15) {
          startPoint = new Point(odometer.getXyt()[0]/0.3048,odometer.getXyt()[1]/0.3048);
          break; 
        }
    }
    leftMotor.stop();
    rightMotor.stop();
    while (true) {
      controller(readUsDistance(), motorSpeeds);
      setMotorSpeeds();
      leftMotor.forward();
      rightMotor.forward();
      double[] curXyt = odometer.getXyt();
      double x = curXyt[0]/0.3048;
      double y = curXyt[1]/0.3048;
      Point current = new Point(x, y);
      boolean stopCond = checkIfPointOnSlope(startPoint, current);
      if(stopCond) {
        leftMotor.stop();
        rightMotor.stop();
        diffFlag = false;
        break;
      }
    
    }
    double angle = odometer.getXyt()[2];
    Movement.turnBy(-angle);
  }
  
  private static void calculateLinearSlope(Point p1, Point p2) {
    double m = (p2.y - p1.y) / (p2.x - p1.x);
    double b = p1.y - m * (p2.x);
    params[0] = m;
    params[1] = b;
  }
  
  private static boolean checkIfPointOnSlope(Point start, Point curr) {
    double xDiff = Math.abs(start.x - curr.x); 
    double yDiff = Math.abs(start.y - curr.y);
    
    return((xDiff < EPSILON && !usRotate) || (yDiff < EPSILON && !usRotate));
  }
  
  /** Sets the speeds of the left and right motors from the motorSpeeds array. */
  public static void setMotorSpeeds() {
    leftMotor.setSpeed(motorSpeeds[LEFT]);
    rightMotor.setSpeed(motorSpeeds[RIGHT]);
  }
  
  /**
   * Process a movement based on the US distance passed in (eg, Bang-Bang or Proportional style).
   *
   * @param distance the distance to the wall in cm
   * @param motorSpeeds output parameter you need to set
   */
  public static void controller(int distance, int[] motorSpeeds) {
    int leftSpeed = MOTOR_HIGH;
    int rightSpeed = MOTOR_HIGH;
    System.out.println("US value: " + distance);
    if (distance > 30 && usRotate) {
      leftMotor.stop();
      rightMotor.stop();
      usMotor.setSpeed(100);
      usMotor.rotate(45, false);
      Movement.turnBy(-70);
      Movement.moveStraightFor(0.1295);
      usMotor.rotate(45, false);
      usRotate = false;
    }
      distance_error = WALL_DIST - distance;

      //When the sensor detects no obstacles the robot goes straight
      if (Math.abs(distance_error) <= WALL_DIST_ERR_THRESH) {
        leftSpeed = MOTOR_HIGH;
        rightSpeed = MOTOR_HIGH;
      //When the sensor detects the robot is getting too close to the wall it goes to the right
      } else if (distance_error > 0) {
        rightSpeed = MOTOR_HIGH + MOTOR_LOW;
        leftSpeed = MOTOR_LOW;
      //When the sensor detects the robot is getting too far from the wall it goes to the left
      } else if (distance_error < 0) {
        rightSpeed = MOTOR_LOW;
        leftSpeed = MOTOR_HIGH + MOTOR_LOW;
      }
    
    //Sets the speed of left and right motors
    motorSpeeds[LEFT] = leftSpeed;
    motorSpeeds[RIGHT] = rightSpeed;
  }
  
  /** Returns the filtered distance between the US sensor and an obstacle in cm. */
  public static int readUsDistance() {
    int[] filterArr = new int[21]; 
    
    for (int i = 0; i < filterArr.length; i++) {
      usSensor.fetchSample(usData, 0);
      filterArr[i] = (int) (usData[0] * 100); 
      //ExecutionController.sleepFor(60);
    }
    return filter(filterArr);
  }
  
  /**
   * Rudimentary filter - toss out invalid samples corresponding to null signal.
   *
   * @param distance raw distance array measured by the sensor in cm
   * @return the filtered distance in cm
   */
  static int filter(int[] arr) {
    Arrays.sort(arr);
    //Median distance value
    distance = arr[10];
    if (distance >= MAX_SENSOR_DIST && invalidSampleCount < INVALID_SAMPLE_LIMIT) {
      // bad value, increment the filter value and return the distance remembered from before
      invalidSampleCount++;
      return prevDistance;
    } else {
      if (distance < MAX_SENSOR_DIST) {
        // distance went below MAX_SENSOR_DIST: reset filter and remember the input distance.
        invalidSampleCount = 0;
      }
      prevDistance = distance;
      return distance;
    }
  }
}
//    Movement.turnBy(90.0); 
//    Movement.moveStraightFor(0.1295); 
//    Movement.turnBy(-90.0); 
//    Movement.moveStraightFor(0.439); 
//    Movement.turnBy(-90.0); 
//    Movement.moveStraightFor(0.1295); 
//    Movement.turnBy(90.0);
    //var startingPoint = waypoints.get(0);
    //odometer.setXyt(startingPoint.x * TILE_SIZE, startingPoint.y * TILE_SIZE, 0);
    //new Thread(odometer).start();
        
    // TODO Localize in the corner like in the previous lab
    //UltrasonicLocalizer.localize();
    //LightLocalizer.localize();    
    
//    Point p = new Point(3,3);
//    Navigation.directTravelTo(p);
    
//    System.out.println("Done localizing");
//    
//    var remainingWaypoints = waypoints.subList(1, waypoints.size()); // other than starting point
//    
//    // TODO Travel to the remaining waypoints in order, taking obstacles into account
//    remainingWaypoints.forEach(point -> {
//      System.out.println("Travel to " + point + ".");
//      Navigation.directTravelTo(point);
//      odometer.printPosition();
//    });
//    
//    odometer.printPositionInTileLengths();
//    System.exit(0);
//  }
//  
//  /** Loads waypoints from given file path. */
//  public static List<Point> loadWaypointsFromFile(Path filepath) {
//    try {
//      return parseWaypoints(Files.readAllLines(WAYPOINTS_PATH));
//    } catch (IOException e) {
//      System.err.println("Could not open file: " + WAYPOINTS_PATH);
//      System.exit(-1);
//    }
//    return null;
//  }
//  
//  /** Parses input lines into block vectors. */
//  public static List<Point> parseWaypoints(List<String> points) {
//    return points.stream().filter(line -> !line.isBlank() && !line.startsWith("#"))
//        .map(line -> line.split(","))
//        .map(xy -> new Point(Double.parseDouble(xy[0]), Double.parseDouble(xy[1])))
//        .collect(Collectors.toUnmodifiableList());
//  }
//
//}
