package eu.aniketos.threatrepository.impl.util;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/** Helper class for providing authentication for SSL tunneling through a proxy.
 * 
 * @author balazs
 *
 */
public class ProxyAuthenticator extends Authenticator {
	 
    private String user, password;  
     
       public ProxyAuthenticator(String user, String password) {  
           this.user = user;  
           this.password = password;  
       }  
     
       protected PasswordAuthentication getPasswordAuthentication() {  
           return new PasswordAuthentication(user, password.toCharArray());  
       }  
}
