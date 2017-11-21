package nato.ivct.etc.fr.fctt_common.resultData.model;

import java.io.IOException;
import java.util.ArrayList;

import nato.ivct.etc.fr.fctt_common.mainWindow.model.IObjectHLA;
import nato.ivct.etc.fr.fctt_common.utils.StringWrapper;
import nato.ivct.etc.fr.fctt_common.utils.FCTT_Enum.eBuildResults;
import nato.ivct.etc.fr.fctt_common.utils.FCTT_Enum.eModelDataHLAType;
import nato.ivct.etc.fr.fctt_common.utils.FCTT_Enum.eModelDataHLAUpdatingWay;
import nato.ivct.etc.fr.fctt_common.utils.FCTT_Enum.eModelState;

public class ResultDataModel 
{
	/**
	 * Stores the data model of the result data view
	 */
	private DataHLA mDataModel;

	/**
	 * Indicates no SOM compliance error
	 */
	private boolean mValidated = true;

	/**
	 * Constructor
	 */
	public ResultDataModel()
	{
		mDataModel = new DataHLA();
		mValidated = true;
	}

	/**
	 * Update the state and the counter of a service
	 * @param pClassName Concerned service name
	 * @param pAttributes Concerned attributes
	 * @param pUpdatingWay send or receive
	 */
	public void updateState(String pClassName, ArrayList<String> pAttributes, eModelDataHLAUpdatingWay pUpdatingWay)
	{
		//Searched the data concerned
		DataHLA lDataConcerned = searchDataHLAByName(pClassName.toLowerCase(), mDataModel, "");

		//If the data has been found
		if (lDataConcerned != null)
		{		
			ArrayList<DataHLA> lAttributesConcerned = new ArrayList<DataHLA>();
			//In the case of an object, keep only attributes updated
			if (pAttributes != null)
			{
				//Keep only the attributes update 
				for (DataHLA lChild : lDataConcerned.childrenProperty())
				{
					if (lChild.dataTypeProperty().get() == eModelDataHLAType.Attribute && pAttributes.contains(lChild.nameProperty().get().toLowerCase()))
					{
						lAttributesConcerned.add(lChild);
					}
				}		
			}
			//In the case of an interaction, all attributes are updated
			else
			{
				//Keep all the attributes cause it's an interaction
				for (DataHLA lChild : lDataConcerned.childrenProperty())
				{
					if (lChild.dataTypeProperty().get() == eModelDataHLAType.Attribute)
					{
						lAttributesConcerned.add(lChild);
					}
				}
			}			

			//For each attributes to update
			for (DataHLA lAttributeToUpdate : lAttributesConcerned)
			{			
				if (pUpdatingWay == eModelDataHLAUpdatingWay.Send)
				{
					//Increment its counter
					lAttributeToUpdate.sentCountProperty().setValue(lAttributeToUpdate.sentCountProperty().get() + 1);

					//Update its state
					if (lAttributeToUpdate.sendingStateProperty().get() == eModelState.ExpectedNotSeen)
					{
						lAttributeToUpdate.sendingStateProperty().setValue(eModelState.ExpectedSeen);		
					}
					else if (lAttributeToUpdate.sendingStateProperty().get() == eModelState.NoInformation)
					{
						lAttributeToUpdate.sendingStateProperty().setValue(eModelState.NotExpectedSeen);
					}
					else if (lAttributeToUpdate.sendingStateProperty().get() == eModelState.NotExpectedNotSeen)
					{
						lAttributeToUpdate.sendingStateProperty().setValue(eModelState.NotExpectedSeen);
					}
				}
				else
				{
					//Increment its counter
					lAttributeToUpdate.receivedCountProperty().setValue(lAttributeToUpdate.receivedCountProperty().get() + 1);

					//Update its state
					if (lAttributeToUpdate.receptionStateProperty().get() == eModelState.ExpectedNotSeen)
					{
						lAttributeToUpdate.receptionStateProperty().setValue(eModelState.ExpectedSeen);		
					}
					else if (lAttributeToUpdate.receptionStateProperty().get() == eModelState.NoInformation)
					{
						lAttributeToUpdate.receptionStateProperty().setValue(eModelState.NotExpectedSeen);
					}
					else if (lAttributeToUpdate.receptionStateProperty().get() == eModelState.NotExpectedNotSeen)
					{
						lAttributeToUpdate.receptionStateProperty().setValue(eModelState.NotExpectedSeen);
					}
				}
			}
		}
	}

