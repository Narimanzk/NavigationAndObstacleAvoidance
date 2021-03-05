package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.*;
import ca.mcgill.ecse211.playingfield.Point;

public class Navigation {
  
  private Navigation() {}
  
  public static void travelTo(Point destination) {
    leftMotor.setSpeed(100);
    rightMotor.setSpeed(100);
    while(true) {
        leftMotor.forward();
        rightMotor.forward();
        System.out.println(Movement.readUsDistance());
        if (Movement.readUsDistance() < 20) {
          Point p1 = new Point(odometer.getXyt()[0]/0.3048,odometer.getXyt()[1]/0.3048);
          double p2x = Math.floor((odometer.getXyt()[0]/0.3048) * 10)/10;
          double p2y = Math.floor((odometer.getXyt()[1]/0.3048) * 10)/10;
          Point p2 = new Point(p2x,p2y + 2.0);
          System.out.println("initial x: " + (odometer.getXyt()[0]/0.3048) + " initial y: " + ((odometer.getXyt()[1]/0.3048) + 2.0));
          calculateLinearSlope(p1, p2);
          break; 
        }
    }
    leftMotor.stop();
    rightMotor.stop();
    while (true) {
      Movement.controller(Movement.readUsDistance(), motorSpeeds);
      leftMotor.setSpeed(100);
      rightMotor.setSpeed(100);
      leftMotor.forward();
      rightMotor.forward();
      double[] curXyt = odometer.getXyt();
      double x = curXyt[0];
      double y = curXyt[1];
      Point current = new Point(x, y);
      boolean stopCond = checkIfPointOnSlope(x,y,params[0],params[1]);
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
//    Double curY = m * x + b;
//    return (curY.compareTo(y)==0) 
    
    Double curY = m * x + b;
    double difference = Math.abs(curY - y);
    System.out.println("difference: " + difference);
    System.out.println("x: " + x + "y: " + y);
    System.out.println("m: " + m + "b: " + b);
//    if(difference < 4.91) {
//      counter++;
//    }
//    System.out.println("counter: " + counter);
//    if( counter > 50 && difference < 4.91) {
//      return true;
//    }
//    else return false;
    if (difference > 5.0) diffFlag = true;
    if (difference < 4.902 && diffFlag) return true;
    return false;
    

  }
  
  public static double toFeet(double meters) {
    return 3.28084 * meters;
  }
  public static double toMeters(double feet) {
    return feet / 3.28084;
  }
  public static Point getCurrentPoint() {
    double[] xyt = odometer.getXyt();
    Point curPoint = new Point(toFeet(xyt[0]), toFeet(xyt[1]));
    return curPoint;
  }
  
  
}
