package newbank.server;

public class Encryption {

    int key;

    public Encryption(int key) {
        this.key = key;
    }

    public String encrypt(String input) {
    char[] x = input.toCharArray();
    String input_encrypted = "";
    int charint;
    for(char c : x)  {
        charint = (c + key) > 126 ? c + key + 32 - 127 : c + key; 
        c = (char) charint;
        input_encrypted += c;
    }
    return input_encrypted;
    }

    public String decrypt(String input) {
    char[] x = input.toCharArray();
    String input_decrypted = "";
    int charint;
    for(char c : x)  {
        charint = (c - key) < 32 ? c - key - 32 + 127 : c - key;
        c = (char) charint;
        input_decrypted += c;
    }
    return input_decrypted;
    }


}
