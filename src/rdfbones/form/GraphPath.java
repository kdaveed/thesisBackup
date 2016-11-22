package rdfbones.form;

import java.util.ArrayList;
import java.util.List;

import rdfbones.lib.ArrayLib;
import rdfbones.lib.GraphLib;
import rdfbones.rdfdataset.RDFNode;
import rdfbones.rdfdataset.Triple;

public class GraphPath {

  public List<Triple> triples = new ArrayList<Triple>();
  public List<String> inputs = new ArrayList<String>();
  public boolean valid = false;
  public String input;
  public List<GraphPath> subPaths = new ArrayList<GraphPath>();
  public GraphPath(){
   
  }
  
  public GraphPath(Triple triple){
    triples.add(triple);
  }
  
  public List<Triple> getTriples(){
    for(GraphPath path : this.subPaths){
      this.triples.addAll(path.triples);
    }
    return triples;
  }
  
  
  public void setInputs(List<String> inputs){
    this.inputs = inputs;
    for(GraphPath path : this.subPaths){
      path.setInputs(inputs);
    }
  }
  
  public String debugValid(){
    
    String debug = new String("");
    debug += "Input : " + this.inputs + "\n";
    for(Triple triple : this.triples){
      debug += triple.subject.varName + "   " + triple.object.varName + " .  \n ";
    }
    return debug;
  }
  
  public String debug(){
    
    String debug = new String("");
    debug += "Input : " + this.inputs + "\n";
    for(Triple triple : this.triples){
      debug += triple.subject.varName + "   " + triple.object.varName + " .  \n ";
    }
    for(GraphPath path : this.subPaths){
        debug += "Subpath { "  + path.debug() + " }  \n";  
    }
    return debug;
  }
  
  public boolean validate(List<String> inputs, List<Triple> triples){
    
    List<String> nodes = GraphLib.getNodes(this.triples);
    //System.out.println("Nodes : " + ArrayLib.debugList(nodes));
    for(String node : nodes){
      List<Triple> otherTriples = GraphLib.getNotRestrictionTriples(triples, node);
      for(Triple triple : otherTriples){
        if(inputs.contains(GraphLib.getObject(triple, node))){
          //System.out.println("Found : " + GraphLib.getObject(triple, node));
          this.valid = true;
          this.inputs.add(GraphLib.getObject(triple, node));
          this.triples.add(triple);
        }
      }
    }
    //Check if the subgraph is valid or not
    for(GraphPath path : this.subPaths){
      if(path.validate(inputs, triples)){
        this.inputs.addAll(path.inputs);
        this.triples.addAll(path.triples);
        this.valid = true;
      } 
    }
    return this.valid;
  }
}


