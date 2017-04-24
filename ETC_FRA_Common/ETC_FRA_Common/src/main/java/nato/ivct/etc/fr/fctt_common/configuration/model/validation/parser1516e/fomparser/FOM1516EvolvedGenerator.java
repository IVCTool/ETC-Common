package nato.ivct.etc.fr.fctt_common.configuration.model.validation.parser1516e.fomparser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import fr.itcs.sme.architecture.technical.ISimAttribute;
import fr.itcs.sme.architecture.technical.ISimEntityClass;
import fr.itcs.sme.architecture.technical.ISimInteractionClass;
import fr.itcs.sme.architecture.technical.ISimModel;
import fr.itcs.sme.architecture.technical.types.IClass;
import fr.itcs.sme.architecture.technical.types.IConstrainedCollection;
import fr.itcs.sme.architecture.technical.types.IEnum;
import fr.itcs.sme.architecture.technical.types.IEnumConstant;
import fr.itcs.sme.architecture.technical.types.IHomogeneousCollection;
import fr.itcs.sme.architecture.technical.types.IMutant;
import fr.itcs.sme.architecture.technical.types.IMutantAlternative;
import fr.itcs.sme.architecture.technical.types.INative;
import fr.itcs.sme.architecture.technical.types.IType;
import fr.itcs.sme.architecture.technical.types.IValueMember;
import fr.itcs.sme.architecture.technical.types.TypeSystem;
import fr.itcs.sme.base.Element;
import fr.itcs.sme.base.Metadata;

/**
 * FOM1516EvolvedGenerator.java declaration.
 *
 */

public class FOM1516EvolvedGenerator {
	// TODO: check all the hard coded value and their impact when the FOM is changed...

	/**
	 * Line separator
	 */
	private final String ls = System.getProperty("line.separator"); //$NON-NLS-1$


	/**
	 * Name of the basic data.
	 */
	private final String anyTypeName = "Unknown"; //$NON-NLS-1$


	/**
	 * All the simple types of the exported FOM.
	 */
	private final Map<String, INative> simpleTypes = new HashMap<String, INative>();


	/**
	 * All the enumerations of the exported FOM.
	 */
	private final Map<String, IEnum> enumTypes = new HashMap<String, IEnum>();


	/**
	 * All the collections (arrays) of the exported FOM.
	 */
	private final Map<String, IHomogeneousCollection> collectionTypes = new HashMap<String, IHomogeneousCollection>();


	/**
	 * All the structures of the exported FOM.
	 */
	private final Map<String, IClass> structTypes = new HashMap<String, IClass>();


	/**
	 * All the unions of the exported FOM.
	 */
	private final Map<String, IMutant> unionTypes = new HashMap<String, IMutant>();


	/**
	 * The "HLAmanager" object class of the MOM.
	 */
	ISimEntityClass momObjectClass = null;


	/**
	 * List of object classes of the MOM
	 */
	List<ISimEntityClass> momObjectClasses = null;


	/**
	 * The "HLAmanager" interaction class of the MOM.
	 */
	ISimInteractionClass momInteractionClass = null;


	/**
	 * List of interaction classes of the MOM
	 */
	List<ISimInteractionClass> momInteractionClasses = null;


	/**
	 * Constructor.
	 */
	public FOM1516EvolvedGenerator() {
	}


