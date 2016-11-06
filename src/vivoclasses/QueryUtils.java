package vivoclasses;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QueryUtils {

  public static List<Map<String, String>> getResult(String queryStr, List<String> uris, List<String> literals, VitroRequest vreq){
    
    List<Map<String, String>> result = new ArrayList<Map<String, String>>();
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
