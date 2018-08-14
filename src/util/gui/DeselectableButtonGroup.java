package util.gui;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;

public class DeselectableButtonGroup extends ButtonGroup {

	private static final long serialVersionUID = -7591390977754152344L;

	public DeselectableButtonGroup(AbstractButton... buttons) {
		super();
		for(AbstractButton button : buttons) {
			add(button);
		}
	}
	
	@Override
	public void setSelected(ButtonModel m, boolean b) {
		if (b && m != null && m != getSelection()) {
			super.setSelected(m, b);
		} else if (!b && m == getSelection()) {
			clearSelection();
		}
	}
	
}
