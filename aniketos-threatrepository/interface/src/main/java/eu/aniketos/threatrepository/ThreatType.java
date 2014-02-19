/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.aniketos.threatrepository;

/** This enum defines the type of threat to search for. The elements are as follows:
 * - <b>threat</b> refers to a high-level threat concept, such as <i>Improper input validation</i>.
 * - <b>vulnerability</b> refers to specific vulnerabilities, such as <i>SQL injection</i>.
 * - <b>category</b> refers to threats and vulnerabilities that may belong to a particular category, such as <i>Java</i>.
 * @author balazs
 */
public enum ThreatType {
    /** <b>threat</b> refers to a high-level threat concept, such as <i>Improper input validation</i>. */
	threat,
	/** <b>vulnerability</b> refers to specific vulnerabilities, such as <i>SQL injection</i>. */
    vulnerability,
    /** <b>category</b> refers to threats and vulnerabilities that may belong to a particular category, such as <i>Java</i>. */
    category,
};