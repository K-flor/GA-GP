public class Island {
	int numIsland;
	int generation;
	
	Population[] pops;
	int[] best_All;
	int bestFitness;
	
	Island(int numIsland, int generation, byte[][] adj, int maxIteration, int pop_size, int tour_size, double probC, double probM) {
		this.numIsland = numIsland; this.generation = generation;
		
		pops = new Population[numIsland];
		
		// byte[][] adj, int maxIteration, int pop_size, int tour_size, double probC, double probM, int crosstype, int name
		pops[0] = new Population(adj, maxIteration, pop_size, tour_size, probC, probM, 0, 0);
		pops[1] = new Population(adj, maxIteration, pop_size, tour_size, probC, probM, 1, 1);
		pops[2] = new Population(adj, maxIteration, pop_size, tour_size, probC, probM, 0, 2);
		pops[3] = new Population(adj, maxIteration, pop_size, tour_size, probC, probM, 2, 3);
	}
	
	class PGA implements Runnable{
		Population p;
		Thread t;
		
		PGA(Population p){
			this.p = p;
			t = new Thread(this);
			t.start();
		}
		
		@Override
		public void run() {
			p.run();
		}
	}
	
	void run_island() {
		PGA[] lands = new PGA[numIsland];
		
		for(int g=0; g < generation; g++) {
			System.out.println("[#] Generation "+g);
			
			for(int j=0; j < numIsland; j++)
				lands[j] = new PGA(pops[j]);
			
			try {
				for(int j=0; j < numIsland; j++)
					lands[j].t.join();
			} catch(InterruptedException e) {
				System.out.println("Interrupted - PGA.join() - Population.run()");
			}
			
			migration();
		}
	}
	
	void migration() {
		int[][] best_individuals = new int[numIsland][];
		int[] best_Fitnesses = new int[numIsland];
		
		
		for(int i=0; i < numIsland; i++) {
			best_individuals[i] = pops[i].best_indiv;
			best_Fitnesses[i] = pops[i].best_fit;
		}
		
		int id = BasicTools.findMaxIndex(best_Fitnesses);
		System.out.println("		Global max clique : "+ best_Fitnesses[id] );
		
		for(int from=0; from < numIsland; from++) {
			int to = (from+1) % numIsland; // 0 -> 1 , 1 -> 2 , 2 -> 3 , 3 -> 0
			
			pops[to].replace_Indiv(best_individuals[from], best_Fitnesses[from]);
		}
	}
	
	public static void main(String[] args) throws IOException {
		// DIMACS graph instances
		//String file = "C500.9.clq";
		String file = "C2000.9.clq";
		byte[][] adj = GraphReader.adjacency(file, 1);
		
		int max_iteration = 100;
		int pop_size = 50;
		int tour_size = 4;
		double probC = 0.8;
		double probM = 0.1;
		
		int generation = 100;
		int num_Island = 4;
		
		Island pga = new Island(num_Island, generation, adj, max_iteration, pop_size, tour_size, probC, probM);
		pga.run_island();
	}

}
