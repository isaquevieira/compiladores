import java.lang.Exception;
import java.util.ArrayList;
import java.util.List;

enum Operator { ADD, SUB, MUL, DIV, RST }
enum OpRel { GTR, LSS, EQL, GEQL, LEQL, DIF }

class ASTExp
{
    ASTExp left, right;
}

class BinExp extends ASTExp {
    Operator op;
    BinExp(ASTExp a, Operator o, ASTExp b) {
        op = o; left = a; right = b;
    }
}

class UnaryExp extends ASTExp {
    Operator op;
    UnaryExp(Operator x, ASTExp y) {
        op = x; left = y; right = null;
    }
}

class ExpL extends ASTExp {
    OpRel op;
    ExpL(OpRel o, ASTExp a, ASTExp b) {
        op = o; left = a; right = b;
    }
}

class IfExp extends ASTExp {
    ASTExp middle;
    IfExp(ASTExp e1, ASTExp e2, ASTExp e3) {
        left = e1; middle = e2; right = e3;
    }
}

class ParamExp extends ASTExp {
    List<ASTExp> list;

    ParamExp(ASTExp e) {
        list = new ArrayList<ASTExp>();
        list.add(e);
    }
}

class Ident extends ASTExp {
    String name;
    Ident(String n) {
        name = n;
    }
}

class NumberConst extends ASTExp {
    float val;
    NumberConst(float x) {
        val = x;
    }
    @Override
    public String toString() {
        return String.valueOf(val);
    }
}

class BoolConst extends ASTExp {
    boolean val;
    BoolConst(boolean x) {
        val = x;
    }
    @Override
    public String toString() {
        return String.valueOf(val);
    }
}

class Interpreter
{
    public ASTExp eval(ASTExp exp) throws Exception
    {
        if(exp == null){
            System.out.println("ast == null");
        }
        else if (exp instanceof NumberConst){
            return exp;
        }
        else if(exp instanceof UnaryExp){
            ASTExp e = eval(exp.left);
            if (e instanceof NumberConst) {
                switch(((UnaryExp) exp).op) {
                    case SUB: return new NumberConst(0 - ((NumberConst) e).val);
                }
            }
        }
        else if (exp instanceof IfExp) {
            BoolConst e = (BoolConst) eval(exp.left);
            if (e.val) {
                return eval(((IfExp) exp).middle);
            }
            else {
                return eval(exp.right);
            }
        }
        else if (exp instanceof ExpL) {
            ASTExp v1 = eval(exp.left);
            ASTExp v2 = eval(exp.right);
            if(v1 instanceof NumberConst && v2 instanceof NumberConst){
                NumberConst b1, b2;
                b1 = (NumberConst) v1;
                b2 = (NumberConst) v2;
                switch(((ExpL) exp).op) {
                    case GTR: return new BoolConst(b1.val > b2.val);
                    case LSS: return new BoolConst(b1.val < b2.val);
                    case EQL: return new BoolConst(b1.val == b2.val);
                    case GEQL: return new BoolConst(b1.val >= b2.val);
                    case LEQL: return new BoolConst(b1.val <= b2.val);
                    case DIF: return new BoolConst(b1.val != b2.val);
                }
            }
        }
        else {
            ASTExp v1 = eval(exp.left);
            ASTExp v2 = eval(exp.right);
            if(v1 instanceof NumberConst && v2 instanceof NumberConst){
                NumberConst n1, n2;
                n1 = (NumberConst) v1;
                n2 = (NumberConst) v2;
                switch(((BinExp) exp).op) {
                    case ADD: return new NumberConst(n1.val + n2.val);
                    case SUB: return new NumberConst(n1.val - n2.val);
                    case MUL: return new NumberConst(n1.val * n2.val);
                    case DIV: return new NumberConst(n1.val / n2.val);
                    case RST: return new NumberConst(n1.val % n2.val);
                }
            }
        }
        return null;
    }
}
