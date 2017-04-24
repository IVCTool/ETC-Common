package nato.ivct.etc.fr.fctt_common.utils;

/**
 * Encapsulates an string in an modifiable object
 */
public class StringWrapper 
{
	private String mString;
	
	public StringWrapper(String pValue)
	{
		mString = pValue;
	}
	
	public String getString()
	{
		return mString;
	}
	
	public void setString(String pValue)
	{
		mString = pValue;
	}
}
