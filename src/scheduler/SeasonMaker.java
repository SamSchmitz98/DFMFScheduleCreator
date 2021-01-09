package scheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Stack;

public class SeasonMaker {

	int weeks;
	int retryflag;
	String schedulestring;
	Random random = new Random(1);
	

	SeasonMaker(int weeks) {
		this.weeks = weeks;
		schedulestring = "game_id,week,home_team_id,home_conference_id,away_team_id,away_conference_id,Conference";
	}

	boolean getNextConferenceGame(Team[] teams, Conference curconf, Team[][] conferenceschedule, int team, int week, ArrayList<Team> opponents) {
		//printConferenceSchedule(conferenceschedule, curconf);
		if (retryflag > 5000000) {
			return false;
		}
		if (week == weeks) {
			team++;
			week = 0;
			//printConferenceSchedule(conferenceschedule, curconf); // For debugging
		}
		if (team == curconf.numTeams()) {
			return true;
		}
		if (week % (curconf.numTeams() - 1) == 0) {
			opponents = getOpponents(curconf.getTeam(team), curconf);
			for (int k = week % (curconf.numTeams() - 1); (k < (curconf.numTeams() - 1) && k + week < weeks); k++) {
				if (conferenceschedule[k + week][team] != null) {
					opponents.remove(conferenceschedule[k + week][team]);
				}
			}
		}
		if (conferenceschedule[week][team] == null) {
			int tempopponent = random.nextInt(opponents.size());
			int l;
			for (l = 0; l < opponents.size(); l++) {
				tempopponent++;
				tempopponent %= opponents.size();
				if (week > 0 && conferenceschedule[week - 1][team] == opponents.get(tempopponent)) {
					continue;
				}
				if (week < weeks - 1 && conferenceschedule[week + 1][team] == opponents.get(tempopponent)) {
					continue;
				}
				if (conferenceschedule[week][curconf.getTeamID(opponents.get(tempopponent))] != null) {
					continue;
				}
				conferenceschedule[week][team] = opponents.remove(tempopponent); // schedule game
				conferenceschedule[week][curconf.getTeamID(conferenceschedule[week][team])] = curconf.getTeam(team);
				if (!getNextConferenceGame(teams, curconf, conferenceschedule, team, week+1, opponents)) {
					opponents.add(tempopponent, conferenceschedule[week][team]);
					conferenceschedule[week][team] = null;
					conferenceschedule[week][curconf.getTeamID(opponents.get(tempopponent))] = null;
					retryflag++;
					if (retryflag > 5000000) {
						return false;
					}
				} else {
					return true;
				}
			}
			if (conferenceschedule[week][team] == null) {
				return false;
			}
		} 
		return getNextConferenceGame(teams, curconf, conferenceschedule, team, week+1, opponents);
	}

	void generateConferenceOnlySeason(Team[] teams, Conference[] conferences) {
		for (int i = 1; i < conferences.length; i++) { // for each conference
			retryflag = 0;
			ArrayList<Team> opponents = new ArrayList<Team>();
			Conference curconf = conferences[i];
			Team[][] conferenceschedule = new Team[weeks][curconf.numTeams()];
			while (!getNextConferenceGame(teams, curconf, conferenceschedule, 0, 0, opponents)) {
				retryflag = 0;
				conferenceschedule = new Team[weeks][curconf.numTeams()];
			}
			printConferenceSchedule(conferenceschedule, curconf);
			arrayToSchedule(conferenceschedule, curconf);
		}
	}

	//TODO
	void quickGenerateConferenceOnlySeason(Team[] teams, Conference[] conferences) {
		for (int i = 1; i < conferences.length; i++) { // for each conference
			ArrayList<Team> opponents = new ArrayList<Team>();
			Conference curconf = conferences[i];
			Team[][] conferenceschedule = new Team[weeks][curconf.numTeams()];
			for (int j = 0; j < weeks; j++) {
				for (int k = 0; k < curconf.numTeams(); k++) {
					conferenceschedule[j][k] = curconf.getTeam(curconf.numTeams()-((k+j)%curconf.numTeams()+1));
				}
			}
			printConferenceSchedule(conferenceschedule, curconf);
			arrayToSchedule(conferenceschedule, curconf);
		}
	}

	/*
	 * Turn 2d array into schedule string used by game
	 */
	void arrayToSchedule(Team[][] schedule, Conference conference) {
		int counter = 1;
		for (int i = 0; i < schedule.length; i++) {
			for (int j = 0; j < schedule[i].length; j++) {
				if (schedule[i][j] != null) {
					schedulestring += "\n" + counter + "," + (i + 1) + ",";
					if (counter % 2 == 0) {
						schedulestring += schedule[i][j].getID() + "," + conference.getID() + ",";
						schedulestring += conference.getTeam(j).getID() + "," + conference.getID() + ",";
					} else {
						schedulestring += conference.getTeam(j).getID() + "," + conference.getID() + ",";
						schedulestring += schedule[i][j].getID() + "," + conference.getID() + ",";
					}
					schedulestring += "TRUE";
					schedule[i][conference.getTeamID(schedule[i][j])] = null;
					counter++;
				}
			}
		}
	}

	/**
	 * Print schedule for debugging purposes
	 * 
	 * @param schedule
	 * @param conference
	 */
	void printConferenceSchedule(Team[][] schedule, Conference conference) {
		String str = "";
		for (int i = 0; i < schedule[0].length; i++) {
			str += String.format("%1$" + 25 + "s", conference.getTeam(i).getName() + "  ");
		}
		str += "\n";
		for (int i = 0; i < 25 * schedule[0].length; i++) {
			str += "_";
		}
		str += "\n";
		for (int i = 0; i < schedule.length; i++) {
			for (int j = 0; j < schedule[i].length; j++) {
				str += (schedule[i][j] == null ? "------------------------|"
						: String.format("%1$" + 25 + "s", schedule[i][j].getName() + "|"));
			}
			str += "\n";
		}

		str += "\n\n";
		System.out.print(str);
	}

	/*
	 * Get list of everyteam in conference besides team
	 */
	ArrayList<Team> getOpponents(Team team, Conference conference) {
		ArrayList<Team> opponents = new ArrayList<Team>();
		for (Team cur : conference.getTeams()) {
			if (cur != team) {
				opponents.add(cur);
			}
		}
		return opponents;
	}
	
	String getSeason() {
		return schedulestring;
	}
}
