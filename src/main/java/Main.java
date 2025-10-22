import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length != 2 || !args[0].equals("-E")) {
            System.out.println("Usage: ./your_program.sh -E <pattern>");
            System.exit(1);
        }

        String pattern = args[1];
        Scanner scanner = new Scanner(System.in);
        String inputLine = scanner.nextLine();

        // You can use print statements as follows for debugging, they'll be visible
        // when running tests.
        System.err.println("Logs from your program will appear here!");

        // Uncomment this block to pass the first stage
        //
        if (matchPattern(inputLine, pattern)) {
            System.exit(0);
        } else {
            System.exit(1);
        }
    }

    public static boolean matchPattern(String inputLine, String stringPattern) {

        Pattern pattern = new Pattern(stringPattern);
        for(int i = 0; i<inputLine.length(); i++){
            if(pattern.matchPattern(inputLine, i)){
                return true;
            }
        }
        return false;
////        HashSet<Integer> positiveCharacterSet;
////        HashSet<Integer> negativeCharacterSet;
//        if (pattern.length() == 1) {
//            return inputLine.contains(pattern);
//        } else if (pattern.contains("\\d")) {
//            for (char ch : inputLineArray) {
//                if (Character.isDigit(ch)) {
//                    return true;
//                }
//            }
//            return false;
//        } else if (pattern.contains("\\w")) {
//            return inputLine.chars().anyMatch(Main::isW);
//        } else {
//            CharacterSet characterSet = getPatternCharacterSet(pattern);
//            if (characterSet != null) {
//                return inputLine.chars().anyMatch(characterSet::doesSetAllow);
////                inputLine.chars().anyMatch(positiveCharacterSet::contains);
//            } else {
//                throw new RuntimeException("Unhandled pattern: " + pattern);
//            }
////            if ((positiveCharacterSet = matchPatternPositiveCharSet(pattern)) != null) {
////                return inputLine.chars().anyMatch(positiveCharacterSet::contains);
////            } else {
////            }
//        }
    }


}
