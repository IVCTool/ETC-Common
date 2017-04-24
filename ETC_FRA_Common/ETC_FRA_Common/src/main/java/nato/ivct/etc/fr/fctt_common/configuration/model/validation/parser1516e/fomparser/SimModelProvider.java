package nato.ivct.etc.fr.fctt_common.configuration.model.validation.parser1516e.fomparser;

import java.io.File;
import java.util.HashMap;

import nato.ivct.etc.fr.fctt_common.utils.FCTT_Constant;
import nato.ivct.etc.fr.fctt_common.utils.FCTT_Environment;

import org.eclipse.core.runtime.IPath;

import fr.itcs.sme.architecture.technical.ISimEntityClass;
import fr.itcs.sme.architecture.technical.ISimInteractionClass;
import fr.itcs.sme.architecture.technical.ISimModel;
import fr.itcs.sme.architecture.technical.TechnicalFactory;
import fr.itcs.sme.architecture.technical.types.IType;
import fr.itcs.sme.architecture.technical.types.TypeSystem;
import fr.itcs.sme.architecture.technical.types.TypesFactory;

/**
 * Import the simulation model of 1516 2010 modular FOM
 *
 */
public class SimModelProvider 
{
	/**
	 * The file extension of the HLA IEEE 1516 2010 FOMs.
	 */
	private static String[] mExtensions = { "*.xml" };
	
	/**
	 * List of ISimInteractionClass objects indexed by name.
	 */
	protected HashMap<String, ISimInteractionClass> parsedSimInteractions;

	/**
	 * List of ISimObjectClass objects indexed by name.
	 */
	protected HashMap<String, ISimEntityClass> parsedSimObjects;
	
	private ISimModel domain = null;

	private HashMap<String, IType> allTypes;

	/**
	 * Default constructor.
	 */
	public SimModelProvider() 
	{

	}

	public HashMap<String, ISimEntityClass> getParsedSimObjects() 
	{
		return parsedSimObjects;
	}

	public HashMap<String, ISimInteractionClass> getParsedSimInteractions() 
	{
		return parsedSimInteractions;
	}

	public HashMap<String, IType> getAllTypes() 
	{
		return allTypes;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fr.itcs.sme.architecture.api.ISimModelProvider#getExtensions()
	 */
	public String[] getExtensions() 
	{
		return mExtensions;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fr.itcs.sme.architecture.api.ISimModelProvider#isMany()
	 */
	public boolean isMany() 
	{
		// Can handle several files for the modular FOM support
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * fr.itcs.sme.architecture.api.ISimModelProvider#parse(org.eclipse.core
	 * .runtime.IPath[], org.eclipse.core.runtime.IProgressMonitor)
	 */
	public ISimModel parse(IPath[] inputs ,IPath mergedFile, boolean pResolveType) throws Exception
	{
		allTypes = new HashMap<String, IType>();
		parsedSimObjects = new HashMap<String, ISimEntityClass>();
		parsedSimInteractions = new HashMap<String, ISimInteractionClass>();

		setDomain(TechnicalFactory.eINSTANCE.createISimModel());
		TypeSystem lTypeSystem = TypesFactory.eINSTANCE.createTypeSystem();
		lTypeSystem.setName("HLA1516e-TypeSystem");
		getDomain().setTypeSystem(lTypeSystem);
		// Create a domain.
		getDomain().setName("HLA IEEE 1516e");
		getDomain().setDescription("HLA 1516e Imported Domain");

		// Here we merge the modules to import (and it also validate them)
		File mergedFOMFile = mergeFOMModules(inputs,mergedFile, FCTT_Environment.getXSD_DIF_Path().toFile());

		File dir = getInputsDir(inputs);

		// Create the Hla1516e FOM reader.
		FDD1516EvolvedReader fomReader = new FDD1516EvolvedReader(getDomain(), allTypes, parsedSimObjects,parsedSimInteractions);

		fomReader.read(dir, mergedFOMFile, pResolveType);

		if (getDomain() != null) 
		{
			getDomain().getEntities().addAll(parsedSimObjects.values());
			getDomain().getInteractions().addAll(parsedSimInteractions.values());
		}
		
		return getDomain();
	}

	public File mergeFOMModules(IPath[] inputs, IPath mergedFile, File pSchemaFile) throws Exception
	{
		if ((inputs!=null)&&(inputs.length>0))
		{
			if (inputs.length == 1)
			{
				return new File(inputs[0].toOSString());
			}
			else
			{
				FDD1516EvolvedMerger merger = new FDD1516EvolvedMerger(new File(inputs[0].toOSString()),pSchemaFile);
				for (int i = 1; i < inputs.length; i++) 
				{
					merger.merge(new File(inputs[i].toOSString()));
				}
				File mergedFOMFile = new File(mergedFile.toOSString());
				merger.saveAs(mergedFOMFile);
				return mergedFOMFile;
			}
		}
		else 
		{
			return null;
		}
	}

	/**
	 * Returns the directory containing the inputs paths.
	 *
	 * @param inputs the input paths
	 * @return the directory containing the input paths
	 */
	private File getInputsDir(IPath[] inputs)
	{
		if (inputs != null && inputs.length > 0)
		{
			IPath input = inputs[0];
			File file = new File(input.toOSString());
			return file.getParentFile();
		}
		else
		{
			return null;
		}
	}

	public ISimModel getDomain() 
	{
		return domain;
	}

	public void setDomain(ISimModel domain) 
	{
		this.domain = domain;
	}
}