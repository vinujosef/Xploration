package es.upm.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: MoveInformation
* @author ontology bean generator
* @version 2016/05/22, 14:32:56
*/
public class MoveInformation implements AgentAction {

   /**
* Protege name: RoverLocation
   */
   private Location roverLocation;
   public void setRoverLocation(Location value) { 
    this.roverLocation=value;
   }
   public Location getRoverLocation() {
     return this.roverLocation;
   }

   /**
* Protege name: MoveDirection
   */
   private Direction moveDirection;
   public void setMoveDirection(Direction value) { 
    this.moveDirection=value;
   }
   public Direction getMoveDirection() {
     return this.moveDirection;
   }

}
