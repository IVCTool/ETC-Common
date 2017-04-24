package nato.ivct.etc.fr.fctt_common.utils;

/**
 * Encapsulates an integer in an modifiable object
 */
public class IntegerWrapper 
{
	private int mInteger;
	
	public IntegerWrapper(int pInteger)
	{
		mInteger = pInteger;
	}
	
	public int getInteger()
	{
		return mInteger;
	}
	
	public void setInteger(int pValue)
	{
		mInteger = pValue;
	}
}
