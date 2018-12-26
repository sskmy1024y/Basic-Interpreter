package syntax_analyzer.nodes;

import lexical_analyzer.*;
import libfunc.CalculateException;
import syntax_analyzer.*;

import java.util.*;
import java.util.List;

public class ExprNode extends Node {
    Node left;
    Node right;
    LexicalType operator;

    private final static Set<LexicalType> first = new HashSet<>(Arrays.asList(
            LexicalType.NAME,
            LexicalType.SUB,
            LexicalType.LP,
            LexicalType.INTVAL,
            LexicalType.DOUBLEVAL,
            LexicalType.LITERAL
    ));

    private final static Map<LexicalType, Integer> OPERATORS = new HashMap<>();
    static {
        // 演算子と優先順位を設定
        OPERATORS.put(LexicalType.DIV,1);
        OPERATORS.put(LexicalType.MUL,2);
        OPERATORS.put(LexicalType.SUB,3);
        OPERATORS.put(LexicalType.ADD,4);
    }


    public static boolean isMatch(LexicalType type){
        return first.contains(type);
    }

    public static Node getHandler(LexicalType type, Environment env) {
        if (isMatch(type)) return new ExprNode(env);
        else return null;
    }

    private ExprNode(Environment env) {
        this.env = env;
        this.type = NodeType.EXPR;
    }

    public ExprNode(Node left, Node right, LexicalType operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public void parse() throws Exception {
        List<Node> result=new ArrayList<>();
        List<LexicalType> operators = new ArrayList<>();

        while(true) {
            switch (env.getInput().peek().getType()) {
                case LP:
                    // LPの破棄
                    env.getInput().get();
                    Node handler = ExprNode.getHandler(env.getInput().peek().getType(), env);
                    handler.parse();
                    result.add(handler);
                    if (env.getInput().get().getType() != LexicalType.RP) {
                        throw new SyntaxException("The composition of the formula is illegal. ')' Can not be found.");
                    }
                    break;
                case INTVAL:
                case DOUBLEVAL:
                case LITERAL:
                    LexicalUnit lu = env.getInput().get();
                    result.add(ConstNode.getHandler(lu.getType(), env, lu.getValue()));
                    break;
                case SUB:
                    if (env.getInput().peek(2).getType() == LexicalType.INTVAL ||
                            env.getInput().peek(2).getType() == LexicalType.DOUBLEVAL ||
                            env.getInput().peek(2).getType() == LexicalType.LP) {
                        env.getInput().get();
                        result.add(ConstNode.getHandler(LexicalType.INTVAL, env, new ValueImpl(-1)));
                        addOperator(result, operators, LexicalType.MUL);
                        continue;
                    } else {
                        // 計算式中に置いて不正な-記号が使われている場合
                        throw new SyntaxException("Illegal - sign is used in calculation formulas.");
                    }
                case NAME:
                    if (env.getInput().peek(2).getType() == LexicalType.LP) {
                        Node tmpNode = CallNode.getHandler(env.getInput().peek().getType(), env);
                        tmpNode.parse();
                        result.add(tmpNode);
                    } else {
                        lu = env.getInput().get();
//                        result.add(VariableNode.getHandler(lu.getType(), env, lu.getValue()));
                        result.add(env.getVariable(lu.getValue().getSValue()));
                    }
                    break;
                default:
                    // 計算式の構成が不正な場合
                    throw new SyntaxException("The composition of the formula is illegal.");
            }


            if (OPERATORS.containsKey(env.getInput().peek().getType())) {
                addOperator(result, operators, env.getInput().get().getType());
            } else {
                break;
            }
        }

        for(int i=operators.size()-1;i>=0;i--){
            if (operators.size()==1){
                left=result.get(0);
                right=result.get(1);
                operator=operators.get(0);
                return;
            }
            result.add(new ExprNode(result.get(result.size()-2),
                    result.get(result.size()-1),operators.get(i)));
            result.remove(result.size()-3);
            result.remove(result.size()-2);
        }
        left = result.get(0);
    }


    private void addOperator (List<Node> rList, List<LexicalType> oList, LexicalType newOperator) throws Exception {
        boolean flag = false;
        for (int i=oList.size()-1; i>=0; i--) {
            if (OPERATORS.get(oList.get(i)) < OPERATORS.get(newOperator)) {
                flag = true;
                rList.add(new ExprNode(rList.get(rList.size()-2), rList.get(rList.size()-1), oList.get(i)));
                rList.remove(rList.size()-3);
                rList.remove(rList.size()-2);
                oList.remove(i);
            } else if (flag && OPERATORS.get(oList.get(i)) >= OPERATORS.get(newOperator)) {
                break;
            }
        }
        oList.add(newOperator);
    }

    public Value getValue() throws Exception {
        if (operator == null) return left.getValue();

        Value leftVal = left.getValue();
        Value rightVal = right.getValue();

        if (leftVal.getType() == ValueType.STRING || rightVal.getType() == ValueType.STRING) {
            switch (operator) {
                case ADD:
                    return new ValueImpl(leftVal.getSValue() + rightVal.getSValue());
                default:
                    throw new CalculateException("Invalid calculate. Can not calculate string.");
            }
        } else if (leftVal.getType() == ValueType.DOUBLE || rightVal.getType() == ValueType.DOUBLE) {
            double leftNum = leftVal.getDValue();
            double rightNum = rightVal.getDValue();
            switch (operator) {
                case ADD:
                    return new ValueImpl(leftNum + rightNum);
                case SUB:
                    return new ValueImpl(leftNum - rightNum);
                case MUL:
                    return new ValueImpl(leftNum * rightNum);
                case DIV:
                    if (rightNum != 0.00) {
                        return new ValueImpl(leftNum / rightNum);
                    } else {
                        throw new CalculateException("Invalid division 'ZERO'.");
                    }
                default:
                    throw new InternalError("Invalid operator.");
            }
        } else {
            int leftNum = leftVal.getIValue();
            int rightNum = rightVal.getIValue();
            switch (operator) {
                case ADD:
                    return new ValueImpl(leftNum + rightNum);
                case SUB:
                    return new ValueImpl(leftNum - rightNum);
                case MUL:
                    return new ValueImpl(leftNum * rightNum);
                case DIV:
                    if (rightNum != 0) {
                        return new ValueImpl(leftNum / rightNum);
                    } else {
                        throw new CalculateException("Invalid division 'ZERO'.");
                    }
                default:
                    throw new InternalError("Invalid operator.");
            }
        }
    }

    public String toString() {
        String tmp="[" + left;
        if (operator!=null){
            tmp+=" "+operator+" ";
            tmp+=right;
        }
        return tmp+"]";
    }
}
