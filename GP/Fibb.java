import java.util.*;

public class Fibb {
	
	public static int[] fibonacci(int n) {
		int[] fib = new int[n+1];
		fib[1] = 1;
		fib[2] = 1;
		
		fibonacciRe(fib,3,n);
		System.out.println(Arrays.toString(fib));
		return fib;
	}
	
	public static void fibonacciRe(int[] fib, int k, int n) {
		if( k <= n ) {
			fib[k] = fib[k-1] + fib[k-2];
			fibonacciRe(fib, k+1, n);
		}
	}
	
	public static void main(String[] args) {
		int[] x = {0,1,2,3,4,5,6,7,8,9,10};
		int[] y = fibonacci(10);
		
	}

}
