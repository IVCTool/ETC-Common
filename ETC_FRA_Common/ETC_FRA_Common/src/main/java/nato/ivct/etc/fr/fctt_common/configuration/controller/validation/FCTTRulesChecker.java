package nato.ivct.etc.fr.fctt_common.configuration.controller.validation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.probatron.Session;
import org.probatron.ValidationReport;

import nato.ivct.etc.fr.fctt_common.configuration.model.validation.schematron.generated.FailedAssert;
import nato.ivct.etc.fr.fctt_common.configuration.model.validation.schematron.generated.SchematronOutput;

/**
 * This class check the SOM with the SCHEMATRON file containing the rules
 */
public class FCTTRulesChecker 
{
	/**
	 * Path to file containing the rules
	 */
	private static final String FILE_RULES = "rules_1516_2010.xml";

	/**
	 * List of the assert
	 */
	private ArrayList<String> mAssert;

	/**
	 * Constructor
	 */
	public FCTTRulesChecker() {
		mAssert = new ArrayList<String>();
	}

	/**
	 * Use SCHEMATRON to check the rules
	 * @param pXMLFileToValidate XML file to validate
	 * @return true if rules check is ok
	 * @throws JAXBException JAXB exception
	 * @throws IOException IO exception
	 */
	public boolean checkRules(String pXMLFileToValidate) throws JAXBException, IOException {

		boolean lreturn= false;
		ValidationReport lvalidationReport = null;
		List<Object> llistReport = null;

		// 
		// Call SCHEMATRON
		//
		Session sess = new Session();
// 2017/08/21 RMA Begin modification
// In order to avoid using resource file in bin/resources directory and using file in src/main/resources directory
//		Path lRulesPath = Paths.get(FCTT_Environment.getPathResources().toString(), FILE_RULES);
		// Create a temporary file
		final File lRulesFile = File.createTempFile("Rules", ".xml");
		final Path lRulesPath = lRulesFile.toPath();
		// If temporary file already exist, delete it
		if (Files.exists(lRulesPath))
		{
			Files.delete(lRulesPath);
		}
		// Copy stream file (in jar) to temporary file
		try (final InputStream lRulesStream = this.getClass().getClassLoader().getResourceAsStream(FILE_RULES);
				)	{
			final long nbCopies = Files.copy(lRulesStream, lRulesPath);
		}
// 2017/08/21 RMA End modification
		sess.setSchemaDoc(lRulesPath.toUri().toString());
		lvalidationReport = sess.doValidation(new File(pXMLFileToValidate).toURI().toString());

		//
		// Read the validation report to get the assert list
		//
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		lvalidationReport.streamOut(bo);
		ByteArrayInputStream in = new ByteArrayInputStream(bo.toByteArray());

		JAXBContext lContext;
		lContext = JAXBContext.newInstance(SchematronOutput.class);
		Unmarshaller lUnmarshaller = lContext.createUnmarshaller();
		SchematronOutput schOutput = (SchematronOutput) lUnmarshaller.unmarshal(in);
		llistReport = schOutput.getActivePatternAndFiredRuleAndFailedAssert();

		if (llistReport.size() > 0) 
		{
			lreturn = false;
			for (Object lObj : llistReport) 
			{
				if (lObj instanceof FailedAssert) 
				{
					FailedAssert fa = (FailedAssert) lObj;
					mAssert.add(fa.getText().replaceAll("\t+", " "));
				}
			}
		} 
		else 
		{
			lreturn = true;
		}
		
// 2017/08/21 RMA Begin modification
		// Delete temporary file
		Files.delete(lRulesPath);
// 2017/08/21 RMA End modification

		return lreturn;
	}

	/**
	 * @return the list of assert
	 */
	public ArrayList<String> getListAssert() 
	{
		return mAssert;
	}

}
