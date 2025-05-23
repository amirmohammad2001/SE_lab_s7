package MiniJava.scanner.type;


import MiniJava.SimpleMatcher.SimpleMatcher;
/**
 * Created by Alireza on 2015-05-26.
 */
public enum Type {
    KEYWORDS("class|extends|public|static|void|return|main|boolean|int|if|else|while|true|false|System.out.println"),
    COMMENT("(/\\*([^*]|[\\r\\n]|(\\*+([^*/]|[\\r\\n])))*\\*+/)|//[^\\s\\r\\n]*"),
    ID("[A-Za-z][A-Za-z0-9]*"),
    ErrorID("[0-9]+[A-Za-z]+[A-Za-z0-9]*"),
    NUM("-?[0-9]+"),
    ARITHMATICOP("[*|+|-]"),
    //WHITESPACES("(\\s)+"),
    COMMA(","),
    RELOP("==|<"),
    ASSIGNMENTOP("="),
    LOGICALOP("&&"),

    SEMICOLON(";"),
    CLOSINGP("\\)"),
    OPENINGP("\\("),
    OPENINGCB("\\{"),
    CLOSINGCB("\\}"),
    DOT("\\."),
    EOF("\\$");

    public final String pattern;

    Type(String pattern) {
        this.pattern = pattern;
    }

    public Type getTyepFormString(String s) {

        SimpleMatcher simpleMatcher;
        for (Type t : values()) {
            simpleMatcher = new SimpleMatcher(t.pattern , s);

            if (simpleMatcher.matches())
                return t;
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
