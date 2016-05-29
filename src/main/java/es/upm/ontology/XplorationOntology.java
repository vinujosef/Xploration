// file: XplorationOntology.java generated by ontology bean generator.  DO NOT EDIT, UNLESS YOU ARE REALLY SURE WHAT YOU ARE DOING!
package es.upm.ontology;

import jade.content.onto.*;
import jade.content.schema.*;
import jade.util.leap.HashMap;
import jade.content.lang.Codec;
import jade.core.CaseInsensitiveString;

/** file: XplorationOntology.java
 * @author ontology bean generator
 * @version 2016/05/26, 14:02:25
 */
public class XplorationOntology extends jade.content.onto.Ontology  {
  //NAME
  public static final String ONTOLOGY_NAME = "xploration";
  // The singleton instance of this ontology
  private static ReflectiveIntrospector introspect = new ReflectiveIntrospector();
  private static Ontology theInstance = new XplorationOntology();
  public static Ontology getInstance() {
     return theInstance;
  }


   // VOCABULARY
    public static final String REGISTERAGENTS_ROVER="rover";
    public static final String REGISTERAGENTS_CAPSULE="capsule";
    public static final String REGISTERAGENTS="RegisterAgents";
    public static final String REQUESTROVERMOVEMENT_DIRECTION="direction";
    public static final String REQUESTROVERMOVEMENT="RequestRoverMovement";
    public static final String REGISTRATIONREQUEST_COMPANY="company";
    public static final String REGISTRATIONREQUEST="RegistrationRequest";
    public static final String RELEASECAPSULE_LOCATION="location";
    public static final String RELEASECAPSULE="ReleaseCapsule";
    public static final String MINERALRESULT_MINERAL="mineral";
    public static final String MINERALRESULT="MineralResult";
    public static final String MOVEINFORMATION_ROVER="rover";
    public static final String MOVEINFORMATION_DIRECTION="direction";
    public static final String MOVEINFORMATION_LOCATION="location";
    public static final String MOVEINFORMATION="MoveInformation";
    public static final String FINDINGSMESSAGE_FINDINGS="findings";
    public static final String FINDINGSMESSAGE_FREQUENCY="frequency";
    public static final String FINDINGSMESSAGE="FindingsMessage";
    public static final String FINDINGS_FINDING="finding";
    public static final String FINDINGS="Findings";
    public static final String PROTOCOL_ANALYZE_MINERAL="PROTOCOL_ANALYZE_MINERAL";
    public static final String PROTOCOL_UPDATE_FINDINGS="PROTOCOL_UPDATE_FINDINGS";
    public static final String ROVER_ROVER_AGENT="rover_agent";
    public static final String ROVER_NAME="name";
    public static final String ROVER="Rover";
    public static final String PROTOCOL_MOVE_INFO="PROTOCOL_MOVE_INFO";
    public static final String FREQUENCY_CHANNEL="channel";
    public static final String FREQUENCY="Frequency";
    public static final String PROTOCOL_ROVER_MOVEMENT="PROTOCOL_ROVER_MOVEMENT";
    public static final String MINERAL_TYPE="type";
    public static final String MINERAL="Mineral";
    public static final String PROTOCOL_RELEASE_CAPSULE="PROTOCOL_RELEASE_CAPSULE";
    public static final String DIRECTION_X="x";
    public static final String DIRECTION="Direction";
    public static final String COMPANY_CAPSULE="capsule";
    public static final String COMPANY_NAME="name";
    public static final String COMPANY_COMPANY_AGENT="company_agent";
    public static final String COMPANY="Company";
    public static final String LOCATION_Y="Y";
    public static final String LOCATION_X="X";
    public static final String LOCATION="Location";
    public static final String PROTOCOL_REGISTRATION="PROTOCOL_REGISTRATION";
    public static final String PROTOCOL_SEND_FINDINGS="PROTOCOL_SEND_FINDINGS";
    public static final String CAPSULE_ROVER="rover";
    public static final String CAPSULE_CAPSULE_AGENT="capsule_agent";
    public static final String CAPSULE_NAME="name";
    public static final String CAPSULE="Capsule";
    public static final String FINDING_MINERAL="mineral";
    public static final String FINDING_LOCATION="location";
    public static final String FINDING="Finding";
    public static final String PROTOCOL="Protocol";

