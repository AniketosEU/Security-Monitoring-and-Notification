package iit.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import security.iit.pdp.PDP;
import security.iit.pdp.PDP.WHEN;
import eu.aniketos.pdplib.SpecificationPDP;

public class ConSpecMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SpecificationPDP spec = new SpecificationPDP();
		try {
			
			//first of all we load the policy from a file, there are 3 ways
			//Option 1: from path string
			//spec.load("rsc\\spec_ret.xml");
			
			//Option 2: from byte array
	//	File file = new File("rsc\\Trustworthiness_exp.xml");
			File file = new File("rsc\\Trustworthiness_Pr.xml");
//			File file = new File("rsc\\spec_ret.xml");	
		byte[] bytes = saveToByteArray(file);
		spec.load(bytes);
			
			//Option 3: from input stream
			//File file = new File("rsc\\spec_ret.xml");
			//spec.load(file.toURI().toURL().openStream());
			
			//then, we instantiate PDP with conspec policy
					PDP pdp_instance = new PDP(spec);
					/*
			//parameters of function OutputStream.write
			String host = "mystr";
			Integer port = 8080;
			Integer length = 8;
			
			//wrap parameters in Object[] to pass them to PDP
			Object[] params = new Object[]{host, port, length};
			*/
					/*
			String eventName = "activity.start";
			String id = "12345";
			String type = "good";
			int time = 12345;
			int date = 54321;
			String exec = "Boss";
			Object[] params = new Object[]{id, type, time, date, exec};	
			*/
					
					String eventName = "java.prova.evaluateTrustworthiness";
					String id="test";
					
				 
				    Object[] params = new Object[]{id};
					
			//only if it is an AFTER call, return is required, else it can be null
			//if(pdp_instance.PDP_allow("java.io.OutputStream.write", WHEN.BEFORE, params, null)){
			if(pdp_instance.PDP_allow("java.prova.invokeService", WHEN.BEFORE, params, null)){
				   
				//if allowed, execute instructions
				//System.out.println("Allowed!");
			}
//			
//			we can also save the policy on file

		
		//	spec.save("rsc\\Trustworthiness2.xml");
			
			System.out.println("finished");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    private static byte[] saveToByteArray(File jar) throws IOException {
        byte[] fileBArray = new byte[(int)jar.length()];
        FileInputStream fis = new FileInputStream(jar);
        fis.read(fileBArray);
        fis.close();
        return fileBArray;
    }
}
