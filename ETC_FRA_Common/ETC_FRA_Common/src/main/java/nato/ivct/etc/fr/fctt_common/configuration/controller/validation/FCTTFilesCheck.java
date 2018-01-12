package nato.ivct.etc.fr.fctt_common.configuration.controller.validation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;

import javax.xml.bind.JAXBException;

import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.slf4j.Logger;

import fr.itcs.sme.architecture.technical.ISimAttribute;
import fr.itcs.sme.architecture.technical.ISimEntityClass;
import fr.itcs.sme.architecture.technical.ISimInteractionClass;
import fr.itcs.sme.architecture.technical.ISimModel;
import fr.itcs.sme.base.Element;
import fr.itcs.sme.base.Metadata;
import nato.ivct.etc.fr.fctt_common.configuration.model.validation.parser1516e.Pair;
import nato.ivct.etc.fr.fctt_common.configuration.model.validation.parser1516e.fomparser.ServiceUtilizationDefinedInOtherSOMException;
import nato.ivct.etc.fr.fctt_common.configuration.model.validation.parser1516e.fomparser.ServiceUtilizationNotIn1stSOMException;
import nato.ivct.etc.fr.fctt_common.configuration.model.validation.parser1516e.fomparser.SimModelProvider;
import nato.ivct.etc.fr.fctt_common.configuration.model.validation.parser1516e.fomparser.Utils;
import nato.ivct.etc.fr.fctt_common.resultData.model.DataHLA;
import nato.ivct.etc.fr.fctt_common.resultServices.model.ServiceHLA;
import nato.ivct.etc.fr.fctt_common.utils.FCTT_Constant;
import nato.ivct.etc.fr.fctt_common.utils.FCTT_Enum.eModelDataHLAType;
import nato.ivct.etc.fr.fctt_common.utils.FCTT_Enum.eModelState;
import nato.ivct.etc.fr.fctt_common.utils.TextInternationalization;

/**
 * This class check the file SOM/FOM
 *
 */
public class FCTTFilesCheck 
{
	/**
	 * Logger
	 */
	private Logger logger;

	/**
	 * Working directory
	 */
	private String workingDir;

	/**
	 * The ISimModelProvider
	 */
	private SimModelProvider mModelProviderWithMIM = null;

	/**
	 * The ISimModelProvider
	 */
	private SimModelProvider mModelProviderWithoutMIM = null;

	/**
	 * The SOM Parser
	 */
	private FCTTSOMParser mSOMParser = null;

	/**
	 * The Rules checker
	 */
	private FCTTRulesChecker mRulesChecker = null;

	/**
	 * Flag result rules test
	 */
	private boolean mResTestRules;

	/**
	 * Flag result SOM inclusion
	 */
	private boolean mResTestInclude;

	/**
	 * Flag result parsing FOM
	 */
	private boolean mResTestParseFOM;

	/**
	 * Flag result parsing SOM
	 */
	private boolean mResTestParseSOM;

	/**
	 * Data model for objects and interactions
	 */
	private DataHLA	mDataHLA;

	/**
	 * Data model for services
	 */
	private ServiceHLA mServiceHLA;

	/**
	 * Data model used for the distribution
	 */
	private ISimModel mDataModelSimulationForDistribution;

	/**
	 * Flag print report in log file
	 */
	private boolean csVerification;

	/**
	 * SuT name
	 */
	private String sutName;

	/**
	 * Constructor
	 * @param logger Logger
	 * @param workingDir Directory used for merge
	 * @param sutName if not null then called by CS Verification test case
	 */
	public FCTTFilesCheck(Logger logger, String workingDir, String sutName) 
	{
		this.logger = logger;
		this.workingDir = workingDir;
		this.csVerification = (sutName != null);
		this.sutName = sutName;
		
		mResTestRules = false;
		mResTestInclude	= false;
		mResTestParseFOM = false;
		mResTestParseSOM = false;	
	}

