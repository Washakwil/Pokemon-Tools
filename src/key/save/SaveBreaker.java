package key.save;

import java.io.IOException;
import java.io.RandomAccessFile;

import key.bv.BVPokemon;
import pokemon.Game;
import util.Util;

public class SaveBreaker {

	private static final String[] EGG_NAMES = new String[] {"タマゴ", "Egg", "Œuf", "Uovo", "Ei", "", "Huevo", "알", "蛋", "蛋"};
	byte[] save1;
	byte[] save2;
	byte[] key;
	byte[] blank;
	byte[] boxKey1;
	byte[] boxKey2;
	private int boxCount;
	private int saveSize;
	private int base1;
	private int base2;
	private int offset;
	
	public static SaveBreaker createBVBreaker(String s1Path, String s2Path, Game game) {
		try {
			SaveBreaker saveBreaker = new SaveBreaker();
			int fileSize;
			int gen = game.getGeneration();
			if(gen != 6 && gen != 7) return null;
			switch(game) {
			case X:
			case Y:
				saveBreaker.offset = 0x26A00;
				break;
			case OMEGA_RUBY:
			case ALPHA_SAPHIR:
				saveBreaker.offset = 0x37400;
				break;
			case SUN:
			case MOON:
				saveBreaker.offset = 0x8200;
				break;
			case ULTRA_SUN:
			case ULTRA_MOON:
				saveBreaker.offset = 0x8600;
				break;
			
			}
			if(gen == 6) {
				fileSize = 0x100000;
				saveBreaker.boxCount = 31;
				saveBreaker.saveSize = 0x07F000;
				saveBreaker.base1 = 0x1000;
			} else {
				fileSize = 0x0FE000;
				saveBreaker.boxCount = 32;
				saveBreaker.saveSize = 0x07E000;
				saveBreaker.base1 = 0x2000;
			}
			saveBreaker.base2 = 0x080000;
			
			RandomAccessFile file1 = new RandomAccessFile(s1Path, "r");
			int offset1 = getOffset(file1, fileSize);
			if(offset1 == -1) {
				file1.close();
				return null;
			}
			saveBreaker.save1 = new byte[fileSize];
			file1.seek(offset1);
			file1.read(saveBreaker.save1);
			file1.close();
			
			RandomAccessFile file2 = new RandomAccessFile(s2Path, "r");
			int offset2 = getOffset(file1, fileSize);
			if(offset2 == -1) {
				file2.close();
				return null;
			}
			saveBreaker.save2 = new byte[fileSize];
			file1.seek(offset2);
			file2.read(saveBreaker.save2);
			file2.close();
			
			if(Util.sequenceEqual(saveBreaker.save1, saveBreaker.save2)) return null;
			if(Util.sequenceEqual(saveBreaker.save1, 16, saveBreaker.save2, 16, 8)) return null;
			
			saveBreaker.breakSave();
			
			return saveBreaker;
			
		} catch(IOException e) {
			return null;
		}
	}

	private static int getOffset(RandomAccessFile file, int fileSize) throws IOException {
		int length = (int) file.length();
		if(length == fileSize) return 0;
		if(length == fileSize + 0x9C) return 0x9C;
		if(length == fileSize + 0x19A) return 0x19A;
		return 0;
	}

	private void breakSave() {
		
		int boxSize = BVPokemon.STORED_SIZE * 30 * boxCount;
		byte[] emptyPkx = new byte[BVPokemon.STORED_SIZE];
		byte[] boxes1 = new byte[saveSize];
		byte[] boxes2 = new byte[saveSize];
		
		for(int i = 0; i < saveSize; i++) {
			boxes1[i] = save1[base2 + i];
			boxes2[i] = save2[base2 + i];
		}
		
		if(Util.sequenceEqual(save1, base2, save2, base2, saveSize)) {
			for(int i = 0; i < saveSize; i++) {
				boxes2[i] = (byte) (save1[base1 + i] ^ save1[base2 + i] ^ save2[base1 + i]);
			}
		}
		
		{
			byte[] b1 = new byte[boxSize];
			byte[] b2 = new byte[boxSize];
			for(int i = 0; i < boxSize; i++) {
				b1[i] = boxes1[offset + i];
				b2[i] = boxes2[offset + i];
			}
			boxes1 = b1;
			boxes2 = b2;
		}
		
		boolean valid = false;
		byte[] incompleteEkx = new byte[BVPokemon.STORED_SIZE];
		for(int off = 0; off <= BVPokemon.STORED_SIZE * 6; off += BVPokemon.STORED_SIZE) {
			for(int i = 0; i < BVPokemon.STORED_SIZE; i++) {
				incompleteEkx[i] = (byte) (boxes1[off + i] ^ boxes2[off + i] ^ BVPokemon.ENCRYPTED_ZEROS[i]);
			}
			int ec = Util.toInt(incompleteEkx, 0);
			if(BVPokemon.getDBlock(ec) != 3) {
				byte[] incompletePkx = BVPokemon.decryptArray(incompleteEkx);
				int lang = incompletePkx[0xE3] - 1;
				if(lang < EGG_NAMES.length) {
					valid = true;
					String nickname = EGG_NAMES[lang];
					byte[] nicknameBytes = new byte[nickname.length() << 1];
					for(int i = 0; i < nicknameBytes.length; i++) {
						char c = nickname.charAt(i >>> 1);
						byte[] bytes = Util.toBytes((short) c);
						nicknameBytes[i] = bytes[i & 1];
					}
					Util.copy(nicknameBytes, 0, emptyPkx, 0x40, nicknameBytes.length);
					Util.copy(incompletePkx, 0xE0, emptyPkx, 0xE0, 4);
					break;
				}
			}
		}
		
		if(!valid) {
			//Other Pokemon
			return;
		}
		
		blank = BVPokemon.encryptArray(emptyPkx);
		
	}
	
}
