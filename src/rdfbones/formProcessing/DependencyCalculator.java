package rdfbones.formProcessing;

import java.util.ArrayList;
import java.util.List;

import rdfbones.form.Form;
import rdfbones.form.FormElement;
import rdfbones.form.GraphPath;
import rdfbones.form.SubformAdder;
import rdfbones.lib.ArrayLib;
import rdfbones.lib.GraphLib;
import rdfbones.lib.TripleLib;
import rdfbones.lib.VariableDependency;
import rdfbones.rdfdataset.Constant;
import rdfbones.rdfdataset.Graph;
import rdfbones.rdfdataset.RDFNode;
import rdfbones.rdfdataset.RestrictionTriple;
import rdfbones.rdfdataset.Triple;

public class DependencyCalculator {

  public static void calculate(Graph graph, List<Triple> triples, Form form,
    List<String> inputVariables) {

    if (form == null) {
      return;
    }
    System.out.println("InputVariables");
    System.out.println(ArrayLib.debugList(inputVariables));
    List<Form> formQueue = new ArrayList<Form>();
    for (FormElement element : form.formElements) {
      List<Triple> copy = new ArrayList<Triple>();
      copy.addAll(triples);
      GraphPath graphPath = getGraphPath(new GraphPath(), copy, element.node.varName);
      graphPath.validate(inputVariables, TripleLib.sdeSchemeTriples());
      //System.out.println(graphPath.debugValid());
      graph.variableDependencies.put(element.node.varName, new VariableDependency(graph,
          graphPath, element.node.varName));

      // inputVariables.add(element.node.varName);
      /*
       * if(element instanceof SubformAdder){ calculate(graph, triples,
       * ((SubformAdder)element).subForm, inputVariables); }
       */
    }
  }

  static GraphPath getGraphPath(GraphPath path, List<Triple> triples, String node) {

    List<Triple> subTriples = getTriple(triples, node);
    if (subTriples.size() == 1) {
      System.out.println("1");
      path.triples.add(subTriples.get(0));
    } else {
      System.out.println("more : " + subTriples.size());
      for (Triple triple : subTriples) {
        String object = GraphLib.getObject(triple, node);
        GraphPath subPath = new GraphPath(triple);
        subPath.input = node;
        path.subPaths.add(getGraphPath(subPath, triples, object));
      }
    }
    return path;
  }

  static List<Triple> getTriple(List<Triple> triples, String node) {
    List<Integer> nums = new ArrayList<Integer>();
    List<Triple> toReturn = new ArrayList<Triple>();
    Integer i = new Integer(0);
    for (Triple triple : triples) {
      if (triple.subject.varName.equals(node) || triple.object.varName.equals(node)) {
        if (triple instanceof RestrictionTriple) {
          nums.add(i);
          toReturn.add(triple);
        }
      }
      i++;
    }
    ArrayLib.remove(triples, nums);
    return toReturn;
  }
}
