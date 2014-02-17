package eu.aniketos.notification.webtrigger;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.aniketos.notification.IAlert;

/**
 * Implementation of the Alert Servlet. It receives messages from the web interface and
 * produces triggers.
 * @author Kostas Giannakakis
 *
 */
public class AlertServlet extends HttpServlet {
	
	/** Logger */
	private static Logger logger = LoggerFactory.getLogger(AlertServlet.class);    
    
	private static final long serialVersionUID = 1L;

    /** The Alert service */
	private IAlert alertService;
	
    /**
    * Implementation of the servlet's HTTP GET action.
    * @param req The HTTP request object
    * @param resp The HTTP response object
    * @throws IOException
    */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
	
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		
		PrintWriter writer = resp.getWriter();
        writer.println("[]");
		writer.close();
	}
    
    /**
    * Implementation of the servlet's HTTP POST action. Called from the web interface to trigger an alert.
    * @param req The HTTP request object
    * @param resp The HTTP response object
    * @throws IOException
    */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
	
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		
        boolean success = true;
        String error = "";

		String myServiceId = req.getParameter("serviceId");
		String myAlertType = req.getParameter("type");
		String myAlertValue = req.getParameter("val");
		String myAlertDesc = req.getParameter("desc");
		String myThreatId = req.getParameter("threatId");

		if (myServiceId == null || myServiceId.length() == 0) {
			error = "Service ID is missing!";
        }
        else if (alertService == null) {
            error = "Internal error!";
        }
        else {
            logger.debug("Sendind alert for service {}: {}, {}, {}, {}",
                         myServiceId, myAlertType, myAlertValue, myAlertDesc, myThreatId);
        
			alertService.alert(myServiceId, myAlertType, myAlertValue, myAlertDesc, myThreatId);        
        }  
                
        String json = String.format("{\"success\":%s,\"error\":\"%s\"}",
                     success ? "true" : "false", error.replace('"', '\''));
        
        PrintWriter writer = resp.getWriter();        
        writer.println(json);	
		writer.close();
	}    
	  
    /**
    * Sets the Alert service
    * @param alertService The Alert service
    */
    public synchronized void setAlertService(IAlert alertService) {
        logger.debug("Converter Service was set. !");
        this.alertService = alertService;        
    }

    /**
    * Unsets the Alert service
    * @param _service The Alert service
    */
    public synchronized void unsetAlertService(IAlert alertService) {
        logger.debug("Converter Service was unset.");
        if (this.alertService == alertService) {
            this.alertService = null;
        }
    } 	  
}

