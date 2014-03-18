

package eu.aniketos.pdplib;

import java.util.Vector;


import eu.aniketos.spec.After;
import eu.aniketos.spec.Before;
import eu.aniketos.spec.Exceptional;

import org.jdom.Element;

import security.iit.pdp.EnvException;
import security.iit.pdp.VarEnvironment;
import security.iit.pdp.PDP.WHEN;

import eu.aniketos.spec.When;

/**
 * Represents a When tag of the conspec policy
 * @author Luca
 *
 */
public abstract class WhenPDP extends When{
	
//	public Identifier identifier;
//	public Vector<Parameter> parameters;
	
	/*
	public abstract Element toElement();
	
	public static WhenPDP generateWhen(Element e){
		if(e.getName().equals(Tag.before_tag)){
			return new Before(e);
		} else if(e.getName().equals(Tag.after_tag)){
			return new After(e);
		} else if(e.getName().equals(Tag.exceptional_tag)){
			return new Exceptional(e);
		}
		return null;
	}
	
	*/
	
	public static WhenPDP createWhen(When whenTmp){
			if(whenTmp instanceof Before){
				return new WhenBeforePDP(whenTmp);
			} else if(whenTmp instanceof After){
				return new WhenAfterPDP(whenTmp);
			} else if(whenTmp instanceof Exceptional){
				return new WhenExceptionalPDP(whenTmp);
			}
		return null;		
	}
	
	public abstract boolean matchWhen(VarEnvironment env, WHEN when, String signature, Object[] parameters, Object returnz) throws EnvException ;
	
	}
