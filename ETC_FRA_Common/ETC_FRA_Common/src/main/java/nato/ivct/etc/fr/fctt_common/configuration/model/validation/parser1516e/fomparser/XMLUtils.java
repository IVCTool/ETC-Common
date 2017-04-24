package nato.ivct.etc.fr.fctt_common.configuration.model.validation.parser1516e.fomparser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;

import org.apache.xerces.impl.Constants;
import org.apache.xerces.parsers.XMLGrammarPreparser;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XMLGrammarPoolImpl;
import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.xml.sax.XMLReader;


public class XMLUtils {
	
	public static HashMap<String, XMLReader> XMLReaders_Cache = new HashMap<String, XMLReader>();

	public static boolean XMLReaders_Cache_Flag = false;


	/**
	 * Cree un reader XML en lui passant un nom de fichier de schema qui sera utilise pour valider
	 * les documents parses par le reader. Le schema n'est analyse qu'une seule fois. Un cache des
	 * readers est utilise pour economiser le CPU. Un flag statique, par defaut a false, controle la
	 * mise en oeuvre de ce cache.
	 * A VOIR : l'inclusion de xsd ne marche pas si on charge le xsd via une URL
	 * 
	 * @param schemaUrl nom d'un fichier schema.
	 * @return un reader avec validation.
	 */
	public static XMLReader newXMLReader(URL schemaUrl)
	{
		if (XMLReaders_Cache_Flag)
		{
			if (XMLReaders_Cache.containsKey(schemaUrl.getPath()))
				return XMLReaders_Cache.get(schemaUrl.getPath());
		}

		SymbolTable sym = new SymbolTable();
		XMLGrammarPreparser preparser = new XMLGrammarPreparser(sym);
		XMLGrammarPoolImpl grammarPool = new XMLGrammarPoolImpl();
		preparser.registerPreparser(XMLGrammarDescription.XML_SCHEMA, null);
		preparser.setProperty(GRAMMAR_POOL, grammarPool);
		preparser.setFeature(VALIDATION_FEATURE_ID, true);
		preparser.setFeature(SCHEMA_VALIDATION_FEATURE_ID, true);

		boolean validation = false;
		try
		{
			preparser.preparseGrammar(XMLGrammarDescription.XML_SCHEMA, new XMLInputSource(null, null, null, schemaUrl.openStream(), null));
			validation = true;
		}
		catch (Exception e)
		{
			SendTrace.sendError("Error when reading the schema '" + schemaUrl + "'. Validation is disabled.", e);
		}

		XMLReader parser;
		if (validation)
		{
			parser = new org.apache.xerces.parsers.SAXParser(sym, grammarPool);
			try
			{
				parser.setFeature(VALIDATION_FEATURE_ID, true);
				parser.setFeature(SCHEMA_VALIDATION_FEATURE_ID, true);
			}
			catch (Exception ex)
			{
				SendTrace.sendWarning("Unexpected error: check the source code: " + ex.getMessage());
			}
		}
		else
		{
			parser = new org.apache.xerces.parsers.SAXParser();
		}

		if (XMLReaders_Cache_Flag)
		{
			XMLReaders_Cache.put(schemaUrl.getPath(), parser);
		}
		return parser;
	}


