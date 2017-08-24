package nato.ivct.etc.fr.fctt_common.configuration.controller.validation;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderJDOMFactory;
import org.jdom2.input.sax.XMLReaderXSDFactory;
import org.slf4j.Logger;

import fr.itcs.sme.architecture.technical.ISimAttribute;
import fr.itcs.sme.architecture.technical.ISimEntityClass;
import fr.itcs.sme.architecture.technical.ISimInteractionClass;
import fr.itcs.sme.architecture.technical.ISimModel;
import fr.itcs.sme.base.Metadata;
import nato.ivct.etc.fr.fctt_common.configuration.model.validation.FCTTParserServices;
import nato.ivct.etc.fr.fctt_common.configuration.model.validation.parser1516e.Pair;
import nato.ivct.etc.fr.fctt_common.configuration.model.validation.parser1516e.fomparser.Utils;
import nato.ivct.etc.fr.fctt_common.resultServices.model.ServiceHLA;
import nato.ivct.etc.fr.fctt_common.utils.FCTT_Constant;
import nato.ivct.etc.fr.fctt_common.utils.FCTT_Enum.eModelState;

/**
 * This class parse the SOM 
 */
public class FCTTSOMParser 
{
	/**
	 * The ISimModel
	 */
	private ISimModel mDomain;

	/**
	 * Namespace of the XML file
	 */
	private Namespace mNameSpace ;

	/**
	 * HashMap containing all the entities
	 */
	private HashMap<String,ISimEntityClass> mAllEntities;

	/**
	 * HashMap containing all the interactions
	 */
	private HashMap<String,ISimInteractionClass> mAllInteractions;

	/**
	 * HashMap containing the list of object not include in FOM
	 */
	private ArrayList<String> mListObjNotInFOM;

	/**
	 * HashMap containing the list of attribut not include in FOM
	 */
	private ArrayList<String> mListAttributNotInFOM;

	/**
	 * HashMap containing the list of interaction not include in FOM
	 */
	private ArrayList<String> mListInterNotInFOM;

	/**
	 * HashMap containing the list of parameter not include in FOM
	 */
	private ArrayList<String> mListParameterNotInFOM;

	/**
	 * HashMap containing the list of service with the state
	 * 
	 */
	private HashMap<String,eModelState> mListServices;

	/**
	 * HashMap containing 
	 * 
	 */
	private HashMap<String, Pair<String,String>> mListSharDiff;


	/**
	 * Constructor
	 * @param pdomain The ISimModel
	 * @param pallEntities HashMap containing the entities
	 * @param pallInteractions HashMap containing the interactions
	 */
	public FCTTSOMParser(ISimModel pdomain, HashMap<String,ISimEntityClass> pallEntities, HashMap<String,ISimInteractionClass> pallInteractions) 
	{
		mListObjNotInFOM = new ArrayList<String>();
		mListInterNotInFOM = new ArrayList<String>();
		mListAttributNotInFOM = new ArrayList<String>();
		mListParameterNotInFOM = new ArrayList<String>();
		mDomain = pdomain;

		mAllEntities=pallEntities;
		mAllInteractions=pallInteractions;

		mListServices = new HashMap<String, eModelState>();
		mListSharDiff = new HashMap<String, Pair<String,String>>();
	}

	/**
	 * Read the file containing the services
	 * @param logger Logger for parse error
	 * @return data model of the simulation for the services as ServiceHLA object
	 * @throws IOException I/O error
	 */
	public ServiceHLA readServicesList(Logger logger) throws IOException 
	{
		FCTTParserServices lFCTTParserServices = new FCTTParserServices(mListServices);
		ServiceHLA lServiceModelSimulation = lFCTTParserServices.readFile(logger);

		return lServiceModelSimulation;
	}

