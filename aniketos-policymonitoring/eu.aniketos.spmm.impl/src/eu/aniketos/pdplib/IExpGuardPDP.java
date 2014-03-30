package eu.aniketos.pdplib;

import org.jdom.Element;

import security.iit.pdp.EnvException;
import security.iit.pdp.VarEnvironment;
import eu.aniketos.spec.BExp;
import eu.aniketos.spec.BIdentifier;
import eu.aniketos.spec.Identifier;
import eu.aniketos.spec.IGuard;
import eu.aniketos.spec.Tag;

public class IExpGuardPDP extends BExpPDP {
	protected IGuardPDP iGuardPDP;
	
	public IExpGuardPDP(BExp bexp){
		iGuardPDP = new IGuardPDP(((IGuard) bexp).type, ((IGuard) bexp).sexp1, ((IGuard) bexp).sexp2);
	}	
	
	@Override
	public Element toElement() {
		return iGuardPDP.toElement();
	}

	@Override
    public boolean eval(VarEnvironment env) throws EnvException{
    	if(iGuardPDP.type.equals(Tag.iequal_tag))
    		return iGuardPDP.sexp1PDP.eval(env) == iGuardPDP.sexp2PDP.eval(env);
    	else if(iGuardPDP.type.equals(Tag.morethan_tag))
    		return iGuardPDP.sexp1PDP.eval(env) > iGuardPDP.sexp2PDP.eval(env);
        else if(iGuardPDP.type.equals(Tag.morequalthan_tag))
    		return iGuardPDP.sexp1PDP.eval(env) >= iGuardPDP.sexp2PDP.eval(env);
        else if(iGuardPDP.type.equals(Tag.lessequalthan_tag))
        	return iGuardPDP.sexp1PDP.eval(env) <= iGuardPDP.sexp2PDP.eval(env);
        else if(iGuardPDP.type.equals(Tag.lessthan_tag))
        	return iGuardPDP.sexp1PDP.eval(env) < iGuardPDP.sexp2PDP.eval(env);
        else	
            return iGuardPDP.sexp1PDP.eval(env) != iGuardPDP.sexp2PDP.eval(env);
        
    }
}
