package security.iit.pdp;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import eu.aniketos.pdplib.AExpPDP;
import eu.aniketos.pdplib.BExpPDP;
import eu.aniketos.pdplib.RExpPDP;
import eu.aniketos.pdplib.SExpPDP;
import eu.aniketos.pdplib.SpecificationPDP;
import eu.aniketos.spec.AExp;
import eu.aniketos.spec.BExp;
import eu.aniketos.spec.BaseType;
import eu.aniketos.spec.Declaration;
import eu.aniketos.spec.RExp;
import eu.aniketos.spec.SExp;
import eu.aniketos.spec.Specification;

public class EnviromentManager {

	private VarEnvironment env;

	/**
	 * instantiate the environment and bind all the variables declared in the Declaration field
	 * @param policy : the conspec policy
	 * @throws EnvException
	 */
	public EnviromentManager(SpecificationPDP policy)
			throws EnvException {
		
		env = new VarEnvironment();

		Declaration[] decl = policy.getDeclarations();
		for (int i = 0; i < decl.length; i++) {
			System.out.println("trying to bind: "+decl[i].identifier.identifier+ "type: "+decl[i].type.t);
			
			if (decl[i].type.t.equals(BaseType.INT)) {
				AExp exp = (AExp) decl[i].value;
				AExpPDP expPDP = AExpPDP.generateAExpPDP(exp);
				env.bind(decl[i].identifier.identifier, expPDP.eval(env));
				System.out.println("bound: "+decl[i].identifier.identifier+", value: "+expPDP.eval(env));
			}
			if (decl[i].type.t.equals(BaseType.BOOL)) {
				BExp exp = (BExp) decl[i].value;
				BExpPDP expPDP = BExpPDP.generateBExpPDP(exp);
				env.bind(decl[i].identifier.identifier, expPDP.eval(env));
				System.out.println("bound: "+decl[i].identifier.identifier+", value: "+expPDP.eval(env));
			}
			if (decl[i].type.t.equals(BaseType.STRING)) {
				SExp exp = (SExp) decl[i].value;
				SExpPDP expPDP = SExpPDP.generateSExpPDP(exp);
				env.bind(decl[i].identifier.identifier, expPDP.eval(env));
				System.out.println("bound: "+decl[i].identifier.identifier+", value: "+expPDP.eval(env));
			}
			if (decl[i].type.t.equals(BaseType.REAL)) {
				RExp exp = (RExp) decl[i].value;
				RExpPDP expPDP = RExpPDP.generateRExpPDP(exp);
				env.bind(decl[i].identifier.identifier, expPDP.eval(env));
				System.out.println("bound: "+decl[i].identifier.identifier+", value: "+expPDP.eval(env));
			}
		}
	}

	public VarEnvironment getVarEnviroment() {
		return env;
	}

}
