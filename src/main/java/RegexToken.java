import java.util.ArrayList;
import java.util.HashSet;

public abstract class RegexToken implements Cloneable {
    Quantifier quantifier;

    public RegexToken() {
        quantifier = Quantifier.NONE;
    }

    public abstract boolean doesItAllow(int codePoint);

    @Override
    public RegexToken clone() {
        try {
            RegexToken clone = (RegexToken) super.clone();
            clone.quantifier = this.quantifier;
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

}

class DigitCharacterClass extends RegexToken {
    @Override
    public boolean doesItAllow(int codePoint) {
        return Character.isDigit(codePoint);
    }

    static HashSet<Integer> characters;

    static {
        characters = new HashSet<>();
        for (char ch = '0'; ch <= '9'; ch++) characters.add((int) ch);
    }
}

class WordCharCharacterClass extends RegexToken {

    @Override
    public boolean doesItAllow(int codePoint) {
        return Character.isLetterOrDigit(codePoint) || codePoint == '_';
    }

    static HashSet<Integer> characters;

    static {
        characters = new HashSet<>();
        for (char ch = '0'; ch <= '9'; ch++) characters.add((int) ch);
        for (char ch = 'a'; ch <= 'z'; ch++) characters.add((int) ch);
        for (char ch = 'A'; ch <= 'Z'; ch++) characters.add((int) ch);
        characters.add((int) '_');
    }
}

class WildCard extends RegexToken {

    @Override
    public boolean doesItAllow(int codePoint) {
        return true;
    }
}

//class CharSetCharacterClass extends RegexToken {
//    private CharacterSet characterSet;
//
//    public CharSetCharacterClass(CharacterSet characterSet) {
//        this.characterSet = characterSet;
//    }
//
//    @Override
//    public boolean doesItAllow(int codePoint) {
//        return characterSet.doesSetAllow(codePoint);
//    }
//
//    @Override
//    public CharSetCharacterClass clone() {
//        CharSetCharacterClass clone = (CharSetCharacterClass) super.clone();
//        clone.characterSet = this.characterSet;
//        return clone;
//    }
//
//}


class Alternation extends RegexToken {
    private ArrayList<Pattern> alternations;

    public Alternation(ArrayList<Pattern> alternations) {
        this.alternations = alternations;
    }

    @Override
    public boolean doesItAllow(int codePoint) {
        for (Pattern pattern : alternations) {
//            pattern.match()
//            if(token.doesItAllow(codePoint)) return true;
        }
        return false;
    }

    @Override
    public Alternation clone() {
        Alternation clone = (Alternation) super.clone();
        clone.alternations = this.alternations;
        return clone;
    }
}

//helper
class CharacterSet extends RegexToken {
    CharacterSetKind kind;
    HashSet<Integer> set;

    public CharacterSet(CharacterSetKind kind, HashSet<Integer> set) {
        this.kind = kind;
        this.set = set;
    }

    @Override
    public boolean doesItAllow(int codePoint) {
        boolean setContains = set.contains(codePoint);
        if (kind == CharacterSetKind.POSITIVE) {
            return setContains;
        } else {
            return !setContains;
        }
    }

    @Override
    public CharacterSet clone() {
        CharacterSet characterSet = (CharacterSet) super.clone();
        characterSet.kind = this.kind;
        characterSet.set = this.set;
        return characterSet;
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