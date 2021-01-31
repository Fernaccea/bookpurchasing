/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import data.Book;
import data.BookInfo;
import data.PaymentInfo;
import jade.core.AID;
import java.util.ArrayList;

/**
 *
 * @author GATES
 BookAgent => TA
 */
public class BookAgent extends Agent {
    
    ArrayList books = new ArrayList();    
    String bankAccountNo = "1234567";
    
    protected void setup() {
        
        addBehaviour(new CyclicBehaviour(this) {
            
            int step = 1;
            
            public void action() {
                
                switch (step) {
                    
                    case 1: //received BookInfo object from PA
                        
                        ACLMessage msg = receive();
                        
                        if (msg != null) {
                            
                            System.out.println("BookAgent: Executing Step-1");
                            System.out.println("");
    
                            try {
                                BookInfo bi = (BookInfo)msg.getContentObject();
                                System.out.println("BookAgent: Received book info request from PA");
                                System.out.println("BookAgent: Title: " + bi.getTitle());
                                System.out.println("BookAgent: Author: " + bi.getAuthor());
                                System.out.println("BookAgent: Publisher: " + bi.getPublisher());                                
                                System.out.println("");

                                ArrayList result = findBook(bi);

                                ACLMessage reply = msg.createReply();

                                if (result.size() > 0) {

                                    step = 2;

                                    System.out.println("BookAgent: Book available");
                                    System.out.println("");
                                    reply.setPerformative(ACLMessage.INFORM);
                                    reply.setContentObject(result);
                                    send(reply); 
                                }
                                else {
                                    System.out.println("BookAgent: Book not available");
                                    System.out.println("");

                                    step = 1;

                                    reply.setPerformative( ACLMessage.REFUSE);                                    
                                    bi.setAvailable(false);
                                    reply.setContentObject(books);
                                    send(reply); 
                                }
                            
                            } catch (Exception ex) {}                        
                            
                        } else
                            block();
                        
                    case 2: //received credit info from BA - BA-step2 - PaymentInfo    
                        
                        ACLMessage msg2 = receive();
                        
                        if (msg2 != null) {
                            
                            System.out.println("BookAgent: Executing Step-2");
                            System.out.println("");
                            
                            try {
                                PaymentInfo pi = (PaymentInfo)msg2.getContentObject();
                                System.out.println("BookAgent: Received payment info from BA (credit)");
                                System.out.println("BookAgent: Book ID: " + pi.getBookId());
                                System.out.println("BookAgent: Amount: " + pi.getAmount()); 
                                System.out.println("BookAgent: Previous Balance: " + pi.getSellerPreviousBalance()); 
                                System.out.println("BookAgent: Current Balance: " + pi.getSellerCurrentBalance()); 
                                System.out.println("");
                                
                                //set ticket to sold - status 2
                                System.out.println("BookAgent: set ticket status to sold"); 
                                System.out.println("BookAgent: Book ID: " + pi.getBookId());
                                System.out.println("");
                                setBookStatusToSold(pi.getBookId());
                                
                                //send inform to PA - ticket selling success
                                System.out.println("BookAgent: Send message inform to PA - ticket selling success!");
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
        
        //create book using Book object and put into arraylist
        //book for sell list
        Book book = new Book();
        book.setId("ID0000001");
        book.setTitle("Johor Bahru");
        book.setAuthor("Kota Bharu");
        book.setPublisher("Mutiara Express");
        book.setPrice(65.50);
        book.setAuthor("9.00 AM");    
        book.setStatus(1);
        book.setBankAccountNo(bankAccountNo);
        
        //add to arraylist
        books.add(book);
        
        book.setId("ID0000001");
        book.setTitle("Johor Bahru");
        book.setAuthor("Kota Bharu");
        book.setPublisher("Mutiara Express");
        book.setPrice(65.50);
        book.setAuthor("9.00 AM");     
        book.setStatus(1);
        book.setBankAccountNo(bankAccountNo);
        
        //add to arraylist
        books.add(book);
    } 
    
    protected ArrayList findBook(BookInfo bi) {
        ArrayList result = new ArrayList();
        
        String title = bi.getTitle();
        String genre = bi.getAuthor();
        String publisher = bi.getPublisher();
        
        for(int i=0; i<books.size(); i++) {
            Book book = (Book)books.get(i);
           
           
            if (book.getTitle().equals(title) && 
                    book.getAuthor().equals(genre) && 
                        book.getPublisher().equals(publisher) &&
                            book.getStatus() == 1) {
                
                System.out.println("BookAgent: Book found");
                System.out.println("BookAgent: ID: " + book.getId());
                System.out.println("BookAgent: Title: " + book.getTitle());
                System.out.println("BookAgent: Author: " + book.getAuthor());
                System.out.println("BookAgent: Publisher: " + book.getPublisher());
                System.out.println("BookAgent: Price: " + book.getPrice());
                System.out.println("");
                result.add(book);
            }
        }
        
        return result;
    }
    
    void setBookStatusToSold(String bookId) {
        
        for(int i=0; i<books.size(); i++) {
            
            Book book = (Book)books.get(i);
            
            if (book.getId().equals(bookId)) {
                book.setStatus(2);
                books.set(i, book);
            }
        }
    }
    
}
