package rdfbones.lib;

import java.util.ArrayList;
import java.util.List;

import rdfbones.form.Form;
import rdfbones.form.FormElement;
import rdfbones.form.Selector;
import rdfbones.form.SubformAdder;
import rdfbones.rdfdataset.Constant;
import rdfbones.rdfdataset.InputNode;
import rdfbones.rdfdataset.MultiTriple;
import rdfbones.rdfdataset.RDFNode;
import rdfbones.rdfdataset.RestrictionTriple;
import rdfbones.rdfdataset.Triple;

public class TripleLib {

  public static List<Triple> sdeDataTiples() {

    List<Triple> triple = new ArrayList<Triple>();
    triple.add(new Triple(new InputNode("subject"), "obo:BFO_0000051",
        "object"));
    triple.add(new MultiTriple("object", "obo:BFO_0000051",
        "specimenCollectionProcess"));
    triple.add(new MultiTriple("specimenCollectionProcess", "obo:OBI_0000293",
        new InputNode("boneSegment")));
    triple.add(new Triple("specimenCollectionProcess", "obo:OBI_0000299", "specimen"));
    triple.add(new Triple("assay", "obo:OBI_0000293", "specimen"));
    triple.add(new MultiTriple("assay", "obo:OBI_0000299", "measurementDatum"));
    triple.add(new Triple("measurementDatum", "obo:IAO_0000299", new InputNode(
        "categoricalLabel")));
    return triple;
  }
  
  public static List<Triple> sdeSchemeTriples() {

    List<Triple> triple = new ArrayList<Triple>();
    triple.add(new Triple(new InputNode("subject"), "rdf:type", "subjectType"));
    triple.add(new RestrictionTriple("subjectType", "obo:BFO_0000051",
        "studyDesignExecutionType"));
    triple.add(new RestrictionTriple("studyDesignExecutionType","obo:BFO_0000051", 
        new InputNode("assayType")));
    triple
        .add(new Triple("object", "rdf:type", "studyDesignExecutionType"));
    triple.add(new Triple("studyDesignExecutionType", "rdfs:subClassOf",
        new Constant("obo:OBI_0000471")));
    triple.add(new Triple("specimenCollectionProcess", "rdf:type",
        "specimenCollectionProcessType"));
    triple.add(new Triple("assay", "rdf:type", new InputNode("assayType")));
    triple.add(new Triple("specimen", "rdf:type", "specimenType"));
    triple.add(new Triple("specimenCollectionProcessType", "rdfs:subClassOf",
        new Constant("obo:OBI_0000659")));
    triple.add(new Triple("assayType", "rdfs:subClassOf", new Constant("obo:OBI_0000070")));
    triple.add(new Triple("specimenType", "rdfs:subClassOf", new Constant("obo:OBI_0100051")));
    triple.add(new RestrictionTriple("assayType", "obo:OBI_0000299", "measurementDatumType"));
    triple.add(new Triple("measurementDatum", "rdf:type", new InputNode(
        "measurementDatumType")));
    triple.add(new RestrictionTriple(new InputNode("assayType"), "obo:OBI_0000293",
        "specimenType"));
    
    //Just test
    triple.add(new RestrictionTriple(new InputNode("specimenType"), "obo:OBI_0000293",
        "testType"));
    triple.add(new RestrictionTriple("specimenCollectionProcessType", "obo:OBI_0000299",
        "specimenType"));
    return triple;
  }

  
  public static Form sdeForm(){
    
    Form measDatumSubForm = new Form();
    FormElement categoricalLabel = new Selector("categoricalLabel");
    measDatumSubForm.formElements.add(categoricalLabel);

    SubformAdder measurementDatum = new SubformAdder("measurementDatum");
    measurementDatum.subForm = measDatumSubForm;
    
    FormElement boneSegment = new SubformAdder("boneSegment");
    
    Form subForm = new Form();
    subForm.formElements.add(boneSegment);
    subForm.formElements.add(measurementDatum);
    
    SubformAdder assayType = new SubformAdder("assayType");
    assayType.subForm = subForm;
    Form mainForm = new Form();
    mainForm.formElements.add(assayType);
    
    return mainForm;
  }
}
