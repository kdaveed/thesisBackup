package rdfbones.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Form {

  public List<FormElement> formElements = new ArrayList<FormElement>();
  public Form(List<FormElement> formElements){
    this.formElements = formElements;
  }
  
  public Form(){
    
  }
}