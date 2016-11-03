package rdfbones.rdfdataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rdfbones.formconfig.Form;



public class DataSet {

  public String name;
  
  public List<String> data1;
  public List<Map<String, String>> data;
  
  public String getName(){
    return name;
  }
  
  public List<Map<String, String>> getData(){
    return this.data;
  }

  public void setData(Form form){
    //Do nothing
  }
}
