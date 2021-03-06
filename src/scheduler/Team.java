package scheduler;

import java.util.ArrayList;

public class Team {
	private String name;
	private String abbrev;
	private int ID;
	private int conference;
	private boolean hasmatchup;
	private ArrayList<Team> rivals;
	
	
	Team(int ID, String name, int conference){
		this.name = name;
		this.ID = ID;
		this.conference = conference;
		this.rivals = new ArrayList<Team>();
	}
	
	Team(int ID, String name, String abbrev, int conference){
		this.name = name;
		this.abbrev = abbrev;
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
	
	String getAbbrev() {
		return abbrev;
	}
	
	int getID() {
		return ID;
	}
	
	int getConferenceID() {
		return conference;
	}
	
	boolean hasMatchup() {
		return hasmatchup;
	}
	
	void setConferenceID(int id) {
		this.conference = id;
	}
	
	void setHasMatchup(boolean bool) {
		this.hasmatchup = bool;
	}
	
	ArrayList<Team> getRivals() {
		return rivals;
	}
	
	public String toString() {
		return name;
	}
}
