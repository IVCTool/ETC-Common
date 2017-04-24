package nato.ivct.etc.fr.fctt_common.configuration.model.validation.parser1516e.fomparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.emf.common.util.EList;

import fr.itcs.sme.architecture.ArchitecturalElement;
import fr.itcs.sme.architecture.technical.ISimAttribute;
import fr.itcs.sme.architecture.technical.ISimEntityClass;
import fr.itcs.sme.architecture.technical.ISimInteractionClass;
import fr.itcs.sme.architecture.technical.ISimModel;
import fr.itcs.sme.architecture.technical.TechnicalFactory;
import fr.itcs.sme.architecture.technical.types.IClass;
import fr.itcs.sme.architecture.technical.types.IConstrainedCollection;
import fr.itcs.sme.architecture.technical.types.IEnum;
import fr.itcs.sme.architecture.technical.types.IEnumConstant;
import fr.itcs.sme.architecture.technical.types.IHomogeneousCollection;
import fr.itcs.sme.architecture.technical.types.IMutant;
import fr.itcs.sme.architecture.technical.types.IMutantAlternative;
import fr.itcs.sme.architecture.technical.types.INative;
import fr.itcs.sme.architecture.technical.types.INativeEnum;
import fr.itcs.sme.architecture.technical.types.IType;
import fr.itcs.sme.architecture.technical.types.IValueMember;
import fr.itcs.sme.architecture.technical.types.TypesFactory;
import fr.itcs.sme.base.Metadata;

/**
 * Reads a FOM encoded in the HLA 1516 2010 format (also called FDD in this case) and translates it into SA types.
 */
public class FDD1516EvolvedReader
{
	//private static final String modelIdentificationString = "modelIdentification"; //$NON-NLS-1$
	private static final String nameString = "name"; //$NON-NLS-1$
	private static final String semanticsString = "semantics"; //$NON-NLS-1$
	private static final String basicDataRepresentationsString = "basicDataRepresentations"; //$NON-NLS-1$
	private static final String interactionsString = "interactions"; //$NON-NLS-1$
	private static final String objectsString = "objects"; //$NON-NLS-1$
	private static final String variantRecordDataTypesString = "variantRecordDataTypes"; //$NON-NLS-1$
	private static final String fixedRecordDataTypesString = "fixedRecordDataTypes"; //$NON-NLS-1$
	private static final String enumeratedDataTypesString = "enumeratedDataTypes"; //$NON-NLS-1$
	private static final String arrayDataTypesString = "arrayDataTypes"; //$NON-NLS-1$
	private static final String simpleDataTypesString = "simpleDataTypes"; //$NON-NLS-1$
	private static final String dataTypesString = "dataTypes"; //$NON-NLS-1$
	private static final String basicDataString = "basicData"; //$NON-NLS-1$
	private static final String basicDataEndianString = "endian"; //$NON-NLS-1$
	private static final String basicDataSizeString = "size"; //$NON-NLS-1$
	private static final String basicDataInterpretationString = "interpretation"; //$NON-NLS-1$
	private static final String basicDataEncodingString = "encoding"; //$NON-NLS-1$
	private static final String simpleDataString = "simpleData"; //$NON-NLS-1$
	private static final String simpleDataRepresentationString = "representation"; //$NON-NLS-1$
	private static final String enumeratedDataString = "enumeratedData"; //$NON-NLS-1$
	private static final String enumeratorString = "enumerator"; //$NON-NLS-1$
	private static final String enumeratorValueString = "value"; //$NON-NLS-1$
	private static final String cardinalityString = "cardinality"; //$NON-NLS-1$
	private static final String arrayDataString = "arrayData"; //$NON-NLS-1$
	private static final String objectClassString = "objectClass"; //$NON-NLS-1$
	private static final String attributeString = "attribute"; //$NON-NLS-1$
	private static final String fixedRecordDataString = "fixedRecordData"; //$NON-NLS-1$
	private static final String fieldString = "field"; //$NON-NLS-1$
	private static final String interactionClassString = "interactionClass"; //$NON-NLS-1$
	private static final String parameterString = "parameter"; //$NON-NLS-1$
	private static final String dataTypeString = "dataType"; //$NON-NLS-1$
	private static final String sharingString = "sharing"; //$NON-NLS-1$
	private static final String variantRecordDataString = "variantRecordData"; //$NON-NLS-1$
	private static final String discriminantString = "discriminant"; //$NON-NLS-1$
	private static final String alternativeString = "alternative"; //$NON-NLS-1$

	// Not used yet
	//List<String> attributesRead = null;

	/**
	 * Hash map of all types.
	 */
	private HashMap<String, IType> allTypes;

	/**
	 * Map of unresolved types.
	 */
	private ArrayList<IHomogeneousCollection> unresolvedTypes = new ArrayList<IHomogeneousCollection>();

	private ArrayList<String> unresolvedTypeName = new ArrayList<String>();


	/**
	 * Map of unresolved attributes types.
	 */
	private ArrayList<ISimAttribute> unresolvedAttributes = new ArrayList<ISimAttribute>();


