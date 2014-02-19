/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.aniketos.threatrepository.impl.util;

import eu.aniketos.threatrepository.RelationType;
import eu.aniketos.threatrepository.ThreatType;

/** Contains (static) utility functions for the Threat Repository Module.
 *
 * @author balazs
 */
public class TRUtils {

	/** Determines whether a string represents a valid threat-countermeasure relation.
	 * 
	 * @param s The string to check
	 * @return True if s is a valid relation (prevent/remove/detect/mitigate); false if not.
	 */
    public static boolean isRelation(String s) {

        for (RelationType t: RelationType.values()) {
            if (t.name().equals(s))
                return true;
        }
        return false;
    }

	/** Determines whether a string represents a valid threat type.
	 * 
	 * @param s The string to check
	 * @return True if s is a valid threat type (threat/vulnerability/category); false if not.
	 */
    public static boolean isThreatType(String s) {

        for (ThreatType t: ThreatType.values()) {
            if (t.name().equals(s))
                return true;
        }
        return false;
    }
}