	/**
	 * Generate a HLA 15.16 FOM.
	 *
	 * @param fom The FOM to process.
	 * @return the buffer.
	 * @throws CoreException exception
	 */
	public InputStream generate(ISimModel fom) throws CoreException, Exception {
		StringBuffer buffer = new StringBuffer();

		// Fill the HLA header.
		fillHeader(buffer, fom);

		// Dump classes.
		// Get the root class.
		buffer.append("<objects>" + ls); //$NON-NLS-1$
		// Write the root object.
		buffer.append("  <objectClass>"+ ls + "    <name>HLAobjectRoot</name>" + ls); //$NON-NLS-1$ //$NON-NLS-2$

		// Insert the MOM if any.
		// TODO: check if something to do in our case
		//		//URL url = Activator.getDefault().getBundle().getResource("/resources/MOMObjectClasses.xml"); //$NON-NLS-1$
		//		InputStream buf = this.getClass().getResourceAsStream("/resources/MOMObjectClasses.xml");
		//		while (buf.available() > 0) {
		//			buffer.append((char) buf.read());
		//		}

		for (ISimEntityClass curNode : fom.getEntities()) {
			if (curNode.getParent() == null) {
				if (curNode.getName().compareTo("HLAobjectRoot") == 0) { //$NON-NLS-1$
					// Just ignore it.
				} else {
					// No HLAobjectRoot.
					buildClassTree(curNode, fom.getEntities(), buffer);
				}
			} else if (curNode.getParent().getName().compareTo("HLAobjectRoot") == 0) {
				buildClassTree(curNode, fom.getEntities(), buffer);
			}
		}
		buffer.append("  </objectClass>" + ls); //$NON-NLS-1$
		buffer.append("</objects>" + ls); //$NON-NLS-1$

		// Dump interactions.
		// Get the root interaction.
		buffer.append("<interactions>" + ls); //$NON-NLS-1$
		// Write the root interaction.
		buffer.append("  <interactionClass><name>HLAinteractionRoot</name>" + ls); //$NON-NLS-1$
		//		// Insert the MOM if any.
		//		/*
		//		 * URL urlInteraction = Activator.getDefault().getBundle().getResource(
		//		 * "/resources/MOMInteractionClasses.xml"); //$NON-NLS-1$ InputStream
		//		 * bufInteraction = urlInteraction.openStream();
		//		 */
		//
		//		InputStream bufInteraction = this.getClass().getResourceAsStream(
		//		"/resources/MOMInteractionClasses.xml");
		//
		//		while (bufInteraction.available() > 0) {
		//			buffer.append((char) bufInteraction.read());
		//		}


		for (ISimInteractionClass curNode : fom.getInteractions()) {
			if (curNode.getParent() == null) {
				if (curNode.getName().compareTo("HLAinteractionRoot") == 0) { //$NON-NLS-1$
					// Just ignore it.

				} else {
					// No HLAinteractionRoot.
					buildInteractionTree(curNode, fom.getInteractions(), buffer);
				}
			} else if (curNode.getParent().getName().compareTo("HLAinteractionRoot") == 0) {
				buildInteractionTree(curNode, fom.getInteractions(), buffer);
			}
		}
		buffer.append("  </interactionClass>" + ls); //$NON-NLS-1$
		buffer.append("</interactions>" + ls); //$NON-NLS-1$

		// Dump transportation and switches sections.
		dumpTransportationAndSwitchtes(buffer);

		// Dump types.
		dumpTypes(buffer);

		// Finish him !
		buffer.append("</objectModel>" + ls); //$NON-NLS-1$
		InputStream stream = new ByteArrayInputStream(buffer.toString().getBytes());

		return stream;
	}


	/**
	 * Build the class tree.
	 *
	 * @param node
	 *            The root node.
	 * @param classes
	 *            The list of classes.
	 * @param buffer
	 *            The current buffer.
	 */
	private void buildClassTree(ISimEntityClass node, List<ISimEntityClass> classes,
			StringBuffer buffer) throws Exception {

		// Don't write the MOM classes.
		if (node.getName().compareTo("HLAManager") == 0) {
			return;
		}
		buffer.append(" <objectClass>" + ls); //$NON-NLS-1$ //$NON-NLS-2$
		buffer.append(" <name>" + node.getName() + "</name>" + ls); //$NON-NLS-1$ //$NON-NLS-2$

		// TODO: en dur dans l'ITCS ?
		buffer.append(" <sharing>\"PublishSubscribe\"</sharing>" + ls); //$NON-NLS-1$ //$NON-NLS-2$

		buffer.append(getDocMetadata(node));

		buffer.append(" <semantics>"+ cleanStrings(node.getDescription()) +"</semantics>" + ls); //$NON-NLS-1$ //$NON-NLS-2$

		// Write the attributes.
		dumpAttributes(node.getAttributes(), buffer);

		for (ISimEntityClass curClass : classes) {
			if (curClass.getParent() != null && curClass.getParent() == node) {
				buildClassTree(curClass, classes, buffer);
			}
		}

		buffer.append(" </objectClass>" + ls); //$NON-NLS-1$
	}