	private ArrayList<String> unresolvedAttrName = new ArrayList<String>();


	private ArrayList<IValueMember> unresolvedFields = new ArrayList<IValueMember>();


	private ArrayList<String> unresolvedFieldsName = new ArrayList<String>();


	/**
	 * Mapping for array types with encoding 'RPRlengthlessArray' with variable size.<br>
	 * (Entity or Interaction name -> array type name -> length attribute name)
	 */
	private Map<String, Map<String, String>> _rprLengthlessArrayMapping = new HashMap<String, Map<String,String>>();


	/**
	 * Reference to base factory.
	 */
	private TypesFactory baseFactory;


	/**
	 * The domain to fill with types.
	 */
	private ISimModel domain;


	/**
	 * The objects list to fill with classes, indexed by name.
	 */
	private Map<String, ISimEntityClass> simObjects;


	/**
	 * The objects list to fill with interactions, indexed by name.
	 */
	private Map<String, ISimInteractionClass> simInteractions;


	public FDD1516EvolvedReader(ISimModel domain,
			HashMap<String, IType> pallTypes,
			Map<String, ISimEntityClass> simObjects,
			Map<String, ISimInteractionClass> simInteractions)
	{
		baseFactory = TypesFactory.eINSTANCE;
		this.domain = domain;
		this.simObjects = simObjects;
		this.simInteractions = simInteractions;
		this.allTypes = pallTypes;
	}
	/**
	 * Reads a FOM from a given URL.
	 *
	 * @param dir the directory containing the inputs FOM files
	 * @param file A FOM file
	 * @param pResolveType Resolve type
	 * @throws Exception Exception
	 */
	public void read(File dir, File file, boolean pResolveType) throws Exception
	{
		Element eFDD = new SAXReader().read(new FileInputStream(file)).getRootElement();

		//readModelIdentification(eFDD.element(modelIdentificationString));

		Element eDataTypes = eFDD.element(dataTypesString);
		readBasicDataRepresentations(eDataTypes.element(basicDataRepresentationsString));
		readSimpleDataTypes(eDataTypes.element(simpleDataTypesString));
		readArrays(eDataTypes.element(arrayDataTypesString));
		readEnums(eDataTypes.element(enumeratedDataTypesString));
		readStructs(eDataTypes.element(fixedRecordDataTypesString));
		readVariants(eDataTypes.element(variantRecordDataTypesString));
		readObjectClasses(eFDD.element(objectsString), null);
		readInteractions(eFDD.element(interactionsString), null);

		if (pResolveType) 
		{
			resolveDataTypes();
			Utils.resolveDataTypesBoundaries(allTypes);
			resolveMappings(dir);
		}

	}

	public Metadata getMetadata(String name, fr.itcs.sme.base.Element element) {
		if (element!=null)
		{
			for (Metadata meta : element.getMetadatas()) {
				if (meta.getName().equals(name))
					return meta;
			}
		}
		return null;
	}

	private String getSubElementValue(Element eType, String elementName, String subElementName, boolean isNeeded)
	{
		String retour = eType.elementText(subElementName);

		//attributesRead.add(subElementName);

		if ((isNeeded) && (retour == null)) {
			SendTrace.sendWarning("Error : No sub-element " + subElementName + " in " + elementName);
		}
		return retour;
	}

	//	private void readModelIdentification(Element element) {
	//		String name = element.elementText(nameString);
	//		domain.setName(name);
	//	}


