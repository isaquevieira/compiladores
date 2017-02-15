import java.lang.Exception;
import java.util.*;

enum Operator { ADD, SUB, MUL, DIV, RST, SQRT, EXP, LN }
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
    @Override
    public String toString() {
        return String.valueOf(left) + String.valueOf(op) + String.valueOf(right);
    }
}

class UnaryExp extends ASTExp {
    Operator op;
    UnaryExp(Operator x, ASTExp y) {
        op = x; left = y; right = null;
    }
    @Override
    public String toString() {
        return String.valueOf(op);
    }
}

class ExpL extends ASTExp {
    OpRel op;
    ExpL(OpRel o, ASTExp a, ASTExp b) {
        op = o; left = a; right = b;
    }
    @Override
    public String toString() {
        return String.valueOf(left) + String.valueOf(op) + String.valueOf(right);
    }
}

class IfExp extends ASTExp {
    ASTExp middle;
    IfExp(ASTExp e1, ASTExp e2, ASTExp e3) {
        left = e1; middle = e2; right = e3;
    }
    @Override
    public String toString() {
        return String.valueOf(left) + String.valueOf(middle) + String.valueOf(right);
    }
}

class DefVar extends ASTExp {
    String name;
    DefVar(String n) {
        name = n;
    }
    @Override
    public String toString() {
        return "=";
    }
}

class Var extends ASTExp {
    String name;
    Var(String n) {
        name = n;
    }
    @Override
    public String toString() {
        return String.valueOf(name); //;
    }
}

class DefFunc extends ASTExp {
    String name;
    List<String> params;
    DefFunc(String n, List<String> l) {
        name = n;
        params = l;
    }
    @Override
    public String toString() {
        return "func";//String.valueOf(name) + " = " + String.valueOf(left);
    }
}

class Func extends ASTExp {
    String name;
    List<ASTExp> params;
    Map<String, ASTExp> localvars;

    Func(String n, List<ASTExp> params) {
        this.name = n;
        this.params = params;
        localvars = new LinkedHashMap<String, ASTExp>();
        //
        // for (String name: expList) {
        //     localvars.put(name, null);
        // }
    }
}

class SeqExp extends ASTExp {
    public List<ASTExp> list;
    ASTExp res;

    SeqExp() {
        list = new ArrayList<ASTExp>();
        res = null;
    }

    void add(ASTExp param){
        list.add(param);
    }
}

class NumberConst extends ASTExp {
    double val;
    NumberConst(double x) {
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
    public LinkedList<HashMap<String, ASTExp>> definicoes;

    Interpreter() {
        definicoes = new LinkedList<HashMap<String, ASTExp>>();
        newContext();
        inserir_nativos();
    }

    public void inserir_pi(){
        NumberConst valor = new NumberConst(Math.PI);
        put("pi", valor);
    }

    public void inserir_e(){
        NumberConst valor = new NumberConst(Math.E);
        put("e", valor);
    }

    public void f_sqrt(){
        List<String> params = new ArrayList<String>();
        params.add("n");
        UnaryExp u = new UnaryExp(Operator.SQRT, new Var("n"));
        DefFunc f = new DefFunc("sqrt", params);
        f.left = u;
        put(f.name,f);
    }

    public void f_exp(){
        List<String> params = new ArrayList<String>();
        params.add("n");
        UnaryExp u = new UnaryExp(Operator.EXP, new Var("n"));
        DefFunc f = new DefFunc("exp", params);
        f.left = u;
        put(f.name,f);
    }

    public void f_ln(){
        List<String> params = new ArrayList<String>();
        params.add("n");
        UnaryExp u = new UnaryExp(Operator.LN, new Var("n"));
        DefFunc f = new DefFunc("ln", params);
        f.left = u;
        put(f.name,f);
    }

    public void inserir_nativos(){
        inserir_pi();
        inserir_e();
        f_sqrt();
        f_exp();
        f_ln();
    }

    public ASTExp find(String name) {
        Iterator itr = definicoes.descendingIterator();
        while(itr.hasNext()) {
            HashMap<String, ASTExp> element = (HashMap<String, ASTExp>) itr.next();
            ASTExp exp = element.get(name);
            if (exp != null){
                return exp;
            }
        }
        return null;
    }

    public void put(String name, ASTExp exp) {
        definicoes.getLast().put(name, exp);
    }

    public void newContext() {
        HashMap<String, ASTExp> c = new HashMap<String, ASTExp>();
        definicoes.add(c);
    }

    public void closeContext() {
        definicoes.removeLast();
    }

    public ASTExp eval(ASTExp exp) throws Exception
    {
        if(exp == null){
            throw new Exception("Erro na AST");
        }
        else {
            if (exp instanceof SeqExp) {
                SeqExp e = (SeqExp) exp;
                newContext();
                for (ASTExp elem: e.list) {
                    eval(elem);
                }
                ASTExp ret = eval(e.res);
                closeContext();
                return ret;
            }
            else if (exp instanceof DefVar) {
                DefVar var = (DefVar) exp;
                ASTExp e = eval(var.left);
                put(var.name, e);
                return e;
            }
            else if (exp instanceof Var) {
                String name = ((Var) exp).name;
                ASTExp e = find(name);
                if (e == null) {
                    throw new Exception(name + " not defined");
                }
                else {
                    return eval(e);
                }
            }
            else if (exp instanceof DefFunc) {
                DefFunc func = (DefFunc) exp;
                newContext();
                put(func.name, func);
                return exp;
            }
            else if (exp instanceof Func) {
                Func f = (Func) exp;
                DefFunc def = (DefFunc) find(f.name);
                if (def == null) {
                    throw new Exception(f.name + " not defined");
                }
                else {
                    newContext();
                    if (f.params.size() == def.params.size()){
                        for (int i=0; i<f.params.size(); i++){
                            put(def.params.get(i), eval(f.params.get(i)));
                        }
                    }
                    else {
                        throw new Exception("Number of parameters doesn't match");
                    }
                    ASTExp e = eval(def.left);
                    closeContext();
                    return e;
                }
            }
            else if (exp instanceof NumberConst){
                return exp;
            }
            else if(exp instanceof UnaryExp){
                ASTExp e = eval(exp.left);
                if (e instanceof NumberConst) {
                    NumberConst n = (NumberConst) e;
                    switch(((UnaryExp) exp).op) {
                        case SUB: return new NumberConst(0 - n.val);
                        case SQRT: return new NumberConst(Math.sqrt(n.val));
                        case EXP: return new NumberConst(Math.exp(n.val));
                        case LN: return new NumberConst(Math.log(n.val));
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
