package nato.ivct.etc.fr.fctt_common.utils;

import java.io.InputStream;

/**
 * Contains utils methods to handle images
 */
public class FCTT_Image 
{
	/**
	 * Get the input stream from the image given in parameter
	 * @param pImageName full name of the image
	 * @return data of the image as InputStream
	 */
	public static InputStream getImage(String pImageName)
	{
		return FCTT_Image.class.getResourceAsStream(pImageName);
	}
}