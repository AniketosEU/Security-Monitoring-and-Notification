package eu.aniketos.pdplib;

import org.jdom.Element;

import security.iit.pdp.VarEnvironment;
import eu.aniketos.spec.RConst;
import eu.aniketos.spec.RExp;
import eu.aniketos.spec.IConst;

public class RExpConstPDP extends RExpPDP{
	protected RConst rconst;
	
	public RExpConstPDP(RExp rexp){
		rconst= new RConst(((RConst) rexp).val);
	}
	
	public Element toElement(){
		return rconst.toElement();
	}	
	
    public double eval(VarEnvironment env){
    	return rconst.val;
    }
}