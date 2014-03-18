package eu.aniketos.pdplib;


import java.io.BufferedReader;
import java.io.DataInputStream;
//import java.io.File;
import java.io.FileInputStream;
//import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
//import java.util.HashMap;
//import java.util.Map;
import java.util.Vector;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;

import security.iit.pdp.pdpResult;
import eu.aniketos.spmm.impl.ContractMonitoring;

public class BundleInvoker {

	BundleInvoker(){};
	
	public Object invoke(String funct, String tag, Class[] parTypes, Object[] parValues){
		
		Return invocation= findFunction(funct, parTypes, parValues);
		
		//System.out.println("Try");
		
		if(invocation==null){return null;}
		
		//System.out.println("Function found");
		
		String address = invocation.addr;
		MyFunction mf=invocation.mf;
		if(!checkFunctType(tag, mf.type)){return null;};
		
		//System.out.println("Function type is ok");
		
		if(!checkArguments(parTypes,mf.types)){return null;};
		
		//System.out.println("arguments are ok");
		
		//System.out.println("!!!!!!!"+mf.types[0].getCanonicalName());
		
		Object[] res;
        JaxWsDynamicClientFactory dcf= JaxWsDynamicClientFactory.newInstance();
       // System.out.println("First stage");
        
        
		try {
			//Client client =dcf.createClient(new URL("http://hestia.atc.gr/eu/aniketos/trustworthiness/ext/messaging/ITrustworthinessPrediction?wsdl")); 
			//Client client =dcf.createClient(new URL("http://localhost:9090/converter?wsdl"));//URL("http://hestia.atc.gr/eu/aniketos/trustworthiness/ext/messaging/ITrustworthinessPrediction?wsdl"));
			Client client =dcf.createClient(new URL(address));
			//System.out.println("Second stage");
			
			System.out.println("Invoking:" +mf.name);
			//res = client.invoke("toFahrenheit", 33.0);// "http://83.235.133.36/AniketosWS/DoUPModuleSoapHttpPort?wsdl");
			res = client.invoke(mf.name, parValues);
			//res = client.invoke("verifyWSDL", "Some input", "Basic256Sha256Rsa15", "symmetric", "AES", 256, "Basic256Sha256Rsa15", "symmetric", "AES", 256);
			
			System.out.println("Result: "+res[0]);
			String result=res[0].toString();
			
			if (result=="false"){
				System.out.println(">>>>>>>>> Policy violated");
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
			}
							
			System.out.println("This is the end");
			
			return setObject(res[0].toString(),mf.type.toString());
			
		} catch (Exception e) {
			System.out.println("Cannot connect to the service");
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return null;
		}
		
	}
	

	private boolean checkFunctType(String tag, Class type) {
		if(tag.equals("real") && type.getCanonicalName().equals("java.lang.Double")){return true;};
		if(tag.equals("int") && type.getCanonicalName().equals("java.lang.Integer")){return true;};
		if(tag.equals("bool") && type.getCanonicalName().equals("java.lang.Boolean")){return true;};
		if(tag.equals("string") && type.getCanonicalName().equals("java.lang.String")){return true;};
		return false;
	}

	private boolean checkArguments(Class[] parTypes, Class[] types) {
		if(parTypes.length!=types.length){return false;}
		for(int i=0; i<parTypes.length;i++){
			if(!parTypes[i].equals(types[i])){return false;}
		}
		return true;
	}

	private Return findFunction(String funct, Class[] parTypes,
			Object[] parValues) {
		try{
			
			FileInputStream fstream = new FileInputStream("Function.ini");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br= new BufferedReader(new InputStreamReader(in));
			String strLine;
			while((strLine=br.readLine())!=null && !checkString(strLine, funct)){
			}
			if(strLine!=null){
				String address = getAddress(strLine);
				MyFunction mf=getMyFunction(strLine);
				if(address==null || mf==null){return null;}
				Return res = new Return(address,mf);
				return res;
			}
			System.out.println("Nothing was found");
			return null;
		}catch(Exception e){
			System.out.println("Cannot read Function.ini");
			return null;
		}
		
	};
	
