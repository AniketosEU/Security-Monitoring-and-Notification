package eu.aniketos.pdplib;

import org.jdom.Element;

import eu.aniketos.spec.AExp;
import eu.aniketos.spec.RExp;
import eu.aniketos.spec.ROp;
import eu.aniketos.spec.RConst;
import eu.aniketos.spec.RInvocation;
import eu.aniketos.spec.RIdentifier;


import security.iit.pdp.EnvException;
import security.iit.pdp.VarEnvironment;


public abstract class RExpPDP extends RExp {

	/*
    public abstract Element toElement();
    
    public static AExp generateAExp(Element e) {
    	return Exp.generateAExp(e);
    }*/

	
	public static RExpPDP generateRExpPDP(RExp rexp) {
		if(rexp instanceof ROp){
			return new RExpOpPDP(rexp);
		}
		if(rexp instanceof RConst){
			return new RExpConstPDP(rexp);
		}
		if(rexp instanceof RInvocation){
			return new RExpInvocationPDP(rexp);
		}
		if(rexp instanceof RIdentifier){
			return new RExpIdentifierPDP(rexp);
		} else return null;
 	    
  }
	
	
	
	
	
	
    /**
     * evaluate the aritmetical expression
     * @param env: the Environment
     * @return the evaluation
     * @throws EnvException
     */
	public abstract double eval(VarEnvironment env) throws EnvException;
}
