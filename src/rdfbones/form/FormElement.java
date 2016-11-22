package rdfbones.form;

import rdfbones.rdfdataset.RDFNode;

public class FormElement {

  public RDFNode node;
  String type;
  
  FormElement(){
    //TODO
  }
  
  public FormElement(String node){
    this.node = new RDFNode(node);
  }
  
  FormElement(RDFNode node){
    this.node = node;
  }
  
  FormElement(RDFNode node, String type){
    this.node = node;
    this.type = type;
  }
}
