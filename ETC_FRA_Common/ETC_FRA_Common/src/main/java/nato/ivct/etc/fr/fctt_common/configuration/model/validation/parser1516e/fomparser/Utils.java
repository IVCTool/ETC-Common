package nato.ivct.etc.fr.fctt_common.configuration.model.validation.parser1516e.fomparser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.emf.common.util.EList;

import fr.itcs.sme.architecture.technical.types.IClass;
import fr.itcs.sme.architecture.technical.types.IConstrainedCollection;
import fr.itcs.sme.architecture.technical.types.IEnum;
import fr.itcs.sme.architecture.technical.types.IHomogeneousCollection;
import fr.itcs.sme.architecture.technical.types.IMutant;
import fr.itcs.sme.architecture.technical.types.IMutantAlternative;
import fr.itcs.sme.architecture.technical.types.INative;
import fr.itcs.sme.architecture.technical.types.IType;
import fr.itcs.sme.architecture.technical.types.IValueMember;
import fr.itcs.sme.base.BaseFactory;
import fr.itcs.sme.base.Element;
import fr.itcs.sme.base.Metadata;

/**
 * Utils declaration.
 * 
 */
public class Utils {

	static Metadata temp = null;


	public static Metadata getMetadata(String _name, Element _element) {
		final String name = _name;
		final Element element = _element;
//		TransactionalEditingDomain domain = BaseUtils.getEditingDomain();
//		TransactionalCommandStack stack = (TransactionalCommandStack) domain.getCommandStack();
//		stack.execute(new RecordingCommand(domain) {
//
//			@Override
//			protected void doExecute() {
				temp = null;
				if (element != null) {
					if (element.getMetadatas() != null) {
						for (Metadata meta : element.getMetadatas()) {
							if (meta.getName().equals(name))
								temp = meta;
						}
					}
				}
//			}
//		});
		return temp;
	}


	public static Metadata addMetadata(String _name, String _value, Element _element) {
		final String name = _name;
		final String value = _value;
		final Element element = _element;
//		TransactionalEditingDomain domain = BaseUtils.getEditingDomain();
//		TransactionalCommandStack stack = (TransactionalCommandStack) domain.getCommandStack();
//		stack.execute(new RecordingCommand(domain) {
//
//			@Override
//			protected void doExecute() {
				temp = null;
				if (element != null) {
					temp = getMetadata(name, element);
					if (temp != null) {
						temp.setValue(value);
					} else {
						temp = BaseFactory.eINSTANCE.createMetadata();
						temp.setName(name);
						temp.setValue(value);
						temp.setCategory(Constants.Metadata_Category_HLA_Encoding);
						if (element.getMetadatas() == null) {

						}
						element.getMetadatas().add(temp);
					}
				}
//			}
//		});
		return temp;

	}


	public static void removeMetadata(String _name, Element _element) {
		final String name = _name;
		final Element element = _element;
//		TransactionalEditingDomain domain = BaseUtils.getEditingDomain();
//		TransactionalCommandStack stack = (TransactionalCommandStack) domain.getCommandStack();
//		stack.execute(new RecordingCommand(domain) {
//
//			@Override
//			protected void doExecute() {
				Metadata myMeta = null;
				if (element != null) {
					for (Metadata meta : element.getMetadatas()) {
						if (meta.getName().equals(name)) {
							myMeta = meta;
							break;
						}
					}
					element.getMetadatas().remove(myMeta);
				}
//			}
//		});
	}


//	public static boolean copyFileToImplementationFacet(IPath pPath, IFolder implFolder,
//			IProgressMonitor monitor, ISimModel pModel) {
//		// Copy the parsed assembly in the implementation folder of the
//		// member.
//		try {
//			IFileStore lFile = EFS.getLocalFileSystem().getStore(pPath);
//			IFileStore lDest = EFS.getLocalFileSystem().getStore(
//					implFolder.getFile(pPath.lastSegment()).getLocation());
//			lFile.copy(lDest, EFS.OVERWRITE, monitor);
//			implFolder.refreshLocal(1, monitor);
//
//			// The assembly file name is a parameter of the DirectSim
//			// application.
//			addMetadata("ParsedFileName", lFile.getName(), pModel);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//		return true;
//	}
//
//
//	public static IFolder createFolderInImplementationFacet(String newFolder, IFolder implFolder)
//	{
//		IFolder folder = null;
//		try {
//			folder = implFolder.getFolder(newFolder);
//			IFileStore lDest = EFS.getLocalFileSystem().getStore(folder.getLocation());
//			lDest.mkdir(0, null);
//			implFolder.refreshLocal(IResource.DEPTH_ZERO, null);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//		return folder;
//	}
//

