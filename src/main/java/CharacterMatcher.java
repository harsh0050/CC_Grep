import java.util.HashSet;

public abstract class CharacterMatcher {
    public abstract boolean doesItAllow(int codePoint);
}
class CharLiteral extends CharacterMatcher {
    int codePoint;
    public CharLiteral(int codePoint) {
        this.codePoint = codePoint;
    }

    @Override
    public boolean doesItAllow(int codePoint) {
        return codePoint == this.codePoint;
    }
}
class DigitCharacterClass extends CharacterMatcher {
    @Override
    public boolean doesItAllow(int codePoint) {
        return Character.isDigit(codePoint);
    }
}

class WordCharCharacterClass extends CharacterMatcher {
    @Override
    public boolean doesItAllow(int codePoint) {
        return Character.isLetterOrDigit(codePoint) || codePoint == '_';
    }
}

class CharSetCharacterClass extends CharacterMatcher {
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


