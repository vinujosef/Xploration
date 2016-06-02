package es.upm.platform05;

import es.upm.ontology.RegistrationRequest;
import es.upm.ontology.XplorationOntology;
import es.upm.ontology.Direction;
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
    HashMap<AID, Location> roversLocations = Spacecraft.roversLocations;
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
        sd.setType(ontology.PROTOCOL_SEND_FINDINGS);
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
                            
                            ArrayList<AID> AgentList = new ArrayList<AID>();
                            AgentList = inRange(mi.getLocation());
                            
                            if(roversWhereabout.contains(msg.getSender())){
                            	roversWhereabout.remove(msg.getSender());
                            	roversWhereabout.add(mi);
                            	
                            	
                            }
                            else{
                            	
                            	roversWhereabout.add(mi);
                            	
                            	
                            }
                            ACLMessage msg2 = new ACLMessage(ACLMessage.INFORM);
                            msg2.setOntology(ontology.getName());
                            msg2.setLanguage(codec.getName());
                            //Should be calculate and identify it's the rover in range
                            for(int i = 0; i < AgentList.size(); i++){
                            	msg2.addReceiver(AgentList.get(i));
                                msg2.setProtocol(ontology.PROTOCOL_MOVE_INFO);
                                getContentManager().fillContent(msg2, new Action(getAID(), mi));
                                send(msg2);
                                System.out.println(myAgent.getLocalName() + " sending communicate information to " + AgentList.get(i));
                            }
                            
                            //change to: check if any rover in range, and should create a new Behavior!!!!!!
                            /*
                            int a = new java.util.Random().nextInt(2);
                            /
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
                            */
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
                	else if(msg.getProtocol() == ontology.PROTOCOL_SEND_FINDINGS){ 
                		System.out.println(myAgent.getLocalName() + " NOT IN RANGE ");
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
    
    protected int judgeX(int x)
    {
    	switch(x){
  		case -3: x = 7; break;
  		case -2: x = 8; break;
  		case -1: x = 9; break;
  		case 0: x = 10; break;
  		case 11: x = 1; break;
  		case 12: x = 2; break;
  		case 13: x = 3; break;
  		case 14: x = 4; break;
  		}
    	return x;
    }
    protected int judgeY(int y)
    {
    	switch(y){
  		case -3: y = 7; break;
  		case -2: y = 8; break;
  		case -1: y = 9; break;
  		case 0: y = 10; break;
  		case 11: y = 1; break;
  		case 12: y = 2; break;
  		case 13: y = 3; break;
  		case 14: y = 4; break;
  		}
    	return y;
    }
    
    protected ArrayList<AID> inRange(Location loc){
  		int x = loc.getX();
  		int y = loc.getY();
  		Location[] locationRange = new Location[18];
  		for(int i = 0; i < 18; i++){
  			locationRange[i] = loc;
  		}
  		
  		
  		
  		
  		locationRange[0].setX(judgeX(x - 2)); locationRange[0].setY(y);
  		locationRange[1].setX(judgeX(x - 1)); locationRange[1].setY(judgeY(y + 1));
  		locationRange[2].setX(judgeX(x + 1)); locationRange[2].setY(judgeY(y + 1));
  		locationRange[3].setX(judgeX(x + 2)); locationRange[3].setY(y);
  		locationRange[4].setX(judgeX(x + 1)); locationRange[4].setY(judgeY(y - 1));
  		locationRange[5].setX(judgeX(x - 1)); locationRange[5].setY(judgeY(y - 1));
  		locationRange[6].setX(judgeX(x - 4)); locationRange[6].setY(y);
  		locationRange[7].setX(judgeX(x - 3)); locationRange[7].setY(judgeY(y + 1));
  		locationRange[8].setX(judgeX(x - 2)); locationRange[8].setY(judgeY(y + 2));
  		locationRange[9].setX(x); locationRange[9].setY(judgeY(y + 2));
  		locationRange[10].setX(judgeX(x + 2)); locationRange[10].setY(judgeY(y + 2));
  		locationRange[11].setX(judgeX(x + 3)); locationRange[11].setY(judgeY(y + 1));
  		locationRange[12].setX(judgeX(x + 4)); locationRange[12].setY(y);
  		locationRange[13].setX(judgeX(x + 3)); locationRange[13].setY(judgeY(y - 1));
  		locationRange[14].setX(judgeX(x + 2)); locationRange[14].setY(judgeY(y - 2));
  		locationRange[15].setX(x); locationRange[15].setY(judgeY(y - 2));
  		locationRange[16].setX(judgeX(x - 2)); locationRange[16].setY(judgeY(y - 2));
  		locationRange[17].setX(judgeX(x - 3)); locationRange[17].setY(judgeY(y - 1));
  		
  		ArrayList<AID> agentList = new ArrayList<AID>();
  		
  		for(AID aid: roversLocations.keySet()){
			for(int i = 0; i < 18; i++){
				if(roversLocations.get(aid).equals(locationRange[i])){					
					agentList.add(aid);
					break;
				}
				else{
					continue;
					}
			}
		}
  		

  		return agentList;
  		
  	}
	
}
