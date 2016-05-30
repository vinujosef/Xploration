package es.upm.company05;

import es.upm.ontology.RegistrationRequest;
import es.upm.ontology.ReleaseCapsule;
import es.upm.ontology.RequestRoverMovement;
import es.upm.ontology.XplorationOntology;
import es.upm.platform05.*;
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
import jade.core.behaviours.SimpleBehaviour;
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
    es.upm.ontology.Rover rover05 = new es.upm.ontology.Rover();
    HashMap<Location, Mineral> analyzeResults = new HashMap<Location, Mineral>();
    HashMap<AID, Location> roversLocations = Spacecraft.roversLocations;

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
                roversLocations.put(getAID(), location);
                rover05.setName(getAID().getName());
                rover05.setRover_agent(getAID());
                System.out.println("-------------------------------------");
            }
        });


        // Inform World to move
        addBehaviour(new SimpleBehaviour(this){


            @Override
            public void action() {
                try {
                    Thread.sleep(700);
                }
                catch (InterruptedException ex) {
                    Logger.getLogger(Rover.class.getName()).log(Level.SEVERE, null, ex);
                }
                move();
                
            }
            
            @Override
            public boolean done() {
            	
                return true;
            }

        });
       

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
                    else if(msg.getProtocol() == ontology.PROTOCOL_ANALYZE_MINERAL){
                        System.out.println(myAgent.getLocalName() + " received an NOT UNDERSTOOD from "+ (msg.getSender()).getLocalName());
                        doWait(1500);
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
                        try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        move();
                    }
                    else if(msg.getProtocol() == ontology.PROTOCOL_ANALYZE_MINERAL){
                        System.out.println(myAgent.getLocalName() + " got REFUSED from "+ (msg.getSender()).getLocalName());
                        
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
                        
                    }
                    else if(msg.getProtocol() == ontology.PROTOCOL_ANALYZE_MINERAL){
                        System.out.println(myAgent.getLocalName() + " got AGREE from "+ (msg.getSender()).getLocalName());
                        //get agree, wake the agent to run, wake line 287
                        doWake();
                        //let agent wait again, not to send move request, line 302 wake it up
                        doWait();
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
                        try {
							Thread.sleep(100000000);
						} catch (InterruptedException e) {
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
                        System.out.println("-------------------------------------");
                        
                        System.out.println("Start to analyze!!!");
                        ACLMessage msg2 = new ACLMessage(ACLMessage.REQUEST);
                        msg2.setOntology(ontology.getName());
                        msg2.setLanguage(codec.getName());
                        msg2.addReceiver(msg.getSender());

                        msg2.setProtocol(ontology.PROTOCOL_ANALYZE_MINERAL);
                        send(msg2);
                        
                        System.out.println(myAgent.getLocalName() + " sending mineral REQUEST to WORLD");
                        //wait until analyze finished, line 220 wake agent up first time
                        doWait();                      
                        move();                       
                    }
                    else if(msg.getProtocol() == ontology.PROTOCOL_ANALYZE_MINERAL){
                        ContentElement ce;
                        try {
                            ce = (Action) getContentManager().extractContent(msg);
                            mr = (MineralResult) ((Action)ce).getAction();
                            analyzeResults.put(location,mr.getMineral());
                            System.out.println(myAgent.getLocalName() + " received an MineralResult from "+ (msg.getSender()).getLocalName()+" "+mr.getMineral().getType());
                            System.out.println("-------------------------------------");
                            //back to line 286 move() again
                            doWake();

                        } catch (CodecException | OntologyException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                    else if(msg.getProtocol() == ontology.PROTOCOL_MOVE_INFO){
                		ContentElement ce;
                		try {
                			ce = (Action) getContentManager().extractContent(msg);
                			MoveInformation mi = (MoveInformation) ((Action)ce).getAction();
                			System.out.println(myAgent.getLocalName() + " received an INFORM from "+ (msg.getSender()).getLocalName() + ". There is a rover at location (" + mi.getLocation().getX() + ", " + mi.getLocation().getY() + "), and moving towards direction " + mi.getDirection().getX());
                			//Calculate if they can crash of not
                			cancel();
                			Thread.sleep(1000);
                			move();
                			
                			
                			
                		} catch (CodecException | OntologyException e) {
                			e.printStackTrace();
                		} catch (InterruptedException e) {
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


    
    private void move(){
    	addBehaviour(new SimpleBehaviour(this)
    	{
    		
    		@Override
            public void action() {
    			
    			setX(new java.util.Random().nextInt(6 - 1 + 1) + 1);
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

                        ACLMessage msg2 = new ACLMessage(ACLMessage.REQUEST);
                        msg2.setOntology(ontology.getName());
                        msg2.setLanguage(codec.getName());
                        msg2.addReceiver(results[i].getName());
                        msg2.setProtocol(ontology.PROTOCOL_ROVER_MOVEMENT);
                        RequestRoverMovement rrm = new RequestRoverMovement();
                        rrm.setDirection(direction);
                        getContentManager().fillContent(msg2, new Action(getAID(), rrm));
                        System.out.println(myAgent.getLocalName() + " sending direction" + direction.getX() +" to World");
                        send(msg2);
                        /* Iteration 5
                         * SENDING an inform Message Broker when moving
                         */
                                                                       
                        dfd = new DFAgentDescription();
                        sd = new ServiceDescription();
                        sd.setType(ontology.PROTOCOL_MOVE_INFO);
                        dfd.addServices(sd);
                        results = new DFAgentDescription[20];
                        
                        results = DFService.search(myAgent,dfd);
                        for(int j=0;j<results.length;j++){
                            if(results[j]==null){
                                break;
                            }
                            
                            MoveInformation mi = new MoveInformation();
                            mi.setRover(rover05);
                            mi.setDirection(direction);                         
                            mi.setLocation(location);
                            
                            ACLMessage informBrokerMsg = new ACLMessage(ACLMessage.INFORM);
                            informBrokerMsg.setOntology(ontology.getName());
                            informBrokerMsg.setLanguage(codec.getName());
                            informBrokerMsg.addReceiver(results[j].getName());
                            informBrokerMsg.setProtocol(ontology.PROTOCOL_MOVE_INFO);
                            getContentManager().fillContent(informBrokerMsg, new Action(getAID(), mi));
                            System.out.println(myAgent.getLocalName() + " sending direction to Broker");
                            send(informBrokerMsg);                           
                        }

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
    		@Override
            public boolean done() {
                return true;
            }
    		
    	});
    }
    //Behavior cancel
    private void cancel(){
    	addBehaviour(new OneShotBehaviour(this){

			@Override
			public void action() {
				
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
    }
    private void setX(int x){
        direction.setX(x);
    }

    private int getX(){
        return direction.getX();
    }

}