package tourguide;

import java.util.ArrayList;

public class Tour {

	private String id;
	private String title;
	private Annotation annotation;
	private ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
	private ArrayList<Leg> legs = new ArrayList<Leg>();
	
	public Tour(String id, String title, Annotation annotation) {
		this.id = id;
		this.title = title;
		this.annotation = annotation;
	}
	
	public Tour(Tour other) {
		this.id = other.getId();
		this.title = other.getTitle();
		this.annotation = other.getAnnotation();
		this.waypoints = other.getWaypoints();
		this.legs = other.getLegs();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Annotation getAnnotation() {
		return annotation;
	}

	public void setAnnotation(Annotation annotation) {
		this.annotation = annotation;
	}
	
	
	public ArrayList<Waypoint> getWaypoints() {
		return waypoints;
	}
	
	public ArrayList<Leg> getLegs() {
		return legs;
	}

	public void pushWaypoint(Annotation annotation, Displacement wLocation) {
		Waypoint waypoint = new Waypoint(annotation, wLocation);
		waypoints.add(waypoint) ;
	}
	
	public void pushLeg(Annotation annotation) {
		Leg leg = new Leg(annotation);
		legs.add(leg) ;
	}
}
