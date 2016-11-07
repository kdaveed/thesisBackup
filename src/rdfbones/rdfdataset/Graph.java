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
import rdfbones.lib.ArrayLib;
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
  public List<String> classNodes;
  
  public Graph(List<Triple> triples){
    
    this.dataTriples = triples;
  }
  
  public Graph() {
    // TODO Auto-generated constructor stub
  }
  
  public Graph(String startNode) {
    // TODO Auto-generated constructor stub
    this.startNode = startNode;
  }
  
  public void initNodes(){
    this.dataResources = GraphLib.getNewInstanceNodes(this.dataTriples);
    this.dataLiterals = GraphLib.getLiteralNodes(this.dataTriples);
    this.inputNodes = GraphLib.getInputNodes(this.dataTriples);
    this.classNodes = GraphLib.getClassNodes(this.restrictionTriples);
  }
  
  public void init(VitroRequest vreq, FormData formData) throws JSONException{
   
    this.formData = formData;
    //Subgraph initialisation
    for(String subGraphKey : this.subGraphs.keySet()){
      Graph subGraph = this.subGraphs.get(subGraphKey);
      if(formData.subFormData.keySet().contains(subGraphKey)){
          System.out.println("Direct subformdata : " + subGraphKey);
          subGraph.init(vreq, this.formData.subFormData.get(subGraphKey));
      } else {
        //Search in the nodes of the subforms
        for(String formDataKey : this.formData.subFormData.keySet()){
          System.out.println("subGraphKeyInner ------  " +  formDataKey);
          if(subGraph.dataResources.contains(formDataKey) ||
              subGraph.classNodes.contains(formDataKey)){
            System.out.println("Found ------  " +  formDataKey + 
                "   "  + this.formData.subFormData.get(formDataKey));
            subGraph.init(vreq, this.formData.subFormData.get(formDataKey));
          }
        }
      }
    }
    this.rdfDataConnector = new RDFDataConnector(this, vreq);
    //This runs only at the main graph
    if(this.startNode.equals("")){
      if(vreq.getParameterMap().containsKey("objectUri")){ 
        //The existing data has to be queried
        this.existingData = rdfDataConnector.getExistingData();
        this.setSubGraphData();
      }  
    }
  }
  
  JSONArray getGraphData(String value) throws JSONException{
  
    //Here the parent graph input is used as well
    this.existingData = this.rdfDataConnector.getExistingData(value);
    this.setSubGraphData();
    return this.existingData;
  }
  
  private void setSubGraphData(){
    
    for(String key : this.subGraphs.keySet()){
      Graph subGraph = this.subGraphs.get(key);
      for(int i = 0; i < this.existingData.length(); i++){
        try {
          //Convert result object to array of the subgraph data
          JSONObject object = this.existingData.getJSONObject(i).getJSONObject(key);
          String initialValue = object.getString("uri");
          String subGraphKey = GraphLib.getObject(subGraph.multiTriple, key);
          object.put(subGraphKey, subGraph.getGraphData(initialValue));
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    }
  }
  
  public void debug(int n){
    
    String tab = new String(new char[n]).replace("\0", "\t");
    System.out.println(tab + "StartNode : " + this.startNode);
    System.out.println(tab + "Data triples : ");
    for(Triple triple : this.dataTriples){
      System.out.println(tab + triple.subject.varName + " \t " + triple.predicate + " \t " + triple.object.varName);
    }
    System.out.println("");
    System.out.println(tab + "Restriction triples : ");
    for(Triple triple : this.restrictionTriples){
      System.out.println(tab + triple.subject.varName + " \t " + triple.predicate + " \t " + triple.object.varName);
    }
    System.out.println(tab + "ClassNodes :      " + ArrayLib.debugList(this.classNodes));
    System.out.println(tab + "DataResources :      " + ArrayLib.debugList(this.dataResources));
    System.out.println(tab + "InputNodes :      " + ArrayLib.debugList(this.inputNodes));

    System.out.println(tab + "DataRetriever Query : \n      " +  this.rdfDataConnector.dataRetriever.getQuery());
    //System.out.println(tab + "TypeRetriver Query :      " +  this.rdfDataConnector.typeRetriever.getQuery() + "\n");

    int k = n + 1;
    System.out.println(tab + "Subgraphs :  " + subGraphs.keySet().size());
    if(subGraphs.keySet().size() > 0){
      for(String key : subGraphs.keySet()){
        System.out.println(tab + "Key : " + key);
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
