package key.bv;

import java.util.Arrays;

import pokemon.Pokemon;
import pokemon.ShinyState;
import rng.LCRNG;
import rng.PokeRNG;
import util.Util;

public class BVPokemon {

	public static final int POKEMON_SIZE = 0x104;
	public static final int STORED_SIZE = 0xE8;
	public static final int TEAM_SIZE = POKEMON_SIZE * 6;
	private static final byte[][] BLOCK_POSITION = new byte[][] {
		{0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 2, 3, 1, 1, 2, 3, 2, 3, 1, 1, 2, 3, 2, 3},
		{1, 1, 2, 3, 2, 3, 0, 0, 0, 0, 0, 0, 2, 3, 1, 1, 3, 2, 2, 3, 1, 1, 3, 2},
		{2, 3, 1, 1, 3, 2, 2, 3, 1, 1, 3, 2, 0, 0, 0, 0, 0, 0, 3, 2, 3, 2, 1, 1},
		{3, 2, 3, 2, 1, 1, 3, 2, 3, 2, 1, 1, 3, 2, 3, 2, 1, 1, 0, 0, 0, 0, 0, 0},
	};
	private static final byte[] BLOCK_POSITION_INVERT = new byte[] {0, 1, 2, 4, 3, 5, 6, 7, 12, 18, 13, 19, 8, 10, 14, 20, 16, 22, 9, 11, 15, 21, 17, 23};
	public static final byte[] ENCRYPTED_ZEROS = encryptArray(new byte[POKEMON_SIZE]);
	
	public byte[] data;

	public BVPokemon(byte[] pkx)
	{
		data = Arrays.copyOf(pkx, pkx.length);
	}

	public short getCheckSum() {
		return Util.toShort(data, 0x6);
	}

	public Pokemon getSpecies() {
		return Pokemon.getPokemon(Short.toUnsignedInt(Util.toShort(data, 0x8)), getForm());
	}
	
	public int getForm() {
		return data[0x1D] >>> 3;
	}

	public String getG7TID() {
		int g7TID = getSID() << 16 | getTID();
		String str = Integer.toUnsignedString(g7TID);
		if(str.length() > 6) str = str.substring(str.length() - 6);
		return str;
	}

	public int getTID() {
		return Short.toUnsignedInt(Util.toShort(data, 0xC));
	}

	public int getSID() {
		return Short.toUnsignedInt(Util.toShort(data, 0xE));
	}

	public int getTSV() {
		return (getTID() ^ getSID()) >>> 4;
	}

	public int getPID() {
		return Util.toInt(data, 0x18);
	}

	public int getPSV() {
		int pid = getPID();
		return (pid >>> 16 ^ pid & 0xFFFF) >>> 4;
	}

	public boolean isEgg() {
		return (Util.toInt(data, 0x74) >>> 30 & 1) == 1;
	}
	
	public ShinyState getShinyState() {
		return ShinyState.getShinyState(getPSV() == getTSV());
	}
	
	public boolean isCorrupted() {
		short chk = 0;
		for (int i = 8; i < STORED_SIZE; i += 2) {
			chk += Util.toShort(data, i);
		}
		return getCheckSum() != chk;
	}
	
	private static byte[] shuffleArray(byte[] data, int sv) {
		byte[] sdata = new byte[data.length];
		Util.copy(data, sdata, 8);

		for(int block = 0; block < 4; block++) {
			Util.copy(data, 8 + 56 * BLOCK_POSITION[block][sv], sdata, 8 + 56 * block, 56);
		}

		if (data.length > STORED_SIZE) {
			Util.copy(data, STORED_SIZE, sdata, STORED_SIZE, 28);
		}

		return sdata;
	}

	public BVPokemon decrypt() {
		data = decryptArray(data);
		return this;
	}

	public static byte[] decryptArray(byte[] ekx) {
		byte[] pkx = Arrays.copyOf(ekx, ekx.length);

		int ec = Util.toInt(pkx, 0);
		int sv = (ec >>> 0xD & 0x1F) % 24;

		LCRNG lcRNG = new PokeRNG(ec);

		for(int i = 8; i < pkx.length; i += 2) {
			if(i == STORED_SIZE) lcRNG.setSeed(ec);
			byte[] bytes = Util.toBytes((short) (Util.toInt(pkx, i) ^ lcRNG.nextInt() >>> 16));
			Util.copy(bytes , 0, pkx, i, bytes.length);
		}

		pkx = shuffleArray(pkx, sv);
		
		return pkx;
	}
	
	public BVPokemon encrypt() {
		data = encryptArray(data);
		return this;
	}
	
	public static byte[] encryptArray(byte[] pkx) {

		byte[] ekx = Arrays.copyOf(pkx, pkx.length);

		int ec = Util.toInt(pkx, 0);
		int sv = (ec >>> 0xD & 0x1F) % 24;

		ekx = shuffleArray(ekx, BLOCK_POSITION_INVERT[sv]);

		LCRNG lcRNG = new PokeRNG(ec);

		for(int i = 8; i < ekx.length; i += 2) {
			if(i == STORED_SIZE) lcRNG.setSeed(ec);
			byte[] bytes = Util.toBytes((short) (Util.toInt(ekx, i) ^ lcRNG.nextInt() >>> 16));
			Util.copy(bytes , 0, ekx, i, bytes.length);
		}

		return ekx;
	}
	
	public static int getDBlock(int ec) {
		int sv = ((ec >>> 0xD) & 0x1F) % 24;
		return BLOCK_POSITION[3][sv];
	}
	
	@Override
	public String toString() {
		return getSpecies().toString();
	}
	
}
