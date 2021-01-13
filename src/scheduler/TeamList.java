package scheduler;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class TeamList {
	
	Team[] teams;
	Conference[] conferences;
	
	
	//get list of teams and conferences
	public TeamList(int numteams) {
		String foldername = null;
		if (numteams == 130) {
			foldername = "Teams0130";
			conferences = new Conference[12];
		}
		else if (numteams == 32) {
			foldername = "Teams0032";
			conferences = new Conference[3];
		}
		else if (numteams == 27) {
			foldername = "Teams0027";
			conferences = new Conference[5];
		}
		else {
			teams = null;
			return;
		}
		for (int i = 1; i < conferences.length; i++) {
			conferences[i] = new Conference(i);
		}
		teams = new Team[numteams + 1];
		File f = new File(foldername);
		File teamxml;
		String[] list = f.list();
		for (int i = 3; i < list.length; i++) {
			teamxml = new File(foldername + "/" + list[i], "teamdata.xml");
			try {
				Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(teamxml);
				teams[i-2] = new Team(i-2, doc.getElementsByTagName("SchoolName").item(0).getTextContent(), doc.getElementsByTagName("NameShort").item(0).getTextContent(), Integer.parseInt(doc.getElementsByTagName("LeagueDivision").item(0).getTextContent()));
				conferences[teams[i-2].getConferenceID()].addTeam(teams[i-2]);
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

}
