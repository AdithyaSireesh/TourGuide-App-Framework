/**
 * 
 */
package tourguide;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author pbj
 */
public class ControllerImp implements Controller {
    private static Logger logger = Logger.getLogger("tourguide");
    private static final String LS = System.lineSeparator();

    private String startBanner(String messageName) {
        return  LS 
                + "-------------------------------------------------------------" + LS
                + "MESSAGE: " + messageName + LS
                + "-------------------------------------------------------------";
    }
    //
    private double waypointRadius;
    private double waypointSeparation ;
    private Mode mode = Mode.BROWSE ;
    private Chunk.BrowseOverview overview = new Chunk.BrowseOverview();
    private ArrayList<Tour> tours = new ArrayList<Tour>();
    private Displacement waypointLoc;
    private List<Chunk> output = new ArrayList<Chunk>();
    private Stage stage = new Stage();
    private Tour selectedTour = new Tour("", "", Annotation.getDefault()); 
    
    public ControllerImp(double waypointRadius, double waypointSeparation) {
    
    	//
    	this.waypointRadius = waypointRadius ;
    	this.waypointSeparation = waypointSeparation ;
    	this.output.add(this.overview);
    }
    
 

    //--------------------------
    // Create tour mode
    //--------------------------

    // Some examples are shown below of use of logger calls.  The rest of the methods below that correspond 
    // to input messages could do with similar calls.
    
    @Override
    public Status startNewTour(String id, String title, Annotation annotation) {
        logger.fine(startBanner("startNewTour"));
        //
        if (mode == Mode.BROWSE) {
        	mode = Mode.CREATE;
        	Tour tour = new Tour(id, title, annotation) ;
        	for (Tour t : tours) {
        		if (t.getId().equals(id)) return new Status.Error("Tour with the same id already exists");
        	}
        	Chunk header = new Chunk.CreateHeader(title, 0, 0);
            tours.add(tour); 
            output.clear();
            output.add(header);
            overview.addIdAndTitle(id, title);
        	return Status.OK;
        }
        return new Status.Error("Can't create a new tour - app must be in browse mode");
    }

    @Override
    public Status addWaypoint(Annotation annotation) {
    	logger.fine(startBanner("addWaypoint"));
    	//
        if (mode == Mode.CREATE) {
        	int lastIndex = tours.size()-1 ;
        	if (lastIndex == -1) return new Status.Error("No tours to add waypoints to");
        	Tour current = tours.get(lastIndex) ;
        	ArrayList<Waypoint> wps = current.getWaypoints();
        	ArrayList<Leg> lgs = current.getLegs();
            if (wps.size() == 0) {
            	if (lgs.size() == 0) {
            		current.pushLeg(Annotation.getDefault());
            	}
            	current.pushWaypoint(annotation, waypointLoc);
            	Chunk header = new Chunk.CreateHeader(current.getTitle(), current.getLegs().size(), 1);
            	output.clear();
            	output.add(header);
            	return Status.OK;
            }
        	for (Waypoint w : wps) {
        		double wDist = w.getDistance() ;
        		double currWpDist = waypointLoc.distance();
        		double wBearing = w.getBearing();
        		double currWpBearing = waypointLoc.bearing();
        		double dist = Math.sqrt(Math.pow(wDist,2) + Math.pow(currWpDist,2) - 2*wDist*currWpDist*Math.cos((wBearing - currWpBearing)*Math.PI/180));
        		if (dist < waypointSeparation) {
        			return new Status.Error("The waypoint is too close to some other waypoint") ;
        		}
        	}
        	if (wps.size() == lgs.size()) {
        		current.pushLeg(Annotation.getDefault());
        	}
        	current.pushWaypoint(annotation, waypointLoc);
        	Chunk header = new Chunk.CreateHeader(current.getTitle(), current.getLegs().size(), current.getWaypoints().size());
        	output.clear();
        	output.add(header);
        	return Status.OK;
        }
        return new Status.Error("Can't add a waypoint - app must be in create mode");
    }

