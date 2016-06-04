package es.upm.company05;

import es.upm.ontology.RegistrationRequest;
import es.upm.ontology.ReleaseCapsule;
import es.upm.ontology.XplorationOntology;
import es.upm.platform05.Spacecraft;
import es.upm.ontology.Finding;
import es.upm.ontology.Findings;
import es.upm.ontology.FindingsMessage;
import es.upm.ontology.Frequency;
import es.upm.ontology.Location;
import es.upm.ontology.MoveInformation;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Capsule extends Agent {

	private static final long serialVersionUID =1L;
	XplorationOntology ontology = (XplorationOntology) XplorationOntology.getInstance();
	Codec codec = new SLCodec();
	Finding finding = new Finding();
    Findings findings = new Findings();
    Frequency frequency = new Frequency();
    HashMap<AID, Location> roversLocations = Spacecraft.roversLocations;

	protected void setup(){
		System.out.println(getLocalName()+ " has entered into the system");
		getContentManager().registerOntology(ontology);
		getContentManager().registerLanguage(codec);

		ReleaseCapsule rc = new ReleaseCapsule();

		//creating description
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setName(this.getName());
        
        sd.setType(ontology.PROTOCOL_SEND_FINDINGS);
        dfd.addServices(sd);
        
        sd.setType(ontology.PROTOCOL_UPDATE_FINDINGS);
        dfd.addServices(sd);
		
		// Release Rovers
		addBehaviour(new SimpleBehaviour(this)
		{
			@Override
			public void action() {
				try {
					int x = (int) myAgent.getArguments()[0];
					int y = (int) myAgent.getArguments()[1];
					Location location = new Location();
					location.setX(x);
			        location.setY(y);
			        roversLocations.put(myAgent.getAID(), location);
					releaseRover(myAgent.getArguments()[0],myAgent.getArguments()[1]);
					System.out.println(myAgent.getLocalName() + " is releasing a rover at "+ myAgent.getArguments()[0] +", " + myAgent.getArguments()[1]);
				}

				catch (ControllerException e) {
					e.printStackTrace();
				}
				finally{
					block();
				}
			}

			@Override
			public boolean done() {
				return true;
			}
		});
		// Receiving inform messages from Broker
		addBehaviour(new SimpleBehaviour(this)
        {
            @Override
            public void action() {
                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
                if(msg == null) return;
                else{
                	if(msg.getProtocol() == ontology.PROTOCOL_SEND_FINDINGS){
                		System.out.println(myAgent.getLocalName() + " got FINDINGS from "+ (msg.getSender()).getLocalName());
                		try {
                            Action ac = (Action) getContentManager().extractContent(msg);
                            FindingsMessage fm = (FindingsMessage) ac.getAction();
                            
                            if(fm.getFrequency().getChannel() == 5){
                                
                                DFAgentDescription dfd = new DFAgentDescription();
                                ServiceDescription sd = new ServiceDescription();
                                sd.setType("Spacecraft");
                                dfd.addServices(sd);
                                DFAgentDescription[] results = new DFAgentDescription[20];
                                results = DFService.search(myAgent,dfd);
            					for(int i=0;i<results.length;i++){
            	                    if(results[i]==null){
            	                        break;
            	                    }
            	                    
            	                    ACLMessage msg2 = new ACLMessage(ACLMessage.INFORM);
            	                    
            	                    msg2.setOntology(ontology.getName());
            	                    msg2.setLanguage(codec.getName());
            	                    msg2.addReceiver(results[i].getName());
            	                    msg2.setProtocol(ontology.PROTOCOL_UPDATE_FINDINGS);
            	                    
            	                    FindingsMessage fm2 = new FindingsMessage();
                                    
                                    fm2.setFindings(fm.getFindings());
                                    
                                    getContentManager().fillContent(msg2, new Action(getAID(), fm2));
            	                    send(msg2);
            	                    System.out.println(myAgent.getLocalName() + " updating Findings to "+results[i].getName().getLocalName());
            	                    break;
            	                    	
            	                    
                                
            	                    
            					}
                            }
            					
                            	
                     	                               
                            
                        } catch (Codec.CodecException e) {
                            e.printStackTrace();
                        } catch (OntologyException e) {
                            e.printStackTrace();
                        } catch (FIPAException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                		
                	}
                }
                
            }

            @Override
            public boolean done() {
                return false;
            }
        });
	}


	protected void releaseRover(Object x, Object y) throws ControllerException{

		ContainerController cc = getContainerController();
		
		Object agentObj[] = new Object[2];
		agentObj[0] = x;
		agentObj[1] = y;
		AgentController ac = cc.createNewAgent("Rover05", "es.upm.company05.Rover", agentObj);
		ac.start();
	}

}
