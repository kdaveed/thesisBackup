package main;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import edu.cornell.mannlib.vitro.webapp.dao.NewURIMakerVitro;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.NewURIMaker;
import rdfbones.formProcessing.GraphProcessor;
import rdfbones.lib.JSON;
import rdfbones.lib.TripleSet;
import rdfbones.rdfdataset.FormData;
import rdfbones.rdfdataset.Graph;
import rdfbones.rdfdataset.InputNode;
import rdfbones.rdfdataset.MultiTriple;
import rdfbones.rdfdataset.RestrictionTriple;
import rdfbones.rdfdataset.ExistingInstance;
import rdfbones.rdfdataset.Triple;
import vivoclasses.VitroRequest;

public class Main {

  public static void main(String[] args) throws JSONException {

    Graph mainGraph = GraphProcessor.getGraph(getTriples(), getSchemeTriples(), "subject");
    mainGraph.inputNode = "subject";
    VitroRequest vreq = new VitroRequest();
    //mainGraph.debug(0);
    NewURIMaker newUri = new NewURIMakerVitro(vreq.getWebappDaoFactory());
    mainGraph.init(vreq, newUri);

    String triplesToCreate = mainGraph.saveData(JSON.getDummy1(), vreq, "subject", "subject5534321");
    //System.out.println(triplesToCreate);
    TripleSet triples = new TripleSet(triplesToCreate);
    triples.javaDebug();
  }

  static List<Triple> getTriples(){
    
    List<Triple> triple = new ArrayList<Triple>();
    triple.add(new Triple(new InputNode("subject"), "obo:BFO_0000051", "studyDesingExecution"));
    triple.add(new MultiTriple("studyDesingExecution", "obo:BFO_0000051", "specimenCollectionProcess"));
    triple.add(new MultiTriple("specimenCollectionProcess", "obo:OBI_0000293", new InputNode("boneSegment")));
    triple.add(new Triple("specimenCollectionProcess", "obo:OBI_0000299", "specimen"));
    triple.add(new Triple("assay", "obo:OBI_0000293", "specimen"));
    triple.add(new MultiTriple("assay", "obo:OBI_0000299", "measurementDatum"));
    triple.add(new Triple("measurementDatum", "obo:IAO_0000299", new InputNode("categoricalLabel")));
    return triple;
  }
  
  static List<Triple> getSchemeTriples(){
    
    List<Triple> triple = new ArrayList<Triple>();
    triple.add(new Triple(new InputNode("subject"), "rdf:type", "subjectType"));
    triple.add(new RestrictionTriple("subjectType", "obo:BFO_0000051", "studyDesignExecutionType"));
    triple.add(new Triple("studyDesingExecution", "rdf:type", "studyDesignExecutionType"));
    triple.add(new Triple("studyDesignExecutionType", "rdfs:subClassOf", "obo:OBI_0000471"));
    triple.add(new Triple("specimenCollectionProcess", "rdf:type", "specimenCollectionProcessType"));
    triple.add(new Triple("assay", "rdf:type", new InputNode("assayType")));
    triple.add(new Triple("specimen", "rdf:type", "specimenType"));
    triple.add(new Triple("specimenCollectionProcessType", "rdfs:subClassOf", "obo:OBI_0000659"));
    triple.add(new Triple("specimenType", "rdfs:subClassOf", "obo:OBI_0100051"));
    triple.add(new Triple("measurementDatum", "rdf:type", new InputNode("measurementDatumType")));
    triple.add(new RestrictionTriple(new InputNode("assayType"), "obo:OBI_0000293", "specimenType"));
    triple.add(new RestrictionTriple("specimenCollectionProcessType", "obo:BFO_0000051", "specimenType"));
    return triple;
  }

}