package util.gui;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

import util.Util;

public class UnsignedIntTextField extends JTextField implements FocusListener {
	
	private static final long serialVersionUID = -4481657480324168621L;
	private int min, max, standard, digits;
	
	public UnsignedIntTextField() {
		this(null);
	}
	
	public UnsignedIntTextField(String text) {
		this(text, 0, 0, 0);
	}
	
	public UnsignedIntTextField(int min, int max, int standard) {
		this(null, min, max, standard);
	}
	
	public UnsignedIntTextField(String text, int min, int max, int standard) {
		this(text, min, max, standard, 0);
	}
	
	public UnsignedIntTextField(int min, int max, int standard, int digits) {
		this(null, min, max, standard, digits);
	}
	
	public UnsignedIntTextField(String text, int min, int max, int standard, int digits) {
		if(text == null) text = Integer.toUnsignedString(standard);
		if(digits > 0) text = Util.add0s(text, digits);
		setText(text);
		addFocusListener(this);
		this.min = min;
		this.max = max;
		this.standard = standard;
		this.digits = digits;
	}

	@Override
	public void focusGained(FocusEvent fe) {
		selectAll();
	}

	@Override
	public void focusLost(FocusEvent fe) {
		select(0, 0);
	}
	
	public int getValue() {
		int value;
		try {
			value = Integer.parseUnsignedInt(getText());
		} catch(NumberFormatException e) {
			value = standard;
		}
		return setValue(value);
	}

	public int setValue(int value) {
		value = checkValue(value);
		String text = Integer.toUnsignedString(value);
		if(digits > 0) text = Util.add0s(text , digits);
		setText(text);
		return value;
	}
	
	public int getHexValue() {
		int value;
		try {
			value = Integer.parseUnsignedInt(getText(), 16);
		} catch(NumberFormatException e) {
			value = standard;
		}
		return setHexValue(value);
	}

	public int setHexValue(int value) {
		value = checkValue(value);
		String text = Integer.toHexString(value);
		if(digits > 0) text = Util.add0s(text , digits);
		setText(text);
		return value;
	}
	
	private int checkValue(int value) {
		if(Integer.compareUnsigned(value, max) > 0) return max;
		if(Integer.compareUnsigned(value, min) < 0) return min;
		return value;
	}
	
	public void setMin(int min) {
		this.min = min;
	}
	
	public void setMax(int max) {
		this.max = max;
	}
	
	public void setStandard(int standard) {
		this.standard = standard;
		if(Integer.compareUnsigned(standard, max) > 0) standard = max;
	}
	
	public void setDigits(int digits) {
		this.digits = digits;
	}

	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}

	public int getStandard() {
		return standard;
	}

	public int getDigits() {
		return digits;
	}

}
