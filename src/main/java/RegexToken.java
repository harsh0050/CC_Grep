import java.util.HashSet;

public abstract class RegexToken implements Cloneable {
    public int length;

    public abstract boolean doesItAllow(int codePoint);

    public boolean doesItAllow(String inputString, int fromIndex) {
        return doesItAllow(inputString.charAt(fromIndex));
    }

    @Override
    public RegexToken clone() {
        try {
            RegexToken clone = (RegexToken) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e.getMessage());
        }
    }
}

class CharLiteral extends RegexToken {
    int codePoint;

    public CharLiteral(int codePoint) {
        this.codePoint = codePoint;
        this.length = 1;
    }

    @Override
    public boolean doesItAllow(int codePoint) {
        return codePoint == this.codePoint;
    }

    @Override
    public CharLiteral clone() {
        CharLiteral clone = (CharLiteral) super.clone();
        clone.codePoint = this.codePoint;
        return clone;
    }

    @Override
    public String toString() {
        return "CharLiteral: '%c'".formatted((char) codePoint);
    }
}

class DigitCharacterClass extends RegexToken {
    public DigitCharacterClass() {
        this.length = 1;
    }

    @Override
    public boolean doesItAllow(int codePoint) {
        return Character.isDigit(codePoint);
    }
}

class WordCharCharacterClass extends RegexToken {
    public WordCharCharacterClass() {
        this.length = 1;
    }

    @Override
    public boolean doesItAllow(int codePoint) {
        return Character.isLetterOrDigit(codePoint) || codePoint == '_';
    }
}

class WildCard extends RegexToken {
    public WildCard() {
        this.length = 1;
    }

    @Override
    public boolean doesItAllow(int codePoint) {
        return true;
    }
}

class PatternSequence extends RegexToken {
    private String string;

    public void setString(String string) {
        this.string = string;
        this.length = string.length();
    }

    @Override
    public boolean doesItAllow(String inputString, int fromIndex) {
        return inputString.startsWith(this.string, fromIndex);
    }

    @Override
    public boolean doesItAllow(int codePoint) {
        return false;
    }

    @Override
    public PatternSequence clone() {
         PatternSequence clone = (PatternSequence) super.clone();
         clone.string = this.string;
         clone.length = this.length;
         return clone;
    }
}

class CharSetCharacterClass extends RegexToken {
    private CharacterSet characterSet;

    public CharSetCharacterClass(CharacterSet characterSet) {
        this.characterSet = characterSet;
        this.length = 1;
    }

    @Override
    public boolean doesItAllow(int codePoint) {
        return characterSet.doesSetAllow(codePoint);
    }

    @Override
    public RegexToken clone() {
        CharSetCharacterClass clone = (CharSetCharacterClass) super.clone();
        clone.characterSet = this.characterSet;
        return clone;
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

enum Quantifier {
    GREEDY_STAR,
    NONE
}