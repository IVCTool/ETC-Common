package nato.ivct.etc.fr.fctt_common.resultServices.model;

import java.io.IOException;
import java.util.stream.Collectors;

import nato.ivct.etc.fr.fctt_common.mainWindow.model.IObjectHLA;
import nato.ivct.etc.fr.fctt_common.utils.StringWrapper;
import nato.ivct.etc.fr.fctt_common.utils.FCTT_Enum.eBuildResults;
import nato.ivct.etc.fr.fctt_common.utils.FCTT_Enum.eModelServiceHLAType;
import nato.ivct.etc.fr.fctt_common.utils.FCTT_Enum.eModelState;

/**
 * Contains data of the results service view
 */
public class ResultServicesModel 
{
	/**
	 * Stores the data model of the result services view
	 */
	private ServiceHLA mDataModel;
	
	/**
	 * Indicates no SOM compliance error
	 */
	private boolean mValidated = true;

	/**
	 * Constructor
	 */
	public ResultServicesModel()
	{
		mDataModel = new ServiceHLA();
		mValidated = true;
	}
	
	/**
	 * Update the state and the counter of a service
	 * @param pServiceName Concerned service name
	 */
	public void updateState(String pServiceName)
	{
		//Searched the service concerned
		ServiceHLA lServiceConcerned = searchServiceHLAByName(pServiceName.toLowerCase(), mDataModel);
		
		if (lServiceConcerned != null)
		{		
			//Increment its counter
			lServiceConcerned.callCountProperty().setValue(lServiceConcerned.callCountProperty().get() + 1);
			
			//Update its state
			if (lServiceConcerned.stateProperty().get() == eModelState.ExpectedNotSeen)
			{
				lServiceConcerned.stateProperty().setValue(eModelState.ExpectedSeen);		
			}
			else if (lServiceConcerned.stateProperty().get() == eModelState.NoInformation)
			{
				lServiceConcerned.stateProperty().setValue(eModelState.NotExpectedSeen);
			}
			else if (lServiceConcerned.stateProperty().get() == eModelState.NotExpectedNotSeen)
			{
				lServiceConcerned.stateProperty().setValue(eModelState.NotExpectedSeen);
			}
		}
	}
	
	/**
	 * Searched recursively in the tree of ServiceHLA by name
	 * @param pServiceName Name of the service searched
	 * @param pCurrentNode Current node of type ServiceHLA (it's a tree)
	 * @return ServiceHLA object which represents the service searched
	 */
	private ServiceHLA searchServiceHLAByName(String pServiceName, ServiceHLA pCurrentNode)
	{
		ServiceHLA lServiceSearched = null;
		
		if (pCurrentNode.methodsName().stream().map(String::toLowerCase).collect(Collectors.toList()).contains(pServiceName) == true)
		{
			lServiceSearched = pCurrentNode;
		}
		else
		{		
			for (ServiceHLA lCurrentService : pCurrentNode.childrenProperty())
			{
				lServiceSearched = searchServiceHLAByName(pServiceName, lCurrentService);
				
				if (lServiceSearched != null)
				{
					break;
				}
			}
		}
		
		return lServiceSearched;
	}
	
	/**
	 * Compute the max length of the first column for write the results of the services.
	 * The max length is computed with the size the service name and the tabulation (= 4 spaces) according to the tree level.
	 * @return The max number of characters of the first column
	 */
	public int computeMaxServiceNameLength()
	{
		return computeMaxServiceNameLengthRecursively(mDataModel, 0);
	}
	
	/**
	 * Compute rescursively the max length of the first column for write the results of the services.
	 * The max length is computed with the size the service name and the tabulation (= 4 spaces) according to the tree level.
	 * @return The max number of characters of the first column
	 */
	private int computeMaxServiceNameLengthRecursively(ServiceHLA pCurrentNode, int pCurrentTreeLevel)
	{
		int lLengthCurrentNode = pCurrentTreeLevel*4 + pCurrentNode.nameProperty().get().length(); 
		int lMaxLength = lLengthCurrentNode;
			
		for (ServiceHLA lCurrentData : pCurrentNode.childrenProperty())
		{
			int lLengthChildNode = computeMaxServiceNameLengthRecursively(lCurrentData, pCurrentTreeLevel + 1);
			
			if (lLengthChildNode > lMaxLength)
			{
				lMaxLength = lLengthChildNode;
			}
		}
		
		return lMaxLength;
	}
	
