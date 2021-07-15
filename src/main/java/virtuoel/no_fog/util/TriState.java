package virtuoel.no_fog.util;

public enum TriState
{
	FALSE, DEFAULT, TRUE;
	
	public boolean get()
	{
		return this == TRUE;
	}
	
	public boolean orElse(boolean value)
	{
		return this == DEFAULT ? value : this.get();
	}
}
