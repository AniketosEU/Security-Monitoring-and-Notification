package eu.aniketos.threatrepository;

import java.util.List;

/** This is the interface class of the Threat Repository Module.
 * 
 * @author balazs
 *
 */
public interface ThreatRepositoryService {

    /** This function sets the credentials to use when communicating with the SVRS. 
    * Each SVRS operation is stateless and atomic, therefore this data can be changed at any time
    * without problems. 
    * If this function is not called (or it is called with a "null" userName parameter), the default 
    * 'Aniketos tester' credentials will be used. 
    *
    * @param userName The username to use for authenticating to the threat repository, or null to use the default account.
    * @param userPass The password to use for authenticating to the threat repository, or null to use the default account.
    */
    public void setCredentials(String userName, String userPass);	
	
    /** This function sets the parameters of the proxy server to use when contacting the SVRS; 
    * all subsequent connections will use this server until the bundle is restarted. 
    * To stop using a proxy server, call this method with a "null" proxyName parameter. 
    *
    * @param proxyName The host name of the proxy server, or null to stop using the current proxy.
    * @param proxyPort The port number of the proxy server.
    * @param proxyUser The username to use for authenticating to the proxy server, or null if the proxy is anonymous.
    * @param proxyPass The password to use for authenticating to the proxy server, or null if the proxy is anonymous.
    */
    public void setProxy(String proxyName, Integer proxyPort, String proxyUser, String proxyPass);
	
    /** This function adds a new threat to the SVRS. If updating an existing threat, use updateThreat instead.
    *
    * @param threatID The UUID of the threat to add.
    * @param threatData A eu.aniketos.wp4.components.threatrepository.schema.upload.Threat object containing threat data to be uploaded to the SVRS.
    * @return A string containing the HTTP response code and result of the upload attempt.
    */
    public String addThreat(String threatID, eu.aniketos.threatrepository.schema.upload.Threat threatData);

    /** This function updates an existing threat in the SVRS. If adding a new threat, use addThreat instead.
    *
    * @param threatID The UUID of the threat to update.
    * @param threatData A eu.aniketos.wp4.components.threatrepository.schema.upload.Threat object containing threat data to be uploaded to the SVRS.
    * @return A string containing the HTTP response code and result of the update attempt.
    */
    public String updateThreat(String threatID, eu.aniketos.threatrepository.schema.upload.Threat threatData);

    /** This function removes a threat from the SVRS. Please note that the threat is not physically erased from the database -- its entry is merely flagged as 'deleted'.
    *
    * @param threatID The UUID of the threat to delete.
    * @return A string containing the HTTP response code and result of the deletion attempt.
    */
    public String deleteThreat(String threatID);

    /** This function adds a new countermeasure to the SVRS. To update an existing countermeasure, use updateCountermeasure instead.
    *
    * @param counterMID The UUID of the countermeasure to add.
    * @param counterData A eu.aniketos.wp4.components.threatrepository.schema.upload.Countermeasure object containing countermeasure data to be uploaded to the SVRS.
    * @return A string containing the HTTP response code and result of the upload attempt.
    */
    public String addCountermeasure(String counterMID, eu.aniketos.threatrepository.schema.upload.Countermeasure counterData);

    /** This function updates an existing countermeasure in the SVRS. To add a new countermeasure, use addCountermeasure instead.
    *
    * @param counterMID The UUID of the countermeasure to update.
    * @param counterData A eu.aniketos.wp4.components.threatrepository.schema.upload.Countermeasure object containing countermeasure data to be uploaded to the SVRS.
    * @return A string containing the HTTP response code and result of the update attempt.
    */
    public String updateCountermeasure(String counterMID, eu.aniketos.threatrepository.schema.upload.Countermeasure counterData);

