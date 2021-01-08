package scheduler;

import java.util.ArrayList;

public class Team {
	private String name;
	private int ID;
	private int conference;
	private ArrayList<Team> rivals;
	
	
	Team(int ID, String name, int conference){
		this.name = name;
		this.ID = ID;
		this.conference = conference;
		this.rivals = new ArrayList<Team>();
	}
	
	void addRival(Team rival) {
		rivals.add(rival);
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
	
	ArrayList<Team> getRivals() {
		return rivals;
	}
}
