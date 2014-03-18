package eu.aniketos.pdplib;

import org.jdom.Element;

import security.iit.pdp.EnvException;
import security.iit.pdp.VarEnvironment;
import eu.aniketos.spec.BExp;
import eu.aniketos.spec.IOp;
import eu.aniketos.spec.AExp;
import eu.aniketos.spec.SGuard;
import eu.aniketos.spec.Tag;
import eu.aniketos.pdplib.AExpPDP;

public class IExpOpPDP extends AExpPDP{
	protected IOp iop;

    public IExpOpPDP(String tag, AExp e1, AExp e2) {
    	iop = new IOp(tag, e1, e2);
    }
	
	public IExpOpPDP(Element e){
		iop = new IOp(e);
	}  
	   
	public Element toElement() {
		return iop.toElement();
	}
	
	public IExpOpPDP(AExp aexp){
		iop= new IOp(((IOp) aexp).type, ((IOp) aexp).exp1, ((IOp) aexp).exp2);
	}
	
	@Override
	public int eval(VarEnvironment env) throws EnvException {
		AExpPDP l = AExpPDP.generateAExpPDP(iop.exp1);
    	AExpPDP r = AExpPDP.generateAExpPDP(iop.exp2);
		if(iop.type.equals(Tag.sum_tag)){
			return l.eval(env) + r.eval(env);
		}
		else if(iop.type.equals(Tag.dif_tag))
			return l.eval(env) - r.eval(env);
		else if(iop.type.equals(Tag.mul_tag))
			return l.eval(env) * r.eval(env);
		else if(iop.type.equals(Tag.mod_tag))
			return l.eval(env) % r.eval(env);
		else throw new EnvException("Type of IOperation not recognized");
	}
}
