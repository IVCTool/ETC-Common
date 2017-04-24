package nato.ivct.etc.fr.fctt_common.federate;

import fr.itcs.sme.architecture.technical.ISimAttribute;
import fr.itcs.sme.architecture.technical.ISimEntityClass;
import fr.itcs.sme.architecture.technical.ISimInteractionClass;
import fr.itcs.sme.architecture.technical.ISimModel;

import de.fraunhofer.iosb.tc_lib.IVCT_RTIambassador;

import hla.rti1516e.AttributeHandle;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.exceptions.FederateNotExecutionMember;
import hla.rti1516e.exceptions.InvalidInteractionClassHandle;
import hla.rti1516e.exceptions.InvalidObjectClassHandle;
import hla.rti1516e.exceptions.NameNotFound;
import hla.rti1516e.exceptions.NotConnected;
import hla.rti1516e.exceptions.RTIinternalError;

import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.emf.common.util.EList;

public class FCTTHandleList 
{

	/**
	 * ISimModel
	 */
	private ISimModel mModel = null;

	/**
	 * The RTIAmbassador
	 */
	private IVCT_RTIambassador mRTIAmb = null;

	/**
	 * HashMap containing the handle corresponding to an object class name
	 */
	private HashMap<String, String> mHandleObj = null;

	/**
	 * HashMap containing the handle corresponding to an interaction class name
	 */
	private HashMap<String, String> mHandleInt = null;

	/**
	 * HashMap containing the handle corresponding to an attribute of an object class name
	 */
	private HashMap<HashSet<String>, String> mHandleAttribute = null;

	/**
	 * HashMap containing the handle corresponding to a parameter of an interaction class name
	 */
	private HashMap<HashSet<String>, String> mHandleParameter = null;

	/**
	 * @param pModel HLA data model
	 * @param pRTIAmb RTI ambassador
	 */
	public FCTTHandleList(ISimModel pModel, IVCT_RTIambassador pRTIAmb) 
	{
		super();
		this.mModel = pModel;
		this.mRTIAmb = pRTIAmb;
		
		mHandleObj = new HashMap<String, String>();
		mHandleInt = new HashMap<String, String>();
		mHandleAttribute = new HashMap<HashSet<String>, String>();
		mHandleParameter = new HashMap<HashSet<String>, String>();
	}

	/**
	 *
	 * @return the HashMap containing the handle corresponding to an object class name
     */
	public HashMap<String, String> getHandleObj()
	{
		return mHandleObj;
	}

	/**
	 *
	 * @return the HashMap containing the handle corresponding to an interaction class name
     */
	public HashMap<String, String> getHandleInt()
	{
		return mHandleInt;
	}

	/**
	 *
	 * @return the HashMap containing the handle corresponding to an attribute of an object class name
     */
    public HashMap<HashSet<String>, String> getHandleAttribute()
	{
		return mHandleAttribute;
	}

	/**
	 *
	 * @return the HashMap containing the handle corresponding to a parameter of an interaction class name
     */
    public HashMap<HashSet<String>, String> getHandleParameter()
	{
		return mHandleParameter;
	}

	/**
	 * 
	 */
	public void readHandle()
	{
		getObjectHandle();
		getInteractionHandle();
	}

	/**
	 *
	 */
	private void getObjectHandle()
	{
		EList<ISimEntityClass> lAllObjects = mModel.getEntities();
		//For each object class
		for (ISimEntityClass lObject : lAllObjects)
		{
			String lObjectClassName = lObject.getFullyQualifiedName();
			ObjectClassHandle lObjectClassHandle = null;
			try {
				lObjectClassHandle = mRTIAmb.getObjectClassHandle(lObjectClassName);
				mHandleObj.put(lObjectClassHandle.toString(), lObjectClassName);

				//For each attribute of the current objet class
				for (ISimAttribute lAttribute : lObject.getAllAttributes())
				{
					String lAttClassName = lAttribute.getName();
					AttributeHandle lAttributeHandle = null;
					try
					{
						lAttributeHandle = mRTIAmb.getAttributeHandle(lObjectClassHandle, lAttClassName);
						HashSet<String> lHashSet = new HashSet<String>();
						lHashSet.add(lObjectClassName);
						lHashSet.add(lAttributeHandle.toString());
						mHandleAttribute.put(lHashSet, lAttClassName);
					}
					catch (NameNotFound e)
					{
						e.printStackTrace();
					}
					catch (InvalidObjectClassHandle e)
					{
						e.printStackTrace();
					}
					catch (FederateNotExecutionMember e)
					{
						e.printStackTrace();
					}
					catch (NotConnected e)
					{
						e.printStackTrace();
					}
					catch (RTIinternalError e)
					{
						e.printStackTrace();
					}

				}
			}
			catch (NameNotFound e)
			{
				e.printStackTrace();
			}
			catch (FederateNotExecutionMember e)
			{
				e.printStackTrace();
			}
			catch (NotConnected e)
			{
				e.printStackTrace();
			}
			catch (RTIinternalError e)
			{
				e.printStackTrace();
			}

		}

	}