	private String getDocMetadata(Element node) {
		String retour = "";
		for (Metadata meta : node.getMetadatas()) {
			if (meta.getName().startsWith(Constants.Metadata_DocPrefix))
			{
				String elementName = meta.getName().replace(Constants.Metadata_DocPrefix, "");
				retour += " <" + elementName + ">" + meta.getValue() + "</" + elementName + ">" + ls ;
			}
		}
		return retour;
	}


	/**
	 * Build the interactions tree.
	 *
	 * @param node
	 *            The root node.
	 * @param interactions
	 *            The list of interactions.
	 * @param buffer
	 *            The current buffer.
	 */
	private void buildInteractionTree(ISimInteractionClass node,
			List<ISimInteractionClass> interactions, StringBuffer buffer) throws Exception {

		// Don't write the MOM interactions.
		if (node.getName().compareTo("HLAManager") == 0) {
			return;
		}

		buffer.append("    <interactionClass>" + ls); //$NON-NLS-1$
		buffer.append("      <name>" + node.getName() + "</name>" + ls); //$NON-NLS-1$ //$NON-NLS-2$

		String sharing = Constants.publishSubscribe.PublishSubscribe.name();
		Metadata meta = Utils.getMetadata(Constants.Metadata_Distribution, node);
		if (meta != null)
			sharing = meta.getValue();
		buffer.append("      <sharing>"+ sharing +"</sharing>" + ls); //$NON-NLS-1$ //$NON-NLS-2$

		buffer.append("      <dimensions/>" + ls); //$NON-NLS-1$
		buffer.append("      <transportation>HLAbestEffort</transportation>" + ls); //$NON-NLS-1$
		// TODO: order = TimeStamp en dur dans l'ITCS ?
		buffer.append("      <order>TimeStamp</order>" + ls); //$NON-NLS-1$

		buffer.append(getDocMetadata(node));
		buffer.append("      <semantics>"+ cleanStrings(node.getDescription()) +"</semantics>" + ls); //$NON-NLS-1$ //$NON-NLS-2$

		// Write the parameters.
		dumpParameters(node.getParameters(), buffer);

		for (ISimInteractionClass curInteraction : interactions) {
			if (curInteraction.getParent() != null && curInteraction.getParent() == node) {
				buildInteractionTree(curInteraction, interactions, buffer);
			}
		}

		buffer.append("    </interactionClass>" + ls); //$NON-NLS-1$
	}


	/**
	 * Dump the attributes of a class.
	 *
	 * @param attributes
	 *            The list of attributes.
	 * @param buffer
	 *            The current buffer.
	 */
	private void dumpAttributes(EList<ISimAttribute> attributes, StringBuffer buffer)
	throws Exception {

		for (ISimAttribute attr : attributes) {
			buffer.append("  <attribute>" + ls); //$NON-NLS-1$
			buffer.append(" <name>" + formatString(attr.getName()) + "</name>" + ls); //$NON-NLS-1$ //$NON-NLS-2$
			String name = null;
			if (attr.getType() != null) {
				extractType(attr.getType());
				name = attr.getType().getName();
				buffer.append(" <dataType>" + formatString(name) + "</dataType>" + ls); //$NON-NLS-1$ //$NON-NLS-2$

				String sharing = Constants.publishSubscribe.PublishSubscribe.name();
				Metadata meta = Utils.getMetadata(Constants.Metadata_Distribution, attr);
				if (meta != null)
					sharing = meta.getValue();

				buffer.append(" <sharing>" + sharing + "</sharing>" + ls); //$NON-NLS-1$ //$NON-NLS-2$
			}
			buffer.append("<dimensions/>" + ls); //$NON-NLS-1$
			buffer.append(" <transportation>HLAbestEffort</transportation>" + ls); //$NON-NLS-1$
			// TODO: order = TimeStamp en dur dans l'ITCS ?
			buffer.append(" <order>TimeStamp</order>" + ls); //$NON-NLS-1$
			buffer.append(getDocMetadata(attr));
			// TODO: updateType, updateCondition ?
			buffer.append(" <semantics>" + cleanStrings(attr.getDescription()) + "</semantics>" + ls); //$NON-NLS-1$ //$NON-NLS-2$
			buffer.append("</attribute>" + ls); //$NON-NLS-1$
		}

	}


