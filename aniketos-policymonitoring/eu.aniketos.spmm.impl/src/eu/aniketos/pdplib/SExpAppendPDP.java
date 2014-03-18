package eu.aniketos.pdplib;

import org.jdom.Element;

import security.iit.pdp.EnvException;
import security.iit.pdp.VarEnvironment;

import eu.aniketos.spec.SExp;
import eu.aniketos.spec.Append;
import eu.aniketos.spec.SGuard;

public class SExpAppendPDP extends SExpPDP{
	protected Append app;
	
	public SExpAppendPDP(SExp sexp){
		app= new Append(((Append) sexp).exp1, ((Append) sexp).exp2);
	}

	 public Element toElement() {
		 return app.toElement();
	 }

	@Override
	public String eval(VarEnvironment env) throws EnvException {
    	SExpPDP l = SExpPDP.generateSExpPDP(app.exp1);
    	SExpPDP r = SExpPDP.generateSExpPDP(app.exp2);
		return l.eval(env)+r.eval(env);
	}
}
