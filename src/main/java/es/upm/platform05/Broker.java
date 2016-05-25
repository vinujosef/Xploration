package es.upm.platform05;

import es.upm.ontology.RegistrationRequest;
import es.upm.ontology.XplorationOntology;
import es.upm.ontology.Location;
import es.upm.ontology.MoveInformation;
import es.upm.ontology.ReleaseCapsule;
import es.upm.ontology.RequestRoverMovement;
import jade.content.Concept;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;
import jade.core.Runtime;

/**
*
* @author Kop, Boshu, Vinu
*/

public class Broker extends Agent{

	private static final long serialVersionUID =1L;
    ACLMessage msg;
    XplorationOntology ontology = (XplorationOntology) XplorationOntology.getInstance();
    Codec codec = new SLCodec();
    ArrayList<MoveInformation> roversWhereabout = new ArrayList<MoveInformation>();
    
    protected void setup(){
    	System.out.println(getLocalName()+ " has entered into the system");
        getContentManager().registerOntology(ontology);
        getContentManager().registerLanguage(codec);
        //creating description
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setName(this.getName());
        sd.setType(ontology.PROTOCOL_MOVE_INFO);
        dfd.addServices(sd);
        
        try {
            //registers description in DF
                DFService.register(this,dfd);
            } 
            
        catch (FIPAException ex) { 
                 Logger.getLogger(World.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Receiving inform messages from rovers
        addBehaviour(new SimpleBehaviour(this)
        {
            @Override
            public void action() {
                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
                if(msg == null) return;
                else{
                	if(msg.getProtocol() == ontology.PROTOCOL_MOVE_INFO){                				                    		                        	
                		System.out.println(myAgent.getLocalName() + " got INFORM from "+ (msg.getSender()).getLocalName());
                		try {
                            Action ac = (Action) getContentManager().extractContent(msg);
                            MoveInformation mi = (MoveInformation) ac.getAction();
                            
                            if(roversWhereabout.contains(msg.getSender())){
                            	roversWhereabout.remove(msg.getSender());
                            	roversWhereabout.add(mi);
                            	
                            }
                            else{
                            	
                            	roversWhereabout.add(mi);
                            	
                            }
                            //change to: check if any rover in range, and should create a new Behavior!!!!!!
                            int a = new java.util.Random().nextInt(2);
                            if(a==1)	//"this should be replace by calculate"
                            {
                            	ACLMessage msg2 = new ACLMessage(ACLMessage.INFORM);
                                msg2.setOntology(ontology.getName());
                                msg2.setLanguage(codec.getName());
                                //Should be calculate and identify it's the rover in range
                                msg2.addReceiver(msg.getSender());
                                msg2.setProtocol(ontology.PROTOCOL_MOVE_INFO);
                                getContentManager().fillContent(msg2, new Action(getAID(), mi));
                                send(msg2);
                                
                                System.out.println(myAgent.getLocalName() + " sending communicate information to Rover05");
                            }
                            else return;
                            /*
                            ACLMessage reply = msg.createReply();
                            reply.setPerformative(ACLMessage.AGREE);
                        	myAgent.send(reply);
                        	System.out.println(myAgent.getLocalName() + " sent a AGREE to " + (msg.getSender()).getLocalName());
                            Thread.sleep(5000);
                            */
                        } catch (Codec.CodecException e) {
                            e.printStackTrace();
                        } catch (OntologyException e) {
                            e.printStackTrace();
                        }/* catch (InterruptedException e) {
							e.printStackTrace();
						}*/
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
         * TODO Adding calculation of rovers' locations
         * 
         * The behavior below will be triggered when the calculation is done
         
       addBehaviour(new SimpleBehaviour(this)
        {
            @Override
            public void action() {
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                    msg.setOntology(ontology.getName());
                    msg.setLanguage(codec.getName());
                    msg.addReceiver(TODO add receiver from calculation);
                    msg.setProtocol(ontology.PROTOCOL_MOVE_INFO);
                    System.out.println(myAgent.getLocalName() + " sending REQUEST to + TODO Rover's Name");
                    send(msg); 
                
            }

            @Override
            public boolean done() {
                return false;
            }
        });
          
         
         */
        
    }
	
}
