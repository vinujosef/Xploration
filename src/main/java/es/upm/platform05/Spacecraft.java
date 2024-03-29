/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.platform05;

import es.upm.ontology.RegistrationRequest;
import es.upm.ontology.XplorationOntology;
import es.upm.ontology.FindingsMessage;
import es.upm.ontology.Location;
import es.upm.ontology.MoveInformation;
import es.upm.ontology.ReleaseCapsule;
import es.upm.ontology.*;
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
import jade.core.behaviours.OneShotBehaviour;
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import jade.core.Runtime;
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
    public final static HashMap<AID, Location> roversLocations = new HashMap<AID, Location>();
    HashMap<AID,Finding> companyFinding = new HashMap<AID, Finding>();


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
                            System.out.println("Registering " + (msg.getSender()).getLocalName());
//                            System.out.println("Registering " + regRequest.getCompany());

                            if(companyList.contains(msg.getSender())){
                                registration_reply.setPerformative(ACLMessage.FAILURE);

                                System.out.println("Failed to register " + (msg.getSender()).getLocalName() + ". Already registered.");
//                                System.out.println("Failed to register " + regRequest.getCompany() + ". Already registered.");
                            }
                            else{
                                registration_reply.setPerformative(ACLMessage.INFORM);
                                companyList.add(msg.getSender().getLocalName());
                                System.out.println("Registered successfully " + (msg.getSender()).getLocalName());
//                                for loop to check the list of companies added into the companylist array
                                for(int j=0; j < companyList.size(); j++) {
                                    System.out.println(companyList.get(j));
                                }
                                //release the capsules
                                release(msg.getSender());
                            }
                            send(registration_reply);


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


        // Receiving update inform messages from Capsule
        addBehaviour(new SimpleBehaviour(this)
        {
            @Override
            public void action() {
                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));


                if(msg == null) return;
                else{


                    if(msg.getProtocol() == ontology.PROTOCOL_UPDATE_FINDINGS){
                        System.out.println(myAgent.getLocalName() + " got FINDINGS from "+ (msg.getSender()).getLocalName());
                        Action ac;
                        try {
                            ac = (Action) getContentManager().extractContent(msg);
                            FindingsMessage fm = (FindingsMessage) ac.getAction();

                            Iterator it =  fm.getFindings().getAllFinding();
                            while(it.hasNext()){

                                Finding find = (Finding) it.next();

                                if(companyFinding.containsValue(find)) continue;
                                else {
                                    companyFinding.put(msg.getSender(), find);
                                    System.out.println(find.getMineral().getType()+" "+find.getLocation().getX()+" "+find.getLocation().getY());
                                }
                            }
                        } catch (CodecException | OntologyException e) {
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

    protected void release(AID receiver){

        Location location = new Location();
        int x=new java.util.Random().nextInt(10)+1;
        int y;
        if(x%2==0)
        {
            y = new java.util.Random().nextInt(4)*2 + 2;
        }
        else
        {
            y = new java.util.Random().nextInt(4)*2 + 1;
        }


        location.setX(x);
        location.setY(y);

        //send a message to company
        addBehaviour(new OneShotBehaviour(this)
        {
            public void action() {
                doWait(500);
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.setOntology(ontology.getName());
                msg.setLanguage(codec.getName());
                msg.addReceiver(receiver);
                msg.setProtocol(ontology.PROTOCOL_RELEASE_CAPSULE);
                ReleaseCapsule releaseCapsule = new ReleaseCapsule();
                releaseCapsule.setLocation(location);
                roversLocations.put(receiver, location);
                try {
                    getContentManager().fillContent(msg, new Action(getAID(), releaseCapsule));
                    System.out.println(myAgent.getLocalName() + " sending a message to release capsules");
                    doWait(500);
                    send(msg);
                } catch (CodecException | OntologyException e) {
                    e.printStackTrace();
                }
                finally{
                    block();
                }
            }
        });

    }

}