	/**
	 * This method check the files
	 * @param fomFiles FOM files
	 * @param somFiles SOM files
	 * @param resultFile check result file
	 * @return true if the files are valid 
	 */
	public boolean checkFiles(List<String> fomFiles,List<String> somFiles,File resultFile) 
	{
		List<String> lFOMFiles = new ArrayList<String>();
		List<String> lSOMFiles = new ArrayList<String>();
		String lsomFile = null;
		boolean lCheckFiles = true;
		// 2018/01/09 ETC FRA 1.4, Capgemini, to generate result message for TS_HLA_Declaration, TS_HLA_Object and TS_HLA_Services
		String lMessage = null;
		boolean lTraceLog = true;
		

		lFOMFiles.addAll(fomFiles);
		lSOMFiles.addAll(somFiles);

		FileWriter lWriter = null;

		try 
		{
			// Open file
			if (resultFile != null)
			{
				lWriter = new FileWriter(resultFile);
				// 2018/01/09 ETC FRA 1.4, Capgemini, to generate result message for TS_HLA_Declaration, TS_HLA_Object and TS_HLA_Services
				lTraceLog = false;
			}
			
			WriteMessage(lWriter ,FCTT_Constant.REPORT_FILE_SEPARATOR);
			WriteMessage(lWriter ,TextInternationalization.getString("files.check.reportFile.federateName")	+ sutName + "\n");
			WriteMessage(lWriter,FCTT_Constant.REPORT_FILE_SEPARATOR);
	    	logger.info(TextInternationalization.getString("files.check.FOM.parse"));
			WriteMessage(lWriter,TextInternationalization.getString("files.check.reportFile.FOMFiles"));
			PrintArrayListString(lFOMFiles, lWriter); 		
			
			//
			// Parse FOM files
			//
			try 
			{

				// 
				// Read FOM without MIM to generate ISimModel. 
				// This ISimModel is used by the SOM parser only
				//
				String lMergedFOMfile = workingDir + File.separator + FCTT_Constant.MERGED_NAME_FOM;
				mModelProviderWithoutMIM = new SimModelProvider();
				mModelProviderWithoutMIM.parse(ArrayPathToList(lFOMFiles), new Path(lMergedFOMfile), false);

				// 
				// Read FOM modules with MIM to generate ISimModel. 
				// This ISimModel is used for the distribution.
				//
				mModelProviderWithMIM = new SimModelProvider();
// 2017/08/21 RMA Begin modification
// In order to avoid using resource file in bin/resources directory and using file in src/main/resources directory
//				java.nio.file.Path lMIMPath = Paths.get(FCTT_Environment.getPathResources().toString(), FCTT_Constant.MIM_FILE_NAME);
//				logger.info("lMIMPath = "+ lMIMPath);
//				lFOMFiles.add(lMIMPath.toString());
				// Create a temporary file
				final File lMIMFile = File.createTempFile("MIM", ".xml");
				final java.nio.file.Path lMIMPath = lMIMFile.toPath();
//				final java.nio.file.Path lMIMPath = Paths.get(
//						Paths.get(".").toAbsolutePath().getParent().toString(), 
//						FCTT_Constant.MIM_FILE_NAME);
//				logger.info("lMIMPath = "+ lMIMPath);
				// If temporary file already exist, delete it
				if (Files.exists(lMIMPath))
				{
					Files.delete(lMIMPath);
				}
				// Copy stream file (in jar) to temporary file
				try (final InputStream lMIMStream = this.getClass().getClassLoader().getResourceAsStream(FCTT_Constant.MIM_FILE_NAME);
						)	{
//					logger.info("lMIMStream = "+ lMIMStream );
//					logger.info("Available = "+ lMIMStream.available() );
					final long nbCopies = Files.copy(lMIMStream, lMIMPath);
//					logger.info("Copy OK - " + nbCopies + " bytes");
				}
// 2017/08/21 RMA End modification
				lFOMFiles.add(lMIMPath.toString());
				mDataModelSimulationForDistribution = mModelProviderWithMIM.parse(ArrayPathToList(lFOMFiles), new Path(lMergedFOMfile), true);

// 2017/08/21 RMA Begin modification
// In order to avoid using resource file in bin/resources directory and using file in src/main/resources directory
				// Delete temporary file
				Files.delete(lMIMPath);
// 2017/08/21 RMA End modification	

				// 2018/01/09 ETC FRA 1.4, Capgemini, to generate result message for TS_HLA_Declaration, TS_HLA_Object and TS_HLA_Services
				lMessage = TextInternationalization.getString("files.check.reportFile.resFOMFiles") + StringResult(true) + "\n";
				if (lTraceLog) logger.info(lMessage);
				WriteMessage(lWriter, lMessage);
				mResTestParseFOM = true;
			}
			catch (Exception pException) 
			{
				// 2018/01/09 ETC FRA 1.4, Capgemini, to generate result message for TS_HLA_Declaration, TS_HLA_Object and TS_HLA_Services
				lMessage = TextInternationalization.getString("files.check.reportFile.resFOMFiles") + StringResult(false) + "\n";
				if (lTraceLog) logger.info(lMessage);
				logger.error(TextInternationalization.getString("files.check.error.FOM.parse") + ":" + pException.getLocalizedMessage());

				WriteMessage(lWriter,lMessage);
				WriteMessage(lWriter,pException.getLocalizedMessage());

				return false;
			}

			//
			// Parse SOM files
			//

			try 
			{
				WriteMessage(lWriter,FCTT_Constant.REPORT_FILE_SEPARATOR);
				logger.info(TextInternationalization.getString("files.check.SOM.parse"));
				WriteMessage(lWriter,TextInternationalization.getString("files.check.reportFile.SOMFiles"));
				PrintArrayListString(lSOMFiles,lWriter);
				
				//
				// Merge SOM modules
				//
				String lMergedSOMFile = workingDir + File.separator + FCTT_Constant.MERGED_NAME_SOM;
// 2017/08/21 RMA Begin modification
// In order to avoid using resource file in bin/resources directory and using file in src/main/resources directory
//				mModelProviderWithoutMIM.mergeFOMModules(ArrayPathToList(lSOMFiles), new Path(lMergedSOMFile),FCTT_Environment.getXSD_FCTT_Path().toFile());
				// Create a temporary file
				final File lXSDFCTTFile = File.createTempFile("XSDFCTT", ".xml");
				final java.nio.file.Path lXSDFCTTPath = lXSDFCTTFile.toPath();
				// If temporary file already exist, delete it
				if (Files.exists(lXSDFCTTPath))
				{
					Files.delete(lXSDFCTTPath);
				}
				// Copy stream file (in jar) to temporary file
				try (final InputStream lXSDFCTTStream = this.getClass().getClassLoader().getResourceAsStream(FCTT_Constant.FILENAME_XSD_FCTT_1516_2010);
						)	{
					final long nbCopies = Files.copy(lXSDFCTTStream, lXSDFCTTPath);
				}
				
				// Begin 2018/01/09 ETC FRA 1.4, Capgemini, to check that serviceUtilization defined only in 1st SOM
				// mModelProviderWithoutMIM.mergeFOMModules(ArrayPathToList(lSOMFiles), new Path(lMergedSOMFile), lXSDFCTTFile);
				try {
					mModelProviderWithoutMIM.mergeFOMModules(ArrayPathToList(lSOMFiles), new Path(lMergedSOMFile),lXSDFCTTFile, true);
				} 
				// No serviceUtilization in first SOM file 
				catch (ServiceUtilizationNotIn1stSOMException pException)
				{
					String lError = TextInternationalization.getString("files.check.SOM.services.notIn1stSOM") +
								pException.getMessage();
					logger.error(TextInternationalization.getString("files.check.error.SOM.parse") + ": " + lError);

					// 2018/01/09 ETC FRA 1.4, Capgemini, to generate result message for TS_HLA_Declaration, TS_HLA_Object and TS_HLA_Services
					lMessage = TextInternationalization.getString("files.check.reportFile.resSOMFiles") + StringResult(false) + "\n";
					if (lTraceLog) logger.info(lMessage);
					WriteMessage(lWriter, lMessage);
					WriteMessage(lWriter, lError);

					return false;
				}
				// serviceUtilization defined in SOM file which is not the first SOM file
				catch (ServiceUtilizationDefinedInOtherSOMException pException)
				{
					String lError = TextInternationalization.getString("files.check.SOM.services.definedInOtherSOMBegin") +
								pException.getMessage() + " " + TextInternationalization.getString("files.check.SOM.services.definedInOtherSOMEnd");
					logger.error(TextInternationalization.getString("files.check.error.SOM.parse") + ": " + lError);

					// 2018/01/09 ETC FRA 1.4, Capgemini, to generate result message for TS_HLA_Declaration, TS_HLA_Object and TS_HLA_Services
					lMessage = TextInternationalization.getString("files.check.reportFile.resSOMFiles") + StringResult(false) + "\n";
					if (lTraceLog) logger.info(lMessage);
					WriteMessage(lWriter, lMessage);
					WriteMessage(lWriter, lError);

					return false;
				}
				// End 2018/01/04 FCTT NG V1.4, Capgemini, to check that serviceUtilization defined only in 1st SOM

				
				// Delete temporary file
				Files.delete(lXSDFCTTPath);
// 2017/08/21 RMA End modification
				

				//
				// Parse SOM modules
				//
				mSOMParser = new FCTTSOMParser(mModelProviderWithoutMIM.getDomain()
						, mModelProviderWithoutMIM.getParsedSimObjects()
						, mModelProviderWithoutMIM.getParsedSimInteractions());

				if (lSOMFiles.size() > 1) 
				{
					lsomFile = lMergedSOMFile;
				} 
				else 
				{
					lsomFile = lSOMFiles.get(0);
				}

				mSOMParser.parsingSOM(lsomFile);

				// 2018/01/09 ETC FRA 1.4, Capgemini, to generate result message for TS_HLA_Declaration, TS_HLA_Object and TS_HLA_Services
				lMessage = TextInternationalization.getString("files.check.reportFile.resSOMFiles") + StringResult(true) + "\n";
				if (lTraceLog) logger.info(lMessage);
				WriteMessage(lWriter, lMessage);
			}	
			catch (Exception pException) 
			{
				// 2018/01/09 ETC FRA 1.4, Capgemini, to generate result message for TS_HLA_Declaration, TS_HLA_Object and TS_HLA_Services
				lMessage = TextInternationalization.getString("files.check.reportFile.resSOMFiles") + StringResult(false) + "\n";
				if (lTraceLog) logger.info(lMessage);
				WriteMessage(lWriter,lMessage);
				logger.error(TextInternationalization.getString("files.check.error.SOM.parse") + ":" + pException.getLocalizedMessage());
				WriteMessage(lWriter,pException.getLocalizedMessage());

				return false;
			}


			// 
			// Check if there are elements in SOM not include in FOM
			//
			mResTestInclude = !mSOMParser.IsElemNotInFom();
			WriteMessage(lWriter,FCTT_Constant.REPORT_FILE_SEPARATOR);
			logger.info(TextInternationalization.getString("files.check.SOM_IN_FOM"));
			// 2018/01/09 ETC FRA 1.4, Capgemini, to generate result message for TS_HLA_Declaration, TS_HLA_Object and TS_HLA_Services
			lMessage = TextInternationalization.getString("files.check.reportFile.resSOMInFOM") 
					+ StringResult(mResTestInclude) + "\n";
			if (lTraceLog) logger.info(lMessage);
			WriteMessage(lWriter,lMessage);

			// Write in the report file the list of objects classes and interactions not present in FOM
			if (!mResTestInclude)
			{
				logger.error(TextInternationalization.getString("files.check.error.inclusion"));
				
				PrintMessList(lWriter,
						      TextInternationalization.getString("files.check.reportFile.objNotIncluded"),
							  mSOMParser.getMlistObjNotInFOM());
				
				PrintMessList(lWriter,
							  TextInternationalization.getString("files.check.reportFile.attNotIncluded"),
							  mSOMParser.getMlistAttributNotInFOM());
				
				PrintMessList(lWriter,
							  TextInternationalization.getString("files.check.reportFile.intNotIncluded"),
							  mSOMParser.getMlistInterNotInFOM());
				
				PrintMessList(lWriter,
							  TextInternationalization.getString("files.check.reportFile.paramNotIncluded"),
							  mSOMParser.getMListParameterNotInFOM());
			}

			// 
			// Check if the sharing of elements are coherent between FOM and SOM
			//
			mResTestParseSOM = mSOMParser.IsSharingOK();
			
			WriteMessage(lWriter,FCTT_Constant.REPORT_FILE_SEPARATOR);
			logger.info(TextInternationalization.getString("files.check.sharing"));
			// 2018/01/09 ETC FRA 1.4, Capgemini, to generate result message for TS_HLA_Declaration, TS_HLA_Object and TS_HLA_Services
			lMessage = TextInternationalization.getString("files.check.reportFile.resSharing") 
					+ StringResult(mResTestParseSOM) + "\n";
			if (lTraceLog) logger.info(lMessage);
			WriteMessage(lWriter,lMessage);

			// Write in the report file the sharing errors.
			if (!mResTestParseSOM) 
			{
				WriteMessage(lWriter,TextInternationalization.getString("files.check.error.sharing") + "\n");
				WriteMessage(lWriter,TextInternationalization.getString("files.check.reportFile.sharingErrors"));
				PrintShareErrors(mSOMParser.getListSharDiff(),lWriter);
			} 

			// 
			// Convert ISimModel to DataHLA
			//
			mDataHLA = convertISimModelIntoDataHLA(mSOMParser.getmDomain());					

			// 
			// Read the service list
			//
			try
			{		
				mServiceHLA = mSOMParser.readServicesList(logger);
			} 
			catch (IOException pIOException) 
			{
				WriteMessage(lWriter,TextInternationalization.getString("files.check.reportFile.readServicesError"));
				WriteMessage(lWriter,pIOException.getLocalizedMessage());

				if (csVerification)
					return false;
				// Not a problem for other TC
			}

			//
			// Check the rules
			//
			mRulesChecker = new FCTTRulesChecker();
			try 
			{
				// log validation des regles
				mResTestRules = mRulesChecker.checkRules(lsomFile);
				WriteMessage(lWriter,FCTT_Constant.REPORT_FILE_SEPARATOR);
				logger.info(TextInternationalization.getString("files.check.rules"));
				// 2018/01/09 ETC FRA 1.4, Capgemini, to generate result message for TS_HLA_Declaration, TS_HLA_Object and TS_HLA_Services
				lMessage = TextInternationalization.getString("files.check.reportFile.resultTest")
						+ StringResult(mResTestRules) + "\n";
				if (lTraceLog) logger.info(lMessage);
				WriteMessage(lWriter,lMessage);

				if (!mResTestRules)
				{
					PrintArrayListString(mRulesChecker.getListAssert(),lWriter);
				}
			} 
			catch (JAXBException | IOException pException) 
			{
				// 2018/01/09 ETC FRA 1.4, Capgemini, to generate result message for TS_HLA_Declaration, TS_HLA_Object and TS_HLA_Services
				lMessage = TextInternationalization.getString("files.check.reportFile.resultTest") + StringResult(false) + "\n";
				if (lTraceLog) logger.info(lMessage);
				logger.error(TextInternationalization.getString("files.check.error.rules") + ":" + pException.getLocalizedMessage());

				WriteMessage(lWriter,FCTT_Constant.REPORT_FILE_SEPARATOR);
				WriteMessage(lWriter,lMessage);
				WriteMessage(lWriter,pException.getLocalizedMessage());

				if (csVerification)
					return false;
				// Not a problem for other TC
			}

			lCheckFiles = mResTestRules && mResTestInclude && mResTestParseFOM &&  mResTestParseSOM ;
		}

		catch (IOException pIOException) 
		{
			logger.error(TextInternationalization.getString("write.error.reportFile") + ": " + pIOException.toString());

			return false;
		}

		finally
		{
			try 
			{
				if (lWriter != null)
					lWriter.close();
			} 
			catch (IOException pIOException) 
			{
				logger.error(TextInternationalization.getString("close.error.reportFile") + ":" + resultFile + " " + pIOException.toString());
			}
		}
		
		return lCheckFiles;
	} 


