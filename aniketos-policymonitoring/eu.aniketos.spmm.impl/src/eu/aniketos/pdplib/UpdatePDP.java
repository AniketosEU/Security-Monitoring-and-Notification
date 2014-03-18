/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.aniketos.pdplib;

import eu.aniketos.spec.Identifier;

import java.io.Serializable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Vector;

import org.jdom.Element;

import security.iit.pdp.EnvException;
import security.iit.pdp.VarEnvironment;

import eu.aniketos.spec.AExp;
import eu.aniketos.spec.BExp;
import eu.aniketos.spec.SExp;
import eu.aniketos.spec.Declaration;

/**
 * Represents a command to be executed inside the environment
 * a command is one or more assigns
 * @author Administrator
 */

import eu.aniketos.spec.Update;

public class UpdatePDP extends Update{

    
   // private Vector<Assign> com;

    public UpdatePDP() {
        super();
    }
    
    public UpdatePDP(Update upd) {
        super();
        com = upd.com;
    }

    public UpdatePDP(Element e) {
        super(e);
    }
    

    /**
     * execute this command.
     * It execute each assign of this command
     * @param env
     * @throws EnvException
     */
	public void exec(VarEnvironment env) throws EnvException {
		try{
			ListIterator<Assign> list = com.listIterator();
			while(list.hasNext()){
				Assign assign = list.next();
				String type = env.typeOf(assign.identifier.identifier);
				System.out.print("assign: "+assign.identifier.identifier+", type: "+type);
				//make the assign and save it in the environment
				if(type.equals(Declaration.dcl_int_typ)){
					AExpPDP aexp = AExpPDP.generateAExpPDP((AExp)assign.value);
					int val = (aexp).eval(env);
					env.setIntVal(assign.identifier.identifier, val);
					System.out.println(" value: "+val);
				}
				else if(type.equals(Declaration.dcl_bool_typ)){
					BExpPDP bexp = BExpPDP.generateBExpPDP((BExp)assign.value);
					boolean val = (bexp).eval(env);
					env.setBoolVal(assign.identifier.identifier, val);
					System.out.println(" value: "+val);
				}
				else if(type.equals(Declaration.dcl_string_typ)){
					SExpPDP sexp = SExpPDP.generateSExpPDP((SExp)assign.value);
					String val = (sexp).eval(env);
					env.setStringVal(assign.identifier.identifier, val);
					System.out.println(" value: "+val);
				}
			}
		}catch(ClassCastException e){
			e.printStackTrace();
		}

	}

}
