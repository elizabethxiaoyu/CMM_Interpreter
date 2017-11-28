package exception;

import java.io.PrintStream;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Text;

public class LexerException extends Exception{
	public LexerException(String msg){
		super(msg);
	}
	
	private static void printException(StyledText resultdata2, Exception e){
		resultdata2.append(e.getMessage());
		resultdata2.append("\n");
	}
	
	@Override
	public void printStackTrace(PrintStream s) {
		// TODO Auto-generated method stub
		super.printStackTrace(s);
		printException(ui.Main.resultdata2, this);
	}
}
