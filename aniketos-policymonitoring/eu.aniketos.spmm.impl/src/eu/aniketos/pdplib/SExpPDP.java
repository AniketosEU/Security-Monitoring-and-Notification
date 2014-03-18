package eu.aniketos.pdplib;

import security.iit.pdp.EnvException;
import security.iit.pdp.VarEnvironment;
import eu.aniketos.spec.AExp;
import eu.aniketos.spec.BConst;
import eu.aniketos.spec.BExp;
import eu.aniketos.spec.BGuard;
import eu.aniketos.spec.BIdentifier;
import eu.aniketos.spec.BInvocation;
import eu.aniketos.spec.BOp;
import eu.aniketos.spec.IGuard;
import eu.aniketos.spec.SGuard;

import eu.aniketos.spec.SConst;
import eu.aniketos.spec.Append;
import eu.aniketos.spec.SIdentifier;
import eu.aniketos.spec.SInvocation;

import eu.aniketos.spec.SExp;

import security.iit.pdp.EnvException;
import security.iit.pdp.VarEnvironment;


public abstract class SExpPDP extends SExp{

	public static SExpPDP generateSExpPDP(SExp sexp) {
		if(sexp instanceof SConst){
			return new SExpConstPDP(sexp);
		} else if(sexp instanceof SIdentifier){
			return new SExpIdentifierPDP(sexp);
		} else if(sexp instanceof SInvocation){
			return new SExpInvocationPDP(sexp);
		} else if(sexp instanceof Append){
			return new SExpAppendPDP(sexp);
		} else return null;
 	    
  }
	
    /**
     * evaluate the string expression
     * @param env: the Environment
     * @return the evaluation
     * @throws EnvException
     */
	public abstract String eval(VarEnvironment env) throws EnvException;	
	
}
