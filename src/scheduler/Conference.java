package scheduler;

import java.util.ArrayList;
import java.util.Comparator;

public class Conference {
	private ArrayList<Team> teams;
	private String name;
	private int ID;
	private int rank;
	private boolean power;
	private boolean independent;
	
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
	
	boolean isPower() {
		return power;
	}
	
	boolean isIndependent() {
		return independent;
	}
	
	Team getTeam(int ID) {
		return teams.get(ID);
	}
	
	int size() {
		return teams.size();
	}
	
	void addTeam(Team team) {
		teams.add(team);
		teams.sort(new Sortbyname());
	}
	
	void setName(String name) {
		this.name = name;
	}
	
	void setRank(int rank) {
		this.rank = rank;
	}
	
	void setPower(boolean power) {
		this.power = power;
	}
	
	void setIndependent(boolean independent) {
		this.independent = independent;
	}
	
	class Sortbyname implements Comparator<Team> {
		public int compare(Team a, Team b) {
			return a.toString().compareTo(b.toString());
		}
	}
}
