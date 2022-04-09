package newbank.server;

public class Encryption {

    int key;

    public Encryption(int key) {
        this.key = key;
    }

    public String encrypt(String input) {
    char[] x = input.toCharArray();
    String input_encrypted = "";
    for(char c : x)  {
        c += key;
        input_encrypted += c;
    }
    return input_encrypted;
    }

    public String decrypt(String input) {
    char[] x = input.toCharArray();
    String input_decrypted = "";
    for(char c : x) {
        c -= key;
        input_decrypted += c;
    }
    return input_decrypted;
    }


}
