package rdfbones.lib;

import java.util.ArrayList;
import java.util.List;

import rdfbones.rdfdataset.*;

public class GraphLib {

  public static List<String> getNodes(List<Triple> dataTriples, List<Triple> restrictionTriples){
    
    List<String> nodes = new ArrayList<String>();
    nodes.addAll(getNodes(dataTriples));
    nodes.addAll(getNodes(restrictionTriples));
    return nodes;
  }
  
  public static List<String> getNodes(List<Triple> triples){
    
    List<String> nodes = new ArrayList<String>();
    for(Triple triple : triples){
      if(! nodes.contains(triple.subject.varName)){
        nodes.add(triple.subject.varName);
      }
      if(! nodes.contains(triple.object.varName)){
        nodes.add(triple.object.varName);
      }
    }
    return nodes; 
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
  
  public static List<Triple> getTypeTriples(List<Triple> triples, List<String> nodes){
      return getTriples(triples, nodes, "rdf:type");
  }

  public static List<Triple> getSubClassTriples(List<Triple> triples, List<String> nodes){
      return getTriples(triples, nodes, "rdf:subClassOf");
  }
  
  public static List<Triple> getTriples(List<Triple> triples, List<String> nodes, String predicate){
  
    List<Triple> typeTriples = new ArrayList<Triple>();
    for(String node : nodes){
      typeTriples.add(getTriple(triples, node, predicate));
    }
    return typeTriples;
  }
  
  public static Triple getTriple(List<Triple> triples, String node, String predicate){
    
    for(Triple triple : triples){
      if(triple.subject.varName.equals(node) && triple.predicate.equals(predicate)){
        return triple;
      }
    }
    return null;
  }
  
  public static List<Triple> getRestrictionTriple(List<String> nodes, List<Triple> triples){
    
    List<Triple> addTo = new ArrayList<Triple>();
    for(Triple triple : triples){
      if(nodes.contains(triple.subject.varName) && nodes.contains(triple.object.varName)){
        addTo.add(triple);
      }
    }
    return addTo;
  }
  
  public static List<String> getNewInstanceNodes(List<Triple> triples){
    
    List<String> newInstances = new ArrayList<String>();
    for(Triple triple : triples){
      if(triple.subject instanceof NewInstance){
        ArrayLib.addDistinct(newInstances, triple.subject.varName);
      }
      if(triple.object instanceof NewInstance){
        ArrayLib.addDistinct(newInstances, triple.object.varName);
      }
    }
    return newInstances;
  }
  
  public static List<String> getResourceNodes(List<Triple> triples){

    List<String> uris = new ArrayList<String>();
    for(Triple triple : triples){
      if(!(triple instanceof LiteralTriple)){
        ArrayLib.addDistinct(uris, triple.subject.varName);
        ArrayLib.addDistinct(uris, triple.object.varName);
      }
    }
    return uris;
  }
  
  public static List<String> getLiteralNodes(List<Triple> triples){

    List<String> literals = new ArrayList<String>();
    for(Triple triple : triples){
      if(!(triple instanceof LiteralTriple)){
        ArrayLib.addDistinct(literals, triple.object.varName);
      }
    }
    return literals;
  }
  
  public static List<String> getInputNodes(List<Triple> triples){

    List<String> inputNodes = new ArrayList<String>();
    for(Triple triple : triples){
      if(triple.subject instanceof InputNode){
        ArrayLib.addDistinct(inputNodes, triple.object.varName);
      }
      if(triple.object instanceof InputNode){
        ArrayLib.addDistinct(inputNodes, triple.object.varName);
      }
    }
    return inputNodes;
  }  
}