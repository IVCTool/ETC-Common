package nato.ivct.etc.fr.fctt_common.utils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Contains functions to get informations about the environment of the application
 */
public class FCTT_Environment 
{

	/**
	 * Get the directory which contains the resources.
	 * This directory is located in the same directory than the jar.
	 * @return Path object which represents the directory
	 */
	public static Path getPathResources()
	{
		String lResourcesParentDirectory = Paths.get(new File(".").getAbsolutePath()).getParent().toString();
		
		Path lResourcesDirectory = Paths.get(lResourcesParentDirectory, "resources");

		return lResourcesDirectory;
	}	
	
// 2017/08/21 RMA Begin modification
// In order to avoid using resource file in bin/resources directory and using file in src/main/resources directory
//	/**
//	 * @return The file XSD
//	 */
//	public static Path getXSD_FCTT_Path() 
//	{
//		Path lXSDPath = Paths.get(FCTT_Environment.getPathResources().toString(), FCTT_Constant.FILENAME_XSD_FCTT_1516_2010);
//		
//		return lXSDPath;
//	}	
// 2017/08/21 RMA End modification
	
// 2017/08/21 RMA Begin modification
// In order to avoid using resource file in bin/resources directory and using file in src/main/resources directory
//	/**
//	 * @return The file XSD
//	 */
//	public static Path getXSD_DIF_Path() 
//	{
//		Path lXSDPath = Paths.get(FCTT_Environment.getPathResources().toString(), FCTT_Constant.FILENAME_XSD_DIF_1516_2010);
//		
//		return lXSDPath;
//	}	
// 2017/08/21 RMA End modification

	/**
	 * @return string containing date
	 */
	public static String getDateForFileName()
	{
		String lDateName = "";
		
		SimpleDateFormat formater = null;
		Date lDateNow = new Date();
		formater = new SimpleDateFormat(FCTT_Constant.FILE_FORMAT_DATE_NAME);
		lDateName = formater.format(lDateNow);
		
		return lDateName;		
	}
}