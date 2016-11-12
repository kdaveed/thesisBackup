package rdfbones.rdfdataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.cornell.mannlib.vitro.webapp.dao.InsertException;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.NewURIMaker;
import rdfbones.lib.ArrayLib;
import rdfbones.lib.GraphLib;
import rdfbones.lib.SPARQLDataGetter;
import rdfbones.lib.SPARQLUtils;
import rdfbones.lib.SubSPARQLDataGetter;
import vivoclasses.QueryUtils;
import vivoclasses.VitroRequest;

public class Graph {

  //Input
  public String inputNode;
  
  //Triples
  public List<Triple> dataTriples;
  public List<Triple> schemeTriples;
  
  //Data Input - Storage
  public List<Triple> triplesToStore;
  public List<String> newInstances;
  public List<String> inputInstances;
  public List<String> constantLiterals;
  public List<String> inputLiterals;
  public List<String> inputClasses;
  //Data Input - type query
  public List<String> classesToSelect;
  public List<Triple> typeQueryTriples;

  //Data Retrival
  public List<Triple> dataRetreivalQuery;
  public List<String> urisToSelect;
  public List<String> literalsToSelect;

  //Type retriever query
  public Map<String, Graph> subGraphs = new HashMap<String, Graph>();
  public Map<String, Graph> optionalSubGraphs = new HashMap<String, Graph>();

  public JSONArray existingData = new JSONArray();
  public Map<String, String> existingTriples;

  public SubSPARQLDataGetter dataRetriever;
  public SubSPARQLDataGetter typeRetriever;
  public NewURIMaker newUriMaker;
  
  public Graph() {
    // TODO Auto-generated constructor stub
  }
  
  public void initNodes(List<Triple> dataTriples, List<Triple> schemeTriples){
    
    this.dataTriples = dataTriples;
    this.schemeTriples = GraphLib.getSchemeTriples(dataTriples, schemeTriples);
    GraphLib.setDataInputVars(this);
    GraphLib.setDataRetrievalVars(this);
  }
  
  public void init(VitroRequest vreq, NewURIMaker newUriMaker) throws JSONException{
   
    this.newUriMaker = newUriMaker;
    this.dataRetriever = new SubSPARQLDataGetter(vreq, this.dataRetreivalQuery,  
        this.urisToSelect, this.literalsToSelect, this.inputNode);
    if(this.typeQueryTriples.size() > 0 && this.inputClasses.size() > 0){
      this.typeRetriever = new SubSPARQLDataGetter(vreq,
          this.typeQueryTriples, this.classesToSelect, null, this.inputClasses.get(0));
    }
    //Subgraph initialisation
    for(String subGraphKey : this.subGraphs.keySet()){
      Graph subGraph = this.subGraphs.get(subGraphKey);
      subGraph.init(vreq, newUriMaker);
    }
    //This runs only at the main graph
    if(this.inputNode.equals("subject")){
      System.out.println("ObjectGetter");
      if(vreq.getParameterMap().containsKey("objectUri") || true){ 
        //The existing data has to be queried
        getGraphData(vreq.getParameter("subject"));
      }
    }
  }
  
  public JSONArray getGraphData(String value) throws JSONException{
 
    //Here the parent graph input is used as well
    this.existingData = QueryUtils.getJSON(this.dataRetriever.getData(value));
    this.getSubGraphData();
    return this.existingData;
  }
  
