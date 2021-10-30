package tsp;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.ArrayUtils;


/**
 *
 * �� Ŭ������ crossover�� ������ Ŭ������ tsp ������ ������ �� �ְ� �������. 
 * ���� population�� 2���� int array�̰� fitness�� 1���� double array�̴�.
 * 
 * �� Ŭ������ ���� instance�� ���������ʰ� ����ϰԲ� �����Ѵ�.
 * 
 * Thread-safe �ϰ� ����(�� �ͤ���)
 *
 * @author Kim so jeong
 * @since  2020.11.24
 */
public class Crossover {
	// Suppresses default constructor, ensuring non-instantiability.
    private Crossover() {}
    
    
	/**
	 * 
	 * �� �Լ��� ��ʸ�Ʈ ������ ������ �Լ��� {@code pop_size} �� {@code t_size}���� �ĺ� individual�� ��� 
	 * fitness���� ���� ���� �ĺ��� �����Ѵ�. tsp�� ��� fitness�� ���� ���� ���� ������ individual ����.
	 * 
	 * java.util.Arrays �� thread safe���� �ƴ��� �𸣰ٴ�.  
	 * List�� thread safe �� �ƴϴ�, ���ľ��Ѵ�. 
	 * 
	 * @param pop_size : size of population, # of individuals
	 * @param t_size : tournament size
	 * @param fitness : fitness of population
	 * 
	 * @return id : index of chosen individual in population
	 */
	static int tournamentSelection(int pop_size, int t_size, double[] fitness) {
		List<Integer> range = IntStream.range(0, pop_size).boxed().collect(Collectors.toList());
		Collections.shuffle(range); 
		int[] entry = Arrays.copyOf(range.parallelStream().mapToInt(i->i).toArray(), t_size);
		
		int id = entry[0];
		for(int i : entry) {
			if(fitness[id] > fitness[i])
				id = i;
		}
		
		return id;
	}
    
	
    /**
     * 
     * partially-mapped crossover
     * 
     * java.utils.Arrays �� 
     * 
     * @param pop
     * @param fitness
     * @param prob_c
     * 
     * @param t_size
     * @return newPop
     */
    public static int[][] pmx(int[][] pop, double[] fitness,double prob_c, int t_size) {
    	int pop_size = pop.length;
    	int n = pop[0].length;
    	
    	int[][] newPop = new int[pop_size][n];
    	
    	for(int i=0; i < pop_size; i=i+2) {
			int p1 = tournamentSelection(pop_size, t_size, fitness);
			int p2 = tournamentSelection(pop_size, t_size, fitness);
			while(p1 == p2) { p2 = tournamentSelection(pop_size, t_size, fitness); }
			
			if( ThreadLocalRandom.current().nextDouble() < prob_c ) {
				int[] pp1 = Arrays.copyOf(pop[p1], pop[p1].length);
				int[] pp2 = Arrays.copyOf(pop[p2], pop[p2].length);
				
				int[] pos = choosePos(n);
				
				int[] o1 = new int[n]; Arrays.fill(o1, -1); 
				int[] o2 = new int[n]; Arrays.fill(o2, -1);
				
				pmxSub(o1, pos, pp2, pp1);
				pmxSub(o2, pos, pp1, pp2);
				
				newPop[i] = Arrays.copyOf(o1, o1.length);
				newPop[i+1] = Arrays.copyOf(o2, o2.length);
			}else {
				newPop[i] = Arrays.copyOf(pop[p1], pop[p1].length);
				newPop[i+1] = Arrays.copyOf(pop[p2], pop[p2].length);
			}
		}
    	
    	return newPop;
    }
    
	static int[] choosePos(int n) {
		int pos1 = ThreadLocalRandom.current().nextInt(n);
		int pos2 = ThreadLocalRandom.current().nextInt(n);
		while( pos1 == pos2 ) { pos2 = ThreadLocalRandom.current().nextInt(n); }
		
		if(pos1 > pos2) {
			int tmp = pos1;
			pos1 = pos2;
			pos2 = tmp;
		}
		
		int[] pos = {pos1,pos2};
		
		return pos;
	}
	
	// Concurrent Hash Map is 'concurrent'
	static void pmxSub(int[] o, int[] pos, int[] K, int[] V) {
		int capacity = pos[1] - pos[0] + 3;
		ConcurrentHashMap<Integer, Integer> hm = new ConcurrentHashMap<Integer, Integer>(capacity);
				
		for(int i=pos[0]; i<pos[1]; i++) {
			o[i] = K[i];
			hm.put(K[i], V[i]);
		}
		
		for(int i=0; i<o.length; i++) {
			int n = V[i];
			if( o[i] < 0 ) {
				while( hm.containsKey(n) ) {
					n = hm.get(n);
				}
				o[i] = n;
			}
		}
	}
    
