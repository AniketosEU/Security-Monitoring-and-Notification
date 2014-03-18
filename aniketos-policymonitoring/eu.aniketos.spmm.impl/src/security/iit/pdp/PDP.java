package security.iit.pdp;

import eu.aniketos.pdplib.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom.JDOMException;

import eu.aniketos.spec.Specification;
import eu.aniketos.pdplib.SpecificationPDP;

public class PDP {

	public enum WHEN {BEFORE, AFTER, EXCEPTIONAL};
	
	private static SpecificationPDP policy;
	private static EnviromentManager manager;
	
	public PDP(SpecificationPDP policy){
		this.policy = policy;
		try {
			manager = new EnviromentManager(policy);
		} catch (EnvException e) {
			e.printStackTrace();
		}
	}
	/**
	 * ask to the pdp if the method with that params is allowed or not
	 * @param signature : method signature. it should be in the complete format, with class and package (e.g. java.lang.String.equals)
	 * @param when : the moment in which the pdp is called: BEFORE(before the method call), AFTER(after the method call), EXCEPTIONAL(after
	 * 				an exception of the method call)
	 * @param params : parameters of the method
	 * @return true: OK false: not allowed
	 */
	public boolean PDP_allow(String signature, WHEN when, Object[] params, Object returnz){
		try {
			//print parameters for debug
			System.out.println("PDP: arrived: " + signature + ", params : ");
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					System.out.println("\t" + params[i].getClass().getName() + ", value: " + params[i]);
				}
			}
			//if no rules regarding the method are activated, PDP returns false
			boolean response = false;
			RulePDP[] rules = policy.getRulesPDP();

			VarEnvironment env = manager.getVarEnviroment();
			// for on all policy rules
			for (int i = 0; i < rules.length; i++) {
				// check if the rule matches with the function passed
				if (rules[i].evaluateRule(env, when, signature, params, returnz)) {
					//if some rules are activated, PDP returns true
					response = true;
					System.out.println("PDP: monitoring " + signature);
					//execute rule
					rules[i].executeRule(env);
				}
			}
			System.out.println("rule ID: "+ policy.getAttribute("id"));
			System.out.println("PDP: response: " + response);
			return response;

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("PDP: some exception in PDP code, return true.");
			return true;
		}
	}
	
}
