package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.*;
import ca.mcgill.ecse211.playingfield.Point;

public class Navigation {
  
  private Navigation() {}
  

  public static void travelTo(Point destination) {
    goAroundObstacle(destination); // remove this
    // TODO
    // Hint: One way to avoid an obstacle is to calculate a simple path around it and call
    // directTravelTo() to get to points on that path before returning to the original trajectory
    
    /**
     * Origional obstacle locations:
     * 1: x=2, y=1.9
     * 2: x=1.6, y=1.5
     * 3: x=1.5, y=0.35
     * 
     */
    
  }

  
  // TODO
  // Think carefully about how you would integrate line detection here, if necessary
  // Don't forget that destination.x and destination.y are in feet, not meters
  public static void directTravelTo(Point destination) {
    
    double[] xyt = odometer.getXyt();
    Point curPoint = new Point(toFeet(xyt[0]), toFeet(xyt[1]));
    double travelDist = toMeters(distanceBetween(curPoint, destination));
    
    if(travelDist>0.3) { // TODO: decide this 0.3 value
      double destTheta = getDestinationAngle(curPoint, destination);
      turnTo(destTheta);
      Movement.moveStraightFor(travelDist);
      // TODO: localize and correct odometer here
    }else{
      System.out.println("Already at destination.");
      // TODO: Localize and correct odometer here
    }
    
  }
  

  public static void turnTo(double angle) {
    Movement.turnBy(minimalAngle(Odometer.getOdometer().getXyt()[2], angle));
  }


  public static double getDestinationAngle(Point current, Point destination) {
    return (Math.toDegrees(
        Math.atan2(destination.x - current.x, destination.y - current.y)) + 360) % 360;
  }
  
  public static double minimalAngle(double initialAngle, double destAngle) {
    initialAngle %= 360; // make sure
    destAngle %= 360;
    double toTurn = (destAngle - initialAngle + 540) % 360 - 180;
    return toTurn;
  }
  

  public static double distanceBetween(Point p1, Point p2) {
    double dxSqr = Math.pow((p2.x - p1.x), 2);
    double dySqr = Math.pow((p2.y - p1.y), 2);
    double dist = Math.sqrt(dxSqr + dySqr);
    return dist;
  }
  
  public static boolean goAroundObstacle(Point destination) {
    double[] curXyt = odometer.getXyt();
    double x = curXyt[0];
    double y = curXyt[1];
    Point current = new Point(x, y);
    
    double[] slopeParams = calculateLinearSlope(current, destination);
    double m = slopeParams[0];
    double b = slopeParams[1];
    boolean stopCondition = checkIfPointOnSlope(x, y, m, b);
    return stopCondition;
  }
  
  private static double[] calculateLinearSlope(Point p1, Point p2) {
    double m = (p2.y - p1.y) / (p2.x - p1.x);
    double b = p1.y - m * (p2.x);
    double[] params = {m, b};
    return params;
  }
  
  private static boolean checkIfPointOnSlope(Double x, Double y, Double m, Double b) {
    Double curY = m * x + b;
    if(curY.compareTo(y)==0) {
      System.out.println("-------------------------------------");
      return true;
    } return false;
  }
  
  private static void wallFollower() {
    // dummy
  }
  
  public static double toFeet(double meters) {
    return 3.28084 * meters;
  }
  
  /*
   * Converts feet to meters
   * @param feet
   */
  public static double toMeters(double feet) {
    return feet / 3.28084;
  }
  
  
  
}
