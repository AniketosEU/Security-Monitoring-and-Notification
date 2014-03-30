package eu.aniketos.pdplib;

import org.jdom.Element;

import security.iit.pdp.EnvException;
import security.iit.pdp.VarEnvironment;
import eu.aniketos.spec.BExp;
import eu.aniketos.spec.BOp;
import eu.aniketos.spec.Identifier;
import eu.aniketos.spec.BIdentifier;
import eu.aniketos.spec.Exp;


public class BExpIdentifierPDP extends BExpPDP {
	public Identifier ident;
	
	public BExpIdentifierPDP(BExp bexp){
		ident = new Identifier(((BIdentifier) bexp).identifier.identifier, ((BIdentifier) bexp).identifier.ide_tag);
	}	
	
	@Override
	public Element toElement() {
		return ident.toElement();
	}

	@Override
	public boolean eval(VarEnvironment env) throws EnvException {
		return env.getBoolVal(ident.identifier);
	}
	
}