	/**
	 * Reads the basic data representation.
	 *
	 * @param eTypes The element which contains all the basic data representations.
	 */
	private void readBasicDataRepresentations(Element eTypes)
	{
		if (eTypes != null)
		{
			Iterator<?> iter = eTypes.elementIterator(basicDataString);
			while (iter.hasNext())
			{
				Element eType = (Element) iter.next();
				String name = eType.elementText(nameString);

				if (!allTypes.containsKey(name)) {
					INative basicData = baseFactory.createINative();
					basicData.setName(name);
					basicData.setFullyQualifiedName(name);
					basicData.setDescription(getSubElementValue(eType,name, semanticsString, false));

					String size = getSubElementValue(eType,name, basicDataSizeString, true); // Set size to true as it's necessary for marshalling
					Utils.addMetadata(Constants.Metadata_MarshallingBitBoundary, size, basicData);

					//attributesRead.add(semanticsString);

					HLA1516JavaEquivalence.set(basicData, getSubElementValue(eType,name,basicDataEndianString,true));

					//attributesRead.add(basicDataEndianString);

					allTypes.put(name, basicData);
					domain.getTypeSystem().getTypes().add(basicData);

					// Add metadata for specific types in RPR-FOM V2 D17 (ITCS)
					if (basicData.getFullyQualifiedName().equals("Unsignedinteger16BE"))
						Utils.addMetadata(Constants.Metadata_MarshallingClass, "Unsignedinteger16BE", basicData);
					if (basicData.getFullyQualifiedName().equals("Unsignedinteger32BE"))
						Utils.addMetadata(Constants.Metadata_MarshallingClass, "Unsignedinteger32BE", basicData);


					// Add metadata for specific type in FOM NETN (ELLIPSE)
					if (basicData.getFullyQualifiedName().equals("UnsignedInteger16BE"))
						Utils.addMetadata(Constants.Metadata_MarshallingClass, "Unsignedinteger16BE", basicData);
					if (basicData.getFullyQualifiedName().equals("UnsignedInteger32BE"))
						Utils.addMetadata(Constants.Metadata_MarshallingClass, "Unsignedinteger32BE", basicData);
					if (basicData.getFullyQualifiedName().equals("UnsignedInteger64BE"))
						Utils.addMetadata(Constants.Metadata_MarshallingClass, "Unsignedinteger64BE", basicData);
					if (basicData.getFullyQualifiedName().equals("RPRunsignedInteger8BE"))
						Utils.addMetadata(Constants.Metadata_MarshallingClass, "Unsignedinteger8BE", basicData);
					if (basicData.getFullyQualifiedName().equals("RPRunsignedInteger16BE"))
						Utils.addMetadata(Constants.Metadata_MarshallingClass, "Unsignedinteger16BE", basicData);
					if (basicData.getFullyQualifiedName().equals("RPRunsignedInteger32BE"))
						Utils.addMetadata(Constants.Metadata_MarshallingClass, "Unsignedinteger32BE", basicData);
					if (basicData.getFullyQualifiedName().equals("RPRunsignedInteger64BE"))
						Utils.addMetadata(Constants.Metadata_MarshallingClass, "Unsignedinteger64BE", basicData);

					//	Adds an "doc" Metadata for Basic Data representation (same thing than in ITCS...)
					if (size!=null)
						Utils.addMetadata(Constants.Metadata_DocPrefix + basicDataSizeString, size, basicData);
					String interpretation = getSubElementValue(eType,name, basicDataInterpretationString, false);
					if (interpretation!=null)
						Utils.addMetadata(Constants.Metadata_DocPrefix + basicDataInterpretationString, interpretation, basicData);
					String endian = getSubElementValue(eType,name, basicDataEndianString, false);
					if (endian!=null)
						Utils.addMetadata(Constants.Metadata_DocPrefix + basicDataEndianString, endian, basicData);
					String encoding = getSubElementValue(eType,name, basicDataEncodingString, false);
					if (encoding!=null)
						Utils.addMetadata(Constants.Metadata_DocPrefix + basicDataEncodingString, encoding, basicData);

					addDocMetadata(eType,basicData);
				}
			}
		}
	}

	/**
	 * Reads the simple data types.
	 *
	 * @param eTypes The element which contains all the simple data types.
	 */
	private void readSimpleDataTypes(Element eTypes)
	{
		if (eTypes != null)
		{
			Iterator<?> iter = eTypes.elementIterator(simpleDataString);
			while (iter.hasNext())
			{
				Element eType = (Element) iter.next();
				String name = eType.elementText(nameString);
				String representation = getSubElementValue(eType,name, simpleDataRepresentationString, true);

				if (!allTypes.containsKey(name))
				{
					// XXX we'll need to copy the attributes of the redefined
					// basicData here.
					INative simpleData = baseFactory.createINative();
					simpleData.setName(name);
					simpleData.setFullyQualifiedName(name);
					simpleData.setDescription(getSubElementValue(eType,name, semanticsString, false));

					if (simpleData.getFullyQualifiedName().equals("HLAunicodeChar"))
						Utils.addMetadata(Constants.Metadata_MarshallingClass, "HLAunicodeChar", simpleData);

					INative natif = (INative) allTypes.get(representation);

					if (natif == null)
					{
						                  SendTrace.sendDebug("No basicType : "
						                        + representation
						                        + " defined. (Referenced by simpleDataType " + name
						                        + "). This type will be ignored.");
					}
					else
					{
						simpleData.setNative(natif.getNative());
						Utils.addMetadata(Constants.Metadata_Representation, representation, simpleData);

						IType representationType = allTypes.get(representation);
						String endian = null;
						if (representationType!=null)
						{
							Metadata endiannessMeta = getMetadata(Constants.Metadata_Endianness, representationType);

							if (endiannessMeta != null) {
								endian = endiannessMeta.getValue();
							}
						}
						// if (dataRepresentation.)
						HLA1516JavaEquivalence.set(simpleData, endian);
						allTypes.put(name, simpleData);
						domain.getTypeSystem().getTypes().add(simpleData);
					}

					addDocMetadata(eType,simpleData);
				}
			}
		}
	}