	/**
	 * Parsing the SOM File
	 * @param pFileSOM SOM file name
	 * @throws IOException I/O exception
	 * @throws JDOMException JDOM exception
	 * @throws MalformedURLException URL exception 
	 */
	public void parsingSOM(String pFileSOM) throws MalformedURLException, JDOMException, IOException 
	{
		List<String> lListString = new ArrayList<String>();
		lListString.add(pFileSOM);
		parsingSOM(lListString);
	}

	/**
	 * Parsing a list of SOM file
	 * @param inputs List of SOM file
	 * @throws IOException I/O exception
	 * @throws JDOMException JDOM exception
	 * @throws MalformedURLException URL exception 
	 */
	public void parsingSOM(List<String> inputs) throws MalformedURLException, JDOMException, IOException 
	{
		Document doc = new Document();
		for (String lPath : inputs)
		{
// 2017/08/21 RMA Begin modification
// In order to avoid using resource file in bin/resources directory and using file in src/main/resources directory
//			File lxsdfile = FCTT_Environment.getXSD_FCTT_Path().toFile();
//			XMLReaderJDOMFactory factory = new XMLReaderXSDFactory(lxsdfile);
			URL lxsdURL = this.getClass().getClassLoader().getResource(FCTT_Constant.FILENAME_XSD_FCTT_1516_2010);
			XMLReaderJDOMFactory factory = new XMLReaderXSDFactory(lxsdURL);
// 2017/08/21 RMA End modification
			SAXBuilder sb = new SAXBuilder(factory);
			doc = sb.build(new File(lPath));	

			mNameSpace = doc.getRootElement().getNamespace();

			if (doc.getRootElement().getChild("serviceUtilization",mNameSpace)!=null)
			{
				List<Element> services=doc.getRootElement().getChild("serviceUtilization",mNameSpace).getChildren();
				parsingSOMServices(services, null, mDomain);
			}

			if (doc.getRootElement().getChild("objects",mNameSpace)!=null)
			{
				Element objects=doc.getRootElement().getChild("objects",mNameSpace).getChild("objectClass",mNameSpace);
				parsingSOMObjectClass(objects, null, mDomain);
			}

			if (doc.getRootElement().getChild("interactions",mNameSpace)!=null)
			{
				Element interactions =doc.getRootElement().getChild("interactions",mNameSpace).getChild("interactionClass",mNameSpace);			
				parsingSOMInteractionClass(interactions, null, mDomain);
			}

		}
	}

	/**
	 * Parsing SOM services
	 * @param services List of services
	 * @param object Object
	 * @param domain2 Domain
	 */
	private void parsingSOMServices(List<Element> services, Object object, ISimModel domain2) 
	{
		for(Element lService:services)
		{
			String lServiceName=lService.getName();
			String lIsUsed=lService.getAttributeValue("isUsed");

			if (lIsUsed.equals("true"))
			{
				mListServices.put(lServiceName, eModelState.ExpectedNotSeen);
			}
			else
			{
				mListServices.put(lServiceName, eModelState.NotExpectedNotSeen);
			}
		}
	}

	/**
	 * Add metaData
	 * @param xmlObject Object
	 * @param pType Element type
	 */
	private void addMetadataSharing(Element xmlObject, fr.itcs.sme.base.Element pType) 
	{
		String lSharingValue=xmlObject.getChildText("sharing", mNameSpace);
		if (lSharingValue!=null)
		{
			Utils.addMetadata("sharing", lSharingValue, pType);
		}
	}

