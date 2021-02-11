package scheduler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class TeamList {

	Team[] teams;
	Conference[] conferences;

	// get list of teams and conferences
	public TeamList(int numteams) {
		String foldername = null;
		if (numteams == 130) {
			foldername = "Teams0130";
		} else if (numteams == 32) {
			foldername = "Teams0032";
		} else if (numteams == 27) {
			foldername = "Teams0027";
		} else {
			teams = null;
			return;
		}
		teams = new Team[numteams + 1];
		File f = new File(foldername);
		int i = 1;
		int conferencecount = 1;
		for (String filename : f.list()) {
			File curfile = new File(foldername + "/" + filename);
			if (curfile.isDirectory()) {
				File teamxml = new File(foldername + "/" + filename, "teamdata.xml");
				try {
					Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(teamxml);
					teams[i] = new Team(i, doc.getElementsByTagName("SchoolName").item(0).getTextContent(),
							doc.getElementsByTagName("NameShort").item(0).getTextContent(),
							Integer.parseInt(doc.getElementsByTagName("LeagueDivision").item(0).getTextContent()));
					if (teams[i].getConferenceID() > conferencecount) {
						conferencecount = teams[i].getConferenceID();
					}
					i++;
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (filename.contentEquals("leaguesettings.xml")) {
				File leaguexml = new File(foldername + "/" + filename);
				try {
					Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(leaguexml);
					ArrayList<Conference> conferencelist = new ArrayList<Conference>();
					conferencelist.add(new Conference(0, ""));
					for (int j = 1; j < 12; j++) {
						if (doc.getElementsByTagName("Div" + j).item(0) != null) {
							String conferencename = doc.getElementsByTagName("Div" + j).item(0).getChildNodes().item(1)
									.getTextContent();
							conferencelist.add(new Conference(conferencelist.size(), conferencename));
							conferencelist.get(conferencelist.size() - 1).setRank(Integer.parseInt(doc
									.getElementsByTagName("Div" + j).item(0).getChildNodes().item(3).getTextContent()));
						}
					}
					if (doc.getElementsByTagName("Div").item(0) != null) {
						for (int j = 0; j < doc.getElementsByTagName("Div").getLength(); j++) {
							String conferencename = doc.getElementsByTagName("Div").item(j).getChildNodes().item(1)
									.getTextContent();
							conferencelist.add(new Conference(conferencelist.size(), conferencename));
							conferencelist.get(conferencelist.size() - 1).setRank(Integer.parseInt(
									doc.getElementsByTagName("Div").item(j).getChildNodes().item(3).getTextContent()));
						}
					}
					conferences = new Conference[conferencelist.size()];
					for (int k = 0; k < conferences.length; k++) {
						conferences[k] = conferencelist.get(k);
					}
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		for (int j = 1; j < teams.length; j++) {
			conferences[teams[j].getConferenceID()].addTeam(teams[j]);
		}
		File data = new File(foldername + "/CustomConferenceData");
		if (!data.exists()) {
			try {
				FileWriter fw = new FileWriter(data);
				for (int k = 1; k < conferences.length; k++) {
					fw.write("0 0\n");
				}
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				Scanner scanner = new Scanner(data);
				int counter = 1;
				while (scanner.hasNextInt()) {
					if (counter <= conferences.length + 1) {
						if (scanner.nextInt() == 1) {
							conferences[counter].setPower(true);
						} else {
							conferences[counter].setPower(false);
						}
						if (scanner.nextInt() == 1) {
							conferences[counter].setIndependent(true);
						} else {
							conferences[counter].setIndependent(false);
						}
						counter++;
					}
				}
				scanner.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	class Sortbyconferencename implements Comparator<Conference> {
		public int compare(Conference a, Conference b) {
			return a.toString().compareTo(b.toString());
		}
	}

}
