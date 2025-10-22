import java.util.ArrayList;
import java.util.HashSet;

public class Pattern {
    private ArrayList<CharacterMatcher> pattern;

    public Pattern(String patternString) {
        this.pattern = buildPattern(patternString);
        if (this.pattern == null) throw new RuntimeException("Unhandled pattern: " + patternString);
    }

    private static ArrayList<CharacterMatcher> buildPattern(String pattern) {
        ArrayList<CharacterMatcher> ls = new ArrayList<>();
        int idx = 0;
        char[] arrPattern = pattern.toCharArray();
        while (idx < arrPattern.length) {
            if (arrPattern[idx] == '\\') {
                if (idx + 1 == arrPattern.length) return null;
                switch (arrPattern[idx + 1]) {
                    case 'w':
                        ls.add(new WordCharCharacterClass());
                        break;
                    case 'd':
                        ls.add(new DigitCharacterClass());
                        break;
                }
                idx++;
            } else if (arrPattern[idx] == '[') {
                idx++;
                int start = idx;
                while (idx < arrPattern.length && arrPattern[idx] != ']') {
                    idx++;
                }
                if (idx == arrPattern.length) return null;
                CharacterSet characterSet = getPatternCharacterSet(pattern, start, idx - 1);
                if (characterSet == null) return null;
                ls.add(new CharSetCharacterClass(characterSet));
            } else {
                ls.add(new CharLiteral(arrPattern[idx]));
//                return null;
//                    
            }
            idx++;
        }
        return ls;
    }


    private static CharacterSet getPatternCharacterSet(String pattern, int start, int end) {
        char[] str = pattern.toCharArray();
        if (str.length - start < 2) return null;
        HashSet<Integer> set = new HashSet<>();
        CharacterSetKind kind;
        int idx = start;
        if (str[idx] == '^') {
            idx++;
            kind = CharacterSetKind.NEGATIVE;
        } else {
            kind = CharacterSetKind.POSITIVE;
        }
        for (int i = idx; i <= end; i++) {
            set.add((int) str[i]);
        }
        return new CharacterSet(kind, set);
    }

    public boolean matchPattern(String string, int start) {
        int patternIdx = 0;
        int stringIdx = start;
        while (patternIdx < pattern.size() && stringIdx < string.length()) {
            CharacterMatcher curr = pattern.get(patternIdx);
            if (!curr.doesItAllow(string.charAt(stringIdx))) {
                return false;
            }
            patternIdx++;
            stringIdx++;
        }
        return patternIdx == pattern.size();
    }
}