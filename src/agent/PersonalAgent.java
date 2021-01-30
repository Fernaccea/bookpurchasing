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

import data.TicketInfo;
import java.util.ArrayList;
import data.Ticket;
import data.PaymentInfo;
import data.SimpleBook;

/**
 *
 * @author GATES
 * PersonalAgent => PA
 */
public class PersonalAgent extends Agent {
    
    String bankAccountNo = "222222";
    private BookShopUI bookshopUi;
    
    protected void setup() {
        
        int secondsToSleep = 5;
        
        bookshopUi = new BookShopUI(this);
	bookshopUi.showGui();
        
        try {
            Thread.sleep(secondsToSleep * 1000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        
        addBehaviour(new CyclicBehaviour(this) {
            
            private int step = 1;
            
            public void action() {
                
                switch (step) {
                    
                    case 1: //send ticket info search to TA
                        
                        System.out.println("PA: Executing Step-1");
                        System.out.println("");
                        step = 2;
                        
                    case 2: //received Ticket ArrayList (INFORM) or TicketInfo (REFUSE)
                            //received refuse - ticket not available
                        
                        ACLMessage msg = receive();
                        
                        if (msg != null) {
                            
                            System.out.println("PA: Executing Step-2");
                            System.out.println("");
                            
                            if (msg.getPerformative()== ACLMessage.REFUSE) {
                                try {
                                    TicketInfo ti2 = (TicketInfo)msg.getContentObject();

                                    if (!ti2.isAvailable()) {
                                        System.out.println("PA: received ticket info reply from TA => Ticket not available");
                                        System.out.println("PA: Location: " + ti2.getLocation());
                                        System.out.println("PA: Destination: " + ti2.getDestination());
                                        System.out.println("");

                                        step = 1;
                                    }
                                } catch (Exception ex) {}
                            } 

                            if (msg.getPerformative()== ACLMessage.INFORM) {

                                Ticket theTicket = null;

                                try {
                                    System.out.println("PA: received ticket info reply from TA => Ticket available");
                                    ArrayList tickets = (ArrayList)msg.getContentObject();                                     

                                    for(int i=0; i<tickets.size(); i++) {
                                        Ticket ticket = (Ticket)tickets.get(i);
                                        System.out.println("PA: ID: " + ticket.getId());
                                        System.out.println("PA: Destination: " + ticket.getDestination());
                                        System.out.println("PA: Location: " + ticket.getLocation());
                                        System.out.println("PA: Company: " + ticket.getCompany());
                                        System.out.println("PA: Time: " + ticket.getTime());
                                        System.out.println("PA: Seat No: " + ticket.getSeatNo());
                                        System.out.println("PA: Price: " + ticket.getPrice());
                                        System.out.println("PA: Bank Account No: " + ticket.getBankAccountNo());
                                        System.out.println("");

                                        theTicket = ticket;
                                    }    
                                } catch (Exception ex) {}

                                if (theTicket != null) {
                                    System.out.println("PA: Send request to bank for current balance");
                                    System.out.println("");

                                    PaymentInfo pi = new PaymentInfo();
                                    pi.setAmount(theTicket.getPrice());
                                    pi.setTicketId(theTicket.getId());
                                    pi.setLocation(theTicket.getLocation());
                                    pi.setDestination(theTicket.getDestination());
                                    pi.setTime(theTicket.getTime());
                                    pi.setBuyerAccountNo(bankAccountNo); //PA account no
                                    pi.setSellerAccountNo(theTicket.getBankAccountNo()); //TA account no

                                    step = 3;

                                    try {
                                        ACLMessage msg2 = new ACLMessage(ACLMessage.REQUEST);
                                        msg2.setContentObject(pi);
                                        msg2.addReceiver(new AID( "BA", AID.ISLOCALNAME) );
                                        send(msg2);
                                    } catch (Exception ex) {}                                    

                                }
                            }                            
                        } else
                            block();
                    
                    case 3: //receive reply (balance enough) from BA - BA-step1  
                        
                        ACLMessage msg2 = receive();
                        
                        if (msg2 != null) {
                            
                            System.out.println("PA: Executing Step-3");
                            System.out.println("");

                            try {
                                PaymentInfo pi = (PaymentInfo)msg2.getContentObject();

                                if (pi.isEnough()) {
                                    System.out.println("PA: received account balance info from BA => Balance enough for payment");
                                    System.out.println("PA: Current account balance: " + pi.getBuyerCurrentBalance());
                                    System.out.println("PA: Ticket price: " + pi.getAmount());
                                    System.out.println("");

                                    //- reply with PaymentInfo pi object to BA - BA-step2
                                    //- this for doing payment, send money to TA
                                    System.out.println("PA: Send request to BA for payment to TA");                                    
                                    System.out.println("");
                                    ACLMessage reply = msg2.createReply();
                                    reply.setPerformative( ACLMessage.REQUEST);
                                    reply.setContentObject(pi);
                                    send(reply);  

                                    step = 4;

                                } else {
                                    System.out.println("PA: received account balance info from BA => Balance not enough for payment");
                                    System.out.println("PA: Current account balance: " + pi.getBuyerCurrentBalance());
                                    System.out.println("PA: Ticket price: " + pi.getAmount());
                                    System.out.println("PA: ticket buying operation failed - done");
                                    System.out.println("");

                                    //reset for next ticket search
                                    step = 1;
                                }
                            } catch (Exception ex) {}
                        
                        } else
                            block();
                        
                    case 4: //received inform from TA - TA-step2
                        
                        ACLMessage msg3 = receive();
                        
                        if (msg3 != null) {
                            
                            System.out.println("PA: Executing Step-4");
                            System.out.println("");
                            
                            try {
                                
                                PaymentInfo pi = (PaymentInfo)msg3.getContentObject();
                                
                                System.out.println("PA: received ticket selling status from TA => ticket selling success!");
                                System.out.println("PA: Ticket id: " + pi.getTicketId());
                                System.out.println("PA: Location: " + pi.getLocation());
                                System.out.println("PA: Destination: " + pi.getDestination());
                                System.out.println("PA: Time: " + pi.getTime());
                                System.out.println("PA: Ticket price: " + pi.getAmount());
                                System.out.println("PA: Previous account balance: " + pi.getBuyerPreviousBalance());
                                System.out.println("PA: Current account balance: " + pi.getBuyerCurrentBalance());
                                System.out.println("");
                            } catch (Exception ex) {}                            
                            
                        } else
                            block();
                }
                
                block();
            }
        });     
    }
    
    public void requestBookPurchase(ArrayList<SimpleBook> booklist) {

        try {
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.setContentObject(booklist);
            msg.addReceiver(new AID("TA", AID.ISLOCALNAME));

            send(msg);
        } catch (Exception ex) {
        }
    }
    
}
