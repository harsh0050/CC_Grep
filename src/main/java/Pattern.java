import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class Pattern {
    private final ArrayList<RegexToken> pattern;
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
        this.pattern = PatternParser.buildPattern(patternString.substring(beginIdx, endIdx));
        if (this.pattern == null) throw new RuntimeException("Unhandled pattern: " + patternString);
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
            RegexToken curr = pattern.get(patternIdx);
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