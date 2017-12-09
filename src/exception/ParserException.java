package exception;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.eclipse.swt.custom.StyledText;

public class ParserException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public ParserException(String msg){
		super(msg);
	}
	
	private static void printException(StyledText resultdata2, Exception e) throws IOException{
		resultdata2.append(e.getMessage());
		resultdata2.append("\n");
//		FileWriter fw = new java.io.FileWriter("exception.txt", false);
//		PrintWriter pw = new PrintWriter(fw);
//		e.printStackTrace(pw);
//		fw.close();
//		pw.close();
//		FileReader fr  = new FileReader("exception.txt");
//		BufferedReader bufferedReader = new BufferedReader(fr);
//        String lineTxt = null;
//        while((lineTxt = bufferedReader.readLine()) != null){
//        	resultdata2.append(lineTxt);
//        }
//        fr.close();
//        bufferedReader.close();
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