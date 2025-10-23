import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class Pattern {
    private final ArrayList<PatternNode> pattern;
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

    private static ArrayList<PatternNode> buildPattern(String pattern) {
        ArrayList<PatternNode> ls = new ArrayList<>();
        int idx = 0;
        char[] arrPattern = pattern.toCharArray();
        while (idx < arrPattern.length) {
            if (arrPattern[idx] == '+') { // considers + as a normal regex token and a star regex token.
                ls.add(new PatternNode(ls.getLast().regexToken.clone()));
                ls.getLast().regexToken.quantifier = Quantifier.GREEDY_STAR;
            } else if (arrPattern[idx] == '?') {
                ls.getLast().regexToken.quantifier = Quantifier.GREEDY_STAR;
            } else if (arrPattern[idx] == '\\') {
                if (idx + 1 == arrPattern.length) return null;
                switch (arrPattern[idx + 1]) {
                    case 'w':
                        ls.add(new PatternNode(new WordCharCharacterClass()));
                        break;
                    case 'd':
                        ls.add(new PatternNode(new DigitCharacterClass()));
                        break;
                    default:
                        ls.add(new PatternNode(new CharLiteral(arrPattern[idx + 1])));
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
                ls.add(new PatternNode(new CharSetCharacterClass(characterSet)));
            } else if (arrPattern[idx] == '.') {
                ls.add(new PatternNode(new WildCard()));
            } else {
                ls.add(new PatternNode(new CharLiteral(arrPattern[idx])));
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
            return match(string, 0, 0) != -1;
        } else if (anchor == Anchor.EXACT) {
            return match(string, 0, 0) == string.length();
        }

        for (int i = 0; i < string.length(); i++) {
            int match = match(string, i, 0);
            if (anchor == Anchor.NONE && match != -1) {
                return true;
            } else if (anchor == Anchor.END_OF_LINE && match == string.length()) {
                return true;
            }
        }
        return false;
    }

    /**
     * return the index from the "string" after matching the found pattern
     */
    private int match(String string, int stringStart, int patternStart) {
        int patternIdx = patternStart;
        int stringIdx = stringStart;
        while (patternIdx < pattern.size() && stringIdx < string.length()) {
            RegexToken curr = pattern.get(patternIdx).regexToken;
            if (curr.quantifier == Quantifier.GREEDY_STAR) {
//                int count = 0;
                int match = match(string, stringIdx, patternIdx + 1); // skip the token
                if (match != -1) return match;
                while (stringIdx < string.length() && curr.doesItAllow(string.charAt(stringIdx))) {
                    stringIdx++;
                    match = match(string, stringIdx, patternIdx + 1);
                    if (match != -1) return match;
                }
                patternIdx++;
                continue;
            }
            if (!curr.doesItAllow(string.charAt(stringIdx))) {
                return -1;
            }
            patternIdx++;
            stringIdx++;
        }
        if (patternIdx != pattern.size())
            return -1;
        return stringIdx;
    }

    enum Anchor {
        START_OF_LINE,
        END_OF_LINE,
        EXACT,
        NONE
    }
}

class PatternNode {
    RegexToken regexToken;
    ArrayList<PatternNode> alterations;

    public PatternNode() {
        alterations = new ArrayList<>();
    }

    public PatternNode(RegexToken regexToken) {
        this.regexToken = regexToken;
    }
}
