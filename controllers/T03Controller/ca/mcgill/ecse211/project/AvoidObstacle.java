package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.*;
import ca.mcgill.ecse211.playingfield.Point;
import java.util.Arrays;
import simlejos.ExecutionController;

public class AvoidObstacle {
  public static final int WALL_DIST_LEFT = 13;
  public static final int WALL_DIST_RIGHT = 10;
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
  private static boolean notReturningFlag = true;
  private static final double EPSILON = 0.012;
  private static boolean turningRight = false;
  private static int totalMotorRotation = 0;
  
  public static int decideLeftRight() { 
    turnUsMotor(-90);
    int distL = readUsDistance();
    
    turnUsMotor(180);
    int distR = readUsDistance();
    
    int decision;
    if(distL > distR) {
      System.out.println("Safer to turn left");
      decision = LEFT;
    } else {
      System.out.println("Safer to turn right");
      decision = RIGHT;
    }
    
    turnUsMotor(-90);
    return decision;
  }
  
  public static void avoid(Point startPoint, Point endPoint) {
    Movement.stopMotors();
    double initialAngle = odometer.getXyt()[2];
    int decision = decideLeftRight();

    if (decision == LEFT) {
      turningRight = false;
    } else {
      turningRight = true;
    }

    double[] params = getLinearSlope(startPoint, endPoint);
    
    wallFollow(params, startPoint);
    Navigation.turnTo(initialAngle);
    
    notReturningFlag = true;
  }

  public static void wallFollow(double[] slopeParams, Point startPoint) {
    System.out.println("m = "+slopeParams[0]+"\tb = "+slopeParams[1]);
    while (true) {
      controller(readUsDistance(), motorSpeeds);
      setMotorSpeeds();
      Movement.drive();
      Point curPoint = Navigation.getCurrentPoint_feet();
      if(checkIfPointOnSlope(startPoint, Navigation.getCurrentPoint_feet())) {
        Movement.stopMotors();
        UsReturnToDefault();
        break;
      }
//      if(checkIfPointOnSlope(slopeParams[0], slopeParams[1], startPoint, Navigation.getCurrentPoint_feet())) {
//        Movement.stopMotors();
//        break;
//      }
    }
  }
  
  private static boolean compareRoughly(double a, double b, double margin) {
    double diff = Math.abs(a-b);
    return (diff < margin); // if they're close enough return true
  }
  
  private static double[] getLinearSlope(Point p1, Point p2) {
//    System.out.println("Getting linear slope");
//    System.out.println("P1 : ("+p1.x+","+p1.y+")");
//    System.out.println("P2 : ("+p2.x+","+p2.y+")");
    double m;
    if(compareRoughly(p1.x, p2.x, 0.8) || compareRoughly(p1.y, p2.y, 0.8)) {
      m = 0;
    } else {
      m = (p2.y - p1.y) / (p2.x - p1.x);
    }
    double b = p1.y - m * (p2.x);
    double[] params = {m, b};
    return params;
  }

//  private static boolean checkIfPointOnSlope(double m, double b, Point start, Point curr) {
//    boolean dist = distIndicator(start, curr);
//    if(m==0) {
//      double xDiff = Math.abs(start.x - curr.x); // just get the difference between x and y coords of points, check if either is less than epsilon
//      double yDiff = Math.abs(start.y - curr.y);
//      System.out.println(yDiff+"\t"+xDiff+"\t"+dist);
//      return((xDiff < EPSILON && !notReturningFlag && dist) || (yDiff < EPSILON && !notReturningFlag && dist));    
//    } else {
//      double curY = m * curr.x + b;
//      double diff = Math.abs(curY - start.y);
//      System.out.println(diff);
//      return(diff < EPSILON && !notReturningFlag && dist); 
//    }
//  }
  


  private static boolean checkIfPointOnSlope(Point start, Point curr) {
    double xDiff = Math.abs(start.x - curr.x);
    double yDiff = Math.abs(start.y - curr.y);
    boolean dist = distIndicator(start, curr);
    return((xDiff < EPSILON && !notReturningFlag && dist) || (yDiff < EPSILON && !notReturningFlag && dist));
  }
  
  private static boolean distIndicator(Point start, Point curr) {
    double distance = Navigation.distanceBetween(start, curr);
    if(distance > 0.5) {
      return true;
    } return false;
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
    
    correctController();
    
    distance_error = turningRight? WALL_DIST_RIGHT - distance : WALL_DIST_LEFT - distance;

    //When the sensor detects no obstacles the robot goes straight
    if (Math.abs(distance_error) <= WALL_DIST_ERR_THRESH) {
      leftSpeed = MOTOR_HIGH;
      rightSpeed = MOTOR_HIGH;
      //When the sensor detects the robot is getting too close to the wall it goes away from wall
    } else if (distance_error > 0) {
      rightSpeed = turningRight ? MOTOR_LOW :  MOTOR_HIGH + MOTOR_LOW;
      leftSpeed = turningRight ? MOTOR_HIGH + MOTOR_LOW : MOTOR_LOW;
      //When the sensor detects the robot is getting too far from the wall it goes towards the wall
    } else if (distance_error < 0) {
      rightSpeed = turningRight ? MOTOR_HIGH + MOTOR_LOW : MOTOR_LOW;
      leftSpeed = turningRight ? MOTOR_LOW : MOTOR_HIGH + MOTOR_LOW;
    }
    //      //Sets the speed of left and right motors
    motorSpeeds[LEFT] = leftSpeed;
    motorSpeeds[RIGHT] = rightSpeed;
  }
  
  private static void correctController() {
    if (distance > 10 && notReturningFlag) {
            
      int motorRotate = 45; 
      int robotRotate = -70; 
      
      if(turningRight) {
        motorRotate = -45; 
        robotRotate = 90;
      }
      
      leftMotor.stop();
      rightMotor.stop();
      turnUsMotor(motorRotate);
      
      Movement.turnBy(robotRotate);
      Movement.moveStraightFor(0.04475);
      turnUsMotor(motorRotate);
      
      notReturningFlag = false;
    }
  }
  
  
  public static void turnUsMotor(int amount) {
    usMotor.setSpeed(100);
    usMotor.rotate(amount, false);
    totalMotorRotation += amount; // update global amount to keep track.
    usMotor.stop();
  }
  
  // return motor to be facing forwards.
  public static void UsReturnToDefault() {
    turnUsMotor(-totalMotorRotation);
  }
  
  

  /** Returns the filtered distance between the US sensor and an obstacle in cm. */
  public static int readUsDistance() {
    int[] filterArr = new int[21]; 
    for (int i = 0; i < filterArr.length; i++) {
      usSensor.fetchSample(usData, 0);
      filterArr[i] = (int) (usData[0] * 100); 
      ExecutionController.sleepFor(60);
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
    distance = arr[10];
    if (distance >= MAX_SENSOR_DIST && invalidSampleCount < INVALID_SAMPLE_LIMIT) {
      invalidSampleCount++;
      return prevDistance;
    } else {
      if (distance < MAX_SENSOR_DIST) {
        invalidSampleCount = 0;
      }
      prevDistance = distance;
      return distance;
    }
  }
}
