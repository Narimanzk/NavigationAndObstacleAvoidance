package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.*;
import ca.mcgill.ecse211.playingfield.Point;

public class Navigation {
  
  private Navigation() {}
  
  public static void travelTo(Point destination) {
    int noiseTolerance = 2;
    Movement.setMotorSpeeds(200);
    while(true) {
      Movement.drive();
      System.out.println(AvoidObstacle.readUsDistance());
      if (AvoidObstacle.readUsDistance() < 9) {  // and make sure we're not at destination.
        noiseTolerance--;
      }
      if(noiseTolerance==0) {
        Point startPoint = new Point(odometer.getXyt()[0]/0.3048,odometer.getXyt()[1]/0.3048); 
        AvoidObstacle.avoid(startPoint, destination);
        break; 
      }
    }
    noiseTolerance = 5;
    Movement.stopMotors();
    
  }

  

  public static void directTravelTo(Point destination) {
    
//    double[] xyt = odometer.getXyt();
//    Point curPoint = new Point(toFeet(xyt[0]), toFeet(xyt[1]));
//    double travelDist = toMeters(distanceBetween(curPoint, destination));
//    
//    if(travelDist>0.3) { // TODO: decide this 0.3 value
//      double destTheta = getDestinationAngle(curPoint, destination);
//      turnTo(destTheta);
//      Movement.moveStraightFor(travelDist);
//      // TODO: localize and correct odometer here
//    }else{
//      System.out.println("Already at destination.");
//      // TODO: Localize and correct odometer here
//    }
    
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
