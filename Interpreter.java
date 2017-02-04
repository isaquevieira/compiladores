import java.lang.Exception;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

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

    ParamExp() {
        list = new ArrayList<ASTExp>();
    }
}

class Ident extends ASTExp {
    String name;
    Ident(String n){
        name = n;
    }
}

class DefVar extends ASTExp {
    String name;
    DefVar(String n) {
        name = n;
    }
}

class Var extends ASTExp {
    String name;
    Var(String n) {
        name = n;
    }
}

class DefFunc extends ASTExp {
    String name;
    DefFunc(String n) {
        name = n;
    }
}

class Func extends ASTExp {
    String name;
    Func(String n) {
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
    Map<String, ASTExp> definicoes = new HashMap<String, ASTExp>();

    public ASTExp eval(ASTExp exp) throws Exception
    {
        if(exp == null){
            throw new Error("Erro na AST");
        }
        else {
            if (exp instanceof DefVar) {
                DefVar var = (DefVar) exp;
                ASTExp e = eval(var.left);
                definicoes.put(var.name, e);
                return e;
            }
            else if (exp instanceof Var) {
                return eval(exp.left);
            }
            else if (exp instanceof Ident){
                String name = ((Ident) exp).name;
                ASTExp e = definicoes.get(name);
                if (e == null) {
                    throw new Error(name + " not defined");
                }
                else {
                    return eval(e);
                }
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
        }
        return null;
    }
}
