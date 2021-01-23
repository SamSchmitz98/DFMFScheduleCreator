package scheduler;

public class Matchup {

	Team home;
	Team away;
	int week;
	
	Matchup(int week, Team home, Team away){
		this.week = week;
		this.home = home;
		this.away = away;
	}
	
	Matchup(int week, Team home){
		this.week = week;
		this.home = home;
		this.away = null;
	}
	
	Team getHome() {
		return home;
	}
	
	Team getAway() {
		return away;
	}
	
	int getWeek() {
		return week;
	}
	
	boolean isBye() {
		return away == null;
	}
	
	boolean isConference() {
		if (isBye()) {
			return false;
		}
		return home.getConferenceID() == away.getConferenceID();
	}
	
	public String toString() {
		return away + " at " + home + " in week " + week;
	}
	
}