    /** This function removes a countermeasure from the SVRS. Please note that the countermeasure is not physically erased from the database -- its entry is merely flagged as 'deleted'.
    *
    * @param counterMID The UUID of the countermeasure to delete.
    * @return A string containing the HTTP response code and result of the deletion attempt.
    */
    public String deleteCountermeasure(String counterMID);

    
    /** This function fetches a list of countermeasures from the SVRS that match the provided requirements: UUID, name, or domain. Optional parameters should be null if not used.
    *
    * @param name The countermeasure name to search for. Optional, but either name, UUID, or domain need to be specified.
    * @param counterMID The UUID of the countermeasure, if available. Optional, but either name or UUID need to be specified.
    * @param domain The countermeasure domain to search for. Optional, but either name, UUID, or domain need to be specified.
    * @return An ArrayList of Countermeasure objects that correspond to countermeasures in the repository. If there were no hits, it returns an empty list.
    */
    public List<eu.aniketos.threatrepository.schema.download.Countermeasure> getCountermeasures(String name, String counterMID, String domain);
    
    /** THIS FUNCTION IS DEPRECATED, USE getCountermeasures(name, counterMID, domain) instead!
    * This function fetches a list of countermeasures from the SVRS that match the provided requirements: UUID or name. Optional parameters should be null if not used.
    * 
    * @param name The countermeasure name to search for. Optional, but either name or UUID need to be specified.
    * @param counterMID The UUID of the countermeasure, if available. Optional, but either name or UUID need to be specified.
    * @return An ArrayList of Countermeasure objects that correspond to countermeasures in the repository. If there were no hits, it returns an empty list.
    */
    public List<eu.aniketos.threatrepository.schema.download.Countermeasure> getCountermeasures(String name, String counterMID);

    /** This function fetches a list of countermeasures from the SVRS that deal with a particular threat.
    *
    * @param threatID The UUID of the threat.
    * @return An ArrayList of Countermeasure objects that correspond to countermeasures in the repository. If there were no matches, it returns an empty list.
    */
    public List<eu.aniketos.threatrepository.schema.download.Countermeasure> getCountermeasuresForThreat(String threatID);

    /** This function fetches a list of threats from the SVRS that match the provided requirements: UUID, name, or type. Optional parameters should be null if not used.
    *
    * @param name The threat name to search for. Optional, but either name, UUID, or domain need to be specified.
    * @param threatID The UUID of the threat, if available. Optional, but either name, UUID, or domain need to be specified.
    * @param type Defines what kind of threat to search for: <i>threat</i>, <i>vulnerability</i>, or <i>category</i>. Optional.
    * @param domain The threat domain to search for. Optional, but either name, UUID, or domain need to be specified.
    * @return An ArrayList of Threat objects that correspond to threats in the repository. If there were no hits, it returns an empty list.
    */
    public List<eu.aniketos.threatrepository.schema.download.Threat> getThreats(String name, String threatID, ThreatType type, String domain);
    
    /** THIS FUNCTION IS DEPRECATED, USE getThreats(name, threatID, type, domain) instead!
    * This function fetches a list of threats from the SVRS that match the provided requirements: UUID, name, or type. Optional parameters should be null if not used.
    *
    * @param name The threat name to search for. Optional, but either name or UUID need to be specified.
    * @param threatID The UUID of the threat, if available. Optional, but either name or UUID need to be specified.
    * @param type Defines what kind of threat to search for: <i>threat</i>, <i>vulnerability</i>, or <i>category</i>. Optional.
    * @return An ArrayList of Threat objects that correspond to threats in the repository. If there were no hits, it returns an empty list.
    */
    public List<eu.aniketos.threatrepository.schema.download.Threat> getThreats(String name, String threatID, ThreatType type);

    /** This wrapper function fetches a list of threats from the SVRS that are dealt with by a particular countermeasure.
    *
    * @param counterMID The UUID of the countermeasure.
    * @return An ArrayList of Threat objects that correspond to the countermeasure in the repository. If there were no matches, it returns an empty list.
    */
    public List<eu.aniketos.threatrepository.schema.download.Threat> getThreatsForCountermeasure(String counterMID);
    
    /** This function fetches the current list of content tags in the SVRS, along with their usage. The list is ordered by tag name.
     * 
     * @return A List of TagData objects; each object corresponds to a single tag, and contains the number of times the tag is used. 
     */
    public List<TagData> getTagList();
}