	/**
	 * @param pWriter
	 * @param pTypeList
	 * @param pList
	 * @throws IOException
	 */
	void PrintMessList(Writer pWriter,String pTypeList,ArrayList<String> pList) throws IOException 
	{
		if (pList.size() > 0)
		{
			WriteMessage(pWriter,pTypeList);
			PrintArrayListString(pList,pWriter);
		}
	}



	/**
	 * Transform List<String> to Path[]
	 * @param pList
	 * @return
	 */
	private Path[] ArrayPathToList(List<String> pList) 
	{
		Path[] retPath= new Path[pList.size()];	

		for(int i=0;i < pList.size();i++) 
		{
			retPath[i] = new Path(pList.get(i));
		}

		return retPath;
	}

	/**
	 * @param logger
	 * @param pFOMFiles
	 * @throws IOException 
	 */
	private void PrintArrayListString(List<String> pFOMFiles,Writer pWriter) throws IOException 
	{
		for (String iElem : pFOMFiles)
		{
			WriteMessage(pWriter, iElem);
			WriteMessage(pWriter, "\n");
		}
	}

	/**
	 * @param pHashMap
	 * @param pWriter
	 * @throws IOException 
	 */
	private void PrintShareErrors(HashMap<String, Pair<String, String>> pHashMap,Writer pWriter) throws IOException 
	{
		for (Entry<String, Pair<String, String>> currentEntry : pHashMap.entrySet()) 
		{
			String lName = currentEntry.getKey();
			Pair<String, String> value = currentEntry.getValue();
			WriteMessage(pWriter,lName + " " + TextInternationalization.getString("files.check.reportFile.InFOM") 
					+ value.getFirst() + " "
					+ TextInternationalization.getString("files.check.reportFile.InSOM")
					+ value.getSecond() + "\n");
		}	
	}

