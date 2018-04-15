package tourguide;

public class Waypoint {

	private Annotation annotation;
	private Displacement wLocation;
	
	public Waypoint(Annotation annotation, Displacement wLocation) {
		this.annotation = annotation;
		this.wLocation = wLocation;
	}
	
	public double getEasting() {
		return wLocation.east;
	}
	
	public double getNorthing() {
		return wLocation.north;
	}
 	
	public double getDistance() {
		return wLocation.distance() ;
	}
	
	public double getBearing() {
		return wLocation.bearing() ;
	}
	
	public Annotation getAnnotation() {
		return annotation;
	}
	
}
