/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.io.Serializable;

/**
 *
 * @author GATES
 */
public class PaymentInfo implements Serializable {

    /**
     * @return the ticketId
     */
    public String getTicketId() {
        return ticketId;
    }

    /**
     * @param ticketId the ticketId to set
     */
    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }
    
    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return the destination
     */
    public String getDestination() {
        return destination;
    }

    /**
     * @param destination the destination to set
     */
    public void setDestination(String destination) {
        this.destination = destination;
    }

    /**
     * @return the time
     */
    public String getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * @return the amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    /**
     * @return the buyerAccountNo
     */
    public String getBuyerAccountNo() {
        return buyerAccountNo;
    }

    /**
     * @param buyerAccountNo the buyerAccountNo to set
     */
    public void setBuyerAccountNo(String buyerAccountNo) {
        this.buyerAccountNo = buyerAccountNo;
    }

    /**
     * @return the sellerAccountNo
     */
    public String getSellerAccountNo() {
        return sellerAccountNo;
    }

    /**
     * @param sellerAccountNo the sellerAccountNo to set
     */
    public void setSellerAccountNo(String sellerAccountNo) {
        this.sellerAccountNo = sellerAccountNo;
    }
    
    /**
     * @return the enough
     */
    public boolean isEnough() {
        return enough;
    }

    /**
     * @param enough the enough to set
     */
    public void setEnough(boolean enough) {
        this.enough = enough;
    }
    
    /**
     * @return the sellerPreviousBalance
     */
    public double getSellerPreviousBalance() {
        return sellerPreviousBalance;
    }

    /**
     * @param sellerPreviousBalance the sellerPreviousBalance to set
     */
    public void setSellerPreviousBalance(double sellerPreviousBalance) {
        this.sellerPreviousBalance = sellerPreviousBalance;
    }

    /**
     * @return the sellerCurrentBalance
     */
    public double getSellerCurrentBalance() {
        return sellerCurrentBalance;
    }

    /**
     * @param sellerCurrentBalance the sellerCurrentBalance to set
     */
    public void setSellerCurrentBalance(double sellerCurrentBalance) {
        this.sellerCurrentBalance = sellerCurrentBalance;
    }
    
    /**
     * @return the buyerPreviousBalance
     */
    public double getBuyerPreviousBalance() {
        return buyerPreviousBalance;
    }

    /**
     * @param buyerPreviousBalance the buyerPreviousBalance to set
     */
    public void setBuyerPreviousBalance(double buyerPreviousBalance) {
        this.buyerPreviousBalance = buyerPreviousBalance;
    }

    /**
     * @return the buyerCurrentBalance
     */
    public double getBuyerCurrentBalance() {
        return buyerCurrentBalance;
    }

    /**
     * @param buyerCurrentBalance the buyerCurrentBalance to set
     */
    public void setBuyerCurrentBalance(double buyerCurrentBalance) {
        this.buyerCurrentBalance = buyerCurrentBalance;
    }
    
    private String ticketId;
    private String location, destination, time;
    private double amount;
    private String buyerAccountNo; //Personal Agent
    private String sellerAccountNo; //Ticketing Agent
    private boolean enough;
    private double sellerPreviousBalance, sellerCurrentBalance;
    private double buyerPreviousBalance, buyerCurrentBalance;
}
