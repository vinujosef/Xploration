package es.upm.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: MineralResult
* @author ontology bean generator
* @version 2016/05/11, 19:54:30
*/
public class MineralResult implements AgentAction {

   /**
* Protege name: Mineral
   */
   private Mineral mineral;
   public void setMineraL(Mineral value) { 
    this.mineral=value;
   }
   public Mineral getMineral() {
     return this.mineral;
   }

}