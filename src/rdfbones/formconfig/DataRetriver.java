package rdfbones.formconfig;

import java.util.ArrayList;
import java.util.List;

import rdfbones.formConfiguration.SPARQLDataGetter;
import rdfbones.rdfdataset.LiteralTriple;
import rdfbones.rdfdataset.SelectNode;
import rdfbones.rdfdataset.InputNode;
import rdfbones.rdfdataset.Triple;
import rdfbones.rdfdataset.Graph;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import vivoclasses.VitroRequest;

public class DataRetriver {

  static JSONObject getData(Graph graph, VitroRequest vreq){
    
    JSONArray queryResult = getQuery(graph, vreq).getData();
    return null;
  }
  
  static JSONArray getSubGraph(String connector, Graph graph, VitroRequest vreq){
    
    
    return null;
  }
  
  static SPARQLDataGetter getQuery(Graph graph, VitroRequest vreq){
    
    List<String> selectUris = new ArrayList<String>();
    List<String> selectLiterals = new ArrayList<String>();
    List<String> inputNodes = new ArrayList<String>();
    List<String> queryTriples = new ArrayList<String>();
    
    for(Triple triple : graph.triples){
      
      if(triple.subject instanceof SelectNode){
        selectUris.add(triple.subject.varName);
      }
      
      if(triple.subject instanceof InputNode){
        selectUris.add(triple.subject.varName);
      }
      
      if(triple.object instanceof SelectNode){
        if(triple instanceof LiteralTriple){
          selectLiterals.add(triple.object.varName);  
        } else {
          selectUris.add(triple.object.varName);
        }
      }
      queryTriples.add(triple.getTriple());
    }
    
    //Assemble
    String query = new String("SELECT");
    for(String var : selectUris){
      query += " ?" + var;
    }
    
    for(String var : selectLiterals){
      query += " ?" + var;
    }
    query += "\nWHERE { \n ";
    
    for(String triple : queryTriples){
      query += triple;
    }
    query += " } ";

    
    //Substitution of the variables to the query - LATER
    return new SPARQLDataGetter(vreq, query, selectUris, selectLiterals);
  }
}
