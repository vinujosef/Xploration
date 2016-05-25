package es.upm.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: RegisterAgents
* @author ontology bean generator
* @version 2016/05/24, 21:16:03
*/
public class RegisterAgents implements AgentAction {

   /**
* Protege name: rover
   */
   private Rover rover;
   public void setRover(Rover value) { 
    this.rover=value;
   }
   public Rover getRover() {
     return this.rover;
   }

   /**
* Protege name: capsule
   */
   private Capsule capsule;
   public void setCapsule(Capsule value) { 
    this.capsule=value;
   }
   public Capsule getCapsule() {
     return this.capsule;
   }

}
