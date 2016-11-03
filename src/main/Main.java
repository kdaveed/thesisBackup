package main;

import java.util.ArrayList;
import java.util.List;

import rdfbones.formconfig.GraphProcessor;
import rdfbones.rdfdataset.InputNode;
import rdfbones.rdfdataset.LiteralTriple;
import rdfbones.rdfdataset.MultiTriple;
import rdfbones.rdfdataset.SelectNode;
import rdfbones.rdfdataset.Triple;

public class Main {

  public static void main(String[] args) {
    // TODO Auto-generated method stub
    List<Triple> graphTriples = new ArrayList<Triple>();
    GraphProcessor.getSubGraphs(getTriples(), "subjectUri").get(0).debug(0);
  }
  
  static List<Triple> getTriples(){
    
    List<Triple> triple = new ArrayList<Triple>();
    triple.add(new MultiTriple(new InputNode("subjectUri"), "hasPart", "studyDesingExecution"));
    triple.add(new Triple("studyDesingExecution", "hasPart", "specimenCollectionProcess"));
    triple.add(new MultiTriple("specimenCollectionProcess", "hasSpecInput", new SelectNode("boneSegment")));
    triple.add(new Triple("specimenCollectionProcess", "hasSpecOutput", "specimen"));
    triple.add(new Triple(new SelectNode("assay"), "hasSpecInput", "specimen"));
    triple.add(new LiteralTriple(new SelectNode("boneSegment"), "boneSegmentLabel", new SelectNode("label")));
    return triple;
  }

}
