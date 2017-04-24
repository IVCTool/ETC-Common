//
// Ce fichier a ete genere par l'implementation de reference JavaTM Architecture for XML Binding (JAXB), v2.2.11 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportee a ce fichier sera perdue lors de la recompilation du schema source. 
// Genere le : 2015.08.28 a 10:15:03 AM CEST 
//


package nato.ivct.etc.fr.fctt_common.configuration.model.validation.schematron.generated;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.oclc.purl.dsdl.svrl package. 
 * <p>An ObjectFactory allows you to programmatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Text_QNAME = new QName("http://purl.oclc.org/dsdl/svrl", "text");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.oclc.purl.dsdl.svrl
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SchematronOutput }
     * 
     * @return a SchematronOutput instance
     */
    public SchematronOutput createSchematronOutput() {
        return new SchematronOutput();
    }

    /**
     * Create an instance of {@link NsPrefixInAttributeValues }
     * 
     * @return a NsPrefixInAttributeValues instance
     */
    public NsPrefixInAttributeValues createNsPrefixInAttributeValues() {
        return new NsPrefixInAttributeValues();
    }

    /**
     * Create an instance of {@link ActivePattern }
     * 
     * @return an ActivePattern instance
     */
    public ActivePattern createActivePattern() {
        return new ActivePattern();
    }

    /**
     * Create an instance of {@link FiredRule }
     * 
     * @return a FiredRule instance
     */
    public FiredRule createFiredRule() {
        return new FiredRule();
    }

    /**
     * Create an instance of {@link FailedAssert }
     * 
     * @return a FailedAssert instance
     */
    public FailedAssert createFailedAssert() {
        return new FailedAssert();
    }

    /**
     * Create an instance of {@link DiagnosticReference }
     * 
     * @return a DiagnosticReference instance
     */
    public DiagnosticReference createDiagnosticReference() {
        return new DiagnosticReference();
    }

    /**
     * Create an instance of {@link SuccessfulReport }
     * 
     * @return a SuccessfulReport instance
     */
    public SuccessfulReport createSuccessfulReport() {
        return new SuccessfulReport();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     * @param value Text to create
     * @return a JAXBElement instance
     */
    @XmlElementDecl(namespace = "http://purl.oclc.org/dsdl/svrl", name = "text")
    public JAXBElement<String> createText(String value) {
        return new JAXBElement<String>(_Text_QNAME, String.class, null, value);
    }

}
