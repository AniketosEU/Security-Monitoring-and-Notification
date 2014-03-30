package eu.aniketos.pdplib;

import org.jdom.Element;

import security.iit.pdp.EnvException;
import security.iit.pdp.VarEnvironment;
import eu.aniketos.spec.AExp;
import eu.aniketos.spec.RExp;
import eu.aniketos.spec.ROp;
import eu.aniketos.spec.Tag;


public class RExpOpPDP extends RExpPDP{
	protected ROp rop;

    public RExpOpPDP(String tag, AExp e1, AExp e2) {
    	rop = new ROp(tag, e1, e2);
    }

    public RExpOpPDP(String tag, RExp e1, AExp e2) {
    	rop = new ROp(tag, e1, e2);
    }
    
    public RExpOpPDP(String tag, AExp e1, RExp e2) {
    	rop = new ROp(tag, e1, e2);
    }
    
    public RExpOpPDP(String tag, RExp e1, RExp e2) {
    	rop = new ROp(tag, e1, e2);
    }
    
    
	public RExpOpPDP(Element e){
		rop = new ROp(e);
	}  
	   
	public Element toElement() {
		return rop.toElement();
	}
	
	public RExpOpPDP(RExp rexp){
		rop= new ROp(((ROp) rexp).type, ((ROp) rexp).exp1, ((ROp) rexp).exp2);
	}
	
	@Override
	public double eval(VarEnvironment env) throws EnvException {
		RExpPDP l = RExpPDP.generateRExpPDP(rop.exp1);
		AExpPDP il=null;
		if(l==null) il = AExpPDP.generateAExpPDP(rop.aexp1);
    	RExpPDP r = RExpPDP.generateRExpPDP(rop.exp2);
    	AExpPDP ir=null;
    	if(r==null) ir = AExpPDP.generateAExpPDP(rop.aexp2);
    	
		if(rop.type.equals(Tag.rsum_tag))
			if(l==null && r==null) {return (double) il.eval(env) + ir.eval(env);}
			else
				if(l==null) {return il.eval(env) + r.eval(env);}
				else
					if(r==null) {return (double) l.eval(env) + ir.eval(env);}
					else return l.eval(env) + r.eval(env);
		
		if(rop.type.equals(Tag.rdif_tag))
			if(l==null && r==null) {return (double) il.eval(env) - ir.eval(env);}
			else
				if(l==null) {return il.eval(env) - r.eval(env);}
				else
					if(r==null) {return (double) l.eval(env) - ir.eval(env);}
					else return l.eval(env) - r.eval(env);		
		
		if(rop.type.equals(Tag.rmul_tag))
			if(l==null && r==null) {return (double) il.eval(env) * ir.eval(env);}
			else
				if(l==null) {return il.eval(env) * r.eval(env);}
				else
					if(r==null) {return (double) l.eval(env) * ir.eval(env);}
					else return l.eval(env) * r.eval(env);
		
		if(rop.type.equals(Tag.rmod_tag))
			if(l==null && r==null) {
				return (double) il.eval(env) % ir.eval(env);}
			else
				if(l==null) {
					return (double) il.eval(env) % r.eval(env);}
				else
					if(r==null) {
						return (double) l.eval(env) % ir.eval(env);}
					else {
						return (double) l.eval(env) % r.eval(env);}
		
		if(rop.type.equals(Tag.round_tag))
			if(l==null && r==null) {
				return (double) ((int) (il.eval(env) / ir.eval(env)));}
			else
				if(l==null) { 
					return (double) ((int) (il.eval(env) / r.eval(env)));}
				else
					if(r==null) {
						return (double) ((int) (l.eval(env) / ir.eval(env)));}
					else {
						return (double) ((int) (l.eval(env) / r.eval(env)));}
		
		if(rop.type.equals(Tag.rdiv_tag))
			if(l==null && r==null) {return (double) il.eval(env) / ir.eval(env);}
			else
				if(l==null) {return il.eval(env) / r.eval(env);}
				else
					if(r==null) {return (double) l.eval(env) / ir.eval(env);}
					else return l.eval(env) / r.eval(env);
		
		else throw new EnvException("Type of ROperation not recognized");
	}
}
