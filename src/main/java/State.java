import java.util.ArrayList;

public
abstract class State {
    RegexToken regexToken;
    int matchedIndex;

    public State(RegexToken regexToken) {
        this.regexToken = regexToken;
        matchedIndex = -1;
    }


    public abstract void addTransitionTo(State state);

    public abstract State clone();

    public abstract boolean doesItAllow(int codePoint);

    public void consume(int index) {
        this.matchedIndex = index;
    }
}

abstract class NullState extends State {
    public NullState() {
        super(null);
    }

    @Override
    public boolean doesItAllow(int codePoint) {
        return false;
    }

//    @Override
//    public void consume(int index) {
//        super.consume(index - 1); //since it doesn't consume current char.
//    }
}

class SingleTransitionState extends State {
    private State next;

    public SingleTransitionState(RegexToken regexToken) {
        super(regexToken);
    }

    @Override
    public void addTransitionTo(State state) {
        this.next = state;
    }

    @Override
    public State clone() {
        SingleTransitionState clone = new SingleTransitionState(this.regexToken);
        clone.next = this.next;
        return clone;
    }

    @Override
    public boolean doesItAllow(int codePoint) {
        return this.regexToken.doesItAllow(codePoint);
    }

    public State getNext() {
        return next;
    }

    public void setNext(State next) {
        this.next = next;
    }
}

class SingleTransitionNullState extends NullState {
    private State next;

    @Override
    public void addTransitionTo(State state) {
        this.next = state;
    }

    @Override
    public State clone() {
        SingleTransitionNullState clone = new SingleTransitionNullState();
        clone.next = this.next;
        return clone;
    }

    public State getNext() {
        return next;
    }

    public void setNext(State next) {
        this.next = next;
    }
}

class BackreferenceState extends SingleTransitionNullState {
    private final State begin;
    private final State end;

    public BackreferenceState(State begin, State end) {
        this.begin = begin;
        this.end = end;
    }

    public int getBeginIndex() {
        return begin.matchedIndex;
    }

    public int getEndIndex() {
        return end.matchedIndex;
    }
}

//class BackReferenceState extends SingleTransitionNullState {
//    private State end;
//    private State start;
//
//
//    public BackReferenceState(State start, State end) {
//        this.start = start;
//        this.end = end;
//    }

//    @Override
//    public State clone() {
//        BackReferenceState backReferenceState = (BackReferenceState) super.clone();
////        backReferenceState.start = new
//    }
//}


class TerminalState extends NullState {

    @Override
    public void addTransitionTo(State state) {
    }

    @Override
    public State clone() {
        return null;
    }
}

class MultiTransitionNullState extends NullState {
    ArrayList<State> transitions;

    public MultiTransitionNullState() {
        transitions = new ArrayList<>();
    }

    @Override
    public void addTransitionTo(State state) {
        transitions.add(state);
    }

    @Override
    public State clone() {
        MultiTransitionNullState clone = new MultiTransitionNullState();
        clone.transitions.addAll(this.transitions);
        return clone;
    }
}

class StringLiteralState extends SingleTransitionState {
    ArrayList<RegexToken> tokens;
    private int currentIndex;

    public StringLiteralState(String string) {
        super(null);
        currentIndex = 0;
        tokens = new ArrayList<>();
        for (char ch : string.toCharArray()) {
            tokens.add(new CharLiteral(ch));
        }
    }

    @Override
    public void addTransitionTo(State state) {
        super.setNext(state);
    }

    @Override
    public State clone() {
        StringLiteralState clone = (StringLiteralState) super.clone();
        clone.currentIndex = this.currentIndex;
        clone.tokens = new ArrayList<>(this.tokens);
        return clone;
    }

    @Override
    public boolean doesItAllow(int codePoint) {
        return tokens.get(currentIndex).doesItAllow(codePoint);
    }

    @Override
    public State getNext() { //keep returning this instance until the tokens list is consumed
        currentIndex++;
        if (currentIndex == tokens.size()) {
            currentIndex = 0; //reset
            return super.getNext();
        }
        return this;
    }

}


