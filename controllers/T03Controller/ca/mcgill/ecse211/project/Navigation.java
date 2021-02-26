package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.*;
import static java.lang.Math.*;

import ca.mcgill.ecse211.playingfield.Point;

/**
 * The Navigation class is used to make the robot navigate around the playing field.
 */
public class Navigation {
  
  /** Do not instantiate this class. */
  private Navigation() {}
  
  /**
   * Travels to destination taking obstacles into account.
   *
   * <p>TODO Describe your algorithm here in detail (then remove these instructions).
   */
  public static void travelTo(Point destination) {
    
    double[] curXYT = odometer.getXyt();
    double x = curXYT[0];
    double y = curXYT[1];
    Point current = new Point(x, y);
    
    double[] slopeParams = calculateLinearSlope(current, destination);
    double m = slopeParams[0];
    double b = slopeParams[1];
    boolean stopCondition = checkIfPointOnSlope(x, y, m, b);
    
    while(!stopCondition) {
      wallFollower();
    }
    
    //...
    
  }
  // TODO
  // Hint: One way to avoid an obstacle is to calculate a simple path around it and call
  // directTravelTo() to get to points on that path before returning to the original trajectory
  
  /** Travels directly (in a straight line) to the given destination. */
  public static void directTravelTo(Point destination) {
    // TODO
    // Think carefully about how you would integrate line detection here, if necessary
    // Don't forget that destination.x and destination.y are in feet, not meters
  }
  
  /**
   * Turns the robot with a minimal angle towards the given input angle in degrees, no matter what
   * its current orientation is. This method is different from {@code turnBy()}.
   */
  public static void turnTo(double angle) {
    // TODO
    // Hint: You can do this in one line by reusing some helper methods declared in this class
    
  }

  /**
   * Returns the angle that the robot should point towards to face the destination in degrees.
   * This function does not depend on the robot's current theta.
   */
  public static double getDestinationAngle(Point current, Point destination) {
    return 0; // TODO
  }
  
  /** Returns the signed minimal angle in degrees from initial angle to destination angle (deg). */
  public static double minimalAngle(double initialAngle, double destAngle) {
    initialAngle %= 360; // make sure
    destAngle %= 360;
    double toTurn = (destAngle - initialAngle + 540) % 360 - 180;
    return toTurn;
  }
  
  /** Returns the distance between the two points in tile lengths (feet). */
  //https://math.stackexchange.com/questions/110080/shortest-way-to-achieve-target-angle
  public static double distanceBetween(Point p1, Point p2) {
    double dx_squared = Math.pow((p2.x - p1.x), 2);
    double dy_squared = Math.pow((p2.y - p1.y), 2);
    double dist = Math.sqrt(dx_squared + dy_squared);
    return dist;
  }
  
  // TODO Bring Navigation-related helper methods from Labs 2 and 3 here
  // You can also add other helper methods here, but remember to document them with Javadoc (/**)!

  
  private static double[] calculateLinearSlope(Point p1, Point p2) {
    // Calculate y=mx+b which crosses p1, p2
    double m = (p2.y - p1.y) / (p2.x - p1.x);
    double b = p1.y - m*(p2.x);
    double[] params = {m,b};
    return params;
  }
  
  private static boolean checkIfPointOnSlope(Double x, Double y, Double m, Double b){
      Double curY = m*x+b;
      return curY.compareTo(y)==0;
  }
  
  private static void wallFollower() {
    // dummy
  }
  
  
  
}