    @Override
    public Status addLeg(Annotation annotation) {
        logger.fine(startBanner("addLeg"));
        //
        if (mode == Mode.CREATE) {
        	int lastIndex = tours.size()-1 ;
        	Tour current = tours.get(lastIndex) ;
        	if (current.getLegs().size() != current.getWaypoints().size()) {
        		return new Status.Error("Can't add two legs in a row");
        	}
        	current.pushLeg(annotation);
        	Chunk header = new Chunk.CreateHeader(current.getTitle(), current.getLegs().size(), current.getWaypoints().size());
        	output.clear();
        	output.add(header);
        	return Status.OK;
        }
        return new Status.Error("Can't add a leg - app must be in create mode");
    }

    @Override
    public Status endNewTour() {
        logger.fine(startBanner("endNewTour"));
        //
        if (mode == Mode.CREATE) {
        	int lastIndex = tours.size()-1 ;
        	Tour current = tours.get(lastIndex) ;
        	if (current.getLegs().size() != current.getWaypoints().size()) {
        		return new Status.Error("Can't end a tour with different number of legs and waypoints");
        	}
        	if (current.getWaypoints().size() == 0) {
        		tours.remove(lastIndex);
        		return new Status.Error("A tour should have at least one waypoint");
        	}
        	
        	mode = Mode.BROWSE ;
        	output.clear();
        	showToursOverview() ;
        	return Status.OK;
        }
        return new Status.Error("Can't end creating a tour - app must be in create mode");
    }

    //--------------------------
    // Browse tours mode
    //--------------------------

    @Override
    public Status showTourDetails(String tourID) {
        if (mode == Mode.BROWSE) {
        	int tourFound = 0;
        	Tour selectedTour = new Tour("", "", Annotation.getDefault()); 
        	for (Tour t : tours) {
        		if (t.getId().equals(tourID)) {
        			tourFound = 1;
        			selectedTour = new Tour(t);
                    break;
        		}
        	}
        	if (tourFound == 0) {
        		return new Status.Error("App doesn't contain this tour");
        	}
        	Chunk details = new Chunk.BrowseDetails(tourID, selectedTour.getTitle(), selectedTour.getAnnotation());
        	output.clear();
        	output.add(details);
        	return Status.OK;
        }
    	return new Status.Error("Can't show tour details - app must be in browse mode");
    }
  
    @Override
    public Status showToursOverview() {
        if (mode == Mode.BROWSE) {
        	if (tours.size() == 0) {
  //      		output.clear();
  //      		output.add(overview);
        		return Status.OK;
        	}
        	output.clear();
        	output.add(overview);
        	return Status.OK;
        }
    	return new Status.Error("Can't show tours overview - app must be in browse mode");
    }

    //--------------------------
    // Follow tour mode
    //--------------------------
    
    @Override
    public Status followTour(String id) {
        if (mode == Mode.BROWSE) {
        	int tourFound = 0;
        	for (Tour t : tours) {
        		if (t.getId().equals(id)) {
        			tourFound = 1;
        			selectedTour = new Tour(t);
                    break;
        		}
        	}
        	if (tourFound == 0) {
        		return new Status.Error("App doesn't contain this tour");
        	}
        	mode = Mode.FOLLOW;
        	
        	return Status.OK;
        	
        }
    	return new Status.Error("Can't follow a tour - app must be in follow mode");
    }

    @Override
    public Status endSelectedTour() {
        if (mode == Mode.FOLLOW) {
        	mode = Mode.BROWSE ;
        	output.clear();
        	showToursOverview() ;
        	return Status.OK;
        }
        return new Status.Error("Can't end the tour - app must be in follow mode");
    }

