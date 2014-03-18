package eu.aniketos.pdplib;

import org.jdom.Element;

import security.iit.pdp.EnvException;
import security.iit.pdp.VarEnvironment;
import eu.aniketos.spec.AExp;
import eu.aniketos.spec.AInvocation;
import eu.aniketos.spec.RExp;
import eu.aniketos.spec.RInvocation;


public class RExpInvocationPDP extends RExpPDP{
	
	public InvocationPDP invocationPDP;
	
	public RExpInvocationPDP(Element e){
		invocationPDP = new InvocationPDP(e);
	}

	public RExpInvocationPDP(RExp rexp){
		invocationPDP = new InvocationPDP(((RInvocation) rexp).invocation);
	}
	
	@Override
	public Element toElement() {
		return invocationPDP.toElement();
	}
	
	
	@Override
	public double eval(VarEnvironment env) throws EnvException {
		Object ret = invocationPDP.invoke(env);
		return (Double) ret;
	
	}
}