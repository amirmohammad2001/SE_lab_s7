package MiniJava.SimpleMatcher;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleMatcher {
    private Matcher matcher;

    public SimpleMatcher(String pattern, String input) {
        Pattern compiledPattern = Pattern.compile(pattern);
        this.matcher = compiledPattern.matcher(input);
    }

    public boolean find() {
        return matcher.find();
    }

    public String group(String group) {
        return matcher.group(group);
    }
    public boolean matches() {
        return matcher.matches();
    }
}
