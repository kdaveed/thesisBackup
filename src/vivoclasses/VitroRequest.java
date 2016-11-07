package vivoclasses;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VitroRequest {

  
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
