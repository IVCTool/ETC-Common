package nato.ivct.etc.fr.fctt_common.utils;

/**
 * Encapsulates a boolean in an modifiable object
 */
public class BooleanWrapper 
{
	private boolean mBoolean;
	
	public BooleanWrapper(boolean pBoolean)
	{
		mBoolean = pBoolean;
	}
	
	public boolean getBoolean()
	{
		return mBoolean;
	}
	
	public void setBoolean(boolean pValue)
	{
		mBoolean = pValue;
	}
}
