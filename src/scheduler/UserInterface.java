package scheduler;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

@SuppressWarnings("serial")
public class UserInterface extends JFrame implements ActionListener {

	TeamList teamlist27, teamlist32, teamlist130;
	Team[] teams27, teams32, teams130;
	Conference[] conferences27, conferences32, conferences130;

	JPanel schedulepanel, conferencesettingspanel, conferencecreationpanel, helppanel;
	JRadioButton teams27rb, teams32rb, teams130rb;
	JSpinner nonconweeks, conweeks, byeweeks;

	public UserInterface() {
		// Create Panels
		schedulepanel = new JPanel();
		conferencesettingspanel = new JPanel();
		conferencecreationpanel = new JPanel();
		helppanel = new JPanel();
		schedulepanel.setLayout(null);
		conferencesettingspanel.setLayout(null);
		conferencecreationpanel.setLayout(null);
		schedulepanel.setSize(500, 400);
		conferencesettingspanel.setSize(500, 400);
		conferencecreationpanel.setSize(500, 400);
		helppanel.setSize(400, 400);
		schedulepanel.setVisible(true);
		conferencesettingspanel.setVisible(true);
		conferencecreationpanel.setVisible(true);
		helppanel.setVisible(true);

		// Get Teams
		teamlist27 = new TeamList(27);
		teamlist32 = new TeamList(32);
		teamlist130 = new TeamList(130);
		teams27 = teamlist27.teams;
		teams32 = teamlist32.teams;
		teams130 = teamlist130.teams;
		conferences27 = teamlist27.conferences;
		conferences32 = teamlist32.conferences;
		conferences130 = teamlist130.conferences;
		/////////////////////
		// Schedule Panel
		/////////////////////

		// Radio Buttons
		JPanel radiobuttonpanel = new JPanel();
		radiobuttonpanel.setLayout(null);
		radiobuttonpanel.setBounds(150, 0, 300, 30);
		radiobuttonpanel.setBackground(Color.GRAY);
		teams27rb = new JRadioButton("27 Teams");
		teams32rb = new JRadioButton("32 Teams");
		teams130rb = new JRadioButton("130 Teams");
		teams27rb.setSelected(false);
		teams32rb.setSelected(false);
		teams130rb.setSelected(true);
		teams27rb.setBounds(0, 0, 100, 30);
		teams32rb.setBounds(100, 0, 100, 30);
		teams130rb.setBounds(200, 0, 100, 30);
		radiobuttonpanel.add(teams27rb);
		radiobuttonpanel.add(teams32rb);
		radiobuttonpanel.add(teams130rb);
		add(radiobuttonpanel);
		ButtonGroup group = new ButtonGroup();
		group.add(teams27rb);
		group.add(teams32rb);
		group.add(teams130rb);
		// TODO
		teams27rb.setEnabled(false);
		// Buttons
		JButton addmatchupbutton = new JButton("Add Matchup");
		JButton remmatchupbutton = new JButton("Remove Matchup");
		JButton generatebutton = new JButton("Generate Schedule");
		addmatchupbutton.setBounds(10, 120, 110, 20);
		remmatchupbutton.setBounds(130, 120, 140, 20);
		generatebutton.setBounds(390, 100, 160, 40);
		schedulepanel.add(addmatchupbutton);
		schedulepanel.add(remmatchupbutton);
		schedulepanel.add(generatebutton);
		// Week Text Fields
		SpinnerModel spinnermodelnon = new SpinnerNumberModel(3, 0, 15, 1);
		SpinnerModel spinnermodelcon = new SpinnerNumberModel(9, 0, 15, 1);
		SpinnerModel spinnermodelbye = new SpinnerNumberModel(3, 0, 6, 1);
		nonconweeks = new JSpinner(spinnermodelnon);
		conweeks = new JSpinner(spinnermodelcon);
		byeweeks = new JSpinner(spinnermodelbye);
		nonconweeks.setBounds(155, 5, 35, 24);
		conweeks.setBounds(155, 35, 35, 24);
		byeweeks.setBounds(155, 65, 35, 24);
		schedulepanel.add(nonconweeks);
		schedulepanel.add(conweeks);
		schedulepanel.add(byeweeks);
		JLabel nonconlabel = new JLabel("Enter number of weeks of non conference play");
		JLabel conlabel = new JLabel("Enter number of weeks of conference play");
		JLabel byelabel = new JLabel("Enter number of bye weeks");
		nonconlabel.setBounds(200, 0, 270, 30);
		conlabel.setBounds(200, 30, 270, 30);
		byelabel.setBounds(200, 60, 270, 30);
		schedulepanel.add(nonconlabel);
		schedulepanel.add(conlabel);
		schedulepanel.add(byelabel);
		// Requested Matchups
		JPanel innerpanel = new JPanel();
		innerpanel.setLayout(null);
		innerpanel.setPreferredSize(new Dimension(450, 100));
		ArrayList<JComboBox<Team>> awayteams = new ArrayList<JComboBox<Team>>();
		ArrayList<JLabel> ats = new ArrayList<JLabel>();
		ArrayList<JComboBox<Team>> hometeams = new ArrayList<JComboBox<Team>>();
		ArrayList<JLabel> weeklabels = new ArrayList<JLabel>();
		ArrayList<JSpinner> weektexts = new ArrayList<JSpinner>();
		JScrollPane matchuppanel = new JScrollPane(innerpanel);
		matchuppanel.setBounds(3, 150, 572, 170);
		matchuppanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		schedulepanel.add(matchuppanel);
		revalidate();

		/////////////////////
		// Conference Settings Panel
		/////////////////////

		// Conference Selection
		JComboBox<Conference> conferences = new JComboBox<Conference>(conferences130);
		conferences.removeItemAt(0);
		conferences.setBounds(10, 10, 235, 30);
		conferencesettingspanel.add(conferences);
		// Conference Information
		JTextField conferencename = new JTextField(conferences130[1].toString());
		SpinnerModel conferencerank = new SpinnerNumberModel(6, 1, 99, -1);
		JSpinner conferencerankspinner = new JSpinner(conferencerank);
		JLabel conferenceranklabel = new JLabel("Conference Rank");
		JRadioButton powerconference = new JRadioButton("Power Conference");
		JRadioButton independent = new JRadioButton("Independent");
		JButton updateconference = new JButton("Update");
		JButton deleteconference = new JButton("Delete Conference");
		conferencename.setBounds(10, 50, 236, 20);
		conferencerankspinner.setBounds(115, 73, 35, 24);
		conferenceranklabel.setBounds(10, 80, 100, 10);
		powerconference.setBounds(10, 100, 140, 25);
		independent.setBounds(150, 100, 100, 25);
		updateconference.setBounds(10, 130, 90, 25);
		deleteconference.setBounds(105, 130, 140, 25);
		conferencesettingspanel.add(conferencename);
		conferencesettingspanel.add(conferencerankspinner);
		conferencesettingspanel.add(conferenceranklabel);
		conferencesettingspanel.add(powerconference);
		conferencesettingspanel.add(independent);
		conferencesettingspanel.add(updateconference);
		conferencesettingspanel.add(deleteconference);
		// Conference Championship
		JRadioButton championship = new JRadioButton("Conference Chamionship");
		JTextField championshiptitle = new JTextField("American Athletic Conference Championship");
		JButton championshiplogo = new JButton("Upload Logo");
		JButton championshiplogodefault = new JButton("Default");
		JButton championshipupdate = new JButton("Update Championship");
		championship.setBounds(335, 215, 200, 25);
		championshiptitle.setBounds(335, 240, 236, 20);
		championshiplogo.setBounds(335, 260, 125, 20);
		championshiplogodefault.setBounds(465, 260, 105, 20);
		championshipupdate.setBounds(335, 285, 170, 30);
		conferencesettingspanel.add(championship);
		conferencesettingspanel.add(championshiptitle);
		conferencesettingspanel.add(championshiplogo);
		conferencesettingspanel.add(championshiplogodefault);
		conferencesettingspanel.add(championshipupdate);
		// Conference Poaching
		JComboBox<Team> fromconferencemembers = new JComboBox<Team>(conferences130[2].getTeamArray());
		JComboBox<Conference> fromconferences = new JComboBox<Conference>(conferences130);
		JButton recieve = new JButton("Recieve");
		JLabel fromthe = new JLabel("From the");
		fromconferences.removeItemAt(0);
		fromconferences.removeItemAt(0);
		fromconferencemembers.setBounds(335, 35, 235, 25);
		fromconferences.setBounds(335, 80, 235, 25);
		recieve.setBounds(335, 10, 90, 20);
		fromthe.setBounds(335, 65, 60, 10);
		conferencesettingspanel.add(fromconferencemembers);
		conferencesettingspanel.add(fromconferences);
		conferencesettingspanel.add(recieve);
		conferencesettingspanel.add(fromthe);
		// Conference Sending
		JComboBox<Team> toconferencemembers = new JComboBox<Team>(conferences130[1].getTeamArray());
		JComboBox<Conference> toconferences = new JComboBox<Conference>(conferences130);
		JButton send = new JButton("Send");
		JLabel tothe = new JLabel("To the");
		toconferences.removeItemAt(0);
		toconferences.removeItemAt(0);
		toconferencemembers.setBounds(335, 145, 235, 25);
		toconferences.setBounds(335, 190, 235, 25);
		send.setBounds(335, 120, 90, 20);
		tothe.setBounds(335, 175, 60, 10);
		conferencesettingspanel.add(toconferencemembers);
		conferencesettingspanel.add(toconferences);
		conferencesettingspanel.add(send);
		conferencesettingspanel.add(tothe);
		// Conference Preview
		JPanel previewpanel = new JPanel();
		previewpanel.setLayout(null);
		ArrayList<JLabel> conferencepreviewteams = new ArrayList<JLabel>();
		for (int i = 0; i < conferences130[1].size(); i++) {
			conferencepreviewteams.add(new JLabel(conferences130[1].getTeam(i).toString()));
			conferencepreviewteams.get(i).setBounds(0, (i * 15), 200, 15);
			previewpanel.setPreferredSize(new Dimension(200, ((i + 1) * 15)));
			previewpanel.add(conferencepreviewteams.get(i));
		}
		JScrollPane previewscrollpane = new JScrollPane(previewpanel);
		previewscrollpane.setBounds(10, 160, 235, 170);
		previewscrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		conferencesettingspanel.add(previewscrollpane);

		/////////////////////
		// Conference Creation Panel
		/////////////////////

		// Conference Information
		JLabel createaconference = new JLabel("<HTML><U><B>Create a Conference</B><U></HTML>");
		createaconference.setFont(new Font(createaconference.getName(), Font.PLAIN, 20));
		JTextField newconferencename = new JTextField();
		JLabel newconferencenamelabel = new JLabel("Conference Name:");
		SpinnerModel newconferencerank = new SpinnerNumberModel(12, 1, 99, -1);
		JSpinner newconferencerankspinner = new JSpinner(newconferencerank);
		JLabel newconferenceranklabel = new JLabel("Conference Rank");
		JButton createbutton = new JButton("Create Conference");
		createaconference.setBounds(10, 8, 235, 20);
		newconferencename.setBounds(10, 50, 235, 20);
		newconferencenamelabel.setBounds(10, 30, 235, 25);
		newconferencerankspinner.setBounds(115, 73, 35, 24);
		newconferenceranklabel.setBounds(10, 80, 100, 10);
		createbutton.setBounds(10, 105, 160, 25);
		conferencecreationpanel.add(createaconference);
		conferencecreationpanel.add(newconferencename);
		conferencecreationpanel.add(newconferencenamelabel);
		conferencecreationpanel.add(newconferencerankspinner);
		conferencecreationpanel.add(newconferenceranklabel);
		conferencecreationpanel.add(createbutton);

		/////////////////////
		// Help Panel
		/////////////////////

		JTextArea helpTextArea = new JTextArea(20, 33);
		helpTextArea.setText(HelpString.helpString());
		JScrollPane helpScrollableTextArea = new JScrollPane(helpTextArea);
		helpScrollableTextArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		helppanel.add(helpScrollableTextArea);

		/////////////////////
		// Tabbed Panel
		/////////////////////

		JTabbedPane tp = new JTabbedPane();
		tp.setBounds(0, 30, 600, 400);
		tp.add("Schedule", schedulepanel);
		tp.add("Conference Settings", conferencesettingspanel);
		tp.add("Conference Creation", conferencecreationpanel);
		tp.add("Help", helppanel);
		add(tp);

		setLayout(null);
		setSize(600, 430);
		setVisible(true);

		addmatchupbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int count = awayteams.size();
				if (teams27rb.isSelected()) {
					awayteams.add(new JComboBox<Team>(teams27));
					awayteams.get(count).removeItemAt(0);
					hometeams.add(new JComboBox<Team>(teams27));
					hometeams.get(count).removeItemAt(0);
				}
				if (teams32rb.isSelected()) {
					awayteams.add(new JComboBox<Team>(teams32));
					awayteams.get(count).removeItemAt(0);
					hometeams.add(new JComboBox<Team>(teams32));
					hometeams.get(count).removeItemAt(0);
				}
				if (teams130rb.isSelected()) {
					awayteams.add(new JComboBox<Team>(teams130));
					awayteams.get(count).removeItemAt(0);
					hometeams.add(new JComboBox<Team>(teams130));
					hometeams.get(count).removeItemAt(0);
				}
				ats.add(new JLabel("at"));
				weeklabels.add(new JLabel("Week"));
				weektexts.add(new JSpinner(new SpinnerNumberModel(1, 1, 99, 1)));
				awayteams.get(count).setBounds(5, 10 + count * 40, 215, 30);
				hometeams.get(count).setBounds(250, 10 + count * 40, 215, 30);
				ats.get(count).setBounds(230, 10 + count * 40, 30, 30);
				weeklabels.get(count).setBounds(485, 10 + count * 40, 40, 30);
				weektexts.get(count).setBounds(525, 10 + count * 40, 35, 30);
				innerpanel.add(awayteams.get(count));
				innerpanel.add(hometeams.get(count));
				innerpanel.add(ats.get(count));
				innerpanel.add(weeklabels.get(count));
				innerpanel.add(weektexts.get(count));
				innerpanel.setPreferredSize(new Dimension(550, (count + 1) * 40));
				innerpanel.revalidate();
				matchuppanel.repaint();
				revalidate();
			}
		});

		remmatchupbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int count = awayteams.size() - 1;
				if (count < 0) {
					return;
				}
				innerpanel.remove(awayteams.get(count));
				innerpanel.remove(hometeams.get(count));
				innerpanel.remove(ats.get(count));
				innerpanel.remove(weeklabels.get(count));
				innerpanel.remove(weektexts.get(count));
				awayteams.remove(count);
				hometeams.remove(count);
				ats.remove(count);
				weeklabels.remove(count);
				weektexts.remove(count);
				innerpanel.setPreferredSize(new Dimension(450, (count + 1) * 40));
				innerpanel.revalidate();
				matchuppanel.repaint();
				revalidate();
			}
		});

		teams27rb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!awayteams.isEmpty() && awayteams.get(0).getItemCount() != teams27.length - 1) {
					for (int i = 0; i < awayteams.size(); i++) {
						awayteams.get(i).removeAllItems();
						hometeams.get(i).removeAllItems();
						for (int j = 1; j < teams27.length; j++) {
							awayteams.get(i).addItem(teams27[j]);
							hometeams.get(i).addItem(teams27[j]);
						}
					}
					innerpanel.revalidate();
					matchuppanel.repaint();
					revalidate();
				}
				if (conferences.getItemAt(0) != conferences27[1]) {
					conferences.removeAllItems();
					for (int i = 1; i < conferences27.length; i++) {
						conferences.addItem(conferences27[i]);
					}
					conferencesettingspanel.repaint();
				}
			}
		});

		teams32rb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!awayteams.isEmpty() && awayteams.get(0).getItemCount() != teams32.length - 1) {
					for (int i = 0; i < awayteams.size(); i++) {
						awayteams.get(i).removeAllItems();
						hometeams.get(i).removeAllItems();
						for (int j = 1; j < teams32.length; j++) {
							awayteams.get(i).addItem(teams32[j]);
							hometeams.get(i).addItem(teams32[j]);
						}
					}
					innerpanel.revalidate();
					matchuppanel.repaint();
					revalidate();
				}
				if (conferences.getItemAt(0) != conferences32[1]) {
					conferences.removeAllItems();
					for (int i = 1; i < conferences32.length; i++) {
						conferences.addItem(conferences32[i]);
					}
					conferencesettingspanel.repaint();
				}
			}
		});

		teams130rb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!awayteams.isEmpty() && awayteams.get(0).getItemCount() != teams130.length - 1) {
					for (int i = 0; i < awayteams.size(); i++) {
						awayteams.get(i).removeAllItems();
						hometeams.get(i).removeAllItems();
						for (int j = 1; j < teams130.length; j++) {
							awayteams.get(i).addItem(teams130[j]);
							hometeams.get(i).addItem(teams130[j]);
						}
					}
					innerpanel.revalidate();
					matchuppanel.repaint();
					revalidate();
				}
				if (conferences.getItemAt(0) != conferences130[1]) {
					conferences.removeAllItems();
					for (int i = 1; i < conferences130.length; i++) {
						conferences.addItem(conferences130[i]);
					}
					conferencesettingspanel.repaint();
				}
			}
		});

		updateconference.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Conference curconf = ((Conference) conferences.getSelectedItem());
				curconf.setName(conferencename.getText());
				curconf.setRank((int) conferencerank.getValue());
				curconf.setPower(powerconference.isSelected());
				curconf.setIndependent(independent.isSelected());
				if (!updateConferenceInfo(curconf)) {
					JOptionPane.showMessageDialog(new JFrame(), "Something went wrong");
					return;
				}
				conferences.repaint();
				JOptionPane.showMessageDialog(new JFrame(), "Conference Updated");
			}
		});

		deleteconference.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Conference curconf = ((Conference) conferences.getSelectedItem());
				if (curconf.size() != 0) {
					JOptionPane.showMessageDialog(new JFrame(), "Cannot delete a conference with Teams in it");
					return;
				}
				if (!deleteConference(curconf)) {
					JOptionPane.showMessageDialog(new JFrame(), "Something went wrong");
					return;
				}
				conferences.removeItem(curconf);
				conferences.repaint();
				JOptionPane.showMessageDialog(new JFrame(), "Conference Removed");
			}
		});

		conferences.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (conferences.getSelectedItem() == null) {
					return;
				}
				Conference curconf = (Conference) conferences.getSelectedItem();
				powerconference.setSelected(curconf.isPower());
				independent.setSelected(curconf.isIndependent());
				toconferencemembers.removeAllItems();
				for (int i = 0; i < curconf.size(); i++) {
					toconferencemembers.addItem(curconf.getTeam(i));
				}
				toconferences.removeAllItems();
				fromconferences.removeAllItems();
				if (teams27rb.isSelected()) {
					for (int i = 1; i < conferences27.length; i++) {
						toconferences.addItem(conferences27[i]);
						fromconferences.addItem(conferences27[i]);
					}
				}
				if (teams32rb.isSelected()) {
					for (int i = 1; i < conferences32.length; i++) {
						toconferences.addItem(conferences32[i]);
						fromconferences.addItem(conferences32[i]);
					}
				}
				if (teams130rb.isSelected()) {
					for (int i = 1; i < conferences130.length; i++) {
						toconferences.addItem(conferences130[i]);
						fromconferences.addItem(conferences130[i]);
					}
				}
				toconferences.removeItem(curconf);
				fromconferences.removeItem(curconf);
				conferencename.setText(curconf.toString());
				conferencerank.setValue(curconf.getRank());
				previewpanel.setLayout(null);
				for (int i = 0; i < conferencepreviewteams.size(); i++) {
					previewpanel.remove(conferencepreviewteams.get(i));
				}
				conferencepreviewteams.removeAll(conferencepreviewteams);
				for (int i = 0; i < curconf.size(); i++) {
					conferencepreviewteams.add(new JLabel(curconf.getTeam(i).toString()));
					conferencepreviewteams.get(i).setBounds(0, (i * 15), 200, 15);
					previewpanel.setPreferredSize(new Dimension(200, ((i + 1) * 15)));
					previewpanel.add(conferencepreviewteams.get(i));
				}
				previewscrollpane.setBounds(10, 160, 235, 170);
				previewscrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
				conferencesettingspanel.add(previewscrollpane);
				conferencesettingspanel.repaint();
				conferencesettingspanel.revalidate();
			}
		});

		fromconferences.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (fromconferences.getSelectedItem() == null) {
					return;
				}
				Conference curconf = (Conference) fromconferences.getSelectedItem();
				fromconferencemembers.removeAllItems();
				for (int i = 0; i < curconf.size(); i++) {
					fromconferencemembers.addItem(curconf.getTeam(i));
				}
				conferencesettingspanel.repaint();
			}
		});

		recieve.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (fromconferencemembers.getSelectedItem() != null) {
					Conference curconf = (Conference) conferences.getSelectedItem();
					Conference fromconference = (Conference) fromconferences.getSelectedItem();
					Team newteam = (Team) fromconferencemembers.getSelectedItem();
					fromconference.getTeams().remove(newteam);
					curconf.addTeam(newteam);
					fromconferencemembers.removeItem(newteam);
					newteam.setConferenceID(curconf.getID());
					for (int i = 0; i < conferencepreviewteams.size(); i++) {
						previewpanel.remove(conferencepreviewteams.get(i));
					}
					conferencepreviewteams.removeAll(conferencepreviewteams);
					for (int i = 0; i < curconf.size(); i++) {
						conferencepreviewteams.add(new JLabel(curconf.getTeam(i).toString()));
						conferencepreviewteams.get(i).setBounds(0, (i * 15), 200, 15);
						previewpanel.setPreferredSize(new Dimension(200, (i * 15)));
						previewpanel.add(conferencepreviewteams.get(i));
					}
					toconferencemembers.removeAllItems();
					for (int i = 0; i < curconf.size(); i++) {
						toconferencemembers.addItem(curconf.getTeam(i));
					}
					conferencesettingspanel.repaint();
					conferencesettingspanel.revalidate();
					String filepath;
					if (teams27rb.isSelected()) {
						filepath = "Teams0027/Team" + (String.format("%03d", newteam.getID()) + "/teamdata.xml");
					} else if (teams32rb.isSelected()) {
						filepath = "Teams0032/Team" + (String.format("%03d", newteam.getID()) + "/teamdata.xml");
					} else {
						filepath = "Teams0130/Team" + (String.format("%03d", newteam.getID()) + "/teamdata.xml");
					}
					File f = new File(filepath);
					try {
						Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
						doc.getElementsByTagName("LeagueDivision").item(0).setTextContent(curconf.getID() + "");
						TransformerFactory transformerFactory = TransformerFactory.newInstance();
						Transformer transformer;
						try {
							transformer = transformerFactory.newTransformer();
							DOMSource source = new DOMSource(doc);
							StreamResult result = new StreamResult(new File(f.getPath()));
							try {
								transformer.transform(source, result);
							} catch (TransformerException te) {
								// TODO Auto-generated catch block
								te.printStackTrace();
							}
						} catch (TransformerConfigurationException tce) {
							// TODO Auto-generated catch block
							tce.printStackTrace();
						}
					} catch (SAXException | IOException | ParserConfigurationException e1) {
						JOptionPane.showMessageDialog(new JFrame(), "Error saving conference member changes");
						return;
					}
				} else {
					JOptionPane.showMessageDialog(new JFrame(), "No team selected to recieve");
				}
			}
		});

		send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (toconferencemembers.getSelectedItem() != null) {
					Conference curconf = (Conference) conferences.getSelectedItem();
					Conference toconference = (Conference) toconferences.getSelectedItem();
					Team newteam = (Team) toconferencemembers.getSelectedItem();
					toconference.addTeam(newteam);
					curconf.getTeams().remove(newteam);
					toconferencemembers.removeItem(newteam);
					newteam.setConferenceID(toconference.getID());
					for (int i = 0; i < conferencepreviewteams.size(); i++) {
						previewpanel.remove(conferencepreviewteams.get(i));
					}
					conferencepreviewteams.removeAll(conferencepreviewteams);
					for (int i = 0; i < curconf.size(); i++) {
						conferencepreviewteams.add(new JLabel(curconf.getTeam(i).toString()));
						conferencepreviewteams.get(i).setBounds(0, (i * 15), 200, 15);
						previewpanel.setPreferredSize(new Dimension(200, (i * 15)));
						previewpanel.add(conferencepreviewteams.get(i));
					}
					if (fromconferences.getSelectedItem() == toconference) {
						fromconferencemembers.removeAllItems();
						for (int i = 0; i < toconference.size(); i++) {
							fromconferencemembers.addItem(toconference.getTeam(i));
						}
					}
					conferencesettingspanel.repaint();
					conferencesettingspanel.revalidate();
					String filepath;
					if (teams27rb.isSelected()) {
						filepath = "Teams0027/Team" + (String.format("%03d", newteam.getID()) + "/teamdata.xml");
					} else if (teams32rb.isSelected()) {
						filepath = "Teams0032/Team" + (String.format("%03d", newteam.getID()) + "/teamdata.xml");
					} else {
						filepath = "Teams0130/Team" + (String.format("%03d", newteam.getID()) + "/teamdata.xml");
					}
					File f = new File(filepath);
					try {
						Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
						doc.getElementsByTagName("LeagueDivision").item(0).setTextContent(toconference.getID() + "");
						TransformerFactory transformerFactory = TransformerFactory.newInstance();
						Transformer transformer;
						try {
							transformer = transformerFactory.newTransformer();
							DOMSource source = new DOMSource(doc);
							StreamResult result = new StreamResult(new File(f.getPath()));
							try {
								transformer.transform(source, result);
							} catch (TransformerException te) {
								// TODO Auto-generated catch block
								te.printStackTrace();
							}
						} catch (TransformerConfigurationException tce) {
							// TODO Auto-generated catch block
							tce.printStackTrace();
						}
					} catch (SAXException | IOException | ParserConfigurationException e1) {
						JOptionPane.showMessageDialog(new JFrame(), "Error saving conference member changes");
						return;
					}
				} else {
					JOptionPane.showMessageDialog(new JFrame(), "No team selected to send");
				}
			}
		});

		championshiplogo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(new JFrame(), "Functionality not implemented yet");
			}
		});

		championshiplogodefault.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(new JFrame(), "Functionality not implemented yet");
			}
		});

		championshipupdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(new JFrame(), "Functionality not implemented yet");
			}
		});

		createbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File f;
				int numcon;
				if (teams27rb.isSelected()) {
					f = new File("Teams0027/leaguesettings.xml");
					numcon = conferences27.length;
				} else if (teams32rb.isSelected()) {
					f = new File("Teams0032/leaguesettings.xml");
					numcon = conferences32.length;
				} else {
					f = new File("Teams0130/leaguesettings.xml");
					numcon = conferences130.length;
				}
				try {
					Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
					Text name = doc.createTextNode(newconferencename.getText());
					Element namenode = doc.createElement("Name");
					namenode.appendChild(name);
					Text rank = doc.createTextNode(newconferencerank.getValue() + "");
					Element ranknode = doc.createElement("Rank");
					ranknode.appendChild(rank);
					Element divisionnode = doc.createElement("Div");
					doc.getElementsByTagName("Divisions").item(0).appendChild(divisionnode);
					divisionnode.appendChild(doc.createTextNode("\n"));
					divisionnode.appendChild(namenode);
					divisionnode.appendChild(doc.createTextNode("\n"));
					divisionnode.appendChild(ranknode);
					divisionnode.appendChild(doc.createTextNode("\n"));
					doc.normalize();
					TransformerFactory transformerFactory = TransformerFactory.newInstance();
					Transformer transformer;
					try {
						transformer = transformerFactory.newTransformer();
						DOMSource source = new DOMSource(doc);
						StreamResult result = new StreamResult(new File(f.getPath()));
						try {
							transformer.transform(source, result);
						} catch (TransformerException te) {
							// TODO Auto-generated catch block
							te.printStackTrace();
						}
					} catch (TransformerConfigurationException tce) {
						// TODO Auto-generated catch block
						tce.printStackTrace();
					}
				} catch (SAXException | IOException | ParserConfigurationException e1) {
					JOptionPane.showMessageDialog(new JFrame(), "Error opening leaguesettings.xml");
					e1.printStackTrace();
					return;
				}
				if (teams27rb.isSelected()) {
					teamlist27 = new TeamList(27);
					teams27 = teamlist27.teams;
					conferences27 = teamlist27.conferences;
					conferences.addItem(conferences27[conferences27.length - 1]);
					toconferences.addItem(conferences27[conferences27.length - 1]);
					fromconferences.addItem(conferences27[conferences27.length - 1]);
				} else if (teams32rb.isSelected()) {
					teamlist32 = new TeamList(32);
					teams32 = teamlist32.teams;
					conferences32 = teamlist32.conferences;
					conferences.addItem(conferences32[conferences32.length - 1]);
					toconferences.addItem(conferences32[conferences32.length - 1]);
					fromconferences.addItem(conferences32[conferences32.length - 1]);
				} else {
					teamlist130 = new TeamList(130);
					teams130 = teamlist130.teams;
					conferences130 = teamlist130.conferences;
					conferences.addItem(conferences130[conferences130.length - 1]);
					toconferences.addItem(conferences130[conferences130.length - 1]);
					fromconferences.addItem(conferences130[conferences130.length - 1]);
				}
				File data;
				if (teams27rb.isSelected()) {
					data = new File("Teams0027" + "/CustomConferenceData");
				} else if (teams32rb.isSelected()) {
					data = new File("Teams0032" + "/CustomConferenceData");
				} else {
					data = new File("Teams0130" + "/CustomConferenceData");
				}
				try {
					FileWriter fw = new FileWriter(data, true);
					fw.write("0 0\n");
					fw.close();
				} catch (IOException ioe) {
					// TODO Auto-generated catch block
					ioe.printStackTrace();
				}
				JOptionPane.showMessageDialog(new JFrame(), "Conference Created");
			}

		});

		generatebutton.addActionListener(new ActionListener() {
			@SuppressWarnings("static-access")
			public void actionPerformed(ActionEvent event) {
				int teamamount;
				Team[] teams;
				Conference[] conferences;
				ArrayList<Matchup> matchups = new ArrayList<Matchup>();
				if (teams27rb.isSelected()) {
					teams = teams27;
					conferences = conferences27;
					teamamount = 27;
				} else if (teams32rb.isSelected()) {
					teams = teams32;
					conferences = conferences32;
					teamamount = 32;
				} else {
					teams = teams130;
					conferences = conferences130;
					teamamount = 130;
				}
				int numweeks = Integer.parseInt(conweeks.getValue().toString())
						+ Integer.parseInt(nonconweeks.getValue().toString())
						+ Integer.parseInt(byeweeks.getValue().toString());
				for (int i = 0; i < awayteams.size(); i++) {
					Team away = (Team) awayteams.get(i).getSelectedItem();
					Team home = (Team) hometeams.get(i).getSelectedItem();
					if (home == away) {
						JOptionPane.showMessageDialog(new JFrame(),
								"Teams cannot be the same. (Matchup " + (i + 1) + ")");
						return;
					}

					if (Integer.parseInt(weektexts.get(i).getValue().toString()) - 1 > numweeks) {
						JOptionPane.showMessageDialog(new JFrame(),
								"Week of matchup cannot be outside season weeks. (Matchup " + (i + 1) + ")");
						return;
					}
					try {
						matchups.add(
								new Matchup(Integer.parseInt(weektexts.get(i).getValue().toString()) - 1, home, away));
						home.setHasMatchup(true);
						away.setHasMatchup(true);
					} catch (NumberFormatException e) {
						JOptionPane.showMessageDialog(new JFrame(), "Week Values must be Integers");
						return;
					}
				}
				SeasonMaker sm = new SeasonMaker(teams, conferences, Integer.parseInt(conweeks.getValue().toString()),
						Integer.parseInt(nonconweeks.getValue().toString()),
						Integer.parseInt(byeweeks.getValue().toString()), matchups);

				sm.generateRegularSeason();
				JOptionPane jopt = new JOptionPane();
				String text = sm.printSchedule();
				JTextArea textArea = new JTextArea(text);
				JScrollPane scrollPane = new JScrollPane(textArea);
				scrollPane.setPreferredSize(new Dimension(1000, 500));
//				JLabel resLabel = new JLabel(text);
				textArea.setFont(new Font("Monospaced", Font.PLAIN, 20));
				if (jopt.showConfirmDialog(null, scrollPane, "Schedule Generation Done",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					File directory = new File("StoredSchedules" + teamamount);
					if (!directory.exists()) {
						directory.mkdir();
						try {
							FileWriter fw = new FileWriter(
									new File("StoredSchedules" + teamamount + "\\StoredSchedulesREADME.txt"));
							fw.write(
									"Each season is picked at random from a list of templates, \nso to ensure that your generated schedule is chosen, \nall previously created schedules are moved here");
							fw.close();
						} catch (IOException ioe) {
							JOptionPane.showMessageDialog(new JFrame(), "Error Creating StoredScheduleREADME");
						}
					}
					for (File file : new File("Schedules" + teamamount).listFiles()) {
						File temp = new File("StoredSchedules" + teamamount + "\\" + file.getName());
						if (temp.exists()) {
							String filename = temp.getName();
							filename = filename.substring(0, filename.length() - 4);
							int copyid = 1;
							File copyfiledest = new File(
									"StoredSchedules" + teamamount + "\\" + filename + "(" + copyid + ").csv");
							while (copyfiledest.exists()) {
								copyid++;
								copyfiledest = new File(
										"StoredSchedules" + teamamount + "\\" + filename + "(" + copyid + ").csv");
							}
							try {
								Files.move(file.toPath(), copyfiledest.toPath());
							} catch (IOException copyerror) {
								JOptionPane.showMessageDialog(new JFrame(), "Error renaming file " + file.getName());
							}
						} else {
							try {
								Files.move(file.toPath(), temp.toPath());
							} catch (IOException moveerror) {
								JOptionPane.showMessageDialog(new JFrame(), "Error moving file " + file.getName());
							}
						}
					}
					File f = new File("Schedules" + teamamount + "\\schedule_template_0.csv");
					try {
						f.createNewFile();
						FileWriter writer = new FileWriter(f);
						writer.write(sm.getSeason());
						writer.close();
					} catch (IOException exc) {

					}
					for (int i = 0; i < teams.length; i++) {
						teams[i].setHasMatchup(false);
					}
					JOptionPane.showMessageDialog(new JFrame(), "Schedule Created");
				}
			}
		});
	}

	public static void main(String[] args) {
		new UserInterface();
	}

	boolean updateConferenceInfo(Conference curconf) {
		String foldername;
		if (teams27rb.isSelected()) {
			foldername = "Teams0027";
		}
		if (teams32rb.isSelected()) {
			foldername = "Teams0032";
		} else {
			foldername = "Teams0130";
		}
		File f = new File(foldername + "/leaguesettings.xml");
		Document doc;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			JOptionPane.showMessageDialog(new JFrame(), "Error Opening " + foldername + "/leaguesettings.xml");
			e.printStackTrace();
			return false;
		}
		if (doc.getElementsByTagName("Div" + curconf.getID()).getLength() != 0) {
			doc.getElementsByTagName("Div" + curconf.getID()).item(0).getChildNodes().item(1)
					.setTextContent(curconf.getName());
			doc.getElementsByTagName("Div" + curconf.getID()).item(0).getChildNodes().item(3)
					.setTextContent(curconf.getRank() + "");
		} else {
			int premade = 1;
			while (doc.getElementsByTagName("Div" + premade).getLength() != 0) {
				premade++;
			}
			doc.getElementsByTagName("Div").item(curconf.getID()-premade).getChildNodes().item(1)
					.setTextContent(curconf.getName());
			doc.getElementsByTagName("Div").item(curconf.getID()-premade).getChildNodes().item(3)
					.setTextContent(curconf.getRank() + "");
		}
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(f.getPath()));
			try {
				transformer.transform(source, result);
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File data = new File(foldername + "/CustomConferenceData");
		String contents = "";
		int counter = 1;
		Scanner scanner;
		try {
			scanner = new Scanner(data);
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(new JFrame(), "Error Opening " + foldername + "/CustomConferenceData");
			e.printStackTrace();
			return false;
		}
		while (scanner.hasNextInt()) {
			if (counter == curconf.getID()) {
				scanner.nextInt();
				scanner.nextInt();
				contents += (curconf.isPower() ? "1 " : "0 ");
				contents += (curconf.isIndependent() ? "1\n" : "0\n");
			} else {
				contents += scanner.nextInt() + " " + scanner.nextInt() + "\n";
			}
			counter++;
		}
		scanner.close();
		FileWriter fw;
		try {
			fw = new FileWriter(data);
			fw.write(contents);
			fw.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(new JFrame(), "Error Opening " + foldername + "/CustomConferenceData");
			e.printStackTrace();
		}
		return true;
	}

	boolean deleteConference(Conference curconf) {
		String foldername;
		if (teams27rb.isSelected()) {
			foldername = "Teams0027";
		}
		if (teams32rb.isSelected()) {
			foldername = "Teams0032";
		} else {
			foldername = "Teams0130";
		}
		File f = new File(foldername + "/leaguesettings.xml");
		Document doc;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			JOptionPane.showMessageDialog(new JFrame(), "Error Opening " + foldername + "/leaguesettings.xml");
			e.printStackTrace();
			return false;
		}
		if (doc.getElementsByTagName("Div" + curconf.getID()).getLength() != 0) {
			JOptionPane.showMessageDialog(new JFrame(), "Cannot delete base conference (but it can just be left empty)");
			return false;
		} else {
			int premade = 1;
			while (doc.getElementsByTagName("Div" + premade).getLength() != 0) {
				premade++;
			}
			Element element = (Element) doc.getElementsByTagName("Div").item(curconf.getID()-premade);
			element.getParentNode().removeChild(element);
			doc.normalize();
		}
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(f.getPath()));
			try {
				transformer.transform(source, result);
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}File data = new File(foldername + "/CustomConferenceData");
		String contents = "";
		int counter = 1;
		Scanner scanner;
		try {
			scanner = new Scanner(data);
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(new JFrame(), "Error Opening " + foldername + "/CustomConferenceData");
			e.printStackTrace();
			return false;
		}
		while (scanner.hasNextInt()) {
			if (counter == curconf.getID()) {
				scanner.nextInt();
				scanner.nextInt();
			} else {
				contents += scanner.nextInt() + " " + scanner.nextInt() + "\n";
			}
			counter++;
		}
		scanner.close();
		FileWriter fw;
		try {
			fw = new FileWriter(data);
			fw.write(contents);
			fw.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(new JFrame(), "Error Opening " + foldername + "/CustomConferenceData");
			e.printStackTrace();
		}
		if (teams27rb.isSelected()) {
			teamlist27 = new TeamList(27);
			teams27 = teamlist27.teams;
			conferences27 = teamlist27.conferences;
		} else if (teams32rb.isSelected()) {
			teamlist32 = new TeamList(32);
			teams32 = teamlist32.teams;
			conferences32 = teamlist32.conferences;
		} else {
			teamlist130 = new TeamList(130);
			teams130 = teamlist130.teams;
			conferences130 = teamlist130.conferences;
		}
		return true;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

}
