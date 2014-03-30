package security.iit.pdp;

/**
 * Represents an exception of the Environment
 * @author Luca
 *
 */
public class EnvException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private String error;
	
	public EnvException(){
		super();
		error = "unknown type error";
	}
	
	public EnvException(String error){
		super();
		this.error = error;
	}
	
	public String getError(){
		return error;
	}
}