	/**
	 * Reads the enumerated data types.
	 *
	 * @param eEnums The element which contains all the enumerated data types.
	 */
	private void readEnums(Element eEnums)
	{
		if (eEnums != null)
		{
			Iterator<?> iter = eEnums.elementIterator(enumeratedDataString);
			while (iter.hasNext())
			{
				Element eEnum = (Element) iter.next();

				String name = eEnum.elementText(nameString);

				if (!allTypes.containsKey(name))
				{
					IEnum enumeratedData = baseFactory.createIEnum();
					enumeratedData.setName(name);
					enumeratedData.setFullyQualifiedName(name);
					enumeratedData.setDescription(getSubElementValue(eEnum, name, semanticsString, false));
					String representation = getSubElementValue(eEnum,name, simpleDataRepresentationString, true);

					Utils.addMetadata(Constants.Metadata_Representation, representation, enumeratedData);


					IType representationType = allTypes.get(representation);
					if (representationType!=null)
					{
						Metadata endiannessMeta = getMetadata(Constants.Metadata_Endianness, representationType);

						if (endiannessMeta != null) {
							Utils.addMetadata(Constants.Metadata_Endianness, endiannessMeta.getValue(),enumeratedData);
						}
					}


					Iterator<?> iter2 = eEnum.elementIterator(enumeratorString);
					while (iter2.hasNext())
					{
						Element eValue = (Element) iter2.next();
						String enumeratorName = eValue.elementText(nameString);
						IEnumConstant enumerator = baseFactory.createIEnumConstant();
						enumerator.setName(enumeratorName);
						// TODO : Verifier que le type de l'enum est coherent avec la valeur
						// (valeur ne depasse pas le max du type par exemple)
						// renvoyer un warning selon la gravite
						enumerator.setValue(new Integer(getSubElementValue(eValue, enumeratorName, enumeratorValueString, true))); //$NON-NLS-1$
						enumeratedData.getValues().add(enumerator);
					}
					allTypes.put(name, enumeratedData);
					domain.getTypeSystem().getTypes().add(enumeratedData);
					addDocMetadata(eEnum,enumeratedData);
				}
			}
		}
	}

	/**
	 * Create an array.
	 *
	 * @param eArray The element node corresponding to the array in the FOM.
	 */
	void createArrayData(Element eArray)
	{
		if (eArray!=null)
		{
			String arrayName = eArray.elementText(nameString);
			if (!allTypes.containsKey(arrayName)) {

				// If item type is a character and cardinality is dynamic create a
				// native string
				// else create an array
				String dataType = getSubElementValue(eArray,arrayName,dataTypeString, true);

				if ((getSubElementValue(eArray,arrayName, cardinalityString, true) == null) ||
						(getSubElementValue(eArray,arrayName, cardinalityString, true).equals("Dynamic")) &&
						((dataType.equals("HLAASCIIchar")) ||
								(dataType.equals("HLAunicodeChar"))))
				{
					// Native Type String
					INative stringData = baseFactory.createINative();
					stringData.setNative(INativeEnum.STRING);
					stringData.setName(getSubElementValue(eArray,arrayName, nameString, true));
					stringData.setFullyQualifiedName(getSubElementValue(eArray,arrayName, nameString, true));
					stringData.setDescription(getSubElementValue(eArray,arrayName, semanticsString, false));
					Utils.addMetadata(Constants.Metadata_Representation, dataType, stringData);

					String encoding = getSubElementValue(eArray,arrayName,basicDataEncodingString, true);

					if (stringData.getFullyQualifiedName().equals("OMT13string"))
						Utils.addMetadata(Constants.Metadata_NullTerminated,Messages.FDD1516EvolvedReader_0, stringData);

					// Add metadata for specificities in FOM NETN (ELLIPSE)
					if (encoding.equals("RPRnullTerminatedArray"))
					{
						Utils.addMetadata(Constants.Metadata_NullTerminated,Messages.FDD1516EvolvedReader_0, stringData);
						Utils.addMetadata(Constants.Metadata_MarshallingBitBoundary, "8",stringData);
					}

					if (encoding.equals("HLAvariableArray"))
					{
						if (dataType.equals("HLAASCIIchar"))
						{
							Utils.addMetadata(Constants.Metadata_MarshallingClass, "HLAASCIIstring", stringData);
							Utils.addMetadata(Constants.Metadata_MarshallingBitBoundary, "32",stringData);
						}
						else if (dataType.equals("HLAunicodeChar"))
						{
							Utils.addMetadata(Constants.Metadata_MarshallingClass, "HLAunicodeString", stringData);
							Utils.addMetadata(Constants.Metadata_MarshallingBitBoundary, "32",stringData);
						}
					}

					allTypes.put(getSubElementValue(eArray,arrayName,nameString,true), stringData);
					domain.getTypeSystem().getTypes().add(stringData);

					addDocMetadata(eArray, stringData);

				}
				else
				{
					Integer cardinality;
					IHomogeneousCollection arrayData;
					String cardString = getSubElementValue(eArray,arrayName,cardinalityString, true); //$NON-NLS-1$
					try {
						cardinality = new Integer(cardString);
					} catch (Exception e) {
						cardinality = new Integer(-1);
					}


					if (cardinality.equals(-1)) {
						arrayData = baseFactory.createIHomogeneousCollection();
					} else {
						arrayData = baseFactory.createIConstrainedCollection();
						((IConstrainedCollection) arrayData).setFirst(0);
						((IConstrainedCollection) arrayData).setLast(cardinality - 1);
					}

					arrayData.setRank(1);
					arrayData.setName(getSubElementValue(eArray,arrayName, nameString, true));
					arrayData.setFullyQualifiedName(getSubElementValue(eArray,arrayName, nameString, true));
					arrayData.setDescription(getSubElementValue(eArray,arrayName,semanticsString, false));
					IType arrayDataType = allTypes.get(dataType);
					if (arrayDataType != null) {
						arrayData.setItemType(arrayDataType);
					} else if (dataType != null) {
						unresolvedTypeName.add(dataType);
						unresolvedTypes.add(arrayData);
					}

					allTypes.put(getSubElementValue(eArray,arrayName,nameString, true), arrayData);
					domain.getTypeSystem().getTypes().add(arrayData);
					Utils.addMetadata(Constants.Metadata_Padding, "true", arrayData);
					Utils.addMetadata(Constants.Metadata_NullTerminated,Messages.FDD1516EvolvedReader_1, arrayData);

					addDocMetadata(eArray, arrayData);
				}
			}
		}
	}

