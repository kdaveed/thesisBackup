package vivoclasses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryUtils {

  public static List<Map<String, String>> getResult(String queryStr, List<String> uris, List<String> literals, VitroRequest vreq){
    
    //Generating dummy data
    List<Map<String, String>> result = new ArrayList<Map<String, String>>();
    Map<String, String> singleResult = new HashMap<String, String>();
    for(String uri : uris){
      String rand = new String(uri + Double.toString(Math.random()*100000).substring(0, 4));
      singleResult.put(uri, rand);
    }
    if(literals != null){
      for(String literal : literals){
        singleResult.put(literal, literal);
      }
    }
    result.add(singleResult);
    return result;
  }
  
  public static String subUrisForQueryVars(String queryString, Map<String, String> varsToUris) {
  
    for (String var : varsToUris.keySet()) {
       queryString = subUriForQueryVar(queryString, var, varsToUris.get(var));
    }
    return queryString;
  }

  /** Manually replace a query variable with a uri when prebinding causes the query to fail, probably
   * due to a Jena bug.
   */
  public static String subUriForQueryVar(String queryString, String varName, String uri) {
      return queryString.replaceAll("\\?" + varName + "\\b", "<" + uri + ">");
  }
}
