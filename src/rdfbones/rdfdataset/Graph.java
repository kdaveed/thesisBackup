package rdfbones.rdfdataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rdfbones.formConfiguration.RDFDataConnector;
import rdfbones.formconfig.GraphProcessor;
import rdfbones.lib.GraphLib;
import vivoclasses.VitroRequest;

public class Graph {

  public List<Triple> dataTriples;
  public List<Triple> restrictionTriples;
  public List<String> newInstances;
  public List<String> inputNodes = new ArrayList<String>();
  public Triple multiTriple = null;
  public String startNode;
  public Map<String, Graph> subGraphs = new HashMap<String, Graph>();
  public JSONArray existingData;
  public RDFDataConnector rdfDataConnector;
  public FormData formData;
  public List<String> nodes;
 
  public List<String> dataResources;
  public List<String> dataLiterals;
  
  
  public Graph(List<Triple> triples){
    
    this.dataTriples = triples;
    //The SPARQLDataGetter contains the query generated once by the triples
    //this.rdfDataGenerator = new RDFDataGenerator(this);
  }
  
  public Graph() {
    // TODO Auto-generated constructor stub
  }
  
  public void init(VitroRequest vreq, FormData formData) throws JSONException{
   
    this.dataResources = GraphLib.getNewInstanceNodes(this.dataTriples);
    this.dataLiterals = GraphLib.getLiteralNodes(this.dataTriples);
    this.inputNodes = GraphLib.getInputNodes(this.dataTriples);
    
    this.formData = formData;
    this.rdfDataConnector = new RDFDataConnector(this, vreq);
    if(vreq.getParameterMap().containsKey("objectUri")){ 
      //The existing data has to be queried
      this.existingData = rdfDataConnector.getExistingData();
    }
    
    
    //Form Data Initialisation
    for(String subGraphKey : this.subGraphs.keySet()){
      Graph subGraph = this.subGraphs.get(subGraphKey);
      if(formData.subFormData.keySet().contains(subGraphKey)){
          subGraph.init(vreq, this.formData.subFormData.get(subGraphKey));
      } else {
        //We have to search in the first node
        
      }
    }
  }
  
  JSONArray getGraphData(VitroRequest vreq) throws JSONException {
    
    //This is the initial graph where only the VitroRequest inputs are used
    this.results = this.rdfDataConnector.getResult(vreq);
    this.setSubGraphData(vreq);
    return this.results;
  }
  
  JSONArray getGraphData(VitroRequest vreq, String initialValue, String key) throws JSONException{
  
    //Here the parent graph input is used as well
    this.results = this.rdfDataConnector.getData(vreq, initialValue, key);
    this.setSubGraphData(vreq);
    return this.results;
  }
  
  private void setSubGraphData(VitroRequest vreq){
    
    for(String key : this.subGraphs.keySet()){
      Graph subGraph = this.subGraphs.get(key);
      for(int i = 0; i < this.results.length(); i++){
        try {
          //Convert result object to array of the subgraph data
          JSONObject object = results.getJSONObject(i).getJSONObject(key);
          String initialValue = object.getString("uri");
          String subGraphKey = GraphLib.getObject(subGraph.multiTriple, key);
          object.put(subGraphKey, subGraph.getGraphData(vreq, initialValue, subGraphKey));
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    }
  }
  
  public String getReplacedQuery(List<String> variable, int cnt, String query){
    for(String var : variable){
      //Remove @ character
      var = "?" + var;
      query = query.replaceAll("\\" + var, var + Integer.toString(cnt)); 
    }
    return query;
  }
  
  public void debug(int n){
    
    String tab = new String(new char[n]).replace("\0", "\t");
    System.out.println(tab + "Triples : ");
    for(Triple triple : this.dataTriples){
      System.out.println(tab + triple.subject.varName + "   " + triple.predicate + "   " + triple.object.varName);
    }
    int k = n + 1;
    System.out.println(tab + "Subgraphs :  " + subGraphs.keySet().size());
    if(subGraphs.keySet().size() > 0){
      for(String key : subGraphs.keySet()){
        System.out.println(". Key : " + key);
        subGraphs.get(key).debug(k);
      }  
    } 
  }
  
  public String getIndirectConnector(JSONObject obj){
    for(String node : this.nodes){
      if(obj.has(node)){
        return node;
      }
    }
    return null;
  }
}
