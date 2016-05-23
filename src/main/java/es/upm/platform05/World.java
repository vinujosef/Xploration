package es.upm.platform05;

import es.upm.platform05.Spacecraft;
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
	Mineral mineral = new Mineral();
	HashMap<AID, Direction> roversDirections = new HashMap<AID, Direction>();
	HashMap<AID, Location> roversLocations = Spacecraft.roversLocations;
	HashMap<AID, Mineral> mineralResults = new HashMap<AID, Mineral>();

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
		DateTime movetime = DateTime.now().plusSeconds(5);

		addBehaviour(new CyclicBehaviour(this){

			@Override
			public void action() {
				msg = receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));

				if(msg != null){

					if(msg.getProtocol().equals(ontology.PROTOCOL_ROVER_MOVEMENT)){
						ACLMessage reply = msg.createReply();
						System.out.println(myAgent.getLocalName()+" received new direction from "+ (msg.getSender()).getLocalName());
						if(movetime.isAfterNow())
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
									Thread.sleep(5000);
								} catch (Codec.CodecException e) {
									e.printStackTrace();
								} catch (OntologyException e) {
									e.printStackTrace();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
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
								ACLMessage reply2 = msg.createReply();
								if(failure){
									reply2.setPerformative(ACLMessage.FAILURE);
									myAgent.send(reply2);
									System.out.println(myAgent.getLocalName() + " sent an FAILURE to " + (msg.getSender()).getLocalName());
								}
								else{
									// when there's no crash
									reply2.setPerformative(ACLMessage.INFORM);
									myAgent.send(reply2);
									if(roversDirections.containsKey(msg.getSender())){
										System.out.println(myAgent.getLocalName() + " sent an INFORM of achieve to " + (msg.getSender()).getLocalName());
										roversDirections.remove(msg.getSender());
			                    		/*
			                    		 * TODO calculate new location
			                    		 */
									}
									else{
										System.out.println(myAgent.getLocalName() + " sent an INFORM of cancel to " + (msg.getSender()).getLocalName());
									}
									//remove directions after finishing moving

								}

								block();
							}
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
								Thread.sleep(15000);
							} catch (Codec.CodecException e) {
								e.printStackTrace();
							} catch (OntologyException e) {
								e.printStackTrace();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

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
							ACLMessage reply2 = msg.createReply();
							if(failure){
								//when crashed, send another reply
								reply2.setPerformative(ACLMessage.FAILURE);
								myAgent.send(reply2);
								System.out.println(myAgent.getLocalName() + " sent an FAILURE to " + (msg.getSender()).getLocalName());
							}
							else{
								// when there's no crash
								reply2.setPerformative(ACLMessage.INFORM);
								myAgent.send(reply2);
								System.out.println(myAgent.getLocalName() + " sent an INFORM to " + (msg.getSender()).getLocalName());

								//remove directions after finishing moving
								roversDirections.remove(msg.getSender());
							}
						}
					}
					else if(msg.getProtocol().equals(ontology.PROTOCOL_ANALYZE_MINERAL)){
						System.out.println(myAgent.getLocalName()+" received REQUEST from "+ (msg.getSender()).getLocalName());
						ACLMessage mineral_reply = msg.createReply();
						if(roversLocations.containsKey(msg.getSender()))
						{
							//if the rover is not crashed
							//reply -> agree
							mineral_reply.setPerformative(ACLMessage.AGREE);
							myAgent.send(mineral_reply);
							System.out.println(myAgent.getLocalName() + " sent a AGREE to " + (msg.getSender()).getLocalName());
							doWait(500);
							//and then send the mineral result inform
							int t = new java.util.Random().nextInt(6 - 1 ) + 'A';
							char ty = (char)t;
							String type = String.valueOf(ty);
							mineral.setType(type);
							ACLMessage mineralResult_reply = msg.createReply();
							MineralResult mineralResult = new MineralResult();
							mineralResult.setMineral(mineral);
							mineralResult_reply.setPerformative(ACLMessage.INFORM);
							try {
								getContentManager().fillContent(mineralResult_reply, new Action(getAID(), mineralResult));
								mineralResults.put(msg.getSender(), mineral);
								System.out.println(myAgent.getLocalName() + " sending a mineral Result "+ type + " to "+msg.getSender().getLocalName());
								doWait(500);
								send(mineralResult_reply);
							} catch (CodecException | OntologyException e) {
								e.printStackTrace();
							}
							finally{
								block();
							}

						}

						else{
							//if the rover is not crashed
							//reply -> refuse
							mineral_reply.setPerformative(ACLMessage.REFUSE);
							myAgent.send(mineral_reply);
							System.out.println(myAgent.getLocalName() + " sent a REFUSE to " + (msg.getSender()).getLocalName());
							doWait(500);
						}
					}

				}
				/*
				else{
					ACLMessage reply = msg.createReply();
					reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
	                myAgent.send(reply);
	                System.out.println(myAgent.getLocalName() + " sent a NOT UNDERSTOOD to " + (msg.getSender()).getLocalName());
					block();
				}
				*/
				else{
					block();
				}
			}

		});

		//receive cancel
		addBehaviour(new SimpleBehaviour(this)
		{
			@Override
			public void action() {
				ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.CANCEL));
				if(msg == null) return;
				else{
					if(msg.getProtocol() == ontology.PROTOCOL_ROVER_MOVEMENT){
						roversDirections.remove(msg.getSender());

						System.out.println(myAgent.getLocalName() + " romove movement of "+ (msg.getSender()).getLocalName());
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