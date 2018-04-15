package tourguide;

import java.util.logging.Logger;

/** 
 * Each waypoint is linked to a Displacement object.
 * Displacement represents the position of the user/waypoint relative to a reference point,
 * hence representing its location.    
 */

public class Displacement {
    private static Logger logger = Logger.getLogger("tourguide");
       
    /**
     * @param  east   the position with respect to the reference point on the horizontal axis
     * @param  north  the position with respect to the reference point on the vertical axis    
     */
    
    public double east;
    public double north;
    
    
    /**
     * Assigns the east and north positions of the Displacement object
     * linked to the user's/waypoint's location.  
     * <p>
     * This method is a constructor, hence has no return value.
     * @param  e  the position with respect to the reference point on the horizontal axis
     * @param  n  the position with respect to the reference point on the vertical axis
     */
    public Displacement(double e, double n) {
        logger.finer("East: " + e + "  North: "  + n);
        
        east = e;
        north = n;
    }
    
    /**
     * Returns a double which represents ones (user/waypoint) distance from a reference point. 
     * The function takes no parameters. 
     * <p>
     * This method works on the relative position (east and north of the reference point)
     * of the user/waypoint. 
     * @return      the distance of the Displacement object from the reference point
     */  
    
    public double distance() {
        logger.finer("Entering");
        
        return Math.sqrt(east * east + north * north);
    }
    
    /**
     * Returns a double which represents ones (user/waypoint) bearing in degrees from a reference point.
     * Bearing means the orientation of the user/waypoint relative to the north. 
     * The function takes no parameters.
     * The bearing lies between 0 and 360 degrees. 
     * <p>
     * This method works on the relative position (east and north of the reference point)
     * of the user/waypoint. 
     * @return      the bearing of the Displacement object from the reference point
     */  
    
    // Bearings measured clockwise from north direction.
    public double bearing() {
        logger.finer("Entering");
              
        // atan2(y,x) computes angle from x-axis towards y-axis, returning a negative result
        // when y is negative.
        
        double inRadians = Math.atan2(east, north);
        
        if (inRadians < 0) {
            inRadians = inRadians + 2 * Math.PI;
        }
        
        return Math.toDegrees(inRadians);
    }
        
    
    
}
