/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.aniketos.pdplib;
import eu.aniketos.spec.AExp;
import eu.aniketos.spec.Perform;
import eu.aniketos.spec.Reaction;
import eu.aniketos.spmm.impl.ContractMonitoring;

import java.util.Iterator;
import java.util.Vector;
import org.jdom.Element;

import security.iit.pdp.EnvException;
import security.iit.pdp.VarEnvironment;
import security.iit.pdp.pdpResult;

import eu.aniketos.pdplib.UpdatePDP;

/**
 * Represent a perform tag, that contains the action to be executed when the method is activated
 * @author Administrator
 */
public class PerformPDP extends Perform {
	

	//the actions to be done
    protected Vector<ReactionPDP> reactionsPDP;
    //the command to be executed if no reactions are activated
    protected UpdatePDP elzePDP;

    public PerformPDP(Element e) {
    	super(e);
    	reactionsPDP = new Vector<ReactionPDP>();
		for(int i = 0; i<reactions.size(); i++) {
			reactionsPDP.add(new ReactionPDP(reactions.get(i)));
		}
		if(elze!=null){
			elzePDP= new UpdatePDP(elze);
    		};   
    }
        
    public PerformPDP(Perform performTmp){
    	reactionsPDP = new Vector<ReactionPDP>();
		for(int i = 0; i<performTmp.reactions.size(); i++) {
			ReactionPDP test =new ReactionPDP(performTmp.reactions.get(i)); 
			//ReactionPDP test2 = new ReactionPDP();
			//reactionsPDP.add(test2);
			reactionsPDP.add(test);
		}
		if(performTmp.elze!=null){
	    	elzePDP= new UpdatePDP(performTmp.elze);
			}
    }
    

    /**
     * Execute a perform. It tries to execute every reaction child. The reactions are activated if their guards return true.
     * @param env
     * @throws EnvException
     */
	public void exec(VarEnvironment env) throws EnvException {
		boolean reactionActivated = false;
		try{
		//activate each reaction which guard returns true
		for(int i=0;i<reactionsPDP.size();i++){
			ReactionPDP reactPDP = reactionsPDP.get(i);
			if(reactPDP.evaluateGuard(env)){
				System.out.println("reaction activated: "+i);
				reactPDP.exec(env);
				
				reactionActivated = true;
				//System.out.println("reaction executed");
				System.out.println("RuleID:"+pdpResult.getRuleId());
				
			}
		}
		//if no reactions are activated, activate the else command
		if(!reactionActivated){
			//System.out.println("no reaction activated, enabling ELSE if present");
			System.out.println("-----------------------------------------------------------------( RULE VIOLATED ! RuleID:"+pdpResult.getRuleId());
			//pdpResult.setResult("rule false");
			
			//Notificationt for contract violation
			ContractMonitoring j=new ContractMonitoring();
			try {
				String s=pdpResult.getServiceId();
				String r=pdpResult.getRuleId();
				j.sendContractViolationNotification(s,r);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/////
			if(elzePDP!=null)
				elzePDP.exec(env);
		}
		}catch(Exception e){
			System.out.println("ERRORRRRRRRR"+e.getMessage());

		}
	}
}
