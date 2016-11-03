package rdfbones.formConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rdfbones.rdfdataset.Graph;
import rdfbones.rdfdataset.InputNode;
import rdfbones.rdfdataset.LiteralTriple;
import rdfbones.rdfdataset.SelectNode;
import rdfbones.rdfdataset.Triple;
import vivoclasses.QueryUtils;
import vivoclasses.VitroRequest;

public class SPARQLDataGetter {

  private boolean substituted = false;
  List<String> selectUris = new ArrayList<String>();
  List<String> selectLiterals = new ArrayList<String>();
  List<String> inputNodes = new ArrayList<String>();
  List<String> queryTriples = new ArrayList<String>();
  String query;
  
  public SPARQLDataGetter(Graph graph){
    
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
  
}
