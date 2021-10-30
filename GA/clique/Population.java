package clique;

import java.util.concurrent.ThreadLocalRandom;
import sojung2021.*;

public class Population {
	byte[][] adj;
	int n; // # of vertex
	
	int[][] pop;
	int[] fitness;
	int[] best_indiv;
	int best_fit;
	
	int maxIteration; // thread
	int pop_size;
	int tour_size;
	double probC;
	double probM;
	
	int name;
	int crosstype;

	Population(byte[][] adj, int maxIteration, int pop_size, int tour_size, double probC, double probM, int crosstype, int name){
		this.maxIteration = maxIteration; this.pop_size = pop_size; this.tour_size = tour_size; this.probC = probC; this.probM = probM;
		this.name = name; this.crosstype = crosstype;
		
		this.adj = adj;
		this.n = adj.length;
		
		this.pop = new int[pop_size][n];
		this.fitness = new int[pop_size];
		
		initial();
		evaluation();
	}
	
	void initial() {
		for(int i=0; i<pop_size; i++) {
			for(int j=0; j<n; j++)
				pop[i][j] = ( ThreadLocalRandom.current().nextDouble() > 0.5 )? 0 : 1 ;
		}
	}
	
	void evaluation() {
		for(int i=0; i<pop_size; i++) {
			byte[][] subadj = sub_adj(pop[i], adj);

			if( isClique(subadj) )
				fitness[i] = BasicTools.sum_element(pop[i]);
			else
				fitness[i] = 0;
		}
		
		int idMax = BasicTools.findMaxIndex(fitness);
		
		if( best_fit < fitness[idMax] ) {
			best_indiv = pop[idMax].clone();
			best_fit = fitness[idMax];
			System.out.println("[Population "+name+"] best fitness : "+ best_fit);
		}
	}
	
	void selAndcross() {
		int[][] nPop = new int[pop_size][n];
		
		for(int i=0; i<nPop.length; i=i+2) {
			int p1 = BasicTools.tournament_Max(pop_size, tour_size, fitness);
			int p2 = BasicTools.tournament_Max(pop_size, tour_size, fitness);
			while(p1 == p2) { p2 = BasicTools.tournament_Max(pop_size, tour_size, fitness); }
			
			int[] pp1 = pop[p1].clone();
			int[] pp2 = pop[p2].clone();
			
			// crossover
			if(ThreadLocalRandom.current().nextDouble() < probC) {
				uniform_cross(pp1,pp2);
			}
			
			nPop[i] = pp1;
			nPop[i+1] = pp2;
		}
		
		pop = nPop;
	}
	
	void selAndcross_type() {
		int[][] nPop = new int[pop_size][n];
		
		for(int i=0; i<nPop.length; i=i+2) {
			int p1 = BasicTools.tournament_Max(pop_size, tour_size, fitness);
			int p2 = BasicTools.tournament_Max(pop_size, tour_size, fitness);
			while(p1 == p2) { p2 = BasicTools.tournament_Max(pop_size, tour_size, fitness); }
				
			nPop[i] = pop[p1].clone();
			nPop[i+1] = pop[p2].clone();
			
			// crossover
			if(ThreadLocalRandom.current().nextDouble() < probC) {
				if(crosstype == 1)
					onepoint_cross(p1, p2, i, i+1, nPop);
				else if(crosstype == 2)
					twopoint_cross(p1, p2, i, i+1, nPop);
				else
					uniform_cross(p1, p2, i, i+1, nPop);
			}
		}
		
		pop = nPop;
	}
	
	void uniform_cross(int[] pp1, int[] pp2) {
		for(int i=0; i<n; i++) {
			if( ThreadLocalRandom.current().nextDouble() < 0.5) {
				int tmp = pp1[i];
				pp1[i] = pp2[i];
				pp2[i] = tmp;
			}
		}
	}
	
	void uniform_cross(int p1, int p2, int i, int j, int[][] nP) {
		int[] off1 = new int[n];
		int[] off2 = new int[n];
		
		for(int k=0; k<n ; k++) {
			if( ThreadLocalRandom.current().nextDouble() < 0.5 ) {
				off1[k] = pop[p1][k];
				off2[k] = pop[p2][k];
			}else {
				off1[k] = pop[p2][k];
				off2[k] = pop[p1][k];
			}
		}
		
		nP[i] = off1;
		nP[j] = off2;
	}
	
	void onepoint_cross(int p1, int p2, int i, int j, int[][] nP) {
		int[] off1 = new int[n];
		int[] off2 = new int[n];
		
		int pos = ThreadLocalRandom.current().nextInt(1, n-2);
		for(int k=0; k<n; k++) {
			if( k > pos) {
				off1[k] = pop[p2][k];
				off2[k] = pop[p1][k];				
			}else {
				off1[k] = pop[p1][k];
				off2[k] = pop[p2][k];
			}
		}
		
		nP[i] = off1;
		nP[j] = off2;
	}
	
