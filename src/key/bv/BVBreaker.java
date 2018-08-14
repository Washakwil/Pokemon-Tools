package key.bv;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import pokemon.Game;
import util.Util;

import static key.bv.BVPokemon.POKEMON_SIZE;
import static key.bv.BVPokemon.TEAM_SIZE;
import static key.bv.BVPokemon.ENCRYPTED_ZEROS;;

public class BVBreaker {
	
	private static final int[] PARTY_OFFSETS = new int[] {0x4E18, 0x4E41};
	private static final int[] OPPONENT_PARTY_OFFSETS = new int[] {0x5438, 0x545e};
	private static final int[] VIDEO_SIZE = new int[] {0x6E60, 0x6BC0};
	private int partyOffset;
	private int opponentPartyOffset;

	private byte[] video1;
	private byte[] video2;
	
	private byte[] key;
	private byte[] opponentKey;
	
	public static BVBreaker createBVBreaker(String v1Path, String v2Path, Game game) {
		try {
			BVBreaker bvBreaker = new BVBreaker();
			if(!bvBreaker.loadOffsets(game)) return null;
			int videosize = getVideoSize(game);
			
			RandomAccessFile file1 = new RandomAccessFile(v1Path, "r");
			int size1 = (int) file1.length();
			if(size1 != videosize) {
				file1.close();
				return null;
			}
			bvBreaker.video1 = new byte[size1];
			file1.read(bvBreaker.video1);
			file1.close();
			
			RandomAccessFile file2 = new RandomAccessFile(v2Path, "r");
			int size2 = (int) file2.length();
			if(size2 != videosize) {
				file2.close();
				return null;
			}
			bvBreaker.video2 = new byte[size2];
			file2.read(bvBreaker.video2);
			file2.close();
			
			bvBreaker.breakBV();
			
			return bvBreaker;
			
		} catch(IOException e) {
			return null;
		}
	}
	
	public static BVBreaker createBVBreakerWithKey(String keyPath, String vPath, Game game) {
		try {
			BVBreaker bvBreaker = new BVBreaker();
			if(!bvBreaker.loadOffsets(game)) return null;
			int videosize = getVideoSize(game);
			
			RandomAccessFile file1 = new RandomAccessFile(keyPath, "r");
			int size1 = (int) file1.length();
			if(size1 != TEAM_SIZE * 2) {
				file1.close();
				return null;
			}
			bvBreaker.key = new byte[TEAM_SIZE];
			file1.read(bvBreaker.key);
			bvBreaker.opponentKey = new byte[TEAM_SIZE];
			file1.read(bvBreaker.opponentKey);
			file1.close();
			
			RandomAccessFile file2 = new RandomAccessFile(vPath, "r");
			int size2 = (int) file2.length();
			if(size2 != videosize) {
				file2.close();
				return null;
			}
			bvBreaker.video2 = new byte[size2];
			file2.read(bvBreaker.video2);
			file2.close();
			
			return bvBreaker;
		} catch(IOException e) {
			return null;
		}
	}
	
	private boolean loadOffsets(Game game) {
		int gen = game.getGeneration();
		if(gen  != 6 && gen != 7) return false;
		gen -= 6;
		partyOffset = PARTY_OFFSETS[gen];
		opponentPartyOffset = OPPONENT_PARTY_OFFSETS[gen];
		return true;
	}
	
	private static int getVideoSize(Game game) {
		return VIDEO_SIZE[game.getGeneration() - 6];
	}
	
	public void save(File file) {
		try {
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			raf.write(key);
			raf.write(opponentKey);
			raf.close();
		} catch(IOException e) {}
	}

	private void breakBV() {
		
		breakBV(false);
		breakBV(true);
		
	}

	private void breakBV(boolean opponent) {
		
		int offset = opponent ? opponentPartyOffset : partyOffset;
		byte[] key = new byte[TEAM_SIZE];
		Util.copy(video1, offset, key, 0, TEAM_SIZE);
		
		for(int i = POKEMON_SIZE; i < TEAM_SIZE; i++) {
			key[i] ^= ENCRYPTED_ZEROS[i % POKEMON_SIZE];
		}

		for(int i = 0; i < POKEMON_SIZE; i++) {
			key[i] ^= (byte) (key[i + POKEMON_SIZE] ^ video2[i + offset + POKEMON_SIZE]);
		}
		if(opponent) opponentKey = key;
		else this.key = key;
	}
	
	public BVPokemon getBVPokemon(int slot, boolean opponent) {
		int slotoff = slot * POKEMON_SIZE;
		int offset = (opponent ? opponentPartyOffset : partyOffset) + slotoff;
		
		byte[] key = opponent ? opponentKey : this.key;
		byte[] ekx = new byte[POKEMON_SIZE];
		Util.copy(video2, offset, ekx, 0, POKEMON_SIZE);
		
		for(int i = 0; i < POKEMON_SIZE; i++) {
			ekx[i] ^= key[i + slotoff];
		}

		BVPokemon pkx = new BVPokemon(ekx).decrypt();

		if(pkx.isCorrupted())
		{
			return null;
		}
		return pkx;
	}
	
	public static FileFilter getKeyFileFilter() {
		return new FileNameExtensionFilter("Binary Files (*.bin)", "bin");
	}
	
}