  /**
   * Constructor
  */
  private XplorationOntology(){ 
    super(ONTOLOGY_NAME, BasicOntology.getInstance());
    try { 

    // adding Concept(s)
    ConceptSchema protocolSchema = new ConceptSchema(PROTOCOL);
    add(protocolSchema, es.upm.ontology.Protocol.class);
    ConceptSchema findingSchema = new ConceptSchema(FINDING);
    add(findingSchema, es.upm.ontology.Finding.class);
    ConceptSchema capsuleSchema = new ConceptSchema(CAPSULE);
    add(capsuleSchema, es.upm.ontology.Capsule.class);
    ConceptSchema protocoL_senD_findingsSchema = new ConceptSchema(PROTOCOL_SEND_FINDINGS);
    add(protocoL_senD_findingsSchema, es.upm.ontology.PROTOCOL_SEND_FINDINGS.class);
    ConceptSchema protocoL_registrationSchema = new ConceptSchema(PROTOCOL_REGISTRATION);
    add(protocoL_registrationSchema, es.upm.ontology.PROTOCOL_REGISTRATION.class);
    ConceptSchema locationSchema = new ConceptSchema(LOCATION);
    add(locationSchema, es.upm.ontology.Location.class);
    ConceptSchema companySchema = new ConceptSchema(COMPANY);
    add(companySchema, es.upm.ontology.Company.class);
    ConceptSchema directionSchema = new ConceptSchema(DIRECTION);
    add(directionSchema, es.upm.ontology.Direction.class);
    ConceptSchema protocoL_releasE_capsuleSchema = new ConceptSchema(PROTOCOL_RELEASE_CAPSULE);
    add(protocoL_releasE_capsuleSchema, es.upm.ontology.PROTOCOL_RELEASE_CAPSULE.class);
    ConceptSchema mineralSchema = new ConceptSchema(MINERAL);
    add(mineralSchema, es.upm.ontology.Mineral.class);
    ConceptSchema protocoL_roveR_movementSchema = new ConceptSchema(PROTOCOL_ROVER_MOVEMENT);
    add(protocoL_roveR_movementSchema, es.upm.ontology.PROTOCOL_ROVER_MOVEMENT.class);
    ConceptSchema frequencySchema = new ConceptSchema(FREQUENCY);
    add(frequencySchema, es.upm.ontology.Frequency.class);
    ConceptSchema protocoL_movE_infoSchema = new ConceptSchema(PROTOCOL_MOVE_INFO);
    add(protocoL_movE_infoSchema, es.upm.ontology.PROTOCOL_MOVE_INFO.class);
    ConceptSchema roverSchema = new ConceptSchema(ROVER);
    add(roverSchema, es.upm.ontology.Rover.class);
    ConceptSchema protocoL_updatE_findingsSchema = new ConceptSchema(PROTOCOL_UPDATE_FINDINGS);
    add(protocoL_updatE_findingsSchema, es.upm.ontology.PROTOCOL_UPDATE_FINDINGS.class);
    ConceptSchema protocoL_analyzE_mineralSchema = new ConceptSchema(PROTOCOL_ANALYZE_MINERAL);
    add(protocoL_analyzE_mineralSchema, es.upm.ontology.PROTOCOL_ANALYZE_MINERAL.class);
    ConceptSchema findingsSchema = new ConceptSchema(FINDINGS);
    add(findingsSchema, es.upm.ontology.Findings.class);

    // adding AgentAction(s)
    AgentActionSchema findingsMessageSchema = new AgentActionSchema(FINDINGSMESSAGE);
    add(findingsMessageSchema, es.upm.ontology.FindingsMessage.class);
    AgentActionSchema moveInformationSchema = new AgentActionSchema(MOVEINFORMATION);
    add(moveInformationSchema, es.upm.ontology.MoveInformation.class);
    AgentActionSchema mineralResultSchema = new AgentActionSchema(MINERALRESULT);
    add(mineralResultSchema, es.upm.ontology.MineralResult.class);
    AgentActionSchema releaseCapsuleSchema = new AgentActionSchema(RELEASECAPSULE);
    add(releaseCapsuleSchema, es.upm.ontology.ReleaseCapsule.class);
    AgentActionSchema registrationRequestSchema = new AgentActionSchema(REGISTRATIONREQUEST);
    add(registrationRequestSchema, es.upm.ontology.RegistrationRequest.class);
    AgentActionSchema requestRoverMovementSchema = new AgentActionSchema(REQUESTROVERMOVEMENT);
    add(requestRoverMovementSchema, es.upm.ontology.RequestRoverMovement.class);
    AgentActionSchema registerAgentsSchema = new AgentActionSchema(REGISTERAGENTS);
    add(registerAgentsSchema, es.upm.ontology.RegisterAgents.class);

    // adding AID(s)

    // adding Predicate(s)


    // adding fields
    findingSchema.add(FINDING_LOCATION, locationSchema, ObjectSchema.MANDATORY);
    findingSchema.add(FINDING_MINERAL, mineralSchema, ObjectSchema.OPTIONAL);
    capsuleSchema.add(CAPSULE_NAME, (TermSchema)getSchema(BasicOntology.STRING), ObjectSchema.MANDATORY);
    capsuleSchema.add(CAPSULE_CAPSULE_AGENT, (ConceptSchema)getSchema(BasicOntology.AID), ObjectSchema.OPTIONAL);
    capsuleSchema.add(CAPSULE_ROVER, roverSchema, ObjectSchema.MANDATORY);
    locationSchema.add(LOCATION_X, (TermSchema)getSchema(BasicOntology.INTEGER), ObjectSchema.OPTIONAL);
    locationSchema.add(LOCATION_Y, (TermSchema)getSchema(BasicOntology.INTEGER), ObjectSchema.OPTIONAL);
    companySchema.add(COMPANY_COMPANY_AGENT, (ConceptSchema)getSchema(BasicOntology.AID), ObjectSchema.OPTIONAL);
    companySchema.add(COMPANY_NAME, (TermSchema)getSchema(BasicOntology.STRING), ObjectSchema.MANDATORY);
    companySchema.add(COMPANY_CAPSULE, capsuleSchema, ObjectSchema.MANDATORY);
    directionSchema.add(DIRECTION_X, (TermSchema)getSchema(BasicOntology.INTEGER), ObjectSchema.MANDATORY);
    mineralSchema.add(MINERAL_TYPE, (TermSchema)getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
    frequencySchema.add(FREQUENCY_CHANNEL, (TermSchema)getSchema(BasicOntology.INTEGER), ObjectSchema.MANDATORY);
    roverSchema.add(ROVER_NAME, (TermSchema)getSchema(BasicOntology.STRING), ObjectSchema.MANDATORY);
    roverSchema.add(ROVER_ROVER_AGENT, (ConceptSchema)getSchema(BasicOntology.AID), ObjectSchema.OPTIONAL);
    findingsSchema.add(FINDINGS_FINDING, findingSchema, 0, ObjectSchema.UNLIMITED);
    findingsMessageSchema.add(FINDINGSMESSAGE_FREQUENCY, frequencySchema, ObjectSchema.OPTIONAL);
    findingsMessageSchema.add(FINDINGSMESSAGE_FINDINGS, findingsSchema, ObjectSchema.MANDATORY);
    moveInformationSchema.add(MOVEINFORMATION_LOCATION, locationSchema, ObjectSchema.MANDATORY);
    moveInformationSchema.add(MOVEINFORMATION_DIRECTION, directionSchema, ObjectSchema.MANDATORY);
    moveInformationSchema.add(MOVEINFORMATION_ROVER, roverSchema, ObjectSchema.MANDATORY);
    mineralResultSchema.add(MINERALRESULT_MINERAL, mineralSchema, ObjectSchema.OPTIONAL);
    releaseCapsuleSchema.add(RELEASECAPSULE_LOCATION, locationSchema, ObjectSchema.MANDATORY);
    registrationRequestSchema.add(REGISTRATIONREQUEST_COMPANY, (TermSchema)getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
    requestRoverMovementSchema.add(REQUESTROVERMOVEMENT_DIRECTION, directionSchema, ObjectSchema.MANDATORY);
    registerAgentsSchema.add(REGISTERAGENTS_CAPSULE, capsuleSchema, ObjectSchema.MANDATORY);
    registerAgentsSchema.add(REGISTERAGENTS_ROVER, roverSchema, ObjectSchema.MANDATORY);

    // adding name mappings

    // adding inheritance
    protocoL_senD_findingsSchema.addSuperSchema(protocolSchema);
    protocoL_registrationSchema.addSuperSchema(protocolSchema);
    protocoL_releasE_capsuleSchema.addSuperSchema(protocolSchema);
    protocoL_roveR_movementSchema.addSuperSchema(protocolSchema);
    protocoL_movE_infoSchema.addSuperSchema(protocolSchema);
    protocoL_updatE_findingsSchema.addSuperSchema(protocolSchema);
    protocoL_analyzE_mineralSchema.addSuperSchema(protocolSchema);

   }catch (java.lang.Exception e) {e.printStackTrace();}
  }
  }
