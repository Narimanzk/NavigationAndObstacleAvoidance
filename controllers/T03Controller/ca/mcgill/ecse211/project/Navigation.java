package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.*;
import ca.mcgill.ecse211.playingfield.Point;

public class Navigation {
  
  private Navigation() {}
  
  public static void travelTo(Point destination) {
    Point curPoint = getCurrentPoint_feet();
    double travelDist = toMeters(distanceBetween(curPoint, destination));
    double destTheta = getDestinationAngle(curPoint, destination);
    double[] xyt = odometer.getXyt();

    double angleDiff = Math.abs(destTheta - xyt[2]);
    
    // case 1: we're already at the destination
    if(travelDist < 0.3) {
      System.out.println("Already at destination.");
      return;
    }
    
    // case 2 : we're facing the right way and we know there won't be obstacles
    if((angleDiff < 6.0 || angleDiff > 354.0) &&
       (pathInGreenZone(curPoint, destination))
    ){
      System.out.println("Already Pointing in the right direction, no obstacles ahead.");
      Movement.moveStraightFor(travelDist); 
    }

    // case 3 : we're facing the right way and there might be obstacles
    else if(
        (angleDiff < 6.0 || angleDiff > 354.0) &&
        (!pathInGreenZone(curPoint, destination))
    ){
      System.out.println("Already Pointing in the right direction, might have obstacles ahead.");
      travelToObstacle(destination);
    }
    
    // case 4 : we have to turn and we know there won't be obstacles
    else if(
        (angleDiff >= 6.0 || angleDiff <= 354.0) &&
        (pathInGreenZone(curPoint, destination))
    ){
      System.out.println("Destination has no obstacles ahead.");
      turnTo(destTheta);
      Movement.moveStraightFor(travelDist);
    }
    
    // case 5 : we have to turn and there might be obstacles.
    else {
      System.out.println("Destination might have obstacles ahead.");
      turnTo(destTheta);
      travelToObstacle(destination);
    }
    
    LightLocalizer.localize();
        
  }

  public static void directTravelTo(Point destination) {
    Point curPoint = getCurrentPoint_feet();
    double travelDist = distanceBetween(curPoint, destination);
    System.out.println("Distance :"+travelDist);
    Movement.moveStraightFor(toMeters(travelDist));
  }
  
  public static void travelToObstacle(Point destination) {
    int noiseTolerance = 2;
    Movement.setMotorSpeeds(200);
    while(true) {
      Movement.drive();
      Point cur = getCurrentPoint_feet();
      if(comparePoints(cur,destination)) {
        System.out.println("Near destination, stop detecting obstacles.");
        break;
      }
      if (AvoidObstacle.readUsDistance() < 10) {
        noiseTolerance--;
      }
      if(noiseTolerance==0) {
        Point startPoint = new Point(odometer.getXyt()[0]/0.3048,odometer.getXyt()[1]/0.3048); 
        AvoidObstacle.avoid(startPoint, destination);
        break; 
      }
    }
    noiseTolerance = 2;
    Movement.stopMotors();
  }
  
  
  public static boolean comparePoints(Point cur, Point destination) {
    double distCurDest = distanceBetween(cur,destination);
    return (distCurDest < 0.2);
  }

  // verify that both destination and current are in the green zone --> no obstacles
  public static boolean pathInGreenZone(Point start, Point end) {
    if(start.x <= 4 && end.x <= 4 ) {
      return true;
    } return false;
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
  public static double toFeet(double meters) {
    return 3.28084 * meters;
  }
  public static double toMeters(double feet) {
    return feet / 3.28084;
  }
  public static Point getCurrentPoint_feet() {
    double[] xyt = odometer.getXyt();
    Point curPoint = new Point(toFeet(xyt[0]), toFeet(xyt[1]));
    return curPoint;
  }
  public static Point getCurrentPoint_meters() {
    double[] xyt = odometer.getXyt();
    Point curPoint = new Point(xyt[0], xyt[1]);
    return curPoint;
  }
  
  
}
