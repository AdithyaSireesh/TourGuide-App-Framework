/**
 * 
 */
package tourguide;

import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author pbj
 *
 */
public class ControllerTest {

    private Controller controller;
    private static final double WAYPOINT_RADIUS = 10.0;
    private static final double WAYPOINT_SEPARATION = 25.0;
   
    // Utility methods to help shorten test text.
    private static Annotation ann(String s) { return new Annotation(s); }
    private static void checkStatus(Status status) { 
        Assert.assertEquals(Status.OK, status);
    }
    private static void checkStatusNotOK(Status status) { 
        Assert.assertNotEquals(Status.OK, status);
    }
    private void checkOutput(int numChunksExpected, int chunkNum, Chunk expected) {
        List<Chunk> output = controller.getOutput();
        Assert.assertEquals("Number of chunks", numChunksExpected, output.size());
        Chunk actual = output.get(chunkNum);
        Assert.assertEquals(expected, actual);  
    }
    
    
    /*
     * Logging functionality
     */
    
    // Convenience field.  Saves on getLogger() calls when logger object needed.
    private static Logger logger;
    
    // Update this field to limit logging.
    public static Level loggingLevel = Level.ALL;
    
    private static final String LS = System.lineSeparator();

    @BeforeClass
    public static void setupLogger() {
         
        logger = Logger.getLogger("tourguide"); 
        logger.setLevel(loggingLevel);
        
        // Ensure the root handler passes on all messages at loggingLevel and above (i.e. more severe)
        Logger rootLogger = Logger.getLogger("");
        Handler handler = rootLogger.getHandlers()[0];
        handler.setLevel(loggingLevel);
    }

    private String makeBanner(String testCaseName) {
        return  LS 
          + "#############################################################" + LS
          + "TESTCASE: " + testCaseName + LS
          + "#############################################################";
    }


    
    @Before
    public void setup() {
        controller = new ControllerImp(WAYPOINT_RADIUS, WAYPOINT_SEPARATION);
    }
    
    @Test
    public void noTours() {  
        logger.info(makeBanner("noTours"));
        
        checkOutput(1, 0, new Chunk.BrowseOverview() );
     }
    
    // Locations roughly based on St Giles Cathedral reference.
    
    private void addNoPointTour() {
    	checkStatus( controller.startNewTour("T0", "Forest Hill", ann("Former informatics building\n")));
    	
    	checkOutput(1, 0, new Chunk.CreateHeader("Forest Hill", 0, 0));
    	
    	checkStatusNotOK( controller.endNewTour());
    }
    
    @Test
    public void testAddNoPointTour() {
    	logger.info(makeBanner("testAddNoPointTour"));
    	
    	addNoPointTour();
    }
    
    private void addOnePointTour() {
        
        checkStatus( controller.startNewTour(
                "T1", 
                "Informatics at UoE", 
                ann("The Informatics Forum and Appleton Tower\n"))
                );
        
        checkOutput(1, 0, new Chunk.CreateHeader("Informatics at UoE", 0,  0));
      
        controller.setLocation(300, -500);
  
        checkStatus( controller.addLeg(ann("Start at NE corner of George Square\n")) );
       
        checkOutput(1, 0, new Chunk.CreateHeader("Informatics at UoE", 1,  0));
        
        checkStatus( controller.addWaypoint(ann("Informatics Forum")) );     
        
        checkOutput(1, 0, new Chunk.CreateHeader("Informatics at UoE", 1,  1));
  
        checkStatus( controller.endNewTour() );
        
    }
    
    private void addDuplicateOnePointTour() {
        
        checkStatusNotOK( controller.startNewTour(
                "T1", 
                "Mathematics at UoE", 
                ann("JCMB and Swann Building\n"))
                );
        
    }
    
    @Test
    public void testAddOnePointTour() { 
        logger.info(makeBanner("testAddOnePointTour"));
        
        addOnePointTour(); 
    }
    
    @Test
    public void testAddDuplicateTours() {
    	logger.info(makeBanner("testAddDuplicateTours"));
    	
    	addOnePointTour();
    	addDuplicateOnePointTour();
    }
    
    private void waypointsTooClose() {
    	checkStatus( controller.startNewTour(
                   "T5", 
                   "Leith", 
                   ann("A tour of Leith\n"))
                   );
    	checkOutput(1, 0, new Chunk.CreateHeader("Leith", 0,  0));
    	
    	checkStatus(controller.addLeg(ann("London Road\n")));
    	
    	checkOutput(1, 0, new Chunk.CreateHeader("Leith", 1,  0));
    	
    	controller.setLocation(100, 100);
    	
    	checkStatus(controller.addWaypoint(ann("Iceland Supermarket")));
    	
    	checkOutput(1,0, new Chunk.CreateHeader("Leith", 1, 1));
    	
    	controller.setLocation(100, 105);
    	
    	checkStatusNotOK(controller.addWaypoint(ann("Adi's flat")));
    	
    }
    
