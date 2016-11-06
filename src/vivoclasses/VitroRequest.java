package vivoclasses;

import java.util.UUID;

public class VitroRequest {

  
  public String getParameter(String parameterName){
    
    return parameterName;
  }
  
  public String generateNewUri(){
    
    return UUID.randomUUID().toString();
  }
}
