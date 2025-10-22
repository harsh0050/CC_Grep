import java.util.HashSet;

public abstract class RegexToken {
    Quantifier quantifier;

    public RegexToken() {
        quantifier = Quantifier.NONE;
    }

    public abstract boolean doesItAllow(int codePoint);
}
class CharLiteral extends RegexToken {
    int codePoint;
    public CharLiteral(int codePoint) {
        this.codePoint = codePoint;
    }

    @Override
    public boolean doesItAllow(int codePoint) {
        return codePoint == this.codePoint;
    }
}
class DigitCharacterClass extends RegexToken {
    @Override
    public boolean doesItAllow(int codePoint) {
        return Character.isDigit(codePoint);
    }
}

class WordCharCharacterClass extends RegexToken {
    @Override
    public boolean doesItAllow(int codePoint) {
        return Character.isLetterOrDigit(codePoint) || codePoint == '_';
    }
}

class CharSetCharacterClass extends RegexToken {
    private final CharacterSet characterSet;

    public CharSetCharacterClass(CharacterSet characterSet) {
        this.characterSet = characterSet;
    }

    @Override
    public boolean doesItAllow(int codePoint) {
        return characterSet.doesSetAllow(codePoint);
    }
}

//helper
class CharacterSet {
    CharacterSetKind kind;
    HashSet<Integer> set;

    public CharacterSet(CharacterSetKind kind, HashSet<Integer> set) {
        this.kind = kind;
        this.set = set;
    }

    public boolean doesSetAllow(int codePoint) {
        boolean setContains = set.contains(codePoint);
        if (kind == CharacterSetKind.POSITIVE) {
            return setContains;
        } else {
            return !setContains;
        }
    }
}

enum CharacterSetKind {
    POSITIVE,
    NEGATIVE
}
enum Quantifier{
    GREEDY_PLUS,
    NONE
}