	/**
	 * @param pRes
	 * @return
	 */
	private String StringResult(boolean pRes) 
	{
		if (pRes)
		{
			return TextInternationalization.getString("files.check.succeeded");
		}
		else
		{
			return TextInternationalization.getString("files.check.failed");
		}
	}

	/**
	 * @return the ISimModel
	 */
	public ISimModel getSimModel() 
	{
		return mModelProviderWithMIM.getDomain();
	}

	/**
	 * @return the SOMParser
	 */
	public FCTTSOMParser getmSOMParser() 
	{
		return mSOMParser;
	}

	/**
	 * Create the data model DataHLA from the data model ISimModel
	 * @param pModel data model as ISimModel to convert
	 * @return DataHLA which contains the data model
	 */
	private DataHLA convertISimModelIntoDataHLA(ISimModel pModel)
	{
		DataHLA lRoot = new DataHLA();		
		lRoot.childrenProperty().add(buildObjectDataModel(pModel.getEntities()));
		lRoot.childrenProperty().add(buildInteractionDataModel(pModel.getInteractions()));

		return lRoot;
	}	
	
	/**
	 * Converts the data model ISimModel into the data model DataHLA only for the objects class.
	 * @param lAllObjects All objects class in the ISimModel
	 * @return DataHLA which contains the data model only for the objects class
	 */
	private DataHLA buildObjectDataModel(EList<ISimEntityClass> lAllObjects)
	{
		DataHLA lRootObject = new DataHLA(TextInternationalization.getString("content.object"));
		lRootObject.dataTypeProperty().setValue(eModelDataHLAType.Root);

		//Stores all DataHLA created with their name as key
		HashMap<String, DataHLA> lDataHLACreated = new HashMap<String, DataHLA>();

		//For each object class
		for (ISimEntityClass lObject : lAllObjects)
		{
			//Creation of the data model as DataHLA
			DataHLA lCurrentObject = new DataHLA();
			lCurrentObject.nameProperty().setValue(lObject.getName());
			lCurrentObject.dataTypeProperty().setValue(eModelDataHLAType.Object);
			
			//For each attribute of the current objet class
			for (ISimAttribute lAttribute : lObject.getAllAttributes())
			{
				//Creation of the data model as DataHLA
				DataHLA lCurrentAttribute = new DataHLA();
				lCurrentAttribute.dataTypeProperty().setValue(eModelDataHLAType.Attribute);
				lCurrentAttribute.nameProperty().setValue(lAttribute.getName());
				lCurrentAttribute.receivedCountProperty().setValue(0);
				lCurrentAttribute.sentCountProperty().setValue(0);

				//Getting of the information about the publishing and the subscribing of the current attribute
				Metadata lMetadataAttribute = this.getMetaData("sharing", lAttribute);

				
				// Look if the object class is present in SOM 
				boolean lObjectNotInSOM = true;
				Metadata lInSom = Utils.getMetadata(FCTT_Constant.METADATA_ELEMENT_IN_SOM, lObject);
				if (null !=lInSom)
				{
					if (lInSom.getValue().equals(FCTT_Constant.VALUE_ELEMENT_PRESENT_IN_SOM))
					{
						lObjectNotInSOM = false;
					}
				}
				
				//If the current attribute isn't declared "subscribed" or "published" or "neither", so its state is "not expected"
				if ( (lMetadataAttribute == null) || (lObjectNotInSOM) )
				{
					lCurrentAttribute.sendingStateProperty().setValue(eModelState.NoInformation);
					lCurrentAttribute.oldSendingStateProperty().setValue(eModelState.NoInformation);
					lCurrentAttribute.receptionStateProperty().setValue(eModelState.NoInformation);
					lCurrentAttribute.oldReceptionStateProperty().setValue(eModelState.NoInformation);
				}
				else
				{
					switch (lMetadataAttribute.getValue()) 
					{
					case FCTT_Constant.SHARE_PUBLISH :
						lCurrentAttribute.sendingStateProperty().setValue(eModelState.ExpectedNotSeen);
						lCurrentAttribute.oldSendingStateProperty().setValue(eModelState.ExpectedNotSeen);
						lCurrentAttribute.receptionStateProperty().setValue(eModelState.NotExpectedNotSeen);
						lCurrentAttribute.oldReceptionStateProperty().setValue(eModelState.NotExpectedNotSeen);
						break;
					case FCTT_Constant.SHARE_SUBSCRIBE :
						lCurrentAttribute.sendingStateProperty().setValue(eModelState.NotExpectedNotSeen);
						lCurrentAttribute.oldSendingStateProperty().setValue(eModelState.NotExpectedNotSeen);
						lCurrentAttribute.receptionStateProperty().setValue(eModelState.ExpectedNotSeen);
						lCurrentAttribute.oldReceptionStateProperty().setValue(eModelState.ExpectedNotSeen);
						break;
					case FCTT_Constant.SHARE_PUBLISH_SUBSCRIBE :
						lCurrentAttribute.sendingStateProperty().setValue(eModelState.ExpectedNotSeen);
						lCurrentAttribute.oldSendingStateProperty().setValue(eModelState.ExpectedNotSeen);
						lCurrentAttribute.receptionStateProperty().setValue(eModelState.ExpectedNotSeen);
						lCurrentAttribute.oldReceptionStateProperty().setValue(eModelState.ExpectedNotSeen);
						break;
					case FCTT_Constant.SHARE_NEITHER :
						lCurrentAttribute.sendingStateProperty().setValue(eModelState.NotExpectedNotSeen);
						lCurrentAttribute.oldSendingStateProperty().setValue(eModelState.NotExpectedNotSeen);
						lCurrentAttribute.receptionStateProperty().setValue(eModelState.NotExpectedNotSeen);
						lCurrentAttribute.oldReceptionStateProperty().setValue(eModelState.NotExpectedNotSeen);
						break;
					default:
						lCurrentAttribute.sendingStateProperty().setValue(eModelState.NoInformation);
						lCurrentAttribute.oldSendingStateProperty().setValue(eModelState.NoInformation);
						lCurrentAttribute.receptionStateProperty().setValue(eModelState.NoInformation);
						lCurrentAttribute.oldReceptionStateProperty().setValue(eModelState.NoInformation);
					}
				}

				//Add the attributes as child of the current object class
				lCurrentObject.childrenProperty().add(lCurrentAttribute);
			}

			//Put the newly DataHLA in the hashmap to retrieve it easily
			lDataHLACreated.put(lCurrentObject.nameProperty().get(), lCurrentObject);
		}

		//Creation of the dependency between objects class
		for (ISimEntityClass lObject : lAllObjects)
		{
			DataHLA lCurrentChild = lDataHLACreated.get(lObject.getName());
			DataHLA lCurrentParent = null;

			if (lObject.getParent() == null)
			{
				lCurrentParent = lRootObject;
			}
			else
			{
				lCurrentParent = lDataHLACreated.get(lObject.getParent().getName());
			}

			lCurrentParent.childrenProperty().add(lCurrentChild);
		}

		return lRootObject;
	}

