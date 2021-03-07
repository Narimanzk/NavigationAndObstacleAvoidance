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
    waypoints = loadWaypointsFromFile(WAYPOINTS_PATH);
    
    // Run a few physics steps to make sure everything is initialized and has settled properly
    ExecutionController.performPhysicsSteps(INITIAL_NUMBER_OF_PHYSICS_STEPS);
    ExecutionController.setNumberOfParties(NUMBER_OF_THREADS);
    ExecutionController.performPhysicsStepsInBackground(PHYSICS_STEP_PERIOD);
    
    var startingPoint = waypoints.get(0);
    odometer.setXyt(startingPoint.x * TILE_SIZE, startingPoint.y * TILE_SIZE, 0);
    new Thread(odometer).start();
        
//     TODO Localize in the corner like in the previous lab
    UltrasonicLocalizer.localize();
    LightLocalizer.localize();    
    
    Navigation.travelTo(new Point(1,7));
    
  }


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
  /** Loads waypoints from given file path. */
  public static List<Point> loadWaypointsFromFile(Path filepath) {
    try {
      return parseWaypoints(Files.readAllLines(WAYPOINTS_PATH));
    } catch (IOException e) {
      System.err.println("Could not open file: " + WAYPOINTS_PATH);
      System.exit(-1);
    }
    return null;
  }
  
  /** Parses input lines into block vectors. */
  public static List<Point> parseWaypoints(List<String> points) {
    return points.stream().filter(line -> !line.isBlank() && !line.startsWith("#"))
        .map(line -> line.split(","))
        .map(xy -> new Point(Double.parseDouble(xy[0]), Double.parseDouble(xy[1])))
        .collect(Collectors.toUnmodifiableList());
  }
}
//
//}
