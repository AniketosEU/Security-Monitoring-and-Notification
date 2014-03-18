package eu.aniketos.pdplib;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.jdom.Element;

import security.iit.pdp.VarEnvironment;
import eu.aniketos.spec.AExp;
import eu.aniketos.spec.BExp;
//import eu.aniketos.spec.Identifier;
import eu.aniketos.spec.Invocation;
import eu.aniketos.spec.Exp;
import eu.aniketos.spec.Declaration;
import eu.aniketos.spec.RExp;
import eu.aniketos.spec.SExp;
import eu.aniketos.spec.Tag;
//import eu.aniketos.trustworthiness.ext.messaging.ITrustworthinessPrediction;
//import eu.aniketos.trustworthiness.ext.messaging.ITrustworthinessPredictionPortType;
//import eu.aniketos.trustworthiness.ext.messaging.Trustworthiness;

import java.net.URL;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;



public class InvocationPDP extends Invocation{
	
	public InvocationPDP( Element e){
		super(e);
	}
	
	public InvocationPDP(Invocation invoke){
		function = invoke.function;
		target = invoke.target;
		arguments = invoke.arguments;
	}
	
	
	/**
	 * Invoke the method.
	 * The method identifier should have the complete path (package+class+method, e.g. java.lang.String.equals)
	 * TODO: currently the method can be invoked in generic classes only if the method is static, because
	 * the parser can't manage objects that are not integers, strings, booleans.
	 * Moreover these method should have only int, bool, string parameters
	 * @param env
	 * @return: the return of the method
	 */
	public Object invoke(VarEnvironment env){
		/*System.out
		.println("\n<<<<<<<<<<<<<<<<<< InvocationPDP.invoke Method >>>>>>>>>>>>>>");*/
		String invoc = function.identifier;
		//get the class of the method
		String clazz = invoc.substring(0, invoc.lastIndexOf("."));
		//get the name of the method
		String method = invoc.substring(invoc.lastIndexOf(".")+1);
		String target_ID = null;
		Vector <Exp> args = arguments;
		Class[] parTypes = new Class[args.size()]; //types of parameters
		Object[] parValues = new Object[args.size()]; //values of parameters
		Object ret = null;
		
		 try {
			for (int i = 0; i < args.size(); i++) {
				// get types and values of the parameters
				Exp arg = args.get(i);
				if (arg.exp_type == Exp.EXP_BOOL) {
					parTypes[i] = Class.forName("java.lang.Boolean");
					BExpPDP val = BExpPDP.generateBExpPDP(((BExp) arg));
					parValues[i] = val.eval(env);
				}
				if (arg.exp_type == Exp.EXP_INT) {
					parTypes[i] = Class.forName("java.lang.Integer");
					AExpPDP val = AExpPDP.generateAExpPDP(((AExp) arg));
					parValues[i] = val.eval(env);
					// parValues[i] = ((AExpPDP) arg).eval(env);
				}
				if (arg.exp_type == Exp.EXP_STR) {
					parTypes[i] = Class.forName("java.lang.String");
					SExpPDP val = SExpPDP.generateSExpPDP(((SExp) arg));
					parValues[i] = val.eval(env);
					// parValues[i] = ((SExpPDP) arg).eval(env);
				}
				if (arg.exp_type == Exp.EXP_REAL) {
					parTypes[i] = Class.forName("java.lang.Double");
					RExpPDP val = RExpPDP.generateRExpPDP(((RExp) arg));
					parValues[i] = val.eval(env);
					// parValues[i] = ((SExpPDP) arg).eval(env);
				}
			}

			Object target_obj = null;
			// if target is null it's a static call
			if (this.target != null) {
				// retrieve the target object
				target_ID = this.target.identifier;
				String type = env.typeOf(target_ID);
				if (type.equals(Declaration.dcl_int_typ)) {
					target_obj = env.getIntVal(target_ID);
				} else if (type.equals(Declaration.dcl_bool_typ)) {
					target_obj = env.getBoolVal(target_ID);
				} else if (type.equals(Declaration.dcl_string_typ)) {
					target_obj = env.getStringVal(target_ID);
				} else if (type.equals(Declaration.dcl_real_typ)) {
					target_obj = env.getDoubleVal(target_ID);
				}
			}

			
			if (clazz.equals("java.prova")
					&& !method.equals("")) {
				System.out
				.println("\n\n!!!!!!!!!!!!!!!!!!!!!!!!!!! Confidentiality Check !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				BundleInvoker bi = new BundleInvoker();
				
				String type = null;
				if(function.ide_tag.equals(Tag.b_identifier_tag)){type="bool";};
				if(function.ide_tag.equals(Tag.i_identifier_tag)){type="int";};
				if(function.ide_tag.equals(Tag.s_identifier_tag)){type="string";};
				if(function.ide_tag.equals(Tag.r_identifier_tag)){type="real";};
				
				ret = bi.invoke(method, type, parTypes, parValues);
				
			} else {
				
				Class c = Class.forName(clazz);
				// retrieve the method
				Method met = ReflectionUtil.getCompatibleMethod(c, method,
						parTypes);// c.getMethod(method,
									// parTypes);
				// invoke the method
				ret = met.invoke(target_obj, parValues);
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return ret;
		
	}
		
}
