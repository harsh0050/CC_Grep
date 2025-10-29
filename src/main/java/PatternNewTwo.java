import java.util.ArrayList;
import java.util.List;

public class PatternNewTwo {


    static class BaseNode {
        NodeType nodeType;
        BaseNode next;

        public BaseNode(NodeType nodeType) {
            this.nodeType = nodeType;
        }
    }

    static class RegexTokenNode extends BaseNode {
        RegexToken regexToken;

        public RegexTokenNode(RegexToken regexToken) {
            super(NodeType.REGEX_TOKEN);
        }
    }

    static class AlternationStartNode extends BaseNode {
        List<BaseNode> choices;

        public AlternationStartNode() {
            super(NodeType.ALTERNATION_START);
            choices = new ArrayList<>();
        }
    }

    static class EndNode extends BaseNode {
        public EndNode() {
            super(NodeType.END);
        }
    }

    static class NormalStart extends BaseNode{
        BaseNode innerNode;
        public NormalStart(BaseNode node){
            super(NodeType.NORMAL_START);
            this.innerNode = node;
        }
    }

    enum NodeType {
        REGEX_TOKEN,
        ALTERNATION_START,
        NORMAL_START,
        END
    }
}

