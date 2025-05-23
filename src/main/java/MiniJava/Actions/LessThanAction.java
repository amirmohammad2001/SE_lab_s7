package MiniJava.Actions;

import MiniJava.codeGenerator.CodeGenerator;
import MiniJava.scanner.token.Token;

public class LessThanAction implements Action {
	@Override
	public void execute(CodeGenerator codeGenerator, Token next) {
		codeGenerator.less_than();
	}
}
