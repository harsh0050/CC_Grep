import java.util.ArrayList;
import java.util.HashSet;

public class PatternParser {

    /**
     * @param end exclusive*/
    static Node buildPattern(char[] pattern, int idx, int end, Node altEnd) {
        Node dummy = new Node(NodeType.DUMMY);
        Node trav = dummy;
        while (idx < end) {
            boolean goNext = true;
            if (pattern[idx] == '\\') {
                idx++;
                if (idx == pattern.length) return null;
                trav.next = new Node(getCharacterClass(pattern[idx]));
            } else if (pattern[idx] == '[') {
                idx++;
                int start = idx;
                while (idx < pattern.length && pattern[idx] != ']') {
                    idx++;
                }
                if (idx == pattern.length) return null;
                CharacterSet characterSet = parseCharacterSet(pattern, start, idx - 1);
                if (characterSet == null) return null;
                trav.next = new Node(characterSet);
            } else if (pattern[idx] == '.') {
                trav.next = new Node(new WildCard());
            } else if (pattern[idx] == '?') {
                goNext = false;
                trav.quantifier = Quantifier.GREEDY_STAR;
            } else if (pattern[idx] == '+') {
                trav.next = trav.clone();
                trav.next.quantifier = Quantifier.GREEDY_STAR;
            } else if (pattern[idx] == '(') {
                int count = 1;
                idx++;
                int innerAltStart = idx;
                while (count != 0 && idx < pattern.length) {
                    if (pattern[idx] == '(') count++;
                    else if (pattern[idx] == ')') {
                        count--;
                        idx--;
                    } else if (pattern[idx] == '\\') {
                        idx++;
                    }
                    idx++;
                } //idx will at ')'
                if (count != 0) return null;
                trav.next = getAlternation(pattern, innerAltStart, idx);
            } else {
                trav.next = new Node(new CharLiteral(pattern[idx]));
            }
            if (goNext) trav = trav.next;
        }
        trav.next = altEnd;
        return dummy.next;
    }

    /**
     * divides pattern into blocks using | delimiter and gets head of each of those sub patterns
     *
     * @param start is inclusive;
     * @param end   is exclusive;
     */
    static Node getAlternation(char[] pattern, int start, int end) {
        int blockStart = start;
        Node node = new Node(NodeType.ALTERNATION_START);
        Node altEnd = new Node(NodeType.ALTERNATION_END);
        for (int i = start; i <= end; i++) {
            if (i == end || pattern[i] == '|') {
                node.alternation.add(buildPattern(pattern, blockStart, i-1, altEnd));
                blockStart = i+1;
            } else if(pattern[i] == '\\'){
                i++;
            }
        }
        return node;
    }

    static RegexToken getCharacterClass(char ch) {
        return switch (ch) {
            case 'w' -> new WordCharCharacterClass();
            case 'd' -> new DigitCharacterClass();
            default -> new CharLiteral(ch);
        };
    }


    private static CharacterSet parseCharacterSet(char[] pattern, int start, int end) {
        if (end - start + 1 <= 0) return null;
        HashSet<Integer> set = new HashSet<>();
        CharacterSetKind kind;
        if (pattern[start] == '^') {
            start++;
            kind = CharacterSetKind.NEGATIVE;
        } else {
            kind = CharacterSetKind.POSITIVE;
        }
        for (int i = start; i <= end; i++) {
            if (pattern[i] == '\\') {
                if (i + 1 > end) return null;
                switch (pattern[i + 1]) {
                    case 'w':
                        set.addAll(WordCharCharacterClass.characters);
                        break;
                    case 'd':
                        set.addAll(DigitCharacterClass.characters);
                        break;
                    default:
                        set.add((int) pattern[i + 1]);
                        break;
                }
                continue;
            }
            set.add((int) pattern[i]);
        }
        return new CharacterSet(kind, set);
    }

}

class Node implements Cloneable {
    RegexToken regexToken;
    Node next;
    ArrayList<Node> alternation;
    NodeType nodeType;
    Quantifier quantifier;

    public Node(RegexToken regexToken) {
        this.regexToken = regexToken;
        this.nodeType = NodeType.DEFAULT;
        this.quantifier = Quantifier.NONE;
    }

    public Node(NodeType nodeType) {
        if (nodeType == NodeType.ALTERNATION_START)
            this.alternation = new ArrayList<>();
        this.nodeType = nodeType;
        this.quantifier = Quantifier.NONE;
    }

    @Override
    public Node clone() {
        try {
            Node clone = (Node) super.clone();
            clone.quantifier = this.quantifier;
            clone.nodeType = this.nodeType;
            clone.alternation = this.alternation;
            clone.regexToken = this.regexToken;
            clone.next = this.next;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}

enum NodeType {
    DEFAULT,
    ALTERNATION_START,
    DUMMY,
    ALTERNATION_END
}