	/**
	 * Dump the parameters of an interaction.
	 *
	 * @param parameters
	 *            The list of parameters.
	 * @param buffer
	 *            The current buffer.
	 */
	private void dumpParameters(EList<ISimAttribute> parameters, StringBuffer buffer) {

		for (ISimAttribute param : parameters) {

			buffer.append("    <parameter>" + ls); //$NON-NLS-1$ //$NON-NLS-2$
			buffer.append("      <name>" + formatString(param.getName()) + "</name>" + ls); //$NON-NLS-1$ //$NON-NLS-2$

			if (param.getType() != null) {
				buffer.append(" <dataType>" + formatString(param.getType().getName()) + "</dataType>" + ls); //$NON-NLS-1$ //$NON-NLS-2$
				extractType(param.getType());
			}
			buffer.append(getDocMetadata(param));
			buffer.append("     <semantics>" + cleanStrings(param.getDescription()) + "</semantics>" + ls); //$NON-NLS-1$ //$NON-NLS-2$
			buffer.append("    </parameter>" + ls); //$NON-NLS-1$
		}
	}


	/**
	 * Dump the types of the model.
	 *
	 * @param buffer
	 *            The buffer.
	 */
	private void dumpTypes(StringBuffer buffer) {

		buffer.append("<dataTypes>" + ls); //$NON-NLS-1$
		//	buffer.append(" <basicDataRepresentations>" + ls); //$NON-NLS-1$
		/*
		 * buffer .append("  <basicData name=\"" + anyTypeName +
		 * "\" size=\"32\" interpretation=\"any possible type\" " +
		 * //$NON-NLS-1$ //$NON-NLS-2$
		 * "endian=\"Big\" encoding=\"Assumed to be portable among hardware devices\"/>\n </basicDataRepresentations>"
		 * //$NON-NLS-1$ + ls); //$NON-NLS-1$
		 */

		if (!simpleTypes.values().isEmpty()) {
			buffer.append("  <basicDataRepresentations>" + ls); //$NON-NLS-1$
			for (INative cur : simpleTypes.values()) {
				Metadata meta = Utils.getMetadata(Constants.Metadata_DocPrefix + "encoding", cur);
				// If Encoding present it's a basicDataRepresentation
				if (meta != null) {
					buffer.append("    <basicData><name>" + formatString(cur.getName()) + "</name>" + ls); //$NON-NLS-1$ //$NON-NLS-2$
					/*
					 * String
					 * sharing=Constants.publishSubscribe.PublishSubscribe
					 * .name();
					 * meta=Utils.getMetadata(Constants.Metadata_Distribution,
					 * cur); if (meta!=null) sharing=meta.getValue();
					 */

					/*
					 * buffer.append("sharing=\""+sharing+"\" ");
					 * buffer.append("units=\"" + unit + "\" "); //$NON-NLS-1$
					 * //$NON-NLS-2$ buffer.append("resolution=\"" + range +
					 * "\" "); //$NON-NLS-1$ //$NON-NLS-2$
					 * buffer.append("accuracy=\"NA\" "); //$NON-NLS-1$
					 */
					buffer.append(getDocMetadata(cur));
					buffer.append("    </basicData>" + ls); //$NON-NLS-1$
				}
			}
			buffer.append("  </basicDataRepresentations>" + ls); //$NON-NLS-1$
		}


		if (!simpleTypes.values().isEmpty()) {
			buffer.append("  <simpleDataTypes>" + ls); //$NON-NLS-1$
			for (INative cur : simpleTypes.values()) {

				Metadata meta = Utils.getMetadata(Constants.Metadata_DocPrefix + "encoding", cur);
				// If Encoding not present it's a simpleDataType
				if (meta == null) {

					String lRepresentation = null;

					meta = Utils.getMetadata(Constants.Metadata_Representation, cur);

					if (meta != null)
						lRepresentation = meta.getValue();

					buffer.append("    <simpleData>" + ls +  "<name>" + formatString(cur.getName()) + "</name>" + ls); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-2$

					if (lRepresentation != null)
						buffer.append("   <representation>" + lRepresentation + "</representation>" + ls); //$NON-NLS-1$ //$NON-NLS-2$

					/*
					 * String unit=""; Metadata
					 * meta=Utils.getMetadata(Constants.MetadataUnit, cur); if
					 * (meta!=null) unit=meta.getValue();
					 *
					 * String range="";
					 * meta=Utils.getMetadata(Constants.MetadataRange, cur); if
					 * (meta!=null) range=meta.getValue();
					 */

					String sharing = Constants.publishSubscribe.PublishSubscribe.name();
					meta = Utils.getMetadata(Constants.Metadata_Distribution, cur);
					if (meta != null)
						sharing = meta.getValue();

					/*
					 * buffer.append("sharing=\""+sharing+"\" ");
					 * buffer.append("units=\"" + unit + "\" "); //$NON-NLS-1$
					 * //$NON-NLS-2$ buffer.append("resolution=\"" + range +
					 * "\" "); //$NON-NLS-1$ //$NON-NLS-2$
					 * buffer.append("accuracy=\"NA\" "); //$NON-NLS-1$
					 */
					buffer.append("    <semantics>" + cleanStrings(cur.getDescription()) + "</semantics>" + ls); //$NON-NLS-1$ //$NON-NLS-2$
					buffer.append(getDocMetadata(cur));
					buffer.append("   </simpleData>" + ls); //$NON-NLS-1$
				}
			}
			buffer.append("  </simpleDataTypes>" + ls); //$NON-NLS-1$
		}

		if (!enumTypes.values().isEmpty()) {
			buffer.append("  <enumeratedDataTypes>" + ls); //$NON-NLS-1$
			for (IEnum cur : enumTypes.values()) {

				String lRepresentation = anyTypeName;

				Metadata meta = Utils.getMetadata(Constants.Metadata_Representation, cur);

				if (meta != null)
					lRepresentation = meta.getValue();


				buffer.append("    <enumeratedData>" + ls + "<name>" + formatString(cur.getName()) + "</name>" + ls); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-2$
				buffer.append("    <representation>" + lRepresentation + "</representation>" + ls); //$NON-NLS-1$ //$NON-NLS-2$
				buffer.append("    <semantics>" + cleanStrings(cur.getDescription()) + "</semantics>" + ls); //$NON-NLS-1$ //$NON-NLS-2$
				buffer.append(getDocMetadata(cur));

				for (IEnumConstant curLiteral : cur.getValues()) {
					buffer.append("    <enumerator>" + ls + "<name>" + formatString(curLiteral.getName()) + "</name>" + ls + "<value>" + curLiteral.getValue() + "</value>" + ls + getDocMetadata(curLiteral) + "</enumerator>" + ls); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
				}
				buffer.append("     </enumeratedData>" + ls); //$NON-NLS-1$
			}
			buffer.append("   </enumeratedDataTypes>" + ls); //$NON-NLS-1$
		}

		if (!collectionTypes.values().isEmpty()) {
			buffer.append("  <arrayDataTypes>" + ls); //$NON-NLS-1$
			for (IHomogeneousCollection cur : collectionTypes.values()) {
				if (cur instanceof IConstrainedCollection) {
					buffer.append("    <arrayData>" + ls + "<name>" + formatString(cur.getName()) + "</name>"+ls+ "<dataType>" + formatString(cur.getItemType().getName()) + "</dataType>"  + ls); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$  //$NON-NLS-5$
					buffer.append("    <cardinality>" + cur.getRank() + "</cardinality>"  + ls); //$NON-NLS-1$ //$NON-NLS-2$
					buffer.append("    <semantics>"+ cleanStrings(cur.getDescription()) + "</semantics>"  + ls); //$NON-NLS-1$ //$NON-NLS-2$
					buffer.append(getDocMetadata(cur));
					buffer.append("    </arrayData>" + ls); //$NON-NLS-1$
				} else {
					buffer.append("    <arrayData>" + ls + "<name>" + formatString(cur.getName()) + "</name>"+ ls+ "<dataType>" + formatString(cur.getItemType().getName()) + "</dataType>"  + ls); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$  //$NON-NLS-5$
					buffer.append("    <cardinality>Dynamic</cardinality>"  + ls); //$NON-NLS-1$
					buffer.append("    <semantics>" + cleanStrings(cur.getDescription()) + "</semantics>"  + ls); //$NON-NLS-1$ //$NON-NLS-2$
					buffer.append(getDocMetadata(cur));
					buffer.append("    </arrayData>" + ls); //$NON-NLS-1$
				}
			}
			buffer.append(" </arrayDataTypes>" + ls); //$NON-NLS-1$
		}

		if (!structTypes.values().isEmpty()) {
			buffer.append("  <fixedRecordDataTypes>" + ls); //$NON-NLS-1$
			for (IClass cur : structTypes.values()) {
				buffer.append("    <fixedRecordData>" + ls + "<name>" + formatString(cur.getName()) + "</name>" + ls); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				buffer.append("    <semantics>" + cleanStrings(cur.getDescription()) + "</semantics>"  + ls); //$NON-NLS-1$ //$NON-NLS-2$
				buffer.append(getDocMetadata(cur));

				for (IValueMember curAttr : cur.getMembers()) {
					System.err.println(curAttr.getName());
					buffer.append("   <field>" + ls + "<name>" + formatString(curAttr.getName()) + "</name>" + ls); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					IType type = curAttr.getType();
					if (type!=null)
					{
						buffer.append("    <dataType>" + formatString(type.getName()) + "</dataType>"  + ls); //$NON-NLS-1$ //$NON-NLS-2$
					}
					buffer.append("    <semantics>" + cleanStrings(curAttr.getDescription()) + "</semantics>" + ls); //$NON-NLS-1$ //$NON-NLS-2$
					buffer.append(getDocMetadata(curAttr));
					buffer.append("    </field>" + ls); //$NON-NLS-1$
				}
				if (cur.getMembers().isEmpty()) {
					buffer.append("   <field>" + ls + "<name>Padding</name>" + ls); //$NON-NLS-1$
					if (!simpleTypes.isEmpty()) {
						buffer
						.append("    <dataType>" + formatString(simpleTypes.values().iterator().next().getName()) + "<dataType>" + ls); //$NON-NLS-1$ //$NON-NLS-2$
					}
					buffer.append("   </field>" + ls); //$NON-NLS-1$ //$NON-NLS-2$
				}
				buffer.append("  </fixedRecordData>" + ls); //$NON-NLS-1$
			}
			buffer.append(" </fixedRecordDataTypes>" + ls); //$NON-NLS-1$
		}

		if (!unionTypes.values().isEmpty()) {
			buffer.append("  <variantRecordDataTypes>" + ls); //$NON-NLS-1$
			for (IMutant cur : unionTypes.values()) {
				buffer.append("    <variantRecordData>" + ls + "<name>" + formatString(cur.getName()) + "</name>" + ls); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				if (cur.getChoice() != null) {
					buffer
					.append("    <discriminant>" + formatString(cur.getFullyQualifiedName()) + "</discriminant>" + ls); //$NON-NLS-1$ //$NON-NLS-2$
					buffer.append("    <dataType>" + formatString(cur.getChoice().getName()) + "</dataType>"  + ls); //$NON-NLS-1$ //$NON-NLS-2$
				}
				buffer.append("    <semantics>" + cleanStrings(cur.getDescription()) + "</semantics>"+ ls); //$NON-NLS-1$ //$NON-NLS-2$
				buffer.append(getDocMetadata(cur));

				for (IMutantAlternative curAlt : cur.getAlternatives()) {
					buffer
					.append("   <alternative>" + ls + "<enumerator>" + formatString(curAlt.getEnumConstant().getName()) + "</enumerator>" + ls); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					buffer.append("    <name>" + formatString(curAlt.getName()) + "</name>" + ls); //$NON-NLS-1$ //$NON-NLS-2$
					buffer.append("    <dataType>" + formatString(curAlt.getType().getName()) + "</dataType>" + ls); //$NON-NLS-1$ //$NON-NLS-2$
					buffer.append("    <semantics>" + cleanStrings(curAlt.getDescription()) + "</semantics>" + ls); //$NON-NLS-1$ //$NON-NLS-2$
					buffer.append(getDocMetadata(curAlt));
					buffer.append("   </alternative>" + ls); //$NON-NLS-1$
				}
				buffer.append("    </variantRecordData>" + ls); //$NON-NLS-1$
			}
			buffer.append("  </variantRecordDataTypes>" + ls); //$NON-NLS-1$
		}

		buffer.append("</dataTypes>" + ls); //$NON-NLS-1$
	}


