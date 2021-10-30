import org.apache.commons.lang3.*;

public class Symbol {
	
	static String[] BinaryOp = {"+", "-", "*", "/","^"};
	static String[] UnaryOp = {"sqrt", "sin", "cos", "tan", "log"};
	static String[] con = {"2","3","4"};
	static String[] val; // ���� & ���
	
	static String[] operator  = ArrayUtils.addAll(BinaryOp, UnaryOp);
	static String[] symbol; // �ǿ����� & ������
	int numV; // ���� ����
	
	
	Symbol(int numV){
		this.numV = numV;
		
		val = new String[numV];
		for(int i=0; i<val.length; i++) 
			val[i] = "x"+i;
		
		val = ArrayUtils.addAll(con,val);
		symbol = ArrayUtils.addAll(operator, val);
	}
}
