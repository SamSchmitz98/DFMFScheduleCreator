package scheduler;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
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

@SuppressWarnings("serial")
public class UserInterface extends JFrame implements ActionListener {

	JPanel schedulepanel, conferencepanel, helppanel;
	JRadioButton teams27rb, teams32rb, teams130rb;
	JSpinner nonconweeks, conweeks, byeweeks;

	public UserInterface() {
		// Create Panels
		schedulepanel = new JPanel();
		conferencepanel = new JPanel();
		helppanel = new JPanel();
		schedulepanel.setLayout(null);
		conferencepanel.setLayout(null);
		schedulepanel.setSize(500, 400);
		conferencepanel.setSize(500, 400);
		helppanel.setSize(400, 400);
		schedulepanel.setVisible(true);
		conferencepanel.setVisible(true);
		helppanel.setVisible(true);

		// Get Teams
		TeamList teamlist27 = new TeamList(27);
		TeamList teamlist32 = new TeamList(32);
		TeamList teamlist130 = new TeamList(130);
		Team[] teams27 = teamlist27.teams;
		Team[] teams32 = teamlist32.teams;
		Team[] teams130 = teamlist130.teams;
		Conference[] conferences27 = teamlist27.conferences;
		Conference[] conferences32 = teamlist32.conferences;
		Conference[] conferences130 = teamlist130.conferences;
		String[] teamStrings27 = new String[teams27.length - 1];
		String[] teamStrings32 = new String[teams32.length - 1];
		String[] teamStrings130 = new String[teams130.length - 1];
		for (int i = 0; i < teams27.length - 1; i++) {
			teamStrings27[i] = teams27[i + 1].getName();
		}
		for (int i = 0; i < teams32.length - 1; i++) {
			teamStrings32[i] = teams32[i + 1].getName();
		}
		for (int i = 0; i < teams130.length - 1; i++) {
			teamStrings130[i] = teams130[i + 1].getName();
		}

		// Schedule Panel
		// Radio Buttons
		JPanel radiobuttonpanel = new JPanel();
		radiobuttonpanel.setLayout(null);
		radiobuttonpanel.setBounds(0, 0, 102, 92);
		radiobuttonpanel.setBackground(Color.GRAY);
		teams27rb = new JRadioButton("27 Teams");
		teams32rb = new JRadioButton("32 Teams");
		teams130rb = new JRadioButton("130 Teams");
		teams27rb.setSelected(false);
		teams32rb.setSelected(false);
		teams130rb.setSelected(true);
		teams27rb.setBounds(0, 0, 100, 30);
		teams32rb.setBounds(0, 30, 100, 30);
		teams130rb.setBounds(0, 60, 100, 30);
		radiobuttonpanel.add(teams27rb);
		radiobuttonpanel.add(teams32rb);
		radiobuttonpanel.add(teams130rb);
		schedulepanel.add(radiobuttonpanel);
		ButtonGroup group = new ButtonGroup();
		group.add(teams27rb);
		group.add(teams32rb);
		group.add(teams130rb);
		// Buttons
		JButton addmatchupbutton = new JButton("Add Matchup");
		JButton remmatchupbutton = new JButton("Remove Matchup");
		JButton generatebutton = new JButton("Generate Schedule");
		addmatchupbutton.setBounds(10, 120, 110, 20);
		remmatchupbutton.setBounds(130, 120, 140, 20);
		generatebutton.setBounds(290, 115, 160, 30);
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
		nonconweeks.setBounds(155, 8, 35, 24);
		conweeks.setBounds(155, 38, 35, 24);
		byeweeks.setBounds(155, 68, 35, 24);
		schedulepanel.add(nonconweeks);
		schedulepanel.add(conweeks);
		schedulepanel.add(byeweeks);
		JLabel nonconlabel = new JLabel("Enter number of weeks of non conference play");
		JLabel conlabel = new JLabel("Enter number of weeks of conference play");
		JLabel byelabel = new JLabel("Enter number of bye weeks");
		nonconlabel.setBounds(200, 5, 270, 30);
		conlabel.setBounds(200, 35, 270, 30);
		byelabel.setBounds(200, 65, 270, 30);
		schedulepanel.add(nonconlabel);
		schedulepanel.add(conlabel);
		schedulepanel.add(byelabel);
		// Requested Matchups
		JPanel innerpanel = new JPanel();
		innerpanel.setLayout(null);
		innerpanel.setPreferredSize(new Dimension(450, 100));
		ArrayList<JComboBox<String>> awayteams = new ArrayList<JComboBox<String>>();
		ArrayList<JLabel> ats = new ArrayList<JLabel>();
		ArrayList<JComboBox<String>> hometeams = new ArrayList<JComboBox<String>>();
		ArrayList<JLabel> weeklabels = new ArrayList<JLabel>();
		ArrayList<JSpinner> weektexts = new ArrayList<JSpinner>();
		JScrollPane matchuppanel = new JScrollPane(innerpanel);
		matchuppanel.setBounds(3, 150, 472, 170);
		matchuppanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		schedulepanel.add(matchuppanel);
		revalidate();

		JTextArea helpTextArea = new JTextArea(20, 33);
		helpTextArea.setText(HelpString.helpString());
		JScrollPane helpScrollableTextArea = new JScrollPane(helpTextArea);
		helpScrollableTextArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		helppanel.add(helpScrollableTextArea);

		JTabbedPane tp = new JTabbedPane();
		tp.setBounds(0, 0, 500, 400);
		tp.add("Schedule", schedulepanel);
		tp.add("Conferences", conferencepanel);
		tp.add("Help", helppanel);
		add(tp);

		setLayout(null);
		setSize(500, 400);
		setVisible(true);

		addmatchupbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int count = awayteams.size();
				if (teams27rb.isSelected()) {
					awayteams.add(new JComboBox<String>(teamStrings27));
					hometeams.add(new JComboBox<String>(teamStrings27));
				}
				if (teams32rb.isSelected()) {
					awayteams.add(new JComboBox<String>(teamStrings32));
					hometeams.add(new JComboBox<String>(teamStrings32));
				}
				if (teams130rb.isSelected()) {
					awayteams.add(new JComboBox<String>(teamStrings130));
					hometeams.add(new JComboBox<String>(teamStrings130));
				}
				ats.add(new JLabel("at"));
				weeklabels.add(new JLabel("Week"));
				weektexts.add(new JSpinner(new SpinnerNumberModel(1, 1, 99, 1)));
				awayteams.get(count).setBounds(5, 10 + count * 40, 175, 30);
				hometeams.get(count).setBounds(200, 10 + count * 40, 175, 30);
				ats.get(count).setBounds(185, 10 + count * 40, 30, 30);
				weeklabels.get(count).setBounds(380, 10 + count * 40, 40, 30);
				weektexts.get(count).setBounds(415, 10 + count * 40, 35, 30);
				innerpanel.add(awayteams.get(count));
				innerpanel.add(hometeams.get(count));
				innerpanel.add(ats.get(count));
				innerpanel.add(weeklabels.get(count));
				innerpanel.add(weektexts.get(count));
				innerpanel.setPreferredSize(new Dimension(450, (count + 1) * 40));
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
				if (awayteams != null && awayteams.get(0).getItemCount() != teamStrings27.length) {
					for (int i = 0; i < awayteams.size(); i++) {
						awayteams.get(i).removeAllItems();
						hometeams.get(i).removeAllItems();
						for (int j = 0; j < teamStrings27.length; j++) {
							awayteams.get(i).addItem(teamStrings27[j]);
							hometeams.get(i).addItem(teamStrings27[j]);
						}
					}
					innerpanel.revalidate();
					matchuppanel.repaint();
					revalidate();
				}
			}
		});

		teams32rb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (awayteams != null && awayteams.get(0).getItemCount() != teamStrings32.length) {
					for (int i = 0; i < awayteams.size(); i++) {
						awayteams.get(i).removeAllItems();
						hometeams.get(i).removeAllItems();
						for (int j = 0; j < teamStrings32.length; j++) {
							awayteams.get(i).addItem(teamStrings32[j]);
							hometeams.get(i).addItem(teamStrings32[j]);
						}
					}
					innerpanel.revalidate();
					matchuppanel.repaint();
					revalidate();
				}
			}
		});

		teams130rb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (awayteams != null && awayteams.get(0).getItemCount() != teamStrings130.length) {
					for (int i = 0; i < awayteams.size(); i++) {
						awayteams.get(i).removeAllItems();
						hometeams.get(i).removeAllItems();
						for (int j = 0; j < teamStrings130.length; j++) {
							awayteams.get(i).addItem(teamStrings130[j]);
							hometeams.get(i).addItem(teamStrings130[j]);
						}
					}
					innerpanel.revalidate();
					matchuppanel.repaint();
					revalidate();
				}
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
				}
				if (teams32rb.isSelected()) {
					teams = teams32;
					conferences = conferences32;
					teamamount = 32;
				} else {
					teams = teams130;
					conferences = conferences130;
					teamamount = 130;
				}
				for (int i = 0; i < awayteams.size(); i++) {
					Team away = new Team(0, "BYE", 0);
					Team home = new Team(0, "BYE", 0);
					for (int j = 1; j < teams.length; j++) {
						if (teams[j].getName().equals(awayteams.get(i).getSelectedItem())) {
							away = teams[j];
						}
						if (teams[j].getName().equals(hometeams.get(i).getSelectedItem())) {
							home = teams[j];
						}
						if (home == away) {
							JOptionPane.showMessageDialog(new JFrame(),
									"Teams cannot be the same for matchup " + (i + 1));
							return;
						}
					}
					try {
						matchups.add(
								new Matchup(Integer.parseInt(weektexts.get(i).getValue().toString()) - 1, home, away));
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
								JOptionPane.showMessageDialog(new JFrame(),
										"Error renaming file " + file.getName());
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
					JOptionPane.showMessageDialog(new JFrame(), "Schedule Created");
				}
			}
		});
	}

	public static void main(String[] args) {
		new UserInterface();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

}
