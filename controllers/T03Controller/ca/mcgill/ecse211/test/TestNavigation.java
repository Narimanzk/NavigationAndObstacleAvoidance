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
    // Going from 45° to 135° means turning by +90°
    assertEquals(90, minimalAngle(45, 135), ERROR_MARGIN);
    
    // Going from 185° to 175° means turning by -10°
    assertEquals(-10, minimalAngle(185, 175), ERROR_MARGIN);
    
    // Going from 355° to 5° means turning by 10° (0° discontinuity)
    assertEquals(10, minimalAngle(355, 5), ERROR_MARGIN);
    
    // TODO Add more test cases here. Don't forget about other edge cases!
  }
  
  @Test void testDistanceBetween() {
    assertEquals(0, distanceBetween(new Point(0, 0), new Point(0, 0)), ERROR_MARGIN);
    assertEquals(2, distanceBetween(new Point(5.5, 3.5), new Point(3.5, 3.5)), ERROR_MARGIN);
    
    // TODO Add more test cases here
  }
  
  // TODO Think about testing your other Navigation functions here
  
  // You can add helper methods below to be used in the tests above

}
