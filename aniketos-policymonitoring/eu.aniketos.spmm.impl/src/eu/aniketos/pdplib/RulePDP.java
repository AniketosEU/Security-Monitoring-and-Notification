/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.aniketos.pdplib;

import eu.aniketos.pdplib.RulePDP;
import eu.aniketos.pdplib.WhenPDP;
import eu.aniketos.pdplib.PerformPDP;

import eu.aniketos.spec.Rule;
import eu.aniketos.spec.When;
import eu.aniketos.spec.Perform;

import java.util.Iterator;

import org.jdom.Element;

import security.iit.pdp.EnvException;
import security.iit.pdp.PDP.WHEN;
import security.iit.pdp.VarEnvironment;

/**
 * represent a conspec rule, made by a when tag an some reactions (perform tag).
 * the when tag is the trigger of the rule, we check if the request to the PDP matches with this rule by checking the when tag.
 * If the request matches, the reactions are activated.
 * @author Administrator
 */
public class RulePDP extends Rule{


    public WhenPDP whenPDP;
    public PerformPDP performPDP;

    public RulePDP(When when, Perform perform) {
    	super(when,perform);
    	whenPDP=WhenPDP.createWhen(this.when);
    	performPDP=new PerformPDP(this.perform);
    }

    public RulePDP(Element e) {
    	super(e);
    	whenPDP=WhenPDP.createWhen(this.when);
    	performPDP=new PerformPDP(this.perform);
    }

    public RulePDP(Rule ruleTmp){
    	super(ruleTmp.when, ruleTmp.perform);
    	whenPDP=WhenPDP.createWhen(this.when);
    	performPDP=new PerformPDP(this.perform); 	
    }
    
    /*
    public Element toElement() {
        Element rule_elm = new Element(Tag.rule_tag);
        rule_elm.addContent(when.toElement());
        rule_elm.addContent(perform.toElement());
        return rule_elm;
    }
*/
    
    /**
     * evaluation of the rule: see if the "when" section matches with current call
     * @param env
     * @param curr_when: request when (AFTER or BEFORE or EXCEPTIONAL)
     * @param signature: request signature method
     * @param parameters: parameters of the request method
     * @param returnz: return of the request method
     * @return : true if the rule matches with the request, false otherwise
     * @throws EnvException
     */
    public boolean evaluateRule(VarEnvironment env, WHEN curr_when, String signature, Object[] parameters, Object returnz) throws EnvException {
		// remove old params
		env.flushParHash();
		
		//check if the method signature matches
    	return whenPDP.matchWhen(env, curr_when, signature, parameters, returnz);
    }
    
    /**
     * Execute the reactions
     * @param env
     * @throws EnvException
     */
    public void executeRule(VarEnvironment env) throws EnvException {
    	performPDP.exec(env);
    }
}