    /**
     * 
     * cycle crossover
     * 
     * @param pop
     * @param fitness
     * @param prob_c
     * @param t_size
     * @return newPop
     */
    public static int[][] cx(int[][] pop, double[] fitness,double prob_c, int t_size) {
    	int pop_size = pop.length;
    	int n = pop[0].length;
    	
    	int[][] newPop = new int[pop_size][n];
    	
    	for(int i=0; i < pop_size; i=i+2) {
    		int p1 = tournamentSelection(pop_size, t_size, fitness);
			int p2 = tournamentSelection(pop_size, t_size, fitness);
			while(p1 == p2) { p2 = tournamentSelection(pop_size, t_size, fitness); }
			
			if( ThreadLocalRandom.current().nextDouble() < prob_c ) {
				int[] pp1 = Arrays.copyOf(pop[p1], pop[p1].length);
				int[] pp2 = Arrays.copyOf(pop[p2], pop[p2].length);
				
				int r = ThreadLocalRandom.current().nextInt(n);
				
				CopyOnWriteArrayList<Integer> ll = new CopyOnWriteArrayList<Integer>();
				//LinkedList<Integer> ll = new LinkedList<>(); // this linkedlist contains index value of cycle
				boolean isCycle = false;
				
				while( !isCycle ) {
					if( ll.contains(r) ) {
						isCycle = true;
						continue;
					}
					
					ll.add(r);
					int v = pp2[r];
					r = ArrayUtils.indexOf(pp1, v);
				}
				
				int[] o1 = new int[n]; Arrays.fill(o1, -1); 
				int[] o2 = new int[n]; Arrays.fill(o2, -1); 
				
				for(int j=0; j < n; j++) {
					if( ll.contains(j) ) {
						o1[j] = pp1[j];
						o2[j] = pp2[j];
					}else {
						o1[j] = pp2[j];
						o2[j] = pp1[j];
					}
				}
				
				newPop[i] = Arrays.copyOf(o1, o1.length);
				newPop[i+1] = Arrays.copyOf(o2, o2.length);
				
			} else {
				newPop[i] = Arrays.copyOf(pop[p1], pop[p1].length);
				newPop[i+1] = Arrays.copyOf(pop[p2], pop[p2].length);
			}
    	}
    	
    	return newPop;
    }
    
    
    public static void ox1() {
    	
    }
    
    public static void ox2() {
    	
    }
    
    
    /*
     * 
     * ���� ����� crossover. 
     * 
     */
    
    /**
     * 
     * 
     * @param pop
     * @param fitness
     * @param dist
     * @param prob_c
     * @param t_size
     * @return
     */
    public static int[][] han1(int[][] pop, double[] fitness, double[][] dist,double prob_c, int t_size) {
    	int pop_size = pop.length;
    	int n = pop[0].length;
    	
    	int[][] newPop = new int[pop_size][n];
    	
    	for(int i=0; i < pop_size; i++) {
    		int p1 = tournamentSelection(pop_size, t_size, fitness);
			int p2 = tournamentSelection(pop_size, t_size, fitness);
			while(p1 == p2) { p2 = tournamentSelection(pop_size, t_size, fitness); }
			
			if( ThreadLocalRandom.current().nextDouble() < prob_c ) {
				int[] pp1 = Arrays.copyOf(pop[p1], pop[p1].length);
				int[] pp2 = Arrays.copyOf(pop[p2], pop[p2].length);
				
				int[] offspring = new int[n]; Arrays.fill(offspring, -1); 
				
				Set<Integer> o_Notcontains = Collections.synchronizedSet( IntStream.range(0, n).boxed().collect(Collectors.toSet()) );
				//HashSet<Integer> o_Notcontains = (HashSet<Integer>) IntStream.range(0, n).boxed().collect(Collectors.toSet());
				
				int id = 0;
				//int id = ThreadLocalRandom.current().nextInt(n);
				
				offspring[id] = pp1[id];
				int v = pp1[id];
				
				while( ArrayUtils.contains(offspring, -1) ) {
					id++;
					o_Notcontains.remove(v);
					
					int u1 = pp1[nextId(pp1, v)];
					int u2 = pp2[nextId(pp2, v)];
					
					if( ArrayUtils.contains(offspring, u1) & ArrayUtils.contains(offspring, u2) ) { 
						// u1�� u2�� �̹� offspring�� ���ԵǾ��ִ� ���, ���� ���Ե��� ���� �������߿��� �����ϰ� �����Ѵ�.
						List<Integer> list = o_Notcontains.parallelStream().collect(Collectors.toList());
						
						int r_id = ThreadLocalRandom.current().nextInt(list.size());
						v = list.get(r_id);
					} else if( ArrayUtils.contains(offspring, u1) ){ 
						// u1�� �̹� offspring��  ���Ե� ���, u2�� ����.
						v = u2;
					} else if( ArrayUtils.contains(offspring, u2) ){
						// u2�� �̹� offspring�� ���Ե� ���, u1�� ����.
						v = u1;
					} else {
						// u1�� u2�� ��� offspring�� ���� ���, cost�� ���� ������ ����.
						if( dist[v][u1] > dist[v][u2] )
							v = u2;
						else
							v = u1;
					}
					
					offspring[id] = v;
				}
				
				newPop[i] = Arrays.copyOf(offspring, offspring.length);
				
			} else {
				newPop[i] = Arrays.copyOf(pop[p1], pop[p1].length);
			}
    	}
    	
    	return newPop;
    }
    
