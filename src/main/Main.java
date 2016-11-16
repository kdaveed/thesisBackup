package main;

import org.json.JSONException;
import rdfbones.formProcessing.GraphProcessor;
import rdfbones.lib.TripleLib;
import rdfbones.rdfdataset.Graph;
import webappconnector.PlainJavaWebappConnector;

public class Main {

  public static void main(String[] args) throws JSONException {

    Graph mainGraph =
        GraphProcessor.getGraph(TripleLib.sdeDataTiples(), TripleLib.sdeSchemeTriples(),
            "subject");
    mainGraph.init(new PlainJavaWebappConnector(true));
    //String triplesToCreate = mainGraph.saveInitialData(JSON.getDummy1());
    //System.out.println(triplesToCreate);
    //mainGraph.debug(0);
    mainGraph.getExistingData("subjectURI", "objectURI");
  }
}