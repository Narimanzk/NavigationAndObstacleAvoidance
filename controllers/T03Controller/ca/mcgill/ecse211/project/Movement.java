package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.BASE_WIDTH;
import static ca.mcgill.ecse211.project.Resources.FORWARD_SPEED;
import static ca.mcgill.ecse211.project.Resources.INVALID_SAMPLE_LIMIT;
import static ca.mcgill.ecse211.project.Resources.LEFT;
import static ca.mcgill.ecse211.project.Resources.MAX_SENSOR_DIST;
import static ca.mcgill.ecse211.project.Resources.MOTOR_HIGH;
import static ca.mcgill.ecse211.project.Resources.MOTOR_LOW;
import static ca.mcgill.ecse211.project.Resources.RIGHT;
import static ca.mcgill.ecse211.project.Resources.ROTATE_SPEED;
import static ca.mcgill.ecse211.project.Resources.WALL_DIST;
import static ca.mcgill.ecse211.project.Resources.WALL_DIST_ERR_THRESH;
import static ca.mcgill.ecse211.project.Resources.WHEEL_RAD;
import static ca.mcgill.ecse211.project.Resources.distance;
import static ca.mcgill.ecse211.project.Resources.distance_error;
import static ca.mcgill.ecse211.project.Resources.invalidSampleCount;
import static ca.mcgill.ecse211.project.Resources.leftMotor;
import static ca.mcgill.ecse211.project.Resources.prevDistance;
import static ca.mcgill.ecse211.project.Resources.rightMotor;
import static ca.mcgill.ecse211.project.Resources.usData;
import static ca.mcgill.ecse211.project.Resources.usMotor;
import static ca.mcgill.ecse211.project.Resources.usRotate;
import static ca.mcgill.ecse211.project.Resources.usSensor;
import java.util.Arrays;
import simlejos.ExecutionController;

public class Movement {
//  private static float[] usData = new float[usSensor.sampleSize()];

  
  
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
   * Moves the robot straight for the given distance.
   *
   * @param distance in feet (tile sizes), may be negative
   */
  public static void moveStraightFor(double distance) {
    //Set motor speeds and rotate them by the given distance.
    // This method will not return until the robot has finished moving.
    leftMotor.setSpeed(FORWARD_SPEED);
    rightMotor.setSpeed(FORWARD_SPEED);
    leftMotor.rotate(convertDistance(distance), true);
    rightMotor.rotate(convertDistance(distance), false);
  }
  
  /**
   * Turns the robot by a specified angle. Note that this method is different from
   * {@code Navigation.turnTo()}. For example, if the robot is facing 90 degrees, calling
   * {@code turnBy(90)} will make the robot turn to 180 degrees, but calling
   * {@code Navigation.turnTo(90)} should do nothing (since the robot is already at 90 degrees).
   *
   * @param angle the angle by which to turn, in degrees
   */
  public static void turnBy(double angle) {
    //Similar to moveStraightFor(), but with a minus sign
    leftMotor.setSpeed(ROTATE_SPEED);
    rightMotor.setSpeed(ROTATE_SPEED);
    leftMotor.rotate(convertAngle(angle), true);
    rightMotor.rotate(-(convertAngle(angle)), false);
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
  
  
  // ====================================================================
  

  public static int readUsDistance() {
    int[] filterArr = new int[21]; 
    
    for (int i = 0; i < filterArr.length; i++) {
      usSensor.fetchSample(usData, 0);
      filterArr[i] = (int) (usData[0] * 100); 
      ExecutionController.sleepFor(60);
    }
    return filter(filterArr);
  }
  
  static int filter(int[] arr) {
    Arrays.sort(arr);
    distance = arr[10];
    if (distance >= MAX_SENSOR_DIST && invalidSampleCount < INVALID_SAMPLE_LIMIT) {
      invalidSampleCount++;
      return prevDistance;
    } else {
      if (distance < MAX_SENSOR_DIST) {
        invalidSampleCount = 0;
      }
      prevDistance = distance;
      return distance;
    }
  }
  
  public static void controller(int distance, int[] motorSpeeds) {
    int leftSpeed = MOTOR_HIGH;
    int rightSpeed = MOTOR_HIGH;
    //System.out.println("US value: " + distance);
    if (distance > 40 && usRotate) {
      leftMotor.stop();
      rightMotor.stop();
      usMotor.setSpeed(100);
      usMotor.rotate(45, false);
      turnBy(-70);
      moveStraightFor(0.1295);
      usMotor.rotate(45, false);
      usRotate = false;
    }
    
    distance_error = WALL_DIST - distance;

    //When the sensor detects no obstacles the robot goes straight
    if (Math.abs(distance_error) <= WALL_DIST_ERR_THRESH) {
      leftSpeed = MOTOR_HIGH;
      rightSpeed = MOTOR_HIGH;
    //When the sensor detects the robot is getting too close to the wall it goes to the right
    } else if (distance_error > 0) {
      rightSpeed = MOTOR_HIGH + MOTOR_LOW;
      leftSpeed = MOTOR_LOW;
    //When the sensor detects the robot is getting too far from the wall it goes to the left
    } else if (distance_error < 0) {
      rightSpeed = MOTOR_LOW;
      leftSpeed = MOTOR_HIGH + MOTOR_LOW;
    }
    
    //Sets the speed of left and right motors
    motorSpeeds[LEFT] = leftSpeed;
    motorSpeeds[RIGHT] = rightSpeed;
  }
  
  
  
  
  
  
  
  
  
  
}