	public static  void resolveDataTypesBoundaries(HashMap<String, IType> allTypes)
	{
		if (allTypes!=null)
		{
			Collection<IType> types=allTypes.values();
			if (types!=null)
			{
				// Here we assume that all basic data representation have been parsed and their Metadata_MarshallingBitBoundary
				// metadata is set

				// Resolve unresolved Enums and SimpleData types first from their representation
				for (Iterator<IType> iterator = types.iterator(); iterator.hasNext();)
				{
					IType iType = iterator.next();

					// Test if we already got the boundary for this type.
					Metadata bitsBoundaryMeta = getMetadata(Constants.Metadata_MarshallingBitBoundary, iType);
					if (bitsBoundaryMeta ==null)
					{
						if ((iType instanceof IEnum) ||(iType instanceof INative))
						{
							resolveDataTypeBoundary(iType, allTypes);
						}
					}
				}			

				// Resolve unresolved other types
				for (Iterator<IType> iterator = types.iterator(); iterator.hasNext();)
				{
					IType iType = iterator.next();

					// Test if we already got the boundary for this type.
					Metadata bitsBoundaryMeta = getMetadata(Constants.Metadata_MarshallingBitBoundary, iType);
					if (bitsBoundaryMeta ==null)
					{
						resolveDataTypeBoundary(iType, allTypes);
					}
				}			
			}
		}
	}


	public static void resolveDataTypeBoundary(IType iType,HashMap<String, IType> allTypes)
	{
		if (allTypes!=null)
		{
			if (iType instanceof IClass)
			{
				resolveClassBoundary((IClass) iType, allTypes);
			}
			else if (iType instanceof IConstrainedCollection)
			{
				resolveCollectionBoundary((IConstrainedCollection) iType, allTypes,0);
			}
			else if (iType instanceof IHomogeneousCollection)
			{
				resolveCollectionBoundary((IHomogeneousCollection) iType, allTypes,32);
			}
			else if (iType instanceof IMutant)
			{
				resolveMutantBoundary((IMutant) iType, allTypes);
			}
			else if (iType instanceof IEnum)
			{
				resolveEnumBoundary((IEnum) iType, allTypes);
			}
			else if (iType instanceof INative)
			{
				resolveNativeBoundary((INative) iType, allTypes);
			}
		}
	}


	public static void resolveNativeBoundary(INative iType, HashMap<String, IType> allTypes)
	{
		if ((iType!=null)&&(allTypes!=null))
		{
			resolveTypeWithRepresentation(iType, allTypes);
		}		
	}


	public static void resolveEnumBoundary(IEnum iType, HashMap<String, IType> allTypes)
	{
		if ((iType!=null)&&(allTypes!=null))
		{
			resolveTypeWithRepresentation(iType, allTypes);
		}		
	}


	private static void resolveTypeWithRepresentation(IType iType, HashMap<String, IType> allTypes) 
	{
		Metadata representation = getMetadata(Constants.Metadata_Representation, iType);
		if ((representation !=null)&&(representation.getValue()!=null))
		{
			IType representationType = allTypes.get(representation.getValue());
			if (representationType!=null)
			{
				Metadata bitsBoundaryMeta = getMetadata(Constants.Metadata_MarshallingBitBoundary, representationType);

				if (bitsBoundaryMeta != null) {
					Utils.addMetadata(Constants.Metadata_MarshallingBitBoundary, bitsBoundaryMeta.getValue(),iType);
				}
			}
			else
			{
				//SendTrace.sendWarning("Error : No representation type named " + representation.getValue());
			}
		}
		else
		{
			//SendTrace.sendWarning("Error : No representation metadata for type " + iType.getName());
		}
	}


