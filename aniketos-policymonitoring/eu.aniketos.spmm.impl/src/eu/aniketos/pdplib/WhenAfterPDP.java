package eu.aniketos.pdplib;

import org.jdom.Element;

import security.iit.pdp.EnvException;
import security.iit.pdp.VarEnvironment;
import security.iit.pdp.PDP.WHEN;

import eu.aniketos.spec.When;
import eu.aniketos.spec.Return;
import eu.aniketos.spec.After;
import eu.aniketos.spec.Declaration;
import eu.aniketos.spec.BaseType;

public class WhenAfterPDP extends WhenPDP{
	public After after;
	
	public WhenAfterPDP(Element e){
		after=new After(e);
	}

	public WhenAfterPDP(When whenTmp){ 
		after = new After(whenTmp.identifier, whenTmp.parameters, ((After) whenTmp).ret);
		//after.identifier=whenTmp.identifier;
		//after.parameters=whenTmp.parameters;
		//after.ret=((WhenAfterPDP) whenTmp).after.ret; 
	}
	
    public Element toElement(){
    	return after.toElement();
    }
	
    
    /**
     * check if the method passed to the PDP matches the method represented in this after tag
     */
	@Override
	public boolean matchWhen(VarEnvironment env, WHEN when, String signature, Object[] params, Object returnz) throws EnvException {
		//if it is not an after call, return false
		//if method signatures don't match, return false
		//if number of parameters doesn't match, return false
		if(when!=WHEN.AFTER || 
				!signature.equals(after.identifier.identifier) || 
				(params!=null && params.length!= after.parameters.size()) ||
				(params==null && after.parameters.size()!=0)){
			System.out.println("WHEN doesn't match");
			return false;
		}
		if(params!=null){
			for(int i=0;i<params.length;i++){
				String par1 = after.parameters.get(i).type.t;
				//if parameters match, continue and insert current parameter in the environment
				if( (params[i] instanceof Integer && par1.equals(BaseType.INT)) ||
						(params[i] instanceof Boolean && par1.equals(BaseType.BOOL)) ||
						(params[i] instanceof Double && par1.equals(BaseType.REAL)) ||
						(params[i] instanceof String && par1.equals(BaseType.STRING))){
					System.out.println("parameter "+i+" match, bind it");
					env.parBind(after.parameters.get(i).identifier.identifier, params[i]);
					continue;
				}
				System.out.println("parameter "+i+" doesn't match , return false and remove parameters");
				//else, return false and flush parameters just inserted
				env.flushParHash();
				return false;
			}
		}
		if( after.ret==null ){
			System.out.println("WHEN match(no returns)");
			return true;
		}
		String ret1 = after.ret.type.t;
		
		if((returnz instanceof Integer && ret1.equals(BaseType.INT)) ||
				(returnz instanceof Boolean && ret1.equals(BaseType.BOOL)) ||
				(returnz instanceof Double && ret1.equals(BaseType.REAL)) ||
				(returnz instanceof String && ret1.equals(BaseType.STRING)) ){
			System.out.println("return match, bind it");
			env.parBind(after.ret.identifier.identifier, returnz);
			System.out.println("WHEN match (return match)");
			return true;
		}
		System.out.println("return doesn't match , return false and remove parameters");
		env.flushParHash();
		return false;
	}
}
