import org.apache.commons.lang3.*;

public class Node {
	String key;
	int level;
	
	Node right;
	Node left;
	Node parent;
	
	int x, y, pos;
	
	Node(){
		key = null; 
		level = 0;
		right = left = parent = null;
	}
	
	Node(String s){
		key = s;
		level = 0;
		right = left = parent = null;
	}
	
	void setLeft(Node l) {
		Node son = l.copy();
		left = son;
		left.parent = this;
	}
	
	void setRight(Node r) {
		Node son = r.copy();
		right = son;
		right.parent = this;
	}
	
	void initial(String key, int level) {
		this.key = key;
		this.level = level;
	}
	
	boolean isBinary(){
		if( ArrayUtils.contains(Symbol.BinaryOp, key) )
			return true;
		else
			return false;
	}
	
	boolean isUnary() {
		if( ArrayUtils.contains( Symbol.UnaryOp, key) )
			return true;
		else
			return false;
	}
	
	boolean isOperator() {
		if( ArrayUtils.contains( Symbol.operator, key))
			return true;
		else 
			return false;
	}

	@Override
	public String toString() {
		return "[" + key + "]";
	}
	
	Node copy() {
		Node newN = new Node();
		newN.initial(this.key, this.level);
		return newN;
	}
	
	void addNullSons() {
		left = new Node("NULL");
		right = new Node("NULL");
		
		left.parent = this;
		right.parent = this;
	}
	
	void addRightNullson() {
		right = new Node("NULL");
		
		right.parent = this;
	}
	
	boolean isNull() {
		if(key.equals("NULL"))
			return true;
		else
			return false;
	}
	
	boolean isLeaf() {
		if( left == null & right == null  )
			return true;
		else 
			return false;
	}
}