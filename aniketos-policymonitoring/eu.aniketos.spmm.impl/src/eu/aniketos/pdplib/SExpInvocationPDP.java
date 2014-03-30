package eu.aniketos.pdplib;

import org.jdom.Element;

import security.iit.pdp.EnvException;
import security.iit.pdp.VarEnvironment;
import eu.aniketos.spec.SExp;
import eu.aniketos.spec.SInvocation;

public class SExpInvocationPDP extends SExpPDP{
	
	public InvocationPDP invocationPDP;
	
	public SExpInvocationPDP(Element e){
		invocationPDP = new InvocationPDP(e);
	}

	public SExpInvocationPDP(SExp sexp){
		invocationPDP = new InvocationPDP(((SInvocation) sexp).invocation);
	}
	
	@Override
	public Element toElement() {
		return invocationPDP.toElement();
	}
	
	
	@Override
	public String eval(VarEnvironment env) throws EnvException {
		Object ret = invocationPDP.invoke(env);
		return (String) ret;
	
	}

}
