package main;

import javax.swing.JFrame;
import javax.swing.UIManager;

import key.bv.GuiBVBreaker;

public class Main {

	public static void main(String[] args) {
		
		GuiBVBreaker.DEFAULT_BV_DIRECTORY.mkdirs();
		GuiBVBreaker.DEFAULT_KEY_DIRECTORY.mkdirs();
		
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
			JFrame.setDefaultLookAndFeelDecorated(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		new GuiMainMenu();
	}

}
