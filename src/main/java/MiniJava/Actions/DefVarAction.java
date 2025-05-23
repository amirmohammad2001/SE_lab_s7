package MiniJava.Actions;

import MiniJava.codeGenerator.CodeGenerator;
import MiniJava.scanner.token.Token;

public class DefVarAction implements Action {
	@Override
	public void execute(CodeGenerator codeGenerator, Token next) {
		codeGenerator.defVar();
	}
}
