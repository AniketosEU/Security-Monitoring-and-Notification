package eu.aniketos.pdplib;

import org.jdom.Element;

import security.iit.pdp.EnvException;
import security.iit.pdp.VarEnvironment;

import eu.aniketos.spec.BExp;
import eu.aniketos.spec.BOp;
import eu.aniketos.spec.Tag;
import eu.aniketos.spec.Exp;

public class BExpOpPDP extends BExpPDP {
	protected BExpPDP lPDP, rPDP;
	protected BOp bop;
	
	
	public BExpOpPDP(BExp bexp){
		bop = new BOp(((BOp) bexp).type, ((BOp) bexp).l, ((BOp) bexp).r);		
    	lPDP = BExpPDP.generateBExpPDP(((BOp) bexp).l);
    	rPDP = BExpPDP.generateBExpPDP(((BOp) bexp).r);
	}
	
	public Element toElement(){
		return bop.toElement();
	}	

    public boolean eval(VarEnvironment env) throws EnvException{
    	if(bop.type.equals(Tag.not_tag))
    		return !lPDP.eval(env);
    	else if(bop.type.equals(Tag.and_tag))
    		return lPDP.eval(env) && rPDP.eval(env);
    	else //if(type.equals(or_tag))
    		return lPDP.eval(env) || rPDP.eval(env);
    }
	
}