	void twopoint_cross(int p1, int p2, int i, int j, int[][] nP) {
		int[] off1 = new int[n];
		int[] off2 = new int[n];
		
		int pos1 = ThreadLocalRandom.current().nextInt(1,n-1);
		int pos2 = ThreadLocalRandom.current().nextInt(1,n-1);
		
		while(pos1==pos2) { pos2 = ThreadLocalRandom.current().nextInt(1,n-1); }
		
		if(pos1 > pos2) {
			int tmp = pos1;
			pos1 = pos2;
			pos2 = tmp;
		}
		
		for(int k=0; k<n; k++) {
			if(pos1 < k & pos2 > k) {
				off1[k] = pop[p2][k];
				off2[k] = pop[p1][k];
			}else {
				off1[k] = pop[p1][k];
				off2[k] = pop[p2][k];
			}
		}
		
		nP[i] = off1;
		nP[j] = off2;
	}
		
	void mutate() {
		for(int i=0; i<pop_size; i++) {
			for(int j=0; j<n; j++) {
				if( ThreadLocalRandom.current().nextDouble() < probM ) {
					pop[i][j] = (pop[i][j]+1) % 2;
				}
			}
		}
	}
	
	void heuristic() {
		for(int p=0; p < pop_size; p++) {
			//Relax
			int[] id0 = BasicTools.findIndex_shuffle(pop[p],0);
			int id0_n = ThreadLocalRandom.current().nextInt(id0.length);
			
			for(int i=0; i < id0_n; i++) {
				int t = id0[i];
				pop[p][t] = 1;
			}
			
			//Repair - (a)
			int idx1 = ThreadLocalRandom.current().nextInt(n);
			for(int i=idx1; i<n; i++) {
				if( pop[p][i] == 1 ) {
					if( ThreadLocalRandom.current().nextInt(2) == 0 )
						pop[p][i] = 0;
					else {
						for(int j = i+1; j < n; j++) {
							if( pop[p][j] == 1 && adj[i][j] == 0)
								pop[p][j] = 0;
						}
						
						for(int j=0; j < i-1; j++) {
							if( pop[p][j] == 1 && adj[i][j] == 0)
								pop[p][j] = 0;
						}
					}
				}
			}
			
			//Repair - (b)
			/*
			for(int i=idx1-1; i >= 0; i--) {
				if( pop[p][i] == 1 )
					pop[p][i] = 0;
				else {
					for(int j=i-1; j >= 0; j--) {
						if( pop[p][j] == 1 && adj[i][j] == 0 )
							pop[p][j] = 0;
					}
				}
			}
			*/
			for(int i=idx1-1; i>=0; i--) {
				if(pop[p][i] == 1) {
					if( ThreadLocalRandom.current().nextInt(2) == 0 )
						pop[p][i] = 0;
					else {
						for(int j=i-1; j>=0; j--) {
							if( pop[p][j] == 1 && adj[i][j] == 0)
								pop[p][j] = 0;
						}
					}
				}
			}
			
			
			//Extend - (a)
			int idx2 = ThreadLocalRandom.current().nextInt(n);
			for(int j=idx2; j < n; j++) {
				if( BasicTools.isConnected(pop[p], j, adj) )
					pop[p][j] = 1;
			}
			
			//Extend - (b)
			for(int j=0; j < idx2-1; j++) {
				if( BasicTools.isConnected(pop[p], j, adj) )
					pop[p][j] = 1;
			}
		}	
	}
	
	byte[][] sub_adj(int[] p, byte[][] adj) {
		int[] S = new int[BasicTools.sum_element(p)];// S : array of vertex of subgraph.
		
		for(int i=0,j=0; i<p.length; i++) {
			if( p[i] == 1 ) {
				S[j] = i;
				++j;
			}
		}

		return GraphReader.subAdjacency(adj, S);
	}
	
	boolean isClique(byte[][] a) {
		for(int i=0, l=a.length; i < l; i++) {
			for(int j=i+1; j < l; j++) {
				if( a[i][j] == 0 )
					return false;
			}
		}
		
		return true;
	}
	
	void replace_Indiv(int[] new_ind, int new_fit) {
		int replaceId = ThreadLocalRandom.current().nextInt(pop_size);
		
		pop[replaceId] = new_ind.clone();
		fitness[replaceId] = new_fit;
	}
	
	void run() {
		for(int i=0; i<maxIteration; i++) {
			//selAndcross();
			selAndcross_type();
			mutate();
			heuristic();
			evaluation();
		}
	}
}
