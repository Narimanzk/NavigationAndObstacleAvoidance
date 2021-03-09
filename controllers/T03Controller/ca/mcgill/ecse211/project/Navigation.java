package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.*;

import ca.mcgill.ecse211.playingfield.Point;

public class Navigation {
  static int a = 0;
  
  private Navigation() {}
  
  /**
   * Turns the robot toward the destination by minimal angle. 
   * Travels to a specific point taking the obstacle into account.
   * Takes the green zone and the red zone into account when it wants to travel
   * @param destination the destination point
   */
  public static void travelTo(Point destination) {
    Point startPoint = getCurrentPoint_feet();
    double travelDist = toMeters(distanceBetween(startPoint, destination));
    double destTheta = getDestinationAngle(startPoint, destination);
    double[] xyt = odometer.getXyt();
    double angleDiff = Math.abs(destTheta - xyt[2]);
    
    // case 1: we're already at the destination
    if (travelDist < 0.2) {
      System.out.println("Already at destination.");
      return;
    }
    
    // case 2 : we're facing the right way and we know there won't be obstacles
    if ((angleDiff < 5.0 || angleDiff > 355.0)
        && (pathInGreenZone(startPoint, destination))) {
      System.out.println("Already Pointing in the right direction, no obstacles ahead.");
      Movement.moveStraightFor(travelDist);
      
      // case 3 : we're facing the right way and there might be obstacles
    } else if ((angleDiff < 5.0 || angleDiff > 355.0)
        && (!pathInGreenZone(startPoint, destination))) {
      System.out.println("Already Pointing in the right direction, might have obstacles ahead.");
      travelToObstacle(destination);
      
      // case 4 : we have to turn and we know there won't be obstacles
    } else if ((angleDiff >= 5.0 || angleDiff <= 355.0)
        && (pathInGreenZone(startPoint, destination))) {
      System.out.println("Destination has no obstacles ahead.");
      turnTo(destTheta);
      Movement.moveStraightFor(travelDist);
      
      // case 5 : we have to turn and there might be obstacles.
    } else {
      System.out.println("Destination might have obstacles ahead.");
      turnTo(destTheta);
      travelToObstacle(destination);
    }
    
    double tolerance = 0.4;
    if ((roughlySame(startPoint.x, destination.x, tolerance)
        || roughlySame(startPoint.y, destination.y, tolerance))
        && a < 2
        ) {
      a++;
      LightLocalizer.localize_2();
    }
  }
  
  
  
  
  /**
   * Takes a point and moves directly toward it.
   * @param destination
   */
  public static void directTravelTo(Point destination) {
    Point curPoint = getCurrentPoint_feet();
    double travelDist = distanceBetween(curPoint, destination);
    Movement.moveStraightFor(toMeters(travelDist));
  }

  
  /**
   * Moves forward and when it detects an obstacle calls avoid method.
   * When it passes the obstacle or it is close to destination calls directTravelTo.
   * @param destination
   */
  public static void travelToObstacle(Point destination) {
    int noiseTolerance = 2;
    Movement.setMotorSpeeds(200);
    while (true) {
      Movement.drive();
      Point cur = getCurrentPoint_feet();
      if (comparePoints(cur, destination, 0.2)) {
        System.out.println("Near destination, stop detecting obstacles.");
        break;
      }
      if (AvoidObstacle.readUsDistance() < 12) {
        noiseTolerance--;
      }
      if (noiseTolerance == 0) {
        Point startPoint = getCurrentPoint_feet();
        AvoidObstacle.avoid(startPoint, destination);
        break; 
      }
    }
    Point c = getCurrentPoint_feet();
    System.out.println("Currently at ("+c.x+","+c.y+")\tTravelling to ("+destination.x+","+destination.y+")\t Distance = "+distanceBetween(c,destination));
    
    if(distanceBetween(c,destination) < 0.5) {
      directTravelTo(destination);
    }else {
      travelTo(destination);
    }
    
  }
  
 
  /**
   * Checks if the path between two points is in the green zone(no obstacles).
   * @param start Starting point
   * @param end Ending point
   * @return boolean
   */
  public static boolean pathInGreenZone(Point start, Point end) {
    if (start.x <= 4 && end.x <= 4) {
      return true;
    }
    return false;
  }
  
  /**
   * Turns toward the given angle.
   * @param angle turning angle
   */
  public static void turnTo(double angle) {
    Movement.turnBy(minimalAngle(Odometer.getOdometer().getXyt()[2], angle));
  }
  
  /**
   * Gets the angle to the destination point.
   * @param current Current position of the robot
   * @param destination Destination point
   * @return double
   */
  public static double getDestinationAngle(Point current, Point destination) {
    return (Math.toDegrees(
        Math.atan2(destination.x - current.x, destination.y - current.y)) + 360) % 360;
  }
  
  /**
   * Calculates the minimal angle to turn.
   * @param initialAngle initial angle of the robot
   * @param destAngle destination angle
   * @return double
   */
  public static double minimalAngle(double initialAngle, double destAngle) {
    initialAngle %= 360;
    destAngle %= 360;
    double toTurn = (destAngle - initialAngle + 540) % 360 - 180;
    return toTurn;
  }

  /**
   * Calculates the distance between two points.
   * @param p1  First point
   * @param p2  Second point
   * @return double
   */
  public static double distanceBetween(Point p1, Point p2) {
    double dxSqr = Math.pow((p2.x - p1.x), 2);
    double dySqr = Math.pow((p2.y - p1.y), 2);
    double dist = Math.sqrt(dxSqr + dySqr);
    return dist;
  }
  
  /**
   * Takes current point and destination points and compare them.
   * If the difference is less than the tolerance
   * returns true
   * @param cur Current point
   * @param destination Destination point
   * @param tolerance Tolerated difference
   * @return boolean
   */
  public static boolean comparePoints(Point cur, Point destination, double tolerance) {
    double distCurDest = distanceBetween(cur, destination);
    return (distCurDest < tolerance);
  }
  
  /**
   * Converts meters to feet.
   * @param meters distance in meter
   * @return double
   */
  public static double toFeet(double meters) {
    return 3.28084 * meters;
  }
  
  
  /**
   * Converts feet to meters.
   * @param feet distance in feet
   * @return double
   */
  public static double toMeters(double feet) {
    return feet / 3.28084;
  }
  
  /**
   * Takes two numbers and compare them.
   * if the difference is less than the tolerance
   * return true
   * @param a first number
   * @param b second number
   * @param tolerance tolerated difference
   * @return boolean
   */
  public static boolean roughlySame(double a, double b, double tolerance) {
    double diff = Math.abs(a - b);
    return (diff < tolerance);
  }
  
  /**
   * gets the current point from odometer in feet.
   * @return Point
   */
  public static Point getCurrentPoint_feet() {
    double[] xyt = odometer.getXyt();
    Point curPoint = new Point(toFeet(xyt[0]), toFeet(xyt[1]));
    return curPoint;
  }
  
  /**
   * gets the current point from odometer in meters.
   * @return Point
   */
  public static Point getCurrentPoint_meters() {
    double[] xyt = odometer.getXyt();
    Point curPoint = new Point(xyt[0], xyt[1]);
    return curPoint;
  }
  
  
}
