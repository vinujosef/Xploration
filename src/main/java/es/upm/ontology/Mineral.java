package es.upm.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: Mineral
* @author ontology bean generator
* @version 2016/05/22, 14:32:56
*/
public class Mineral implements Concept {

   /**
   * The mineral result type of the cell.
* Protege name: type
   */
   private String type;
   public void setType(String value) { 
    this.type=value;
   }
   public String getType() {
     return this.type;
   }

}
