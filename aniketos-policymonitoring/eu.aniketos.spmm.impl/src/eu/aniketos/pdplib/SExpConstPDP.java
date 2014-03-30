package eu.aniketos.pdplib;

import org.jdom.Element;

import security.iit.pdp.VarEnvironment;
import eu.aniketos.spec.SConst;
import eu.aniketos.spec.SExp;

public class SExpConstPDP extends SExpPDP {
	protected SConst sconst;

	public SExpConstPDP(SExp sexp){
		sconst= new SConst(((SConst) sexp).text);
	}
	
	public Element toElement(){
		return sconst.toElement();
	}	
	
    public String eval(VarEnvironment env){
    	return sconst.text;
    }

}
