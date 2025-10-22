import java.util.HashSet;

public abstract class CharacterMatcher {
    CharacterClassKind kind;

    public abstract boolean doesItAllow(int codePoint);
}
class CharLiteral extends CharacterMatcher {
    int codePoint;
    public CharLiteral(int codePoint) {
        this.codePoint = codePoint;
        this.kind = CharacterClassKind.LITERAL;
    }

    @Override
    public boolean doesItAllow(int codePoint) {
        return codePoint == this.codePoint;
    }
}
class DigitCharacterClass extends CharacterMatcher {
    public DigitCharacterClass() {
        this.kind = CharacterClassKind.DIGIT;
    }

    @Override
    public boolean doesItAllow(int codePoint) {
        return Character.isDigit(codePoint);
    }
}

class WordCharCharacterClass extends CharacterMatcher {
    public WordCharCharacterClass() {
        this.kind = CharacterClassKind.CHARACTER;
    }

    @Override
    public boolean doesItAllow(int codePoint) {
        return Character.isLetterOrDigit(codePoint);
    }
}

class CharSetCharacterClass extends CharacterMatcher {
    private final CharacterSet characterSet;

    public CharSetCharacterClass(CharacterSet characterSet) {
        this.characterSet = characterSet;
        this.kind = CharacterClassKind.CHARACTER_CLASS;
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

enum CharacterClassKind {
    LITERAL,
    DIGIT,
    CHARACTER,
    CHARACTER_CLASS
}

