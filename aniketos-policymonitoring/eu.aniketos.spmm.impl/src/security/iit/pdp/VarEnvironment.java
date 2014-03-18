package security.iit.pdp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import eu.aniketos.spec.Declaration;

public class VarEnvironment {

	/**
	 * hashtable that contains variables
	 */
	private Hashtable<String, Object> hash;
	/**
	 * hashtable that contains parameters
	 */
	private Hashtable<String, Object> par_hash;
	
	public VarEnvironment(){
		hash = new Hashtable<String, Object>();
		par_hash = new Hashtable<String, Object>();
	}
	
	public VarEnvironment(String[] var, Object[] val) throws EnvException{
		hash = new Hashtable<String, Object>();
		par_hash = new Hashtable<String, Object>();
		if(var.length != val.length)
			throw new EnvException("error: variables length doesn't match with values length");
		for(int i=0; i<var.length; i++){
			hash.put(var[i], val[i]);
		}
	}
	
	/**
	 * get the type of the selected variable stored in the environment
	 * @param var: the variable
	 * @return
	 */
	public String typeOf(String var){
		if(this.get(var) instanceof Integer )
			return Declaration.dcl_int_typ;
		else if(this.get(var) instanceof String )
			return Declaration.dcl_string_typ;
		else if(this.get(var) instanceof Boolean )
			return Declaration.dcl_bool_typ;
		else return ""; //potrebbero esserci altri tipi
	}
	
	//Le get cercano per primo il parametro se esiste, altrimenti cercano la variabile
	public synchronized int getIntVal(String var) throws EnvException{
		Object value = this.get(var);
		if(value==null)
			throw new EnvException("error: object "+var+" not found");
		if(!(value instanceof Integer ))
			throw new EnvException("error: trying to get int value, but "+var+" is of type: "+value.getClass().getSimpleName());
		else
			return (Integer)value;
	}
	
	public synchronized boolean getBoolVal(String var) throws EnvException{
		Object value = this.get(var);
		if(value==null)
			throw new EnvException("error: object "+var+" not found");
		if(!(value instanceof Boolean ))
			throw new EnvException("error: trying to get boolean value, but "+var+" is of type: "+value.getClass().getSimpleName());
		else
			return (Boolean)value;
	}
	
	public synchronized String getStringVal(String var) throws EnvException{
		Object value = this.get(var);
		if(value==null)
			throw new EnvException("error: object "+var+" not found");
		if(!(value instanceof String ))
			throw new EnvException("error: trying to get String value, but "+var+" is of type: "+value.getClass().getSimpleName());
		else
			return (String)value;
	}
	
	public synchronized double getDoubleVal(String var) throws EnvException{
		Object value = this.get(var);
		if(value==null)
			throw new EnvException("error: object "+var+" not found");
		if(!(value instanceof Double ))
			throw new EnvException("error: trying to get String value, but "+var+" is of type: "+value.getClass().getSimpleName());
		else
			return (Double)value;
	}
	
	//i set cambiano solo le variabili, perchè i parametri non si possono modificare
	
	public synchronized void setIntVal(String var, int val) throws EnvException{
		Object value = hash.get(var);
		if(value!=null)
			if(!(value instanceof Integer ))
				throw new EnvException("error: trying to set Integer value, but "+var+" is already defined as: "+value.getClass().getSimpleName());
		hash.put(var, val);
	}
	
	public synchronized void setBoolVal(String var, boolean val) throws EnvException{
		Object value = hash.get(var);
		if(value!=null)
			if(!(value instanceof Boolean))
				throw new EnvException("error: trying to set Boolean value, but "+var+" is already defined as: "+value.getClass().getSimpleName());
		hash.put(var, val);
	}
	
	public synchronized void setStringVal(String var, String val) throws EnvException{
		Object value = hash.get(var);
		if(value!=null)
			if(!(value instanceof String))
				throw new EnvException("error: trying to set String value, but "+var+" is already defined as: "+value.getClass().getSimpleName());
		hash.put(var, val);
	}
	
	public synchronized void setDoubleVal(String var, double val) throws EnvException{
		Object value = hash.get(var);
		if(value!=null)
			if(!(value instanceof Double))
				throw new EnvException("error: trying to set String value, but "+var+" is already defined as: "+value.getClass().getSimpleName());
		hash.put(var, val);
	}
	
	/**
	 * bind a declared variable in the environment
	 * @param var: variable
	 * @param val: value of var
	 * @throws EnvException
	 */
	public synchronized void bind(String var, Object val) throws EnvException{
		//aggiungere controllo collisioni
		if(val instanceof Integer)
			setIntVal(var, (Integer)val);
		else if(val instanceof Boolean)
			setBoolVal(var, (Boolean)val);
		else if(val instanceof String)
			setStringVal(var, (String)val);
		else if(val instanceof Double)
			setDoubleVal(var, (Double)val);
		else throw new EnvException("error: "+var+" is of type: "+val.getClass().getSimpleName()+", only Boolean, Integer, Double and String types are permitted");
	}
	
	/**
	 * bind method parameters in the environment
	 * @param par: parameter
	 * @param val: value of par
	 * @throws EnvException
	 */
	public synchronized void parBind(String par, Object val) throws EnvException{
		if(val.getClass().equals("java.lang.Integer"))
			par_hash.put(par, ((Integer)val).intValue());
		else if(val.getClass().equals("java.lang.Boolean"))
			par_hash.put(par, ((Boolean)val).booleanValue());
		else if(val.getClass().equals("java.lang.Character"))
			par_hash.put(par, ((Character)val).charValue());
		else if(val.getClass().equals("java.lang.Byte"))
			par_hash.put(par, ((Byte)val).byteValue());
		else if(val.getClass().equals("java.lang.Double"))
			par_hash.put(par, ((Double)val).doubleValue());
		else if(val.getClass().equals("java.lang.Float"))
			par_hash.put(par, ((Float)val).floatValue());
		else if(val.getClass().equals("java.lang.Long"))
			par_hash.put(par, ((Long)val).longValue());
		else if(val.getClass().equals("java.lang.Short"))
			par_hash.put(par, ((Short)val).shortValue());
		else par_hash.put(par, val);
	}

	/**
	 * clear all the parameters stored
	 */
	public synchronized void flushParHash(){
		par_hash.clear();
	}
	
	/**
	 * get a generic object stored, variable or parameter
	 * @param n :  name of the object
	 * @return: value
	 */
	private Object get(String n){
		if(par_hash.containsKey(n))
			return par_hash.get(n);
		else return hash.get(n);
	}

    public List<String> ListEntries() {
        List<String> retList = new ArrayList<String>();
        String[] keys = (String[])hash.keySet().toArray();
        retList.addAll(Arrays.asList(keys));
        return retList;
    }
}
