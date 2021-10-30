package tsp;

import java.io.IOException;
import sojung2021.*;

public class TspMain {

	public static void main(String[] args) throws IOException {
		String filename = "a280.tsp";
		double[][] distance = GraphReader.tspDouble(filename);
		
		double probcross = 0.95;
		double probmut = 0.01;
		int generation = 100;
		int pop_size = 5000;
		int t_size = 20;
		
		
		new Population(probcross, probmut, generation);
		
		TspThread[] threads = new TspThread[3];
		for(int i=1; i<4; i++) {
			//Population p = new Population(distance, pop_size, t_size);
			System.out.println("i = "+i);
			threads[i-1] = new TspThread(new Population(distance, pop_size, t_size), i);
		}
		
		for(int i=1; i<4; i++) {
			try {
				threads[i-1].t.join();
				System.out.println("[#]  "+i+"번째 thread join!");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		for(int i=1; i<4; i++) {
			double fit = threads[i-1].p.bestFit;
			
			System.out.println(i+"번째 thread의  best Fitness = " + fit);
		}
	}

}
