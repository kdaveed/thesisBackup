package rdfbones.lib;

import java.util.List;

public class ArrayLib {

  public static void addDistinct(List<String> list, String object) {
    if(!list.contains(object)){
      list.add(object);
    }
  }
}
