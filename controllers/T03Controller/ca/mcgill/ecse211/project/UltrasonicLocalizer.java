package ca.mcgill.ecse211.project;
import static ca.mcgill.ecse211.project.Resources.*;
import static ca.mcgill.ecse211.project.Movement.*;
import java.util.Arrays;
import simlejos.ExecutionController;

/**
 * The ultrasonic localizer.
 * This classes contains the falling edge and rising edge localization routines
 */
public class UltrasonicLocalizer {
  

//  /** The previous distance recorded by ultrasonic sensor. */
//  private static int prevDistance;
//  
//  /** The latest distance recorded by ultrasonic sensor. */
//  private static int distance;
//  
//  /** The number of invalid samples seen by filter() so far. */
//  private static int invalidSampleCount;
//  
//  /** Buffer (array) to store US samples. */
//  private static float[] usData = new float[usSensor.sampleSize()];
  
  /** Angle at which back wall is detected. */
  public static double alpha;
  
  /** Angle at which left wall is detected. */
  public static double beta;
  
  /**
   * Localizes the robot to theta = 0.
   */
  public static void localize() {
    //Facing the robot toward the walls
    if (readUsDistance() < (COMMON_D - FALLINGEDGE_K)) {
      risingEdge();
    //Facing the robot against the walls  
    } else {
      fallingEdge();
    }
  }
  
  /**
  * Method performs the falling edge localization.
  * Robot always completes an clockwise rotation around 
  * its center of rotation to record the value for alpha.
  * Then it rotates in the anti-clockwise direction to record value for beta.
  * Using the values recorded,
  * the robot will then appropriately orient itself accordingly along the 0 degree y-axis.
  */
  public static void fallingEdge() {
    
    // Clockwise rotation to record value for alpha
    leftMotor.setSpeed(ROTATE_SPEED);
    rightMotor.setSpeed(-1 * ROTATE_SPEED);
    leftMotor.forward();
    rightMotor.backward();
    
    while (true) {
      if (readUsDistance() < COMMON_D - FALLINGEDGE_K) {
        alpha = odometer.getXyt()[2];
        leftMotor.setSpeed(0);
        rightMotor.setSpeed(0);
        break;
      }
    }
    // Anti-clockwise rotation to record beta value
    turnBy(-alpha);
    leftMotor.backward();
    rightMotor.forward();
    
    while (true) {
      if (readUsDistance() < COMMON_D - FALLINGEDGE_K) {
        beta = odometer.getXyt()[2];
        leftMotor.setSpeed(0);
        rightMotor.setSpeed(0);
        break;
      }
    }
    
    if (alpha < beta) { // back wall is closer
      odometer.setTheta((229.8 - (alpha + beta) / 2) + odometer.getXyt()[2]);
    } else { // left wall is closer
      odometer.setTheta((45 - (alpha + beta) / 2) + odometer.getXyt()[2]);
    }
    
    // Approximately orienting against the 0 degree y-axis.
    leftMotor.setSpeed(ROTATE_SPEED);
    rightMotor.setSpeed(-1 * ROTATE_SPEED);
    turnBy(360 - odometer.getXyt()[2]);
    
  }
  
  /**
   * Method performs the rising edge localization.
   * Robot always completes an clockwise rotation around its center of
   * rotation to record the value for alpha.
   * Then it rotates in the anti-clockwise direction to record value for beta.
   * Using the values recorded,
   * the robot will then appropriately orient itself accordingly along the 0 degree y-axis.
   */
  public static void risingEdge() {
    
    // clockwise rotation to record alpha value
    leftMotor.setSpeed(ROTATE_SPEED);
    rightMotor.setSpeed(-1 * ROTATE_SPEED);
    leftMotor.forward();
    rightMotor.backward();
    
    while (true) {
      if (readUsDistance() > COMMON_D - FALLINGEDGE_K) {
        alpha = odometer.getXyt()[2];
        leftMotor.setSpeed(0);
        rightMotor.setSpeed(0);
        break;
      }
    }
    
    //anti-clockwise rotation to record beta value
    turnBy(-alpha);
    leftMotor.backward();
    rightMotor.forward();
    
    while (true) {
      if (readUsDistance() > COMMON_D - FALLINGEDGE_K) {
        beta = odometer.getXyt()[2];
        leftMotor.setSpeed(0);
        rightMotor.setSpeed(0);
        break;
      }
    }
    
    if (alpha > beta) { //left wall is closer
      odometer.setTheta((229.8 - (alpha + beta) / 2) + odometer.getXyt()[2]);
    } else { //back wall is closer
      odometer.setTheta((47 - (alpha + beta) / 2) + odometer.getXyt()[2]);
    }
    
    // Approximately orienting against the 0 degree y-axis.
    leftMotor.setSpeed(ROTATE_SPEED);
    rightMotor.setSpeed(-1 * ROTATE_SPEED);
    turnBy(360 - odometer.getXyt()[2]);
  }
  

  
  /**
   * Converts input distance to the total rotation of each wheel needed to cover that distance.
   *
   * @param distance the input distance in meters
   * @return the wheel rotations necessary to cover the distance in degrees
   */
  public static int convertDistance(double distance) {
    // Using arc length formula to calculate the distance + scaling
    return (int) ((180 * distance) / (Math.PI * WHEEL_RAD) * 100) / 100;
  }

  /**
   * Converts input angle to the total rotation of each wheel needed to rotate the robot by that
   * angle.
   *
   * @param angle the input angle in degrees
   * @return the wheel rotations necessary to rotate the robot by the angle in degrees
   */
  public static int convertAngle(double angle) {
    // Using convertDistance method to calculate constant rotation of the robot + scaling
    return convertDistance((Math.PI * BASE_WIDTH * angle / 360.0) * 100) / 100;
  }
  
  /** Returns the filtered distance between the US sensor and an obstacle in cm. */
  public static int readUsDistance() {
    int[] filterArr = new int[21]; 
    
    for (int i = 0; i < filterArr.length; i++) {
      usSensor.fetchSample(usData, 0);
      filterArr[i] = (int) usData[0] * 100; 
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