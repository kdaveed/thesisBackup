package main;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import rdfbones.formconfig.GraphProcessor;
import rdfbones.rdfdataset.FormData;
import rdfbones.rdfdataset.Graph;
import rdfbones.rdfdataset.InputNode;
import rdfbones.rdfdataset.LiteralTriple;
import rdfbones.rdfdataset.MultiTriple;
import rdfbones.rdfdataset.RestrictionTriple;
import rdfbones.rdfdataset.SelectNode;
import rdfbones.rdfdataset.Triple;
import vivoclasses.VitroRequest;

public class Main {

  public static void main(String[] args) throws JSONException {

    // TODO Auto-generated method stub
    Graph mainGraph = GraphProcessor.getGraph(getTriples(), getRestrictionTriples(), "subject");
    mainGraph.debug(0);
    //Test interface 
    VitroRequest vreq = new VitroRequest();
    mainGraph.init(vreq, getFormData());
  }
  
  static List<Triple> getTriples(){
    
    List<Triple> triple = new ArrayList<Triple>();
    triple.add(new MultiTriple(new InputNode("subject"), "hasPart", "studyDesingExecution"));
    triple.add(new MultiTriple("studyDesingExecution", "hasPart", "specimenCollectionProcess"));
    triple.add(new MultiTriple("specimenCollectionProcess", "hasSpecInput", new SelectNode("boneSegment")));
    triple.add(new Triple("specimenCollectionProcess", "hasSpecOutput", "specimen"));
    triple.add(new Triple(new SelectNode("assay"), "hasSpecInput", "specimen"));
    triple.add(new MultiTriple("assay", "hasSpecOutput", "measurementDatum"));
    triple.add(new Triple("measurementDatum", "hasCategoricalLabel", "categoricalLabel"));
    return triple;
  }
  
  static List<Triple> getRestrictionTriples(){
    
    List<Triple> triple = new ArrayList<Triple>();
    triple.add(new Triple("subject", "rdf:type", "subjectType"));
    triple.add(new RestrictionTriple("subjectType", "obo:BFO_0000051", "studyDesignExecutionType"));
    triple.add(new Triple("studyDesingExecution", "rdf:type", "studyDesignExecutionType"));
    triple.add(new Triple("studyDesignExecutionType", "rdfs:subClassOf", "obo:OBI_0000471"));

    //Now I omit the form related restriction
    
    triple.add(new Triple("specimenCollectionProcess", "rdf:type", "specimenCollectionProcessType"));
    triple.add(new Triple("assay", "rdf:type", "assayType"));
    triple.add(new Triple("specimen", "rdf:type", "specimenType"));
    
    triple.add(new Triple("specimenCollectionProcessType", "rdfs:subClassOf", "obo:OBI_0000659"));
    triple.add(new Triple("assayType", "rdfs:subClassOf", "obo:OBI_0100051"));
    
    triple.add(new RestrictionTriple("assayType", "obo:OBI_0000293", "specimenType"));
    triple.add(new RestrictionTriple("specimenCollectionProcessType", "obo:BFO_0000051", "specimenType"));

    return triple;
  }
  
  static FormData getFormData(){
    
    FormData formData = new FormData();
    FormData assayType = new FormData("assayType");
    FormData boneSegment = new FormData("boneSegment");
    FormData measurementDatum = new FormData("measurementDatum");
    measurementDatum.setInputs("categoricalLabel");
    assayType.addSubformData("boneSegment", boneSegment);
    assayType.addSubformData("measurement", measurementDatum);
    formData.addSubformData("assayType", assayType);
    return formData;
  } 
}
