package rdfbones.form;

public class ExistingInstanceSelector extends FormElement {

  public ExistingInstanceSelector(String varName){
    super(varName);
    this.type = new String("existingInstanceSelector");
  }
}
