package tsp;

public class TspThread implements Runnable {
	Population p;
	Thread t;
	int c_type;
	
	TspThread(Population p, int c_type) {
		this.p = p; this.c_type = c_type;
		t = new Thread(this);
		t.start();
	}
	
	@Override
	public void run() {
		p.run(c_type);
	}
	
}
