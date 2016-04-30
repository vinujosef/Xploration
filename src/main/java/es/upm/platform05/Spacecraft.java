/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.platform05;

import es.upm.ontology.RegistrationRequest;
import es.upm.ontology.XplorationOntology;
import jade.content.Concept;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;
/**
 *
 * @author Kop, Boshu, Vinu
 */
public class Spacecraft extends Agent{
    
     private static final long serialVersionUID =1L;
    public final static String REGISTRATION = "Registration";
    ACLMessage msg;
    XplorationOntology ontology = (XplorationOntology) XplorationOntology.getInstance();
    Codec codec = new SLCodec();
    ArrayList<AID> companyList = new ArrayList<>();
    
    protected void setup(){
        System.out.println(getLocalName()+ " has entered into the system");
        getContentManager().registerOntology(ontology);
        getContentManager().registerLanguage(codec);
        //creating description
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setName(this.getName());
        sd.setType(ontology.PROTOCOL_REGISTRATION);
        dfd.addServices(sd);
        
        try {
        //registers description in DF
            DFService.register(this,dfd);
        } 
        
        catch (FIPAException ex) { 
             Logger.getLogger(Spacecraft.class.getName()).log(Level.SEVERE, null, ex);
         }

        DateTime finalcall = DateTime.now().plusMinutes(1);
//        DateTime finalcall = DateTime.now().plusSeconds(10);    
        System.out.println(getLocalName()+" registered in the DF");
        System.out.println("-------------------------------------");

        dfd=null;
        sd=null;  
        
        addBehaviour(new CyclicBehaviour(this)
        {
            private static final long serialVersionUID =1L;
            public void action() {
                
                //Receiving request message from Company
                msg = receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
                if(msg!=null){
                    //If a request message is received from Company
                    System.out.println(myAgent.getLocalName()+" received REQUEST from "+ (msg.getSender()).getLocalName());
                    ACLMessage registration_reply = msg.createReply();
                    if(finalcall.isBeforeNow())
                    {
                        //if the registration of the company is late
                        //reply -> refuse
                        registration_reply.setPerformative(ACLMessage.REFUSE);
                        myAgent.send(registration_reply);
                        System.out.println(myAgent.getLocalName() + " sent a REFUSE to " + (msg.getSender()).getLocalName());
                    }
                    
                    else{
                        //reply -> accept
                        registration_reply.setPerformative(ACLMessage.AGREE);
                        myAgent.send(registration_reply);
                        System.out.println(myAgent.getLocalName() + " sent an AGREE to " + (msg.getSender()).getLocalName());
                        //adding the company to a temporary variable "companyName" to check if it has already been registered
                        doWait(500);
                        try {
                            Action ac = (Action) getContentManager().extractContent(msg);
                            RegistrationRequest regRequest = (RegistrationRequest) ac.getAction();
                            System.out.println("Registering " + regRequest.getCompany());

                            if(companyList.contains(msg.getSender())){
                                registration_reply.setPerformative(ACLMessage.FAILURE);

                                System.out.println("Failed to register " + regRequest.getCompany() + ". Already registered.");
                            }
                            else{
                                registration_reply.setPerformative(ACLMessage.INFORM);

                                System.out.println("Registered successfuly  " + regRequest.getCompany());
                            }
                            send(registration_reply);

                        } catch (Codec.CodecException e) {
                            e.printStackTrace();
                        } catch (OntologyException e) {
                            e.printStackTrace();
                        }
                  }
                }
                
                else{
                    // If not message arrives
                    block();
                }
            }
        });

    }
    
}
