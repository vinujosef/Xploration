package es.upm.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: MineralResult
* @author ontology bean generator
* @version 2016/05/26, 14:02:25
*/
public class MineralResult implements AgentAction {

   /**
   * The mineral result of cell.
* Protege name: mineral
   */
   private Mineral mineral;
   public void setMineral(Mineral value) { 
    this.mineral=value;
   }
   public Mineral getMineral() {
     return this.mineral;
   }

}