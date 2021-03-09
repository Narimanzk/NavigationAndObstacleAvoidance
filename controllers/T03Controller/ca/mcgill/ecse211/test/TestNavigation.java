package ca.mcgill.ecse211.test;

import static ca.mcgill.ecse211.project.Navigation.*;
import static ca.mcgill.ecse211.project.Resources.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import ca.mcgill.ecse211.playingfield.Point;

/**
 * Tests the Navigation class. This test runs in Eclipse (right-click > Run as > Unit test) and
 * on the command line (`make test`), not in Webots!
 * 
 * @author Younes Boubekeur
 */
public class TestNavigation {
  
  /** Tolerate up to this amount of error due to double imprecision. */
  private static final double ERROR_MARGIN = 0.01;
  
  @Test void testGetDestinationAngle() {
    assertEquals(0, getDestinationAngle(new Point(1, 1), new Point(1, 2)), ERROR_MARGIN);
    assertEquals(45, getDestinationAngle(new Point(5, 5), new Point(6, 6)), ERROR_MARGIN);
    
    // TODO Add more test cases here
  }
  
  @Test void testMinimalAngle() {
    // Below is TA's work so I commented it out 
    // Going from 45° to 135° means turning by +90°
    //assertEquals(90, minimalAngle(45, 135), ERROR_MARGIN);
    
    // Going from 185° to 175° means turning by -10°
    //assertEquals(-10, minimalAngle(185, 175), ERROR_MARGIN);
    
    // Going from 355° to 5° means turning by 10° (0° discontinuity)
   // assertEquals(10, minimalAngle(355, 5), ERROR_MARGIN);
    
    // First to first 
    assertEquals(20, minimalAngle(40, 60), ERROR_MARGIN);
    //From first to second
    assertEquals(90, minimalAngle(40, 130), ERROR_MARGIN);
    //From first to third
    assertEquals(160, minimalAngle(50, 210), ERROR_MARGIN);
    // first to third reversed 
    assertEquals(-110, minimalAngle(10, 260), ERROR_MARGIN);
    //From first to fourth
    assertEquals(-100, minimalAngle(20, 280), ERROR_MARGIN);
   
   // Second to first 
    assertEquals(-60, minimalAngle(110, 50), ERROR_MARGIN);
    // Second to Second
    assertEquals(20, minimalAngle(100, 120), ERROR_MARGIN);
    // Second to third
    assertEquals(100, minimalAngle(150, 250), ERROR_MARGIN);
    // Second to fourth 
    assertEquals(-160, minimalAngle(100, 300), ERROR_MARGIN);
    // Second to fourth reversed 
    assertEquals(-130, minimalAngle(110, -20), ERROR_MARGIN);
    
    // third to first 
    assertEquals(170, minimalAngle(200, 10), ERROR_MARGIN);
    // third to first reversed
    assertEquals(-160, minimalAngle(200,40 ), ERROR_MARGIN);
    // third to second 
    assertEquals(-90, minimalAngle(190, 100), ERROR_MARGIN);
    // third to third 
    assertEquals(30, minimalAngle(200, 230), ERROR_MARGIN);
    // third to third reversed
    assertEquals(-70, minimalAngle(270, 200), ERROR_MARGIN);
    // third to forth 
    assertEquals(20, minimalAngle(260, 280), ERROR_MARGIN);
    
   // forth to first 
    assertEquals(40, minimalAngle(340, 20), ERROR_MARGIN);
    // forth to second
    assertEquals(140, minimalAngle(320, 100), ERROR_MARGIN);
    // forth to second reverse 
    assertEquals(-150, minimalAngle(300, 150), ERROR_MARGIN);
    // forth to third
    assertEquals(-90, minimalAngle(280, 190), ERROR_MARGIN);
    // forth to forth 
    assertEquals(60, minimalAngle(290, 350), ERROR_MARGIN);
    // forth to forth reversed 
    assertEquals(-10, minimalAngle(340, 330), ERROR_MARGIN);
    
    // Edge cases
    assertEquals(-180, minimalAngle(0,180), ERROR_MARGIN);
    assertEquals(0, minimalAngle(270, 270), ERROR_MARGIN);
    assertEquals(90, minimalAngle(90, 180), ERROR_MARGIN);
    assertEquals(0, minimalAngle(230, 230), ERROR_MARGIN);
    assertEquals(99.222, minimalAngle(36.321, 135.543), ERROR_MARGIN);
    
 
    
    // TODO Add more test cases here. Don't forget about other edge cases!
  }
  
  @Test void testDistanceBetween() {
    assertEquals(0, distanceBetween(new Point(0, 0), new Point(0, 0)), ERROR_MARGIN);
    assertEquals(2, distanceBetween(new Point(5.5, 3.5), new Point(3.5, 3.5)), ERROR_MARGIN);
    assertEquals(1.414214, distanceBetween(new Point(1,1), new Point(2,2)), ERROR_MARGIN);
    assertEquals(1.414214, distanceBetween(new Point(1, 5), new Point(2, 4)), ERROR_MARGIN);
    assertEquals(3.162278, distanceBetween(new Point(-1, 5), new Point(2, 4)), ERROR_MARGIN);
    assertEquals(17.464249, distanceBetween(new Point(-10, -21), new Point(-3,-5)), ERROR_MARGIN);
    assertEquals(45.021106, distanceBetween(new Point(-0,45), new Point(3.3,0.1)), ERROR_MARGIN);
    assertEquals(1.4142, distanceBetween(new Point(1, 1), new Point(2, 2)), ERROR_MARGIN);
  
  }
  
  // TODO Think about testing your other Navigation functions here
  // You can add helper methods below to be used in the tests above
  
 @Test void testToFeet(){
    assertEquals(3.28084, toFeet(1), ERROR_MARGIN);
    assertEquals(4.9206, toFeet(1.5), ERROR_MARGIN);
    assertEquals(32.8084, toFeet(10), ERROR_MARGIN);
    assertEquals(14.0419952, toFeet(4.28), ERROR_MARGIN);
    assertEquals(20.46916, toFeet(6.239), ERROR_MARGIN);
  }
  
 @Test void testToMeters() {
   assertEquals(0.30479,toMeters(1), ERROR_MARGIN);
   assertEquals(1.38989,toMeters(4.56), ERROR_MARGIN);
   assertEquals(1.6154,toMeters(5.3), ERROR_MARGIN);
   
 }
 
  
}
