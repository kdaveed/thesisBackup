package rdfbones.formConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rdfbones.rdfdataset.FormData;
import rdfbones.rdfdataset.Graph;
import rdfbones.rdfdataset.InputNode;
import rdfbones.rdfdataset.LiteralTriple;
import rdfbones.rdfdataset.RestrictionTriple;
import rdfbones.rdfdataset.SelectNode;
import rdfbones.rdfdataset.Triple;
import vivoclasses.QueryUtils;
import vivoclasses.VitroRequest;

public class RDFDataConnector {

  Graph graph;
  
  private boolean substituted = false;
  List<String> selectUris = new ArrayList<String>();
  List<String> selectLiterals = new ArrayList<String>();
  List<String> inputNodes = new ArrayList<String>();
  List<String> queryTriples = new ArrayList<String>();
  FormData formData;
  List<String> dataNodes = new ArrayList<String>();
  
  
  public RDFDataConnector(Graph graph){
    
    this.graph = graph;
    this.initFormDataRetrieval();
    this.initFormDataInput();
  }
  
  void initFormDataRetrieval(){
  
    for(Triple triple : graph.triples){
    
      //InputNodes
      if(triple.subject instanceof SelectNode){
        if(this.inputNodes.contains(triple.subject.varName)){
          this.inputNodes.add(triple.subject.varName);
        }
      }
      
      if(triple.object instanceof SelectNode){
        if(this.inputNodes.contains(triple.object.varName)){
          this.inputNodes.add(triple.object.varName);
        }
      }
      
      if(triple.subject instanceof SelectNode){
        this.selectUris.add(triple.subject.varName);
      }
      
      //By restriction triple everything is selected
      if(triple instanceof RestrictionTriple){
        this.selectUris.add(triple.subject.varName);
        this.selectUris.add(triple.object.varName);
      }
      
      if(triple.object instanceof SelectNode){
        if(triple instanceof LiteralTriple){
          this.selectLiterals.add(triple.object.varName);  
        } else {
          this.selectUris.add(triple.object.varName);
        }
      }
      queryTriples.add(triple.getTriple());
    }
    
    //URIs to select
    String query = new String("SELECT");
    for(String var : selectUris){
      query += " ?" + var;
    }
    //Types
    for(String var : selectUris){
      query += " ?" + var + "Type";
    }
    
    for(String var : selectLiterals){
      query += " ?" + var;
    }
    query += "\nWHERE { \n ";
    
    //Query triples
    for(String triple : queryTriples){
      query += triple;
    }
    //Most Specific Types
    for(String uri : this.selectUris){
      query += getMSTTriple(uri);
    }
    query += " } ";
    this.query = query;
  }
 
  public void substituteRequestData(VitroRequest vreq){

    if(!this.substituted){
      for(String inputNode : this.inputNodes){
        this.query = this.query.replace("?" + inputNode, vreq.getParameter(inputNode));
      }
      this.substituted = true;
    }
  }
  
  public JSONArray getResult(VitroRequest vreq) throws JSONException{
    
    substituteRequestData(vreq);
    return getJSON(vreq, this.query);
  }
  
  public JSONArray getResult(VitroRequest vreq, String value, String key) throws JSONException{
    
    substituteRequestData(vreq);
    //Substitute input data
    query = this.query.replace("?" + key, "<" + value + ">");
    return this.getJSON(vreq, query);
  }
  
  JSONArray getJSON(VitroRequest vreq, String query) throws JSONException{
    
    List<Map<String, String>> results = QueryUtils.getResult(this.query, this.selectUris, this.selectLiterals, vreq);
    JSONArray resultArray = new JSONArray();
    for(Map<String, String> result : results){
      JSONObject jsonObject = new JSONObject();
      for(String uri : this.selectUris){
        jsonObject.put(uri, getInstanceObject(result, uri));
      }
      for(String literal : this.selectLiterals){
        jsonObject.put(literal, result.get(literal));
      }
      resultArray.put(jsonObject);
    }   
    return resultArray;
  }
  
  static JSONObject getInstanceObject(Map<String, String> result, String varName) throws JSONException{
    
    JSONObject jsonObject = new JSONObject();
    jsonObject.put(varName, result.get(varName));
    jsonObject.put(varName + "Type", result.get(varName + "Type"));
    return jsonObject;
  }
  
