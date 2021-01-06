package scheduler;

import java.util.ArrayList;

public class Conference {
	private ArrayList<Team> teams;
	private String name;
	private int ID;
	
	Conference(int ID){
		this.ID = ID;
		this.name = null;
		teams = new ArrayList<Team>();
	}
	
	Conference(int ID, String name){
		this.ID = ID;
		this.name = name;
		teams = new ArrayList<Team>();
	}
	
	ArrayList<Team> getTeams(){
		return teams;
	}
	
	String getName() {
		return name;
	}
	
	int getID() {
		return ID;
	}
	
	int getTeamID(Team team) {
		return teams.indexOf(team);
	}
	
	int numTeams() {
		return teams.size();
	}
	
	Team getTeam(int ID) {
		return teams.get(ID);
	}
	
	void addTeam(Team team) {
		teams.add(team);
	}
}
