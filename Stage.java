package tourguide;

public class Stage {

	private int stageNumber;

	public Stage() {
		stageNumber = 0;
	}
	
	public int getStageNumber() {
		return stageNumber;
	}

	public void setStageNumber(int stageNumber) {
		this.stageNumber = stageNumber;
	}
	
	public void incrementStageNumber() {
		stageNumber++; 
	}
	
	
}
