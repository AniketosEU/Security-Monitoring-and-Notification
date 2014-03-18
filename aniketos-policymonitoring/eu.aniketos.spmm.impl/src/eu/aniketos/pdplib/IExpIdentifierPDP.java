package eu.aniketos.pdplib;

import org.jdom.Element;

import security.iit.pdp.EnvException;
import security.iit.pdp.VarEnvironment;
import eu.aniketos.spec.AExp;
import eu.aniketos.spec.AIdentifier;
import eu.aniketos.spec.Identifier;

public class IExpIdentifierPDP extends AExpPDP {
	public Identifier ident;
	
	public IExpIdentifierPDP(AExp aexp){
		ident = new Identifier(((AIdentifier) aexp).identifier.identifier, ((AIdentifier) aexp).identifier.ide_tag);
	}	
	
	@Override
	public Element toElement() {
		return ident.toElement();
	}

	@Override
	public int eval(VarEnvironment env) throws EnvException {
		return env.getIntVal(ident.identifier);
	}

}
