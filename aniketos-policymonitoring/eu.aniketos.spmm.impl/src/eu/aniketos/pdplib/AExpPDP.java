package eu.aniketos.pdplib;

import org.jdom.Element;

import eu.aniketos.spec.BConst;
import eu.aniketos.spec.BExp;
import eu.aniketos.spec.BGuard;
import eu.aniketos.spec.BIdentifier;
import eu.aniketos.spec.BInvocation;
import eu.aniketos.spec.BOp;
import eu.aniketos.spec.Exp;
import eu.aniketos.spec.AExp;
import eu.aniketos.spec.IGuard;
import eu.aniketos.spec.SGuard;
import security.iit.pdp.EnvException;
import security.iit.pdp.VarEnvironment;

import eu.aniketos.spec.IOp;
import eu.aniketos.spec.IConst;
import eu.aniketos.spec.AInvocation;
import eu.aniketos.spec.AIdentifier;

public abstract class AExpPDP extends AExp {

	/*
    public abstract Element toElement();
    
    public static AExp generateAExp(Element e) {
    	return Exp.generateAExp(e);
    }*/

	
	public static AExpPDP generateAExpPDP(AExp aexp) {
		if(aexp instanceof IOp){
			return new IExpOpPDP(aexp);
		}
		if(aexp instanceof IConst){
			return new IExpConstPDP(aexp);
		}
		if(aexp instanceof AInvocation){
			return new IExpInvocationPDP(aexp);
		}
		if(aexp instanceof AIdentifier){
			return new IExpIdentifierPDP(aexp);
		} else return null;
 	    
  }
	
	
	
	
	
	
    /**
     * evaluate the aritmetical expression
     * @param env: the Environment
     * @return the evaluation
     * @throws EnvException
     */
	public abstract int eval(VarEnvironment env) throws EnvException;
}