	/**
	 * Extract the type and add it to the corresponding internal map.
	 *
	 * @param type
	 *            The type.
	 */
	private void extractType(IType aType) {

		if (simpleTypes.size() == 0 && structTypes.size() == 0 && unionTypes.size() == 0
				&& collectionTypes.size() == 0 && enumTypes.size() == 0) {

			EObject parent = aType.eContainer();
			if (parent instanceof TypeSystem) {
				TypeSystem domain = (TypeSystem) parent;

				for (IType type : domain.getTypes()) {
					if (type instanceof INative) {
						INative theType = (INative) type;
						simpleTypes.put(type.getName(), theType);
					} else if (type instanceof IEnum) {
						IEnum theType = (IEnum) type;
						enumTypes.put(type.getName(), theType);
					} else if (type instanceof IHomogeneousCollection) {
						IHomogeneousCollection theType = (IHomogeneousCollection) type;
						collectionTypes.put(type.getName(), theType);
					} else if (type instanceof IClass) {
						IClass theType = (IClass) type;
						structTypes.put(type.getName(), theType);
					} else if (type instanceof IMutant) {
						IMutant theType = (IMutant) type;
						unionTypes.put(type.getName(), theType);
					}
				}
			}
		}
	}


	/**
	 * Dump Transportation and Switches parts
	 *
	 * @param buffer
	 *            the buffer where to write dump
	 */
	private void dumpTransportationAndSwitchtes(StringBuffer buffer) {
		// TODO: en dur dans l'ITCS ?
		buffer
		.append("<dimensions>" + ls + //$NON-NLS-1$
				"  <dimension>"+ ls + //$NON-NLS-1$
				"    <name>Federate</name>" + ls + //$NON-NLS-1$
				"    <dataType>HLAfederateHandle</dataType>" + ls + //$NON-NLS-1$
				"    <upperBound>2000000</upperBound>"+ ls + //$NON-NLS-1$
				"    <upperBoundNotes>MOM1</upperBoundNotes>"+ ls + //$NON-NLS-1$
				"    <normalization>Normalize Federate Handle service</normalization>" + ls + //$NON-NLS-1$
				"    <value>Excluded</value>"+ ls + //$NON-NLS-1$
				"  </dimension>"+ ls + //$NON-NLS-1$
				"  <dimension>"+ ls + //$NON-NLS-1$
				"    <name>ServiceGroup</name>" + ls + //$NON-NLS-1$
				"    <dataType>HLAserviceGroupName</dataType>" + ls + //$NON-NLS-1$
				"    <upperBound>7</upperBound>"+ ls + //$NON-NLS-1$
				"    <upperBoundNotes>MOM1</upperBoundNotes>"+ ls + //$NON-NLS-1$
				"    <normalization>Normalize Service Group service</normalization>" + ls + //$NON-NLS-1$
				"    <value>Excluded</value>"+ ls + //$NON-NLS-1$
				"  </dimension>"+ ls + //$NON-NLS-1$
				"</dimensions>" + //$NON-NLS-1$

//				"<transportations>" + ls +//$NON-NLS-1$
//				"  <transportation>" + ls +//$NON-NLS-1$
//				"    <name>HLAreliable</name>" + ls +//$NON-NLS-1$
//				"    <description>Provide reliable delivery of data in the sense that TCP/IP delivers its data reliably</description>" + ls + //$NON-NLS-1$
//				"  </transportation>" + ls +//$NON-NLS-1$
//				"  <transportation>" + ls +//$NON-NLS-1$
//				"    <name>HLAbestEffort</name>" + ls +//$NON-NLS-1$
//				"    <description>Make an effort to deliver data in the sense that UDP provides best-effort delivery</description>" + ls + //$NON-NLS-1$
//				"  </transportation>" + ls +//$NON-NLS-1$
//				"</transportations>" + ls + //$NON-NLS-1$

				"<switches>" + ls + //$NON-NLS-1$
				"  <autoProvide>Disabled</autoProvide>" + //$NON-NLS-1$
				"  <conveyRegionDesignatorSets>Disabled</conveyRegionDesignatorSets>" + //$NON-NLS-1$
				"  <conveyProducingFederate>Disabled</conveyProducingFederate>" + //$NON-NLS-1$
				"  <attributeScopeAdvisory>Disabled</attributeScopeAdvisory>" + //$NON-NLS-1$
				"  <attributeRelevanceAdvisory>Disabled</attributeRelevanceAdvisory>" + //$NON-NLS-1$
				"  <objectClassRelevanceAdvisory>Disabled</objectClassRelevanceAdvisory>" + //$NON-NLS-1$
				"  <interactionRelevanceAdvisory>Disabled</interactionRelevanceAdvisory>" + //$NON-NLS-1$
				"  <serviceReporting>Disabled</serviceReporting>" + ls +//$NON-NLS-1$
				"  <exceptionReporting>Disabled</exceptionReporting>" + ls +//$NON-NLS-1$
				"  <delaySubscriptionEvaluation>Disabled</delaySubscriptionEvaluation>" + ls +//$NON-NLS-1$
				"  <automaticResignAction>CancelThenDeleteThenDivest</automaticResignAction>" + ls +//$NON-NLS-1$
				"</switches>"//$NON-NLS-1$
		);
	}


