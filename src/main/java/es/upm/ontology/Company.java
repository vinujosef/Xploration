package es.upm.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: Company
* @author ontology bean generator
* @version 2016/05/22, 14:32:56
*/
public class Company implements Concept {

   /**
* Protege name: company_agent
   */
   private AID company_agent;
   public void setCompany_agent(AID value) { 
    this.company_agent=value;
   }
   public AID getCompany_agent() {
     return this.company_agent;
   }

   /**
* Protege name: name
   */
   private String name;
   public void setName(String value) { 
    this.name=value;
   }
   public String getName() {
     return this.name;
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
