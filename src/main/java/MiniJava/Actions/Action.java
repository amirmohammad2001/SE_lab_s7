package MiniJava.Actions;
import MiniJava.codeGenerator.CodeGenerator;
import MiniJava.scanner.token.Token;

public interface Action {
	void execute(CodeGenerator codeGenerator, Token next);
}
