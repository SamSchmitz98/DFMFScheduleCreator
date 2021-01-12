package scheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;

public class SeasonMaker {

	int confweeks;
	int nonconweeks;
	int byeweeks;
	int weeks;
	int retryflag;
	int retryflag1;
	String schedulestring;
	Random random = new Random(1);
	Team[] teams;
	Conference[] conferences;
	Team[][] schedule;
	ArrayList<Matchup> matchups;
	HashMap<Integer, Integer> nonconcount;
	HashMap<Integer, Integer> byeweekcount;

	SeasonMaker(Team[] teams, Conference[] conferences, int confweeks, int nonconweeks, int byeweeks,
			ArrayList<Matchup> matchups) {
		this.teams = teams;
		this.conferences = conferences;
		this.confweeks = confweeks;
		this.nonconweeks = nonconweeks;
		this.byeweeks = byeweeks;
		weeks = confweeks + nonconweeks + byeweeks;
		this.matchups = matchups;
		this.nonconcount = new HashMap<Integer, Integer>();
		this.byeweekcount = new HashMap<Integer, Integer>();
		schedulestring = "game_id,week,home_team_id,home_conference_id,away_team_id,away_conference_id,Conference";
	}

	void createScheduleTemplate() {
		schedule = new Team[weeks][teams.length];
		addRequestedMatchups();
		adjustAvailableConferenceMembers();
	}

	void addRequestedMatchups() {
		for (Matchup curmatchup : matchups) {
			schedule[curmatchup.getWeek()][curmatchup.getHome().getID()] = curmatchup.getAway();
			schedule[curmatchup.getWeek()][curmatchup.getAway().getID()] = curmatchup.getHome();
			incrementNonconCount(curmatchup.getHome().getID());
			incrementNonconCount(curmatchup.getAway().getID());
		}
	}