	/**
	 * Parsing object class
	 * @param xmlObject Object
	 * @param parentClass Parent class
	 * @param domainLocale Domain
	 */
	private void parsingSOMObjectClass(Element xmlObject, ISimEntityClass parentClass, ISimModel domainLocale)
	{
		ISimEntityClass entityClass=mAllEntities.get(xmlObject.getChildText("name", mNameSpace));
		if (entityClass!=null)
		{
			// Add metadata to know if an object class is declared in SOM
			Utils.addMetadata(FCTT_Constant.METADATA_ELEMENT_IN_SOM, FCTT_Constant.VALUE_ELEMENT_PRESENT_IN_SOM, entityClass);
			
			TestSharingIn(entityClass.getName(), entityClass.getMetadatas(), null,xmlObject);
			addMetadataSharing(xmlObject, entityClass);

			ArrayList<Element> attList=new ArrayList<Element>(xmlObject.getChildren("attribute",mNameSpace));
			for (Element att: attList) 
			{
				boolean lfindAtt = false;
				for (ISimAttribute lAttribute:entityClass.getAttributes())
				{
					if (lAttribute.getName().equals(att.getChildText("name",mNameSpace)))
					{
						TestSharingIn(entityClass.getName(), entityClass.getMetadatas(), lAttribute, att);
						addMetadataSharing(att, lAttribute);		
						lfindAtt = true;
						break;
					}
				}
				if (!lfindAtt) 
				{
					mListAttributNotInFOM.add(xmlObject.getChildText("name", mNameSpace) + " " + att.getChildText("name",mNameSpace));
				}
			}

			ArrayList<Element> subObjectList= new ArrayList<Element>(xmlObject.getChildren("objectClass", mNameSpace));
			for (Element subObj:subObjectList) 
			{
				parsingSOMObjectClass(subObj, entityClass, domainLocale);
			}
		}
		else
		{
			mListObjNotInFOM.add(xmlObject.getChildText("name", mNameSpace));
		}
	}

	/**
	 * Parsing interaction class
	 * @param xmlObject Object
	 * @param parentClass Parent class
	 * @param domainLocale Domain
	 */
	private void parsingSOMInteractionClass(Element xmlObject, ISimInteractionClass parentClass, ISimModel domainLocale)
	{
		ISimInteractionClass interactionClass=mAllInteractions.get(xmlObject.getChildText("name", mNameSpace));
		if (interactionClass!=null)
		{
			// Add metadata to know if an interaction class is declared in SOM
			Utils.addMetadata(FCTT_Constant.METADATA_ELEMENT_IN_SOM, FCTT_Constant.VALUE_ELEMENT_PRESENT_IN_SOM, interactionClass);

			TestSharingIn(interactionClass.getName(), interactionClass.getMetadatas(), null,xmlObject);
			addMetadataSharing(xmlObject, interactionClass);

			ArrayList<Element> attList=new ArrayList<Element>(xmlObject.getChildren("parameter",mNameSpace));
			for (Element att: attList) {
				boolean lfindParam = false;
				for (ISimAttribute lParameter:interactionClass.getParameters())
				{
					if (lParameter.getName().equals(att.getChildText("name",mNameSpace)))
					{
						TestSharingIn(interactionClass.getName(), null, lParameter, att);
						addMetadataSharing(xmlObject, lParameter);		
						lfindParam = true;
						break;
					}
				}
				if (!lfindParam) 
				{
					mListParameterNotInFOM.add(xmlObject.getChildText("name", mNameSpace) + " " + att.getChildText("name",mNameSpace));
				}
			}

			ArrayList<Element> subObjectList= new ArrayList<Element>(xmlObject.getChildren("interactionClass", mNameSpace));
			for (Element subObj:subObjectList) 
			{
				parsingSOMInteractionClass(subObj, interactionClass, domainLocale);
			}

		} 
		else 
		{
			mListInterNotInFOM.add(xmlObject.getChildText("name", mNameSpace));
		}
	}

	/**
	 * @return the list of object not include in FOM
	 */
	public ArrayList<String> getMlistObjNotInFOM() 
	{
		return mListObjNotInFOM;
	}

	/**
	 * @return the list of interaction not include in FOM
	 */
	public ArrayList<String> getMlistInterNotInFOM() 
	{
		return mListInterNotInFOM;
	}

	/**
	 * @return the list of attribute not include in FOM
	 */
	public ArrayList<String> getMlistAttributNotInFOM() 
	{
		return mListAttributNotInFOM;
	}

