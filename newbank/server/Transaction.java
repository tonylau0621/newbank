package newbank.server;

import java.time.Instant;

/**
 * Represents a transaction to be processed by the bank.
 * The transaction is then able to be logged to the database.
 */
public class Transaction implements Comparable<Transaction>{
    private String id;
    private Instant dateAndTime;
    private String from;
    private String to;
    private double amount;
    private String type; //Payment, Received and Transfer
    private static int count = 0;

    public Transaction(String from, String to, double amount, String type){
        this.id = String.valueOf(count + 1);
        this.dateAndTime = Instant.now();
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.type = type;
        count++;
    }
    //For reading data
    public Transaction(String id, String dateAndTime, String amount, String from, String to, String type){
        this.id = id;
        this.dateAndTime = Instant.parse(dateAndTime);
        this.from = from;
        this.to = to;
        this.amount = Double.parseDouble(amount);
        this.type = type;
        count++;
    }

    
    /** 
     * @param o
     * @return int
     */
    @Override
    public int compareTo(Transaction o) {
        return this.dateAndTime.compareTo(o.dateAndTime);
    }

    
    /** 
     * @return String
     */
    public String getFrom(){
        return this.from;
    }

    
    /** 
     * @return String
     */
    public String getTo(){
        return this.to;
    }

    
    /** 
     * @return String
     */
    public String getID(){
        return this.id;
    }

    
    /** 
     * @return String
     */
    public String getDateAndTime(){
        return this.dateAndTime.toString();
    }

    
    /** 
     * @return Instant
     */
    public Instant getDT(){
        return this.dateAndTime;
    }

    
    /** 
     * @return String
     */
    public String getType(){
        return this.type;
    }

    
    /** 
     * @return double
     */
    public double getAmount(){
        return this.amount;
    }
    


}
