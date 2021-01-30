/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.core.AID;

/**
 *
 * @author GATES
 */
public class CyClicAndStepTemplate extends Agent {
    
    protected void setup() {
        
        addBehaviour(new CyclicBehaviour(this) {
        
            int step = 1;
            
            public void action() {
                
                switch (step) {
                    
                    case 1: //starting operation - sending msg to other agent - like PersonalAgent
                        
                        /*
                        try {
                            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                            msg.setContentObject(ti);
                            msg.addReceiver(new AID( "TA", AID.ISLOCALNAME) );
                            
                            step = 2;
                            send(msg);
                        } catch (Exception ex) {}
                        */
                        
                    case 2: //waiting for msg from other agent - Like BankAgent and TicketAgent
                        
                        ACLMessage msg = receive();
                        
                        if (msg != null) {
                            
                            //do something with message
                            
                        } else
                            block();
                }
            }
        });
    }
}