	public static void resolveClassBoundary(IClass iType, HashMap<String, IType> allTypes)
	{
		if ((iType!=null)&&(allTypes!=null))
		{
			EList<IValueMember> members = iType.getMembers();
			int boundary = 0;
			for (Iterator<IValueMember> iterator = members.iterator(); iterator.hasNext();) {
				IValueMember iValueMember = iterator.next();

				if (iValueMember != null)
				{
					IType type = iValueMember.getType();
					if (type !=null)
					{
						boundary = resolveSubElementTypeBoundary(type, allTypes,	boundary);
					}
				}
			}
			if (boundary > 0)
			{
				Utils.addMetadata(Constants.Metadata_MarshallingBitBoundary, Integer.toString(boundary),iType);
			}
			else
			{
				SendTrace.sendWarning("Error : Unable to resolve boundary for type " + iType.getFullyQualifiedName());
			}
		}		
	}


	public static void resolveCollectionBoundary(IHomogeneousCollection iType, HashMap<String, IType> allTypes, int minBoundary)
	{
		if ((iType!=null)&&(allTypes!=null))
		{
			int boundary = minBoundary;
			IType elementType = iType.getItemType();
			if (elementType !=null)
			{
				boundary = resolveSubElementTypeBoundary(elementType, allTypes,	boundary);
			}
			if (boundary > 0)
			{
				Utils.addMetadata(Constants.Metadata_MarshallingBitBoundary, Integer.toString(boundary),iType);
			}
		}		
	}


	public static void resolveMutantBoundary(IMutant iType, HashMap<String, IType> allTypes)
	{
		if ((iType!=null)&&(allTypes!=null))
		{
			int boundary = 0;

			//			IEnum choiceType = iType.getChoice();
			//			Metadata bitsBoundaryMeta = getMetadata(Constants.Metadata_MarshallingBitBoundary, choiceType);
			//			if (bitsBoundaryMeta ==null)
			//			{
			//				SendTrace.sendWarning("Error : No boundary for enum type " + choiceType.getFullyQualifiedName());
			//			}
			//			else
			//			{
			//				String value = bitsBoundaryMeta.getValue();
			//				int choiceBoundary = Integer.parseInt(value);
			//				if (choiceBoundary > boundary)
			//				{
			//					boundary = choiceBoundary;
			//				}
			//			}

			EList<IMutantAlternative> alternatives = iType.getAlternatives();
			for (Iterator<IMutantAlternative> iterator = alternatives.iterator(); iterator.hasNext();) {
				IMutantAlternative iAlternative = iterator.next();
				if (iAlternative != null)
				{
					IType type = iAlternative.getType();
					if (type !=null)
					{
						boundary = resolveSubElementTypeBoundary(type, allTypes,	boundary);
					}
				}
			}
			if (boundary > 0)
			{
				Utils.addMetadata(Constants.Metadata_MarshallingBitBoundary, Integer.toString(boundary),iType);
			}
			else
			{
				SendTrace.sendWarning("Error : Unable to resolve boundary for type " + iType.getFullyQualifiedName());
			}
		}		
	}


	private static int resolveSubElementTypeBoundary(IType iType, HashMap<String, IType> allTypes, int currentBoundaryValue) {
		// Test if we already got the boundary for this type.
		Metadata bitsBoundaryMeta = getMetadata(Constants.Metadata_MarshallingBitBoundary, iType);
		if (bitsBoundaryMeta ==null)
		{
			resolveDataTypeBoundary(iType, allTypes);
		}
		bitsBoundaryMeta = getMetadata(Constants.Metadata_MarshallingBitBoundary, iType);
		if (bitsBoundaryMeta ==null)
		{
			SendTrace.sendWarning("Error : Unable to resolve boundary for type " + iType.getFullyQualifiedName());
		}
		else
		{
			String value = bitsBoundaryMeta.getValue();
			int memberBoundary = Integer.parseInt(value);
			if (memberBoundary > currentBoundaryValue)
			{
				currentBoundaryValue = memberBoundary;
			}
		}
		return currentBoundaryValue;
	}
}
