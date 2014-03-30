package security.iit.pdp;


public class pdpResult{
	

	// For correlating the events.
	
	private static  String Result;
	private static  String RuleId;
	private static  String ServiceId;
	
	
	
	
     
	
	public pdpResult(String  result) {
		this.Result = result;
		
	}
	

	
	//get methods

	public static String  getResult() {
		return Result;
	}

	public static String  getRuleId() {
		return RuleId;
	}
	
	public static String  getServiceId() {
		return ServiceId;
	}
	//set methods

		public static void setResult(String  result) {
			Result = result;
		}

		public static void setRuleId(String  ruleid) {
			RuleId = ruleid;
		}
		
		public static void setServiceId(String  serviceid) {
			ServiceId = serviceid;
		}

		
	}

	
