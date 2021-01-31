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

import java.util.ArrayList;
import data.Account;
import data.PaymentInfo;

/**
 *
 * @author GATES
 */
public class BankAgent extends Agent {
    
    ArrayList accounts = new ArrayList();
    
    protected void setup() {       
        
        Account account = new Account();
        account.setAccountNo("222222"); //Personal Agent
        account.setBalance(150.50);        
        accounts.add(account);
        
        account = new Account();
        account.setAccountNo("1234567"); //Ticketing Agent
        account.setBalance(100);
        accounts.add(account);
        
        addBehaviour(new CyclicBehaviour(this) {
            
            int step = 1;
            
            public void action() {
                
                switch (step) {
                    
                    case 1: //received money balance query from PA - PA-step1
                        
                        ACLMessage msg = receive();
                        
                        if (msg != null) {
                            
                            System.out.println("BankAgent: Executing Step-1");
                            System.out.println("");

                            try {
                                PaymentInfo pi = (PaymentInfo)msg.getContentObject();

                                System.out.println("BankAgent: Received bank account balance request from PA");
                                System.out.println("BankAgent: Book price: " + pi.getAmount());
                                System.out.println("BankAgent: PA account no: " + pi.getBuyerAccountNo());

                                double amountToPay = pi.getAmount();
                                String buyerAccountNo = pi.getBuyerAccountNo();

                                double buyerAccountBalance = getAccountBalance(buyerAccountNo);
                                System.out.println("BankAgent: PA account balance: " + buyerAccountBalance);
                                pi.setBuyerCurrentBalance(buyerAccountBalance);

                                if (buyerAccountBalance > amountToPay) {
                                    System.out.println("BankAgent: PA balance is enough for payment");
                                    System.out.println("");
                                    pi.setEnough(true);

                                    step = 2;

                                } else {
                                    System.out.println("BankAgent: PA balance is not enough for payment");
                                    System.out.println("");
                                    pi.setEnough(false);

                                    step = 1;
                                }

                                //- reply with PaymentInfo pi object to PA - PA-step2
                                System.out.println("BankAgent: Send reply to PA - Bank account balance");
                                System.out.println("");
                                ACLMessage reply = msg.createReply();
                                reply.setPerformative( ACLMessage.INFORM);
                                reply.setContentObject(pi);
                                send(reply);

                            } catch (Exception ex) {}
                            
                        } else
                            block();
                    
                    case 2: //received request from PA - PA-step2 - PaymentInfo object  
                        
                        ACLMessage msg2 = receive();
                        
                        if (msg2 != null) {
                            
                            System.out.println("BankAgent: Executing Step-2");
                            System.out.println("");

                            try {
                                
                                PaymentInfo pi = (PaymentInfo)msg2.getContentObject();
                                
                                System.out.println("BankAgent: Received book payment request from PA");
                                System.out.println("BankAgent: Book price: " + pi.getAmount());
                                System.out.println("BankAgent: Book id: " + pi.getBookId());
                                System.out.println("BankAgent: PA account no: " + pi.getBuyerAccountNo());
                                System.out.println("BankAgent: BookAgent account no: " + pi.getSellerAccountNo());
                                System.out.println("");
                                
                                double amountToPay = pi.getAmount();
                                String buyerAccountNo = pi.getBuyerAccountNo();
                                String sellerAccountNo = pi.getSellerAccountNo();
                                
                                System.out.println("BankAgent: PA account balance before payment: " + getAccountBalance(buyerAccountNo));
                                System.out.println("BankAgent: BookAgent account balance before payment: " + getAccountBalance(sellerAccountNo));
                                System.out.println("");
                                
                                pi.setSellerPreviousBalance(getAccountBalance(sellerAccountNo));
                                pi.setBuyerPreviousBalance(getAccountBalance(buyerAccountNo));
                                
                                doPayment(amountToPay, buyerAccountNo, sellerAccountNo);
                                
                                System.out.println("BankAgent: PA account balance after payment: " + getAccountBalance(buyerAccountNo));
                                System.out.println("BankAgent: BookAgent account balance after payment: " + getAccountBalance(sellerAccountNo));
                                System.out.println("");
                                
                                pi.setSellerCurrentBalance(getAccountBalance(sellerAccountNo));
                                pi.setBuyerCurrentBalance(getAccountBalance(buyerAccountNo));
                                
                                step = 1;
                                
                                //- info BookAgent about fund credit - send pi object
                                System.out.println("BankAgent: Send payment info to BookAgent");
                                System.out.println("");
                                
                                ACLMessage msg3 = new ACLMessage(ACLMessage.INFORM);
                                msg3.setContentObject(pi);
                                msg3.addReceiver(new AID( "BookAgent", AID.ISLOCALNAME) );
                                send(msg3);
                                
                            } catch (Exception ex) {}                            
                            
                        } else
                            block();
                }
            }           
        });
    } 
    
    double getAccountBalance(String accountNo) {
        
        double balance = 0;
        
        for(int i=0; i<accounts.size(); i++) {
            
            Account account = (Account)accounts.get(i);
            
            if (account.getAccountNo().equals(accountNo)) {
                balance = account.getBalance();
            }
        }
        
        return balance;
    }
    
    void doPayment(double amountToPay, 
                   String buyerAccountNo, 
                   String sellerAccountNo) {
        
        //debit amountToPay to buyer balance (PA)
        for(int i=0; i<accounts.size(); i++) {
            
            Account account = (Account)accounts.get(i);
            
            if (account.getAccountNo().equals(buyerAccountNo)) {
                
                double newBalance = account.getBalance() - amountToPay;
                
                //update balance
                account.setBalance(newBalance);
                
                //update account in accounts arraylist
                accounts.set(i, account);
            }
        }
        
        //credit amountToPay to seller balance (TA)
        for(int i=0; i<accounts.size(); i++) {
            
            Account account = (Account)accounts.get(i);
            
            if (account.getAccountNo().equals(sellerAccountNo)) {
                
                double newBalance = account.getBalance() + amountToPay;
                
                //update balance
                account.setBalance(newBalance);
                
                //update account in accounts arraylist
                accounts.set(i, account);
            }
        }
    }
}
