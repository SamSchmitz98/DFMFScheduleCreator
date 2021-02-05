package scheduler;

import java.util.ArrayList;
import java.util.Random;

public class SeasonMaker {

	int confweeks;
	int nonconweeks;
	int byeweeks;
	int weeks;
	int retryflag;
	int retryflag1;
	String schedulestring;
	String error;
	Random random = new Random(1);
	Team[] teams;
	Conference[] conferences;
	int[] nonconcount;
	int[] byeweekcount;
	Team[][] schedule;
	Boolean[][] homeschedule;
	ArrayList<Matchup> matchups;
	Team BYE = new Team(0, "--Bye", "----Bye", -1);

	SeasonMaker(Team[] teams, Conference[] conferences, int confweeks, int nonconweeks, int byeweeks,
			ArrayList<Matchup> matchups) {
		this.teams = teams;
		this.conferences = conferences;
		this.confweeks = confweeks;
		this.nonconweeks = nonconweeks;
		this.byeweeks = byeweeks;
		weeks = confweeks + nonconweeks + byeweeks;
		this.matchups = matchups;
		this.nonconcount = new int[teams.length];
		this.byeweekcount = new int[teams.length];
		schedulestring = "game_id,week,home_team_id,home_conference_id,away_team_id,away_conference_id,Conference";

		this.error = "Nothing";
	}

	void createScheduleTemplate() {
		schedule = new Team[weeks][teams.length];
		this.nonconcount = new int[teams.length];
		this.byeweekcount = new int[teams.length];
		addRequestedMatchups();
		adjustAvailableConferenceMembers();
	}

	void createHomeSchedule() {
		homeschedule = new Boolean[weeks][teams.length];
		for (Matchup cur : matchups) {
			homeschedule[cur.getWeek()][cur.getHome().getID()] = true;
			homeschedule[cur.getWeek()][cur.getAway().getID()] = false;
		}
		int counter = 1;
		for (int team = 1; team < teams.length; team++) {
			for (int week = 0; week < weeks; week++) {
				if (homeschedule[week][team] == null && schedule[week][team].getID() != 0) {
					if (counter % 2 == 0) {
						homeschedule[week][team] = true;
						homeschedule[week][schedule[week][team].getID()] = false;
					} else {
						homeschedule[week][team] = false;
						homeschedule[week][schedule[week][team].getID()] = true;
					}
					counter++;
				}
			}
		}
	}