  private void getSubGraphData(){
    for(int i = 0; i < this.existingData.length(); i++){
        for(String key : this.subGraphs.keySet()){
          Graph subGraph = this.subGraphs.get(key);
        try {
          JSONObject object = this.existingData.getJSONObject(i);
          String initialValue = object.getString(subGraph.inputNode);
          object.put(key, subGraph.getGraphData(initialValue));
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    }
  }
  
  public String saveData(JSONObject inputObject, VitroRequest vreq, 
    String key, String value) throws JSONException{
    
    Map<String, String> variableMap = new HashMap<String, String>();
    this.setInstanceMap(inputObject, vreq, variableMap);
    //this.setTypeMap(inputObject, vreq, variableMap);
    if(this.typeRetriever != null){
      List<Map<String, String>> data = this.typeRetriever.getData(variableMap.get(this.inputClasses.get(0)));
      variableMap.putAll(data.get(0));
    }
    variableMap.put(key, value);
    return generateN3(inputObject, vreq, variableMap);
  }
  
  void setInstanceMap(JSONObject obj, VitroRequest vreq, Map<String, String> instanceMap) throws JSONException{
    
    //New Instances
    for(String newInstance : this.newInstances){
     if(this.inputNode.equals(newInstance)){
      instanceMap.put(newInstance, instanceMap.get(newInstance));
     } else {
       try {
        instanceMap.put(newInstance, this.newUriMaker.getUnusedNewURI(null));
      } catch (InsertException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
     }
    }
    //InputData 
    for(String inputClass : this.inputClasses){
      instanceMap.put(inputClass, obj.getString(inputClass));
    }
    for(String inputInstance : this.inputInstances){
      instanceMap.put(inputInstance, obj.getString(inputInstance));
    }
    for(String inputLiterals : this.inputLiterals){
      instanceMap.put(inputLiterals, obj.getString(inputLiterals));
    }
  } 
  
  public String generateN3(JSONObject inputObject, VitroRequest vreq, Map<String, String> variableMap) throws JSONException{
    
    //Creating string to create
    String triplesToStore = SPARQLUtils.assembleTriples(this.triplesToStore);
    triplesToStore = QueryUtils.subUrisForQueryVars(triplesToStore, variableMap);
    for(String subgraphKey : this.subGraphs.keySet()){
      Graph subGraph = this.subGraphs.get(subgraphKey);
      String key = subGraph.inputNode;
      String value = variableMap.get(key);
      JSONArray array = inputObject.getJSONArray(subgraphKey);
      for(int i = 0; i < array.length(); i++){
        JSONObject jsonObject = array.getJSONObject(i); 
        triplesToStore += subGraph.saveData(jsonObject, vreq, key, value);
      }
    }
    return triplesToStore;
  }
  
  public void debug(int n){
    
    String tab = new String(new char[n]).replace("\0", "\t");
    System.out.println(tab + "InputNode : " + this.inputNode);

    System.out.println(tab + "DataTriples : " + ArrayLib.debugTriples(tab, this.dataTriples));
    System.out.println(tab + "SchemeTriples : " + ArrayLib.debugTriples(tab, this.schemeTriples));
    System.out.println(tab + "TriplesToStore : " + ArrayLib.debugTriples(tab, this.triplesToStore));
    
    System.out.println(tab + "newInstances :      " + ArrayLib.debugList(this.newInstances));
    System.out.println(tab + "inputInstances :      " + ArrayLib.debugList(this.inputInstances));
    System.out.println(tab + "constantLiterals :      " + ArrayLib.debugList(this.constantLiterals));
    System.out.println(tab + "inputLiterals :      " + ArrayLib.debugList(this.inputLiterals));
    System.out.println(tab + "inputClasses :      " + ArrayLib.debugList(this.inputClasses));
    System.out.println(tab + "classesToSelect :      " + ArrayLib.debugList(this.classesToSelect));
    System.out.println(tab + "typeQueryTriples :      " + ArrayLib.debugTriples(tab, this.typeQueryTriples));
    
    if(this.dataRetriever != null){
      System.out.println(tab + "DataRetriever Query : \n      " +  this.dataRetriever.getReadableQuery());
    }
    if(this.typeRetriever != null){
      System.out.println(tab + "TypeRetriver Query :      " +  this.typeRetriever.getReadableQuery() + "\n");
    }
    
    int k = n + 1;
    System.out.println(tab + "Subgraphs :  " + subGraphs.keySet().size());
    if(subGraphs.keySet().size() > 0){
      for(String key : subGraphs.keySet()){
        System.out.println(tab + "Key : " + key);
        subGraphs.get(key).debug(k);
      }  
    }
  }
}
