/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.company05;

import es.upm.ontology.RegistrationRequest;
import es.upm.ontology.ReleaseCapsule;
import es.upm.ontology.XplorationOntology;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

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
    ReleaseCapsule rc = new ReleaseCapsule();
    
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
                if(msg == null){
                	return;
                }
                else{
                	if(msg.getProtocol() == ontology.PROTOCOL_REGISTRATION){
                		System.out.println(myAgent.getLocalName() + " got REFUSED from "+ (msg.getSender()).getLocalName());
                		doWait(1500);
                	}
                	else{
                		return;
                	}
                }
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
                if(msg == null){
                	return;
                }
                else{
                	if(msg.getProtocol() == ontology.PROTOCOL_REGISTRATION){
                		System.out.println(myAgent.getLocalName() + " got AGREE from "+ (msg.getSender()).getLocalName());
                		doWait(1500);
                	}
                	else{
                		return;
                	}
                }
            }

            @Override
            public boolean done() {
                return false;
            }
        });
        
        /*
        BEHAVIOUR
        Getting INFORM perfomative if not registered earlier and printing the message
        AND if get release capsule inform
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
                if(msg == null){
                	return;
                }
                else{
                	if(msg.getProtocol() == ontology.PROTOCOL_REGISTRATION){
                		System.out.println(myAgent.getLocalName() + " got INFORM from "+ (msg.getSender()).getLocalName());
                	}
                	else if(msg.getProtocol() == ontology.PROTOCOL_RELEASE_CAPSULE){
                		ContentElement ce;
    					try {
    						ce = (Action) getContentManager().extractContent(msg);
    						rc = (ReleaseCapsule) ((Action)ce).getAction();
    	                	System.out.println(myAgent.getLocalName() + " received a capsule release from " + msg.getSender().getLocalName() + " " +rc.getLocation().getX());
    	                	
    	                	//Runtime rt = Runtime.instance();
    	            		//Profile profile = new ProfileImpl(null, 1200, null);
    	                	//ContainerController cc = rt.createAgentContainer(profile);
    	            		ContainerController cc = getContainerController();
    	            		//Object obj = new Object();
    	            		Object agentObj[] = new Object[2];
    	            		agentObj[0] = rc.getLocation().getX();
    	            		agentObj[1] = rc.getLocation().getY();
    	            		AgentController ac = cc.createNewAgent("Capsule05", "es.upm.company05.Capsule", agentObj);
    	            		
    	            		ac.start();
    	                	
    					} catch (CodecException | OntologyException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					} catch (StaleProxyException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}
                	}
                	else{
                		return;
                	}
                	doWait(1500);
                }
                
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
                if(msg == null){
                	return;
                }
                else{
                	if(msg.getProtocol() == ontology.PROTOCOL_REGISTRATION){
                		System.out.println(myAgent.getLocalName() + " got FAILURE from "+ (msg.getSender()).getLocalName());
                		doWait(1500);
                	}
                	else{
                		return;
                	}
                }
            }

            @Override
            public boolean done() {
                return false;
            }
        });        
    }
    
   
}
