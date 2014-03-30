package eu.aniketos.pdplib;

import org.jdom.Element;

import security.iit.pdp.EnvException;
import security.iit.pdp.VarEnvironment;
import eu.aniketos.spec.BExp;
import eu.aniketos.spec.BInvocation;

import eu.aniketos.pdplib.BExpInvocationPDP;

public class BExpInvocationPDP extends BExpPDP{
	
	public InvocationPDP invocationPDP;
	
	public BExpInvocationPDP(Element e){
		invocationPDP = new InvocationPDP(e);
	}

	public BExpInvocationPDP(BExp bexp){
		invocationPDP = new InvocationPDP(((BInvocation) bexp).invocation);
	}
	
	@Override
	public Element toElement() {
		return invocationPDP.toElement();
	}
	
	
	@Override
	public boolean eval(VarEnvironment env) throws EnvException {
		Object ret = invocationPDP.invoke(env);
		return (Boolean) ret;
	
	}
}
