package newbank.server;

import java.time.Instant;

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

    @Override
    public int compareTo(Transaction o) {
        return this.dateAndTime.compareTo(o.dateAndTime);
    }

    public String getFrom(){
        return this.from;
    }

    public String getTo(){
        return this.to;
    }

    public String getID(){
        return this.id;
    }

    public String getDateAndTime(){
        return this.dateAndTime.toString();
    }

    public Instant getDT(){
        return this.dateAndTime;
    }

    public String getType(){
        return this.type;
    }

    public double getAmount(){
        return this.amount;
    }
    


}
