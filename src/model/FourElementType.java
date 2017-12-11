package model;

public class FourElementType {

    public static final String JUMP = "JUMP";
    public static final String READ = "READ";//读取用户输入
    public static final String WRITE = "WRITE";//输出变量值
    public static final String IN = "IN";//进入大括号，level+1
    public static final String OUT = "OUT";//反上
    public static final String INT = "INT";
    public static final String REAL = "REAL";
    public static final String ASSIGN = "ASSIGN";//赋值
    public static final String PLUS = "PLUS";
    public static final String MINUS = "MINUS";
    public static final String MUL = "MUL";
    public static final String DIV = "DIV";
    public static final String GT = ">";
    public static final String LT = "<";
    public static final String GET = ">=";
    public static final String LET = "<=";
    public static final String EQ = "==";
    public static final String NEQ = "!=";//not equal

    private String first;
    private String second;
    private String third;
    private String forth;


    public FourElementType(String first, String second, String third, String forth) {
        super();
        this.first = first;
        this.second = second;
        this.third = third;
        this.forth = forth;
    }



    public String getFirst() {
        return first;
    }



    public void setFirst(String first) {
        this.first = first;
    }



    public String getSecond() {
        return second;
    }



    public void setSecond(String second) {
        this.second = second;
    }



    public String getThird() {
        return third;
    }



    public void setThird(String third) {
        this.third = third;
    }



    public String getForth() {
        return forth;
    }



    public void setForth(String forth) {
        this.forth = forth;
    }



    @Override
    public String toString() {
        return String.format("%s  %s  %s  %s", first, second, third, forth);
    }
}

