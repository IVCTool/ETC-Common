package nato.ivct.etc.fr.fctt_common.configuration.model.validation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;

import javafx.collections.ObservableList;
import nato.ivct.etc.fr.fctt_common.resultServices.model.ServiceHLA;
import nato.ivct.etc.fr.fctt_common.utils.FCTT_Enum.eModelServiceHLAType;
import nato.ivct.etc.fr.fctt_common.utils.FCTT_Enum.eModelState;
import nato.ivct.etc.fr.fctt_common.utils.TextInternationalization;

/**
 * This class read the file containing the service and
 * create the ResultServiceModel
 *
 */
public class FCTTParserServices 
{
    /**
	 * Path to file containing the service list
	 */
	private static final String FILE_SERVICE = "services.csv";

	/**
	 * 
	 */
	private HashMap<String,eModelState> mListServ;

	/**
	 * Constructor
	 * @param pListServ the state of the services
	 */
	public FCTTParserServices(HashMap<String,eModelState> pListServ) 
	{
		mListServ = pListServ;
	}

	/**
	 * Read the service file
	 * @param logger Logger for parse error
	 * @return data model of the simulation for the services as ServiceHLA object
	 * @throws IOException I/O error
	 */
	public ServiceHLA readFile(Logger logger) throws IOException
	{
		ServiceHLA lRoot = new ServiceHLA(TextInternationalization.getString("content.root"), null);

// 2017/08/21 RMA Begin modification
// In order to avoid using resource file in bin/resources directory and using file in src/main/resources directory
//		Path lFilePath = Paths.get(FCTT_Environment.getPathResources().toString(), FILE_SERVICE);
//		List<String> lLines = Files.readAllLines(lFilePath);
		// Create a temporary file
		final File lServicesFile = File.createTempFile("Services", ".xml");
		final Path lServicesPath = lServicesFile.toPath();
		// If temporary file already exist, delete it
		if (Files.exists(lServicesPath))
		{
			Files.delete(lServicesPath);
		}
		// Copy stream file (in jar) to temporary file
		try (final InputStream lServicesStream = this.getClass().getClassLoader().getResourceAsStream(FILE_SERVICE);
				)	{
			final long nbCopies = Files.copy(lServicesStream, lServicesPath);
		}
		List<String> lLines = Files.readAllLines(lServicesPath);
// 2017/08/21 RMA End modification
		ServiceHLA lCurrentGroupServices = new ServiceHLA();
		for(String lLine:lLines)
		{
			String [] lTokens=lLine.split(";"); 
			// It's a group of services
			if (lLine.startsWith("*")) 
			{

				lCurrentGroupServices = new ServiceHLA(lTokens[1], null);
				lCurrentGroupServices.serviceTypeProperty().set(eModelServiceHLAType.Group);
				lRoot.childrenProperty().add(lCurrentGroupServices);
			}
			// It's a service
			else
			{
				String lServiceName = lTokens[0];
				String [] lRTIServiceNames = lTokens[1].split("\\|"); 
				List<String> llistServiceNames = Arrays.asList(lRTIServiceNames);

				ServiceHLA lCurrentService = new ServiceHLA(lServiceName, llistServiceNames);

				// read the list of services in the SOM to update the parameter expected or not expected service
				for(String iServiceName:llistServiceNames) 
				{
					if (mListServ.containsKey(iServiceName)) 
					{
						eModelState lState = mListServ.get(iServiceName);
						lCurrentService.stateProperty().setValue(lState);
						lCurrentService.oldStateProperty().setValue(lState);
						lCurrentService.serviceTypeProperty().set(eModelServiceHLAType.Service);
						lCurrentGroupServices.childrenProperty().add(lCurrentService);
						break;
					}
					else
					{
						// Service not declared in SOM
						eModelState lStateNewServ = eModelState.NoInformation;
						ServiceHLA lNewService = new ServiceHLA(lServiceName, llistServiceNames);
						lNewService.stateProperty().setValue(lStateNewServ);
						lNewService.oldStateProperty().setValue(lStateNewServ);
						lNewService.serviceTypeProperty().set(eModelServiceHLAType.Service);
						
						ObservableList<ServiceHLA> lGrpList = lRoot.childrenProperty();
						for(ServiceHLA iGrpServices:lGrpList) 
						{
							if (iGrpServices.nameProperty().getName().equals(lCurrentGroupServices.nameProperty().getName()))
							{
								lCurrentGroupServices.childrenProperty().add(lNewService);
								break;
							}
						}						
						
						logger.warn(TextInternationalization.getString("files.check.SOM.services.notDeclared") + iServiceName);
					}
				}
			}
		}

// 2017/08/21 RMA Begin modification
// In order to avoid using resource file in bin/resources directory and using file in src/main/resources directory
		// Delete temporary file
		Files.delete(lServicesPath);
// 2017/08/21 RMA End modification				


		return lRoot;
	}
}