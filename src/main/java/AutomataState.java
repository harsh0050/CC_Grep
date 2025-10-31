import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AutomataState {
    public int matchedIndex;
    public List<Transition> transitions;

    public AutomataState() {
        transitions = new LinkedList<>();
    }

    public void addTransition(Transition transition) {
        this.transitions.add(transition);
    }
}

class BackreferenceAutomataState extends AutomataState {
    public AutomataState beginState;
    public int backreferenceIndex;

    public BackreferenceAutomataState(AutomataState beginState) {
        this.beginState = beginState;
    }

    public void save(String originalString, List<PatternSequence> savedStrings) {
        String substring = originalString.substring(beginState.matchedIndex, this.matchedIndex);
        //this.matchedIndex is at correct position.
        savedStrings.get(backreferenceIndex).setString(substring);
    }
}

