/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.company05;

import es.upm.ontology.RegistrationRequest;
import es.upm.ontology.XplorationOntology;
import es.upm.ontology.Location;
import es.upm.ontology.Capsule;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kop, Boshu, Vinu
 */
public class Company extends Agent{
    
    private static final long serialVersionUID =1L;
    XplorationOntology ontology = (XplorationOntology) XplorationOntology.getInstance();
    Codec codec = new SLCodec();
    
    protected void setup(){
        System.out.println(getLocalName()+ " has entered into the system");
        getContentManager().registerOntology(ontology);
        getContentManager().registerLanguage(codec);
        /*
        BEHAVIOUR        
        Finds the spacecraft and make a request for registration
        */     
        addBehaviour(new SimpleBehaviour(this)
        {
            @Override
            public void action() {
                try {
                    Thread.sleep(700);
                } 
                catch (InterruptedException ex) {
                    Logger.getLogger(Company.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                DFAgentDescription dfd = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType(ontology.PROTOCOL_REGISTRATION);
                dfd.addServices(sd);
                DFAgentDescription[] results = new DFAgentDescription[20];
                try {
                    results = DFService.search(myAgent,dfd);
//                    System.out.println("Found " + results.length);
                    for(int i=0;i<results.length;i++){
                        if(results[i]==null){
                            break;
                        }

                    ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                    msg.setOntology(ontology.getName());
                    msg.setLanguage(codec.getName());
                    msg.addReceiver(results[i].getName());
                    msg.setProtocol(ontology.PROTOCOL_REGISTRATION);
                    RegistrationRequest regRequest = new RegistrationRequest();
                    regRequest.setCompany("Company05");
                    getContentManager().fillContent(msg, new Action(getAID(), regRequest));
                    System.out.println(myAgent.getLocalName() + " sending REQUEST to Spacecraft");
                    send(msg);                        
                   }
                } 
                catch (FIPAException ex) { 
                    Logger.getLogger(Company.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Codec.CodecException e) {
                    e.printStackTrace();
                } catch (OntologyException e) {
                    e.printStackTrace();
                }
//                doWait(1000);                
//                doWait(1000);
            }

            @Override
            public boolean done() {
                return true;
                }       
        });
        
        /*
        BEHAVIOUR
        Getting refusal performative if time exceeds and displaying the message
        */        
        addBehaviour(new SimpleBehaviour(this)
        {
            @Override
            public void action() {
                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.REFUSE));
                if(msg == null) return;
                System.out.println(myAgent.getLocalName() + " got REFUSED from "+ (msg.getSender()).getLocalName());
                doWait(1500);
            }
            
            @Override
            public boolean done() {
                //once i added the false only, the refuse message got printed
                return false;
            }
        });
        
        /*
        BEHAVIOUR
        Getting accepted performative if on time and displaying the message
        */ 
        addBehaviour(new SimpleBehaviour(this)
        {
            @Override
            public void action() {
                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.AGREE));
                if(msg == null) return;
                System.out.println(myAgent.getLocalName() + " received an AGREE from "+ (msg.getSender()).getLocalName());
                doWait(1500);
            }

            @Override
            public boolean done() {
                return false;
            }
        });
        
        /*
        BEHAVIOUR
        Getting INFORM perfomative if not registered earlier and printing the message
        */ 
        addBehaviour(new SimpleBehaviour(this)
        {
            @Override
            public void action() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Company.class.getName()).log(Level.SEVERE, null, ex);
                }
                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
                if(msg == null) return;
                System.out.println(myAgent.getLocalName() + " received an INFORM from "+ (msg.getSender()).getLocalName());
//                System.out.println("--------------------------------------");
            }

            @Override
            public boolean done() {
                return false;
            }
        });
        
        /*
        BEHAVIOUR
        Getting FAILURE perfomative if registered earlier and printing the message
        */ 
        addBehaviour(new SimpleBehaviour(this)
        {
            @Override
            public void action() {
                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.FAILURE));
                if(msg == null) return;
                System.out.println(myAgent.getLocalName() + " received an FAILURE from "+ (msg.getSender()).getLocalName());
                System.out.println("--------------------------------------");
            }

            @Override
            public boolean done() {
                return false;
            }
        });
        
        // as02
        
        addBehaviour(new SimpleBehaviour(this)
    	{
    		@Override
    		public void action(){
    			ACLMessage msg = receive(MessageTemplate.MatchProtocol(ontology.PROTOCOL_RELEASE_CAPSULE));
    			if(msg == null || msg.getPerformative() != ACLMessage.INFORM){
    				return;
    			}
    			else{
    				try { 
						if(msg.getContentObject() instanceof Location){
							Location capsLocation = (Location) msg.getContentObject();
							Capsule capsule = new Capsule();
							capsule.setName("capsule01");
						}
					} catch (UnreadableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    			}
    		}

			@Override
			public boolean done() {
				// TODO Auto-generated method stub
				return false;
			}
    	});
        
    }
   
}