	/**
	 * Clean strings by replacing '\' with &quot if required
	 *
	 * @param string
	 *            the string to clean
	 * @return the string cleaned
	 */
	private String cleanStrings(String string) {
		if (string != null) {
			string = string.replace("&", "&amp;"); //$NON-NLS-1$ //$NON-NLS-2$
			string = string.replace("\"", "&quot;"); //$NON-NLS-1$ //$NON-NLS-2$
			string = string.replace("<", "&lt;"); //$NON-NLS-1$ //$NON-NLS-2$
			string = string.replace(">", "&gt;"); //$NON-NLS-1$ //$NON-NLS-2$
			return string;
		} else {
			return ""; //$NON-NLS-1$
		}
	}


	/**
	 * Format a string to remove forbidden characters.
	 *
	 * @param string
	 *            The input string.
	 * @return The string with forbidden characters replaced by underscores.
	 */
	private String formatString(String string) {
		if (string != null) {
			string = string.replace(" ", "_"); //$NON-NLS-1$ //$NON-NLS-2$
			string = string.replace("&", "_"); //$NON-NLS-1$ //$NON-NLS-2$
			string = string.replace("\"", "_"); //$NON-NLS-1$ //$NON-NLS-2$
			string = string.replace("<", "_"); //$NON-NLS-1$ //$NON-NLS-2$
			string = string.replace(">", "_"); //$NON-NLS-1$ //$NON-NLS-2$
			string = string.replace("/", "_"); //$NON-NLS-1$ //$NON-NLS-2$
			string = string.replace("#", "_"); //$NON-NLS-1$ //$NON-NLS-2$
			return string;
		}
		return ""; //$NON-NLS-1$
	}