    @Test
    public void testWaypointsTooClose() {
    	logger.info(makeBanner("testWaypointsTooClose"));
    	
    	waypointsTooClose();
    }

    private void addTwoPointTour() {
         checkStatus(
                controller.startNewTour("T2", "Old Town", ann("From Edinburgh Castle to Holyrood\n"))
                );
        
        checkOutput(1, 0, new Chunk.CreateHeader("Old Town", 0,  0));
      
        controller.setLocation(-500, 0);
        
        // Leg before this waypoint with default annotation added at same time
        checkStatus( controller.addWaypoint(ann("Edinburgh Castle\n")) );     
        
        checkOutput(1, 0, new Chunk.CreateHeader("Old Town", 1,  1));
  
        checkStatus( controller.addLeg(ann("Royal Mile\n")) );
  
        checkOutput(1, 0, new Chunk.CreateHeader("Old Town", 2,  1) );
      
        checkStatusNotOK( 
                controller.endNewTour()
                );
  
        controller.setLocation(1000, 300);
               
        checkStatus( controller.addWaypoint(ann("Holyrood Palace\n")) );

        checkOutput(1, 0, new Chunk.CreateHeader("Old Town", 2,  2) );
  
        checkStatus( controller.endNewTour() );
        
    }
    
    @Test
    public void testAddTwoPointTour() { 
        logger.info(makeBanner("testAddTwoPointTour"));
       
        addTwoPointTour(); 
    }
    
    @Test
    public void testAddOfTwoTours() {
        logger.info(makeBanner("testAddOfTwoTour"));
        
        addOnePointTour();
        addTwoPointTour();
    }
    
    
    @Test
    public void browsingTwoTours() {
        logger.info(makeBanner("browsingTwoTours"));
        
        addOnePointTour();
        addTwoPointTour();
 
        Chunk.BrowseOverview overview = new Chunk.BrowseOverview(); 
        overview.addIdAndTitle("T1", "Informatics at UoE");
        overview.addIdAndTitle("T2", "Old Town");
        checkOutput(1, 0, overview);
        
        checkStatusNotOK( controller.showTourDetails("T3") );
        checkStatus( controller.showTourDetails("T1") );
            
        checkOutput(1, 0, new Chunk.BrowseDetails(
                "T1", 
                "Informatics at UoE", 
                ann("The Informatics Forum and Appleton Tower\n")
                ));
    }
    
    @Test 
    public void followOldTownTour() {
        logger.info(makeBanner("followOldTownTour"));
       
        addOnePointTour();
        addTwoPointTour();

        checkStatus( controller.followTour("T2") );
        
        controller.setLocation(0.0, 0.0);
  
        checkOutput(3,0, new Chunk.FollowHeader("Old Town", 0, 2) );      
        checkOutput(3,1, new Chunk.FollowLeg(Annotation.DEFAULT));
        checkOutput(3,2, new Chunk.FollowBearing(270.0, 500.0));
         
        controller.setLocation(-490.0, 0.0);
      
        checkOutput(4,0, new Chunk.FollowHeader("Old Town", 1, 2) );  
        checkOutput(4,1, new Chunk.FollowWaypoint(ann("Edinburgh Castle\n")));
        checkOutput(4,2, new Chunk.FollowLeg(ann("Royal Mile\n")));
        checkOutput(4,3, new Chunk.FollowBearing(79.0, 1520.0));
 
        controller.setLocation(900.0, 300.0);
        
        checkOutput(3,0, new Chunk.FollowHeader("Old Town", 1, 2) );  
        checkOutput(3,1, new Chunk.FollowLeg(ann("Royal Mile\n")));
        checkOutput(3,2, new Chunk.FollowBearing(90.0, 100.0));
        
        controller.setLocation(1000.0, 300.0);
  
        checkOutput(2,0, new Chunk.FollowHeader("Old Town", 2, 2) );  
        checkOutput(2,1, new Chunk.FollowWaypoint(ann("Holyrood Palace\n")));
                      
        controller.endSelectedTour();
        
        Chunk.BrowseOverview overview = new Chunk.BrowseOverview(); 
        overview.addIdAndTitle("T1", "Informatics at UoE");
        overview.addIdAndTitle("T2", "Old Town");
        checkOutput(1, 0, overview);
    
    }
    
