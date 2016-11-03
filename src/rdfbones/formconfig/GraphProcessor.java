package rdfbones.formconfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rdfbones.rdfdataset.Triple;
import rdfbones.rdfdataset.MultiTriple;
import rdfbones.rdfdataset.Graph;


public class GraphProcessor {

  public static List<Graph> getSubGraphs(List<Triple> triples, String startNode){

    //CheckSubgraph
    int num = 0;
    int index = 0;
    int tripleNum = 0;
    int[] multiTriples = new int[triples.size()];
    boolean valid = true;
    //Checking how many multitriples are coming out
    for(Triple triple : triples){
        if(triple.subject.varName.equals(startNode)
            || triple.object.varName.equals(startNode)){
        if(triple instanceof MultiTriple){
          //We are here only once
          multiTriples[num] = index;
          num++;
        } else { //If we are here then the condition is not fulfilled
          valid = false;
          break;
        }
      }
      index++;
    }
    List<Graph> graph = new ArrayList<Graph>();
    if(valid && multiTriples.length > 0){
      for(int i = 0; i < num; i++){
        List<Triple> graphTriples = new ArrayList<Triple>();
        Triple multiTriple = triples.get(multiTriples[i]);
        graphTriples.add(multiTriple);
        triples.remove(multiTriples[i]);
        graph.add(getSubGraph(triples, getObject(multiTriple, startNode), graphTriples));  
      }
    } else {
      
      List<Triple> graphTriples = new ArrayList<Triple>();
      graph.add(getSubGraph(triples, startNode, graphTriples));
    }
    return graph;
  }
  
  public static Graph getSubGraph(List<Triple> triples, String startNode, List<Triple> graphTriples){
    
    Graph graph = new Graph();
    if(graphTriples.size() > 0){
      //This is the multitriple
      graph.multiTriple = graphTriples.get(0);
    }
    Map<String, String> subGraphNodes = new HashMap<String, String>();
    List<String> graphNodes = new ArrayList<String>();
    graphNodes.add(startNode);
    int i = 0;
    boolean flag = false;
    while(true){
      List<Integer> tripleNums = new ArrayList<Integer>();
      for(Triple triple : triples){
        flag = false;
        if(triple.subject.varName.equals(startNode) || triple.object.varName.equals(startNode)){
          String object = getObject(triple, startNode);
          if(triple instanceof MultiTriple){
             subGraphNodes.put(object, getSubject(triple, startNode));
          } else {
             graphTriples.add(triple);
             graphNodes.add(object);
             tripleNums.add(i);
          }
        }
        i++;
      }
      i = 0;
      //Removing the found triples from graph
      for(int j = tripleNums.size(); j > 0; j--){
        triples.remove(tripleNums.get(j-1).intValue());
      }
      //Remove the used startNode
      if(graphNodes.size() == 0){
        break;
      } else {
        //There are node to discover
        startNode = graphNodes.get(0);
        graphNodes.remove(0);
      }
    }
    //Set the found triple to the graph
    graph.triples = graphTriples;
    for(String subGraphNode : subGraphNodes.keySet()){
      String node = subGraphNodes.get(subGraphNode);
      graph.subGraphs.put(subGraphNode, getSubGraphs(triples, node));
    }
    return graph;
  }
  
  public static String getSubject(Triple triple, String varName){
    
    if(triple.subject.varName.equals(varName)){
      return triple.subject.varName;
    } else {
      return triple.object.varName;
    }
  }
  
  public static String getObject(Triple triple, String varName){
    
    if(triple.subject.varName.equals(varName)){
      return triple.object.varName;
    } else {
      return triple.subject.varName;
    }
  }
  
}

