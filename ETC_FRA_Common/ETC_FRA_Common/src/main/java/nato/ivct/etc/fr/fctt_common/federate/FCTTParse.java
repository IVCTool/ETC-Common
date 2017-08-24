package nato.ivct.etc.fr.fctt_common.federate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FCTTParse 
{


	public static String getS1(String pString)
	{
		return decodeParam1(pString,0);
	}

	public static String getS2(String pString)
	{
		return decodeParam1(pString,1);
	}

	public static String getS3(String pString)
	{
		return decodeParam1(pString,2);
	}


	/**
	 * For a string formatted like S1{S2(S3)}
	 * @param pString string to decode
	 * @param pInd choose return value
	 * @return
	 * if pInd==0 return S2(S3)
	 * if pInd==1 return S2
	 * if pInd==2 return S3
	 */
	private static String decodeParam1(String pString, int pInd)
	{

		String retValue ="";
		Pattern p = Pattern .compile("\\w+\\{(.*)\\}");
		Matcher m = p.matcher(pString);

		if (m.find() && m.groupCount() ==1) 
		{
			if (pInd == 0)
			{
				retValue = m.group(1);
			}
			if (pInd == 1)
			{
				String lType = m.group(1);
				retValue = decodeParam2(lType,1);
			}
			if (pInd == 2)
			{
				String lType = m.group(1);
				retValue = decodeParam2(lType,2);
			}
		}

		return retValue;
	}

	/**
	 * For a string formatted like S2(N1)
	 * @param pString
	 * @param pInd
	 * @return
	 * if pInd==0 return S2(N1)
	 * if pInd==1 return S2
	 * if pInd==2 return N1
	 */
	private static String decodeParam2(String pString, int pInd)
	{
		String retValue ="";
		Pattern p = Pattern.compile("(.*)\\((\\d+)\\)");
		Matcher m = p.matcher(pString);
		if (m.find()&& m.groupCount() ==2) 
		{
			retValue = m.group(pInd);
		}
		return retValue;
	}
}
