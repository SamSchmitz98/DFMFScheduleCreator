package scheduler;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;

public class Scheduler extends Frame implements ActionListener {

	public static void main(String[] args) {
		Scheduler app = new Scheduler();
	}

	public Scheduler() {
		ArrayList<JComboBox<String>> awayteams = new ArrayList<JComboBox<String>>();
		ArrayList<Label> ats = new ArrayList<Label>();
		ArrayList<JComboBox<String>> hometeams = new ArrayList<JComboBox<String>>();
		ArrayList<Label> weeklabels = new ArrayList<Label>();
		ArrayList<TextField> weektexts = new ArrayList<TextField>();
		Container container = new Container();
		container.setLayout(new FlowLayout());

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent evt) {
				System.exit(0); // Terminate the program
			}
		});
		final JRadioButton teams27 = new JRadioButton("27 Teams");
		teams27.setActionCommand("27 Teams");
		teams27.setSelected(false);
		teams27.setBounds(30, 60, 150, 30);
		container.add(teams27);
		final JRadioButton teams32 = new JRadioButton("32 Teams");
		teams32.setActionCommand("32 Teams");
		teams32.setSelected(false);
		teams32.setBounds(30, 90, 150, 30);
		container.add(teams32);
		JRadioButton teams130 = new JRadioButton("130 Teams");
		teams130.setActionCommand("130 Teams");
		teams130.setSelected(true);
		teams130.setBounds(30, 120, 150, 30);
		container.add(teams130);
		ButtonGroup group = new ButtonGroup();
		group.add(teams27);
		group.add(teams32);
		group.add(teams130);
		container.add(new Label("Enter number of weeks of conference play"));
		final TextField confweekamount = new TextField("9");
		confweekamount.setEditable(true);
		container.add(confweekamount);
		container.add(new Label("Enter number of weeks of conference play"));
		final TextField nonconfweekamount = new TextField("3");
		nonconfweekamount.setEditable(true);
		container.add(nonconfweekamount);
		container.add(new Label("                 Enter season number"));
		final TextField season = new TextField("1");
		season.setEditable(true);
		container.add(season);
		container.add(new Label("                                               "));
		container.add(new Label("                "));
		Button m = new Button("Add Matchup");
		m.setBounds(30, 250, 80, 30);// setting button position
		container.add(m);
		Button r = new Button("Remove Matchup");
		r.setBounds(30, 250, 80, 30);// setting button position
		container.add(r);
		Button b = new Button("Generate");
		b.setBounds(30, 250, 80, 30);// setting button position
		container.add(b);// adding button into frame
		container.add(new Label("         "));
		add(container);
		setSize(450, 210);// frame size
		Scrollbar scrollbar = new Scrollbar(Scrollbar.VERTICAL, 0, 210, 0, 210);
		add(scrollbar, BorderLayout.EAST);
		int scrolllastval = 0;
		setTitle("Season Generator");
		setVisible(true);

		scrollbar.addAdjustmentListener(new AdjustmentListener() {

			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				container.setLocation(container.getLocation().x, -1*e.getValue());
				
			}
			
		});
		
		m.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int teamamount = 130;
				if (teams27.isSelected()) {
					teamamount = 27;
				}
				if (teams32.isSelected()) {
					teamamount = 32;
				}
				TeamList teamlist = new TeamList(teamamount);
				Team[] teams = teamlist.teams;
				Conference[] conferences = teamlist.conferences;
				String[] teamStrings = new String[teams.length - 1];
				for (int i = 0; i < teams.length - 1; i++) {
					teamStrings[i] = teams[i + 1].getName();
				}
				awayteams.add(new JComboBox<String>(teamStrings));
				ats.add(new Label(" at"));
				hometeams.add(new JComboBox<String>(teamStrings));
				weeklabels.add(new Label("                                                                        Week: "));
				weektexts.add(new TextField("1"));
				container.add(awayteams.get(awayteams.size() - 1));
				container.add(ats.get(ats.size() - 1));
				container.add(hometeams.get(hometeams.size() - 1));
				container.add(weeklabels.get(weeklabels.size()-1));
				container.add(weektexts.get(weektexts.size()-1));
				scrollbar.setMaximum(scrollbar.getMaximum()+60);
				container.setSize(450, container.getSize().height + 60);
					setSize(450, Scheduler.super.getSize().height + 60);
				
				setVisible(true);
			}
		});

		r.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!awayteams.isEmpty()) {
					container.remove(awayteams.get(awayteams.size() - 1));
					container.remove(ats.get(ats.size() - 1));
					container.remove(hometeams.get(hometeams.size() - 1));
					container.remove(weeklabels.get(weeklabels.size()-1));
					container.remove(weektexts.get(weektexts.size()-1));
						setSize(450, Scheduler.super.getSize().height - 60);
					
					Scheduler.super.repaint();
					awayteams.remove(awayteams.size() - 1);
					ats.remove(ats.size() - 1);
					hometeams.remove(hometeams.size() - 1);
					weeklabels.remove(weeklabels.size() - 1);
					weektexts.remove(weektexts.size() - 1);
				}
			}
		});

		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int teamamount = 130;
				if (teams27.isSelected()) {
					teamamount = 27;
				}
				if (teams32.isSelected()) {
					teamamount = 32;
				}
				TeamList teamlist = new TeamList(teamamount);
				Team[] teams = teamlist.teams;
				Conference[] conferences = teamlist.conferences;
				ArrayList<Matchup> matchups = new ArrayList<Matchup>();
				for (int i = 0; i < awayteams.size(); i++) {
					Team away = new Team(0, "BYE", 0);
					Team home = new Team(0, "BYE", 0);
					for (int j = 1; j < teams.length; j++) {
						if (teams[j].getName().equals(awayteams.get(i).getSelectedItem())){
							away = teams[j];
						}
						if (teams[j].getName().equals(hometeams.get(i).getSelectedItem())){
							home = teams[j];
						}
					}
					matchups.add(new Matchup(Integer.parseInt(weektexts.get(i).getText())-1, away, home));
				}
				SeasonMaker sm = new SeasonMaker(teams, conferences, Integer.parseInt(confweekamount.getText()),
						Integer.parseInt(nonconfweekamount.getText()), matchups);

				sm.generateRegularSeason();
				File f = new File("Schedules" + teamamount + "\\schedule_template_"
						+ (Integer.parseInt(season.getText()) - 1) + ".csv");
				try {
					f.createNewFile();
					FileWriter writer = new FileWriter(f);
					writer.write(sm.getSeason());
					writer.close();
				} catch (IOException exc) {

				}
			}
		});
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}
}
