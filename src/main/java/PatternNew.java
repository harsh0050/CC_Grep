public class PatternNew {
    private final Node pattern;
    private final Pattern.Anchor anchor;

    public PatternNew(String patternString) {
        if (patternString.isEmpty()) throw new RuntimeException("Unhandled pattern: " + patternString);
        int beginIdx = 0;
        int endIdx = patternString.length();
        if (patternString.startsWith("^")) {
            if (patternString.endsWith("$")) {
                anchor = Pattern.Anchor.EXACT;
                endIdx--;
            } else
                anchor = Pattern.Anchor.START_OF_LINE;
            beginIdx++;
        } else if (patternString.endsWith("$")) {
            anchor = Pattern.Anchor.END_OF_LINE;
            endIdx--;
        } else {
            anchor = Pattern.Anchor.NONE;
        }
        this.pattern = PatternParser.buildPattern(patternString.toCharArray(), 0, patternString.length(), new Node(NodeType.ALTERNATION_END));
        if (this.pattern == null) throw new RuntimeException("Unhandled pattern: " + patternString);
    }

    private int matchHere(char[] string, int stringIdx, Node currentPatternNode) {
        if (currentPatternNode == null) return stringIdx;
        if (currentPatternNode.quantifier == Quantifier.GREEDY_STAR) {
            int match = matchHere(string, stringIdx, currentPatternNode.next); //skip the token
            if (match != -1) return match;
//            matchHere(string, stringIdx)
        }
        if (currentPatternNode.nodeType == NodeType.ALTERNATION_START) {
            for (Node subPattern : currentPatternNode.alternation) {
                int match = matchHere(string, stringIdx, subPattern);
                if (match != -1) return match;
            }
            return -1;
        }
        if (currentPatternNode.nodeType == NodeType.DEFAULT) {
            if (!currentPatternNode.regexToken.doesItAllow(string[stringIdx])) return -1;
            return matchHere(string, stringIdx + 1, currentPatternNode.next);
        }
        return -1;


    }
}
