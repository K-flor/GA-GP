import org.apache.commons.lang3.*;

public class Symbol {
	
	static String[] BinaryOp = {"+", "-", "*", "/","^"};
	static String[] UnaryOp = {"sqrt", "sin", "cos", "tan", "log"};
	static String[] con = {"2","3","4"};
	static String[] val; // 변수 & 상수
	
	static String[] operator  = ArrayUtils.addAll(BinaryOp, UnaryOp);
	static String[] symbol; // 피연산자 & 연산자
	int numV; // 변수 갯수
	
	
	Symbol(int numV){
		this.numV = numV;
		
		val = new String[numV];
		for(int i=0; i<val.length; i++) 
			val[i] = "x"+i;
		
		val = ArrayUtils.addAll(con,val);
		symbol = ArrayUtils.addAll(operator, val);
	}
}