	void addRequestedMatchups() {
		for (Matchup curmatchup : matchups) {
			schedule[curmatchup.getWeek()][curmatchup.getHome().getID()] = curmatchup.getAway();
			schedule[curmatchup.getWeek()][curmatchup.getAway().getID()] = curmatchup.getHome();
			nonconcount[curmatchup.getHome().getID()]++;
			nonconcount[curmatchup.getAway().getID()]++;
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
							int tempopponent1 = random.nextInt(conferences[i].size());
							for (int k = 0; k < conferences[i].size(); k++) {
								tempopponent1 %= conferences[i].size();
								if (schedule[week][conferences[i].getTeam(tempopponent1).getID()] == null) {
									break;
								}
								tempopponent1++;
							}
							int tempopponent2 = random.nextInt(conferences[j].size());
							for (int l = 0; l < conferences[j].size(); l++) {
								tempopponent2 %= conferences[j].size();
								if (schedule[week][conferences[j].getTeam(tempopponent2).getID()] == null) {
									break;
								}
								tempopponent2++;
							}
							schedule[week][conferences[i].getTeam(tempopponent1).getID()] = conferences[j]
									.getTeam(tempopponent2);
							schedule[week][conferences[j].getTeam(tempopponent2).getID()] = conferences[i]
									.getTeam(tempopponent1);
							nonconcount[conferences[j].getTeam(tempopponent2).getID()]++;
							nonconcount[conferences[i].getTeam(tempopponent1).getID()]++;
							confcount[i]++;
							confcount[j]++;
						}
					}
				}
			}
		}
	}

	void addByeWeeks() {
		for (int i = 1; i < conferences.length; i++) {
			if (conferences[i].size() % 2 != 0) {
				while (!createOddConferenceByeWeek(conferences[i]))
					System.out.println("Here?");
			}
		}
		if (byeweeks == 0) {
			return;
		}
		for (int i = 0; i <= byeweeks; i++) {
			for (int team = 1; team < teams.length; team++) {
				if (i == 3 && team == 3) {
				}
				if (byeweekcount[team] < i) {
					int week = random.nextInt(weeks - 2);
					for (int j = 0; j < weeks; j++) {
						if ((week == 0 && schedule[week + 1][team] != BYE && schedule[week + 2][team] != BYE)
								|| (week == 1 && schedule[week - 1][team] != BYE && schedule[week + 1][team] != BYE
										&& schedule[week + 2][team] != BYE)
								|| ((week > 1 && schedule[week - 1][team] != BYE && schedule[week - 2][team] != BYE)
										&& schedule[week + 1][team] != BYE && schedule[week + 2][team] != BYE)
										&& schedule[week][team] == null) {
							Conference curconf = conferences[teams[team].getConferenceID()];
							int randconfop = random.nextInt(curconf.size());
							boolean foundteam = false;
							for (int k = 0; k < curconf.size(); k++) {
								if (curconf.getTeam(randconfop) == teams[team]) {
									continue;
								}
								if (schedule[week][curconf.getTeam(randconfop).getID()] == null) {
									int teamop = curconf.getTeam(randconfop).getID();
									if ((byeweekcount[teamop] >= byeweeks)
											|| (week > 0 && schedule[week - 1][teamop] == BYE)
											|| (week > 1 && schedule[week - 2][teamop] == BYE)
											|| schedule[week + 1][teamop] == BYE || schedule[week + 2][teamop] == BYE) {
										continue;
									}
									foundteam = true;
									break;
								}
							}
							if (foundteam) {
								schedule[week][team] = BYE;
								byeweekcount[team]++;
								schedule[week][curconf.getTeam(randconfop).getID()] = BYE;
								byeweekcount[curconf.getTeam(randconfop).getID()]++;
								break;
							} else {
								week++;
								week %= weeks - 2;
							}

						} else {
							week++;
							week %= weeks - 2;
						}
					}
				}
			}
		}

	}

	boolean getNextConferenceGame(Conference curconf, int inteam, int week, ArrayList<Team> opponents,
			int conferencegamecounter) {
//		printConferenceSchedule(curconf); // For debugging
		if (retryflag > 5000) {
			return false;
		}
		if (week == weeks) {
			inteam++;
			week = 0;
			conferencegamecounter = 0;
			// printConferenceSchedule(curconf); // For debugging
		}
		if (inteam == curconf.size()) {
			return true;
		}
		int team = curconf.getTeam(inteam).getID();
		if (conferencegamecounter % (curconf.size() - 1) == 0) {
			opponents = getOpponents(curconf.getTeam(inteam), curconf);
			int limit = curconf.size() - 1;
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
					error = "repeat opponent behind" + week + " " + team;
					continue;
				}
				if (week < confweeks - 1 && schedule[week + 1][team] == opponents.get(tempopponent)) {
					error = "repeat opponent ahead" + week + " " + team;
					continue;
				}
				if (opponents.get(tempopponent).getID() != 0
						&& schedule[week][opponents.get(tempopponent).getID()] != null) {
					error = "opponent already scheduled" + week + " " + team;
					continue;
				}
				if (opponents.get(tempopponent).getID() == 0) {
					boolean byeflag = false;
					for (int i = 0; i < curconf.size(); i++) {
						if (schedule[week][curconf.getTeamID(curconf.getTeam(i))] != null
								&& schedule[week][curconf.getTeamID(curconf.getTeam(i))].getAbbrev()
										.contentEquals("Bye")) {
							byeflag = true;
							break;
						}
					}
					if (byeflag) {
						error = "conference already had byeweek" + week + " " + team;
						continue;
					}
				}
				schedule[week][team] = opponents.remove(tempopponent); // schedule game
				if (schedule[week][team].getID() != 0) {
					schedule[week][schedule[week][team].getID()] = curconf.getTeam(inteam);
				}
				if (!getNextConferenceGame(curconf, inteam, week + 1, opponents, conferencegamecounter + 1)) {
					opponents.add(tempopponent, schedule[week][team]);
					schedule[week][team] = null;
					if (opponents.get(tempopponent).getID() != 0)
						schedule[week][opponents.get(tempopponent).getID()] = null;
					retryflag++;
					if (retryflag > 5000) {
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
//		printSchedule();
		if (retryflag1 > 1000000) {
			return false;
		}
		if (team == teams.length) {
			week++;
			team = 1;
		}
		if(week == weeks) {
			return true;
		}
		while (nonconcount[team] >= nonconweeks) {
			team++;
			if (team == teams.length) {
				week++;
				team = 1;
			}
			if(week == weeks) {
				return true;
			}
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
				if (nonconcount[tempopponent] >= nonconweeks) {
					continue;
				}
				if (havePlayed(curteam, tempteam)) {
					continue;
				}
				if (!conferenceGameScheduled(tempteam, week)) {
					continue;
				}
				schedule[week][team] = teams[tempopponent];
				schedule[week][tempopponent] = curteam;
				nonconcount[team]++;
				nonconcount[tempopponent]++;
				if (!getNextNonConGame(team+1, week)) {
					schedule[week][team] = null;
					schedule[week][tempopponent] = null;
					nonconcount[team]--;
					nonconcount[tempopponent]--;
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
		return getNextNonConGame(team+1, week);
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
		printSchedule();
		for (int i = 1; i < conferences.length; i++) {
			int total = 0;
			for (int j = 0; j < conferences[i].size(); j++) {
				if (schedule[3][conferences[i].getTeam(j).getID()] == null) {
					total++;
				}
			}
			System.out.println(conferences[i] + " " + total);
		}
		if (confweeks != 0) {
			for (int i = 1; i < conferences.length; i++) { // for each conference
				int count = 0;
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
					count++;
					if (count >= 500) {
						System.out.println(curconf + "  " + error);
						generateRegularSeason();
						return;
					}
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
		createHomeSchedule();
		arrayToSchedule();
	}

	/*
	 * Turn 2d array into schedule string used by game
	 */
	void arrayToSchedule() {
		Team[][] schedule = new Team[weeks][teams.length];
		for (int week = 0; week < weeks; week++) {
			for (int team = 1; team < teams.length; team++) {
				schedule[week][team] = this.schedule[week][team];
			}
		}
		int counter = 1;
		for (int i = 0; i < schedule.length; i++) {
			for (int j = 1; j < schedule[i].length; j++) {
				if (schedule[i][j] != null && schedule[i][j].getID() != 0) {
					schedulestring += "\n" + counter + "," + (i + 1) + ",";
					if (homeschedule[i][j] != null && homeschedule[i][j] == true) {
						schedulestring += teams[j].getID() + "," + teams[j].getConferenceID() + ",";
						schedulestring += schedule[i][j].getID() + "," + schedule[i][j].getConferenceID() + ",";
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
	String printConferenceSchedule(Conference conference) {
		String str = "";
		for (int i = 0; i < conference.size(); i++) {
			str += String.format("%1$" + 25 + "s", conference.getTeam(i).getName() + "  ");
		}
		str += "\n";
		for (int i = 0; i < 25 * conference.size(); i++) {
			str += "_";
		}
		str += "\n";
		for (int i = 0; i < weeks; i++) {
			for (int j = 0; j < conference.size(); j++) {
				str += (schedule[i][conference.getTeam(j).getID()] == null ? "------------------------|"
						: String.format("%1$" + 25 + "s", schedule[i][conference.getTeam(j).getID()].getName() + "|"));
			}
			str += "\n";
		}

		str += "\n\n";
		System.out.print(str);
		return str;
	}

	String printSchedule() {
		String str = "";
		for (int i = 1; i < teams.length; i++) {
			str += String.format("%1$" + 9 + "s", teams[i].getAbbrev());
		}
		str += "\n";
		str += "\n";
		for (int i = 0; i < weeks; i++) {
			for (int j = 1; j < schedule[i].length; j++) {
				str += (schedule[i][j] == null ? "--------|"
						: (String.format("%1$" + 9 + "s",
								(homeschedule != null && homeschedule[i][j] != null && !homeschedule[i][j] ? "@" : " ")
										+ schedule[i][j].getAbbrev() + "|")));
			}
			str += "\n";
		}
		System.out.println(str);
		return str;
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

	boolean createOddConferenceByeWeek(Conference curconf) {
		Team[][] schedule = new Team[this.schedule.length][this.schedule[0].length];
		for (int i = 0; i < this.schedule.length; i++) {
			for (int j = 0; j < this.schedule[i].length; j++) {
				schedule[i][j] = this.schedule[i][j];
			}
		}
		ArrayList<Integer> weeknums = new ArrayList<Integer>();
		for (int i = 0; i < curconf.size(); i++) {
			weeknums.add(i);
		}
		for (int i = 0; i < curconf.size(); i++) {
			int index = random.nextInt(weeknums.size());
			int j;
			nextweek: for (j = 0; j < weeknums.size(); j++) {
				int week = weeknums.remove((index + j) % weeknums.size());
				int counter = 0;
				while (week + (curconf.size() * counter) < weeks) {
					if (schedule[week + (curconf.size() * counter)][curconf.getTeam(i).getID()] == null) {
						schedule[week + (curconf.size() * counter)][curconf.getTeam(i).getID()] = BYE;
						byeweekcount[curconf.getTeam(i).getID()]++;
						counter++;
					} else if (week + (curconf.size() * counter) < nonconweeks) {
						counter++;
					} else {
						weeknums.add((index + j) % weeknums.size(), week);
						while (week + (curconf.size() * counter) >= 0) {
							if (schedule[week + (curconf.size() * counter)][curconf.getTeam(i).getID()] == BYE) {
								schedule[week + (curconf.size() * counter)][curconf.getTeam(i).getID()] = null;
								byeweekcount[curconf.getTeam(i).getID()]--;
								counter--;
							}
						}
						continue nextweek;
					}
				}
				break;
			}
			if (!weeknums.isEmpty() && j == weeknums.size()) {
				return false;
			}
		}
		for (int i = 0; i < this.schedule.length; i++) {
			for (int j = 0; j < this.schedule[i].length; j++) {
				this.schedule[i][j] = schedule[i][j];
			}
		}
		return true;
	}

	boolean havePlayed(Team team1, Team team2) {
		for (int i = 0; i < weeks; i++) {
			if (schedule[i][team1.getID()] == team2) {
				return true;
			}
		}
		return false;
	}
	
	boolean needsNoncon() {
		for (int i = 1; i < teams.length; i++) {
			if (nonconcount[i] < nonconweeks) {
				return true;
			}
		}
		return false;
	}

	boolean conferenceGameScheduled(Team curteam, int week) {
		int emptyweeks = 0;
		for (int i = 0; i < week; i++) {
			if (this.schedule[i][curteam.getID()] == null) {
				emptyweeks++;
			}
		}
		if (nonconcount[curteam.getID()] + emptyweeks < nonconweeks) {
			return true;
		}
		Conference curconf = conferences[curteam.getConferenceID()];
		Team[][] schedule = new Team[weeks][teams.length];
		for (int i = 0; i < weeks; i++) {
			for (int j = 0; j < teams.length; j++) {
				schedule[i][j] = this.schedule[i][j];
			}
		}
		nextweek:for (int i = week - 1; i >= 0; i--) {
			if (schedule[i][curteam.getID()] == null) {
				int confteamnumber = random.nextInt(curconf.size());
				for (int j = 0; j < curconf.size(); j++) {
					Team confteam = curconf.getTeam(confteamnumber);
					if (confteam == curteam) {
						confteamnumber++;
						confteamnumber %= curconf.size();
						continue;
					}
					if (schedule[i][confteam.getID()] == null) {
						schedule[i][confteam.getID()] = curteam;
						schedule[i][curteam.getID()] = confteam;
						continue nextweek;
					}
				}
				return false;
			}
		}
		for (int i = 0; i < weeks; i++) {
			for (int j = 0; j < teams.length; j++) {
				this.schedule[i][j] = schedule[i][j];
			}
		}
		return true;
	}

	String getSeason() {
		return schedulestring;
	}
}
