//
// Ce fichier a ete genere par l'implementation de reference JavaTM Architecture for XML Binding (JAXB), v2.2.11 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportee a ce fichier sera perdue lors de la recompilation du schema source. 
// Genere le : 2015.08.28 a 10:15:03 AM CEST 
//


package nato.ivct.etc.fr.fctt_common.configuration.model.validation.schematron.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Classe Java pour anonymous complex type.
 * 
 * <p>Le fragment de schema suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://purl.oclc.org/dsdl/svrl}text" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{http://purl.oclc.org/dsdl/svrl}ns-prefix-in-attribute-values" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;sequence maxOccurs="unbounded"&gt;
 *           &lt;element ref="{http://purl.oclc.org/dsdl/svrl}active-pattern"/&gt;
 *           &lt;sequence maxOccurs="unbounded"&gt;
 *             &lt;element ref="{http://purl.oclc.org/dsdl/svrl}fired-rule"/&gt;
 *             &lt;choice maxOccurs="unbounded" minOccurs="0"&gt;
 *               &lt;element ref="{http://purl.oclc.org/dsdl/svrl}failed-assert"/&gt;
 *               &lt;element ref="{http://purl.oclc.org/dsdl/svrl}successful-report"/&gt;
 *             &lt;/choice&gt;
 *           &lt;/sequence&gt;
 *         &lt;/sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="title" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *       &lt;attribute name="phase" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" /&gt;
 *       &lt;attribute name="schemaVersion" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "text",
    "nsPrefixInAttributeValues",
    "activePatternAndFiredRuleAndFailedAssert"
})
@XmlRootElement(name = "schematron-output")
public class SchematronOutput {

    protected List<String> text;
    @XmlElement(name = "ns-prefix-in-attribute-values")
    protected List<NsPrefixInAttributeValues> nsPrefixInAttributeValues;
    @XmlElements({
        @XmlElement(name = "active-pattern", required = true, type = ActivePattern.class),
        @XmlElement(name = "fired-rule", required = true, type = FiredRule.class),
        @XmlElement(name = "failed-assert", required = true, type = FailedAssert.class),
        @XmlElement(name = "successful-report", required = true, type = SuccessfulReport.class)
    })
    protected List<Object> activePatternAndFiredRuleAndFailedAssert;
    @XmlAttribute(name = "title")
    @XmlSchemaType(name = "anySimpleType")
    protected String title;
    @XmlAttribute(name = "phase")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String phase;
    @XmlAttribute(name = "schemaVersion")
    @XmlSchemaType(name = "anySimpleType")
    protected String schemaVersion;

    /**
     * Gets the value of the text property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the text property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getText().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     * @return the value of the text property
     */
    public List<String> getText() {
        if (text == null) {
            text = new ArrayList<String>();
        }
        return this.text;
    }

    /**
     * Gets the value of the nsPrefixInAttributeValues property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nsPrefixInAttributeValues property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNsPrefixInAttributeValues().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NsPrefixInAttributeValues }
     * 
     * 
     * @return the value of the text property
     */
    public List<NsPrefixInAttributeValues> getNsPrefixInAttributeValues() {
        if (nsPrefixInAttributeValues == null) {
            nsPrefixInAttributeValues = new ArrayList<NsPrefixInAttributeValues>();
        }
        return this.nsPrefixInAttributeValues;
    }

    /**
     * Gets the value of the activePatternAndFiredRuleAndFailedAssert property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the activePatternAndFiredRuleAndFailedAssert property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getActivePatternAndFiredRuleAndFailedAssert().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ActivePattern }
     * {@link FiredRule }
     * {@link FailedAssert }
     * {@link SuccessfulReport }
     * 
     * 
     * @return the value of the text property
     */
    public List<Object> getActivePatternAndFiredRuleAndFailedAssert() {
        if (activePatternAndFiredRuleAndFailedAssert == null) {
            activePatternAndFiredRuleAndFailedAssert = new ArrayList<Object>();
        }
        return this.activePatternAndFiredRuleAndFailedAssert;
    }

    /**
     * Obtient la valeur de la propriete title.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Definit la valeur de la propriete title.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Obtient la valeur de la propriete phase.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhase() {
        return phase;
    }

    /**
     * Definit la valeur de la propriete phase.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhase(String value) {
        this.phase = value;
    }

    /**
     * Obtient la valeur de la propriete schemaVersion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSchemaVersion() {
        return schemaVersion;
    }

    /**
     * Definit la valeur de la propriete schemaVersion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSchemaVersion(String value) {
        this.schemaVersion = value;
    }

}
