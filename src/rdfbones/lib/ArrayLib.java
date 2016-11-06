package rdfbones.lib;

import java.util.List;

public class ArrayLib {

  public static void addDistinct(List<String> list, String object) {
    if(!list.contains(object)){
      list.add(object);
    }
  }
  
  public static String debugList(List<String> list){
    
    String arr = new String();
    for(String str : list){
      arr += str + " ";
    }
    return arr;
  }
}
