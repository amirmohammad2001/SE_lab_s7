package MiniJava.Actions;

import MiniJava.codeGenerator.CodeGenerator;
import MiniJava.scanner.token.Token;

public class IntPidAction implements Action {
	@Override
	public void execute(CodeGenerator codeGenerator, Token next) {
		codeGenerator.intpid(next);
	}
}