	/**
	 * Reads the array data types.
	 *
	 * @param eArrays The element which contains all the array data types.
	 */
	private void readArrays(Element eArrays)
	{
		if (eArrays != null)
		{
			Iterator<?> iter = eArrays.elementIterator(arrayDataString);
			while (iter.hasNext())
			{
				Element eArray = (Element) iter.next();
				createArrayData(eArray);
			}
		}
	}


	/**
	 * Reads the variant data types.
	 *
	 * @param eVariants The element which contains all the variant data types.
	 */
	private void readVariants(Element eVariants)
	{
		if (eVariants != null)
		{
			Iterator<?> iter = eVariants.elementIterator(variantRecordDataString);
			while (iter.hasNext())
			{
				Element eVariant = (Element) iter.next();
				String name = eVariant.elementText(nameString);

				if (!allTypes.containsKey(name))
				{
					IMutant unionData = baseFactory.createIMutant();
					unionData.setName(name);
					unionData.setFullyQualifiedName(getSubElementValue(eVariant, name, discriminantString, true));

					IEnum enumType = (IEnum) allTypes.get(getSubElementValue(eVariant, name, dataTypeString, true));
					unionData.setChoice(enumType);
					//				discriminant.setName(getAttributeValue(atts,"discriminant")); //$NON-NLS-1$
					// discriminant.setDescription(getAttributeValue(atts,"dataType"));

					// Metadata meta=BaseFactory.eINSTANCE.createMetadata();
					// meta.setName("dataType");
					// meta.setValue(getAttributeValue(atts,"dataType"));
					// enumeratedData.getMetadatas().add(meta);


					// unionData.setDiscriminant(discriminant);
					unionData.setDescription(getSubElementValue(eVariant, name, semanticsString, false));

					Iterator<?> iterAlternatives = eVariant.elementIterator(alternativeString);
					while (iterAlternatives.hasNext())
					{
						Element eAlternative = (Element) iterAlternatives.next();

						// addPaddingMetadata=true;
						if (eAlternative != null) {
							IMutantAlternative alt = baseFactory.createIMutantAlternative();
							if (enumType!=null)
							{
								for (IEnumConstant cur : enumType.getValues())
								{
									if (cur.getName().compareTo(getSubElementValue(eAlternative, name, enumeratorString, true)) == 0) {
										alt.setEnumConstant(cur);
									}
								}
							}
							alt.setType(allTypes.get(getSubElementValue(eAlternative, name, dataTypeString, true)));
							alt.setName(getSubElementValue(eAlternative, name, nameString, true));
							alt.setDescription(getSubElementValue(eAlternative, name, semanticsString, false));
							unionData.getAlternatives().add(alt);
						}
					}

					allTypes.put(name, unionData);
					domain.getTypeSystem().getTypes().add(unionData);
					Utils.addMetadata(Constants.Metadata_Padding, "true", unionData);

					addDocMetadata(eVariant, unionData);
				}
			}
		}
	}


	/**
	 * Create a field.
	 *
	 * @param eField The field element.
	 * @return
	 */
	IValueMember createField(Element eField) {
		//		if (!getAttributeValue(atts, nameString).startsWith("Padding")) { //$NON-NLS-1$
		IValueMember field = baseFactory.createIValueMember();
		String fieldName = eField.elementText(nameString);
		if (fieldName == null)
		{
			SendTrace.sendWarning("Error : No sub-element " + nameString + " in field!");
		}

		field.setName(fieldName);
		String fieldDataTypeName = getSubElementValue(eField, fieldName, dataTypeString, true);
		IType fieldDataType = allTypes.get(fieldDataTypeName);
		// field.setDescription(getAttributeValue(atts,semanticsString));
		if (fieldDataType != null)
		{
			field.setType(fieldDataType);
		}
		else if (fieldDataTypeName != null)
		{
			unresolvedFieldsName.add(fieldDataTypeName);
			unresolvedFields.add(field);
		}
		// }
		return field;
	}

