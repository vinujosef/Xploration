package es.upm.company05;

import es.upm.ontology.*;
import es.upm.ontology.RequestRoverMovement;
import es.upm.ontology.XplorationOntology;
import es.upm.platform05.*;
import es.upm.ontology.Direction;
import es.upm.ontology.Location;
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
    Finding finding = new Finding();
    Findings findings = new Findings();
    Frequency frequency = new Frequency();
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
                frequency.setChannel(5);
                System.out.println("-------------------------------------");
            }
        });


        // Inform World to Move
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
        BEHAVIOUR: when receiving AGREE of Move
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
                    else if(msg.getProtocol() == ontology.PROTOCOL_ANALYZE_MINERAL){
                        System.out.println(myAgent.getLocalName() + " got AGREE from "+ (msg.getSender()).getLocalName());
                        //get agree, wake the agent to run
                        doWake();
                        //let agent wait again, not to send move request
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
                        /*
                         * Finially change the location information on Rover agent it self
                         */
                        Location nLoc = new Location();

                        nLoc = CaculateLocation(location,direction);

                        location.setX(nLoc.getX());
                        location.setY(nLoc.getY());
                        /*
                         * Then start to analyze immediately
                         */

                        System.out.println("------------------------");
                        System.out.println("Start to analyze!!!");
                        ACLMessage msg2 = new ACLMessage(ACLMessage.REQUEST);
                        msg2.setOntology(ontology.getName());
                        msg2.setLanguage(codec.getName());
                        msg2.addReceiver(msg.getSender());

                        msg2.setProtocol(ontology.PROTOCOL_ANALYZE_MINERAL);
                        send(msg2);

                        System.out.println(myAgent.getLocalName() + " sending mineral REQUEST to WORLD");
                        //wait until analyze finished, line 213 wake agent up first time
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

                            finding.setLocation(location);
                            finding.setMineral(mr.getMineral());
                            findings.addFinding(finding);


                            //AskforUpdate(findings);
                            DFAgentDescription dfd = new DFAgentDescription();
                            ServiceDescription sd = new ServiceDescription();
                            sd.setType("Broker");
                            dfd.addServices(sd);
                            DFAgentDescription[] results = new DFAgentDescription[20];
                            try {
                                results = DFService.search(myAgent,dfd);
                                for(int i=0;i<results.length;i++){
                                    if(results[i]==null){
                                        break;
                                    }
                                    ACLMessage msg2 = new ACLMessage(ACLMessage.INFORM);

                                    msg2.setOntology(ontology.getName());
                                    msg2.setLanguage(codec.getName());
                                    msg2.addReceiver(results[i].getName());
                                    msg2.setProtocol(ontology.PROTOCOL_SEND_FINDINGS);
                                    FindingsMessage fm = new FindingsMessage();
                                    fm.setFindings(findings);
                                    fm.setFrequency(frequency);

                                    getContentManager().fillContent(msg2, new Action(getAID(), fm));
                                    send(msg2);

                                    System.out.println(myAgent.getLocalName() + " sending Findings to "+results[i].getName().getLocalName());
                                    break;


                                }



                            } catch (FIPAException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (CodecException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (OntologyException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }



                            //After all the work, have a rest for 1 s
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }


                            System.out.println("-------------------------------------");
                            doWake();

                        } catch (CodecException | OntologyException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                    /*
                     * Receive Move information from Broker
                     */

                    else if(msg.getProtocol() == ontology.PROTOCOL_MOVE_INFO){
                        ContentElement ce;
                        try {
                            ce = (Action) getContentManager().extractContent(msg);
                            MoveInformation mi = (MoveInformation) ((Action)ce).getAction();
                            System.out.println(myAgent.getLocalName() + " received an INFORM from "+ (msg.getSender()).getLocalName() + ". There is a rover at location (" + mi.getLocation().getX() + ", " + mi.getLocation().getY() + "), and moving towards direction " + mi.getDirection().getX());

                            //Calculate if they can crash of not
                            if(CrashDetect(location,direction,mi)){

                                cancel();
                                Thread.sleep(1000);
                                move();
                            }

                            else
                                System.out.println((msg.getSender()).getLocalName() + " working well with Rover05");

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


    //Movement activity
    private void move(){
        addBehaviour(new SimpleBehaviour(this)
        {

            @Override
            public void action() {

                setX(new java.util.Random().nextInt(6 - 1 + 1) + 1);
                DFAgentDescription dfd = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("World");
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
                        sd.setType("Broker");
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
                sd.setType("World");
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

    //Calculate the new location
    protected Location CaculateLocation(Location loc, Direction dir){
        int x = loc.getX();
        int y = loc.getY();
        int d = dir.getX();
        Location newLoc = new Location();
        switch(d){
            case 1: x = x - 2; break;
            case 2: x = x - 1 ; y = y + 1; break;
            case 3: x = x + 1 ; y = y + 1; break;
            case 4: x = x + 2; break;
            case 5: x = x + 1 ; y = y - 1; break;
            case 6: x = x - 1 ; y = y - 1; break;
        }
        switch(x){
            case -1: x = 9; break;
            case 0: x = 10; break;
            case 11: x = 1; break;
            case 12: x = 2; break;
        }
        switch(y){
            case -1: y = 9; break;
            case 0: y = 10; break;
            case 11: y = 1; break;
            case 12: y = 2; break;
        }
        newLoc.setX(x);
        newLoc.setY(y);
        return newLoc;

    }


    //Detect can crash or not
    protected boolean CrashDetect(Location loc, Direction dir, MoveInformation mi){
    	/*
  		int x1 = loc.getX();
  		int y1 = loc.getY();
  		int d1 = dir.getX();
  		int x2 = mi.getLocation().getX();
  		int y2 = mi.getLocation().getY();
  		int d2 = mi.getDirection().getX();
  		*/
        Location l1 = new Location();
        Location l2 = new Location();
        l1 = CaculateLocation(loc, dir);
        l2 = CaculateLocation(mi.getLocation(), mi.getDirection());
        if(l1.getX() == l2.getX() && l1.getY() == l2.getY()) return true;
        else return false;

    }



    private void setX(int x){
        direction.setX(x);
    }

    private int getX(){
        return direction.getX();
    }

}