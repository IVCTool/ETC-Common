package nato.ivct.etc.fr.fctt_common.configuration.model.validation.parser1516e.fomparser;

/**
 * Exception dedicated to manage service utilization not defined in 1st SOM file
 */
public class ServiceUtilizationNotIn1stSOMException extends Exception {

	private static final long serialVersionUID = 2968208893494264743L;

	public ServiceUtilizationNotIn1stSOMException() {
		super();
	}

	public ServiceUtilizationNotIn1stSOMException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public ServiceUtilizationNotIn1stSOMException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceUtilizationNotIn1stSOMException(String message) {
		super(message);
	}

	public ServiceUtilizationNotIn1stSOMException(Throwable cause) {
		super(cause);
	}
}