	static int nextId(int[] arr, int element) {
		int element_id = ArrayUtils.indexOf(arr, element);
		
		if( element_id == arr.length-1 )
			return 0;
		else
			return element_id+1;
	}
    
	
    public static int[][] han2(int[][] pop, double[] fitness, double[][] dist,double prob_c, int t_size) {
    	int pop_size = pop.length;
    	int n = pop[0].length;
    	
    	int[][] newPop = new int[pop_size][n];
    	
    	for(int i=0; i < pop_size; i++) {
    		int p1 = tournamentSelection(pop_size, t_size, fitness);
			int p2 = tournamentSelection(pop_size, t_size, fitness);
			while(p1 == p2) { p2 = tournamentSelection(pop_size, t_size, fitness); }
			
			if( ThreadLocalRandom.current().nextDouble() < prob_c ) {
				int[] pp1 = Arrays.copyOf(pop[p1], pop[p1].length);
				int[] pp2 = Arrays.copyOf(pop[p2], pop[p2].length);
				
				int[] offspring = new int[n]; Arrays.fill(offspring, -1); 
				
				Set<Integer> o_Notcontains = Collections.synchronizedSet( IntStream.range(0, n).boxed().collect(Collectors.toSet()) );
				//HashSet<Integer> o_Notcontains = (HashSet<Integer>) IntStream.range(0, n).boxed().collect(Collectors.toSet());
				
				int id = 0;
				//int id = ThreadLocalRandom.current().nextInt(n);
				
				offspring[id] = pp1[id];
				int v = pp1[id];
				
				while( ArrayUtils.contains(offspring, -1) ) {
					id++;
					o_Notcontains.remove(v);
					
					int u1 = pp1[nextId(pp1, v)];
					int u2 = pp2[nextId(pp2, v)];
					
					if( ArrayUtils.contains(offspring, u1) & ArrayUtils.contains(offspring, u2) ) { 
						// u1�� u2�� �̹� offspring�� ���ԵǾ��ִ� ���, ���� ���Ե��� ���� �������߿��� �����ϰ� �����Ѵ�.
						List<Integer> list = o_Notcontains.parallelStream().collect(Collectors.toList());
						int r_id = ThreadLocalRandom.current().nextInt(list.size());
						v = list.get(r_id);
					} else if( ArrayUtils.contains(offspring, u1) ){ 
						// u1�� �̹� offspring��  ���Ե� ���, u2�� ����.
						v = u2;
					} else if( ArrayUtils.contains(offspring, u2) ){
						// u2�� �̹� offspring�� ���Ե� ���, u1�� ����.
						v = u1;
					} else {
						// u1�� u2�� ��� offspring�� ���� ���, 70%�� Ȯ���� cost�� ���� ���� �����ϰ� 30%�� Ȯ���� cost�� ���� ������ ����.
						if( ThreadLocalRandom.current().nextDouble() < 0.7 )
							v = ( dist[v][u1] > dist[v][u2] ) ? u2 : u1 ;
						else
							v = ( dist[v][u1] > dist[v][u2] ) ? u1 : u2 ;
					}
					
					offspring[id] = v;
				}
				
				newPop[i] = Arrays.copyOf(offspring, offspring.length);
				
			} else {
				newPop[i] = Arrays.copyOf(pop[p1], pop[p1].length);
			}
    	}
    	
    	return newPop;
    }
    
    public static void kim(int[][] pop, double[] fitness,double prob_c, int t_size) {
    	
    }
}