	/**
	 * 
	 */
	private void getInteractionHandle()
	{
		EList<ISimInteractionClass> lAllInteractions = mModel.getInteractions();
		//For each interaction
		for (ISimInteractionClass lInteraction : lAllInteractions)
		{
			String lInterClassName = lInteraction.getFullyQualifiedName();
			InteractionClassHandle lInterClassHandle = null;
			try
			{
				lInterClassHandle =  mRTIAmb.getInteractionClassHandle(lInterClassName);
				mHandleInt.put(lInterClassHandle.toString(), lInterClassName);

				//For each attribute of the current interaction
				for (ISimAttribute lAttribute : lInteraction.getAllParameters())
				{
					String lParamClassName = lAttribute.getName();
					ParameterHandle lParameterHandle = null;
					try
					{
						lParameterHandle = mRTIAmb.getParameterHandle(lInterClassHandle, lParamClassName);
						HashSet<String> lHashSet = new HashSet<String>();
						lHashSet.add(lInterClassName);
						lHashSet.add(lParameterHandle.toString());
						mHandleParameter.put(lHashSet, lParamClassName);
					}
					catch (NameNotFound e)
					{
						e.printStackTrace();
					}
					catch (InvalidInteractionClassHandle e)
					{
						e.printStackTrace();
					}
					catch (FederateNotExecutionMember e)
					{
						e.printStackTrace();
					}
					catch (NotConnected e)
					{
						e.printStackTrace();
					}
					catch (RTIinternalError e)
					{
						e.printStackTrace();
					}
				}
			}
			catch (NameNotFound e)
			{
				e.printStackTrace();
			}
			catch (FederateNotExecutionMember e)
			{
				e.printStackTrace();
			}
			catch (NotConnected e)
			{
				e.printStackTrace();
			}
			catch (RTIinternalError e)
			{
				e.printStackTrace();
			}

		}
	}


	/**
	 *
	 * @param pParam Interaction to search for
	 * @return Interaction class name
     */
	public String getInteractionClassName(String pParam)
	{
		String lReturnValue="";
		if (mHandleInt.containsKey(pParam))
		{
			lReturnValue = mHandleInt.get(pParam);
		}
		else
		{
			if (mHandleInt.containsValue(pParam))
			{
				lReturnValue = pParam;
			}
		}
		return lReturnValue;
	}

	/**
	 *
	 * @param pParam Object to search for
	 * @return Object class name
	 */
	public String getObjectClassName(String pParam)
	{
		String lReturnValue="";
		if (mHandleObj.containsKey(pParam))
		{
			lReturnValue = mHandleObj.get(pParam);
		}
		else
		{
			if (mHandleObj.containsValue(pParam))
			{
				lReturnValue = pParam;
			}
		}
		return lReturnValue;
	}

	/**
	 *
	 * @param pObjectClassName Object class for attribute to search for
	 * @param pParam Attribute to search for
	 * @return Attribute class name
	 */
	public String getAttributeClassName(String pObjectClassName, String pParam)
	{
		String lReturnValue="";

		if (mHandleAttribute.containsValue(pParam))
		{
			lReturnValue = pParam;
		}
		else
		{
			HashSet<String> lHashSet = new HashSet<String>();
			lHashSet.add(pObjectClassName);
			lHashSet.add(pParam);
			if (mHandleAttribute.containsKey(lHashSet))
			{
				lReturnValue = mHandleAttribute.get(lHashSet);
			}
		}

		return lReturnValue;
	}

}
