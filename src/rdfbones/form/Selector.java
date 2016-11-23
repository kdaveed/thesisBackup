package rdfbones.form;

public class Selector extends FormElement{
  
  public Form subForm;
  public Selector(String name){
    super(name);
    this.type = new String("selector");
  }
}