	/**
	 * Converts the data model ISimModel into the data model DataHLA only for the interactions.
	 * @param lAllInteractions All interactions in the ISimModel
	 * @return DataHLA which contains the data model only for the interactions
	 */
	private DataHLA buildInteractionDataModel(EList<ISimInteractionClass> lAllInteractions)
	{
		DataHLA lRootInteraction = new DataHLA(TextInternationalization.getString("content.interaction"));
		lRootInteraction.dataTypeProperty().setValue(eModelDataHLAType.Root);

		//Stores all DataHLA created with their name as key
		HashMap<String, DataHLA> lDataHLACreated = new HashMap<String, DataHLA>();

		//For each interaction
		for (ISimInteractionClass lInteraction : lAllInteractions)
		{
			//Creation of the data model as DataHLA
			DataHLA lCurrentInteraction = new DataHLA();
			lCurrentInteraction.nameProperty().setValue(lInteraction.getName());
			lCurrentInteraction.dataTypeProperty().setValue(eModelDataHLAType.Interaction);			

			//For each attribute of the current interaction
			for (ISimAttribute lAttribute : lInteraction.getAllParameters())
			{
				//Creation of the data model as DataHLA
				DataHLA lCurrentAttribute = new DataHLA();
				lCurrentAttribute.dataTypeProperty().setValue(eModelDataHLAType.Attribute);
				lCurrentAttribute.nameProperty().setValue(lAttribute.getName());
				lCurrentAttribute.receivedCountProperty().setValue(0);
				lCurrentAttribute.sentCountProperty().setValue(0);

				//Getting of the information about the publishing and the subscribing of the current attribute
				Metadata lMetadataAttribute = this.getMetaData("sharing", lAttribute);

				// Look if the object class is present in SOM 
				boolean lInterNotInSOM = true;
				Metadata lInSom = Utils.getMetadata(FCTT_Constant.METADATA_ELEMENT_IN_SOM, lInteraction);
				if (null !=lInSom)
				{
					if (lInSom.getValue().equals(FCTT_Constant.VALUE_ELEMENT_PRESENT_IN_SOM))
					{
						lInterNotInSOM = false;
					}
				}

				//If the current attribute isn't declared "subscribed" or "published" or "neither", so its state is "not expected"
				if ( (lMetadataAttribute == null) || (lInterNotInSOM) )
				{
					lCurrentAttribute.sendingStateProperty().setValue(eModelState.NoInformation);
					lCurrentAttribute.oldSendingStateProperty().setValue(eModelState.NoInformation);
					lCurrentAttribute.receptionStateProperty().setValue(eModelState.NoInformation);
					lCurrentAttribute.oldReceptionStateProperty().setValue(eModelState.NoInformation);
				}
				else
				{

					switch (lMetadataAttribute.getValue()) 
					{
					case FCTT_Constant.SHARE_PUBLISH :
						lCurrentAttribute.sendingStateProperty().setValue(eModelState.ExpectedNotSeen);
						lCurrentAttribute.oldSendingStateProperty().setValue(eModelState.ExpectedNotSeen);
						lCurrentAttribute.receptionStateProperty().setValue(eModelState.NotExpectedNotSeen);
						lCurrentAttribute.oldReceptionStateProperty().setValue(eModelState.NotExpectedNotSeen);
						break;
					case FCTT_Constant.SHARE_SUBSCRIBE :
						lCurrentAttribute.sendingStateProperty().setValue(eModelState.NotExpectedNotSeen);
						lCurrentAttribute.oldSendingStateProperty().setValue(eModelState.NotExpectedNotSeen);
						lCurrentAttribute.receptionStateProperty().setValue(eModelState.ExpectedNotSeen);
						lCurrentAttribute.oldReceptionStateProperty().setValue(eModelState.ExpectedNotSeen);
						break;
					case FCTT_Constant.SHARE_PUBLISH_SUBSCRIBE :
						lCurrentAttribute.sendingStateProperty().setValue(eModelState.ExpectedNotSeen);
						lCurrentAttribute.oldSendingStateProperty().setValue(eModelState.ExpectedNotSeen);
						lCurrentAttribute.receptionStateProperty().setValue(eModelState.ExpectedNotSeen);
						lCurrentAttribute.oldReceptionStateProperty().setValue(eModelState.ExpectedNotSeen);
						break;
					case FCTT_Constant.SHARE_NEITHER :
						lCurrentAttribute.sendingStateProperty().setValue(eModelState.NotExpectedNotSeen);
						lCurrentAttribute.oldSendingStateProperty().setValue(eModelState.NotExpectedNotSeen);
						lCurrentAttribute.receptionStateProperty().setValue(eModelState.NotExpectedNotSeen);
						lCurrentAttribute.oldReceptionStateProperty().setValue(eModelState.NotExpectedNotSeen);
						break;
					default:
						lCurrentAttribute.sendingStateProperty().setValue(eModelState.NoInformation);
						lCurrentAttribute.oldSendingStateProperty().setValue(eModelState.NoInformation);
						lCurrentAttribute.receptionStateProperty().setValue(eModelState.NoInformation);
						lCurrentAttribute.oldReceptionStateProperty().setValue(eModelState.NoInformation);
					}

				}

				//Add the attributes as child of the current interaction
				lCurrentInteraction.childrenProperty().add(lCurrentAttribute);
			}

			//Put the newly DataHLA in the hashmap to retrieve it easily
			lDataHLACreated.put(lCurrentInteraction.nameProperty().get(), lCurrentInteraction);
		}

		//Creation of the dependency between interactions
		for (ISimInteractionClass lInteraction : lAllInteractions)
		{
			DataHLA lCurrentChild = lDataHLACreated.get(lInteraction.getName());
			DataHLA lCurrentParent = null;

			if (lInteraction.getParent() == null)
			{
				lCurrentParent = lRootInteraction;
			}
			else
			{
				lCurrentParent = lDataHLACreated.get(lInteraction.getParent().getName());
			}

			lCurrentParent.childrenProperty().add(lCurrentChild);
		}
		return lRootInteraction;
	}

