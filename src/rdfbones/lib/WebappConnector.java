package rdfbones.lib;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;

import edu.cornell.mannlib.vitro.webapp.dao.jena.QueryUtils;

public class WebappConnector {

  public Map<String, Object> requestMap = new HashMap<String, Object>();
  public boolean logEnabled = true;
  
  public WebappConnector(){
    
  }
  
  public WebappConnector(boolean log){
    this.logEnabled = log;
  }
  
  public String getUnusedNewURI(){
    return new String(UUID.randomUUID().toString().substring(0, 2));
  }
  
  public String getInputParameter(String parameterName){
    
    if(this.requestMap.containsKey(parameterName)){
      return this.requestMap.get(parameterName).toString();
    } else {
      return parameterName + "URI";  
    }
  }
  
  public List<Map<String, String>> sparqlResult(String queryStr, List<String> uris, List<String> literals){
    
    return QueryUtils.getResult(queryStr, uris, literals);
  }
  
  public void log(String msg){
    
    if(this.logEnabled){
      System.out.println(msg);
    }
  }
}
