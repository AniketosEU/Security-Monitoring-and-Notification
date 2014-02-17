package eu.aniketos.notification.webtrigger;

import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class that registers static content and the alert servlet in a HttpService.
 * It is defined as a component with immediate start.
 * @author Kostas Giannakakis
 *
 */
public class ServletManager {

	/** Logger */
	private static Logger logger = LoggerFactory.getLogger(ServletManager.class);
    
	/** The instance of http service */
	private HttpService httpService;
	
	/** The Alert servlet */
	private Servlet servlet;
	
	/** The path (alias) of the servlet. It is read from the servlet's attributes */
	private String context;
	
    /** The app of the application */
    private String appPath = "/alert";
    
	/**
	 * Sets the http service
	 * @param httpService The http service
	 */
	public void setHttpService(HttpService httpService) {
		logger.debug("Setting HTTP service");
		this.httpService = httpService;
		registerStaticContent();
		registerServlet();
	}
	
	/**
	 * Unsets the http service
	 * @param httpService The http service
	 */
	public void unsetHttpService(HttpService httpService) {
		if (this.httpService == httpService) {
			logger.debug("Unsetting service");
			unregisterStaticContent();
			this.httpService = null;
			unregisterServlet();
		}
	}
		
	@SuppressWarnings("rawtypes")
	/**
	 * Sets the Alert servlet
	 * @param servlet The Alert servlet
	 * @param attrs The attributes of the servlet. Contains the path parameter
	 */
	public void setServlet(Servlet servlet, Map attrs) {
		logger.debug("Setting Servlet");

		if (attrs != null && attrs.containsKey("Web-ContextPath")) {
			String ctx = appPath + (String) attrs.get("Web-ContextPath");
			logger.debug("Servlet path: {}", ctx);
			
			this.servlet = servlet;
			this.context = ctx;
			registerServlet();
		}
	}
	
	/**
	 * Unsets the Alert servlet
	 * @param servlet The Alert servlet
	 */
	public void unsetServlet(Servlet servlet) {
		logger.debug("Unsetting servlet");
		unregisterServlet();
	}	
	
	/**
	 * Registers the static content of the web application
	 */
	private void registerStaticContent() {
		if (httpService != null) {
			try {
				httpService.registerResources( appPath, "/html", null );
				httpService.registerResources( appPath + "/images", "/images", null );
			} catch (NamespaceException e) {
				logger.debug("Failed to register static content");
			}
		}
	}

	/**
	 * Unregisters the static content of the web application
	 */
	private void unregisterStaticContent() {
		if (httpService != null) {
			httpService.unregister(appPath);
			httpService.unregister(appPath + "/images");
		}
	}
	
	/**
	 * Registers the servlet
	 */
	private void registerServlet() {
		if (httpService != null && servlet != null && context != null) {
			try {
				httpService.registerServlet(context, servlet, null, null);
			} catch (ServletException e) {
				logger.warn("Failed to registerServlet: {}", e.getMessage());
			} catch (NamespaceException e) {
				logger.warn("Failed to registerServlet: {}", e.getMessage());
			}		
		}
	}

	/**
	 * Unregisters the servlet
	 */
	private void unregisterServlet() {
		if (httpService != null && servlet != null && context != null) {
			httpService.unregister(context);
		}
	}	

}
