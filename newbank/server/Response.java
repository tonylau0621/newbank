package newbank.server;

/**
 * Represents a response to be sent to a client.
 */
public class Response {
    private String responseMessage;
    private CustomerID customer;

    public Response() {}

    public void setResponseMessage(String responseMessage) { this.responseMessage = responseMessage; }

    public void setCustomer(CustomerID customer) { this.customer = customer; }

    public String getResponseMessage() { return this.responseMessage; }
    public CustomerID getCustomer() { return this.customer; }
}
