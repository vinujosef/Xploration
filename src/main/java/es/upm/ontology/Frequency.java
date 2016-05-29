package es.upm.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: Frequency
* @author ontology bean generator
* @version 2016/05/26, 14:02:25
*/
public class Frequency implements Concept {

   /**
* Protege name: channel
   */
   private int channel;
   public void setChannel(int value) { 
    this.channel=value;
   }
   public int getChannel() {
     return this.channel;
   }

}
