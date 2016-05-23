package es.upm.company05;

import es.upm.ontology.RegistrationRequest;
import es.upm.ontology.ReleaseCapsule;
import es.upm.ontology.XplorationOntology;
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

import java.util.logging.Level;
import java.util.logging.Logger;

public class Capsule extends Agent {
	
	private static final long serialVersionUID =1L;
    XplorationOntology ontology = (XplorationOntology) XplorationOntology.getInstance();
    Codec codec = new SLCodec();
	
	protected void setup(){
		System.out.println(getLocalName()+ " has entered into the system");
        getContentManager().registerOntology(ontology);
        getContentManager().registerLanguage(codec);
        
        // release rovers
        addBehaviour(new SimpleBehaviour(this)
        {
            @Override
            public void action() {
                try {
                	System.out.println(myAgent.getLocalName() + " is releasing a rover.");
					releaseRover();
				} catch (ControllerException e) {
					e.printStackTrace();
				}
                finally{
                	block();
                }
            }

            @Override
            public boolean done() {
                return false;
            }
        });      
	}
	

	protected void releaseRover() throws ControllerException{
		Runtime rt = Runtime.instance();
		Profile profile = new ProfileImpl(null, 1200, null);
		ContainerController cc = rt.createAgentContainer(profile);
		Object obj = new Object();
		Object agentObj[] = new Object[1];
		agentObj[0] = obj;
		AgentController ac = cc.createNewAgent("Rover", "es.upm.company05.Rover", agentObj);
		ac.start();
	}

}
