package webappconnector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import rdfbones.formProcessing.WebappConnector;
import rdfbones.lib.ArrayLib;
import edu.cornell.mannlib.vitro.webapp.dao.jena.QueryUtils;

public class PlainJavaWebappConnector implements WebappConnector{

  public Map<String, Object> requestMap = new HashMap<String, Object>();
  boolean logEnabled = true;
  
  public PlainJavaWebappConnector(){
    
  }
  
  public PlainJavaWebappConnector(boolean log){
    this.logEnabled = log;
  }

  public String getInputParameter(String parameterName){
    
    if(this.requestMap.containsKey(parameterName)){
      return this.requestMap.get(parameterName).toString();
    } else {
      return parameterName + "URI";  
    }
  }
  
  public List<Map<String, String>> sparqlResult(String queryStr, List<String> uris, List<String> literals){
    
    System.out.println("uris : " + ArrayLib.debugList(uris));
    System.out.println("literals" + ArrayLib.debugList(literals));
    return QueryUtils.getResult(queryStr, uris, literals);
  }
  
  public void log(String msg){
    
    if(this.logEnabled){
      System.out.println(msg);
    }
  }

  public String getUnusedURI() {
    return new String(UUID.randomUUID().toString().substring(0, 2));
  }

}
