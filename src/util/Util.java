package util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Util {

	public static String add0s(int i, int digits) {
		return add0s(Integer.toUnsignedString(i), digits);
	}
	
	public static String add0s(String s, int digits) {
		StringBuilder sb = new StringBuilder(s);
		while(sb.length() < digits) sb.insert(0, 0);
		return sb.toString();
	}
	
	public static String capitalize(String s) {
		if(s == null || s.length() == 0) return s;
		int index;
		StringBuilder sb = new StringBuilder(s.length());
		s = s.replace('_', ' ');
		while((index = s.indexOf(' ') + 1) > 0) {
			sb.append(s.substring(0, 1).toUpperCase()).append(s.substring(1, index).toLowerCase());
			s = s.substring(index);
		}
		sb.append(s.substring(0, 1).toUpperCase()).append(s.substring(1).toLowerCase());
		return sb.toString();
	}

	public static String capitalizeFirst(String s) {
		if(s == null || s.length() == 0) return s;
		int index;
		int beginIndex = 1;
		StringBuilder sb = new StringBuilder(s.length());
		s = s.replace('_', ' ');
		sb.append(s.charAt(0));
		while((index = s.indexOf(' ') + 1) > 0) {
			sb.append(s.substring(beginIndex, index).toLowerCase());
			s = s.substring(index);
			beginIndex = 0;
		}
		sb.append(s.substring(beginIndex).toLowerCase());
		return sb.toString();
	}
	
	public static String addIfNotExisting(String text, String s) {
		if(text.indexOf(s) > -1) return text;
		if(text.length() > 0) {
			text += ", ";
		}
		return text + s;
	}
	
	public static short toShort(byte[] data, int offset) {
		short result = 0;
		for(int i = 0; i < Short.BYTES; i++) {
			try {
				result |= Byte.toUnsignedInt(data[offset + i]) << (i << 3);
			} catch(ArrayIndexOutOfBoundsException e) {break;}
		}
		return result ;
	}
	
	public static int toInt(byte[] data, int offset) {
		int result = 0;
		for(int i = 0; i < Integer.BYTES; i++) {
			try {
				result |= Byte.toUnsignedInt(data[offset + i]) << (i << 3);
			} catch(ArrayIndexOutOfBoundsException e) {break;}
		}
		return result;
	}
	
	public static void copy(byte[] source, byte[] destination, int length) {
		copy(source, 0, destination, 0, length);
	}
	
	public static void copy(byte[] source, int sourceOffset, byte[] destination, int destinationOffset, int length) {
		for(int i = 0; i < length; i++) {
			destination[destinationOffset + i] = source[sourceOffset + i];
		}
	}

	public static byte[] toBytes(short s) {
		byte[] bytes = new byte[Short.BYTES];
		for(int i = 0; i < Short.BYTES; i++) {
			bytes[i] = (byte) (s >>> (i << 3));
		}
		return bytes;
	}

	public static byte[] toBytes(int s) {
		byte[] bytes = new byte[Integer.BYTES];
		for(int i = 0; i < Integer.BYTES; i++) {
			bytes[i] = (byte) (s >>> (i << 3));
		}
		return bytes;
	}
	
	public static void writeString(RandomAccessFile file, String s) throws IOException {
		file.writeChars(s);
		file.writeChar('\n');
	}
	
	public static String readString(RandomAccessFile file) throws IOException {
		StringBuilder sb = new StringBuilder();
		char c = file.readChar();
		while(c != '\n') {
			sb.append(c);
			c = file.readChar();
		}
		return sb.toString();
	}

	public static boolean sequenceEqual(byte[] s1, byte[] s2) {
		int length = s1.length;
		if(length != s2.length) return false;
		return sequenceEqual(s1, 0, s2, 0, length);
	}

	public static boolean sequenceEqual(byte[] s1, int off1, byte[] s2, int off2, int length) {
		for(int i = 0; i < length; i++) {
			if(s1[off1 + i] != s2[off2 + i]) return false;
		}
		return true;
	}
	
	public static File forceFileNameExtension(File file, String extension) {
		String filename = file.getName();
		int index = filename.indexOf('.') + 1;
		if(index > 0) {
			filename = filename.substring(0, index);
		} else filename = filename + ".";
		filename += extension;
		file = new File(file.getParent(), filename);
		return file;
	}
}