	/**
	 * Write the current results into the StringWrapper
	 * @param pBuildResults Action to do
	 * @param pWriter Object which stores a string, allows to pass by reference a string
	 * @param pFormatter Formatter to write the results
	 * @return StringWrapper completed with results of services
	 */
	public StringWrapper writeResults(eBuildResults pBuildResults, StringWrapper pWriter, String pFormatter)
	{
		try 
		{
			pWriter.setString(writeResultsRecursively(mDataModel, pWriter, 0, pFormatter, pBuildResults));
		} 
		catch (IOException e) 
		{

		}
		
		return pWriter;
	}
	
	/**
	 * Write recursively the current results into the StringWrapper
	 * @param pCurrentNode Node Current node to treat
	 * @param pWriter Object which stores a string, allows to pass by reference a string
	 * @param pTreeLevel Indicates the current level on which the recursion works
	 * @param pFormatter Formatter to write the results
	 * @param pBuildResults Action to do
	 * @return String with all results of services
	 * @throws IOException I/O error
	 */
	public String writeResultsRecursively(ServiceHLA pCurrentNode,
										  StringWrapper pWriter,
										  int pTreeLevel,
										  String pFormatter,
										  eBuildResults pBuildResults) throws IOException
	{
		String lCurrentNodeBuffer = "";				

		for (ServiceHLA lCurrentData : pCurrentNode.childrenProperty())
		{
			//Go down in the tree until the leafs
			String lChildrenBuffer = writeResultsRecursively(lCurrentData, pWriter, pTreeLevel + 1, pFormatter, pBuildResults);
			
			//If there is at least one child write, concatenates the results in the current node
			if (lChildrenBuffer != "")
			{
				lCurrentNodeBuffer = lCurrentNodeBuffer+lChildrenBuffer;
			}
		}
		
		//Computes how many tabulation (here a tabulation is 4 spaces) must be displayed according to the level of the tree
		String lTabulation = "";
		for (int i = 0; i < pTreeLevel; i++)
		{
			lTabulation += "    ";
		}			
		
		//Check if the current node is a service and not a group or a root
		if (pCurrentNode.serviceTypeProperty().get() == eModelServiceHLAType.Service)
		{			
			if ( pCurrentNode.stateProperty().get() == eModelState.NotExpectedSeen 
					|| pCurrentNode.stateProperty().get() == eModelState.ExpectedNotSeen )
			{
				// Service not certificated
				mValidated = false;
				if (pBuildResults == eBuildResults.ServicesNotCertificated)
				{
					lCurrentNodeBuffer = FormatResultService(pFormatter, pCurrentNode, lTabulation);
				}
			}
			else
			{
				// Service certificated
				if (pBuildResults == eBuildResults.ServicesCertificated)
				{
					lCurrentNodeBuffer = FormatResultService(pFormatter, pCurrentNode, lTabulation);
				}
			}
		}
		//If the current node isn't a service
		else
		{
			//If at least one child of the current node has been written, concatenates the children results and the name of the current node
			if (lCurrentNodeBuffer != "")
			{
				lCurrentNodeBuffer = lTabulation+pCurrentNode.nameProperty().get()+"\r\n"+lCurrentNodeBuffer;
			}
		}
		
		return lCurrentNodeBuffer;
	}

	/**
	 * @param pFormatter
	 * @param pCurrentNode
	 * @param pTabulation
	 * @return
	 */
	private String FormatResultService(String pFormatter, ServiceHLA pCurrentNode, String pTabulation)
	{
		String lCurrentNodeBuffer = String.format(pFormatter, 
				pTabulation+pCurrentNode.nameProperty().get(), 
				pCurrentNode.callCountProperty().get(), 
				"R : "+pCurrentNode.stateProperty().get()+" (D : "+pCurrentNode.oldStateProperty().get()+")");
		return lCurrentNodeBuffer;
	}

	/**
	 * Reset all the services
	 */
	public void clearResults()
	{
		this.clearResultsRecursively(mDataModel);
	}
	
	/**
	 * Reset all the services recursively
	 * @param pCurrentNode Top node
	 */
	public void clearResultsRecursively(ServiceHLA pCurrentNode)
	{
		for (ServiceHLA lCurrentData : pCurrentNode.childrenProperty())
		{
			clearResultsRecursively(lCurrentData);
		}
		
		pCurrentNode.stateProperty().setValue(pCurrentNode.oldStateProperty().get());
		pCurrentNode.callCountProperty().setValue(0); 
	}
	
	/**
	 * Get the data model
	 * @return ServiceHLA Root object of the data model
	 */
	public IObjectHLA getDataModel()
	{
		return mDataModel;
	}
	
	/**
	 * Set the data model
	 * @param pDataModel object which represents the data model
	 */
	public void setDataModel(ServiceHLA pDataModel)
	{
		mDataModel = pDataModel;
	}
	
	/**
	 * Get the SOM compliance error
	 * @return boolean State of SOM compliance
	 */
	public boolean getValidated()
	{
		return mValidated;
	}
}