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

		ReleaseCapsule rc = new ReleaseCapsule();

		// Release Rovers
		addBehaviour(new SimpleBehaviour(this)
		{
			@Override
			public void action() {
				try {
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
