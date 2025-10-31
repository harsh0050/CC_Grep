import java.util.ArrayList;

public class PatternV3 {

    private final AutomataState start;
    private final ArrayList<Anchor> anchors;
    private final ArrayList<PatternSequence> savedStrings;

    public PatternV3(String pattern) {
        if (pattern.isEmpty()) throw new RuntimeException("Unhandled pattern: " + pattern);
        start = new AutomataState();
        anchors = new ArrayList<>();
        savedStrings = new ArrayList<>();
        int beginIdx = 0;
        int endIdx = pattern.length();
        if (pattern.startsWith("^")) {
            anchors.add(Anchor.START_OF_LINE);
            beginIdx++;
        }
        if (pattern.endsWith("$")) {
            anchors.add(Anchor.END_OF_LINE);
            endIdx--;
        }
        pattern = pattern.substring(beginIdx, endIdx);
        AutomataState state = buildPattern(pattern, new TerminalState());
        if (state == null) {
            throw new RuntimeException("Unhandled pattern: " + pattern);
        }
        start.addTransitionTo(state);
    }

    private AutomataState buildPattern(String stringPattern, AutomataState end) {
        if (stringPattern.isEmpty()) return end;
        char[] pattern = stringPattern.toCharArray();
        boolean isBackref = false;
        int idx = 0;
        AutomataState currStart, currEnd;
        currStart = currEnd = new AutomataState();

        if (pattern[idx] == '\\') {
            if (idx + 1 == pattern.length) return null;
            switch (pattern[++idx]) {
                case 'w' -> currStart.addTransition(new NormalTransition(new WordCharCharacterClass()));
                case 'd' -> currStart.addTransition(new NormalTransition(new DigitCharacterClass()));
                default -> currStart.addTransition(new NormalTransition(new CharLiteral(pattern[idx])));
            }
        } else if (pattern[idx] == '[') {
            idx++;
            int start = idx;
            while (idx < pattern.length && pattern[idx] != ']') {
                idx++;
            }
            if (idx == pattern.length) return null;
            CharacterSet characterSet = Pattern.getPatternCharacterSet(stringPattern, start, idx - 1);
            if (characterSet == null) return null;
            currStart.addTransition(new NormalTransition(new CharSetCharacterClass(characterSet)));
        } else if (pattern[idx] == '.') {
            currStart.addTransition(new NormalTransition(new WildCard()));
        } else if (pattern[idx] == '(') {
            isBackref = true;
            currStart = new AutomataState();
            currEnd = new AutomataState();
            idx++;
            int tempStart = idx;
            int count = 1;
            while (idx <= pattern.length && count != 0) {
                if (pattern[idx] == '(') count++;
                else if (pattern[idx] == ')') count--;
                if ((pattern[idx] == '|' && count == 1) || count == 0) {
                    String substring = stringPattern.substring(tempStart, idx);
                    Transition epsTransition = new EpsilonTransition();
                    epsTransition.destination = buildPattern(substring, currEnd);
                    currStart.addTransition(epsTransition);
                    tempStart = idx + 1;
                }
                idx++;
            }
            idx--;
            if (count != 0) return null;
        } else {
            currStart.addTransition(new NormalTransition(new CharLiteral(pattern[idx])));
        }

        if (idx + 1 < pattern.length && (pattern[idx + 1] == '?')) {
            idx++;
            // CurrStart -> CurrEnd -> NewStart/End -> next
            //     ^-----------------------|
            AutomataState newState = new AutomataState();
            currEnd.addTransitionTo(newNode);
            newNode.addTransitionTo(currStart);
            currStart = currEnd = newNode;
        }

    }

    enum Anchor {
        START_OF_LINE,
        END_OF_LINE
    }
}
