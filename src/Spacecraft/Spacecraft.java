/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Spacecraft;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;
import org.joda.time.Period;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Vinu
 */
public class Spacecraft extends Agent{
    
    private static final long serialVersionUID =1L;
    public final static String SPACECRAFT = "Spacecraft"; 
    DateTime dt_now = new DateTime();
    DateTime dt_in1min = new DateTime();
    //maybe we shall discuss about the way to store the companys' names
    String[] ComN = new String[]{"","","","","","",""};
    String Name = new String();
    
    protected void setup(){
        System.out.println(getLocalName()+ ": has entered into the system");
        
        //creating description
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setName(this.getName());
        sd.setType(SPACECRAFT);
        dfd.addServices(sd);
        
        try {
        //registers description in DF
            DFService.register(this,dfd);
        } catch (FIPAException ex) {
            Logger.getLogger(Spacecraft.class.getName()).log(Level.SEVERE, null, ex);
        }
        DateTime finalcall = DateTime.now().plusMinutes(1);
//        DateTime finalcall = DateTime.now().plusSeconds(10);
        System.out.println(getLocalName()+": registered in the DF");

        dfd=null;
        sd=null;  
        
        addBehaviour(new CyclicBehaviour(this)
        {
            private static final long serialVersionUID =1L;
            public void action() {
                
                //Receiving request message from Company
                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
                if(msg!=null){
                    //If a request message is received from Company
                    System.out.println(myAgent.getLocalName()+": received request from "+ (msg.getSender()).getLocalName());
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
                        Name = (msg.getSender()).getLocalName();
                    	Pattern pattern = Pattern.compile(Name);
                    	int i;
                    	for(i=0;i<7;i++){
                    		Matcher matcher = pattern.matcher(ComN[i]);
                    		//reply->failure
                    		if(matcher.matches())
                    		{
                    			registration_reply.setPerformative(ACLMessage.FAILURE);
                    			myAgent.send(registration_reply);
                                System.out.println(myAgent.getLocalName() + " sent a FAILURE to " + (msg.getSender()).getLocalName());
                                break;
                    		}
                    	}
                        //reply ->accept
                        if(i==7)
                        {
                            registration_reply.setPerformative(ACLMessage.AGREE);
                            myAgent.send(registration_reply);
                            System.out.println(myAgent.getLocalName() + " sent a AGREE to " + (msg.getSender()).getLocalName());
                        }
                    }
                }
                
                else{
                    // If not message arrives
                    block();
                }
            }
         
            
           
            
        });
        
        
    }
}

