package scheduler;

public class Team {
	private String name;
	private int ID;
	private int conference;
	
	Team(int ID, String name, int conference){
		this.name = name;
		this.ID = ID;
		this.conference = conference;
	}
	
	String getName() {
		return name;
	}
	
	int getID() {
		return ID;
	}
	
	int getConferenceID() {
		return conference;
	}
}
