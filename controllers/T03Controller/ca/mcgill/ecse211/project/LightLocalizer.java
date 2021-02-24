package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.*;
import static ca.mcgill.ecse211.project.Movement.*;
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
  
  //Values to operate color sensor
  private static int current_color_blue = 1000;
  private static int current_color_red = 1000;
  
  /**
   * Localizes the robot to (x, y, theta) = (1, 1, 0).
   */
  public static void localize() {
    //Moving toward black line
    leftMotor.forward();
    rightMotor.forward();
    
    //First time that the robot pass the black line
    while (true) {
      if (blackLineTrigger(colorSensor1, sensor1_data) 
          &&  blackLineTrigger(colorSensor2, sensor2_data)) {
        leftMotor.stop();
        rightMotor.stop();
        moveStraightFor(-0.0273);
        turnBy(90.0);
        break;
      //When sensor 1 reaches the black line first
      } else if (blackLineTrigger(colorSensor1, sensor1_data)) {
        rightMotor.stop();
        if (blackLineTrigger(colorSensor2, sensor2_data)) {
          leftMotor.stop();
          moveStraightFor(-0.0273);
          turnBy(90.0);
          break;
        }
      //When sensor 2 reaches the black line first
      } else if (blackLineTrigger(colorSensor2, sensor2_data)) {
        leftMotor.stop();
        if (blackLineTrigger(colorSensor1, sensor1_data)) {
          rightMotor.stop();
          moveStraightFor(-0.0273);
          turnBy(90.0);
          break;
        }
      }
    }
    //Moving toward (1,1)
    while (true) {
      leftMotor.setSpeed(FORWARD_SPEED);
      rightMotor.setSpeed(FORWARD_SPEED);
      leftMotor.forward();
      rightMotor.forward();      
      //When it reaches (1,1)
      if (blackLineTrigger(colorSensor1, sensor1_data)
          && blackLineTrigger(colorSensor2, sensor2_data)) {
        leftMotor.stop();
        rightMotor.stop();
        moveStraightFor(-0.0273 * 3.5);
        turnBy(-90.0);
        break;
      } else if (blackLineTrigger(colorSensor1, sensor1_data)) {
        rightMotor.stop();
        if (blackLineTrigger(colorSensor2, sensor2_data)) {
          leftMotor.stop();
          moveStraightFor(-0.0273 * 3.5);
          turnBy(-90.0);
          break;
        }
      } else if (blackLineTrigger(colorSensor2, sensor2_data)) {
        leftMotor.stop();
        if (blackLineTrigger(colorSensor1, sensor1_data)) {
          rightMotor.stop();
          moveStraightFor(-0.0273 * 3.5);
          turnBy(-90.0);
          break;
        }
      }
    }
  }
  
  /**
   * The method fetches data recorded by the color sensors in RedMode 
   * and compares the most recent value to verify if the
   * robot has traveled over a black line.
   * Method makes use of a fixed threshold value which may not be reliable in
   * certain conditions, however it has been tested and conditioned to minimize false negatives.
   * @param the color sensor and the data recorded by the color sensor
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
  
  

}