package eu.aniketos.pdplib;

import org.jdom.Element;

import security.iit.pdp.VarEnvironment;
import eu.aniketos.spec.BConst;
import eu.aniketos.spec.BExp;
import eu.aniketos.spec.AExp;
import eu.aniketos.spec.IConst;

public class IExpConstPDP extends AExpPDP{
	protected IConst iconst;
	
	public IExpConstPDP(AExp aexp){
		iconst= new IConst(((IConst) aexp).val);
	}
	
	public Element toElement(){
		return iconst.toElement();
	}	
	
    public int eval(VarEnvironment env){
    	return iconst.val;
    }
}
