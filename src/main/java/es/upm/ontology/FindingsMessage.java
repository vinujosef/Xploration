package es.upm.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: FindingsMessage
* @author ontology bean generator
* @version 2016/05/26, 14:02:25
*/
public class FindingsMessage implements AgentAction {

   /**
* Protege name: frequency
   */
   private Frequency frequency;
   public void setFrequency(Frequency value) { 
    this.frequency=value;
   }
   public Frequency getFrequency() {
     return this.frequency;
   }

   /**
* Protege name: findings
   */
   private Findings findings;
   public void setFindings(Findings value) { 
    this.findings=value;
   }
   public Findings getFindings() {
     return this.findings;
   }

}
