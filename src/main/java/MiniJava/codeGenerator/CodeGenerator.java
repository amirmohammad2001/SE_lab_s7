package MiniJava.codeGenerator;

import MiniJava.Actions.*;
import MiniJava.Log.Log;
import MiniJava.errorHandler.ErrorHandler;
import MiniJava.scanner.token.Token;
import MiniJava.semantic.symbol.Symbol;
import MiniJava.semantic.symbol.SymbolTable;
import MiniJava.semantic.symbol.SymbolType;

import MiniJava.SimpleStack.SimpleStack ;

import java.util.HashMap;

/**
 * Created by Alireza on 6/27/2015.
 */
public class CodeGenerator {
    private Memory memory = new Memory();
    private SimpleStack<Address> ss = new SimpleStack<Address>();
    private SimpleStack<String> symbolStack = new SimpleStack<>();
    private SimpleStack<String> callStack = new SimpleStack<>();
    private SymbolTable symbolTable;
    private HashMap<Integer , Action> actionMap;

    public CodeGenerator() {
        symbolTable = new SymbolTable(memory);
        actionMap = new HashMap<Integer , Action>();
        actionMap.put(1, new CheckIdAction());
        actionMap.put(2, new PidAction());
        actionMap.put(3, new FPidAction());
        actionMap.put(4, new KPidAction());
        actionMap.put(5, new IntPidAction());
        actionMap.put(6, new StartCallAction());
        actionMap.put(7, new CallAction());
        actionMap.put(8, new ArgAction());
        actionMap.put(9, new AssignAction());
        actionMap.put(10, new AddAction());
        actionMap.put(11, new SubAction());
        actionMap.put(12, new MultAction());
        actionMap.put(13, new LabelAction());
        actionMap.put(14, new SaveAction());
        actionMap.put(15, new WhileAction());
        actionMap.put(16, new JpfSaveAction());
        actionMap.put(17, new JpHereAction());
        actionMap.put(18, new PrintAction());
        actionMap.put(19, new EqualAction());
        actionMap.put(20, new LessThanAction());
        actionMap.put(21, new AndAction());
        actionMap.put(22, new NotAction());
        actionMap.put(23, new DefClassAction());
        actionMap.put(24, new DefMethodAction());
        actionMap.put(25, new PopClassAction());
        actionMap.put(26, new ExtendAction());
        actionMap.put(27, new DefFieldAction());
        actionMap.put(28, new DefVarAction());
        actionMap.put(29, new MethodReturnAction());
        actionMap.put(30, new DefParamAction());
        actionMap.put(31, new LastTypeBoolAction());
        actionMap.put(32, new LastTypeIntAction());
        actionMap.put(33, new DefMainAction());
    }

    public void printMemory() {
        memory.pintCodeBlock();
    }

    public void semanticFunction(int func, Token next) {
        Log.print("codegenerator : " + func);
        actionMap.get(func).execute(this,next);
    }

    public void defMain() {
        //ss.pop();
        memory.add3AddressCode(ss.pop().num, Operation.JP, new Address(memory.getCurrentCodeBlockAddress(), varType.Address), null, null);
        String methodName = "main";
        String className = symbolStack.pop();

        symbolTable.addMethod(className, methodName, memory.getCurrentCodeBlockAddress());

        symbolStack.push(className);
        symbolStack.push(methodName);
    }

    //    public void spid(Token next){
//        symbolStack.push(next.value);
//    }
    public void checkID() {
        symbolStack.pop();
        if (ss.peek().varType == varType.Non) {
            //TODO : error
        }
    }

    public void pid(Token next) {
        if (symbolStack.size() > 1) {
            String methodName = symbolStack.pop();
            String className = symbolStack.pop();
            try {

                Symbol s = symbolTable.get(className, methodName, next.value);
                varType t = varType.Int;
                switch (s.type) {
                    case Bool:
                        t = varType.Bool;
                        break;
                    case Int:
                        t = varType.Int;
                        break;
                }
                ss.push(new Address(s.address, t));


            } catch (Exception e) {
                ss.push(new Address(0, varType.Non));
            }
            symbolStack.push(className);
            symbolStack.push(methodName);
        } else {
            ss.push(new Address(0, varType.Non));
        }
        symbolStack.push(next.value);
    }

    public void fpid() {
        ss.pop();
        ss.pop();

        Symbol s = symbolTable.get(symbolStack.pop(), symbolStack.pop());
        varType t = varType.Int;
        switch (s.type) {
            case Bool:
                t = varType.Bool;
                break;
            case Int:
                t = varType.Int;
                break;
        }
        ss.push(new Address(s.address, t));

    }