	/**
	 * Searched recursively in the tree of DataHLA by name
	 * @param pFullClassNameSearched Full class name of the data searched
	 * @param pCurrentNode Current node of type DataHLA (it's a tree)
	 * @return DataHLA object which represents the data searched
	 */
	private DataHLA searchDataHLAByName(String pFullClassNameSearched, DataHLA pCurrentNode, String pCurrentFullClassName)
	{
		DataHLA lDataSearched = null;		
		String lCurrentFullClassName = null;

		//Each node contains only its name, not the full class name from the root (example : physicalentity and its full class name hlaobjectroot.baseentity.physicalentity).
		//So, it's necessary to build the full class name progressively with the searched in the tree
		//If the current node is a root, the full class name to test is empty because the name of a root node isn't a part of the full class name
		if (pCurrentNode.dataTypeProperty().get() == eModelDataHLAType.Root)
		{
			lCurrentFullClassName = "";
		}
		else
		{
			//if the current full class name tested isn't empty, it means there was previously a node test which wasn't a root,
			//so it's necessary to do a concatenation with a dot
			if (pCurrentFullClassName != "")
			{
				lCurrentFullClassName = pCurrentFullClassName+"."+pCurrentNode.nameProperty().get().toLowerCase();
			}
			//If the current full class name tested is empty, it means the last current node tested was a root,
			//so it musn't have a dot at start
			else
			{
				lCurrentFullClassName = pCurrentNode.nameProperty().get().toLowerCase();
			}
		}

		if (lCurrentFullClassName.equals(pFullClassNameSearched) == true)
		{
			lDataSearched = pCurrentNode;
		}
		else
		{		
			for (DataHLA lCurrentData : pCurrentNode.childrenProperty())
			{
				lDataSearched = searchDataHLAByName(pFullClassNameSearched, lCurrentData, lCurrentFullClassName);

				if (lDataSearched != null)
				{
					break;
				}
			}
		}
		return lDataSearched;
	}

	/**
	 * 
	 * @return data length
	 */
	public int computeMaxDataNameLength()
	{
		return computeMaxDataNameLengthRecursively(mDataModel, 0);
	}

	/**
	 * 
	 * @param pCurrentNode
	 * @param pCurrentTreeLevel
	 * @return
	 */
	private int computeMaxDataNameLengthRecursively(DataHLA pCurrentNode, int pCurrentTreeLevel)
	{
		int lLengthCurrentNode = (pCurrentTreeLevel*4) + pCurrentNode.nameProperty().get().length() + 5; 
		int lMaxLength = lLengthCurrentNode;

		for (DataHLA lCurrentData : pCurrentNode.childrenProperty())
		{
			int lLengthChildNode = computeMaxDataNameLengthRecursively(lCurrentData, pCurrentTreeLevel + 1);

			if (lLengthChildNode > lMaxLength)
			{
				lMaxLength = lLengthChildNode;
			}
		}

		return lMaxLength;
	}

	/**
	 * Write the current results into the buffered writer
	 * @param pBuildResults Results criteria
	 * @param pWriter Stream into which write current results
	 * @param pFormatter Results format string
	 * @return pWriter completed with results of data
	 */
	public StringWrapper writeResults(eBuildResults pBuildResults, StringWrapper pWriter, String pFormatter)
	{
		try 
		{
			pWriter.setString(writeResultsRecursively(mDataModel, pWriter, -1, pFormatter, pBuildResults));
		} 
		catch (IOException e) 
		{

		}

		return pWriter;
	}

