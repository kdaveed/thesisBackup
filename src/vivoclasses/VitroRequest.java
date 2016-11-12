package vivoclasses;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import edu.cornell.mannlib.vitro.webapp.dao.DummyFactory;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;

public class VitroRequest {

  public WebappDaoFactory getWebappDaoFactory(){
    return new DummyFactory();
  }
  
  public String getParameter(String parameterName){
    
    return parameterName + "URI";
  }
  
  public String generateNewUri(){
    
    return UUID.randomUUID().toString();
  }
  
  public Map<String, String[]> getParameterMap(){
    
    return new HashMap<String, String[]>();
  }
}
