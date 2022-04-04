package newbank.server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Handles data flow between the bank server and the database.
 * Reads and writes from the database files.
 */
public class DataHandler {
    private static String accCsv= "newbank/server/data/Account.txt";
    private static String cusCsv= "newbank/server/data/Customer.txt";
    private static String tranCsv= "newbank/server/data/Transaction.txt";
    private static String separator="#se2#"; //for handling the case that the data include comma

    
    /** 
     * @return HashMap<String, Customer>
     */
    public static HashMap<String, Customer> readCustData(){
        HashMap<String, Customer> customers;
        customers = readCustomer();
        readAccount(customers);
        return customers;
    }

    
    /** 
     * @return ArrayList<Transaction>
     */
    public static ArrayList<Transaction> readTransaction(){
        ArrayList<Transaction> transactions = new ArrayList<>();
        String[] line;
        File tranFile = new File(tranCsv);
        try (Scanner scanner = new Scanner(tranFile)){
            //skip header
            line = scanner.nextLine().split(",");
            //read linebyline and put to hashmap
            while(scanner.hasNextLine()){
                line = scanner.nextLine().split(",");
                getComma(line);
                Transaction transaction = new Transaction(line[0],line[1],line[2],line[3],line[4],line[5]);
                transactions.add(transaction);
            }
            //Sort the transaction by the date
            Collections.sort(transactions, Collections.reverseOrder());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return transactions;
    }

    
    /** 
     * @return HashMap<String, Customer>
     */
    private static HashMap<String, Customer> readCustomer(){
        HashMap<String, Customer> customers = new HashMap<>();
        String[] line;
        File cusFile = new File(cusCsv);
        try (Scanner scanner = new Scanner(cusFile)){
            //skip header
            line = scanner.nextLine().split(",");
            //read linebyline and put to hashmap
            while(scanner.hasNextLine()){
                line = scanner.nextLine().split(",", -1);
                getComma(line);
                Customer customer = new Customer(line[0],line[2],line[3],line[4],line[5],line[6], line[7]);
                customers.put(line[1],customer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customers;
    }


    
    /** 
     * @param line
     */
    private static void getComma(String[] line){
        //replace the seperater by the comma
        for (int i=0; i<line.length;i++){
          line[i]=line[i].replace(separator,",");
        }
    }

    
    /** 
     * @param customers
     */
    private static void readAccount(HashMap<String, Customer> customers){
        String[] line;
        File accFile = new File(accCsv);
        try (Scanner scanner = new Scanner(accFile)){
            //move to next line
            line = scanner.nextLine().split(",");
            while(scanner.hasNextLine()){
                line = scanner.nextLine().split(",");
                getComma(line);
                Account account = new Account(line[0], line[1],Double.parseDouble(line[2]));
                //get customer by customerid and add the account to the customer
                for (Customer customer : customers.values()) {
                    if (customer.getUserID().equals(line[0].split("-")[0])){
                        customer.addAccount(account);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    /** 
     * @param customers
     * @throws IOException
     */
    public static void updateCustomerCSV(Map<String, Customer> customers) throws IOException {
        // write back staff data
        File file = new File(cusCsv);
        // creates the file
        file.createNewFile();
        // creates a FileWriter Object
        try (FileWriter writer = new FileWriter(file)){
        // Writes the content to the file
        String header = "id, username, password, firstName, lastName, phone, email, address\n";
        writer.write(header);
        for(Map.Entry<String, Customer> customer : customers.entrySet()){
            writer.write(customer.getValue().getUserID().replace(",",separator)+","+
                         customer.getKey().replace(",",separator)+","+
                         customer.getValue().getPassword().replace(",",separator)+","+
                         customer.getValue().getFirstName().replace(",",separator)+","+
                         customer.getValue().getLastName().replace(",",separator)+","+
                         customer.getValue().getPhone().replace(",",separator)+","+
                         customer.getValue().getEmail().replace(",",separator)+","+
                         customer.getValue().getAddress().replace(",",separator)+"\n");
        }
        
        writer.flush();
        writer.close();
      }
    }

    
    /** 
     * @param customers
     * @throws IOException
     */
    public static void updateAccountCSV(Map<String, Customer> customers) throws IOException {
        // write back staff data
        File file = new File(accCsv);
        // creates the file
        file.createNewFile();
        // creates a FileWriter Object
        try (FileWriter writer = new FileWriter(file)){
        // Writes the content to the file
        String header = "id, name, balance, customer_id\n";
        writer.write(header);
        for(Customer customer : customers.values()){
            for (Account account : customer.getAccounts()){
                writer.write(account.getID().replace(",",separator)+","+
                            account.getAccount().replace(",",separator)+","+
                            Double.toString(account.getAmount())+"\n");
            }
        }
        
        writer.flush();
        writer.close();
      }
    }

    
    /** 
     * @param transactions
     * @throws IOException
     */
    public static void updateTransactionCSV(ArrayList<Transaction> transactions) throws IOException {
        // write back staff data
        File file = new File(tranCsv);
        // creates the file
        file.createNewFile();
        // creates a FileWriter Object
        try (FileWriter writer = new FileWriter(file)){
        // Writes the content to the file
        String header = "id, date&time, amount, from_id, to_id, type\n";
        writer.write(header);
        for(Transaction transaction : transactions){
            writer.write(transaction.getID().replace(",",separator)+","+
                        transaction.getDateAndTime().replace(",",separator)+","+
                        String.valueOf(transaction.getAmount()).replace(",",separator)+","+
                        transaction.getFrom().replace(",",separator)+","+
                        transaction.getTo().replace(",",separator)+","+
                        transaction.getType().replace(",",separator)+"\n");
        }
        
        writer.flush();
        writer.close();
      }
    }
}
