/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Company;

import static com.sun.org.apache.xpath.internal.axes.HasPositionalPredChecker.check;
import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import Spacecraft.Spacecraft;

/**
 *
 * @author Vinu
 */

public class Company extends Agent{
	protected void setup(){
		System.out.println(getAID().getName()+" is ready.");
		
		String companyName = "Company05";
		
		// Assuming we know the Agent ID of the Spacecraft, let say the ID is SC01
		
		Spacecraft sc01 = new Spacecraft();
		
		// Create message to send to Spacecraft
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setContent("Registration request by " + companyName);
		msg.addReceiver(sc01.getAID()); // add Agent (Spacecraft)
		msg.setSender(getAID());
		msg.setLanguage("English");
		msg.setOntology("Xploration-Ontology"); // We fix this when we know the ontology
		send(msg);
		
		addBehaviour(new CyclicBehaviour(this) 
        {
             public void action() 
             {
                ACLMessage msg = receive();
                if (msg != null){
                    System.out.println(msg.getContent());
                }
                else
                	block();
             }
        });
		
	}
	
}

/*
public class Company extends Agent{
       
    private static final long serialVersionUID =1L;
    public final static String REGISTRAION = "Registration"; 
    
    protected void setup(){
        System.out.println(getLocalName()+ ": has entered into the system");
        
//     BEHAVIOUR
//     Finds the spacecraft and make a request for registration
        addBehaviour(new SimpleBehaviour(this)
        {
            @Override
            public void action() {
//                System.out.println("Searching for agent.");
                DFAgentDescription dfd = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("Spacecraft");
                dfd.addServices(sd);
                DFAgentDescription[] results = new DFAgentDescription[20];
                try {
                    results = DFService.search(myAgent,dfd);
//                    System.out.println("Found " + results.length);
                    for(int i=0;i<results.length;i++){
                        if(results[i]==null){
                            break;
                        }
//                        System.out.println("Sending message to " + results[i].getName());
                        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                        msg.addReceiver(results[i].getName());
                        msg.setContent("Company6");
                        send(msg);
                   }
                } 
                
                catch (FIPAException ex) {
                    Logger.getLogger(Company.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public boolean done() {
                return true;
                }       
        });
        
        addBehaviour(new SimpleBehaviour(this)
        {
            @Override
            public void action() {
                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.REFUSE));
                if(msg == null) return;
                System.out.println("Company has got refused from Spacecraft");
            }

            @Override
            public boolean done() {
                return true;
            }
        });
        
        addBehaviour(new SimpleBehaviour(this)
        {
            @Override
            public void action() {
                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.AGREE));
                if(msg == null) return;
                System.out.println("Company has got ACCEPTED to the Spacecraft.");
            }

            @Override
            public boolean done() {
                return true;
            }
        });
        
        addBehaviour(new SimpleBehaviour(this)
        {
            @Override
            public void action() {
                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.NOT_UNDERSTOOD));
                if(msg == null) return;
                System.out.println("Company has got NOT UNDERSTOOD message from Spacecraft.");
            }

            @Override
            public boolean done() {
                return true;
            }
        });
        
        addBehaviour(new SimpleBehaviour(this)
        {
            @Override
            public void action() {
                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.FAILURE));
                if(msg == null) return;
                System.out.println("Company has got FAILURE message from Spacecraft.");
            }

            @Override
            public boolean done() {
                return true;
            }
        });
        
        addBehaviour(new SimpleBehaviour(this)
        {
            @Override
            public void action() {
                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.CONFIRM));
                if(msg == null) return;
                System.out.println("Company has got CONFIRM message from Spacecraft.");
            }

            @Override
            public boolean done() {
                return true;
            }
        });
        
    }
}
*/