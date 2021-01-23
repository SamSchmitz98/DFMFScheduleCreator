package scheduler;

import java.util.ArrayList;

public class Conference {
	private ArrayList<Team> teams;
	private String name;
	private int ID;
	private int rank;
	
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
	
	Conference(int ID, String name, int rank){
		this.ID = ID;
		this.name = name;
		this.rank = rank;
		teams = new ArrayList<Team>();
	}
	
	ArrayList<Team> getTeams(){
		return teams;
	}
	
	Team[] getTeamArray(){
		Team[] result = new Team[teams.size()];
		return teams.toArray(result);
	}
	
	String getName() {
		return name;
	}
	
	public String toString() {
		return name;
	}
	
	int getID() {
		return ID;
	}
	
	int getTeamID(Team team) {
		return teams.indexOf(team);
	}
	
	int getRank() {
		return rank;
	}
	
	int size() {
		return teams.size();
	}
	
	Team getTeam(int ID) {
		return teams.get(ID);
	}
	
	void addTeam(Team team) {
		teams.add(team);
	}
}
