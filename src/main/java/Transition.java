import java.util.LinkedList;
import java.util.List;

public abstract class Transition{
    public AutomataState destination;
    /**@return Subsequent index of the last matched index, or -1 if no match.*/
    public abstract int match(String str, int fromIndex);
}

class NormalTransition extends Transition{
    private final RegexToken token;

    public NormalTransition(RegexToken token) {
        this.token = token;
    }

    @Override
    public int match(String str, int fromIndex) {
        return token.doesItAllow(str.charAt(fromIndex)) ? fromIndex + 1 : -1;
    }
}

class EpsilonTransition extends Transition{
    @Override
    public int match(String str, int fromIndex) {
        return fromIndex;
    }
}
