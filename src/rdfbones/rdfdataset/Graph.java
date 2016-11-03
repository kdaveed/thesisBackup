package rdfbones.rdfdataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rdfbones.formConfiguration.SPARQLDataGetter;
import rdfbones.formconfig.GraphProcessor;
import vivoclasses.VitroRequest;

public class Graph {

  public Graph(List<Triple> triples){
    this.triples = triples;
    //The SPARQLDataGetter contains the query generated once by the triples
    this.sparqlDataGetter = new SPARQLDataGetter(this);
  }
  
  public Graph() {
    // TODO Auto-generated constructor stub
  }

  public List<Triple> triples;
  public Triple multiTriple = null;
  public String startNode;
  public Map<String, List<Graph>> subGraphs = new HashMap<String, List<Graph>>();
  public JSONArray results;
  public SPARQLDataGetter sparqlDataGetter;
  
  JSONArray getGraphData(VitroRequest vreq) throws JSONException{
    
    //This is the initial graph where only the VitroRequest inputs are used
    this.results = this.sparqlDataGetter.getResult(vreq);
    this.setSubGraphData(vreq);
    return this.results;
  }
  
  JSONArray getGraphData(VitroRequest vreq, String initialValue, String key) throws JSONException{
  
    //Here the parent graph input is used as well
    this.results = this.sparqlDataGetter.getResult(vreq, initialValue, key);
    this.setSubGraphData(vreq);
    return this.results;
  }
  
  private void setSubGraphData(VitroRequest vreq){
    
    for(String key : this.subGraphs.keySet()){
      for(Graph subGraph : this.subGraphs.get(key)){
        for(int i = 0; i < this.results.length(); i++){
          try {
            //Convert result object to array of the subgraph data
            JSONObject object = results.getJSONObject(i).getJSONObject(key);
            String initialValue = object.getString("uri");
            String subGraphKey = GraphProcessor.getObject(subGraph.multiTriple, key);
            object.put(subGraphKey, subGraph.getGraphData(vreq, initialValue, subGraphKey));
          } catch (JSONException e) {
            e.printStackTrace();
          }
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
    for(Triple triple : triples){
      System.out.println(tab + triple.subject.varName + "   " + triple.predicate + "   " + triple.object.varName);
    }
    System.out.println(tab + "Subgraph");
    if(subGraphs.keySet().size() > 0){
      for(String key : subGraphs.keySet()){
        for(Graph subGraph : subGraphs.get(key)){
          subGraph.debug(n + 1);
        }
      }  
    } 
  }
}
