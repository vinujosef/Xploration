package es.upm.company05;

import es.upm.ontology.RegistrationRequest;
import es.upm.ontology.ReleaseCapsule;
import es.upm.ontology.RequestRoverMovement;
import es.upm.ontology.XplorationOntology;
import es.upm.ontology.Direction;
import es.upm.ontology.Location;
import es.upm.ontology.*;
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
import jade.core.behaviours.*;
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

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Rover extends Agent{
	
    private static final long serialVersionUID =1L;
    XplorationOntology ontology = (XplorationOntology) XplorationOntology.getInstance();
    Codec codec = new SLCodec();
    Location location = new Location();
    Direction direction = new Direction();
    Mineral mineral = new Mineral();
    MineralResult mr = new MineralResult();
    HashMap<Location, Mineral> analyzeResults = new HashMap<Location, Mineral>();
    
	protected void setup(){
		System.out.println(getLocalName()+ " has entered into the system");
        getContentManager().registerOntology(ontology);
        getContentManager().registerLanguage(codec);
      
        //set initial location
        addBehaviour(new OneShotBehaviour(this){
        	@Override
			public void action() {
        		int x = (int) myAgent.getArguments()[0];
    			int y = (int) myAgent.getArguments()[1];
                location.setX(x);
                location.setY(y);
        	}
        });
        
        //Move
        addBehaviour(new SimpleBehaviour(this){

			@Override
			public void action() {
				setX(new java.util.Random().nextInt(6 - 1 + 1) + 1);
			}

			@Override
			public boolean done() {
				return false;
			}
        	
        });
        
        // Inform World to move
        addBehaviour(new OneShotBehaviour(this){
        	

			@Override
			public void action() {
				try {
                    Thread.sleep(700);
                } 
                catch (InterruptedException ex) {
                    Logger.getLogger(Rover.class.getName()).log(Level.SEVERE, null, ex);
                }
				
                DFAgentDescription dfd = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType(ontology.PROTOCOL_ROVER_MOVEMENT);
                dfd.addServices(sd);
                DFAgentDescription[] results = new DFAgentDescription[20];
                
                try {
                    results = DFService.search(myAgent,dfd);
                    for(int i=0;i<results.length;i++){
                        if(results[i]==null){
                            break;
                        }

                        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                        msg.setOntology(ontology.getName());
                        msg.setLanguage(codec.getName());
                        msg.addReceiver(results[i].getName());
                        msg.setProtocol(ontology.PROTOCOL_ROVER_MOVEMENT);
                        RequestRoverMovement rrm = new RequestRoverMovement();
                        rrm.setDirection(direction);
                        getContentManager().fillContent(msg, new Action(getAID(), rrm));
                        System.out.println(myAgent.getLocalName() + " sending direction to World");
                        send(msg);                    
                   }
                } 
                catch (FIPAException ex) { 
                    Logger.getLogger(Rover.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Codec.CodecException e) {
                    e.printStackTrace();
                } catch (OntologyException e) {
                    e.printStackTrace();
                }
				
			}

        });
        // Cancel the movement
        addBehaviour(new OneShotBehaviour(this){
        	

			@Override
			public void action() {
				try {
                    Thread.sleep(700);
                } 
                catch (InterruptedException ex) {
                    Logger.getLogger(Rover.class.getName()).log(Level.SEVERE, null, ex);
                }
				
                DFAgentDescription dfd = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType(ontology.PROTOCOL_ROVER_MOVEMENT);
                dfd.addServices(sd);
                DFAgentDescription[] results = new DFAgentDescription[20];
                try {
					results = DFService.search(myAgent,dfd);
					for(int i=0;i<results.length;i++){
	                    if(results[i]==null){
	                        break;
	                    }
	                    ACLMessage msg = new ACLMessage(ACLMessage.CANCEL);
	                    msg.setOntology(ontology.getName());
	                    msg.setLanguage(codec.getName());
	                    msg.addReceiver(results[i].getName());
	                    msg.setProtocol(ontology.PROTOCOL_ROVER_MOVEMENT);
	                    
	                    System.out.println(myAgent.getLocalName() + " sending CANCEL to World");
	                    send(msg);  
					}
					
					  
                
				} catch (FIPAException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

        });
        
        
        
        // Inform Rover the mineral result
 
        /*
        BEHAVIOUR
        Getting refusal performative if time exceeds and displaying the message
        */        
        /*addBehaviour(new SimpleBehaviour(this)
        {
            @Override
            public void action() {
                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.REFUSE));
                if(msg == null) return;
                else{
                	if(msg.getProtocol() == ontology.PROTOCOL_ROVER_MOVEMENT){
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
        */
        /*
        BEHAVIOUR: when receiving NOT UNDERSTOOD of move
        */ 
        addBehaviour(new SimpleBehaviour(this)
        {
            @Override
            public void action() {
                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.NOT_UNDERSTOOD));
                if(msg == null) return;
                else{
                	if(msg.getProtocol() == ontology.PROTOCOL_ROVER_MOVEMENT){
                		System.out.println(myAgent.getLocalName() + " received an NOT UNDERSTOOD from "+ (msg.getSender()).getLocalName());
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
        BEHAVIOUR: when receiving REFUSE of move
        */ 
        addBehaviour(new SimpleBehaviour(this)
        {
            @Override
            public void action() {
                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.REFUSE));
                if(msg == null) return;
                else{
                	if(msg.getProtocol() == ontology.PROTOCOL_ROVER_MOVEMENT){
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
                return false;
            }
        });
        
        /*
        BEHAVIOUR: when receiving AGREE of move
        */ 
        addBehaviour(new SimpleBehaviour(this)
        {
            @Override
            public void action() {
                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.AGREE));
                if(msg == null) return;
                else{
                	if(msg.getProtocol() == ontology.PROTOCOL_ROVER_MOVEMENT){
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
        BEHAVIOUR: when receiving FAILURE of move
        */ 
        addBehaviour(new SimpleBehaviour(this)
        {
            @Override
            public void action() {
                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.FAILURE));
                if(msg == null) return;
                else{
                	if(msg.getProtocol() == ontology.PROTOCOL_ROVER_MOVEMENT){
                        System.out.println(myAgent.getLocalName() + " received an FAILURE from "+ (msg.getSender()).getLocalName());
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
        BEHAVIOUR: when receiving INFORM of move achieved
        */ 
        addBehaviour(new SimpleBehaviour(this)
        {
            @Override
            public void action() {
                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
                if(msg == null) return;
                else{
                	if(msg.getProtocol() == ontology.PROTOCOL_ROVER_MOVEMENT){
                		System.out.println(myAgent.getLocalName() + " received an INFORM from "+ (msg.getSender()).getLocalName());
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
        
        //request analyze
        addBehaviour(new SimpleBehaviour(this)
        {
            @Override
            public void action() {
                try {
                    Thread.sleep(700);
                } 
                catch (InterruptedException ex) {
                    Logger.getLogger(Rover.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                DFAgentDescription dfd = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType(ontology.PROTOCOL_ANALYZE_MINERAL);
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
                    msg.setProtocol(ontology.PROTOCOL_ANALYZE_MINERAL);
                    
                    System.out.println(myAgent.getLocalName() + " sending REQUEST to WORLD");
                    send(msg);                        
                   }
                } 
                catch (FIPAException ex) { 
                    Logger.getLogger(Rover.class.getName()).log(Level.SEVERE, null, ex);
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
        BEHAVIOUR: when receiving NOT UNDERSTOOD of analyze
        */ 
        addBehaviour(new SimpleBehaviour(this)
        {
            @Override
            public void action() {
                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.NOT_UNDERSTOOD));
                if(msg == null) return;
                else{
                	if(msg.getProtocol() == ontology.PROTOCOL_ANALYZE_MINERAL){
                		System.out.println(myAgent.getLocalName() + " received an NOT UNDERSTOOD from "+ (msg.getSender()).getLocalName());
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
        BEHAVIOUR: when receiving REFUSE of analyze
        */ 
        addBehaviour(new SimpleBehaviour(this)
        {
            @Override
            public void action() {
                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.REFUSE));
                if(msg == null) return;
                else{
                	if(msg.getProtocol() == ontology.PROTOCOL_ANALYZE_MINERAL){
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
                return false;
            }
        });
        
        /*
        BEHAVIOUR: when receiving AGREE of analyze
        */ 
        addBehaviour(new SimpleBehaviour(this)
        {
            @Override
            public void action() {
                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.AGREE));
                if(msg == null) return;
                else{
                	if(msg.getProtocol() == ontology.PROTOCOL_ANALYZE_MINERAL){
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
        BEHAVIOUR: when receiving INFORM mineral
        */ 
        addBehaviour(new SimpleBehaviour(this)
        {
            @Override
            public void action() {
                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
                if(msg == null) return;
                else{
                	if(msg.getProtocol() == ontology.PROTOCOL_ANALYZE_MINERAL){
                		ContentElement ce;
                		try {
							ce = (Action) getContentManager().extractContent(msg);
							mr = (MineralResult) ((Action)ce).getAction();
							analyzeResults.put(location,mr.getMineral());
	                		System.out.println(myAgent.getLocalName() + " received an MineralResult from "+ (msg.getSender()).getLocalName()+" "+mr.getMineral().getType());
	                		doWait(1500);
						} catch (CodecException | OntologyException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
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
	
	private void setX(int x){
		direction.setX(x);
	}
	
	private int getX(){
		return direction.getX();
	}

}
