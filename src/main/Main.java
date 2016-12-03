package main;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import rdfbones.lib.JSON;
import rdfbones.lib.TripleLib;
import rdfbones.lib.VariableDependency;
import rdfbones.rdfdataset.Graph;
import webappconnector.PlainJavaWebappConnector;

public class Main {

  public static void main(String[] args) throws JSONException {

    /*
    FormConfiguration formConfig = GraphLib.getFormConfig(TripleLib.csrDataTriples(), 
        TripleLib.csrSchemeTriples(), TripleLib.csrForm(), new PlainJavaWebappConnector(true));
    
    FormConfiguration formConfigSDE = GraphLib.getFormConfig(TripleLib.sdeDataTiples(), 
        TripleLib.sdeSchemeTriples(), TripleLib.sdeForm(), new PlainJavaWebappConnector(true));

    */
    //formConfigSDE.dataGraph.dependencyDebug();
    
    Graph graph = new Graph("object", TripleLib.csrDataTriples(), 
        TripleLib.csrSchemeTriples());
    graph.init(new PlainJavaWebappConnector(true));
    graph.debug();
    
    //RDFGraph graph2 = new RDFGraph("object", TripleLib.sdeDataTiples());
    //graph2.debug();
    //graph2.debugMulti(0);

    //formConfig.dataGraph.debug();
    //System.out.println("FormConfig :" + formConfig.form.getJSON());

    //formConfig.dataGraph.dependencyDebug();
  }
  
  static void dependencyDebug(Graph graph){
   
    JSONObject request = JSON.obj(json5());
    JSONObject response = JSON.obj();
    Iterator<?> keys = request.keys();
    while(keys.hasNext()){
      String key = (String) keys.next();
      JSONObject inputVars = JSON.object(request, key);
      VariableDependency dependency = graph.variableDependencies.get(key);
      JSON.put(response, key, dependency.getData(inputVars));
    }
    System.out.println(JSON.debug(response, 0));
  }
  
  static String json1(){
    String data = new String("");
    data += "{  assayType : { subject :  'subjectUri' }} ";
    return data;
  }
  
  static String json2(){
    String data = new String("");
    data += "{  boneOrgan : { assayType :  'assayType1' },"
          + "   measurementDatumType : { assayType : 'assayType1' }} ";
    return data;
  }
  
  static String json3(){
    String data = new String("");
    data += "{  categoricalLabel : { measurementDatumType :  'measurementDatumType1' }},";
    return data;
  }
  
  static String json4(){
    String data = new String("");
    data += "{  skeletalRegionType : { subject :  'subjectUri1', rangeUri : 'rangeUri1' }} ";
    return data;
  }
  
  static String json5(){
    String data = new String("");
    data += "{  boneOrganType : { subject :  'http://vivo.mydomain.edu/individual/n4705', "
        + "skeletalRegionType : 'http://purl.obolibrary.org/obo/FMA_53672' }} ";
    return data;
  }
  
  static String json6(){
    String data = new String("");
    data += "{  boneSegmentType : { subject :  'http://vivo.mydomain.edu/individual/n4705', "
        + "boneOrganType : 'http://purl.obolibrary.org/obo/FMA_53672' }} ";
    return data;
  }
  
  //mainGraph.getExistingData("subject11", "object11");
  //mainGraph.getExistsingData("subject11", "object11");
  //System.out.println(JSON.debug(mainGraph.existingData));
  //System.out.println(JSON.debug(mainGraph.dependencyDescriptor(), 0));
  //System.out.println(JSON.debug(TripleLib.sdeForm().getJSON(), 0));
  //System.out.println(JSON.debug()
  //dependencyDebug(mainGraph);

}