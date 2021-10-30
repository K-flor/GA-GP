package tsp;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.ArrayUtils; // Threadsafe

public class Population {
	double[][] dist;
	
	int[][] pop;
	double[] fitness;
	int[] bestInd;
	double bestFit;
		
	int t_size; // tournament size
	int pop_size;
	int n; // # of vertex
	
	static double probcross;
	static double probmut;
	static int generation;
	
	Population(double probcross, double probmut, int generation) {
		Population.probcross = probcross; Population.probmut = probmut; Population.generation = generation;
	}
	
	Population(double[][] dist, int pop_size, int t_size) {
		this.dist = dist; this.n = dist.length;
		this.pop_size = pop_size; this.t_size = t_size;
		
		this.pop = new int[pop_size][n];
		this.fitness = new double[pop_size];
		this.bestFit = Double.MAX_VALUE;
		
		initial();
		evaluation();
	}
	
	void initial() {
		for(int i=0; i < pop_size; i++) {
			List<Integer> range = IntStream.range(0, n).boxed().collect(Collectors.toList());
			Collections.shuffle(range); 
			pop[i] = range.parallelStream().mapToInt(j->j).toArray();
		}
	}
	
	void evaluation() {
		for(int i=0; i<pop_size; i++) {
			int v = pop[i][0], u = pop[i][n-1];
			double d = dist[v][u];
			
			for(int j=0; j < pop[i].length-1; j++) {
				v = pop[i][j]; u = pop[i][j+1];
				d = d + dist[v][u];
			}
			
			fitness[i] = d;
		}
		
		compare();
	}
	
	void compare() {
		double localbest = Double.MAX_VALUE;
		int id = -1;
		
		for(int i=0; i<pop_size; i++) {
			if(fitness[i] < localbest) {
				localbest = fitness[i];
				id = i;
			}
		}
		
		if(bestFit > localbest) {
			bestFit = localbest;
			bestInd = Arrays.copyOf(pop[id], pop[id].length);
			//System.out.println("Best Fitness : " + localbest);
		}
	}
	
	void selAndcross(int c_type) {
		if(c_type == 1)
			pop = Crossover.pmx(pop, fitness, probcross, t_size);
		else if(c_type == 2)
			pop = Crossover.han1(pop, fitness, dist, probcross, t_size);
		else if(c_type == 3)
			pop = Crossover.han2(pop, fitness, dist, probcross, t_size);
		else 
			pop = Crossover.pmx(pop, fitness, probcross, t_size);
	}
	
	void mutation() {
		for(int i=0; i<pop_size; i++) {
			if(ThreadLocalRandom.current().nextDouble() < probmut) {
				int pos1 = ThreadLocalRandom.current().nextInt(n);
				int pos2 = ThreadLocalRandom.current().nextInt(n);  // 맨 처음과 맨끝이 안걸리게 해야함
				while(pos1 == pos2) {pos2 = ThreadLocalRandom.current().nextInt(n);}
				if(pos1 > pos2) {
					int tmp = pos1;
					pos1 = pos2;
					pos2 = tmp;
				}
				
				ArrayUtils.reverse(pop[i], pos1, pos2);
			}
		}
	}
	
	void run(int c_type) {
		for(int g=0; g < generation; g++) {
			selAndcross(c_type);
			mutation();
			evaluation();
		}
	}
}