    public void kpid(Token next) {
        ss.push(symbolTable.get(next.value));
    }

    public void intpid(Token next) {
        ss.push(new Address(Integer.parseInt(next.value), varType.Int, TypeAddress.Imidiate));
    }

    public void startCall() {
        //TODO: method ok
        ss.pop();
        ss.pop();
        String methodName = symbolStack.pop();
        String className = symbolStack.pop();
        symbolTable.startCall(className, methodName);
        callStack.push(className);
        callStack.push(methodName);

        //symbolStack.push(methodName);
    }

    public void call() {
        //TODO: method ok
        String methodName = callStack.pop();
        String className = callStack.pop();
        try {
            symbolTable.getNextParam(className, methodName);
            ErrorHandler.printError("The few argument pass for method");
        } catch (IndexOutOfBoundsException e) {
        }
        varType t = varType.Int;
        switch (symbolTable.getMethodReturnType(className, methodName)) {
            case Int:
                t = varType.Int;
                break;
            case Bool:
                t = varType.Bool;
                break;
        }
        Address temp = new Address(memory.getTemp(), t);
        ss.push(temp);
        memory.add3AddressCode(Operation.ASSIGN, new Address(temp.num, varType.Address, TypeAddress.Imidiate), new Address(symbolTable.getMethodReturnAddress(className, methodName), varType.Address), null);
        memory.add3AddressCode(Operation.ASSIGN, new Address(memory.getCurrentCodeBlockAddress() + 2, varType.Address, TypeAddress.Imidiate), new Address(symbolTable.getMethodCallerAddress(className, methodName), varType.Address), null);
        memory.add3AddressCode(Operation.JP, new Address(symbolTable.getMethodAddress(className, methodName), varType.Address), null, null);

        //symbolStack.pop();
    }

    public void arg() {
        //TODO: method ok

        String methodName = callStack.pop();
//        String className = symbolStack.pop();
        try {
            Symbol s = symbolTable.getNextParam(callStack.peek(), methodName);
            varType t = varType.Int;
            switch (s.type) {
                case Bool:
                    t = varType.Bool;
                    break;
                case Int:
                    t = varType.Int;
                    break;
            }
            Address param = ss.pop();
            if (param.varType != t) {
                ErrorHandler.printError("The argument type isn't match");
            }
            memory.add3AddressCode(Operation.ASSIGN, param, new Address(s.address, t), null);

//        symbolStack.push(className);

        } catch (IndexOutOfBoundsException e) {
            ErrorHandler.printError("Too many arguments pass for method");
        }
        callStack.push(methodName);

    }

    public void assign() {
        Address s1 = ss.pop();
        Address s2 = ss.pop();
//        try {
        if (s1.varType != s2.varType) {
            ErrorHandler.printError("The type of operands in assign is different ");
        }
//        }catch (NullPointerException d)
//        {
//            d.printStackTrace();
//        }
        memory.add3AddressCode(Operation.ASSIGN, s1, s2, null);
    }

    public void add() {
        Address temp = new Address(memory.getTemp(), varType.Int);
        Address s2 = ss.pop();
        Address s1 = ss.pop();

        if (s1.varType != varType.Int || s2.varType != varType.Int) {
            ErrorHandler.printError("In add two operands must be integer");
        }
        memory.add3AddressCode(Operation.ADD, s1, s2, temp);
        ss.push(temp);
    }

    public void sub() {
        Address temp = new Address(memory.getTemp(), varType.Int);
        Address s2 = ss.pop();
        Address s1 = ss.pop();
        if (s1.varType != varType.Int || s2.varType != varType.Int) {
            ErrorHandler.printError("In sub two operands must be integer");
        }
        memory.add3AddressCode(Operation.SUB, s1, s2, temp);
        ss.push(temp);
    }

    public void mult() {
        Address temp = new Address(memory.getTemp(), varType.Int);
        Address s2 = ss.pop();
        Address s1 = ss.pop();
        if (s1.varType != varType.Int || s2.varType != varType.Int) {
            ErrorHandler.printError("In mult two operands must be integer");
        }
        memory.add3AddressCode(Operation.MULT, s1, s2, temp);
//        memory.saveMemory();
        ss.push(temp);
    }

    public void label() {
        ss.push(new Address(memory.getCurrentCodeBlockAddress(), varType.Address));
    }

    public void save() {
        ss.push(new Address(memory.saveMemory(), varType.Address));
    }

    public void _while() {
        memory.add3AddressCode(ss.pop().num, Operation.JPF, ss.pop(), new Address(memory.getCurrentCodeBlockAddress() + 1, varType.Address), null);
        memory.add3AddressCode(Operation.JP, ss.pop(), null, null);
    }

