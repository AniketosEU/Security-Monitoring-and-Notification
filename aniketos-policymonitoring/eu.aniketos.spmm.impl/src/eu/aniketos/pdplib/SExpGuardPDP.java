package eu.aniketos.pdplib;

import org.jdom.Element;

import security.iit.pdp.EnvException;
import security.iit.pdp.VarEnvironment;
import eu.aniketos.spec.BExp;
import eu.aniketos.spec.BGuard;
import eu.aniketos.spec.SGuard;
import eu.aniketos.spec.SExp;

public class SExpGuardPDP extends BExpPDP {
	protected SGuard sg; 
	
	public SExpGuardPDP(BExp bexp){
		sg= new SGuard(((SGuard) bexp).type, ((SGuard) bexp).sexp1, ((SGuard) bexp).sexp2);
	}

	 public Element toElement() {
		 return sg.toElement();
	 }
	
    //only the equal operation is currently implemented for booleans
    public boolean eval(VarEnvironment env) throws EnvException{
    	SExpPDP l = SExpPDP.generateSExpPDP(sg.sexp1);
    	SExpPDP r = SExpPDP.generateSExpPDP(sg.sexp2);
    //	return l.eval(env) == r.eval(env);
    	return l.eval(env).equals(r.eval(env));
    	
    }
	
}
