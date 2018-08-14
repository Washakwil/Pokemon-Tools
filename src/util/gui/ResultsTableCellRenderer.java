package util.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import key.bv.BVPokemon;
import pokemon.Gender;
import pokemon.Pokemon;
import pokemon.ShinyState;
import rng.gen7.egg.Gen7EggRNGAdvance;
import rng.gen7.egg.Gen7InheritedIV;
import rng.gen7.egg.Gen7InheritedNature;

public class ResultsTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -3132327026057237904L;
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		c.setIcon(null);
		Color color = null;
		if(value instanceof ShinyState) {
			color = Color.RED;
		} else if(value instanceof Gen7EggRNGAdvance) {
			color = ((Gen7EggRNGAdvance) value).getColor();
		} else if(value instanceof Gender) {
			color = Gender.getColor((Gender) value);
		} else if(value instanceof Gen7InheritedIV) {
			color = ((Gen7InheritedIV) value).getForeground();
		} else if(value instanceof Gen7InheritedNature) {
			color = ((Gen7InheritedNature) value).getForeground();
		} else if(value instanceof BVPokemon) {
			BVPokemon pokemon = ((BVPokemon) value);
			if(pokemon.isEgg()) c.setIcon(Pokemon.EGG_ICON);
			else c.setIcon(pokemon.getSpecies().getIcon());
		}
		if(isSelected) {
			color = table.getSelectionForeground();
		}
		if(color == null) color = table.getForeground();
		c.setForeground(color);
		c.setHorizontalAlignment(JLabel.CENTER);
		if(c.getText().length() > 0) c.setToolTipText(c.getText());
		return c;
	}

}
