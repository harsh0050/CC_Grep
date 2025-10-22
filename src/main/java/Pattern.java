import java.util.ArrayList;
import java.util.HashSet;

public class Pattern {
    private final ArrayList<CharacterMatcher> pattern;
    public final Anchor anchor;

    public Pattern(String patternString) {
        if (patternString.isEmpty()) throw new RuntimeException("Unhandled pattern: " + patternString);
        int beginIdx = 0;
        int endIdx = patternString.length();
        if (patternString.startsWith("^")) {
            if (patternString.endsWith("$")) {
                anchor = Anchor.EXACT;
                endIdx--;
            } else
                anchor = Anchor.START_OF_LINE;
            beginIdx++;
        } else if (patternString.endsWith("$")) {
            anchor = Anchor.END_OF_LINE;
            endIdx--;
        } else {
            anchor = Anchor.NONE;
        }
        this.pattern = buildPattern(patternString.substring(beginIdx, endIdx));
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
                    default:
                        return null;
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

    public boolean match(String string) {
        if (anchor == Anchor.START_OF_LINE) {
            return match(string, 0);
        } else if (anchor == Anchor.END_OF_LINE) {
            return string.length() >= pattern.size() && match(string, string.length() - pattern.size());
        } else if (anchor == Anchor.EXACT) {
            return string.length() == pattern.size() && match(string, 0);
        }
        for (int i = 0; i < string.length(); i++) {
            if (match(string, i)) {
                return true;
            }
        }
        return false;
    }

    private boolean match(String string, int start) {
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

    enum Anchor {
        START_OF_LINE,
        END_OF_LINE,
        EXACT,
        NONE
    }
}