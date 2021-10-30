import java.util.*;
import java.util.concurrent.*;
import org.apache.commons.lang3.*;
import org.apache.commons.math3.random.*;

/*
 *	변수의 갯수가 1개일때 사용
 * 
 */
public class SimpleGP {
	Tree[] pop;
	int[] fit_mae;
	Tree bestT;
	int[] bestfit;
	
	double probC, probM;
	int t_height; // tree의 depth
	int pop_size;
	
	int[] data;
	int[] obs;
	int numOfval;
	int tour_size;
	
	int generation;
	
	SimpleGP(double probC, double probM, int t_height, int pop_size, int[] data, int[] obs,
			String[] op, String[] co, int numOfval,int tour_size){
		this.probC = probC; this.probM = probM;	this.t_height = t_height; this.pop_size= pop_size; this.data = data;
		this.obs = obs; this.numOfval = numOfval; this.tour_size = tour_size;
		
		new Tree(t_height, op, numOfval, co);
		new EvalThS(data, obs, pop_size);
		
		pop = new Tree[pop_size];
		fit_mae = new int[pop_size];
		bestfit = new int[obs.length]; Arrays.fill(bestfit, 1000);
		
		create(pop);
		evaluationTh(pop);
		compare();
		//System.out.println(Arrays.toString(fit_mae));
		//System.out.println(MAE(bestfit));
	}
	
	void selectAndcross2() { // t1과 t2를 교배할때, MAX_height가 넘지않는 부분트리가 선택된다.
		Tree[] nPop = new Tree[pop.length];
		
		//crossover
		for(int i=0; i<pop.length; i=i+2) {
			
			int p1 = tournament();
			int p2 = tournament();
			while(p1==p2) { p2 = tournament(); }
			
			Tree t1 = pop[p1]; Tree t2 = pop[p2];
			
			if( ThreadLocalRandom.current().nextDouble()  < probC) { 
				t1 = t1.crossover(t2);
				t2 = t2.crossover(t1);
			}
			
			nPop[i] = t1.copy(); nPop[i+1] = t2.copy();
		}
		
		pop = nPop;
	}
	
	void mutation() {
		for(Tree t: pop) {
			if( ThreadLocalRandom.current().nextDouble()  < probM)
				t.mutate();
		}
	}

	int tournament() {
		int[] entry = new int[tour_size]; Arrays.fill(entry, -1);
		for(int i=0; i<entry.length; i++) {
			int j = ThreadLocalRandom.current().nextInt(fit_mae.length);
			
			while( ArrayUtils.contains(entry, j) ) { j = ThreadLocalRandom.current().nextInt(fit_mae.length);}
			entry[i] = j;
		}
		
		int id = entry[0];
		for(int i : entry) {
			if(fit_mae[id] > fit_mae[i])
				id = i;
		}
		
		return id;
	}
	
	int MAE(int[] f) {
		int sum = 0;
		for(int i=0; i<f.length; i++)
			sum = sum + Math.abs(obs[i]-f[i]);
		return sum;
	}
	
	int[] evaluation(Tree t) {
		int[] f = new int[data.length];
		for(int i=0; i<f.length; i++)
			f[i] = t.evaluation(data[i]);
		
		return f;
	}
	
	void evaluationGP(Tree[] pop, int[] fit_mae) {
		for(int i=0; i<pop.length; i++) {
			fit_mae[i] = MAE( evaluation(pop[i]) );
		}
	}
	
	void evaluationTh(Tree[] p) {
		Thread[] thr = new Thread[5];
		int n = p.length/thr.length;
		
		for(int i=0; i<thr.length; i++) {
			thr[i] = new Thread( new EvalThS(p, i*n, (i+1)*n) );
			thr[i].start();
		}
		
		for(Thread t: thr) {
			try {
				t.join();
			} catch (InterruptedException e) {	}
		}
		
		fit_mae = Arrays.copyOf(EvalThS.fit_mae, EvalThS.fit_mae.length);
	}

	void create(Tree[] p) {
		for(int i=0; i<p.length; i++)
			p[i] = new Tree(true);
	}
	
	void compare() {
		int min = MAE(bestfit);
		int index = -1;
		
		for(int i=0; i<fit_mae.length; i++) {
			if(min > fit_mae[i]) {
				index = i;
				min = fit_mae[i];
			}
		}

		if(index != -1) {
			bestT = pop[index].copy();
			bestfit = evaluation(bestT);
			System.out.println("MAE : " + MAE(bestfit));
		}
	}
	
	void run(int generation) {
		for(int g=0; g < generation; g++) {
			selectAndcross2();
			mutation();
			evaluationTh(pop);
			compare();
		}
	}
	
	public static int[] fibonacci(int n) {
		int[] fib = new int[n];
		fib[0] = 1;
		fib[1] = 1;
		
		fibonacciRe(fib,2,n);
		return fib;
	}
	
	public static void fibonacciRe(int[] fib, int k, int n) {
		if( k < n ) {
			fib[k] = fib[k-1] + fib[k-2];
			fibonacciRe(fib, k+1, n);
		}
	}

	public static void main(String[] args) {
		int numOfval = 1;
		int t_height = 6;
		
		String[] op = {"+", "*", "-","/"};
		String[] co = {"2","3","4","5"};
		double probC = 0.9;
		double probM = 0.25;
		
		int generation = 1000;
		int pop_size = 1000;
		int tour_size = 10;
		
		int[] x = {1,2,3,4,5,6,7,8,9,10};
		int[] y = fibonacci(10);
		System.out.println("This is X : "+Arrays.toString(x));
		System.out.println("This is observation : "+Arrays.toString(y));
		
		SimpleGP gp = new SimpleGP(probC, probM, t_height, pop_size, x, y, op, co, numOfval, tour_size);
		
		gp.run(generation);
		
		//System.out.println("This is X : "+Arrays.toString(x));
		System.out.println("This is observation : "+Arrays.toString(y));
		System.out.println("This is prediction : "+Arrays.toString(gp.bestfit));
		System.out.println("MAE "+gp.MAE(gp.bestfit));
		gp.bestT.print();
		
	}
}