	/**
	 * Write the current results into the buffered writer
	 * @param pCurrentNode Node to explore
	 * @param pWriter Stream into which write current results
	 * @param pTreeLevel Tree depth level
	 * @param pFormatter Results format string
	 * @param pBuildResults Results criteria
	 * @return String Result element for data
	 * @throws IOException I/O error
	 */
	public String writeResultsRecursively(DataHLA pCurrentNode,
  										  StringWrapper pWriter,
	  									  int pTreeLevel,
		 								  String pFormatter,
										  eBuildResults pBuildResults) throws IOException
	{
		String lCurrentNodeBuffer = "";

		for (DataHLA lCurrentData : pCurrentNode.childrenProperty())
		{
			String lChildrenBuffer = writeResultsRecursively(lCurrentData, pWriter, pTreeLevel + 1, pFormatter, pBuildResults);
			if (lChildrenBuffer != "")
			{
				lCurrentNodeBuffer = lCurrentNodeBuffer+lChildrenBuffer;
			}
		}

		String lTabulation = "";
		for (int i = 0; i < pTreeLevel; i++)
		{
			lTabulation += "    ";
		}			

		if (pCurrentNode.dataTypeProperty().get() == eModelDataHLAType.Attribute)
		{			
			// Ignore RTI reserved attribute
			if (!pCurrentNode.nameProperty().get().equals("HLAprivilegeToDeleteObject"))
			{
				if ( pCurrentNode.receptionStateProperty().get() == eModelState.NotExpectedSeen 
 				  || pCurrentNode.receptionStateProperty().get() == eModelState.ExpectedNotSeen 
				  || pCurrentNode.sendingStateProperty().get() == eModelState.NotExpectedSeen  
				  || pCurrentNode.sendingStateProperty().get() == eModelState.ExpectedNotSeen	)
				{
					// Data not certificated
					mValidated = false;
					if (pBuildResults == eBuildResults.DataNotCertificated)
					{
						lCurrentNodeBuffer = FormatResultData(pFormatter, pCurrentNode, lTabulation);
					}	
				}
				else
				{
					// Data certificated
					if (pBuildResults == eBuildResults.DataCertificated)
					{
						// 2017/11/15 ETC FRA V1.3, Capgemini, to avoid "NoInformation" in results files
						if ((pCurrentNode.sendingStateProperty().get() != eModelState.NoInformation) &&
							(pCurrentNode.receptionStateProperty().get() != eModelState.NoInformation))
								lCurrentNodeBuffer = FormatResultData(pFormatter, pCurrentNode, lTabulation);
					}
				}
			}
		}
		else
		{
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
	private String FormatResultData(String pFormatter, DataHLA pCurrentNode, String pTabulation)
	{
		String lCurrentNodeBuffer = String.format(pFormatter, 
				pTabulation+pCurrentNode.nameProperty().get(), 
				pCurrentNode.sentCountProperty().get(), 
				"R : "+pCurrentNode.sendingStateProperty().get()+" (D : "+pCurrentNode.oldSendingStateProperty().get()+")", 
				pCurrentNode.receivedCountProperty().get(), 
				"R : "+pCurrentNode.receptionStateProperty().get()+" (D : "+pCurrentNode.oldReceptionStateProperty().get()+")");
		return lCurrentNodeBuffer;
	}
	
	
	
	/**
	 * Reset all the data
	 */
	public void clearResults()
	{
		this.clearResultsRecursively(mDataModel);
	}

	/**
	 * Reset all the data recursively
	 * @param pCurrentNode Current node
	 */
	public void clearResultsRecursively(DataHLA pCurrentNode)
	{
		for (DataHLA lCurrentData : pCurrentNode.childrenProperty())
		{
			clearResultsRecursively(lCurrentData);
		}

		pCurrentNode.receptionStateProperty().setValue(pCurrentNode.oldReceptionStateProperty().get());
		pCurrentNode.sendingStateProperty().setValue(pCurrentNode.oldSendingStateProperty().get()); 
		pCurrentNode.receivedCountProperty().setValue(0); 
		pCurrentNode.sentCountProperty().setValue(0); 
	}

	/**
	 * Get the data model
	 * @return DataHLA Root object of the data model
	 */
	public IObjectHLA getDataModel()
	{
		return mDataModel;
	}

	/**
	 * Set the data model
	 * @param pDataModel DataHLA object which represents the data model
	 */
	public void setDataModel(DataHLA pDataModel)
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