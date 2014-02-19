/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.aniketos.threatrepository;

/** This enum defines the type of relation between a threat and a countermeasure, as follows:
 * - <b>prevent</b> refers to countermeasures that can prevent the threat from endangering the asset.
 * - <b>remove</b> refers to countermeasures that can eliminate the threat.
 * - <b>detect</b> refers to countermeasures that can detect whether the threat is present in a service.
 * - <b>mitigate</b> refers to countermeasures that limit or reduce the risk associated with the threat.
 * @author balazs
 */
public enum RelationType {
    /** <b>prevent</b> refers to countermeasures that can prevent the threat from endangering the asset. */
	prevent,
	/** <b>remove</b> refers to countermeasures that can eliminate the threat. */
	remove,
	/** <b>detect</b> refers to countermeasures that can detect whether the threat is present in a service. */
	detect,
	/** <b>mitigate</b> refers to countermeasures that limit or reduce the risk associated with the threat. */
	mitigate,
};
