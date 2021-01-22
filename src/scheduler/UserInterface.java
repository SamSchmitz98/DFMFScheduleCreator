package scheduler;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
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
	JRadioButton teams27, teams32, teams130;
	JSpinner nonconweeks, conweeks, byeweeks;

	public UserInterface() {
		//Create Panels
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

		//Schedule Panel
			//Radio Buttons
		JPanel radiobuttonpanel = new JPanel();
		radiobuttonpanel.setLayout(null);
		radiobuttonpanel.setBounds(0, 0, 102, 92);
		radiobuttonpanel.setBackground(Color.GRAY);
		teams27 = new JRadioButton("27 Teams");
		teams32 = new JRadioButton("32 Teams");
		teams130 = new JRadioButton("130 Teams");
		teams27.setSelected(false);
		teams32.setSelected(false);
		teams130.setSelected(true);
		teams27.setBounds(0, 0, 100, 30);
		teams32.setBounds(0, 30, 100, 30);
		teams130.setBounds(0, 60, 100, 30);
		radiobuttonpanel.add(teams27);
		radiobuttonpanel.add(teams32);
		radiobuttonpanel.add(teams130);
		schedulepanel.add(radiobuttonpanel);
		ButtonGroup group = new ButtonGroup();
		group.add(teams27);
		group.add(teams32);
		group.add(teams130);
			//Week Text Fields
		SpinnerModel spinnermodelnon = new SpinnerNumberModel(3, 0, 15, 1);
		SpinnerModel spinnermodelcon = new SpinnerNumberModel(9, 0, 15, 1);
		SpinnerModel spinnermodelbye = new SpinnerNumberModel(3, 0, 6, 1);
		nonconweeks = new JSpinner(spinnermodelnon);
		conweeks = new JSpinner(spinnermodelcon);
		byeweeks = new JSpinner(spinnermodelbye);
		nonconweeks.setBounds(155, 3, 35, 24);
		conweeks.setBounds(155, 33, 35, 24);
		byeweeks.setBounds(155, 63, 35, 24);
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
			//Requested Matchups
		JPanel innerpanel = new JPanel();
		JScrollPane matchuppanel = new JScrollPane(innerpanel);
		matchuppanel.setBounds(3, 150, 480, 180);
		matchuppanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		matchuppanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		ArrayList<JComboBox<String>> awayteams = new ArrayList<JComboBox<String>>();
		ArrayList<Label> ats = new ArrayList<Label>();
		ArrayList<JComboBox<String>> hometeams = new ArrayList<JComboBox<String>>();
		ArrayList<Label> weeklabels = new ArrayList<Label>();
		ArrayList<TextField> weektexts = new ArrayList<TextField>();
		TeamList teamlist = new TeamList(130);
		Team[] teams = teamlist.teams;
		String[] teamStrings = new String[teams.length - 1];
		for (int i = 0; i < teams.length - 1; i++) {
			teamStrings[i] = teams[i + 1].getName();
		}
		awayteams.add(new JComboBox<String>(teamStrings));
		awayteams.add(new JComboBox<String>(teamStrings));
		awayteams.add(new JComboBox<String>(teamStrings));
		awayteams.add(new JComboBox<String>(teamStrings));
		awayteams.add(new JComboBox<String>(teamStrings));
		innerpanel.add(awayteams.get(0));
		innerpanel.add(awayteams.get(1));
		innerpanel.add(awayteams.get(2));
		innerpanel.add(awayteams.get(3));
		innerpanel.add(awayteams.get(4));
		awayteams.get(3).setBounds(50, 10, WIDTH, HEIGHT);
		schedulepanel.add(matchuppanel);
		getContentPane().add(matchuppanel, BorderLayout.SOUTH);
		
		
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
	}

	public void actionPerformed(ActionEvent e) {

	}

	public static void main(String[] args) {
		new UserInterface();
	}

}
