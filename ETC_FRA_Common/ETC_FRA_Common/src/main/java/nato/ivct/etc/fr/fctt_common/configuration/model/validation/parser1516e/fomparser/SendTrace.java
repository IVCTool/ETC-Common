package nato.ivct.etc.fr.fctt_common.configuration.model.validation.parser1516e.fomparser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SendTrace {

	/**
	 * Logger
	 */
	private static final Logger logger = LogManager.getLogger(SendTrace.class);

	public static void sendWarning(String pMess) {
		logger.warn(pMess);
	}

	public static void sendError(String pMess, Exception pException) {
		logger.error(pMess + ":" + pException);
	}

	public static void sendDebug(String pMess) {
		logger.debug(pMess);
	}
	
	public static void sendError(String pMess) {
		logger.error(pMess);
	}

}