    private void addThreePointTour() {
    	checkStatus(
                controller.startNewTour("T3", "Christmas Market", ann("Princes Street\n"))
                );
        
        checkOutput(1, 0, new Chunk.CreateHeader("Christmas Market", 0,  0));
      
        controller.setLocation(-500, 0);
        
        // Leg before this waypoint with default annotation added at same time
        checkStatus( controller.addWaypoint(ann("Waverley\n")) );     
        
        checkOutput(1, 0, new Chunk.CreateHeader("Christmas Market", 1,  1));
  
        checkStatus( controller.addLeg(ann("Princes Street Part 1\n")) );
  
        checkOutput(1, 0, new Chunk.CreateHeader("Christmas Market", 2,  1) );
      
        checkStatusNotOK( 
                controller.endNewTour()
                );
  
        controller.setLocation(1000, 300);
               
        checkStatus( controller.addWaypoint(ann("Amusement Park\n")) );

        checkOutput(1, 0, new Chunk.CreateHeader("Christmas Market", 2,  2) );
         
        checkStatus( controller.addLeg(ann("Princes Street Part 2\n")) );
        
        checkStatusNotOK( 
                controller.endNewTour()
                );
        checkOutput(1, 0, new Chunk.CreateHeader("Christmas Market", 3,  2) );
        
        controller.setLocation(500, 500);
        
        checkStatus( controller.addWaypoint(ann("National Gallery\n")) );
        
        checkOutput(1, 0, new Chunk.CreateHeader("Christmas Market", 3,  3) );
  
        checkStatus( controller.endNewTour() );
    }
    
    @Test
    public void testAddThreePointTour() {
    	logger.info(makeBanner("testAddThreePointTour"));
    	
    	addThreePointTour();
    }
    
    @Test
    public void browsingThreeTours() {
        logger.info(makeBanner("browsingThreeTours"));
        
        addOnePointTour();
        addTwoPointTour();
        addThreePointTour();
 
        Chunk.BrowseOverview overview = new Chunk.BrowseOverview(); 
        overview.addIdAndTitle("T1", "Informatics at UoE");
        overview.addIdAndTitle("T2", "Old Town");
        overview.addIdAndTitle("T3", "Christmas Market");
        checkOutput(1, 0, overview);
        
        checkStatus( controller.showTourDetails("T3") );
        checkStatusNotOK( controller.showTourDetails("T4") );
            
        checkOutput(1, 0, new Chunk.BrowseDetails(
                "T3", 
                "Christmas Market", 
                ann("Princes Street\n")
                ));
    }
    
    @Test 
    public void followChristmasMarketTour() {
        logger.info(makeBanner("followChristmasMarketTour"));
       
        addOnePointTour();
        addTwoPointTour();
        addThreePointTour();

        checkStatus( controller.followTour("T3") );
        
        controller.setLocation(0.0, 0.0);
  
        checkOutput(3,0, new Chunk.FollowHeader("Christmas Market", 0, 3) );      
        checkOutput(3,1, new Chunk.FollowLeg(Annotation.DEFAULT));
        checkOutput(3,2, new Chunk.FollowBearing(270.0, 500.0));
         
        controller.setLocation(-490.0, 0.0);
      
        checkOutput(4,0, new Chunk.FollowHeader("Christmas Market", 1, 3) );  
        checkOutput(4,1, new Chunk.FollowWaypoint(ann("Waverley\n")));
        checkOutput(4,2, new Chunk.FollowLeg(ann("Princes Street Part 1\n")));
        checkOutput(4,3, new Chunk.FollowBearing(79.0, 1520.0));
 
        controller.setLocation(900.0, 300.0);
        
        checkOutput(3,0, new Chunk.FollowHeader("Christmas Market", 1, 3) );  
        checkOutput(3,1, new Chunk.FollowLeg(ann("Princes Street Part 1\n")));
        checkOutput(3,2, new Chunk.FollowBearing(90.0, 100.0));
        
        controller.setLocation(1000.0, 300.0);
  
        checkOutput(4,0, new Chunk.FollowHeader("Christmas Market", 2, 3) );  
        checkOutput(4,1, new Chunk.FollowWaypoint(ann("Amusement Park\n")));
        checkOutput(4,2, new Chunk.FollowLeg(ann("Princes Street Part 2\n")));
        checkOutput(4,3, new Chunk.FollowBearing(292.0, 539.0));
        
        // User backtracks to the first waypoint
        controller.setLocation(-490.0, 0.0);
        
        checkOutput(4,0, new Chunk.FollowHeader("Christmas Market", 2, 3) );  
        checkOutput(4,1, new Chunk.FollowWaypoint(ann("Waverley\n")));
        checkOutput(4,2, new Chunk.FollowLeg(ann("Princes Street Part 2\n")));
        checkOutput(4,3, new Chunk.FollowBearing(63.0, 1109.0));
        
        controller.setLocation(495, 500);
        
        checkOutput(2,0, new Chunk.FollowHeader("Christmas Market", 3, 3));
        checkOutput(2,1, new Chunk.FollowWaypoint(ann("National Gallery\n")));
                      
        controller.endSelectedTour();
        
        Chunk.BrowseOverview overview = new Chunk.BrowseOverview(); 
        overview.addIdAndTitle("T1", "Informatics at UoE");
        overview.addIdAndTitle("T2", "Old Town");
        overview.addIdAndTitle("T3", "Christmas Market");
        checkOutput(1, 0, overview);
    
    }
    
}
