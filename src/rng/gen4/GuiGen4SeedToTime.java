package rng.gen4;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import util.Date;
import util.Time;
import util.gui.ResultsTable;
import util.gui.UnsignedIntTextField;

public class GuiGen4SeedToTime extends JPanel implements ActionListener{
	
	private static final long serialVersionUID = -3507324988361572631L;
	private UnsignedIntTextField yearInput, seedInput;
	private JLabel lYear, lseed;
	private JButton generate;
	private ResultsTable results;
	private Gen4SeedToTime seedToTime;

	public GuiGen4SeedToTime(int seed, int year) {
		super();
		
		yearInput = new UnsignedIntTextField(2000, 2099, year);
		seedInput = new UnsignedIntTextField(0, 0xFFFFFFFF, seed, 8);
		
		lYear = new JLabel("Year:", JLabel.RIGHT);
		lseed = new JLabel("Seed (Hex):", JLabel.RIGHT);
		
		generate = new JButton("Generate");
		generate.addActionListener(this);
		
		results = new ResultsTable(new Object[] {"Date", "Time", "Delay"});
		
		setLayout(new BorderLayout(10, 10));
		JPanel input = new JPanel(new GridLayout(3, 2, 10, 10));
		
		input.add(lseed);
		input.add(seedInput);
		input.add(lYear);
		input.add(yearInput);
		input.add(new JLabel());
		input.add(generate);
		
		add(input, BorderLayout.NORTH);
		add(new JScrollPane(results), BorderLayout.CENTER);
		
		generate();
	}
	
	public GuiGen4SeedToTime() {
		this(0, Calendar.getInstance().get(Calendar.YEAR));
	}

	public void generate() {
		resetTable();
		int seed = seedInput.getHexValue();
		int year = yearInput.getValue();
		seedToTime = new Gen4SeedToTime(this, seed, year);
		seedToTime.generate();
	}

	public void addToTable(int delay, int second, int minute, int hour, int day, int month, int year) {
		results.getModel().addRow(new Object[] {
				new Date(year, month, day),
				new Time(hour, minute, second),
				delay
		});
	}
	
	public void resetTable() {
		results.reset();
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource() == generate) generate();
	}

	public void setInput(int seed, int year) {
		this.seedInput.setHexValue(seed);
		this.yearInput.setValue(year);
	}
	
}
