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

    public static boolean matchPattern(String inputLine, String pattern) {
        char[] inputLineArray = inputLine.toCharArray();
//        HashSet<Integer> positiveCharacterSet;
//        HashSet<Integer> negativeCharacterSet;
        if (pattern.length() == 1) {
            return inputLine.contains(pattern);
        } else if (pattern.contains("\\d")) {
            for (char ch : inputLineArray) {
                if (Character.isDigit(ch)) {
                    return true;
                }
            }
            return false;
        } else if (pattern.contains("\\w")) {
            return inputLine.chars().anyMatch(Main::isW);
        } else {
            CharacterSet characterSet = getPatternCharacterSet(pattern);
            if (characterSet != null) {
                return inputLine.chars().anyMatch(characterSet::doesSetAllow);
//                inputLine.chars().anyMatch(positiveCharacterSet::contains);
            } else {
                throw new RuntimeException("Unhandled pattern: " + pattern);
            }
//            if ((positiveCharacterSet = matchPatternPositiveCharSet(pattern)) != null) {
//                return inputLine.chars().anyMatch(positiveCharacterSet::contains);
//            } else {
//            }
        }
    }

    public static boolean isW(int ch) {
        return Character.isAlphabetic(ch) || Character.isDigit(ch) || ch == '_';
    }

    private static CharacterSet getPatternCharacterSet(String pattern) {
        char[] str = pattern.toCharArray();
        if (str.length < 2) return null;
        HashSet<Integer> set = new HashSet<>();
        CharacterClassKind kind;
        if (str[0] == '[') {
            int idx = 1;
            if (str[idx] == '^') {
                kind = CharacterClassKind.NEGATIVE;
                idx++;
            } else {
                kind = CharacterClassKind.POSITIVE;
            }

            while (idx < str.length && str[idx] != ']') {
                char ch = str[idx];
                set.add((int) ch);
                idx++;
            }
            if (idx == str.length) return null;
            return new CharacterSet(kind, set);
        }
        return null;
    }

    static class CharacterSet {
        Main.CharacterClassKind kind;
        HashSet<Integer> set;

        public CharacterSet(Main.CharacterClassKind kind, HashSet<Integer> set) {
            this.kind = kind;
            this.set = set;
        }

        public boolean doesSetAllow(int codePoint) {
            boolean setContains = set.contains(codePoint);
            if (kind == CharacterClassKind.POSITIVE) {
                return setContains;
            }else{
                return !setContains;
            }
        }
    }

    enum CharacterClassKind {
        POSITIVE,
        NEGATIVE
    }
}
