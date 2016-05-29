package es.upm.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: MoveInformation
* @author ontology bean generator
* @version 2016/05/26, 14:02:25
*/
public class MoveInformation implements AgentAction {

   /**
* Protege name: location
   */
   private Location location;
   public void setLocation(Location value) { 
    this.location=value;
   }
   public Location getLocation() {
     return this.location;
   }

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

}
