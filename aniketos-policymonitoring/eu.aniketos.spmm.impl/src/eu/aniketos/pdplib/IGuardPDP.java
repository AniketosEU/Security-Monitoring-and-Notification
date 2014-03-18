package eu.aniketos.pdplib;

import java.util.Iterator;

import org.jdom.Element;

import eu.aniketos.spec.AExp;
import eu.aniketos.spec.IGuard;

public class IGuardPDP extends IGuard{
    public AExpPDP sexp1PDP;
    public AExpPDP sexp2PDP;
    
    public IGuardPDP(String tag, AExp s1, AExp s2) {
    	super(tag, s1, s2);
    	sexp1PDP = AExpPDP.generateAExpPDP(s1);
    	sexp2PDP = AExpPDP.generateAExpPDP(s2);
    }
    
    public IGuardPDP(Element e){
    	super(e);
    	sexp1PDP = AExpPDP.generateAExpPDP(sexp1);
    	sexp2PDP = AExpPDP.generateAExpPDP(sexp2);
    }
    
    
}
