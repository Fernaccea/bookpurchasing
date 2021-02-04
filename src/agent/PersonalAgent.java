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

import data.BookInfo;
import java.util.ArrayList;
import data.Book;
import data.PaymentInfo;
import data.SimpleBook;
import data.SimpleBookList;

/**
 *
 * @author GATES
 * PersonalAgent => PA
 */
public class PersonalAgent extends Agent {
    
    String bankAccountNo = "222222";
    private BookShopUI bookshopUi;
    private ArrayList<SimpleBook> booklist;
    private double total;
    
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
                    
                    case 1: //send book info search to BookAgent
                        
                        System.out.println("PA: Executing Step-1");
                        System.out.println("");
                        step = 2;
                        
                    case 2: //received Book ArrayList (INFORM) or BookInfo (REFUSE)
                            //received refuse - book not available
                        
                        ACLMessage msg = receive();
                        
                        if (msg != null) {
                            
                            System.out.println("PA: Executing Step-2");
                            System.out.println("");
                            
                            if (msg.getPerformative()== ACLMessage.REFUSE) {
                                try {
                                    BookInfo bi2 = (BookInfo)msg.getContentObject();

                                    if (!bi2.isAvailable()) {
                                        System.out.println("PA: received book info reply from BA => Book not available");
                                        System.out.println("PA: Title: " + bi2.getTitle());
                                        System.out.println("PA: Author: " + bi2.getAuthor());
                                        System.out.println("PA: Publisher: " + bi2.getPublisher());
                                        System.out.println("");

                                        step = 1;
                                    }
                                } catch (Exception ex) {}
                            } 

                            if (msg.getPerformative()== ACLMessage.INFORM) {

                                Book theBook = null;

                                try {
                                    System.out.println("PA: received book info reply from BA => Book available");
                                    ArrayList books = (ArrayList)msg.getContentObject();                                     

                                    for(int i=0; i<books.size(); i++) {
                                        Book book = (Book)books.get(i);
                                        System.out.println("PA: ID: " + book.getId());
                                        System.out.println("PA: Title: " + book.getTitle());
                                        System.out.println("PA: Author: " + book.getAuthor());
                                        System.out.println("PA: Publisher: " + book.getPublisher());
                                        System.out.println("PA: Price: " + book.getPrice());
                                        System.out.println("PA: Bank Account No: " + book.getBankAccountNo());
                                        System.out.println("");

                                        theBook = book;
                                    }    
                                } catch (Exception ex) {}

                                if (theBook != null) {
                                    System.out.println("PA: Send request to bank for current balance");
                                    System.out.println("");

                                    PaymentInfo pi = new PaymentInfo();
                                    pi.setAmount(theBook.getPrice());
                                    pi.setBookId(theBook.getId());
                                    pi.setTitle(theBook.getTitle());
                                    pi.setAuthor(theBook.getAuthor());
                                    pi.setPublisher(theBook.getPublisher());
                                    pi.setBuyerAccountNo(bankAccountNo); //PA account no
                                    pi.setSellerAccountNo(theBook.getBankAccountNo()); //TA account no

                                    step = 3;

                                    try {
                                        ACLMessage msg2 = new ACLMessage(ACLMessage.REQUEST);
                                        msg2.setContentObject(pi);
                                        msg2.addReceiver(new AID( "BankAgent", AID.ISLOCALNAME) );
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
                                    System.out.println("PA: Received account balance info from BankAgent => Balance enough for payment");
                                    System.out.println("PA: Current account balance: " + pi.getBuyerCurrentBalance());
                                    System.out.println("PA: Book price: " + pi.getAmount());
                                    System.out.println("");

                                    //- reply with PaymentInfo pi object to BookAgent - BookAgent-step2
                                    //- this for doing payment, send money to BookAgent
                                    System.out.println("PA: Send request to BankAgent for payment to BookAgent");                                    
                                    System.out.println("");
                                    ACLMessage reply = msg2.createReply();
                                    reply.setPerformative( ACLMessage.REQUEST);
                                    reply.setContentObject(pi);
                                    send(reply);  

                                    step = 4;

                                } else {
                                    System.out.println("PA: Received account balance info from BankAgent => Balance not enough for payment");
                                    System.out.println("PA: Current account balance: " + pi.getBuyerCurrentBalance());
                                    System.out.println("PA: Book price: " + pi.getAmount());
                                    System.out.println("PA: Book buying operation failed - done");
                                    System.out.println("");

                                    //reset for next book search
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
                                
                                System.out.println("PA: Received book selling status from BookAgent => Book selling success!");
                                System.out.println("PA: Book id: " + pi.getBookId());
                                System.out.println("PA: Title: " + pi.getTitle());
                                System.out.println("PA: Author: " + pi.getAuthor());
                                System.out.println("PA: Publisher: " + pi.getPublisher());
                                System.out.println("PA: Book price: " + pi.getAmount());
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
        //Generate book object for book request
	    
	BookInfo bi = new BookInfo();
	bi.setTitle("The Flash");  
	bi.setAuthor("Steve Austin");
	bi.setPublisher("DC Comics");
    
	System.out.println("PA: Requesting book info");
	System.out.println("PA: Title: " + bi.getTitle());
	System.out.println("PA: Genre: " + bi.getAuthor());
	System.out.println("PA: Publisher: " + bi.getPublisher());
	System.out.println("");

        try {
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.setContentObject(bi);
            msg.addReceiver(new AID("BookAgent", AID.ISLOCALNAME));

            send(msg);
        } catch (Exception ex) {
        }
    }
    
}
