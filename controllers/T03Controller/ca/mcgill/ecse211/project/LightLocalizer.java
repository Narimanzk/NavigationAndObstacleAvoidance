package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Movement.*;
import static ca.mcgill.ecse211.project.Resources.*;

import simlejos.ExecutionController;
import simlejos.robotics.SampleProvider;


/**
 * The light localizer.
 * This class implements the light localizer routine.
 * It uses two color sensors to first detect the horizontal line.
 * Then it turns toward (1,1) point and when it reaches there
 * it will turn and stay at (1,1).
 */
public class LightLocalizer {
  
  /* Threshold value to determine if a black line is detected or not */
  private static final int THRESHOLD = 60;
 
  /** Buffer (array) to store US1 samples. */
  private static float[] sensor1_data = new float[colorSensor1.sampleSize()];
  
  /** Buffer (array) to store US2 samples. */
  private static float[] sensor2_data = new float[colorSensor2.sampleSize()];
  
  /** Values to operate color sensor. */
  private static int current_color_blue = 1000;
  private static int current_color_red = 1000;
  
  /**
   * Localizes the robot to (x, y, theta) = (1, 1, 0)
   * and sets the odometer to (1,1,0) for our world frame of reference.
   */
  public static void localize() {
    stepOne();  // Moving towards (y=1) black line.
    stepTwo();  // Moving toward (1,1)
  }
  
  /**
   * A modified version of localization using at waypoints.
   */
  public static void localize_2() {
    stepOne_2();
    stepTwo_2();
  }
  
  
  /**
   * Localizes the robot to the black line which intersects (1,1).
   * Does so by moving forwards until both sensors detect a black line.
   * When a sensor on the left or right detects a black line, the motor on the 
   * same side will stop, and allow the other motor to detect the black line.
   * The resulting position of the robot will be a robot which is perpendicular
   * to the black line which intersects (1,1).
   */
  private static void stepOne() {
    alignWithLine();
    moveStraightFor(-0.0273);
    turnBy(90.0);
  }
  
  /**
   * Localizes the robot to (1,1,0)
   * Does so in the same way as step 1,
   * however it moves straight for a greater amount of time so as
   * to place the robot at (1,1,0) with reference to the odometer.
   */
  private static void stepTwo() {
    alignWithLine();
    moveStraightFor(-0.0273 * 3.5);
    turnBy(-90.0);
  }
  
  /**
   * A modified version of stepOne using at waypoints.
   * Modified the position of the robot
   */
  private static void stepOne_2() {
    alignWithLine();
    moveStraightFor(-0.0273 * 3.5);
    turnBy(90.0);
  }
  
  /**
   * A modified version of stepTwo.
   * The robot will not turn in the modified version.
   */
  private static void stepTwo_2() {
    alignWithLine();
    moveStraightFor(-0.0273 * 3.5);
  }
  
  /**
   * align and orient the robot with a line.
   */
  public static void localizeAngle() {
    reAlign();
    reOrientate();
  }
  
  /**
   * align the robot with the black line.
   */
  private static void reAlign() {
    int first = 0;
    while (first == 0) {
      leftMotor.setSpeed(FORWARD_SPEED);
      rightMotor.setSpeed(FORWARD_SPEED);
      leftMotor.forward();
      rightMotor.forward(); 
      if (blackLineTrigger(colorSensor1, sensor1_data)) {
        System.out.println("Stopping sensor 1");
        rightMotor.stop();
        first = 1;
        break;
      }
      if (blackLineTrigger(colorSensor2, sensor2_data)) {
        System.out.println("Stopping sensor 2");
        leftMotor.stop();
        first = 2;
        break;
      }
    }
    
    int stopSecond = 0;
    if (first == 2) {
      while (stopSecond < 2) {
        if (blackLineTrigger(colorSensor2, sensor2_data)) {
          stopSecond++;
          System.out.println("stopSecond = " + stopSecond);
          pause();
        }
      }
      leftMotor.stop();
    } else {
      while (stopSecond < 2) {
        if (blackLineTrigger(colorSensor1, sensor1_data)) {
          stopSecond++;
          System.out.println("stopSecond = " + stopSecond);
          pause();
        }
      }
      rightMotor.stop();
    }
    
    moveStraightFor(-0.0273);
    turnBy(-90.0);
  }
  
  private static void reOrientate() {
    alignWithLine();
    moveStraightFor(-0.0273 * 3.5);
  }
  
  /**
   * align the robot with the line at (1,1).
   */
  private static void alignWithLine() {
    // Indicators for if the sensors detect a black line.
    boolean s1Indicator = false;
    boolean s2Indicator = false;
    
    while (s1Indicator == false || s2Indicator == false) {   
      leftMotor.setSpeed(FORWARD_SPEED);
      rightMotor.setSpeed(FORWARD_SPEED);
      leftMotor.forward();
      rightMotor.forward();      

      //When it reaches (1,1) with sensor1 first
      if (blackLineTrigger(colorSensor1, sensor1_data)) {
        rightMotor.stop();
        s1Indicator = true;
      }
      
      //When it reaches (1,1) with sensor2 first
      if (blackLineTrigger(colorSensor2, sensor2_data)) {
        leftMotor.stop();
        s2Indicator = true;
      }
    }
  }
  
  
  
  /**
   * The method fetches data recorded by the color sensors in RedMode 
   * and compares the most recent value to verify if the
   * robot has traveled over a black line.
   * Method makes use of a fixed threshold value which may not be reliable in
   * certain conditions, however it has been tested and conditioned to minimize false negatives.
   * @param colorSensor the color sensor
   * @param sensor the data recorded by the color sensor
   * @return true if black line is detected by both sensors.
   */
  public static boolean blackLineTrigger(SampleProvider colorSensor, float[] sensor) {
    colorSensor.fetchSample(sensor, 0);
    current_color_blue = (int) (sensor[2]);
    current_color_red = (int) (sensor[0]);

    if (current_color_blue < THRESHOLD && current_color_red < THRESHOLD) {
      return true;
    } else {
      return false;
    }
  }
  
  /**
   * drives until black line is detected by sensor one.
   */
  public static void continueUntilLine() {
    leftMotor.setSpeed(FORWARD_SPEED);
    rightMotor.setSpeed(FORWARD_SPEED);
    while (true) {
      leftMotor.forward();
      rightMotor.forward();
      if (blackLineTrigger(colorSensor1, sensor1_data)) {
        rightMotor.stop();
        leftMotor.stop();
        break;
      }
    }
  }
  
  /**
   * stops the robot and pauses.
   */
  private static void pause() {
    System.out.println("Pause");
    leftMotor.setSpeed(0);
    rightMotor.setSpeed(0);
    
    for (int i = 0; i < 3000; i++) {
      ExecutionController.waitUntilNextStep();
    }
    
    leftMotor.setSpeed(FORWARD_SPEED);
    rightMotor.setSpeed(FORWARD_SPEED);
  }
  

}