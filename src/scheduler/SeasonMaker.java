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
	Team[] teams;
	Conference[] conferences;
	Team[][] schedule;
	ArrayList<Matchup> matchups;

	SeasonMaker(Team[] teams, Conference[] conferences, int confweeks, int nonconweeks, ArrayList<Matchup> matchups) {
		this.teams = teams;
		this.conferences = conferences;
		this.confweeks = confweeks;
		this.nonconweeks = nonconweeks;
		this.matchups = matchups;
		schedulestring = "game_id,week,home_team_id,home_conference_id,away_team_id,away_conference_id,Conference";
	}

	void addRequestedMatchups() {
		for (Matchup curmatchup : matchups) {
			schedule[curmatchup.getWeek()][curmatchup.getHome().getID()] = curmatchup.getAway();
			schedule[curmatchup.getWeek()][curmatchup.getAway().getID()] = curmatchup.getHome();
		}
	}

	void adjustAvailableConferenceMembers() {
		for (int week = nonconweeks; week < nonconweeks + confweeks; week++) {
			int[] confcount = new int[conferences.length + 1];
			for (Matchup curmatchup : matchups) {
				if (curmatchup.getWeek() == week) {
					if (!curmatchup.isConference()) {
						confcount[curmatchup.getHome().getConferenceID()]++;
						confcount[curmatchup.getAway().getConferenceID()]++;
					}
				}
			}
			for (int i = 1; i < confcount.length; i++) {
				if (confcount[i] % 2 != 0) {
					for (int j = i + 1; j < confcount.length; j++) {
						if (confcount[j] % 2 != 0) {
							System.out.println("Here!");
							int tempopponent1 = random.nextInt(conferences[i].numTeams());
							for (int k = 0; k < conferences[i].numTeams(); k++) {
								tempopponent1 %= conferences[i].numTeams();
								if (schedule[week][conferences[i].getTeam(tempopponent1).getID()] == null) {
									break;
								}
								tempopponent1++;
							}
							int tempopponent2 = random.nextInt(conferences[j].numTeams());
							for (int l = 0; l < conferences[j].numTeams(); l++) {
								tempopponent2 %= conferences[j].numTeams();
								if (schedule[week][conferences[j].getTeam(tempopponent2).getID()] == null) {
									break;
								}
								tempopponent2++;
							}
							schedule[week][conferences[i].getTeam(tempopponent1).getID()] = conferences[j]
									.getTeam(tempopponent2);
							schedule[week][conferences[j].getTeam(tempopponent2).getID()] = conferences[i]
									.getTeam(tempopponent1);
							confcount[i]++;
							confcount[j]++;
						}
					}
				}
			}
		}
	}

	boolean getNextConferenceGame(Conference curconf, int inteam, int week, ArrayList<Team> opponents) {
		// printConferenceSchedule(curconf);
		if (retryflag > 5000000) {
			return false;
		}
		if (week == confweeks + nonconweeks) {
			inteam++;
			week = 0;
			// printConferenceSchedule(curconf); // For debugging
		}
		if (inteam == curconf.numTeams()) {
			return true;
		}
		int team = curconf.getTeam(inteam).getID();
		if (week % (curconf.numTeams() - 1) == 0) {
			opponents = getOpponents(curconf.getTeam(inteam), curconf);
			for (int k = week % (curconf.numTeams() - 1); (k < (curconf.numTeams() - 1) && k + week < confweeks); k++) {
				if (schedule[k + week][team] != null) {
					opponents.remove(schedule[k + week][team]);
				}
			}
		}
		if (schedule[week][team] == null) {
			int tempopponent = random.nextInt(opponents.size());
			int l;
			for (l = 0; l < opponents.size(); l++) {
				tempopponent++;
				tempopponent %= opponents.size();
				if (week > 0 && schedule[week - 1][team] == opponents.get(tempopponent)) {
					continue;
				}
				if (week < confweeks - 1 && schedule[week + 1][team] == opponents.get(tempopponent)) {
					continue;
				}
				if (opponents.get(tempopponent).getID() != 0
						&& schedule[week][opponents.get(tempopponent).getID()] != null) {
					continue;
				}
				schedule[week][team] = opponents.remove(tempopponent); // schedule game
				if (schedule[week][team].getID() != 0)
					schedule[week][schedule[week][team].getID()] = curconf.getTeam(inteam);
				if (!getNextConferenceGame(curconf, inteam, week + 1, opponents)) {
					opponents.add(tempopponent, schedule[week][team]);
					schedule[week][team] = null;
					if (opponents.get(tempopponent).getID() != 0)
						schedule[week][opponents.get(tempopponent).getID()] = null;
					retryflag++;
					if (retryflag > 5000000) {
						return false;
					}
				} else {
					return true;
				}
			}
			if (schedule[week][team] == null) {
				return false;
			}
		}
		return getNextConferenceGame(curconf, inteam, week + 1, opponents);
	}

	boolean getNextNonConGame(int team, int week) {
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
			int tempopponent = random.nextInt(teams.length - 1) + 1;
			tempteam = teams[tempopponent];
			for (int i = 1; i < teams.length; i++) {
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
				if (!getNextNonConGame(team, week + 1)) {
					schedule[week][team] = null;
					schedule[week][tempopponent] = null;
				} else {
					return true;
				}
			}
			return false;
		}
		return getNextNonConGame(team, week + 1);
	}

	void generateConferenceOnlySeason() {
		for (int i = 1; i < conferences.length; i++) { // for each conference
			retryflag = 0;
			ArrayList<Team> opponents = new ArrayList<Team>();
			Conference curconf = conferences[i];
			Team[][] conferenceschedule = new Team[confweeks][curconf.numTeams()];
			while (!getNextConferenceGame(curconf, 0, 0, opponents)) {
				retryflag = 0;
				conferenceschedule = new Team[confweeks][curconf.numTeams()];
			}
			printConferenceSchedule(curconf);
		}
	}

	void generateRegularSeason() {
		do {
			schedule = new Team[confweeks + nonconweeks][teams.length];
			addRequestedMatchups();
			adjustAvailableConferenceMembers();
			//printSchedule();
		} while (!getNextNonConGame(1, 0));
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
			while (!getNextConferenceGame(curconf, 0, 0, opponents)) {
				retryflag = 0;
				schedule = new Team[confweeks + nonconweeks][teams.length];
				for (int j = 0; j < schedule.length; j++) {
					for (int k = 0; k < schedule[j].length; k++) {
						schedule[j][k] = schedulecopy[j][k];
					}
				}
				addRequestedMatchups();
				adjustAvailableConferenceMembers();
				//printSchedule();
			}
		}
		printSchedule();
		arrayToSchedule();
	}

	/*
	 * Turn 2d array into schedule string used by game
	 */
	void arrayToSchedule() {
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
					schedulestring += (teams[j].getConferenceID() == schedule[i][j].getConferenceID() ? "TRUE"
							: "FALSE");
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
	void printConferenceSchedule(Conference conference) {
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

	void printSchedule() {
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
