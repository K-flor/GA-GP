package ga;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import sojung2021.BasicTools;

/*
	neural network for XOR problem
	
	- two inputs
	- one Hidden layer & 2 nodes
	- one output
	
	* weights & bias
		
		- Input & Hidden
		
				(	| w1 w2 b1 | | x1 | )   | h1 |
		sigmoid	(	| w3 w4	b2 | | x2 |	) = | h2 |
				(				 |  1 | )
		
		
		- Hidden & Output
		
				(				 | h1 | )
		sigmoid (	| w5 w6 b3 | | h2 | ) = | o1 |
				(				 |  1 | )
		
	* chromosome
		[w1 w2 b1 w3 w4 b2 w5 w6 b3]
*/

public class XORproblem {
	static double[][] pop;
	static double[][] y_hat;
	static double[] fitness;
	
	static double[] best_chro;
	static double[] best_y_hat;
	static double best_fit;

	static double[][] xs = {{0.0, 0.0}, {0.0, 1.0}, {1.0, 0.0}, {1.0, 1.0}};
	static double[] ys= {0.0, 1.0, 1.0, 0.0};
	
	static double lower;
	static double upper;
	static double prob_cross;
	static double prob_mut;
	static int generation;
	static int pop_size;
	static int tour_size;
	static double alpha; // for arithmetic crossover
	
	XORproblem(double lower, double upper, double prob_cross, double prob_mut, int generation, int pop_size, int tour_size, double alpha){
		this.prob_cross = prob_cross; this.prob_mut = prob_mut;
		this.generation = generation; this.pop_size = pop_size; this.tour_size = tour_size;
		this.lower = lower; this.upper = upper; this.alpha = alpha;
		
		pop = new double[pop_size][9];
		y_hat = new double[pop_size][];
		fitness = new double[pop_size];
		best_fit = Double.MAX_VALUE;

		initial_population();
		evaluation();
	}
	
	static void initial_population() {
		for(int i=0; i<pop_size; i++) {
			for(int j=0, l = pop[i].length; j < l; j++)
				pop[i][j] = ThreadLocalRandom.current().nextDouble(lower, upper);
		}
	}
	
	static void evaluation() {
		for(int i=0; i<pop_size; i++) {
			int l = xs.length;
			double[] outs = new double[l];
			
			for(int j=0; j < l; j++) {
				double x1 = xs[j][0], x2 = xs[j][1];
						
				double h1 = sigmoid(pop[i][0]*x1 + pop[i][1]*x2 + pop[i][2]*1);
				double h2 = sigmoid(pop[i][3]*x1 + pop[i][4]*x2 + pop[i][5]*1);
				
				double o1 = pop[i][6]*h1 + pop[i][7]*h2 + pop[i][8]*1;
				outs[j] = sigmoid(o1);
			}
			y_hat[i] = outs;
			double rr = rmse(ys, outs);
			fitness[i] = rr;
		}
		
		int min_id = BasicTools.findMinIndex(fitness);
		if(best_fit > fitness[min_id]) {
			best_fit = fitness[min_id];
			best_chro = Arrays.copyOf(pop[min_id], pop[min_id].length);
			best_y_hat = Arrays.copyOf(y_hat[min_id], y_hat[min_id].length);
			
			System.out.println("Best Outputs = " + Arrays.toString(best_y_hat));
			System.out.println("Best Fitness = "+best_fit);
		}
	}
	
	static double sigmoid(double x) {
		return 1/(1+ Math.exp(-x));
	}
	
	static double rmse(double[] y, double[] pred) {
		int n = y.length;
		double sum = 0;
		for(int i=0; i<n; i++)
			sum += Math.pow(y[i]-pred[i], 2);
		
		return Math.sqrt(sum/n);
	}
	
	static void selAndcrossover() {
		double[][] newPop = new double[pop_size][9];
		
		for(int i=0; i<pop_size; i=i+2) {
			int p1 = tournamentSelection();
			int p2 = tournamentSelection();
			while(p1 == p2) { p2 = tournamentSelection(); }
			
			double[] pp1 = Arrays.copyOf(pop[p1], pop[p1].length);
			double[] pp2 = Arrays.copyOf(pop[p2], pop[p2].length);
			arithmetic(pp1, pp2);
			
			newPop[i] = pp1;
			newPop[i+1] = pp2;
		}
		pop = newPop;
	}
	
	static void arithmetic(double[] a, double[] b) {
		int l = a.length;
		for(int i=0; i < l; i++) {
			if(ThreadLocalRandom.current().nextDouble() < prob_cross) {
				double ai = a[i], bi = b[i]; 
				a[i] = alpha*bi + (1-alpha)*ai;
				b[i] = alpha*ai + (1-alpha)*bi; 
			}
		}
	}
	
	static void mutation() {
		for(int i=0; i<pop_size; i++) {
			for(int j=0, l=pop[i].length; i<l; i++) {
				if(ThreadLocalRandom.current().nextDouble() < prob_mut)
					pop[i][j] = ThreadLocalRandom.current().nextDouble(lower, upper);
			}
		}
	}
	
	static int tournamentSelection() {
		List<Integer> range = IntStream.range(0, pop_size).boxed().collect(Collectors.toList());
		Collections.shuffle(range); 
		int[] entry = Arrays.copyOf(range.stream().mapToInt(i->i).toArray(), tour_size);
		
		int id = entry[0];
		for(int i : entry) {
			if(fitness[id] > fitness[i])
				id = i;
		}
		
		return id;
	}
	
	public static void run() {
		for(int g = 0; g < generation; g++) {
			selAndcrossover();
			mutation();
			evaluation();
		}
		
		System.out.println("-------------- run() Result");
		System.out.println("Best Outputs = " + Arrays.toString(best_y_hat));
		System.out.println("best Fitness : "+best_fit);
	}
	
	
	public static void main(String[] args) {
		int pop_size = 50;
		int tour_size = 7;
		int generation = 100;
		double prob_cross = 0.8;
		double prob_mut = 0.01;
		
		double lower = -20.0;
		double upper = 20.0;
		double alpha = 0.6;
		
		XORproblem g = new	XORproblem(lower, upper, prob_cross, prob_mut, generation, pop_size, tour_size, alpha);
		run();
	}

}
