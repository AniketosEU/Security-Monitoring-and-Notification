package eu.aniketos.pdplib;

import org.jdom.Element;

import security.iit.pdp.EnvException;
import security.iit.pdp.VarEnvironment;
import security.iit.pdp.PDP.WHEN;

import eu.aniketos.spec.After;
import eu.aniketos.spec.When;
import eu.aniketos.spec.Before;
import eu.aniketos.spec.Declaration;
import eu.aniketos.spec.BaseType;


public class WhenBeforePDP extends WhenPDP{
		public Before before;
		
		public WhenBeforePDP(Element e){
			before=new Before(e);
		}

		public WhenBeforePDP(When whenTmp){
			before=new Before(whenTmp.identifier, whenTmp.parameters);
			//before.identifier=whenTmp.identifier;
			//before.parameters=whenTmp.parameters; 
		}
		
	    public Element toElement(){
	    	return before.toElement();
	    }

	    /**
	     * check if the method passed to the PDP matches the method represented in this before tag
	     */
		@Override
		public boolean matchWhen(VarEnvironment env,WHEN when, String signature, Object[] params, Object returnz) throws EnvException{
			// if it is not a before call, return false
			// if method signatures don't match, return false
			// if number of parameters doesn't match, return false
			if (when != WHEN.BEFORE || !signature.equals(before.identifier.identifier)
					|| (params!=null && params.length != before.parameters.size()) ||
					(params==null && before.parameters.size()!=0)) {
				System.out.println("WHEN doesn't match");
				return false;
			}

			if(params!=null){
				for (int i = 0; i < params.length; i++) {
					String par1 = before.parameters.get(i).type.t;
					// if parameters match, continue and insert current parameter in the
					// environment
					if (params[i] instanceof Integer && par1.equals(BaseType.INT)) {
						System.out.println("parameter " + i + " match, bind it");
						env.parBind(before.parameters.get(i).identifier.identifier, params[i]);
						continue;
					}
					if (params[i] instanceof Boolean && par1.equals(BaseType.BOOL)) {
						System.out.println("parameter " + i + " match, bind it");
						env.parBind(before.parameters.get(i).identifier.identifier, params[i]);
						continue;
					}
					if (params[i] instanceof String && par1.equals(BaseType.STRING)) {
						System.out.println("parameter " + i + " match, bind it");
						env.parBind(before.parameters.get(i).identifier.identifier, params[i]);
						continue;
					}
					if (params[i] instanceof Double && par1.equals(BaseType.REAL)) {
						System.out.println("parameter " + i + " match, bind it");
						env.parBind(before.parameters.get(i).identifier.identifier, params[i]);
						continue;
					}
					System.out.println("parameter " + i
							+ " doesn't match , return false and remove parameters");
					// else, return false and flush parameters just inserted
					env.flushParHash();
					return false;
				}
			}

			System.out.println("WHEN match");
			return true;
		}	    
	    
}
