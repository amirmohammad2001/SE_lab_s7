package MiniJava.scanner.token;

import MiniJava.scanner.type.Type;
import MiniJava.SimpleMatcher.SimpleMatcher;


public class Token {
    public Type type;
    public String value;

    public Token(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("(%s,%s)", type.name(), value);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Token) {
            Token temp = (Token) o;
            if (temp.type == this.type) {
                return this.type != Type.KEYWORDS || this.value.equals(temp.value);
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = type.hashCode();
        if (type == Type.KEYWORDS) result = prime * result + (value == null ? 0 : value.hashCode());
        return result;
    }

    public static Type getTyepFormString(String s) {

        SimpleMatcher simpleMatcher;
        for (Type t : Type.values()) {
            if (t.toString().equals(s)) return t;
        }
        for (Type t : Type.values()) {
            simpleMatcher = new SimpleMatcher(t.pattern , s) ;


            if (simpleMatcher.matches()) return t;
        }

//        if (s.equals("class")||s.equals("extends")||s.equals("public")||s.equals("static")||s.equals("void")||s.equals("return")||s.equals("main")||
//                s.equals("boolean")||s.equals("int")||s.equals("if")||s.equals("else")||s.equals("while")||s.equals("true")||s.equals("false")||s.equals("System.out.println")) {
//            return KEYWORDS;
//        }else if(s.equals("")){
//
//        }else if(s.equals("")){
//
//        }
        throw new IllegalArgumentException();
    }
}
