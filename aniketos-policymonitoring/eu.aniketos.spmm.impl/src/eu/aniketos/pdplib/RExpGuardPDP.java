package eu.aniketos.pdplib;

import org.jdom.Element;

import security.iit.pdp.EnvException;
import security.iit.pdp.VarEnvironment;
import eu.aniketos.spec.BExp;
import eu.aniketos.spec.IGuard;
import eu.aniketos.spec.RGuard;
import eu.aniketos.spec.Tag;

	public class RExpGuardPDP extends BExpPDP {
		protected RGuardPDP rGuardPDP;
		
		public RExpGuardPDP(BExp bexp){
			if(((RGuard) bexp).exp1==null && ((RGuard) bexp).exp2==null) {rGuardPDP = new RGuardPDP(((RGuard) bexp).type, ((RGuard) bexp).aexp1, ((RGuard) bexp).aexp2);}
			else 
				if(((RGuard) bexp).exp1==null) {rGuardPDP = new RGuardPDP(((RGuard) bexp).type, ((RGuard) bexp).aexp1, ((RGuard) bexp).exp2);}
				else 
					if(((RGuard) bexp).exp2==null) {rGuardPDP = new RGuardPDP(((RGuard) bexp).type, ((RGuard) bexp).exp1, ((RGuard) bexp).aexp2);}
					else {rGuardPDP = new RGuardPDP(((RGuard) bexp).type, ((RGuard) bexp).exp1, ((RGuard) bexp).exp2);}
		}	
		
		@Override
		public Element toElement() {
			return rGuardPDP.toElement();
		}

		@Override
	    public boolean eval(VarEnvironment env) throws EnvException{
	    	if(rGuardPDP.type.equals(Tag.requal_tag))
	    		
	    		if(rGuardPDP.rexp1PDP==null && rGuardPDP.rexp2PDP==null) {return rGuardPDP.aexp1PDP.eval(env) == rGuardPDP.aexp2PDP.eval(env);}
	    		else
	    			if(rGuardPDP.rexp1PDP==null) {return rGuardPDP.aexp1PDP.eval(env) == rGuardPDP.rexp2PDP.eval(env);}
		    		else
		    			if(rGuardPDP.rexp2PDP==null) {return rGuardPDP.rexp1PDP.eval(env) == rGuardPDP.aexp2PDP.eval(env);}
	    	
	    	           ////// DOULBE=DOUBLE!
			    		else {return rGuardPDP.rexp1PDP.eval(env) == rGuardPDP.rexp2PDP.eval(env);}
	    	
	    	else if(rGuardPDP.type.equals(Tag.rmorethan_tag))
	    		
	    		if(rGuardPDP.rexp1PDP==null && rGuardPDP.rexp2PDP==null) {return rGuardPDP.aexp1PDP.eval(env) > rGuardPDP.aexp2PDP.eval(env);}
	    		else
	    			if(rGuardPDP.rexp1PDP==null) {return rGuardPDP.aexp1PDP.eval(env) > rGuardPDP.rexp2PDP.eval(env);}
		    		else
		    			if(rGuardPDP.rexp2PDP==null) {return rGuardPDP.rexp1PDP.eval(env) > rGuardPDP.aexp2PDP.eval(env);}
	    	
			    		else {return rGuardPDP.rexp1PDP.eval(env) > rGuardPDP.rexp2PDP.eval(env);}
	    	
	        else if(rGuardPDP.type.equals(Tag.rlessthan_tag))
	        	
	    		if(rGuardPDP.rexp1PDP==null && rGuardPDP.rexp2PDP==null) {return rGuardPDP.aexp1PDP.eval(env) < rGuardPDP.aexp2PDP.eval(env);}
	    		else
	    			if(rGuardPDP.rexp1PDP==null) {return rGuardPDP.aexp1PDP.eval(env) < rGuardPDP.rexp2PDP.eval(env);}
		    		else
		    			if(rGuardPDP.rexp2PDP==null) {return rGuardPDP.rexp1PDP.eval(env) < rGuardPDP.aexp2PDP.eval(env);}
	    	
			    		else {return rGuardPDP.rexp1PDP.eval(env) < rGuardPDP.rexp2PDP.eval(env);}

	        else if(rGuardPDP.type.equals(Tag.rlessequalthan_tag))
	        	
	    		if(rGuardPDP.rexp1PDP==null && rGuardPDP.rexp2PDP==null) {return rGuardPDP.aexp1PDP.eval(env) <= rGuardPDP.aexp2PDP.eval(env);}
	    		else
	    			if(rGuardPDP.rexp1PDP==null) {return rGuardPDP.aexp1PDP.eval(env) <= rGuardPDP.rexp2PDP.eval(env);}
		    		else
		    			if(rGuardPDP.rexp2PDP==null) {return rGuardPDP.rexp1PDP.eval(env) <= rGuardPDP.aexp2PDP.eval(env);}
	    	
			    		else {return rGuardPDP.rexp1PDP.eval(env) <= rGuardPDP.rexp2PDP.eval(env);}

	        else if(rGuardPDP.type.equals(Tag.rmorequalthan_tag))
	        	
	    		if(rGuardPDP.rexp1PDP==null && rGuardPDP.rexp2PDP==null) {return rGuardPDP.aexp1PDP.eval(env) >= rGuardPDP.aexp2PDP.eval(env);}
	    		else
	    			if(rGuardPDP.rexp1PDP==null) {return rGuardPDP.aexp1PDP.eval(env) >= rGuardPDP.rexp2PDP.eval(env);}
		    		else
		    			if(rGuardPDP.rexp2PDP==null) {return rGuardPDP.rexp1PDP.eval(env) >= rGuardPDP.aexp2PDP.eval(env);}
	    	
			    		else {return rGuardPDP.rexp1PDP.eval(env) >= rGuardPDP.rexp2PDP.eval(env);}
	    	
	        else 
	        	
	    		if(rGuardPDP.rexp1PDP==null && rGuardPDP.rexp2PDP==null) {return rGuardPDP.aexp1PDP.eval(env) != rGuardPDP.aexp2PDP.eval(env);}
	    		else
	    			if(rGuardPDP.rexp1PDP==null) {return rGuardPDP.aexp1PDP.eval(env) != rGuardPDP.rexp2PDP.eval(env);}
		    		else
		    			if(rGuardPDP.rexp2PDP==null) {return rGuardPDP.rexp1PDP.eval(env) != rGuardPDP.aexp2PDP.eval(env);}
	    	
			    		else {return rGuardPDP.rexp1PDP.eval(env) != rGuardPDP.rexp2PDP.eval(env);}
	    	
	    }
	}
