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
        
    UltrasonicLocalizer.localize();
    LightLocalizer.localize();
    System.out.println("Done localizing");
    odometer.printPosition();
    
//    Navigation.directTravelTo(new Point(1,7));
    
    var remainingWaypoints = waypoints.subList(1, waypoints.size()); // other than starting point   
    remainingWaypoints.forEach(point -> {
      System.out.println("Travel to " + point + ".");
      Navigation.travelTo(point);
      odometer.printPosition();
    });
    
    Movement.stopMotors();
    
    odometer.printPositionInTileLengths();
    System.exit(0);
  }
  
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