    public void jpf_save() {
        Address save = new Address(memory.saveMemory(), varType.Address);
        memory.add3AddressCode(ss.pop().num, Operation.JPF, ss.pop(), new Address(memory.getCurrentCodeBlockAddress(), varType.Address), null);
        ss.push(save);
    }

    public void jpHere() {
        memory.add3AddressCode(ss.pop().num, Operation.JP, new Address(memory.getCurrentCodeBlockAddress(), varType.Address), null, null);
    }

    public void print() {
        memory.add3AddressCode(Operation.PRINT, ss.pop(), null, null);
    }

    public void equal() {
        Address temp = new Address(memory.getTemp(), varType.Bool);
        Address s2 = ss.pop();
        Address s1 = ss.pop();
        if (s1.varType != s2.varType) {
            ErrorHandler.printError("The type of operands in equal operator is different");
        }
        memory.add3AddressCode(Operation.EQ, s1, s2, temp);
        ss.push(temp);
    }

    public void less_than() {
        Address temp = new Address(memory.getTemp(), varType.Bool);
        Address s2 = ss.pop();
        Address s1 = ss.pop();
        if (s1.varType != varType.Int || s2.varType != varType.Int) {
            ErrorHandler.printError("The type of operands in less than operator is different");
        }
        memory.add3AddressCode(Operation.LT, s1, s2, temp);
        ss.push(temp);
    }

    public void and() {
        Address temp = new Address(memory.getTemp(), varType.Bool);
        Address s2 = ss.pop();
        Address s1 = ss.pop();
        if (s1.varType != varType.Bool || s2.varType != varType.Bool) {
            ErrorHandler.printError("In and operator the operands must be boolean");
        }
        memory.add3AddressCode(Operation.AND, s1, s2, temp);
        ss.push(temp);
    }

    public void not() {
        Address temp = new Address(memory.getTemp(), varType.Bool);
        Address s2 = ss.pop();
        Address s1 = ss.pop();
        if (s1.varType != varType.Bool) {
            ErrorHandler.printError("In not operator the operand must be boolean");
        }
        memory.add3AddressCode(Operation.NOT, s1, s2, temp);
        ss.push(temp);
    }

    public void defClass() {
        ss.pop();
        symbolTable.addClass(symbolStack.peek());
    }

    public void defMethod() {
        ss.pop();
        String methodName = symbolStack.pop();
        String className = symbolStack.pop();

        symbolTable.addMethod(className, methodName, memory.getCurrentCodeBlockAddress());

        symbolStack.push(className);
        symbolStack.push(methodName);
    }

    public void popClass() {
        symbolStack.pop();
    }

    public void extend() {
        ss.pop();
        symbolTable.setSuperClass(symbolStack.pop(), symbolStack.peek());
    }

    public void defField() {
        ss.pop();
        symbolTable.addField(symbolStack.pop(), symbolStack.peek());
    }

    public void defVar() {
        ss.pop();

        String var = symbolStack.pop();
        String methodName = symbolStack.pop();
        String className = symbolStack.pop();

        symbolTable.addMethodLocalVariable(className, methodName, var);

        symbolStack.push(className);
        symbolStack.push(methodName);
    }

    public void methodReturn() {
        //TODO : call ok

        String methodName = symbolStack.pop();
        Address s = ss.pop();
        SymbolType t = symbolTable.getMethodReturnType(symbolStack.peek(), methodName);
        varType temp = varType.Int;
        switch (t) {
            case Int:
                break;
            case Bool:
                temp = varType.Bool;
        }
        if (s.varType != temp) {
            ErrorHandler.printError("The type of method and return address was not match");
        }
        memory.add3AddressCode(Operation.ASSIGN, s, new Address(symbolTable.getMethodReturnAddress(symbolStack.peek(), methodName), varType.Address, TypeAddress.Indirect), null);
        memory.add3AddressCode(Operation.JP, new Address(symbolTable.getMethodCallerAddress(symbolStack.peek(), methodName), varType.Address), null, null);

        //symbolStack.pop();
    }

    public void defParam() {
        //TODO : call Ok
        ss.pop();
        String param = symbolStack.pop();
        String methodName = symbolStack.pop();
        String className = symbolStack.pop();

        symbolTable.addMethodParameter(className, methodName, param);

        symbolStack.push(className);
        symbolStack.push(methodName);
    }

    public void lastTypeBool() {
        symbolTable.setLastType(SymbolType.Bool);
    }

    public void lastTypeInt() {
        symbolTable.setLastType(SymbolType.Int);
    }

    public void main() {

    }
}
