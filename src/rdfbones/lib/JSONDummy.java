package rdfbones.lib;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONDummy {

  
  public JSONObject getDummy1() throws JSONException{
    
    //Bone Segments
    JSONObject boneSegment1 = obj("boneSegment1");
    JSONObject boneSegment2 = obj("boneSegment2");

    JSONArray boneSegmentArray = arr();
    boneSegmentArray.put(boneSegment1);
    boneSegmentArray.put(boneSegment2);
    
    //Categorical Label
    JSONObject catLab1 = obj("catLab1");
    JSONObject catLab2 = obj("catLab2");
    
    //Measurement Datum
    JSONObject measDatum1 = obj("measDatum1");
    JSONObject measDatum2 = obj("measDatum2");
    
    measDatum1.put("categoricalLabel", catLab1);
    measDatum2.put("categoricalLabel", catLab2);
    
    JSONArray meausurementDatumArray = arr();
    meausurementDatumArray.put(measDatum1);
    meausurementDatumArray.put(measDatum2);

    JSONObject assayType1 = obj("assayType1");
    assayType1.put("boneSegment", boneSegmentArray);
    assayType1.put("meausurementDatum", meausurementDatumArray);
    
    JSONArray assayTypeArray = arr();
    assayTypeArray.put(assayType1);
    JSONObject data = obj("subjectUri");
    
    data.put("assayType", assayTypeArray);
    return data;
  }
  
  public JSONObject obj(){
    return new JSONObject();
  }  
  
  public JSONArray arr(){
    return new JSONArray();
  }
  
  JSONObject obj(String varName) throws JSONException{
    return obj().put("uri", varName);
  }
  
}