	/**
	 * Fill the header of the HLA file with some dummy stuff.
	 *
	 * @param buffer
	 *            The buffer to fill.
	 * @param fom
	 *            The FOM
	 */
	private void fillHeader(StringBuffer buffer, ISimModel fom) {

		buffer.append("<?xml version=\"1.0\"?>" + ls + //$NON-NLS-1$
				"<objectModel xsi:schemaLocation=\"http://standards.ieee.org/IEEE1516-2010 http://standards.ieee.org/downloads/1516/1516.2-2010/IEEE1516-DIF-2010.xsd\" xmlns=\"http://standards.ieee.org/IEEE1516-2010\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + ls + //$NON-NLS-1$
				"  <modelIdentification>"+ ls  + //$NON-NLS-1$
				"    <name>" + cleanStrings(fom.getName()) + "</name>" + ls  + //$NON-NLS-1$ //$NON-NLS-2$
				"    <type>FOM</type>" + ls  + //$NON-NLS-1$
				"    <version>1.0</version>"+ ls  + //$NON-NLS-1$
				"    <purpose></purpose>"+ ls  + //$NON-NLS-1$
				"    <applicationDomain></applicationDomain>"+ ls  + //$NON-NLS-1$
				"  </modelIdentification>"+ ls); //$NON-NLS-1$
	}

}
