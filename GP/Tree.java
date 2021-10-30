import java.util.*;
import java.util.concurrent.*;
import java.lang.Math;
import org.apache.commons.lang3.*;
import org.apache.commons.lang3.math.*;
import org.apache.commons.lang3.mutable.*;

public class Tree {
	Node root;
	int height; // ���� Ʈ���� ������ height
	
	static int MAX_H; // ��� Ʈ���� max height
	static String[] op; // ������.
	static String[] val; // ���� & ���
	static String[] symbol;// ������ & ���� & ���
	static int numV;
	
	int max_depth;
	boolean isFull;
	int fit;
	Node tmp;
	
	Tree(int h, String[] op, int v, String[] cons){
		MAX_H = h; 
		numV = v;	new Symbol(numV);
		
		this.op = op;
		val = new String[numV];
		for(int i=0; i<val.length; i++) 
				val[i] = "x"+i;
		
		val = ArrayUtils.addAll(cons, val);
		symbol = ArrayUtils.addAll(op, val);
	}
	
	Tree(boolean initial){
		this.height = ThreadLocalRandom.current().nextInt(2, MAX_H+1);
		//this.height = RandomUtils.nextInt(2, MAX_H+1);
		
		if(initial) {
			int i = ThreadLocalRandom.current().nextInt(2);
			if(i == 1)
				initializeFull();
			else {
				initializeGrow();
				while( this.maxDepth(root) < height ) {
					initializeGrow();
				}
			}
		}
	}
		
	/********************************************
	 * 
	 * Full-tree
	 * 
	*********************************************/
	void initializeFull() {
		isFull = true;
		root = new Node();
		initializeFullRe(root,1);
	}
	
	void initializeFullRe(Node cur, int level) {
		String s = random(level);
		cur.initial(s,level);
		
		if( ArrayUtils.contains(Symbol.BinaryOp,s) ) {
			cur.left = new Node(); cur.right = new Node();
			cur.left.parent = cur; 
			cur.right.parent = cur;
			initializeFullRe(cur.left, level+1); initializeFullRe(cur.right, level+1);
		} else if( ArrayUtils.contains(Symbol.UnaryOp, s) ) {
			cur.left = new Node();
			cur.left.parent = cur;
			initializeFullRe(cur.left, level+1);
		}
	}
	
	String random(int level) {
		if(level == height) {
			int i = ThreadLocalRandom.current().nextInt(0, val.length);
			//int i = RandomUtils.nextInt(0, val.length);
			return val[i];
		}
		else {
			int i = ThreadLocalRandom.current().nextInt(0, op.length);
			//int i = RandomUtils.nextInt(0, op.length);
			return op[i];
		}
	}
	
	/*******************************************
	 * 
	 * Grow-tree
	 * 
	********************************************/
	void initializeGrow() {
		isFull = false;
		root = new Node();
		initializeGrowRe(root,1);
	}
	
	void initializeGrowRe(Node cur, int level) {
		String s = randomG(level);
		cur.initial(s, level);
		
		if( ArrayUtils.contains(Symbol.BinaryOp,s) ) {
			cur.left = new Node(); cur.right = new Node();
			cur.left.parent = cur; 
			cur.right.parent = cur;
			initializeGrowRe(cur.left, level+1); initializeGrowRe(cur.right, level+1);
		}
		else if( ArrayUtils.contains(Symbol.UnaryOp, s) ) {
			cur.left = new Node();
			cur.left.parent = cur; 
			initializeGrowRe(cur.left, level+1);
		}
	}
	
	String randomG(int level) {
		if(level == 1) {
			int i = ThreadLocalRandom.current().nextInt(0, op.length);
			//int i = RandomUtils.nextInt(0, op.length);
			return op[i];
		}
		else if(level == height){
			int i = ThreadLocalRandom.current().nextInt(0, val.length);
			//int i = RandomUtils.nextInt(0, val.length);
			return val[i];
		}
		else {
			int i = ThreadLocalRandom.current().nextInt(0, symbol.length);
			//int i = RandomUtils.nextInt(0, symbol.length);
			return symbol[i];
		}
	}
	
	/***************************************************
	 * 
	 *  Copy �Լ�
	 *  
	 *  - copy() : ������ Ʈ�� ��ü�� �����Ͽ� return
	 *  - copy(boolean a) : chooseNode()�� �̿��Ͽ� ������ tmp�� �̿�. tmp�� root�� �ϴ� tree�� return
	 *  - change(Tree other) : tmp�� �ڸ��� other�� root�� �� �ڽĵ��� �ִ´�  <crossover �ÿ� ���>
	 *  - copyRe(Node newN, Node original) 
	 *   
	 ***************************************************/
	Tree copy() {
		
		Tree newT = new Tree(false);
		if( root != null) {
			newT.root = this.root.copy();
			copyRe(newT.root, this.root);
		}
		return newT;
	}
	
