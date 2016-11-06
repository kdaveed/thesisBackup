package rdfbones.rdfdataset;

import java.util.ArrayList;
import java.util.List;

import edu.cornell.mannlib.vitro.webapp.n3editing.formConfigurationStatic.BonyDataSet2;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import rdfbones.rdfdataset.Triple;

public class Triples {

  public List<Triple> triples = new ArrayList<Triple>();
  
  public String getTriples(){
     String t = new String("");
     for(Triple triple : this.triples){
       t += triple.getTriple();
     }
     return t;
  }
}
