package es.upm.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: RequestRoverMovement
* @author ontology bean generator
* @version 2016/05/11, 19:54:30
*/
public class RequestRoverMovement implements AgentAction {

   /**
   * The direction of the rover movement
* Protege name: direction
   */
   private Direction direction;
   public void setDirection(Direction value) { 
    this.direction=value;
   }
   public Direction getDirection() {
     return this.direction;
   }

}