	Tree copy(boolean a) {
		Tree newT = new Tree(false);
		selectNode();
		newT.root = this.tmp.copy();
		copyRe(newT.root, this.tmp);
		return newT;
	}
	
	void change(Tree other) {
		Node cur = other.root;
		tmp.initial(cur.key, cur.level);
		copyRe(tmp, cur);
		giveLevel();
	}
	
	void copyRe(Node newN, Node original) {
		if(original.left != null) {
			newN.left = original.left.copy();
			copyRe(newN.left, original.left);
		}else {
			newN.left = null;
		}
		
		if(original.right != null) {
			newN.right = original.right.copy();
			copyRe(newN.right, original.right);
		}
		else {
			newN.right = null;
		}
	}
	
	/*****************************************************
	 * 
	 * crossover
	 * other�� �κ� Ʈ���� this.tmp
	 * 
	 *****************************************************/
	Tree crossover(Tree other) {
		Tree t = this.copy();

		t.selectNode(); // t�� tmp�� �Ҵ��.
		//System.out.println("tmp node : "+t.tmp);
		Tree part = other.copy(true); // other �� tmp�� �ڽĵ��� �����ؼ� ������
		
		while( t.tmp.level + part.maxDepth(part.root) - 1 > MAX_H ){ // 
			part = other.copy(true);
		}
		
		//System.out.println("\nThis is part of other");
		//part.print();
		t.change(part);
		return t;
	}
	
	/********************************
	 * 
	 * mutation
	 * tmp���� ���� �ڽĵ��� key�� ���� -- mutation
	 * 
	*********************************/	
	void mutate() {
		giveLevel();
		selectNode();
		
		mutateRe(tmp);
	}
	
	void mutateRe(Node p) {
		if(p.level == MAX_H)
			
			p.key = val[ThreadLocalRandom.current().nextInt(0, val.length)];
		else {
			p.key = symbol[ThreadLocalRandom.current().nextInt(0, symbol.length)];
			
			if( p.isBinary() ) {
				if(p.left == null) {
					p.left = new Node();
					p.left.initial("+", p.level+1);
				}
				
				if(p.right == null) {
					p.right = new Node();
					p.right.initial("+", p.level+1);
				}
				
				mutateRe(p.left);
				mutateRe(p.right);
			}else if( p.isUnary() ){
				p.right = null;
				
				if(p.left == null) {
					p.left = new Node();
					p.left.initial("+", p.level+1);
				}
				mutateRe(p.left);
			}else {
				p.left = null; p.right = null;
			}
		}
	}
	

	// cur�������� leaf node������ �ִ� ����.
	// ���̴� root == 1 �� �����Ѵ�.
	int maxDepth(Node cur) {
		if(cur == null) 
			return 0;
		
		int lDepth = maxDepth(cur.left);
		int rDepth = maxDepth(cur.right);
		
		max_depth = Math.max(lDepth, rDepth)+1;
		return Math.max(lDepth, rDepth)+1;
	}
	
	// Node�� level �ٽ� �ο� -- root�� level = 1
	void giveLevel() {
		giveLevelRe(root,1);
	}
	
	void giveLevelRe(Node cur, int i) {
		cur.level = i;
		if(cur.left != null) 
			giveLevelRe(cur.left, i+1);

		if(cur.right != null)
			giveLevelRe(cur.right, i+1);
	}
	
	/*****************************************************
	 * CUT
	 * max depth�� MAX_h���� ū tree�� MAX_h�� �°� �߶󳽴�.
	 * ( �� Ŭ������ �Լ� crossover�� ����ϸ� cut�Լ��� ����� �ʿ䰡 ����.)
	 *****************************************************/
	void cut() {
		giveLevel();
		if( maxDepth(root) > MAX_H )
			cutting(root);
	}
	
	void cutting(Node p) {
		if(p.level == MAX_H) {
			if( p.left != null || p.right != null ) { //p�� �ڽĵ��� �߷�����.
				//p.key = evaluation(p);
				p.key = val[ThreadLocalRandom.current().nextInt(0, val.length)];
				p.left = null; p.right = null;
			}
		}
		else {
			if(p.left != null) 
				cutting(p.left);
			
			if(p.right != null)
				cutting(p.right);
		}
	}

	/***********************************
	 * 
	 * Ʈ�� �� Ư�� ��� ����.
	 * tree�� preorder�� �湮�ϸ鼭 n��° node�� ã�´�. 
	 *
	 **********************************/
	void selectNode() {
		int n = ThreadLocalRandom.current().nextInt(numOfNode()); //������ ����� ��ȣ.
		MutableInt cnt = new MutableInt(0);
		Node[] chosen = {null};
		selectNodeRe(root, n, cnt, chosen);
		
		tmp = chosen[0];
	}
	
	void selectNodeRe(Node p, int n, MutableInt cnt, Node[] chosen) {
		if( n == cnt.getValue() ) {
			chosen[0] = p;
			return;
		}
		
		cnt.add(1);
		
		if(p.left != null)
			selectNodeRe(p.left, n, cnt, chosen);
			
		if(p.right != null & chosen[0] == null) 
			selectNodeRe(p.right, n, cnt, chosen);
	}
	
