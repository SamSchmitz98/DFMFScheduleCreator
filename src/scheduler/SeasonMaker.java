package scheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Stack;

public class SeasonMaker {

	int weeks;
	String schedulestring;
	Random random = new Random(1);

	SeasonMaker(int weeks) {
		this.weeks = weeks;
		schedulestring = "game_id,week,home_team_id,home_conference_id,away_team_id,away_conference_id,Conference";
	}

	void generateConferenceOnlySeason(Team[] teams, Conference[] conferences) {
		ArrayList<Team> opponents = new ArrayList<Team>();
		for (int i = 1; i < conferences.length; i++) { //for each conference
			Conference curconf = conferences[i];
			Team[][] conferenceschedule = new Team[weeks][curconf.numTeams()];
			for (int j = 0; j < curconf.numTeams(); j++) { //for each team in conference
				for (int week = 0; week < weeks; week++) { //for each week
					if (week % (curconf.numTeams() - 1) == 0) { //this gets list of opponents so everyone is played once before repeats
						opponents = getOpponents(curconf.getTeam(j), curconf);
						for (int k = week % (curconf.numTeams() - 1); (k < (curconf.numTeams() - 1)
								&& k + week < weeks); k++) {
							if (conferenceschedule[k + week][j] != null) {
								opponents.remove(conferenceschedule[k + week][j]);
							}
						}
					}
					if (opponents.isEmpty()) { //if everyone has been played, loop
						continue;
					}
					if (conferenceschedule[week][j] == null) { 
						int tempopponent = random.nextInt(opponents.size());
						int l;
						boolean flag = false;
						for (l = 0; l < opponents.size(); l++) {
							tempopponent += l;
							tempopponent %= opponents.size();
							if (week > 0 && conferenceschedule[week - 1][j] == opponents.get(tempopponent)) {//ensure no team plays twice in a row
								continue;
							}
							if (week < weeks - 1 && conferenceschedule[week + 1][j] == opponents.get(tempopponent)) {//ensure no team plays twice in a row
								continue;
							}
							if (conferenceschedule[week][curconf.getTeamID(opponents.get(tempopponent))] != null) {//ensure a team isn't scheduled against a team that already has a game
								continue;
							}
							for (int k = 0; k < curconf.numTeams(); k++) {
								if (conferenceschedule[week][k] == opponents.get(tempopponent)) { //ensure another team hasn't scheduled them already
									flag = true;
									break;
								}
							}
							if (flag == true) {
								continue;
							}
							conferenceschedule[week][j] = opponents.remove(tempopponent); //schedule game
							conferenceschedule[week][curconf.getTeamID(conferenceschedule[week][j])] = curconf
									.getTeam(j);
							break;
						}
					}
				}
				//printConferenceSchedule(conferenceschedule, curconf);
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
	 *  Print schedule for debugging purposes
	 * @param schedule
	 * @param conference
	 */
	void printConferenceSchedule(Team[][] schedule, Conference conference) {
		for (int i = 0; i < schedule[0].length; i++) {
			System.out.print(String.format("%1$" + 25 + "s", conference.getTeam(i).getName() + "  "));
		}
		System.out.println("");
		for (int i = 0; i < 25 * schedule[0].length; i++) {
			System.out.print("_");
		}
		System.out.println("");
		for (int i = 0; i < schedule.length; i++) {
			for (int j = 0; j < schedule[i].length; j++) {
				System.out.print((schedule[i][j] == null ? "------------------------|"
						: String.format("%1$" + 25 + "s", schedule[i][j].getName() + "|")));
			}
			System.out.println("");
		}

		System.out.println("");
		System.out.println("");
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
