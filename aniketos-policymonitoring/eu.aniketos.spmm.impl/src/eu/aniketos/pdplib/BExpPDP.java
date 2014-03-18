/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.aniketos.pdplib;

import eu.aniketos.spec.BExp;
import eu.aniketos.spec.Exp;
import eu.aniketos.spec.RGuard;
import eu.aniketos.spec.When;
import eu.aniketos.spec.BOp;
import eu.aniketos.spec.BIdentifier;
import eu.aniketos.spec.BConst;
import eu.aniketos.spec.IGuard;
import eu.aniketos.spec.BGuard;
import eu.aniketos.spec.SGuard;
import eu.aniketos.spec.BInvocation;

import org.jdom.Element;

import security.iit.pdp.EnvException;
import security.iit.pdp.VarEnvironment;
import eu.aniketos.pdplib.BExpConstPDP;
import eu.aniketos.pdplib.BExpIdentifierPDP;
import eu.aniketos.pdplib.BExpGuardPDP;
import eu.aniketos.pdplib.SExpGuardPDP;

/**
 *Abstract model of a boolean Expression
 * @author Administrator
 */
public abstract class BExpPDP extends BExp {
	/**
	 * trasfrom it to a DOM element
	 */
//    public abstract Element toElement();

//public static BExpPDP generateBExp(Element e) {
//	return Exp.generateBExp(e);
//}
	
	public static BExpPDP generateBExpPDP(BExp bexp) {
			if(bexp instanceof BConst){
				return new BExpConstPDP(bexp);
			} else if(bexp instanceof BOp){
				return new BExpOpPDP(bexp);
			} else if(bexp instanceof BIdentifier){
				return new BExpIdentifierPDP(bexp);
			} else if(bexp instanceof IGuard){
				return new IExpGuardPDP(bexp);
			} else if(bexp instanceof BInvocation){
				return new BExpInvocationPDP(bexp);
			} else if(bexp instanceof BGuard){
				return new BExpGuardPDP(bexp);
			} else if(bexp instanceof SGuard){
				return new SExpGuardPDP(bexp);
			} else if(bexp instanceof RGuard){
				return new RExpGuardPDP(bexp);
			} else return null;
	 	    
	  }
	
    /**
     * evaluate the boolean expression
     * @param env: the Environment
     * @return the evaluation
     * @throws EnvException
     */
    public abstract boolean eval(VarEnvironment env) throws EnvException;
}
