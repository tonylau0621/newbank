package newbank.server;

public class Encryption {

    public String encrypt(String input) {

    int key = 5;
    char[] x = input.toCharArray();
    String input_encrypted = "";
    for(char c : x)  {
        c += key;
        input_encrypted += c;
    }
    return input_encrypted;
    }

    public String decrypt(String input) {

    int key = 5;
    char[] x = input.toCharArray();
    String input_decrypted = "";
    for(char c : x) {
        c -= key;
        input_decrypted += c;
    }
    return input_decrypted;
    }


}
