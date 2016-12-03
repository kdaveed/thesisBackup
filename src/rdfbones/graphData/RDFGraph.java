package rdfbones.graphData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rdfbones.lib.ArrayLib;
import rdfbones.lib.GraphLib;
import rdfbones.rdfdataset.MultiTriple;
import rdfbones.rdfdataset.RDFNode;
import rdfbones.rdfdataset.Triple;

public class RDFGraph {

  public Triple triple = null;
  public List<Triple> dataTriples = new ArrayList<Triple>();
  List<Triple> schemeTriples = new ArrayList<Triple>();

  String inputNode;
  public Map<RDFNode, RDFGraph> map;
  public Map<String, RDFGraph> subGraphs = new HashMap<String, RDFGraph>();
  
  public RDFGraph(Triple triple, String node, List<Triple> triples){
    
    this.triple = triple;
    this.init(GraphLib.getObject(triple, node), triples);
  }
  
  public RDFGraph(String node, List<Triple> triples){
    
    this.init(node, triples);
    this.initGraph();
  }
 
  public void init(String nodeName, List<Triple> triples){
    
    this.map = new HashMap<RDFNode, RDFGraph>();
    List<Triple> neighbours = GraphLib.getAndRemoveTriples(triples, nodeName);
    for(Triple triple : neighbours){
      RDFNode node = GraphLib.getObjectNode(triple, nodeName);
      map.put(node, new RDFGraph(triple, nodeName, triples));
    }
  }
  
  public void initGraph(){
    this.initGraphMap(this);
  }
  
  public void initGraphMap(RDFGraph graph){
    
    for(RDFNode key : this.map.keySet()){
      RDFGraph subGraph = this.map.get(key);
      Triple triple = subGraph.triple;
      if(triple instanceof MultiTriple){
        subGraph.inputNode = GraphLib.getObject(triple, key.varName);
        subGraph.initGraph();
        if(subGraph.triple != null) subGraph.dataTriples.add(triple);
        this.subGraphs.put(triple.predicate, subGraph);
      } else {
        graph.dataTriples.add(triple);
        subGraph.initGraphMap(graph);
      }
    }
  }
  
  public void initSchemeTriples(List<Triple> schemeTriples1){
    
    this.schemeTriples = GraphLib.getSchemeTriples(this.dataTriples, schemeTriples1);  
    for(String key : this.subGraphs.keySet()){
      this.subGraphs.get(key).initSchemeTriples(schemeTriples1);
    }
  }
  
  public void debug(){
     this.debug(0);
  }
  
  public void debug(int n){
    String tab = new String(new char[n]).replace("\0", "\t");
    if(this.triple != null)
      System.out.println(tab + this.triple.subject.varName + "\t"  + 
           this.triple.predicate + "\t" + this.triple.object.varName);  
    n++;
    for(RDFNode key : this.map.keySet()){
      System.out.println(tab + "\t" + key.varName);
      this.map.get(key).debug(n);
    }
  }
  
  public void debugMulti(int n){
    String tab = new String(new char[n]).replace("\0", "\t");
    n++;
    if(this.inputNode != null){
      System.out.println(tab + "InputNode : " + this.inputNode);
    }
    System.out.println(tab + "Triple");
    if(this.triple != null)
      System.out.println(tab + this.triple.subject.varName + "\t"  + 
           this.triple.predicate + "\t" + this.triple.object.varName);  
    System.out.println(tab + "Triples");
    for(Triple triple : this.dataTriples){
      System.out.println(tab + triple.subject.varName + "\t"  
         + triple.predicate + "\t" + triple.object.varName); 
    }
    System.out.println(tab + "SchemeTriples : "
        + ArrayLib.debugTriples(tab, this.schemeTriples));
    System.out.println(tab + "Subgraphs");
    for(String key : this.subGraphs.keySet()){
      System.out.println(tab + key);
      this.subGraphs.get(key).debugMulti(n);
    }
  }
}
