package eu.aniketos.pdplib;

import org.jdom.Element;

import security.iit.pdp.EnvException;
import security.iit.pdp.VarEnvironment;


import eu.aniketos.spec.BExp;
import eu.aniketos.spec.BGuard;
import eu.aniketos.spec.BIdentifier;

public class BExpGuardPDP extends BExpPDP{
	protected BGuard bg; 
	
	public BExpGuardPDP(BExp bexp){
		bg= new BGuard(((BGuard) bexp).type, ((BGuard) bexp).l, ((BGuard) bexp).r);
	}

	 public Element toElement() {
		 return bg.toElement();
	 }
	
    //only the equal operation is currently implemented for booleans
    public boolean eval(VarEnvironment env) throws EnvException{
    	BExpPDP l = BExpPDP.generateBExpPDP(bg.l);
    	BExpPDP r = BExpPDP.generateBExpPDP(bg.r);
    	return l.eval(env) == r.eval(env);
    	
    }
}
