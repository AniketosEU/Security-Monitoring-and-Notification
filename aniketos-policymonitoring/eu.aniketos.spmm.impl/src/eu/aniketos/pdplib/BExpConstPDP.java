package eu.aniketos.pdplib;
import org.jdom.Element;

import security.iit.pdp.VarEnvironment;
import eu.aniketos.spec.BExp;
import eu.aniketos.spec.Exp;
import eu.aniketos.spec.BConst;

public class BExpConstPDP extends BExpPDP {
	protected BConst bconst;

	public BExpConstPDP(BExp bexp){
		bconst= new BConst(((BConst) bexp).val);
	}
	
	public Element toElement(){
		return bconst.toElement();
	}	
	
    public boolean eval(VarEnvironment env){
    	return bconst.val;
    }
	
}
