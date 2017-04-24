package nato.ivct.etc.fr.fctt_common.utils;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class TextInternationalization 
{
//	private static final String mBundleName = "nato.ivct.etc.fr.resources.lang";
	private static final String mBundleName = "lang";
	private static final Locale locale = new Locale(System.getProperty("user.language"),
													System.getProperty("user.country"));
	private static ResourceBundle mResourceBundle = ResourceBundle.getBundle(mBundleName,locale);
	
	/**
	 * Get the value of the key passed in parameter
	 * @param key String represents the key of the text wanted
	 * @return The text as String
	 */
	public static String getString(String key) 
	{
		try 
		{
			return mResourceBundle.getString(key);
		} 
		catch (MissingResourceException e) 
		{
			return '!' + key + '!';
		}
	}
}