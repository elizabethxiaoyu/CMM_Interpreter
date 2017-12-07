package exception;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Text;

public class InterpretException extends Exception {
	private static final long serialVersionUID = 1L;
	private static Color color =  new Color(255,0,0,100);
	public InterpretException(String msg){
		super(msg);
	}
	
	private static void printException(StyledText resultdata2, Exception e) throws IOException{
		resultdata2.append(e.getMessage());
		resultdata2.append("\n");
//		resultdata2.append(e.getMessage());
//		resultdata2.append("\n");
//		FileWriter fw = new java.io.FileWriter("exception.txt", true);
//		PrintWriter pw = new PrintWriter(fw);
//		e.printStackTrace(pw);
//		FileReader fr  = new FileReader("exception.txt");
//		BufferedReader bufferedReader = new BufferedReader(fr);
//        String lineTxt = null;
//        while((lineTxt = bufferedReader.readLine()) != null){
//        	resultdata2.append(lineTxt);
//        }
	}
	
	@Override
	public void printStackTrace(PrintStream s) {
		super.printStackTrace(s);
		try {
			printException(ui.Main.resultdata2, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
