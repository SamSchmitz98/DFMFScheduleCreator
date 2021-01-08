package scheduler;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

public class Scheduler extends Frame implements ActionListener{
	
	public static void main(String[] args) {
		Scheduler app = new Scheduler();
	}
		
	public Scheduler() {
		setLayout(new FlowLayout());

		addWindowListener(new WindowAdapter() {
	         @Override
	         public void windowClosing(WindowEvent evt) {
	            System.exit(0);  // Terminate the program
	         }
		});
		final JRadioButton teams27 = new JRadioButton("27 Teams");
	    teams27.setActionCommand("27 Teams");
	    teams27.setSelected(true);
	    teams27.setBounds(30, 60, 150, 30);
	    add(teams27);
		final JRadioButton teams32 = new JRadioButton("32 Teams");
	    teams32.setActionCommand("32 Teams");
	    teams32.setSelected(false);
	    teams32.setBounds(30, 90, 150, 30);
	    add(teams32);
		JRadioButton teams130 = new JRadioButton("130 Teams");
	    teams130.setActionCommand("130 Teams");
	    teams130.setSelected(false);
	    teams130.setBounds(30, 120, 150, 30);
	    add(teams130);
	    ButtonGroup group = new ButtonGroup();
	    group.add(teams27);
	    group.add(teams32);
	    group.add(teams130);
	    add(new Label("Enter number of weeks"));
	    final TextField weekamount = new TextField("12");
	    weekamount.setEditable(true);
	    add(weekamount);
	    add(new Label("Enter season number"));
	    final TextField season = new TextField("1");
	    season.setEditable(true);
	    add(season);
	    add(new Label("                  "));
		Button b = new Button("Generate");  
		b.setBounds(30,250,80,30);// setting button position  
		add(b);//adding button into frame  
		setSize(300,180);//frame size 300 width and 180 height  
		setTitle("Season Generator");  
		setVisible(true);
		
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
				SeasonMaker sm = new SeasonMaker(Integer.parseInt(weekamount.getText()));
				Team[] teams = teamlist.teams;
				Conference[] conferences = teamlist.conferences;

				sm.generateConferenceOnlySeason(teams, conferences);
				File f = new File("Schedules" + teamamount + "\\schedule_template_" + (Integer.parseInt(season.getText())-1) + ".csv");
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