  static String getMSTTriple(String varName){
    
    return new String("?" + varName + "\tvitro:mostSpecificType\t" + varName + "Type .\n");
  }
  
  /*
   * DATA INPUT
   */
  
  //These triples will be stored - including the type triples
  List<Triple> dataTriples = new ArrayList<Triple>();
  String typeQuery = new String();
  List<String> typeQueryInputs = new ArrayList<String>();
  List<String> typesToSelect = new ArrayList<String>();
  String query;
  
  void initFormDataInput(){
    
    
  }
  
  //Called initially
  public String saveData(JSONObject inputObject, VitroRequest vreq) throws JSONException{
    
    Map<String, String> instanceMap = new HashMap<String, String>();
    return generateN3(inputObject, vreq, this.getInstanceMap(inputObject, vreq, instanceMap));
  }
  
  //Called for subgraphs
  public String saveData(JSONObject inputObject, VitroRequest vreq, String key, String value) throws JSONException{
    
    Map<String, String> instanceMap = new HashMap<String, String>();
    instanceMap.put(key, value);
    return generateN3(inputObject, vreq, this.getInstanceMap(inputObject, vreq, instanceMap));
  }
  
  public String generateN3(JSONObject inputObject, VitroRequest vreq, Map<String, String> instanceMap) throws JSONException{
    
    String triplesToStore = new String();
    Map<String, String> typeMap = this.getTypeMap(inputObject, vreq);
    //Creating string to create
    String triplesToCreate = new String();
    for(Triple triple : this.dataTriples){
      triplesToCreate += triple.getTriple();
    }
    //Substitute classes
    triplesToCreate = QueryUtils.subUrisForQueryVars(this.typeQuery, typeMap);
    triplesToCreate = QueryUtils.subUrisForQueryVars(this.typeQuery, instanceMap);
    
    for(String subgraphKey : this.graph.subGraphs.keySet()){
      //Calling subgraphs
      for(Graph subGraph : this.graph.subGraphs.get(subgraphKey)){
        String key = subGraph.startNode;
        String value = instanceMap.get(key);
        String dataKey = new String();
        if(inputObject.has(key)){
          dataKey = key;
        } else {
          dataKey = subGraph.getIndirectConnector(inputObject);
        }
        JSONArray array = inputObject.getJSONArray(dataKey);
        for(int i = 0; i < array.length(); i++){
          JSONObject jsonObject = array.getJSONObject(i); 
          subGraph.rdfDataConnector.saveData(jsonObject, vreq, dataKey, value);
        }
        triplesToCreate += subGraph.rdfDataConnector.saveData(inputObject, vreq);
      }
    }
    return triplesToStore;
  }
  
  Map<String, String> getInstanceMap(JSONObject obj, VitroRequest vreq, Map<String, String> instanceMap) throws JSONException{
    
    /*
     * Notes : 
     * 
     * In the current version of the code we assume that an instance is either newly created
     * or coming with request. Dependent instance assignment is not implemented.
     *
     * Moreover each triple is created that were defined. There is no optional part yet.
     */

    //Here we create the new instance nodes
    for(String dataNode : dataNodes){
     if(this.formData.input.equals(dataNode)){
      instanceMap.put(dataNode, obj.getString("uri"));
     } else if (this.formData.inputs.contains(dataNode)){
       instanceMap.put(dataNode, obj.getJSONObject(dataNode).getString("uri"));
     } else {
       instanceMap.put(dataNode, vreq.generateNewUri());
     }
    }
    return instanceMap;
  } 
  
  
  Map<String, String> getTypeMap(JSONObject obj, VitroRequest vreq) throws JSONException{

    Map<String, String> inputMap = new HashMap<String, String>();
    for(String typeQueryInput : this.typeQueryInputs){
      if(this.formData.input.equals(typeQueryInput)){
        inputMap.put(typeQueryInput, obj.getString("uri"));
      } else {
        inputMap.put(typeQueryInput, obj.getJSONObject(typeQueryInput).getString("uri"));
      }
    }
    this.typeQuery = QueryUtils.subUrisForQueryVars(this.typeQuery, inputMap);
    return QueryUtils.getResult(this.typeQuery, this.typesToSelect, null, vreq).get(0);
  }
}