	/**
	 * Get the metadata of an interaction or an entity
	 * @param pMetadataName Name of the metadata searched
	 * @param pEntity Entity which contains the metadata
	 * @return Metadata which represents the metadata or null not found
	 */
	private Metadata getMetaData(String pMetadataName, Element pEntity)
	{
		EList<Metadata> lAllMetaData = pEntity.getMetadatas();
		ListIterator<Metadata> lAllMetaDataIterator = lAllMetaData.listIterator();
		while (lAllMetaDataIterator.hasNext())
		{
			Metadata lCurrentMetaData = lAllMetaDataIterator.next();
			if (lCurrentMetaData.getName().contains(pMetadataName))
			{
				return lCurrentMetaData;
			}
		}

		return null;
	}

	/**
	 * Get the data model of the simulation for objects and interactions
	 * @return DataHLA structure
	 */
	public DataHLA getDataHLA() 
	{
		return mDataHLA;
	}
	/**
	 * Get the data model of the simulation for services
	 * @return ServiceHLA structure
	 */
	public ServiceHLA getServiceHLA() 
	{
		return mServiceHLA;
	}

	/**
	 * Get the data model of the simulation used for the distribution
	 * @return ISimModel structure
	 */
	public ISimModel getSimModelForDistribution() 
	{
		return mDataModelSimulationForDistribution;
	}

	/**
	 * Write message in file report and in console depending parameter pConsole
	 * @param pFW output file
	 * @param pMessage message
	 * @throws IOException I/O error
	 */
	public void WriteMessage(Writer pFW,String pMessage) throws IOException {

		if (pFW != null)
		{
			pFW.write(pMessage);
			if (csVerification) 
				logger.info(pMessage);
		}
	}


}