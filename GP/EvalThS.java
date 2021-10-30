public class EvalThS implements Runnable {
	Tree[] pop;
	int start, end;

	static int[] data;
	static int[] obs;
	static int[] fit_mae; 
	
	EvalThS(int[] data, int[] obs, int pop_size){
		this.data = data; this.obs = obs;
		fit_mae = new int[pop_size];
	}
	
	EvalThS(Tree[] pop, int start, int end) {
		this.pop = pop; this.start = start;	this.end = end;
	}
	
	@Override
	public void run() {
		for(int i= start; i<end ; i++)
			fit_mae[i] = MAE( evaluation(pop[i]) );
	}
	
	public int[] evaluation(Tree p) {
		int[] f = new int[obs.length];
		for(int i=0; i<f.length; i++)
			f[i] = p.evaluation(data[i]);
		
		return f;
	}
	
	public int MAE(int[] f) {
		int sum = 0;
		for(int i=0; i<f.length; i++) 
			sum = sum + Math.abs(f[i]-obs[i]);
		
		return sum;
	}
	

}
