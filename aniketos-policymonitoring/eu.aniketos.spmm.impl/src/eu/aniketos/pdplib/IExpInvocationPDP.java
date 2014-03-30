package eu.aniketos.pdplib;

import org.jdom.Element;

import security.iit.pdp.EnvException;
import security.iit.pdp.VarEnvironment;
import eu.aniketos.spec.AExp;
import eu.aniketos.spec.AInvocation;

public class IExpInvocationPDP extends AExpPDP{
	
	public InvocationPDP invocationPDP;
	
	public IExpInvocationPDP(Element e){
		invocationPDP = new InvocationPDP(e);
	}

	public IExpInvocationPDP(AExp aexp){
		invocationPDP = new InvocationPDP(((AInvocation) aexp).invocation);
	}
	
	@Override
	public Element toElement() {
		return invocationPDP.toElement();
	}
	
	
	@Override
	public int eval(VarEnvironment env) throws EnvException {
		Object ret = invocationPDP.invoke(env);
		return (Integer) ret;
	
	}
}
