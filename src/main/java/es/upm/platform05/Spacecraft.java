/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.platform05;

import es.upm.ontology.RegistrationRequest;
import es.upm.ontology.XplorationOntology;
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
import java.util.Arrays;
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
    ArrayList<String> companyList = new ArrayList<>();
    
    protected void setup(){
        System.out.println(getLocalName()+ " has entered into the system");
        getContentManager().registerOntology(ontology);
        getContentManager().registerLanguage(codec);
        //creating description
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setName(this.getName());
        sd.setType("Spacecraft");

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
//                                the company should be added to the <companyList> for checking
                                companyList.add(msg.getSender().getLocalName());
                                System.out.println("Registered successfuly " + regRequest.getCompany());
//                                for loop to check if the companies have been added into "companyList" array
//                                for(int j=0; j < companyList.size(); j++) {
//                                    System.out.println(companyList.get(j));
//                                }
                            }
                            send(registration_reply);
                            // begins as02
//                            release(msg.getSender());
                            // ends as02

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
    
    // as02
    
    protected void release(AID receiver){
        
        Location location = new Location();
        location.setX(new java.util.Random().nextInt());
        location.setY(new java.util.Random().nextInt());
    	
    	addBehaviour(new CyclicBehaviour(this)
		{
			 public void action() {
				 ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				 msg.setOntology(ontology.getName());
				 msg.setLanguage(codec.getName());
				 msg.addReceiver(receiver);
				 msg.setProtocol(ontology.PROTOCOL_RELEASE_CAPSULE);
				 ReleaseCapsule releaseCapsule = new ReleaseCapsule();
				 releaseCapsule.setLocation(location);
				 try {
					getContentManager().fillContent(msg, new Action(getAID(), releaseCapsule));
					//System.out.println(myAgent.getLocalName() + " sending RELEASE CAPSULE to Spacecraft");
                    send(msg);
				} catch (CodecException | OntologyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
		});
    }
    
}
