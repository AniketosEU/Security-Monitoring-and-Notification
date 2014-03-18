package eu.aniketos.pdplib;

import org.jdom.Element;

import security.iit.pdp.EnvException;
import security.iit.pdp.VarEnvironment;
import eu.aniketos.spec.SExp;
import eu.aniketos.spec.SIdentifier;
import eu.aniketos.spec.Identifier;

public class SExpIdentifierPDP  extends SExpPDP {
	public Identifier ident;
	
	public SExpIdentifierPDP(SExp sexp){
		ident = new Identifier(((SIdentifier) sexp).identifier.identifier, ((SIdentifier) sexp).identifier.ide_tag);
	}	
	
	@Override
	public Element toElement() {
		return ident.toElement();
	}

	@Override
	public String eval(VarEnvironment env) throws EnvException {
		return env.getStringVal(ident.identifier);
	}

}
