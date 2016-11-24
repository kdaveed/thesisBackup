package main;

import org.json.JSONException;

import rdfbones.formProcessing.DependencyCalculator;
import rdfbones.formProcessing.GraphProcessor;
import rdfbones.graphData.QueryStructure;
import rdfbones.lib.ArrayLib;
import rdfbones.lib.SPARQLDataGetter;
import rdfbones.lib.TripleLib;
import rdfbones.rdfdataset.Graph;
import webappconnector.PlainJavaWebappConnector;

public class Main {

  public static void main(String[] args) throws JSONException {

    /*
    Graph mainGraph =
        GraphProcessor.getGraph(TripleLib.sdeDataTiples(), TripleLib.sdeSchemeTriples(),
            "subject");
    mainGraph.init(new PlainJavaWebappConnector(true));
    //String triplesToCreate = mainGraph.saveInitialData(JSON.getDummy1());
    //System.out.println(triplesToCreate);
    //mainGraph.debug(0);
    //mainGraph.getExistingData("subjectURI", "objectURI");
    DependencyCalculator.calculate(mainGraph, TripleLib.sdeSchemeTriples(), TripleLib.sdeForm());
    //Debug dependencies
    for(String dependencyKey : mainGraph.variableDependencies.keySet()){
      System.out.println(dependencyKey);
      SPARQLDataGetter dataGetter = mainGraph.variableDependencies.get(dependencyKey).dataGetter;
      System.out.println(dataGetter.getQuery());
    }
    */
    QueryStructure qs = new QueryStructure(TripleLib.greedy2(), "a1");
    System.out.println(qs.getQuery());
   
  }
}