	/**
	 * @return the list of parameter not include in FOM
	 */
	public ArrayList<String> getMListParameterNotInFOM() 
	{
		return mListParameterNotInFOM;
	}

	/**
	 * @return true if at least one element in SOM is not in FOM
	 */
	public boolean IsElemNotInFom() 
	{
		boolean lReturnValue = false;
		if ( (mListInterNotInFOM.size() > 0) 
				|| (mListObjNotInFOM.size() > 0) 
				|| (mListAttributNotInFOM.size() > 0) 
				|| (mListParameterNotInFOM.size() > 0)) 
		{
			lReturnValue = true;
		}
		else
		{
			lReturnValue = false;

		}
		return lReturnValue;
	}

	/**
	 * @param pFOMSharing FOM sharing state
	 * @param pSOMSharing SOM sharing state
	 * @return sharing state
	 */
	private boolean IsSharingIn(String pFOMSharing, String pSOMSharing) 
	{
		boolean lRet = false;

		if (pFOMSharing.equals(FCTT_Constant.SHARE_NEITHER)) 
		{
			if (pSOMSharing.equals(FCTT_Constant.SHARE_NEITHER))
			{
				lRet=true;
			}
		} 
		else if (pFOMSharing.equals(FCTT_Constant.SHARE_SUBSCRIBE))
		{
			if (pSOMSharing.equals(FCTT_Constant.SHARE_NEITHER) || pSOMSharing.equals(FCTT_Constant.SHARE_SUBSCRIBE))
			{
				lRet=true;
			}
		} 
		else if (pFOMSharing.equals(FCTT_Constant.SHARE_PUBLISH))
		{
			if (pSOMSharing.equals(FCTT_Constant.SHARE_NEITHER) || pSOMSharing.equals(FCTT_Constant.SHARE_PUBLISH))
			{
				lRet=true;
			}

		} 
		else if (pFOMSharing.equals(FCTT_Constant.SHARE_PUBLISH_SUBSCRIBE)) 
		{
			lRet=true;

		}
		return lRet;

	}

	/**
	 * @param pName Name
	 * @param pMeta Meta-data
	 * @param pAttribute Attribute
	 * @param pXMLObject Object
	 */
	private void TestSharingIn(String pName, EList<Metadata> pMeta, ISimAttribute pAttribute, Element pXMLObject) 
	{
		if (pMeta !=null) {
			if (pMeta.size() > 0)
			{
				if (pAttribute !=null)
				{
					String lFOM = pAttribute.getMetadatas().get(0).getValue();
					String lSOM = pXMLObject.getChildText("sharing", mNameSpace);
					if ((lFOM != null) && (lSOM !=null))
						if (!IsSharingIn(lFOM, lSOM))
						{
							mListSharDiff.put(pName + "."+ pAttribute.getName(), new Pair<String, String>(lFOM,lSOM));
						}

				}
				else
				{
					String lFOM = pMeta.get(0).getValue();
					String lSOM = pXMLObject.getChildText("sharing", mNameSpace);
					if ((lFOM != null) && (lSOM !=null))
						if (!IsSharingIn(lFOM, lSOM))
						{
							mListSharDiff.put(pName, new Pair<String, String>(lFOM,lSOM));
						}

				}
			}
		}
	}

	/**
	 * @return true if at least one sharing element is not correct
	 */
	public boolean IsSharingOK() 
	{
		boolean lReturnValue = false;
		if ( mListSharDiff.size() > 0)  
		{
			lReturnValue = false;
		}
		else
		{
			lReturnValue = true;

		}
		return lReturnValue;
	}

	/**
	 * @return the sharing list errors
	 */
	public HashMap<String, Pair<String, String>> getListSharDiff() 
	{
		return mListSharDiff;
	}

	/**
	 * @return SimModel
	 */
	public ISimModel getmDomain() 
	{
		return mDomain;
	}
}