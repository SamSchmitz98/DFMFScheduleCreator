package scheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Stack;

public class SeasonMaker {

	int confweeks;
	int nonconweeks;
	int retryflag;
	int retryflag1;
	String schedulestring;
	Random random = new Random(1);

	SeasonMaker(int confweeks, int nonconweeks) {
		this.confweeks = confweeks;
		this.nonconweeks = nonconweeks;
		schedulestring = "game_id,week,home_team_id,home_conference_id,away_team_id,away_conference_id,Conference";
	}

	boolean getNextConferenceGame(Team[] teams, Conference curconf, Team[][] conferenceschedule, int inteam, int week,
			ArrayList<Team> opponents) {
		// printConferenceSchedule(conferenceschedule, curconf);
		if (retryflag > 5000000) {
			return false;
		}
		if (week == confweeks + nonconweeks) {
			inteam++;
			week = 0;
			// printConferenceSchedule(conferenceschedule, curconf); // For debugging
		}
		if (inteam == curconf.numTeams()) {
			return true;
		}
		int team = curconf.getTeam(inteam).getID();
		if (week % (curconf.numTeams() - 1) == 0) {
			opponents = getOpponents(curconf.getTeam(inteam), curconf);
			for (int k = week % (curconf.numTeams() - 1); (k < (curconf.numTeams() - 1) && k + week < confweeks); k++) {
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
				if (week < confweeks - 1 && conferenceschedule[week + 1][team] == opponents.get(tempopponent)) {
					continue;
				}
				if (opponents.get(tempopponent).getID() != 0
						&& conferenceschedule[week][opponents.get(tempopponent).getID()] != null) {
					continue;
				}
				conferenceschedule[week][team] = opponents.remove(tempopponent); // schedule game
				if (conferenceschedule[week][team].getID() != 0)
					conferenceschedule[week][conferenceschedule[week][team].getID()] = curconf.getTeam(inteam);
				if (!getNextConferenceGame(teams, curconf, conferenceschedule, inteam, week + 1, opponents)) {
					opponents.add(tempopponent, conferenceschedule[week][team]);
					conferenceschedule[week][team] = null;
					if (opponents.get(tempopponent).getID() != 0)
					conferenceschedule[week][opponents.get(tempopponent).getID()] = null;
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
		return getNextConferenceGame(teams, curconf, conferenceschedule, inteam, week + 1, opponents);
	}

	boolean getNextNonConGame(Team[] teams, Conference[] conferences, Team[][] schedule, int team, int week) {
		if (week == nonconweeks) {
			week = 0;
			team++;
		}
		if (team == teams.length) {
			return true;
		}
		if (schedule[week][team] == null) {
			Team curteam = teams[team];
			Team tempteam;
			boolean badopponent = true;
			int tempopponent = random.nextInt(teams.length-1)+1;
			tempteam = teams[tempopponent];
			for (int i = 1; i < teams.length; i ++) {
				badopponent = false;
				if (curteam.getConferenceID() == tempteam.getConferenceID()) {
					continue;
				}
				if (week > 0 && schedule[week - 1][team] == tempteam) {
					continue;
				}
				if (week < nonconweeks - 1 && schedule[week + 1][team] == tempteam) {
					continue;
				}
				if (schedule[week][tempopponent] != null) {
					continue;
				}
				schedule[week][team] = teams[tempopponent];
				schedule[week][tempopponent] = curteam;
				if (!getNextNonConGame(teams, conferences, schedule, team, week + 1)) {
					schedule[week][team] = null;
					schedule[week][tempopponent] = null;
				} else {
					return true;
				}
			}
			return false;
		}
		return getNextNonConGame(teams, conferences, schedule, team, week+1);
	}

	void generateConferenceOnlySeason(Team[] teams, Conference[] conferences) {
		for (int i = 1; i < conferences.length; i++) { // for each conference
			retryflag = 0;
			ArrayList<Team> opponents = new ArrayList<Team>();
			Conference curconf = conferences[i];
			Team[][] conferenceschedule = new Team[confweeks][curconf.numTeams()];
			while (!getNextConferenceGame(teams, curconf, conferenceschedule, 0, 0, opponents)) {
				retryflag = 0;
				conferenceschedule = new Team[confweeks][curconf.numTeams()];
			}
			printConferenceSchedule(conferenceschedule, curconf);
		}
	}

	void generateRegularSeason(Team[] teams, Conference[] conferences) {
		Team[][] schedule = new Team[confweeks + nonconweeks][teams.length];
		while (!getNextNonConGame(teams, conferences, schedule, 1, 0)) {
			schedule = new Team[confweeks + nonconweeks][teams.length];
		}
		for (int i = 1; i < conferences.length; i++) { // for each conference
			retryflag = 0;
			ArrayList<Team> opponents = new ArrayList<Team>();
			Conference curconf = conferences[i];
			Team[][] schedulecopy = new Team[confweeks + nonconweeks][teams.length];
			for (int j = 0; j < schedule.length; j++) {
				for (int k = 0; k < schedule[j].length; k++) {
					schedulecopy[j][k] = schedule[j][k];
				}
			}
			while (!getNextConferenceGame(teams, curconf, schedule, 0, 0, opponents)) {
				retryflag = 0;
				schedule = new Team[confweeks + nonconweeks][teams.length];
				for (int j = 0; j < schedule.length; j++) {
					for (int k = 0; k < schedule[j].length; k++) {
						schedule[j][k] = schedulecopy[j][k];
					}
				}
			}
		}
		printSchedule(schedule, teams);
		arrayToSchedule(schedule, teams);
	}

	/*
	 * Turn 2d array into schedule string used by game
	 */
	void arrayToSchedule(Team[][] schedule, Team[] teams) {
		int counter = 1;
		for (int i = 0; i < schedule.length; i++) {
			for (int j = 1; j < schedule[i].length; j++) {
				if (schedule[i][j] != null) {
					schedulestring += "\n" + counter + "," + (i + 1) + ",";
					if (counter % 2 == 0) {
						schedulestring += schedule[i][j].getID() + "," + schedule[i][j].getConferenceID() + ",";
						schedulestring += teams[j].getID() + "," + teams[j].getConferenceID() + ",";
					} else {
						schedulestring += teams[j].getID() + "," + teams[j].getConferenceID() + ",";
						schedulestring += schedule[i][j].getID() + "," + schedule[i][j].getConferenceID() + ",";
					}
					schedulestring += (teams[j].getConferenceID() == schedule[i][j].getConferenceID() ? "TRUE" : "FALSE");
					schedule[i][schedule[i][j].getID()] = null;
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

	void printSchedule(Team[][] schedule, Team[] teams) {
		String str = "";
		for (int i = 1; i < schedule[0].length; i++) {
			str += String.format("%1$" + 8 + "s", teams[i].getAbbrev());
		}
		str += "\n";
		for (int i = 1; i < 8 * schedule[0].length; i++) {
			str += "_";
		}
		str += "\n";
		for (int i = 0; i < nonconweeks + confweeks; i++) {
			for (int j = 1; j < schedule[i].length; j++) {
				str += (schedule[i][j] == null ? "-------|"
						: String.format("%1$" + 8 + "s", schedule[i][j].getAbbrev() + "|"));
			}
			str += "\n";
		}
		System.out.println(str);
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
		if (conference.numTeams() % 2 != 0) {
			opponents.add(new Team(0, "Bye", "Bye", conference.getID()));
		}
		return opponents;
	}

	String getSeason() {
		return schedulestring;
	}
}
