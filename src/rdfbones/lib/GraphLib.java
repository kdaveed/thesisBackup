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
      ArrayLib.addDistinct(nodes, triple.subject.varName);
      ArrayLib.addDistinct(nodes, triple.object.varName);
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
  
  public static List<String> getObjectNodes(List<Triple> triples){
   
    List<String> object = new ArrayList<String>();
    for(Triple triple : triples){
      object.add(triple.object.varName);
    }
    return object;
  }
  
  public static List<Triple> getAndRemoveTypeTriples(List<Triple> triples, List<String> nodes){
    
    List<Triple> toReturn = new ArrayList<Triple>();
    List<Integer> nums = getTypeNums(triples, nodes);
    set(toReturn, triples, nums);
    remove(triples, nums);
    return toReturn;
  }
  
  public static List<Triple> getAndRemoveSubClassTriples(List<Triple> triples, List<String> nodes){
    
    List<Triple> toReturn = new ArrayList<Triple>();
    List<Integer> nums = getSubClassNums(triples, nodes);
    set(toReturn, triples, getTypeNums(triples, nodes));
    remove(triples, nums);
    return toReturn;
  }
  
  public static void set(List<Triple> triplesToSet, List<Triple> triples, List<Integer> integers){
    
    for(Integer i : integers){
      triplesToSet.add(triples.get(i));
    }
  }
  
  public static void remove(List<Triple> triples, List<Integer> integers){
    
    for(int j = integers.size(); j > 0; j--){
      triples.remove(integers.get(j-1).intValue());
    }
  }
 
  public static List<Integer> getTypeNums(List<Triple> triples, List<String> nodes){
 
    return getTripleNums(triples, nodes, "rdf:type");
  }
  
  public static List<Integer> getSubClassNums(List<Triple> triples, List<String> nodes){
    
    return getTripleNums(triples, nodes, "rdfs:subClassOf");
  }
  
  public static List<Integer> getTripleNums(List<Triple> triples, List<String> nodes, String predicate){
  
    List<Integer> typeNums = new ArrayList<Integer>();
    Integer i = 0;
    for(Triple triple : triples){
      if(nodes.contains(triple.subject.varName) && triple.predicate.equals(predicate)){
        typeNums.add(i);
      }
      i++;
    }
    
    
    return typeNums;
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
  
  public static List<Triple> getRestrictionTriples(List<String> nodes, List<Triple> triples){
    
    List<Triple> addTo = new ArrayList<Triple>();
    for(Triple triple : triples){
      if(nodes.contains(triple.subject.varName) && nodes.contains(triple.object.varName)){
        addTo.add(triple);
      }
    }
    return addTo;
  }
  
  public static List<Triple> getAndRemoveRestrictionTriples(List<String> nodes, List<Triple> triples){
    
    List<Integer> nums = new ArrayList<Integer>();
    Integer i = 0;
    for(Triple triple : triples){
      if(nodes.contains(triple.subject.varName) && nodes.contains(triple.object.varName)){
         nums.add(i);
      }
      i++;
    }
    List<Triple> toReturn = new ArrayList<Triple>();
    set(toReturn, triples, nums);
    remove(triples, nums);
    return toReturn;
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
  
  public static List<String> getClassNodes(List<Triple> restrictionTriples){
    
    List<String> classNodes = new ArrayList<String>();
    for(Triple triple : restrictionTriples){
      if(triple instanceof RestrictionTriple){
        ArrayLib.addDistinct(classNodes, triple.subject.varName);
        ArrayLib.addDistinct(classNodes, triple.object.varName);
      }
    }
    return classNodes;
  }
  
}