	/**
	 * Cree un reader XML en lui passant un nom de fichier de schema qui sera utilise pour valider
	 * les documents parses par le reader. Le schema n'est analyse qu'une seule fois. Un cache des
	 * readers est utilise pour economiser le CPU. Un flag statique, par defaut a false, controle la
	 * mise en oeuvre de ce cache.
	 * 
	 * @param schemaFile nom d'un schema.
	 * @return un reader avec validation.
	 */
	public static XMLReader newXMLReader(String schemaFile)
	{
		if (XMLReaders_Cache_Flag)
		{
			if (XMLReaders_Cache.containsKey(schemaFile))
				return XMLReaders_Cache.get(schemaFile);
		}

		SymbolTable sym = new SymbolTable();
		XMLGrammarPreparser preparser = new XMLGrammarPreparser(sym);
		XMLGrammarPoolImpl grammarPool = new XMLGrammarPoolImpl();
		preparser.registerPreparser(XMLGrammarDescription.XML_SCHEMA, null);
		preparser.setProperty(GRAMMAR_POOL, grammarPool);
		preparser.setFeature(VALIDATION_FEATURE_ID, true);
		preparser.setFeature(SCHEMA_VALIDATION_FEATURE_ID, true);

		boolean validation = false;
		try
		{
			preparser.preparseGrammar(XMLGrammarDescription.XML_SCHEMA, new XMLInputSource(null, schemaFile, null));
			validation = true;
		}
		catch (Exception e)
		{
			SendTrace.sendError("Error when reading the schema '" + schemaFile + "'. Validation is disabled.", e);
		}
		

		XMLReader parser;
		if (validation)
		{
			parser = new org.apache.xerces.parsers.SAXParser(sym, grammarPool);
			try
			{
				parser.setFeature(VALIDATION_FEATURE_ID, true);
				parser.setFeature(SCHEMA_VALIDATION_FEATURE_ID, true);
			}
			catch (Exception ex)
			{
				SendTrace.sendWarning("Unexpected error: check the source code." + ex.getMessage());
			}
		}
		else
		{
			parser = new org.apache.xerces.parsers.SAXParser();
		}

		if (XMLReaders_Cache_Flag)
		{
			XMLReaders_Cache.put(schemaFile, parser);
		}
		return parser;
	}


	/**
	 * Reads an XML file with a given schema as reference.
	 * 
	 * @param file file to read.
	 * @param schemaFile schema file, this schema can be null.
	 * @return an XML element if the file is read successfully, null otherwise.
	 * @throws Exception read error
	 */
	public static Element readFile(File file, File schemaFile) throws Exception
	{
		return readFile(file, schemaFile, false);
	}


	/**
	 * Reads an XML file with a given schema as reference.
	 * 
	 * @param file file to read.
	 * @param schemaFile schema file, this schema can be null.
	 * @param needsInclude With URL, includes in XSD files are not resolved.
	 * Before finding and solving the problem, this method is added to propose a solution
	 * without rewriting the de-serialization code. See {@link #newXMLReader(URL)} method comments.
	 * @return an XML element if the file is read successfully, null otherwise.
	 * @throws Exception read error
	 */
	public static Element readFile(File file, File schemaFile, boolean needsInclude) throws Exception
	{
		if (file == null)
		{
			throw new IllegalArgumentException();
		}

		if (schemaFile == null)
		{
			try
			{
				return new SAXReader().read(new BufferedInputStream(new FileInputStream(file))).getRootElement();
			}
			catch (Exception e)
			{
				throw(e);
			}  
		}

		try
		{
			SAXReader XMLin = null;
			if(needsInclude)
			{
				XMLin = new SAXReader(newXMLReader(schemaFile.getPath()));
			}
			else
			{
				XMLin = new SAXReader(newXMLReader(schemaFile.toURI().toURL()));
			}
			XMLin.setValidation(true);
			return XMLin.read(file).getRootElement();
		}
		catch (Exception e)
		{
			throw(e);
		}
	}

	/**
	 * Reads an XML file with a given schema as reference.
	 * 
	 * @param file file to read.
	 * @param schemaFileName name of the schema corresponding to the file to read, this schema can be null.
	 * @return an XML element if the file is read successfully, null otherwise.
	 * @throws Exception read error
	 */
	public static Element readFile(File file, String schemaFileName) throws Exception
	{
		return readFile(file, schemaFileName!=null?new File(schemaFileName):null);
	}

	/**
	 * Reads an XML file.
	 * 
	 * @param file file to read (with a null schema).
	 * @return an XML element if the file is read successfully, null otherwise.
	 * @throws Exception read error
	 */
	public static Element readFile(File file) throws Exception
	{
		return readFile(file, (File) null);
	}