	/**
	 * Reads the structure data types.
	 *
	 * @param eStructs The element which contains all the structure data types.
	 */
	private void readStructs(Element eStructs)
	{
		if (eStructs != null)
		{
			Iterator<?> iter = eStructs.elementIterator(fixedRecordDataString);
			while (iter.hasNext())
			{
				Element eStruct = (Element) iter.next();
				String name = eStruct.elementText(nameString);

				if (!allTypes.containsKey(name))
				{
					IClass fixedRecordData = baseFactory.createIClass();
					fixedRecordData.setName(name);
					fixedRecordData.setFullyQualifiedName(name);
					fixedRecordData.setDescription(getSubElementValue(eStruct, name, semanticsString, false));

					Iterator<?> iterAtt = eStruct.elementIterator(fieldString);
					while (iterAtt.hasNext())
					{
						Element eAtt = (Element) iterAtt.next();
						IValueMember field = createField(eAtt);
						if (field!=null)
						{
							fixedRecordData.getMembers().add(field);
						}
					}
					allTypes.put(name, fixedRecordData);
					domain.getTypeSystem().getTypes().add(fixedRecordData);
					Utils.addMetadata(Constants.Metadata_Padding, "true", fixedRecordData);

					addDocMetadata(eStruct, fixedRecordData);
				}
			}
		}
	}

	/**
	 * Create an object attribute.
	 *
	 * @param attributeElement The xml element of the attribute to be created.
	 * @param objectName The name of the object which contains this attribute
	 */
	ISimAttribute createAttribute(Element attributeElement, String objectName) {
		String semantic = getSubElementValue(attributeElement, null, semanticsString, false);
		if ((semantic != null)&&(semantic.contains("@generated")))
		{//$NON-NLS-1$
			return null;
		}

		ISimAttribute attribute = TechnicalFactory.eINSTANCE.createISimAttribute();
		String attributeName = getSubElementValue(attributeElement,objectName, nameString, true);
		attribute.setName(attributeName);
		attribute.setDescription(getSubElementValue(attributeElement,attributeName, semanticsString, false));
		// Don't try to find the type : its defined later.
		String dataType = getSubElementValue(attributeElement,attributeName, dataTypeString, true);
		if (dataType != null  && dataType.compareTo("NA") != 0) { //$NON-NLS-1$
			unresolvedAttrName.add(dataType);
			unresolvedAttributes.add(attribute);
		}
		// Resolve sharing
		String sharing = getSubElementValue(attributeElement,attributeName, sharingString, false);
		Utils.addMetadata(Constants.Metadata_Distribution, sharing, attribute);
		return attribute;
	}


	/**
	 * Reads the objects.
	 *
	 * @param eObjectClasses The element which contains a list of object classes.
	 * @param superClass The super class of the list of object classes.
	 */
	private void readObjectClasses(Element eObjectClasses, ISimEntityClass superClass)
	{
		Iterator<?> iter = eObjectClasses.elementIterator(objectClassString);
		while (iter.hasNext())
		{
			Element eClass = (Element) iter.next();

			/*
			 * if (getAttributeValue(atts,semanticsString) != null) { if
			 * (getAttributeValue(atts,semanticsString).contains("@generated")) {
			 * //$NON-NLS-1$ // Avoid MOM objects return; } }
			 */
			ISimEntityClass objectClass = TechnicalFactory.eINSTANCE.createISimEntityClass();
			String name = getSubElementValue(eClass, "object class", nameString, true);
			objectClass.setName(name);
			objectClass.setDescription(getSubElementValue(eClass, name, semanticsString, false));
			
			// Resolve sharing
			String sharing = getSubElementValue(eClass,name, sharingString, false);
			Utils.addMetadata(Constants.Metadata_Distribution, sharing, objectClass);

			String fullyQualifiedName = objectClass.getName();

			if (superClass != null) {
				objectClass.setParent(superClass);
				fullyQualifiedName = superClass.getFullyQualifiedName() + "." + fullyQualifiedName;
			}

			objectClass.setFullyQualifiedName(fullyQualifiedName);

			// Check filtering flag status
			// TODO: verifier si c'est necessaire
			//SUPPRESSION_INTEG
			//         if (!name.equals("HLAmanager"))
			//         {
			//SUPPRESSION_INTEG
			simObjects.put(name, objectClass);
			Iterator<?> iterAtt = eClass.elementIterator(attributeString);
			while (iterAtt.hasNext())
			{
				ISimAttribute currentAttribute = createAttribute((Element) iterAtt.next(),name);
				if (currentAttribute!=null)
				{
					objectClass.getAttributes().add(currentAttribute);
					// dumpMetadatas(cur);
					//                  String type = Messages.FDD1516EvolvedReader_2;
					//                  if (currentAttribute.getType() != null)
					//                     type = currentAttribute.getType().getName();
					// System.err.println("attribut : "+cur.getName()+" type :"+type);
				}
			}
			readObjectClasses(eClass, objectClass);
			//SUPPRESSION_INTEG
			//         }
			//SUPPRESSION_INTEG
		}
	}

