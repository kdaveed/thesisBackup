package rdfbones.formConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rdfbones.lib.ArrayLib;
import rdfbones.lib.QueryLib;
import rdfbones.lib.SPARQLDataGetter;
import rdfbones.lib.SPARQLUtils;
import rdfbones.lib.SubSPARQLDataGetter;
import rdfbones.rdfdataset.FormData;
import rdfbones.rdfdataset.Graph;
import rdfbones.rdfdataset.RestrictionTriple;
import rdfbones.rdfdataset.Triple;
import vivoclasses.QueryUtils;
import vivoclasses.VitroRequest;

public class RDFDataConnector {

  Graph graph;
  VitroRequest vreq;
  
  SPARQLDataGetter dataRetriever;
  SPARQLDataGetter typeRetriever;
  List<String> selectUris = new ArrayList<String>();
  List<String> selectLiterals = new ArrayList<String>();
  List<String> queryTriples = new ArrayList<String>();
  FormData formData;
  List<String> dataNodes = new ArrayList<String>();
  
  public RDFDataConnector(Graph graph, VitroRequest vreq){
    
    this.vreq = vreq;
    this.graph = graph;
    this.initFormDataRetrieval();
    this.initFormDataInput();
  }
  
  void initFormDataRetrieval(){
    
    //URIs to select
    String selectVars = new String();
    for(String var : this.graph.dataResources){
      selectVars += " ?" + var;
      selectVars += " ?" + var + "Type";
    }
    //Types
    for(String var : this.graph.dataLiterals){
      selectVars += " ?" + var;
    }

    String queryTriples = new String("");
    //Query triples
    for(Triple triple : this.graph.dataTriples){
      queryTriples += triple.getTriple();
    }
    //Most Specific Types
    for(String uri : this.graph.dataResources){
      queryTriples += QueryLib.getMSTTriple(uri);
    }
    
    for(String inputNode : this.graph.inputNodes){
      queryTriples = queryTriples.replace("?" + inputNode, vreq.getParameter(inputNode));
    }
    
    if(this.graph.startNode.equals("")){
      //Main graph
      this.dataRetriever = new SPARQLDataGetter(this.vreq,selectVars,  
          queryTriples, this.graph.dataResources, this.graph.dataResources);
    } else {
      //Subgraph
      this.dataRetriever = new SubSPARQLDataGetter(this.vreq,selectVars,  
          queryTriples, this.graph.dataResources, this.graph.dataResources, this.graph.startNode);
    }
  }
  
  public JSONArray getExistingData() throws JSONException{
    
    return this.getJSON(this.dataRetriever.getData());
  }
  
  public JSONArray getExistingData(String value) throws JSONException{
    
    return this.getJSON(((SubSPARQLDataGetter) this.dataRetriever).getData(value));
  }
  
  public JSONArray getJSON(List<Map<String, String>> results) throws JSONException{
    
    JSONArray resultArray = new JSONArray();
    for(Map<String, String> result : results){
      JSONObject jsonObject = new JSONObject();
      for(String uri : this.graph.dataResources){
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
  

  
  /*
   * DATA INPUT
   */
  //These triples will be stored - including the type triples
  
  List<Triple> dataTriples = new ArrayList<Triple>();
  String typeQuery = new String();
  List<String> typeQueryInputs = new ArrayList<String>();
  boolean typeQueryFlag = false;
  
  void initFormDataInput(){
    
    
    List<Triple> typeQueryTriples = new ArrayList<Triple>();
    List<String> typesToSelect = new ArrayList<String>();

    //We are working with the restrictionTriples of the graph
    //this.typesToSelect = GraphLib.getNewInstanceNodes(this.graph.triples);

    for(Triple triple : this.graph.restrictionTriples){
      if(triple instanceof RestrictionTriple){
        ((RestrictionTriple) triple).increment();
      }
      if(triple.predicate.equals("rdf:type")){
        if(this.formData.input.equals(triple.subject.varName) || 
             this.formData.inputs.contains(triple.subject.varName)){
          typeQueryTriples.add(triple);
          ArrayLib.addDistinct(typesToSelect, triple.object.varName);
        }
      } else {
        typeQueryTriples.add(triple);
        ArrayLib.addDistinct(typesToSelect, triple.subject.varName);
        ArrayLib.addDistinct(typesToSelect, triple.object.varName);
      }
    }
    if(typesToSelect.size() > 0){
      //There is something to query regarding the types
      this.typeQueryFlag = true;
      this.typeRetriever = new SubSPARQLDataGetter(this.vreq,
          SPARQLUtils.assembleQueryTriples(typeQueryTriples),
          SPARQLUtils.assembleSelectVars(typesToSelect),
          typesToSelect, null, this.formData.input);
    }
  }
  
  //Called initially
  public String saveData(JSONObject inputObject, VitroRequest vreq) throws JSONException{
    
    Map<String, String> variableMap = new HashMap<String, String>();
    this.setInstanceMap(inputObject, vreq, variableMap);
    this.setTypeMap(inputObject, vreq, variableMap);
    return generateN3(inputObject, vreq, variableMap);
  }
  
  //Called for subgraphs
  public String saveData(JSONObject inputObject, VitroRequest vreq, String key, String value) throws JSONException{
    
    Map<String, String> variableMap = new HashMap<String, String>();
    this.setInstanceMap(inputObject, vreq, variableMap);
    this.setTypeMap(inputObject, vreq, variableMap);
    variableMap.put(key, value);
    return generateN3(inputObject, vreq, variableMap);
  }
  
  void setInstanceMap(JSONObject obj, VitroRequest vreq, Map<String, String> instanceMap) throws JSONException{
    
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
  } 
  
  void setTypeMap(JSONObject obj, VitroRequest vreq, Map<String, String> variableMap) throws JSONException{

    String value;
    if(this.typeQueryFlag){
      if(this.formData.input.equals(typeQueryInput)){
        variableMap.putAll(this.typeRetriever.getData().get(0));
        variableMap.put(typeQueryInput, );
        value = obj.getString("uri");
      } else {
        value = obj.getJSONObject(typeQueryInput).getString("uri"));
      }
    }
    //Have to be revised#
      
    this.typeQuery = QueryUtils.subUrisForQueryVars(this.typeQuery, variableMap);
    variableMap.putAll(QueryUtils.getResult(this.typeQuery, this.typesToSelect, null, vreq).get(0));
  }
  
  public String generateN3(JSONObject inputObject, VitroRequest vreq, Map<String, String> variableMap) throws JSONException{
    
    String triplesToStore = new String();
    //Creating string to create
    String triplesToCreate = new String();
    for(Triple triple : this.dataTriples){
      triplesToCreate += triple.getTriple();
    }
    //Substitute variables
    triplesToCreate = QueryUtils.subUrisForQueryVars(this.typeQuery, variableMap);
    
    for(String subgraphKey : this.graph.subGraphs.keySet()){
      //Calling subgraphs
      Graph subGraph = this.graph.subGraphs.get(subgraphKey);
      String key = subGraph.startNode;
      String value = variableMap.get(key);
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
    return triplesToStore;
  }
}