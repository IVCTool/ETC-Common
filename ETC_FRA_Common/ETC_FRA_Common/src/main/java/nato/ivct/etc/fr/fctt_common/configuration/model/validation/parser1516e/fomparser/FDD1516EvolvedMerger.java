package nato.ivct.etc.fr.fctt_common.configuration.model.validation.parser1516e.fomparser;

import nato.ivct.etc.fr.fctt_common.utils.FCTT_Constant;
import nato.ivct.etc.fr.fctt_common.utils.FCTT_Environment;

import org.dom4j.Element;

import java.io.File;
import java.util.Iterator;

/**
 * Merges BOM files into a main FDD file.<br>
 * Files must have the HLA 1516 2010 format.
 */
public class FDD1516EvolvedMerger
{

	// Root of the main XML document
	private Element _eFDD;
	private File _schemaFile;

	public FDD1516EvolvedMerger(File fddFile, File pSchemaFile) throws Exception
	{
		_schemaFile = pSchemaFile;

		_eFDD = XMLUtils.readFile(fddFile,_schemaFile);
	}

	private File getSchemaFile() {
		File file = FCTT_Environment.getXSD_DIF_Path().toFile();
		return file;
	}

	public void merge(File bomFile) throws Exception
	{
		Element eBOM = XMLUtils.readFile(bomFile,_schemaFile);

		// merge objects
		Element eSrc = eBOM.element("objects");
		if (eSrc != null)
		{
			Element eDst = XMLUtils.getOrCreateElt(_eFDD, "objects");
			Iterator it = eSrc.elementIterator();
			while (it.hasNext())
			{
				merge((Element) it.next(), eDst);
			}
		}

		// merge interactions
		eSrc = eBOM.element("interactions");
		if (eSrc != null)
		{
			Element eDst = XMLUtils.getOrCreateElt(_eFDD, "interactions");
			Iterator it = eSrc.elementIterator();
			while (it.hasNext())
			{
				merge((Element) it.next(), eDst);
			}
		}

		Element eDataTypeSrc = eBOM.element("dataTypes");
		Element eDataTypeDst = _eFDD.element("dataTypes");
		if (eDataTypeSrc!=null)
		{
			addElementsInside(eDataTypeSrc.element("basicDataRepresentations"),XMLUtils.getOrCreateElt(eDataTypeDst, "basicDataRepresentations"));
			addElementsInside(eDataTypeSrc.element("simpleDataTypes"), XMLUtils.getOrCreateElt(eDataTypeDst, "simpleDataTypes"));
			addElementsInside(eDataTypeSrc.element("enumeratedDataTypes"), XMLUtils.getOrCreateElt(eDataTypeDst, "enumeratedDataTypes"));
			addElementsInside(eDataTypeSrc.element("arrayDataTypes"), XMLUtils.getOrCreateElt(eDataTypeDst, "arrayDataTypes"));
			addElementsInside(eDataTypeSrc.element("fixedRecordDataTypes"), XMLUtils.getOrCreateElt(eDataTypeDst, "fixedRecordDataTypes"));
			addElementsInside(eDataTypeSrc.element("variantRecordDataTypes"), XMLUtils.getOrCreateElt(eDataTypeDst, "variantRecordDataTypes"));
		}

		// merge services
		Element eServ = eBOM.element("serviceUtilization");
		if (eServ != null)
		{
			Element eDst = XMLUtils.getOrCreateElt(_eFDD, "serviceUtilization");
			Iterator it = eServ.elementIterator();
			while (it.hasNext())
			{
				Element elem = (Element)it.next();


				Iterator itdest = eDst.elementIterator();
				boolean findelem = false;
				while (itdest.hasNext())
				{
					Element elemdst = (Element)itdest.next();
					if (elemdst.getName().equals(elem.getName())) {
						if (elemdst.attributeValue("isUsed").equals("true")) {

						} else {
							elemdst.addAttribute("isUsed",elem.attributeValue("isUsed"));
						}
						findelem = true;
						break;
					}
				}
				if (!findelem)
				{
					eDst.add(elem.createCopy());
				}
			}
		}
	}

	public void saveAs(File fddFile) throws Exception
	{
		XMLUtils.saveFile(_eFDD, fddFile, true);
	}

	private void merge(Element eSrc, Element eDst)
	{
		Element dstChild = getNamedElement(eDst, eSrc.elementText("name"));

		if (dstChild != null)
		{
			Iterator it = eSrc.elementIterator();
			while (it.hasNext())
			{
				merge((Element) it.next(), dstChild);
			}
		}
		else
		{

			String lSrcName = eSrc.elementText("name");
			String lDestName =eDst.elementText("name");
			String lParentSrcName =eSrc.getParent().elementText("name");

			if (lSrcName == null) 
			{
				if (!lDestName.equals(lParentSrcName)) 
				{
					eDst.add(eSrc.createCopy());
				}
				// an element with the same name exist
				else
				{
					String lShareSrc = eSrc.getParent().elementText("sharing");
					String lShareDest = eDst.elementText("sharing");

					if (null != lShareSrc)
					{
						if(null != lShareDest)
						{
							if (!lShareSrc.equals(lShareDest))
							{
								if (eDst.element("sharing") !=null) 
								{
									String lSharing = getMergeSharing(lShareSrc, lShareDest);
									eDst.element("sharing").setText(lSharing);
								}

							}
						}
					}
				}
			} 
			else 
			{
				eDst.add(eSrc.createCopy());
			}
		}
	}

	/**
	 * Method to merge sharing information 
	 * @param pShar1
	 * @param pShar2
	 * @return
	 */
	private String getMergeSharing(String pShar1, String pShar2) {
		String lSharing=FCTT_Constant.SHARE_NEITHER;
		if (pShar1.equals(FCTT_Constant.SHARE_NEITHER)  || pShar2.equals(FCTT_Constant.SHARE_NEITHER))
		{
			if (pShar1.equals(FCTT_Constant.SHARE_NEITHER))
			{
				lSharing = pShar2;
			}
			else
			{
				lSharing = pShar1;
			}
		}
		else
		{
			if (pShar1.equals(pShar2)) 
			{
				lSharing = pShar1;
			}
			else
			{
				lSharing = FCTT_Constant.SHARE_PUBLISH_SUBSCRIBE;
			}
		}
		return lSharing;
	}


	private void addElementsInside(Element eSrc, Element eDst)
	{
		if (eSrc != null)
		{
			Iterator iter = eSrc.elementIterator();
			while (iter.hasNext())
			{
				Element elem = (Element) iter.next();
				if (!containsNamedElement(eDst, elem.elementText("name")))
				{
					eDst.add(elem.createCopy());
				}
			}
		}
	}

	private boolean containsNamedElement(Element eParent, String name)
	{
		return getNamedElement(eParent, name) != null;
	}

	private Element getNamedElement(Element eParent, String name)
	{
		Iterator iter = eParent.elementIterator();
		while (iter.hasNext())
		{
			Element elem = (Element) iter.next();
			String elementText = elem.elementText("name");
			if ((elementText!=null)&&(elementText.equals(name)))
			{
				return elem;
			}
		}
		return null;
	}

}