	/********************************
	 * 
	 * tree ���� ����� �� 
	 * 
	*********************************/	
	int numOfNode() {
		MutableInt cnt = new MutableInt(0);
		countNode(cnt, root);
		return cnt.getValue();
	}
	
	void countNode(MutableInt cnt, Node p) {
		if(p != null)
			cnt.add(1);
		
		if(p.left != null)
			countNode(cnt, p.left);
		
		if(p.right != null)
			countNode(cnt, p.right);
	}
	
	
	/********************************
	 * 
	 * ���
	 * 
	*********************************/	
	void print() { // tree ���
		inQueue();
		System.out.println();
	}
	
	void inQueue() {
		ArrayDeque<Node> que = new ArrayDeque<>();
		que.add(this.root);
		int cur_lev = 1;
		while( !que.isEmpty() ) {
			Node p = que.poll();
			if( cur_lev < p.level) {
				System.out.println();
				cur_lev = p.level;
			}
			System.out.print("["+p.key+"]");
			
			if(p.left != null)
				que.add(p.left);
			
			if(p.right != null)
				que.add(p.right);
		}
	}
	
	/************************************
	 * 
	 * evaluation
	 * 
	*************************************/
	// ������ ������ ����
	int evaluation(int[] X, Node cur) {
		ArrayDeque<String> q = new ArrayDeque<>();
		evaluationRe(cur, X, q);
		return Integer.parseInt( q.pop() );
	}
	
	// root���� ����
	int evaluation(int[] X) {
		ArrayDeque<String> q = new ArrayDeque<>();
		evaluationRe(root, X, q);
		return Integer.parseInt( q.pop() );
	}

	// ���� Ž���� �̿��Ͽ� ����
	void evaluationRe(Node p, int[] X, ArrayDeque<String> q) {
		if(p.left != null)
			evaluationRe(p.left, X, q);
		
		if(p.right != null)
			evaluationRe(p.right, X, q);
		
		if(p.key.charAt(0) == 'x') { //������ �� 
			int i = Integer.parseInt( p.key.substring(1, p.key.length()) );
			q.addFirst( Integer.toString( X[i]) );
		}
		else if( NumberUtils.isCreatable(p.key) ) { //����� ��
			q.addFirst(p.key);
		}
		else { // ������ 
			if(p.key.equals("+")) {
				int v1 = Integer.parseInt(q.pop());
				int v2 = Integer.parseInt(q.pop());
				q.addFirst( String.format("%d",v2+v1) );
			}
			else if(p.key.equals("-")) {
				int v1 = Integer.parseInt(q.pop());
				int v2 = Integer.parseInt(q.pop());
				q.addFirst( String.format("%d",v2-v1) );
			}else if(p.key.equals("*")) {
				int v1 = Integer.parseInt(q.pop());
				int v2 = Integer.parseInt(q.pop());
				q.addFirst( String.format("%d",v2*v1) );
			}else if(p.key.equals("/")) {
				int v1 = Integer.parseInt(q.pop());
				int v2 = Integer.parseInt(q.pop());
				v1 = (v1 == 0)? 1 : v1;
				q.addFirst( String.format("%d",v2/v1) );
			}
		}
	}
	
	// numOfval == 1 �� ���
	int evaluation(int x) {
		ArrayDeque<String> q = new ArrayDeque<>();
		evaluationRe(root, x, q);
		return Integer.parseInt( q.pop() );
	}
	
	void evaluationRe(Node p, int X, ArrayDeque<String> q) {
		if(p.left != null)
			evaluationRe(p.left, X, q);
		
		if(p.right != null)
			evaluationRe(p.right, X, q);

		if(p.key.charAt(0) == 'x') { //������ �� 
			q.addFirst( Integer.toString(X) );
		}
		else if( NumberUtils.isCreatable(p.key) ) { //����� ��
			q.addFirst(p.key);
		}
		else { // ������ 
			if(p.key.equals("+")) {
				int v1 = Integer.parseInt(q.pop());
				int v2 = Integer.parseInt(q.pop());
				q.addFirst( String.format("%d",v2+v1) );
			}
			else if(p.key.equals("-")) {
				int v1 = Integer.parseInt(q.pop());
				int v2 = Integer.parseInt(q.pop());
				q.addFirst( String.format("%d",v2-v1) );
			}else if(p.key.equals("*")) {
				int v1 = Integer.parseInt(q.pop());
				int v2 = Integer.parseInt(q.pop());
				q.addFirst( String.format("%d",v2*v1) );
			}else if(p.key.equals("/")) {
				int v1 = Integer.parseInt(q.pop());
				int v2 = Integer.parseInt(q.pop());
				v1 = (v1 == 0)? 1 : v1;
				q.addFirst( String.format("%d",v2/v1) );
			}
		}
	}

}