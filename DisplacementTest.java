/**
 * 
 */
package tourguide;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author pbj
 */
public class DisplacementTest {
    /**
     * EPS = Epsilon, the difference to allow in floating point numbers when 
     * comparing them for equality.
     */
    private static final double EPS = 0.01; 
    
    @Test
    public void testNorthBearing() {
        double bearing = new Displacement(0.0, 1.0).bearing();
        assertEquals(0.0, bearing, EPS);
    }
    
    @Test
    public void testEastBearing() {
        double bearing = new Displacement(1.0, 0.0).bearing();
        assertEquals(90.0, bearing, EPS);
    }
    
    @Test
    public void testSouthBearing() {
        double bearing = new Displacement(0.0, -1.0).bearing();
        assertEquals(180.0, bearing, EPS);
    }
    
    @Test
    public void testWestBearing() {
        double bearing = new Displacement(-1.0, 0.0).bearing();
        assertEquals(270.0, bearing, EPS);
    }
    
    @Test
    public void testNorthEastBearing() {
        double bearing = new Displacement(1.0, 1.0).bearing();
        assertEquals(45.0, bearing, EPS);
    }
    
    @Test
    public void testSouthEastBearing() {
        double bearing = new Displacement(1.0, -1.0).bearing();
        assertEquals(135.0, bearing, EPS);
    }
    
    @Test
    public void testSouthWestBearing() {
        double bearing = new Displacement(-1.0, -1.0).bearing();
        assertEquals(225.0, bearing, EPS);
    }
    
    @Test
    public void testNorthWestBearing() {
        double bearing = new Displacement(-1.0, 1.0).bearing();
        assertEquals(315.0, bearing, EPS);
    }
    
    @Test
    public void testNorthDistance() {
        double distance = new Displacement(0.0, 1.0).distance();
        assertEquals(1.0, distance, EPS);
    }
    
    @Test
    public void testEastDistance() {
        double distance = new Displacement(1.0, 0.0).distance();
        assertEquals(1.0, distance, EPS);
    }
    
    @Test
    public void testSouthDistance() {
        double distance = new Displacement(0.0, -1.0).distance();
        assertEquals(1.0, distance, EPS);
    }
    
    @Test
    public void testWestDistance() {
        double distance = new Displacement(-1.0, 0.0).distance();
        assertEquals(1.0, distance, EPS);
    }
    
    @Test
    public void testNorthEastDistance() {
        double distance = new Displacement(1.0, 1.0).distance();
        assertEquals(Math.sqrt(2), distance, EPS);
    }
    
    @Test
    public void testSouthEastDistance() {
        double distance = new Displacement(1.0, -1.0).distance();
        assertEquals(Math.sqrt(2), distance, EPS);
    }
    
    @Test
    public void testSouthWestDistance() {
        double distance = new Displacement(-1.0, -1.0).distance();
        assertEquals(Math.sqrt(2), distance, EPS);
    }
    
    @Test
    public void testNorthWestDistance() {
        double distance = new Displacement(-1.0, 1.0).distance();
        assertEquals(Math.sqrt(2), distance, EPS);
    }
 
}
