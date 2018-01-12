package nato.ivct.etc.fr.fctt_common.configuration.model.validation.parser1516e.fomparser;

/**
 * Exception dedicated to manage service utilization defined in one another SOM file than 1st SOM file
 */
public class ServiceUtilizationDefinedInOtherSOMException extends Exception {

	private static final long serialVersionUID = -8632660470111171908L;

	public ServiceUtilizationDefinedInOtherSOMException() {
		super();
	}

	public ServiceUtilizationDefinedInOtherSOMException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public ServiceUtilizationDefinedInOtherSOMException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ServiceUtilizationDefinedInOtherSOMException(String arg0) {
		super(arg0);
	}

	public ServiceUtilizationDefinedInOtherSOMException(Throwable arg0) {
		super(arg0);
	}

}