	/** 
	 * Method that return the unique XML element with 'name'  
	 * or create it if needed. 
	 * @param parent file to read (with a null schema).
	 * @param name file to read (with a null schema).
	 * @return an XML element.
	 */
	public static Element getOrCreateElt(Element parent, String name)
	{
		Element sub;
		if ( parent == null )
			return DocumentHelper.createElement(name);
		else if (null != (sub = parent.element(name)))
			return sub;
		else 
			return parent.addElement(name);
	}


	/**
	 * Save the document of an XML element as an XML file with UTF-8 encoding.<br>
	 * If the parent directories do not exist,
	 * the method {@link File#mkdirs()} is called on the file given as parameter.
	 * If the element is not contained in an XML document, one is created.
	 * 
	 * @param element XML element to save (this element is the root XML element).
	 * @param file the destination file. The file is replaced if it exists already.
	 * @param prettyPrint true to save file with line returns and spaces.   
	 * @param withoutTrim true to remove spaces at the beginning of each line. 
	 * @throws IOException I/O error
	 */
	public static void saveFile(Element element, File file, boolean prettyPrint, boolean withoutTrim) throws IOException
	{
		if (file.getParentFile() != null)
		{
			file.getParentFile().mkdirs();
		}
		save(element, new FileOutputStream(file), prettyPrint, withoutTrim);
	}


	/**
	 * Same as {@link #saveFile(Element, File, boolean, boolean)} 
	 * with withoutTrim=true
	 * @param element XML element to save (this element is the root XML element).
	 * @param file the destination file. The file is replaced if it exists already.
	 * @param prettyPrint true to save file with line returns and spaces.   
	 * @throws IOException I/O error
	 */
	public static void saveFile(Element element, File file, boolean prettyPrint) throws IOException
	{
		saveFile(element, file, prettyPrint, true);
	}


	/**
	 * Same as {@link #save(Element, OutputStream, boolean, boolean)} 
	 * with prettyPrint=true 
	 * @param element XML element to save (this element is the root XML element).
	 * @param out the destination stream. The file is replaced if it exists already.
	 * @param prettyPrint true to save file with line returns and spaces.   
	 * @throws IOException I/O error
	 */
	public static void save(Element element, OutputStream out, boolean prettyPrint) throws IOException
	{
		save(element, out, prettyPrint, true);
	}


	/**
	 * Save the document of an XML element in a stream with UTF-8 encoding.<br>
	 * If the element is not contained in an XML document, one is created.
	 * 
	 * @param element XML element to save (this element is the root XML element).
	 * @param out the destination stream. The file is replaced if it exists already.
	 * @param prettyPrint true to save file with line returns. 
	 * @param withoutTrim true to add spaces at the beginning of each line matching the depth of XML element.
	 * @throws IOException I/O error
	 */
	public static void save(Element element, OutputStream out, boolean prettyPrint, boolean withoutTrim) throws IOException
	{
		OutputFormat format = null;
		if (prettyPrint)
		{
			format = OutputFormat.createPrettyPrint();
			if ( withoutTrim )
			{
				// Dans ce cas, pas de trim sur le texte
				format.setTrimText(false);
			}
		}
		else
		{
			format = OutputFormat.createCompactFormat();
		}
		format.setEncoding("UTF-8");
		format.setNewlines(false);
		
		XMLWriter writer = new XMLWriter(out, format);
		if (element.getDocument() == null)
		{
			writer.write(DocumentHelper.createDocument(element));
		}
		else
		{
			writer.write(element.getDocument());
		}
		writer.close();
	}

	/** Property identifier: grammar pool. */
	private static final String GRAMMAR_POOL = 
		Constants.XERCES_PROPERTY_PREFIX + Constants.XMLGRAMMAR_POOL_PROPERTY;

	/** Validation feature id (http://xml.org/sax/features/validation). */
	private static final String VALIDATION_FEATURE_ID = 
		"http://xml.org/sax/features/validation";

	/** Schema validation feature id (http://apache.org/xml/features/validation/schema). */
	private static final String SCHEMA_VALIDATION_FEATURE_ID = 
		"http://apache.org/xml/features/validation/schema";


}