	/**
	 * Create an interaction parameter.
	 *
	 * @param parameterElement The xml element of the parameter to be created.
	 * @param objectName The name of the object which contains this parameter
	 */
	ISimAttribute createParameter(Element parameterElement, String objectName) {
		String semantic = getSubElementValue(parameterElement, null, semanticsString, false);
		if ((semantic != null)&&(semantic.contains("@generated")))
		{//$NON-NLS-1$
			return null;
		}

		ISimAttribute parameter = TechnicalFactory.eINSTANCE.createISimAttribute();
		String parameterName = getSubElementValue(parameterElement,objectName, nameString, true);
		parameter.setName(parameterName);
		parameter.setDescription(getSubElementValue(parameterElement,parameterName, semanticsString, false));

		// Don't try to find the type : its defined later.
		String dataType = getSubElementValue(parameterElement,parameterName, dataTypeString, true);
		if (dataType != null  && dataType.compareTo("NA") != 0) { //$NON-NLS-1$
			unresolvedAttrName.add(dataType);
			unresolvedAttributes.add(parameter);
		}
		return parameter;
	}



	/**
	 * Reads the interactions.
	 *
	 * @param eInteractions The element which contains a list of interaction classes.
	 * @param superClass The super class of the list of interactions classes.
	 */
	private void readInteractions(Element eInteractions,  ISimInteractionClass superClass)
	{
		Iterator<?> iter = eInteractions.elementIterator(interactionClassString);
		while (iter.hasNext())
		{
			Element eClass = (Element) iter.next();

			// if (getAttributeValue(atts,semanticsString) != null) {
			//			if (getAttributeValue(atts,semanticsString).contains("@generated")) { //$NON-NLS-1$
			// // Avoid MOM objects
			// return;
			// }
			// }
			ISimInteractionClass interactionClass = TechnicalFactory.eINSTANCE.createISimInteractionClass();
			String name = getSubElementValue(eClass, "interaction class", nameString, true);
			interactionClass.setName(name);
			interactionClass.setDescription(getSubElementValue(eClass, name, semanticsString, false));

			// Create the fully qualified Name
			String fullyQualifiedName = interactionClass.getName();

			// Set heritage.
			if (superClass != null) {
				interactionClass.setParent(superClass);

				fullyQualifiedName = superClass.getFullyQualifiedName() + "." + fullyQualifiedName;
			}

			interactionClass.setFullyQualifiedName(fullyQualifiedName);

			// Resolve sharing
			String sharing = getSubElementValue(eClass,name, sharingString, false);
			Utils.addMetadata(Constants.Metadata_Distribution, sharing, interactionClass);

			// Check filtering flag status
			// TODO: verifier si c'est necessaire
			//SUPPRESSION_INTEG
			//if (!name.equals("HLAmanager")) //$NON-NLS-1$
			//{
			//SUPPRESSION_INTEG
			simInteractions.put(name, interactionClass);
			Iterator<?> iterAtt = eClass.elementIterator(parameterString);
			while (iterAtt.hasNext())
			{
				ISimAttribute currentParameter = createParameter((Element) iterAtt.next(),name);
				if (currentParameter!=null)
				{
					interactionClass.getParameters().add(currentParameter);
					// dumpMetadatas(cur);
					//                  String type = Messages.FDD1516EvolvedReader_3;
					//                  if (currentParameter.getType() != null)
					//                     type = currentParameter.getType().getName();
					// System.err.println("attribut : "+cur.getName()+" type :"+type);
				}
			}

			readInteractions(eClass, interactionClass);
			//SUPPRESSION_INTEG
			//}
			//SUPPRESSION_INTEG

		}
	}

	/**
	 * Resolves the data types after reading.
	 * A name of a data type is replaced by a reference to the created class for the data type.
	 */
	public void resolveDataTypes()
	{
		//      String error = "Type "; //$NON-NLS-1$

		// For each unresolved type.
		for (IHomogeneousCollection cur : unresolvedTypes) {
			// Get the name of the unresolved reference.
			String typeName = unresolvedTypeName.get(0);

			// Try to find the corresponding type.
			IType toFind = allTypes.get(typeName);
			if (toFind != null) {
				cur.setItemType(toFind);
			} else {
				/*
				 * throw new SAXException(error + typeName +
				 * " non trouve pour le tableau " + cur.getName());
				 * //$NON-NLS-1$
				 */
				// System.err.println(error + typeName
				// + " non trouve pour le tableau " + cur.getName());
			}
			unresolvedTypeName.remove(0);
		}

		// For each unresolved attribute's type.
		for (ISimAttribute cur : unresolvedAttributes) {
			// Get the name of the unresolved reference.
			String typeName = unresolvedAttrName.get(0);

			// Try to find the corresponding type.
			IType toFind = allTypes.get(typeName);
			if (toFind != null) {
				cur.setType(toFind);
				// System.err.println("*****" + error + typeName
				// + " trouve pour l'attribut ou le parametre " +
				// cur.getName());
			} else {
				/*
				 * throw new SAXException(error + typeName +
				 * "non trouve pour l'attribut ou le parametre " +
				 * cur.getName()); //$NON-NLS-1$
				 */
				// System.err.println(error + typeName
				// + "non trouve pour l'attribut ou le parametre " +
				// cur.getName());

			}
			unresolvedAttrName.remove(0);
		}

		// For each unresolved attribute's type.
		for (IValueMember cur : unresolvedFields) {
			// Get the name of the unresolved reference.
			String typeName = unresolvedFieldsName.get(0);

			// Try to find the corresponding type.
			IType toFind = allTypes.get(typeName);
			if (toFind != null) {
				cur.setType(toFind);
				// System.err.println("*****" + error + typeName
				// + " trouve pour l'attribut ou le parametre " +
				// cur.getName());
			} else {
				/*
				 * throw new SAXException(error + typeName +
				 * "non trouve pour l'attribut ou le parametre " +
				 * cur.getName()); //$NON-NLS-1$
				 */
				// System.err.println(error + typeName
				// + "non trouve pour l'attribut ou le parametre " +
				// cur.getName());

			}
			unresolvedFieldsName.remove(0);
		}
	}

