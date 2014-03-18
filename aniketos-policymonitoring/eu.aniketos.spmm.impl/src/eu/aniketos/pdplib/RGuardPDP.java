package eu.aniketos.pdplib;

import org.jdom.Element;

import eu.aniketos.spec.AExp;
import eu.aniketos.spec.IGuard;
import eu.aniketos.spec.RExp;
import eu.aniketos.spec.RGuard;

	public class RGuardPDP extends RGuard{
	    public AExpPDP aexp1PDP;
	    public AExpPDP aexp2PDP;
	    public RExpPDP rexp1PDP;
	    public RExpPDP rexp2PDP;
	    
	    public RGuardPDP(String tag, AExp s1, AExp s2) {
	    	super(tag, s1, s2);
	    	aexp1PDP = AExpPDP.generateAExpPDP(s1);
	    	aexp2PDP = AExpPDP.generateAExpPDP(s2);
	    }
	    public RGuardPDP(String tag, RExp s1, AExp s2) {
	    	super(tag, s1, s2);
	    	rexp1PDP = RExpPDP.generateRExpPDP(s1);
	    	aexp2PDP = AExpPDP.generateAExpPDP(s2);
	    }
	    public RGuardPDP(String tag, AExp s1, RExp s2) {
	    	super(tag, s1, s2);
	    	aexp1PDP = AExpPDP.generateAExpPDP(s1);
	    	rexp2PDP = RExpPDP.generateRExpPDP(s2);
	    }
	    public RGuardPDP(String tag, RExp s1, RExp s2) {
	    	super(tag, s1, s2);
	    	rexp1PDP = RExpPDP.generateRExpPDP(s1);
	    	rexp2PDP = RExpPDP.generateRExpPDP(s2);
	    }
	    
	    public RGuardPDP(Element e){
	    	super(e);
	    	aexp1PDP = AExpPDP.generateAExpPDP(aexp1);
	    	aexp2PDP = AExpPDP.generateAExpPDP(aexp2);
	    	rexp1PDP = RExpPDP.generateRExpPDP(exp1);
	    	rexp2PDP = RExpPDP.generateRExpPDP(exp2);
	    }
	    
}
