package es.upm.platform05;

import es.upm.ontology.RegistrationRequest;
import es.upm.ontology.ReleaseCapsule;
import es.upm.ontology.RequestRoverMovement;
import es.upm.ontology.XplorationOntology;
import es.upm.ontology.Direction;
import es.upm.ontology.Location;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.content.onto.basic.Action;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.Runtime;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import jade.core.AID;
import java.util.HashMap;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;

public class World extends Agent{
	
    private static final long serialVersionUID =1L;
    XplorationOntology ontology = (XplorationOntology) XplorationOntology.getInstance();
    Codec codec = new SLCodec();
    ACLMessage msg;
    HashMap<AID, Direction> roversDirections = new HashMap<AID, Direction>();
    HashMap<AID, Location> roversLocations = new HashMap<AID, Location>();
    
	protected void setup(){
		System.out.println(getLocalName()+ " has entered into the system");
        getContentManager().registerOntology(ontology);
        getContentManager().registerLanguage(codec);
        //creating description
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setName(this.getName());
        sd.setType(ontology.PROTOCOL_ROVER_MOVEMENT);
        dfd.addServices(sd);
        
        try {
            //registers description in DF
                DFService.register(this,dfd);
            } 
            
        catch (FIPAException ex) { 
                 Logger.getLogger(World.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Let say this is the time it takes for a rover to move
        DateTime finalcall = DateTime.now().plusMinutes(10);
        
        addBehaviour(new CyclicBehaviour(this){

			@Override
			public void action() {
				msg = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
				if(msg != null){
					ACLMessage reply = msg.createReply();
					if(msg.getProtocol().equals(ontology.PROTOCOL_ROVER_MOVEMENT)){
						System.out.println(myAgent.getLocalName()+" received new direction from "+ (msg.getSender()).getLocalName());
						if(finalcall.isAfterNow())
	                    {
	                        if(roversDirections.containsKey(msg.getSender())){
	                        	reply.setPerformative(ACLMessage.REFUSE);
	                        	myAgent.send(reply);
	                        	System.out.println(myAgent.getLocalName() + " sent a REFUSE to " + (msg.getSender()).getLocalName());
	                        }
	                        else{
	                        	reply.setPerformative(ACLMessage.AGREE);
	                        	myAgent.send(reply);
	                        	System.out.println(myAgent.getLocalName() + " sent an AGREE to " + (msg.getSender()).getLocalName());
	                        	doWait(500);
	                            try {
	                                Action ac = (Action) getContentManager().extractContent(msg);
	                                RequestRoverMovement rrm = (RequestRoverMovement) ac.getAction();
	                                System.out.println("Adding movement from " + msg.getSender().getLocalName());
	                                roversDirections.put(msg.getSender(), rrm.getDirection());
	                                
	                                /*
	                                 * TODO Set locations
	                                 */

	                            } catch (Codec.CodecException e) {
	                                e.printStackTrace();
	                            } catch (OntologyException e) {
	                                e.printStackTrace();
	                            }
	                        }
	                    }
						else{
							
							boolean failure = false;
							
							for(AID aid: roversLocations.keySet()){
								if(msg.getSender().equals(aid)){
									continue;
								}
								else{
									// when the rover has the same location as another rover = crash
									if(roversLocations.get(aid).equals(roversLocations.get(msg.getSender()))){		                        	
			                        	//remove those rovers off
			                        	roversLocations.remove(aid);
			                        	roversLocations.remove(msg.getSender());
			                        	roversDirections.remove(aid);
			                        	roversDirections.remove(msg.getSender());
			                        	
			                        	failure = true;
			                        	break;
									}
								}
							}
							
							if(failure){
								reply.setPerformative(ACLMessage.FAILURE);
	                        	myAgent.send(reply);
	                        	System.out.println(myAgent.getLocalName() + " sent an FAILURE to " + (msg.getSender()).getLocalName());
							}
							else{
								// when there's no crash
								reply.setPerformative(ACLMessage.INFORM);
		                    	myAgent.send(reply);
		                    	System.out.println(myAgent.getLocalName() + " sent an INFORM to " + (msg.getSender()).getLocalName());
		                    	
		                    	//remove directions after finishing moving
		                    	roversDirections.remove(msg.getSender());
							}
	                    	
	                        block();
						}
					}
					else{
						if(finalcall.isBeforeNow())
	                    {
							reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
		                    myAgent.send(reply);
		                    System.out.println(myAgent.getLocalName() + " sent a NOT UNDERSTOOD to " + (msg.getSender()).getLocalName());
	                    }
						block();
					}
				}
				else{
					block();
				}
			}
        	
        });
        
	}	
	
	protected void setLocation(AID aid, Location location){
		//TODO set the locations
	}
	
	protected void setLocation(AID aid, Direction direction){
		//TODO calculate the location
	}
	
	protected Location getLocation(AID aid){
		if(roversLocations.containsKey(aid)){
			return roversLocations.get(aid);
		}
		else{
			System.out.println("Cannot get location due to inexistence of the rover.");
			return null;
		}
	}

}