	public ISimModel getDomain()
	{
		return domain;
	}


	public void addDocMetadata(Element elem, IType type)
	{
		if ((elem!=null)&&(type!=null))
		{
			/*
			 * Ajout des metadatas documentaires (les attributs n'ayant pas
			 * encore ete lus)
			 */

			List<?> subElements = elem.elements();
			for (int i = 0; i < subElements.size(); i++)
			{
				Element subElement = (Element) subElements.get(i);
				String qName = subElement.getName();
				if (!isDocMetadataException(qName))
				{
					String value = elem.elementText(qName);
					Utils.addMetadata(Constants.Metadata_DocPrefix + qName, value, type);
				}
			}
		}
	}

	private static String[] exceptions= {"name", "field", "enumerator", "alternative", "discriminant", "semantics", "dataType", "cardinality", "representation"};
	private boolean isDocMetadataException(String qName)
	{
		boolean result = false;

		for (int i = 0 ; i < exceptions.length && result == false; i++ )
		{
			if (qName.equals(exceptions[i]))
			{
				result = true;
			}
		}

		return result;
	}


	/**
	 * Parses and resolves all '.mapping' files.
	 *
	 * @param dir the directory where to read the '.mapping' files.
	 */
	private void resolveMappings(File dir)
	{
		File[] mappingFiles = dir.listFiles(new FileFilter()
		{
			public boolean accept(File pathname)
			{
				return pathname.isFile() && pathname.getName().endsWith(".mapping");
			}
		});

		for (File mappingFile : mappingFiles)
		{
			try
			{
				FileReader fir = new FileReader(mappingFile);
				BufferedReader br = new BufferedReader(fir);
				String line = null;
				while ((line = br.readLine()) != null)
				{
					String[] tokens = line.split("->");
					if (tokens.length == 3)
					{
						String object = tokens[0].trim();
						if (!_rprLengthlessArrayMapping.containsKey(object))
						{
							_rprLengthlessArrayMapping.put(object, new HashMap<String, String>());
						}
						String arrayAttr = tokens[1].trim();
						String lengthAttr = tokens[2].trim();
						_rprLengthlessArrayMapping.get(object).put(arrayAttr, lengthAttr);
					}
				}
			}
			catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
		}

		// add the 'array_length_attribute' metadata
		for (String objectName : _rprLengthlessArrayMapping.keySet())
		{
			// for ISimEntityClass objects
			ISimEntityClass simEntityClass = simObjects.get(objectName);
			if (simEntityClass != null)
			{
				EList<ISimAttribute> simAttributes = simEntityClass.getAttributes();
				addArrayLengthAttrMetadata(objectName, simAttributes);
			}

			// for ISimInteractionClass objects
			ISimInteractionClass simInteractionClass = simInteractions.get(objectName);
			if (simInteractionClass != null)
			{
				EList<ISimAttribute> simParameters = simInteractionClass.getParameters();
				addArrayLengthAttrMetadata(objectName, simParameters);
			}

			// for class types
			IType type = allTypes.get(objectName);
			if (type != null && type instanceof IClass)
			{
				IClass classType = (IClass) type;
				EList<IValueMember> members = classType.getMembers();
				addArrayLengthAttrMetadata(objectName, members);
			}
		}
	}


	/**
	 * Adds the 'array_length_attribute' metadata to the elements of a given object (if needed).<br>
	 * <ul>
	 *   <li>Attributes of entities
	 *   <li>Parameters of interactions
	 *   <li>Members of class types
	 * </ul>
	 *
	 * @param objectName the object name
	 * @param elements the object elements
	 */
	private void addArrayLengthAttrMetadata(String objectName, EList<? extends ArchitecturalElement> elements)
	{
		Map<String, String> mapping = _rprLengthlessArrayMapping.get(objectName);
		for (ArchitecturalElement element : elements)
		{
			String attributeName = element.getName();
			if (mapping.containsKey(attributeName))
			{
				String metadataName = Constants.Metadata_DocPrefix + Constants.METADATA_ARRAY_LENGTH_ATTRIBUTE;
				String metadataValue = mapping.get(attributeName);
				Utils.addMetadata(metadataName, metadataValue, element);
			}
		}
	}
}