	void adjustAvailableConferenceMembers() {
		for (int week = nonconweeks; week < weeks; week++) {
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
							incrementNonconCount(conferences[j].getTeam(tempopponent2).getID());
							incrementNonconCount(conferences[i].getTeam(tempopponent1).getID());
							confcount[i]++;
							confcount[j]++;
						}
					}
				}
			}
		}
	}

	void addByeWeeks() {
		if (byeweeks == 0) {
			return;
		}
		Team BYE = new Team(0, "Bye", "----Bye", -1);
		for (int i = 1; i < teams.length; i++) {
			for (int week = 0; week < schedule.length; week++) {
				if (schedule[week][i] == null) {
					schedule[week][i] = BYE;
					break;
				}
			}
			incrementByeWeekCount(i);
		}
		for (int i = 1; i <= byeweeks; i++) {
			for (int team = 1; team < teams.length; team++) {
				if (byeweekcount.get(team) < i) {
					int week = random.nextInt(weeks - 1);
					for (int j = 0; j < weeks; j++) {
						if ((week == 0 && schedule[week + 1][team] != BYE)
								|| ((week != 0 && schedule[week - 1][team] != BYE)
										&& schedule[week + 1][team] != BYE)) {
							if (schedule[week][team] != null) {
								int teamop = schedule[week][team].getID();
								if ((byeweekcount.containsKey(teamop) && byeweekcount.get(teamop) >= byeweeks)
										|| (week != 0 && schedule[week - 1][teamop] == BYE)
										|| schedule[week + 1][teamop] == BYE) {
									week++;
									week %= weeks - 1;
									continue;
								}
								boolean matchupflag = false;
								for (Matchup match : matchups) {
									if ((match.week == week)
											&& (schedule[week][team] == match.getAway()
													|| schedule[week][team] == match.getHome())
											&& (teams[team] == match.getAway() || teams[team] == match.getHome())) {
										matchupflag = true;
										break;
									}
								}
								if (matchupflag) {
									week++;
									week %= weeks - 1;
								} else {
									if (schedule[week][team] == null && schedule[week][teamop] == null) {
										schedule[week][team] = schedule[week][team];
										schedule[week][team] = BYE;
										incrementByeWeekCount(team);
										schedule[week][teamop] = schedule[week][teamop];
										schedule[week][teamop] = BYE;
										incrementByeWeekCount(teamop);
										break;
									}
//									for (int k = 1; k < weeks - week; k++) {
//										if (schedule[week + k][team] == null && schedule[week + k][teamop] == null) {
//											schedule[week + k][team] = schedule[week][team];
//											schedule[week][team] = BYE;
//											incrementByeWeekCount(team);
//											schedule[week + k][teamop] = schedule[week][teamop];
//											schedule[week][teamop] = BYE;
//											incrementByeWeekCount(teamop);
//											break;
//										}
//									}
//									break;
								}
							} else {
								Conference curconf = conferences[teams[team].getConferenceID()];
								int randconfop = random.nextInt(curconf.numTeams());
								boolean foundteam = false;
								for (int k = 0; k < curconf.numTeams(); k++) {
									if (curconf.getTeam(randconfop) == teams[team]) {
										continue;
									}
									if (schedule[week][curconf.getTeam(randconfop).getID()] == null) {
										int teamop = curconf.getTeam(randconfop).getID();
										if ((byeweekcount.containsKey(teamop) && byeweekcount.get(teamop) >= byeweeks)
												|| (week != 0 && schedule[week - 1][teamop] == BYE)
												|| schedule[week + 1][teamop] == BYE) {
											continue;
										}
										foundteam = true;
										break;
									}
								}
								if (foundteam) {
									schedule[week][team] = BYE;
									incrementByeWeekCount(team);
									schedule[week][curconf.getTeam(randconfop).getID()] = BYE;
									incrementByeWeekCount(curconf.getTeam(randconfop).getID());
									break;
								} else {
									week++;
									week %= weeks - 1;
								}
							}
						} else {
							week++;
							week %= weeks - 1;
						}
					}
				}
			}
		}
	}

	boolean getNextConferenceGame(Conference curconf, int inteam, int week, ArrayList<Team> opponents,
			int conferencegamecounter) {
		// printConferenceSchedule(curconf); // For debugging
		if (retryflag > 50000) {
			return false;
		}
		if (week == weeks) {
			inteam++;
			week = 0;
			conferencegamecounter = 0;
			// printConferenceSchedule(curconf); // For debugging
		}
		if (inteam == curconf.numTeams()) {
			return true;
		}
		int team = curconf.getTeam(inteam).getID();
		if (conferencegamecounter % (curconf.numTeams() - 1) == 0) {
			opponents = getOpponents(curconf.getTeam(inteam), curconf);
			int limit = (curconf.numTeams() - 1);
			for (int k = week; (k < limit && k + week < confweeks); k++) {
				if (schedule[k][team] != null) {
					if (schedule[k][team].getConferenceID() != curconf.getID()) {
						limit++;
					} else {
						opponents.remove(schedule[k + week][team]);
					}
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
				if (!getNextConferenceGame(curconf, inteam, week + 1, opponents, conferencegamecounter + 1)) {
					opponents.add(tempopponent, schedule[week][team]);
					schedule[week][team] = null;
					if (opponents.get(tempopponent).getID() != 0)
						schedule[week][opponents.get(tempopponent).getID()] = null;
					retryflag++;
					if (retryflag > 50000) {
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
		if (schedule[week][team].getConferenceID() == curconf.getID()) {
			return getNextConferenceGame(curconf, inteam, week + 1, opponents, conferencegamecounter + 1);
		}
		return getNextConferenceGame(curconf, inteam, week + 1, opponents, conferencegamecounter);
	}

	boolean getNextNonConGame(int team, int week) {
		if (retryflag1 > 1000000) {
			return false;
		}
		while (nonconcount.containsKey(team) && nonconcount.get(team) >= nonconweeks) {
			week = 0;
			team++;
		}
		if (team == teams.length) {
			return true;
		}
		if (schedule[week][team] == null) {
			Team curteam = teams[team];
			Team tempteam;
			int tempopponent;
			int tempnum = random.nextInt(teams.length - 1) + 1;
			for (int i = 1; i < teams.length; i++) {
				tempopponent = ((tempnum + i) % (teams.length - 1) + 1);
				tempteam = teams[tempopponent];
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
				if (nonconcount.containsKey(tempopponent) && nonconcount.get(tempopponent) >= nonconweeks) {
					continue;
				}
				schedule[week][team] = teams[tempopponent];
				schedule[week][tempopponent] = curteam;
				incrementNonconCount(team);
				incrementNonconCount(tempopponent);
				if (!getNextNonConGame(team, week + 1)) {
					schedule[week][team] = null;
					schedule[week][tempopponent] = null;
					deincrementNonconCount(team);
					deincrementNonconCount(tempopponent);
					retryflag1++;
					if (retryflag1 > 1000000) {
						return false;
					}
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
			while (!getNextConferenceGame(curconf, 0, 0, opponents, 0)) {
				retryflag = 0;
			}
			printConferenceSchedule(curconf);
		}
	}

	void generateRegularSeason() {
		if (nonconweeks != 0) {
			do {
				retryflag1 = 0;
				createScheduleTemplate();
			} while (!getNextNonConGame(1, 0));
		} else {
			createScheduleTemplate();
		}
		addByeWeeks();
		if (confweeks != 0) {
			for (int i = 1; i < conferences.length; i++) {
				for (int week = 0; week < weeks; week++) {
					int counter = 0;
					for (int j = 0; j < conferences[i].numTeams(); j++) {
						if (schedule[week][conferences[i].getTeam(j).getID()] != null && schedule[week][conferences[i].getTeam(j).getID()].getName() == "Bye") {
							counter++;
						}
					}
					System.out.println("Conference " + i + " had " + counter + " bye weeks in week" + week);
				}
			}
			for (int i = 1; i < conferences.length; i++) { // for each conference
				System.out.println("Conference " + i);
				retryflag = 0;
				ArrayList<Team> opponents = new ArrayList<Team>();
				Conference curconf = conferences[i];
				Team[][] schedulecopy = new Team[schedule.length][schedule[0].length];
				for (int j = 0; j < schedule.length; j++) {
					for (int k = 0; k < schedule[j].length; k++) {
						schedulecopy[j][k] = schedule[j][k];
					}
				}
				while (!getNextConferenceGame(curconf, 0, 0, opponents, 0)) {
					retryflag = 0;
					schedule = new Team[weeks][teams.length];
					for (int j = 0; j < schedule.length; j++) {
						for (int k = 0; k < schedule[j].length; k++) {
							schedule[j][k] = schedulecopy[j][k];
						}
					}
				}
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
				if (schedule[i][j] != null && schedule[i][j].getID() != 0) {
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
		for (int i = 0; i < conference.numTeams(); i++) {
			str += String.format("%1$" + 25 + "s", conference.getTeam(i).getName() + "  ");
		}
		str += "\n";
		for (int i = 0; i < 25 * conference.numTeams(); i++) {
			str += "_";
		}
		str += "\n";
		for (int i = 0; i < weeks; i++) {
			for (int j = 0; j < conference.numTeams(); j++) {
				str += (schedule[i][conference.getTeam(j).getID()] == null ? "------------------------|"
						: String.format("%1$" + 25 + "s", schedule[i][conference.getTeam(j).getID()].getName() + "|"));
			}
			str += "\n";
		}

		str += "\n\n";
		System.out.print(str);
	}

	void printSchedule() {
		String str = "";
		for (int i = 1; i < teams.length; i++) {
			str += String.format("%1$" + 8 + "s", teams[i].getAbbrev());
		}
		str += "\n";
		for (int i = 1; i < 8 * teams.length; i++) {
			str += "_";
		}
		str += "\n";
		for (int i = 0; i < weeks; i++) {
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

	void incrementNonconCount(int index) {
		if (nonconcount.containsKey(index)) {
			nonconcount.put(index, nonconcount.get(index) + 1);
		} else {
			nonconcount.put(index, 1);
		}
	}

	void deincrementNonconCount(int index) {
		if (nonconcount.containsKey(index)) {
			nonconcount.put(index, nonconcount.get(index) - 1);
		} else {
			nonconcount.put(index, 0);
		}
	}

	void incrementByeWeekCount(int index) {
		if (byeweekcount.containsKey(index)) {
			byeweekcount.put(index, byeweekcount.get(index) + 1);
		} else {
			byeweekcount.put(index, 1);
		}
	}

	void deincrementByeWeekCount(int index) {
		if (byeweekcount.containsKey(index)) {
			byeweekcount.put(index, byeweekcount.get(index) - 1);
		} else {
			byeweekcount.put(index, 0);
		}
	}

	String getSeason() {
		return schedulestring;
	}
}
