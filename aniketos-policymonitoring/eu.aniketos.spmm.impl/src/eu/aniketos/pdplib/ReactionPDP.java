package eu.aniketos.pdplib;

import java.util.Iterator;

import org.jdom.Element;

import security.iit.pdp.EnvException;
import security.iit.pdp.VarEnvironment;

import eu.aniketos.spec.Reaction;
import eu.aniketos.pdplib.BExpPDP;

/**
 * Represents a reaction, composed by a guard and a command to perform in case the guard is true
 * @author Luca
 *
 */
public class ReactionPDP extends Reaction{

	public BExpPDP guardPDP; //the guard
	public UpdatePDP updatePDP; //command to perform in case the guard is true
	
	public ReactionPDP(){
		super();
	}
	
	public ReactionPDP(Element e){
		super(e);
		guardPDP=BExpPDP.generateBExpPDP(guard);
		updatePDP=new UpdatePDP(updatePDP);
	}
	
	public ReactionPDP(Reaction reactionTmp){
		guard= reactionTmp.guard;
		update=reactionTmp.update;
		guardPDP=BExpPDP.generateBExpPDP(reactionTmp.guard);
		updatePDP=new UpdatePDP(reactionTmp.update);	
	}
    /**
     * evaluate the guard of the reaction
     * @param env
     * @return the response of the guard
     * @throws EnvException
     */
    public boolean evaluateGuard(VarEnvironment env) throws EnvException {
    	return guardPDP.eval(env);
    }

    /**
     * execute the command
     * @param env
     * @throws EnvException
     */
	public void exec(VarEnvironment env) throws EnvException {
		updatePDP.exec(env);
	}
}
