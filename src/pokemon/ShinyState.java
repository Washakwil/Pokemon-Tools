package pokemon;

public enum ShinyState {

	SHINY,
	NOT_SHINY;
	
	@Override
	public String toString() {
		return this == SHINY ? "\u2605" : "";
	}
	
	public static ShinyState getShinyState(boolean isShiny) {
		return isShiny ? SHINY : NOT_SHINY;
	}
	
}
