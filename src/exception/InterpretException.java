package exception;

import java.awt.Color;
import java.io.PrintStream;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Text;

public class InterpretException extends Exception {
	private static final long serialVersionUID = 1L;
	private static Color color =  new Color(255,0,0,100);
	public InterpretException(String msg){
		super(msg);
	}
	
	private static void printException(StyledText resultdata2, Exception e){
		resultdata2.append(e.getMessage());
		resultdata2.append("\n");
	}
	
	@Override
	public void printStackTrace(PrintStream s) {
		super.printStackTrace(s);
		printException(ui.Main.resultdata2, this);
	}
}
