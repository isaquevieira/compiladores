import java.lang.Exception;
import java.util.ArrayList;
import java.util.List;

enum Operator { ADD, SUB, MUL, DIV, RST, GTR, LSS, EQL, GEQL, LEQL, DIF }

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

class IfExp extends ASTExp {
    ASTExp e1, e2, e3;
    IfExp(ASTExp e1, ASTExp e2, ASTExp e3) {
        this.e1 = e1; this.e2 = e2; this.e3 = e3;
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
        // if(ast instanceof UnaryExp){
        //     ASTExp e = EvaluateSubtree(ast.left);
        //     if e instanceof NumberConst {
        //         switch(e.op) {
        //             case Operator.SUB: return 0 - e;
        //         }
        //     }
        // }
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
                }
            }
        }
        return null;
    }
}