	private MyFunction getMyFunction(String strLine) {
		MyFunction res=new MyFunction();
		
		// invocation name
		int line = strLine.indexOf('|');
		if(line==-1){
			System.out.println("Error in syntax of Function.ini.");
			return null;}
		
		// wsdl address
		line = strLine.indexOf('|',line+1);
		if(line==-1){
			System.out.println("Error in syntax of Function.ini.");
			return null;}
		
		String funct = removeSpace(strLine.substring(line+1,strLine.length()));
		
		
		// function return type
		line = funct.indexOf(' ');
		if(line==-1){
			System.out.println("Error in syntax of Function.ini.");
			return null;}
		String t=removeSpace(funct.substring(0,line));
		Class cl = setClass(t);
		if(cl==null){
			System.out.println("Error in syntax of Function.ini.");
			return null;};
		res.setType(cl);
		
		// function name
		int line2=funct.indexOf('(');
		if(line2==-1){
			System.out.println("Error in syntax of Function.ini.");
			return null;}
		String n=removeSpace(funct.substring(line+1,line2));
		res.setName(n);
		
		// function parameters
		line=funct.indexOf(',');
		String param;
		String type;
		String val;
		Class someClass;
		Vector<Class> types= new Vector<Class>();
		while(line!=-1){
			param=funct.substring(line2+1,line);
			
			type=removeSpace(param);
			someClass=setClass(type); 
			if(someClass==null){
				System.out.println("Error in syntax of Function.ini.");
				return null;}
			types.add(someClass);
			
			line2=line;
			line = funct.indexOf(',', line+1);
		}
		
		line=funct.indexOf(')');
		
		param=funct.substring(line2+1,line);
		
		type=removeSpace(param);
		someClass=setClass(type); 
		if(someClass==null){
			System.out.println("Error in syntax of Function.ini.");
			return null;}
		types.add(someClass);

		res.setTypes(types);
		
		return res;
	}

	private Object setObject(String val, String type) {
		if(type.equals("int")){
			Integer res = Integer.parseInt(val);
			return res;};
		if(type.equals("bool")){
			Boolean res;
			if(val.equals("true")){
				return true;
			};
			if(val.equals("false")){
				return false;
			};
			return null;};
		if(type.equals("string")){
			String res=val;
			return val;
			};
		if(type.equals("real")){
			Double res = Double.parseDouble(val);
			return res;
			};
		return null;
	}

	private Class setClass(String t) {
		try {
			if(t.equals("int")){return Class.forName("java.lang.Integer");};
			if(t.equals("bool")){return Class.forName("java.lang.Boolean");};
			if(t.equals("string")){return Class.forName("java.lang.String");};
			if(t.equals("real")){return Class.forName("java.lang.Double");};
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Wrong specificaiton of types!");
		}
		return null;
	}

	private String getAddress(String strLine) {
		int line = strLine.indexOf('|');
		if(line==-1){return null;}
		int line2 = strLine.indexOf('|',line+1);
		if(line2==-1){return null;}
		
		return removeSpace(strLine.substring(line+1,line2));
	}

	private boolean checkString(String strLine, String f) {
		int line = strLine.indexOf('|');
		if(line==-1){
			System.out.println("Syntax error in Function.ini");
			return false;
		}
		String nameNew=removeSpace(strLine.substring(0,line));
		if(nameNew.equals(f)){
			return true;
		}
		return false;
	}
	
	private String removeSpace(String str) {
		int i=0;
		while(i<str.length() && str.charAt(i)==' '){
			i++;
		}
		if(i==str.length()){return "";}
		str=str.substring(i,str.length());
		
		i=str.length()-1;
		while(i>0 && str.charAt(i)==' '){
			i--;
		}
		
		str=str.substring(0,i+1);
		return str;
	}

	private class MyFunction{
		Class type;
		String name;
		Class[] types;
		
		
		public Class getType() {
			return type;
		}

		public void setType(Class type) {
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Class[] getTypes() {
			return types;
		}

		public void setTypes(Vector<Class> types) {
			this.types = new Class[types.size()];
			for(int i=0;i<types.size(); i++){
				this.types[i]=types.get(i);
			}
		}

		MyFunction(){
			type=null;
			name=null;
			types=null;
		};
		
		MyFunction(MyFunction mf){
			type=mf.type;
			name = mf.name;
			types = new Class[mf.types.length];
			for(int i=0;i<mf.types.length; i++){
				types[i]=mf.types[i];
			}
		};
		
		MyFunction(String n, Class t1, Class[] t, Object[] p){
			type=t1;
			name = n;
			if(t.length!=p.length){
				types= null;
			}else{
				types = new Class[t.length];
				for(int i=0;i<t.length; i++){
					types[i]=t[i];
				}
			}
		};		
		
	}
	
	private class Return{
		String addr;
		MyFunction mf;
		
		Return(){
			addr=null;
			mf=null;
		};
		
		Return (Return r){
			this.addr=r.addr;
			this.mf=r.mf;
		}
		
		Return(String s, MyFunction m){
			this.addr=s;
			this.mf=m;
		}
		
	}
	
}
