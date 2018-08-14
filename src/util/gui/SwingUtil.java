package util.gui;

import java.awt.Component;
import java.awt.Container;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

public class SwingUtil {

	public static void enableJPanel(JPanel panel, boolean enabled) {
		panel.setEnabled(enabled);
		Component[] components = panel.getComponents();
		for(Component component : components) {
			component.setEnabled(enabled);
		}
	}
	
	public static void addEmptyLabels(Container container, int count) {
		for(int i = 0; i < count; i++) container.add(new JLabel());
	}
	
	public static void add(Container container, Component[] components) {
		for(Component component : components) {
			container.add(component);
		}
	}
	
	public static void openFile(File defaultDirectory, JTextField textField, Component parent, FileFilter filter) {
		File file = new File(textField.getText());
		JFileChooser fileChooser = new JFileChooser(defaultDirectory);
		fileChooser.setFileFilter(filter);
		if(file.exists()) {
			fileChooser.setCurrentDirectory(file);
		}
		if(fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
			textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
		}
	}

}
