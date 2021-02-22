package ca.mcgill.ecse211.playingfield;

import java.text.DecimalFormat;
import java.util.Objects;

/**
 * Represents a coordinate point on the playing field grid.
 * 
 * @author Younes Boubekeur
 */
public class Point {
  /** The x coordinate in tile lengths. */
  public double x;

  /** The y coordinate in tile lengths. */
  public double y;
  
  /** The threshold for coordinates to be considered equal. This value is around 1mm. */
  private static final double EPSILON = 0.003; // ft

  /** Constructs a Point. The arguments are in tile lengths. */
  public Point(double x, double y) {
    this.x = x;
    this.y = y;
  }
  
  @Override public boolean equals(Object o) {
    if (!(o instanceof Point)) {
      return false;
    }
    Point other = (Point) o;
    return Math.abs(x - other.x) < EPSILON && Math.abs(y - other.y) < EPSILON;
  }
  
  @Override public final int hashCode() {
    return Objects.hash(x, y);
  }

  @Override public String toString() {
    var fmt = new DecimalFormat("#.##");
    return "(" + fmt.format(x) + ", " + fmt.format(y) + ")";
  }

}
