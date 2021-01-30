/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import data.Ticket;
import data.TicketInfo;
import data.PaymentInfo;
import jade.core.AID;
import java.util.ArrayList;

/**
 *
 * @author GATES
 * TicketingAgent => TA
 */
public class TicketingAgent extends Agent {
    
    ArrayList tickets = new ArrayList();    
    String bankAccountNo = "1234567";
    
    protected void setup() {
        
        addBehaviour(new CyclicBehaviour(this) {
            
            int step = 1;
            
            public void action() {
                
                switch (step) {
                    
                    case 1: //received TicketInfo object from PA
                        
                        ACLMessage msg = receive();
                        
                        if (msg != null) {
                            
                            System.out.println("TA: Executing Step-1");
                            System.out.println("");
    
                            try {
                                TicketInfo ti = (TicketInfo)msg.getContentObject();
                                System.out.println("TA: Received ticket info request from PA");
                                System.out.println("TA: Location: " + ti.getLocation());
                                System.out.println("TA: Destination: " + ti.getDestination());
                                System.out.println("TA: Time: " + ti.getTime());                                
                                System.out.println("");

                                ArrayList result = findTicket(ti);

                                ACLMessage reply = msg.createReply();

                                if (result.size() > 0) {

                                    step = 2;

                                    System.out.println("TA: Ticket available");
                                    System.out.println("");
                                    reply.setPerformative(ACLMessage.INFORM);
                                    reply.setContentObject(result);
                                    send(reply); 
                                }
                                else {
                                    System.out.println("TA: Ticket not available");
                                    System.out.println("");

                                    step = 1;

                                    reply.setPerformative( ACLMessage.REFUSE);                                    
                                    ti.setAvailable(false);
                                    reply.setContentObject(tickets);
                                    send(reply); 
                                }
                            
                            } catch (Exception ex) {}                        
                            
                        } else
                            block();
                        
                    case 2: //received credit info from BA - BA-step2 - PaymentInfo    
                        
                        ACLMessage msg2 = receive();
                        
                        if (msg2 != null) {
                            
                            System.out.println("TA: Executing Step-2");
                            System.out.println("");
                            
                            try {
                                PaymentInfo pi = (PaymentInfo)msg2.getContentObject();
                                System.out.println("TA: Received payment info from BA (credit)");
                                System.out.println("TA: Ticket ID: " + pi.getTicketId());
                                System.out.println("TA: Amount: " + pi.getAmount()); 
                                System.out.println("TA: Previous Balance: " + pi.getSellerPreviousBalance()); 
                                System.out.println("TA: Current Balance: " + pi.getSellerCurrentBalance()); 
                                System.out.println("");
                                
                                //set ticket to sold - status 2
                                System.out.println("TA: set ticket status to sold"); 
                                System.out.println("TA: Ticket ID: " + pi.getTicketId());
                                System.out.println("");
                                setTicketStatusToSold(pi.getTicketId());
                                
                                //send inform to PA - ticket selling success
                                System.out.println("TA: Send message inform to PA - ticket selling success!");
                                System.out.println("");
                                ACLMessage msg3 = new ACLMessage(ACLMessage.INFORM);
                                msg3.setContentObject(pi);
                                msg3.addReceiver(new AID( "PA", AID.ISLOCALNAME) );
                                send(msg3);
                                
                            } catch (Exception ex) {}  
                            
                        } else
                            block();
                }
            }           
        });
        
        //create ticket using Ticket object and put into arraylist
        //ticket for sell list
        Ticket ticket = new Ticket();
        ticket.setId("ID0000001");
        ticket.setLocation("Johor Bahru");
        ticket.setDestination("Kota Bharu");
        ticket.setCompany("Mutiara Express");
        ticket.setPrice(65.50);
        ticket.setTime("9.00 AM");
        ticket.setSeatNo(1);        
        ticket.setStatus(1);
        ticket.setBankAccountNo(bankAccountNo);
        
        //add to arraylist
        tickets.add(ticket);
        
        ticket = new Ticket();
        ticket.setId("ID0000002");
        ticket.setLocation("Johor Bahru");
        ticket.setDestination("Kota Tinggi");
        ticket.setCompany("City Express");
        ticket.setPrice(14.50);
        ticket.setTime("10.00 AM");
        ticket.setSeatNo(1);        
        ticket.setStatus(1);
        ticket.setBankAccountNo(bankAccountNo);
        
        //add to arraylist
        tickets.add(ticket);
    } 
    
    protected ArrayList findTicket(TicketInfo ti) {
        ArrayList result = new ArrayList();
        
        String location = ti.getLocation();
        String destination = ti.getDestination();
        String time = ti.getTime();
        
        for(int i=0; i<tickets.size(); i++) {
            Ticket ticket = (Ticket)tickets.get(i);
           
           
            if (ticket.getLocation().equals(location) && 
                    ticket.getDestination().equals(destination) && 
                        ticket.getTime().equals(time) &&
                            ticket.getStatus() == 1) {
                
                System.out.println("TA: Ticket found");
                System.out.println("TA: ID: " + ticket.getId());
                System.out.println("TA: Destination: " + ticket.getDestination());
                System.out.println("TA: Location: " + ticket.getLocation());
                System.out.println("TA: Company: " + ticket.getCompany());
                System.out.println("TA: Time: " + ticket.getTime());
                System.out.println("TA: Seat No: " + ticket.getSeatNo());
                System.out.println("TA: Price: " + ticket.getPrice());
                System.out.println("");
                result.add(ticket);
            }
        }
        
        return result;
    }
    
    void setTicketStatusToSold(String ticketId) {
        
        for(int i=0; i<tickets.size(); i++) {
            
            Ticket ticket = (Ticket)tickets.get(i);
            
            if (ticket.getId().equals(ticketId)) {
                ticket.setStatus(2);
                tickets.set(i, ticket);
            }
        }
    }
    
}
