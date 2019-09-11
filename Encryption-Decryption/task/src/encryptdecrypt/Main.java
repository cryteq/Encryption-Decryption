package encryptdecrypt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


interface Algorithms {
    void encrypt(String message, int key, String out);
    void decrypt(String message, int key, String out);
}

class AlgorithmPicker {
    private Algorithms algorithm;

    void setAlgorithm(Algorithms algorithm) {
        this.algorithm = algorithm;
    }

    void encrypt(String message, int key, String out) {
        this.algorithm.encrypt(message, key, out);
    }

    void decrypt(String message, int key, String out) {
        this.algorithm.decrypt(message, key, out);
    }
}

class ShiftAlgorithm implements Algorithms {

    @Override
    public void encrypt(String message, int key, String out) {

        char[] msg = message.toCharArray();
        String alphabet = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz";
        char[] alphabetC = alphabet.toCharArray();
        StringBuilder result = new StringBuilder();
        for (char c : msg) {
            if (alphabet.indexOf(c) == -1) {
                result.append(c);

            } else {
                result.append(alphabetC[alphabet.indexOf(c) + key]);
            }
        }

        if (out.equals("")) {
            System.out.println(result);
        } else {
            try (FileWriter writer = new FileWriter(out)) {
                writer.write((result.toString()));
            } catch (IOException e) {
                System.err.println("IOException");
            }
        }
    }

    @Override
    public void decrypt(String message, int key, String out) {
        char[] msg = message.toCharArray();
        String alphabet = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz";
        char[] alphabetC = alphabet.toCharArray();
        StringBuilder result = new StringBuilder();
        for (char c : msg) {
            if (alphabet.indexOf(c) == -1) {
                result.append(c);

            } else {
                result.append(alphabetC[alphabet.indexOf(c) + 26 - key]);
            }
        }

        if (out.equals("")) {
            System.out.println(result);
        } else {
            try (FileWriter writer = new FileWriter(out)) {
                writer.write((result.toString()));
            } catch (IOException e) {
                System.err.println("IOException");
            }
        }

    }
}

class UnicodeAlgorithm implements Algorithms {

    @Override
    public void encrypt(String message, int key, String out) {
        char[] msg = message.toCharArray();
        int[] encryptedMsg = new int[msg.length];
        for (int i = 0; i < msg.length; i++) {
            encryptedMsg[i] = msg[i] + key;
            msg[i] = (char)encryptedMsg[i];
        }

        if (out.equals("")) {
            System.out.println(new String(msg));
        } else {
            try (FileWriter writer = new FileWriter(out)) {
                writer.write(new String(msg));
            } catch (IOException e) {
                System.err.println("IOException");
            }
        }
    }

    @Override
    public void decrypt(String message, int key, String out) {
        char[] msg = message.toCharArray();
        int[] decryptedMsg = new int[msg.length];
        for (int i = 0; i < msg.length; i++) {
            decryptedMsg[i] = msg[i] - key;
            msg[i] = (char)decryptedMsg[i];
        }

        if (out.equals("")) {
            System.out.println(new String(msg));
        } else {
            try (FileWriter writer = new FileWriter(out)) {
                writer.write(new String(msg));
            } catch (IOException e) {
                System.err.println("IOException");
            }
        }
    }
}





public class Main {
    private static Algorithms select(String alg) {
        switch (alg) {
            case "shift":
                return new ShiftAlgorithm();
            case "unicode":
                return new UnicodeAlgorithm();
            default: {
                throw new IllegalArgumentException("Unknown algorithm type: " + alg);
            }
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        String mode = "enc";
        int key = -1;
        String data = "";
        StringBuilder alg = new StringBuilder();
        StringBuilder inFile = new StringBuilder("");
        StringBuilder outFile = new StringBuilder("");


        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-mode":
                    mode = args[i + 1];
                    break;
                case "-key":
                    key = Integer.parseInt(args[i + 1]);
                    break;
                case "-data":
                    data = args[i + 1].replace("\"", "");
                    break;
                case "-in":
                    inFile.append(args[i + 1]);
                    break;
                case "-out":
                    outFile.append(args[i + 1]);
                    break;
                case "-alg":
                    alg.append(args[i + 1]);
                    break;
            }
        }
        if (key == -1) {
            key = sc.nextInt();
        }

        if (data.equals("") && inFile.toString().equals("")) {
            data = sc.nextLine();
        } else if (!inFile.toString().equals("")){
            File file = new File(inFile.toString());
            try (Scanner scanner = new Scanner(file)) {
                data = scanner.nextLine();
            } catch (FileNotFoundException e) {
                System.err.println("FILE NOT FOUND");
            }
        }

        Algorithms algorithm = select(alg.toString());

        AlgorithmPicker algorithmPicker = new AlgorithmPicker();
        algorithmPicker.setAlgorithm(algorithm);


        if (mode.equals("enc")) {
            algorithmPicker.encrypt(data, key, outFile.toString());
        } else if (mode.equals("dec")) {
            algorithmPicker.decrypt(data,key, outFile.toString());
        }

    }
}
