package eu.aniketos.pdplib;

import org.jdom.Element;

import security.iit.pdp.EnvException;
import security.iit.pdp.VarEnvironment;
import eu.aniketos.spec.AExp;
import eu.aniketos.spec.AIdentifier;
import eu.aniketos.spec.Identifier;
import eu.aniketos.spec.RExp;
import eu.aniketos.spec.RIdentifier;


public class RExpIdentifierPDP extends RExpPDP {
	public Identifier ident;
	
	public RExpIdentifierPDP(RExp rexp){
		ident = new Identifier(((RIdentifier) rexp).identifier.identifier, ((RIdentifier) rexp).identifier.ide_tag);
	}	
	
	@Override
	public Element toElement() {
		return ident.toElement();
	}

	@Override
	public double eval(VarEnvironment env) throws EnvException {
		return env.getDoubleVal(ident.identifier);
	}

}