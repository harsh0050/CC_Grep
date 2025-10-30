import java.util.ArrayList;

public class PatternV2 {
    private final State start;
    private final ArrayList<Anchor> anchors;
    private final ArrayList<StringLiteralState> backreferences;

    public PatternV2(String pattern) {
        if (pattern.isEmpty()) throw new RuntimeException("Unhandled pattern: " + pattern);
        start = new SingleTransitionNullState();
        anchors = new ArrayList<>();
        backreferences = new ArrayList<>();
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
        State state = buildPattern(pattern, new TerminalState());
        if (state == null) {
            throw new RuntimeException("Unhandled pattern: " + pattern);
        }
        start.addTransitionTo(state);
    }

    public boolean match(String string) {
        if (anchors.contains(Anchor.START_OF_LINE)) {
            return match(string, start, 0);
        }

        for (int i = 0; i < string.length(); i++) {
            if (match(string, start, i)) return true;
        }
        return false;
    }

    public boolean match(String string, State state, int fromIndex) {
        if (state instanceof TerminalState) {
            if (anchors.contains(Anchor.END_OF_LINE)) {
                return fromIndex == string.length();
            }
            return true;
        }
        // TODO (string empty and remains nodes are Stars)
//        if (string.isEmpty()) return false;
        int idx = fromIndex;
        if (!(state instanceof NullState)) {
            if (fromIndex == string.length()) return false;
            if(!state.doesItAllow(string.charAt(idx))) return false;
            state.consume(idx++);
        }else{
            state.consume(idx);
        }
        switch (state) {
            case BackreferenceState backreferenceState -> {
                String substring = string.substring(backreferenceState.getBeginIndex(), backreferenceState.getEndIndex());
                backreferences.add(new StringLiteralState(substring));
                return match(string, backreferenceState.getNext(), idx);
            }
            case SingleTransitionState singleTransitionState -> {
                return match(string, singleTransitionState.getNext(), idx);
            }
            case SingleTransitionNullState singleTransitionNullState -> {
                return match(string, singleTransitionNullState.getNext(), idx);
            }
            case MultiTransitionNullState multiTransitionNullState -> {
                for (State next : multiTransitionNullState.transitions) {
                    if (match(string, next, idx)) return true;
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + state);
        }
        return false;
    }

    private State buildPattern(String pattern, State end) {
        if (pattern.isEmpty()) return end;
        char[] pat = pattern.toCharArray();

        boolean isBackref = false;
        int idx = 0;
        State currStart, currEnd;
        if (pat[idx] == '\\') {
            if (idx + 1 == pat.length) return null;
            currStart = switch (pat[++idx]) {
                case 'w' -> currEnd = new SingleTransitionState(new WordCharCharacterClass());
                case 'd' -> currEnd = new SingleTransitionState(new DigitCharacterClass());
                default -> currEnd = new SingleTransitionState(new CharLiteral(pat[idx]));
            };
        } else if (pat[idx] == '[') {
            idx++;
            int start = idx;
            while (idx < pat.length && pat[idx] != ']') {
                idx++;
            }
            if (idx == pat.length) return null;
            CharacterSet characterSet = Pattern.getPatternCharacterSet(pattern, start, idx - 1);
            if (characterSet == null) return null;
            currStart = currEnd = new SingleTransitionState(new CharSetCharacterClass(characterSet));
        } else if (pat[idx] == '.') {
            currStart = currEnd = new SingleTransitionState(new WildCard());
        } else if (pat[idx] == '(') {
            isBackref = true;
            currStart = new MultiTransitionNullState();
            currEnd = new SingleTransitionNullState();
            idx++;
            int tempStart = idx;
            int count = 1;
            while (idx <= pat.length && count != 0) {
                if (pat[idx] == '(') count++;
                else if (pat[idx] == ')') count--;
                if ((pat[idx] == '|' && count == 1) || count == 0) {
                    String substring = pattern.substring(tempStart, idx);
                    currStart.addTransitionTo(buildPattern(substring, currEnd));
                    tempStart = idx + 1;
                }
                idx++;
            }
            idx--;
            if (count != 0) return null;

        } else {
            currStart = currEnd = new SingleTransitionState(new CharLiteral(pat[idx]));
        }


        if (idx + 1 < pat.length && (pat[idx + 1] == '+')) {
            idx++;
            String previous = pattern.substring(0,idx);
            State cloneEnd = new SingleTransitionNullState();
            State clone = buildPattern(previous, cloneEnd);

            State newNode = new MultiTransitionNullState();
            currEnd.addTransitionTo(newNode);
            newNode.addTransitionTo(currStart);
            cloneEnd.addTransitionTo(newNode);
            currEnd = newNode;
            currStart = clone;

            //for +
            //                          Clone (NewStart)
            //                            V
            // CurrStart -> CurrEnd -> NewEnd -> next
            //     ^-----------------------|

        }
        if (idx + 1 < pat.length && (pat[idx + 1] == '?')) {
            idx++;
            // CurrStart -> CurrEnd -> NewStart/End -> next
            //     ^-----------------------|
            State newNode = new MultiTransitionNullState();
            currEnd.addTransitionTo(newNode);
            newNode.addTransitionTo(currStart);
            currStart = currEnd = newNode;
        }

        State next = buildPattern(pattern.substring(idx + 1), end);
        if(isBackref){
            State backrefState = new BackreferenceState(currStart, currEnd);
            currEnd.addTransitionTo(backrefState);
            currEnd = backrefState;
        }
        currEnd.addTransitionTo(next);
        return currStart;
    }


    enum Anchor {
        START_OF_LINE,
        END_OF_LINE
    }

}