    //--------------------------
    // Multi-mode methods
    //--------------------------
    @Override
    public void setLocation(double easting, double northing) {
    
    	if (mode == Mode.CREATE) {
    		waypointLoc = new Displacement(easting,northing);
    	} else if (mode == Mode.FOLLOW) {
    		int currStageNo = stage.getStageNumber();
    		int numberWaypoints = selectedTour.getWaypoints().size();
    		int waypointIndex = -1;
    		int loopIndex = 0;
			Displacement userLoc = new Displacement(easting, northing);
			double userDist = userLoc.distance();
			double userBearing = userLoc.bearing();
			///
    		for (Waypoint w : selectedTour.getWaypoints()) {
    			double wDist = w.getDistance();
    			double wBearing = w.getBearing();
    			double dist = Math.sqrt(Math.pow(userDist,2) + Math.pow(wDist,2) - 2*userDist*wDist*Math.cos((userBearing - wBearing)*Math.PI/180));
    			if (dist <= waypointRadius) {
    				waypointIndex = loopIndex;
    				break;
    			}
    			loopIndex++;
    			
    		} 
    		// not near waypoint. display annotation of next leg
    		if (waypointIndex == -1) {
    			if (currStageNo == numberWaypoints) {
    				return;
    			}
    			double wBearing = selectedTour.getWaypoints().get(currStageNo).getBearing();
     			double wDist = selectedTour.getWaypoints().get(currStageNo).getDistance();
     			
     			double nextWpDistance = Math.sqrt(Math.pow(userDist,2) + Math.pow(wDist,2) - 2*userDist*wDist*Math.cos((userBearing - wBearing)*Math.PI/180));
                Displacement nextWp = new Displacement(selectedTour.getWaypoints().get(currStageNo).getEasting() - easting, selectedTour.getWaypoints().get(currStageNo).getNorthing()-northing);
     			double nextWpBearing = nextWp.bearing();
    			Chunk header = new Chunk.FollowHeader(selectedTour.getTitle(), currStageNo, numberWaypoints);
    			Chunk legInfo = new Chunk.FollowLeg(selectedTour.getLegs().get(currStageNo).getAnnotation());
    			Chunk dirInfo = new Chunk.FollowBearing(nextWpBearing, nextWpDistance);
    			output.clear();
    			output.add(header);
    			output.add(legInfo);
    			output.add(dirInfo);
    		}
    		else {
    		Waypoint currWp = selectedTour.getWaypoints().get(waypointIndex);
    		// if we are on the 2nd last stage
    		if (currStageNo == numberWaypoints - 1) {
    			// if we are close to last waypoint
    			if (currStageNo == waypointIndex) {
    				stage.incrementStageNumber();
    				currStageNo = stage.getStageNumber();
    				Chunk header = new Chunk.FollowHeader(selectedTour.getTitle(), currStageNo, numberWaypoints);  
    		        Chunk waypointInfo = new Chunk.FollowWaypoint(currWp.getAnnotation());
    		        output.clear();
        			output.add(header);
        			output.add(waypointInfo);
    			}
    			// if we are close to any waypoint except the last one
    			else {
    				Chunk header = new Chunk.FollowHeader(selectedTour.getTitle(), currStageNo, numberWaypoints);  
    		        Chunk waypointInfo = new Chunk.FollowWaypoint(currWp.getAnnotation());
    		        
    		        double wBearing = selectedTour.getWaypoints().get(currStageNo).getBearing();
        			double wDist = selectedTour.getWaypoints().get(currStageNo).getDistance();
        			
        			double nextWpDistance = Math.sqrt(Math.pow(userDist,2) + Math.pow(wDist,2) - 2*userDist*wDist*Math.cos((userBearing - wBearing)*Math.PI/180));
                    Displacement nextWp = new Displacement(selectedTour.getWaypoints().get(currStageNo).getEasting() - easting, selectedTour.getWaypoints().get(currStageNo).getNorthing()-northing);
        			double nextWpBearing = nextWp.bearing();
        			
        			Chunk legInfo = new Chunk.FollowLeg(selectedTour.getLegs().get(currStageNo).getAnnotation());
        			Chunk dirInfo = new Chunk.FollowBearing(nextWpBearing, nextWpDistance);
        			output.clear();
        			output.add(header);
        			output.add(waypointInfo);
        			output.add(legInfo);
        			output.add(dirInfo);
    			}
    		}
    		// on the last stage
    		else if (currStageNo == numberWaypoints) {
    				Chunk header = new Chunk.FollowHeader(selectedTour.getTitle(), currStageNo, numberWaypoints);  
    		        Chunk waypointInfo = new Chunk.FollowWaypoint(currWp.getAnnotation());
    		        output.clear();
        			output.add(header);
        			output.add(waypointInfo);
    		} 
    		// if on any other stage
    		else {
    			// if close to next waypoint
    			if (currStageNo == waypointIndex) {
    				stage.incrementStageNumber();
    				currStageNo = stage.getStageNumber();
    				Chunk header = new Chunk.FollowHeader(selectedTour.getTitle(), currStageNo, numberWaypoints);  
    		        Chunk waypointInfo = new Chunk.FollowWaypoint(currWp.getAnnotation());
    		        
    		        double wBearing = selectedTour.getWaypoints().get(currStageNo).getBearing();
        			double wDist = selectedTour.getWaypoints().get(currStageNo).getDistance();
        			
        			double nextWpDistance = Math.sqrt(Math.pow(userDist,2) + Math.pow(wDist,2) - 2*userDist*wDist*Math.cos((userBearing - wBearing)*Math.PI/180));
                    Displacement nextWp = new Displacement(selectedTour.getWaypoints().get(currStageNo).getEasting() - easting, selectedTour.getWaypoints().get(currStageNo).getNorthing()-northing);
        			double nextWpBearing = nextWp.bearing();
        			
        			Chunk legInfo = new Chunk.FollowLeg(selectedTour.getLegs().get(currStageNo).getAnnotation());
        			Chunk dirInfo = new Chunk.FollowBearing(nextWpBearing, nextWpDistance);
        			output.clear();
        			output.add(header);
        			output.add(waypointInfo);
        			output.add(legInfo);
        			output.add(dirInfo);
    			}
    			// if close to waypoint on a stage less than current stage( waypoint on or before current waypoint)
    			else if (currStageNo > waypointIndex) {
    				Chunk header = new Chunk.FollowHeader(selectedTour.getTitle(), currStageNo, numberWaypoints);  
    		        Chunk waypointInfo = new Chunk.FollowWaypoint(currWp.getAnnotation());
    		        
    		        double wBearing = selectedTour.getWaypoints().get(currStageNo).getBearing();
        			double wDist = selectedTour.getWaypoints().get(currStageNo).getDistance();
        			
        			double nextWpDistance = Math.sqrt(Math.pow(userDist,2) + Math.pow(wDist,2) - 2*userDist*wDist*Math.cos((userBearing - wBearing)*Math.PI/180));
                    Displacement nextWp = new Displacement(selectedTour.getWaypoints().get(currStageNo).getEasting() - easting, selectedTour.getWaypoints().get(currStageNo).getNorthing()-northing);
        			double nextWpBearing = nextWp.bearing();
        			
        			Chunk legInfo = new Chunk.FollowLeg(selectedTour.getLegs().get(currStageNo).getAnnotation());
        			Chunk dirInfo = new Chunk.FollowBearing(nextWpBearing, nextWpDistance);
        			output.clear();
        			output.add(header);
        			output.add(waypointInfo);
        			output.add(legInfo);
        			output.add(dirInfo);
    			}
    			// if user has jumped waypoints
    			else {
    				Chunk header = new Chunk.FollowHeader(selectedTour.getTitle(), currStageNo, numberWaypoints);
    		        
    				double wBearing = selectedTour.getWaypoints().get(currStageNo).getBearing();
        			double wDist = selectedTour.getWaypoints().get(currStageNo).getDistance();
        			
        			double nextWpDistance = Math.sqrt(Math.pow(userDist,2) + Math.pow(wDist,2) - 2*userDist*wDist*Math.cos((userBearing - wBearing)*Math.PI/180));
                    Displacement nextWp = new Displacement(selectedTour.getWaypoints().get(currStageNo).getEasting() - easting, selectedTour.getWaypoints().get(currStageNo).getNorthing()-northing);
        			double nextWpBearing = nextWp.bearing();
        			
        			Chunk legInfo = new Chunk.FollowLeg(selectedTour.getLegs().get(currStageNo).getAnnotation());
        			Chunk dirInfo = new Chunk.FollowBearing(nextWpBearing, nextWpDistance);
        			output.clear();
        			output.add(header);
        			output.add(legInfo);
        			output.add(dirInfo);
    			}
    		}
    	}	
    	}
    }

    @Override
    public List<Chunk> getOutput() {
        return output;
    }


}
