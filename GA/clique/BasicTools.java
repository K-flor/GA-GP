import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.math3.linear.BlockRealMatrix;

public class BasicTools {
	private static final int SHUFFLE_THRESHOLD        =    5;

    private BasicTools() { }

    public static Point[] splitJobs(int numOfJobs, int numCore) {
        assert (numCore <= numOfJobs) : "BasicTools.splitJobs() - numCore < numOfJobs";

        Point[] pool = new Point[numCore];
        int[] tmp = new int[numCore];
        int each = numOfJobs/numCore;
        int more = numOfJobs - (numCore * each);
        Arrays.fill(tmp, each);

        for (int i = 0; i < more; i++)
            tmp[i]++;

        for (int i = 1; i < numCore; i++) {
            tmp[i] = tmp[i-1] + tmp[i];
        }

        int start = 0;
        for (int i = 0; i < numCore; i++) {
            pool[i] = new Point(start, tmp[i]);
            start = tmp[i];
        }

        return pool;
    }
    
	public static int[] getPermutationZero2(int n, Random rand) {
		int pos, temp;
		int[] L = new int[n];

		//L 에 0 ~ (n - 1) 을 저장
		for (int i = 0; i < n; i++)
			L[i] = i;

		for (int i = 0; i < n; i++) {
			pos = nextInt(i, n, rand);
			temp = L[i];
			L[i] = L[pos];
			L[pos] = temp;
		}
		return L;
	}
	
	public static int[] range(int start, int end) {
		int[] re = new int[end-start];
		int value = start;

		for (int i = 0; i < end-start; i++) {
			re[i] = value++;
		}
		return re;
	}
	
	public static int[] range(int n) {
		int[] re = new int[n];

		for (int i = 0; i < n; i++)
			re[i] = i;

		return re;
	}
    
	public static int nextInt(int s, int e, Random ran) {
		return ran.nextInt(e - s) + s;
	}

    public static void shuffle(List<?> list) {
        shuffle2(list);
    }

    public static void shuffle2(java.util.List<?> list) {
        int size = list.size();
        if (size < SHUFFLE_THRESHOLD || list instanceof RandomAccess) {
            for (int i=size; i>1; i--)
                swap(list, i-1, ThreadLocalRandom.current().nextInt(i));
        } else {
            Object[] arr = list.toArray();

            // Shuffle array
            for (int i=size; i>1; i--)
                swap(arr, i-1, ThreadLocalRandom.current().nextInt(i));

            // Dump array back into list
            // instead of using a raw type here, it's possible to capture
            // the wildcard but it will require a call to a supplementary
            // private method
            ListIterator it = list.listIterator();
            for (Object e : arr) {
                it.next();
                it.set(e);
            }
        }
    }

    private static void swap(Object[] arr, int i, int j) {
        Object tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    public static void swap(java.util.List<?> list, int i, int j) {
        // instead of using a raw type here, it's possible to capture
        // the wildcard but it will require a call to a supplementary
        // private method
        final List l = list;
        l.set(i, l.set(j, l.get(i)));
    }

    public static int findMinIndex(double[] M){
        int pos=0;
        double value = M[0];

        for(int i=1; i < M.length; i++){
            if(M[i] < value){
                pos = i;
                value = M[i];
            }
        }

        return pos;
    }
    
    public static int findMinIndex(int[] M){
        int pos=0;
        int value = M[0];

        for(int i=1; i < M.length; i++){
            if(M[i] < value){
                pos = i;
                value = M[i];
            }
        }

        return pos;
    }
    
    public static int findMaxIndex(double[] M) {
    	int pos = 0;
    	double value = M[0];
    	
    	for(int i=1; i < M.length; i++) {
    		if( M[i] > value ) {
    			pos = i;
    			value = M[i];
    		}
    	}
    	
    	return pos;
    }
    
    public static int findMaxIndex(int[] M) {
    	int pos = 0;
    	int value = M[0];
    	
    	for(int i=1; i < M.length; i++) {
    		if( M[i] > value ) {
    			pos = i;
    			value = M[i];
    		}
    	}
    	
    	return pos;
    }
    
    public static int sum_element(int[] p) {
    	int sum = 0;
    	for(int i : p)
    		sum += i;
    	return sum;
    }
    
    public static int sum_element(byte[] p) {
    	int sum = 0;
    	for(byte i : p)
    		sum += i;
    	
    	return sum;
    }

    public static int[][] copy2DArray(int[][] M, int newLength){
        int[][] tmpM = new int[newLength][];
        for(int i=0; i<newLength; i++)
            tmpM[i] = Arrays.copyOf(M[i], M[i].length);

        return tmpM;
    }
    
    public static byte[][] copy2DArray(byte[][] M, int newLength){
    	byte[][] tempM = new byte[newLength][];
    	for(int i=0; i<newLength; i++)
    		tempM[i] = Arrays.copyOf(M[i], M[i].length);
    	
    	return tempM;
    }
    
    /*
     * this function returns Max of Min element in int/double array, M
     */
    public static int maxValue(int[] M) {
    	int max = Integer.MIN_VALUE;
    	for(int i : M) {
    		if(i > max)
    			max = i;
    	}
    	return max;
    }
    
    public static double maxValue(double[] M) {
    	double max = Double.MIN_VALUE;
    	for(double i : M) {
    		if( i > max )
    			max = i;
    	}
    	return max;
    }
    
    public static int minValue(int[] M) {
    	int min = Integer.MAX_VALUE;
    	for(int i : M) {
    		if( i < min )
    			min = i;
    	}
    	return min;
    }
    
    public static double minValue(double[] M) {
    	double min = Double.MAX_VALUE;
    	for(double i : M) {
    		if( i < min )
    			min = i;
    	}
    	return min;
    }  
    
    /*
     * check array contains only value_contain value. 
     * */
    public static boolean isFilledwValue(int[] M, int value_contain) {
    	for(int i=0, l=M.length; i<l; i++) {
    		if(M[i] != value_contain)
    			return false;
    	}
    	return true;
    }
    
    public static boolean isFilledwValue(byte[] M, byte value_contain) {
    	for(int i=0, l=M.length; i<l; i++) {
    		if(M[i] != value_contain)
    			return false;
    	}
    	return true;
    }
    
    public static boolean isFilledwValue(double[] M, double value_contain) {
    	for(int i=0, l=M.length; i<l; i++) {
    		if(M[i] != value_contain)
    			return false;
    	}
    	return true;
    }
    
    public static int[] deleteAtIndex(int[] M, int idTodelete) {
    	int l = M.length;
    	int[] new_M = new int[l - 1];
    	int j=0;
    	for(int i=0; i<l; i++) {
    		if( i == idTodelete )
    			continue;
    		
    		new_M[j] = M[i];
    		j++;
    	}
    	
    	return new_M;
    }
    
 /***************************************************************************
  * 배열 M에 {valueToFind}가 있는지 확인하고 
  * 없다면 null을 return, 있다면 index 정보를  담아 int array로 return.
  * 
  *  * shuffle은 index 정보들을 섞어서 int array로 return
  * 
  ***************************************************************************/    
    public static int[] findIndex_shuffle(int[] M, int valueToFind) {
    	List<Integer> arr = new ArrayList<>();
		
		for(int i=0; i<M.length; i++) {
			if( M[i] == valueToFind )
				arr.add(i);
		}
		
		if(arr.size() == 0)
			return null;
		else {
			shuffle(arr);
			int[] arr2 = arr.stream().mapToInt(i -> i).toArray();
			return arr2;	
		}
    }
    
    public static int[] findIndex(int[] M, int valueToFind) {
    	List<Integer> arr = new ArrayList<>();
		
		for(int i=0; i<M.length; i++) {
			if( M[i] == valueToFind )
				arr.add(i);
		}
		
		if(arr.size() == 0)
			return null;
		else {
			int[] arr2 = arr.stream().mapToInt(i -> i).toArray();
			return arr2;	
		}
    }
    
    public static int[] findIndex_shuffle(double[] M, double valueToFind) {
    	List<Integer> arr = new ArrayList<>();
		
		for(int i=0; i<M.length; i++) {
			if( M[i] == valueToFind )
				arr.add(i);
		}
		if(arr.size() == 0)
			return null;
		else {
			shuffle(arr);
			int[] arr2 = arr.stream().mapToInt(i -> i).toArray();
			
			return arr2;	
		}
    }
    
    public static int[] findIndex(double[] M, double valueToFind) {
    	List<Integer> arr = new ArrayList<>();
		
		for(int i=0; i<M.length; i++) {
			if( M[i] == valueToFind )
				arr.add(i);
		}
		if(arr.size() == 0)
			return null;
		else {
			int[] arr2 = arr.stream().mapToInt(i -> i).toArray();
			return arr2;	
		}
    }
    
    /** CONVERT ARRAY
     * 
     * byte[] to double[]
     * byte[] to int[]
     * double[] to int[]
     * int[] to double[]
     * @return 
     * 
     */
     public static double[] convert2Double(byte[] sources) {
    	 Byte[] a = ArrayUtils.toObject(sources);
    	 return Arrays.stream(a).mapToDouble(i -> i).toArray();
     }
     
     public static double[] convert2Double(int[] sources) {
    	 return Arrays.stream(sources).asDoubleStream().toArray();
     }
     
     public static double[][] convert2Double2D(byte[][] sources) {
    	 double[][] arr = new double[sources.length][];
    	 for(int i=0; i<sources.length; i++)
    		 arr[i] = convert2Double(sources[i]);
    	 return arr;
     }
     
     public static double[][] convert2Double2D(int[][] sources) {
    	 double[][] arr = new double[sources.length][];
    	 for(int i=0; i<sources.length; i++)
    		 arr[i] = convert2Double(sources[i]);
    	 return arr;
     }
     
     public static int[] convert2Int(byte[] sources) {
    	 Byte[] a = ArrayUtils.toObject(sources);
    	 return Arrays.stream(a).mapToInt(i -> i).toArray();
     }
     
     public static int[] convert2Int(double[] sources) {
    	 return Arrays.stream(sources).mapToInt(i -> (int)i).toArray();
     }
     
     public static int[][] convert2Int2D(byte[][] sources){
    	 int[][] arr = new int[sources.length][];
    	 for(int i=0; i<sources.length; i++)
    		 arr[i] = convert2Int(sources[i]);
    	 return arr;
     }
     
     public static int[][] convert2Int2D(double[][] sources){
    	 int[][] arr = new int[sources.length][];
    	 for(int i=0; i<sources.length; i++)
    		 arr[i] = convert2Int(sources[i]);
    	 return arr;
     }
     
     public static byte[] convert2Byte(double[] sources) {
    	 byte[] arr = new byte[sources.length];
    	 for(int i=0; i<arr.length; i++)
    		 arr[i] = (byte) sources[i];
    	 return arr;    			 
     }
     
     public static byte[][] convert2Byte2D(double[][] sources){
    	 byte[][] arr = new byte[sources.length][];
    	 for(int i=0; i<sources.length; i++)
    		 arr[i] = convert2Byte(sources[i]);
    	 return arr;
     }
     
    /********************************* Matrix Multiplication *******************
     * 
     * 2개의 2차원 배열을 곱한다. 
     * 곱셈이 불가능한 크기인 경우 null을 return 한다
     * 
     * @param A : 2D Array
     * @param B : 2D Array
     * 
     * @return A*B
     * 
     * - byte
     * - int
     * ***********************************************************************/
    public static byte[][] matrixMul(byte[][] A, byte[][] B){
    	int row_a = A.length, col_a = A[0].length;
    	int row_b = B.length, col_b = B[0].length;
    	
    	if(col_a != row_b) {
    		System.out.println("[!] Error from BasicTools.matrixMul ... ");
    		System.out.println("[!] two arrays do not satisfy matrix multiplication condition ...");
    		System.out.println("number of column of A : "+col_a+" ... number of row of B : "+row_b);
    		return null;
    	}
    	
    	byte[][] c = new byte[row_a][col_b];
    	for(int i=0; i<row_a; i++){
			for(int k=0; k<col_a; k++){
				for(int j=0; j<col_b; j++)
					c[i][j] += A[i][k] * B[k][j];
			}
		}
    	return c;
    }
    
    public static int[][] matrixMul(int[][] A, int[][] B){
    	int row_a = A.length, col_a = A[0].length;
    	int row_b = B.length, col_b = B[0].length;
    	
    	if(col_a != row_b) {
    		System.out.println("[!] Error from BasicTools.matrixMul ... ");
    		System.out.println("[!] two arrays do not satisfy matrix multiplication condition ...");
    		System.out.println("number of column of A : "+col_a+" ... number of row of B : "+row_b);
    		return null;
    	}
    	
    	int[][] c = new int[row_a][col_b];
    	for(int i=0; i<row_a; i++){
			for(int k=0; k<col_a; k++){
				for(int j=0; j<col_b; j++)
					c[i][j] += A[i][k] * B[k][j];
			}
		}
    	return c;
    }
    
    public static byte[][] matrixMul_BRM(byte[][] A, byte[][] B){
    	double[][] A_p = convert2Double2D(A);
    	double[][] B_p = convert2Double2D(B);
    	
    	BlockRealMatrix M = new BlockRealMatrix(A_p); 
    	BlockRealMatrix N = new BlockRealMatrix(B_p);
    	
    	BlockRealMatrix C = M.multiply(N);
    	
    	double[][] out = C.getData();
    	return convert2Byte2D(out);
    }
    
    public static byte[][] matrixMul_BRM(byte[][] A){
    	double[][] A_p = convert2Double2D(A);
    	
    	BlockRealMatrix M = new BlockRealMatrix(A_p); 
    	
    	BlockRealMatrix C = M.multiply(M);
    	
    	double[][] out = C.getData();
    	return convert2Byte2D(out);
    }
    
    /*
     * 정방 행렬의 대각 성분을 0으로 변경한다 .
     */
    public static void removeDiagonal(byte[][] a) {
    	if( a.length != a[0].length ) {
    		System.out.println("[!] this matrix is not n x n");
    		return;
    	}
    	
    	int n = a.length;
    	for(int i=0; i<n; i++) {
    		a[i][i] = 0;
    	}
    }
    
    /**
     * this method performs like R's rbind
     * For example, there are two 2d arrays
     * 		a = {{1,2,3},{4,5,6}}
     * 		b = {{7,8,9},{1,2,3}}   
     * 
     * then the result will be
     * 			{{1,2,3},
     * 			 {4,5,6},
     * 			 {7,8,9},
     * 			 {1,2,3}}
     * 
     */
    public static int[][] append(int[][] a, int[][] b) {
        int[][] result = new int[a.length + b.length][];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
   
    public static byte[][] append(byte[][] a, byte[][] b) {
        byte[][] result = new byte[a.length + b.length][];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
    
    
	/***************************** Tournament selection ************************************
	 * 
	 * @param N : population size
	 * @param K : tournament size
	 * @param fitness : array of fitness value
	 * 
	 * @return
	 * 
	 * 1. tournament_Min : fitness가 작은 chromosome의 id를 return
	 * 		1-1 fitness가 double array
	 * 		1-2 fitness가 int array
	 * 
	 * 2. tournament_Max : fitness가 큰 chromosome의 id를 return
	 * 		2-1 fitness가 double array
	 * 		2-2 fitness가 int array
	 * 
	 ***************************************************************************************/
    public static int tournament_Min(int N, int K, double[] fitness){
    	// 0 ~ N-1 중 K개 선택.
    	
        Set<Integer> set = new HashSet<>();
        while(set.size() != K){
            set.add( ThreadLocalRandom.current().nextInt(N) );
        }

        int[] entry = set.stream().mapToInt(i->i).toArray();
        int id = entry[0];
        for(int i : entry) {
            if(fitness[id] > fitness[i])
                id = i;
        }

        return id;
    }
    
    public static int tournament_Min(int N, int K, int[] fitness){
    	// 0 ~ N-1 중 K개 선택.
        Set<Integer> set = new HashSet<>();
        while(set.size() != K){
            set.add( ThreadLocalRandom.current().nextInt(N) );
        }

        int[] entry = set.stream().mapToInt(i->i).toArray();
        int id = entry[0];
        for(int i : entry) {
            if(fitness[id] > fitness[i])
                id = i;
        }

        return id;
    }
    
    public static int tournament_Max(int N, int K, double[] fitness) {
    	Set<Integer> set = new HashSet<>();
        while(set.size() != K){
            set.add( ThreadLocalRandom.current().nextInt(N) );
        }

        int[] entry = set.stream().mapToInt(i->i).toArray();
        int id = entry[0];
        for( int i : entry) {
        	if(fitness[id] < fitness[i] )
        		id = i;
        }
        
        return id;
    }
    
    public static int tournament_Max(int N, int K, int[] fitness) {
    	Set<Integer> set = new HashSet<>();
        while(set.size() != K){
            set.add( ThreadLocalRandom.current().nextInt(N) );
        }

        int[] entry = set.stream().mapToInt(i->i).toArray();
        int id = entry[0];
        for( int i : entry) {
        	if( fitness[id] < fitness[i] )
        		id = i;
        }
        
        return id;
    }
    
    public static boolean isConnected(int[] p, int u, byte[][] adj) { // p : 0 or 1 로 구성된 int array
		for(int i=0; i<p.length; i++) {
			if( p[i] == 1 && adj[i][u] == 0)
				return false;
		}
		
		return true;
	}
    
    /**
     * Array의 두 개 index를 선택 후 정렬해서 return    
     */
    public static int[] choosePos(int n) {
    	int pos1 = RandomUtils.nextInt(0,n);
    	int pos2 = RandomUtils.nextInt(0,n);
    	while(pos1 == pos2) { pos2 = RandomUtils.nextInt(0,n); }
    	
    	if( pos1 > pos2) {
    		int temp = pos2;
    		pos2 = pos1;
    		pos1 = temp;
    	}
    	
    	int[] P = {pos1, pos2};
    	return P;
    }

    
    /**
     * 
     * Sorting
     * 	- 교수님 TWIN에서 가져옴 
     *  - Decreasing sort
     * 
     **/
    // -quickSortDec
 	public static void quickSortDec(int[] A, int p, int r) {
 		int splitPoint = -1;

 		if (p < r) {
 			splitPoint = splitDec(A, p, r);
 			quickSortDec(A, p, splitPoint - 1);
 			quickSortDec(A, splitPoint + 1, r);
 		}
 	}

 	// -quickSortDec
 	public static void quickSortDec(short[] A, int p, int r) {
 		int splitPoint = -1;

 		if (p < r) {
 			splitPoint = splitDec(A, p, r);
 			quickSortDec(A, p, splitPoint - 1);
 			quickSortDec(A, splitPoint + 1, r);
 		}
 	}

 	// -quickSortDec
 	public static void quickSortDec(double[] A, int p, int r) {
 		int splitPoint = -1;

 		if (p < r) {
 			splitPoint = splitDec(A, p, r);
 			quickSortDec(A, p, splitPoint - 1);
 			quickSortDec(A, splitPoint + 1, r);
 		}
 	}
 	
	/*-------------------------------------------------------------------------------------------------
	Helper method for TWIN.quickSortDec()  //int
	-------------------------------------------------------------------------------------------------*/
	public static int splitDec(int[] A, int p, int r) {
		int target = A[p];
		int splitPoint = p;
		int temp;

		for (int j = p + 1; j <= r; j++) {
			if (A[j] > target) {
				splitPoint++;
				temp = A[splitPoint];
				A[splitPoint] = A[j];
				A[j] = temp;
			}
		}

		A[p] = A[splitPoint];
		A[splitPoint] = target;

		return splitPoint;
	}

	/*-------------------------------------------------------------------------------------------------
	Helper method for TWIN.quickSortDec()		//short
	-------------------------------------------------------------------------------------------------*/
	public static int splitDec(short[] A, int p, int r) {
		short target = A[p];
		int splitPoint = p;
		short temp;

		for (int j = p + 1; j <= r; j++) {
			if (A[j] > target) {
				splitPoint++;
				temp = A[splitPoint];
				A[splitPoint] = A[j];
				A[j] = temp;
			}
		}

		A[p] = A[splitPoint];
		A[splitPoint] = target;

		return splitPoint;
	}

	/*-------------------------------------------------------------------------------------------------
	Helper method for TWIN.quickSortDec()	//double
	-------------------------------------------------------------------------------------------------*/
	public static int splitDec(double[] A, int p, int r) {
		double target = A[p];
		int splitPoint = p;
		double temp;

		for (int j = p + 1; j <= r; j++) {
			if (A[j] > target) {
				splitPoint++;
				temp = A[splitPoint];
				A[splitPoint] = A[j];
				A[j] = temp;
			}
		}

		A[p] = A[splitPoint];
		A[splitPoint] = target;

		return splitPoint;
	}
	
    /*************************************************************
     * 
     * 	sort_index
     *  --> integer array 와 double array를 sort 시키는데
     *  	결과를 index가 오름차순으로 정렬된 값을 돌려준다. 
     *  
     *  예) {5,0,6,1,2} 를 sort_index 함수에 적용하면 
     *  	{1,3,4,0,2} 가 return
     * 
     * 
     * 	decsort_index
     * 	---> 내림차순으로 정렬
     * @return 
     **/
	// 추가가 필요한 상항
	// Map을 사용하는데 이를 사용하지 않고 단순 array만 사용해보기.
    public static int[] sort_index(int[] M) {
    	Map<Integer, Integer> map = new HashMap<>();
    	for(int i=0, l=M.length; i < l; i++)
    		map.put(M[i], i);
    	
    	Object[] mapkey = map.keySet().toArray();
    	Arrays.sort(mapkey);
    	
    	int[] sorted_id = new int[M.length];
    	int i=0;
    	for(Integer nKey : map.keySet()) {
    		sorted_id[i] = map.get(nKey);
    		i++;
    	}
    	
    	return sorted_id;
    }
    
    public static int[] sort_index(double[] M) {
    	Map<Double, Integer> map = new HashMap<>();
    	for(int i=0, l=M.length; i < l; i++)
    		map.put(M[i], i);
    	
    	Object[] mapkey = map.keySet().toArray();
    	Arrays.sort(mapkey);
    	
    	int[] sorted_id = new int[M.length];
    	int i=0;
    	for(Double nKey : map.keySet()) {
    		sorted_id[i] = map.get(nKey);
    		i++;
    	}
    	
    	return sorted_id;
    }

	public static int[] decsort_index(int[] M) {
		Map<Integer, Integer> map = new HashMap<>();
    	for(int i=0, l=M.length; i < l; i++)
    		map.put(M[i], i);
    	
    	Object[] mapkey = map.keySet().toArray();
    	Arrays.sort(mapkey);
    	
    	int[] sorted_id = new int[M.length];
    	int i = M.length -1; 
    	for(Integer nKey : map.keySet()) {
    		sorted_id[i] = map.get(nKey);
    		i--;
    	}
    	
    	return sorted_id;
	}
	
	public static int[] decsort_index(double[] M) {
		Map<Double, Integer> map = new HashMap<>();
    	for(int i=0, l=M.length; i < l; i++)
    		map.put(M[i], i);
    	
    	Object[] mapkey = map.keySet().toArray();
    	Arrays.sort(mapkey);
    	
    	int[] sorted_id = new int[M.length];
    	int i = M.length -1; 
    	for(Double nKey : map.keySet()) {
    		sorted_id[i] = map.get(nKey);
    		i--;
    	}
    	
    	return sorted_id;
	}
	
	 /***************************************************************************
	  
	 		Print Array
	 		
	  ***************************************************************************/    
	public static void print2DArray(byte[][] M) {
		int n = M.length;
		int l = M[0].length;
		
		System.out.printf("%5s\t|","graph");
		for(int i=0; i<l;i++)
			System.out.printf("%3d ", i);
		System.out.println();
		System.out.println("----".repeat(l));
		for(int i=0; i<n; i++) {
			System.out.printf("%5d\t|", i);
			for(int j=0; j<l; j++)
				System.out.printf("%3d ", M[i][j]);
			System.out.println();
		}
		System.out.println();
	}
	
	public static void print2DArray(int[][] M) {
		int n = M.length;
		int l = M[0].length;
		
		System.out.printf("%5s\t|","graph");
		for(int i=0; i<l;i++)
			System.out.printf("%3d ", i);
		System.out.println();
		System.out.println("----".repeat(l));
		for(int i=0; i<n; i++) {
			System.out.printf("%5d\t|", i);
			for(int j=0; j<l; j++)
				System.out.printf("%3d ", M[i][j]);
			System.out.println();
		}
		System.out.println();
	}
	
	public static void print2DArray(double[][] M) {
		int n = M.length;
		int l = M[0].length;
		
		System.out.printf("%5s\t|","graph");
		for(int i=0; i<l;i++)
			System.out.printf("%3.f ", i);
		System.out.println();
		System.out.println("----".repeat(l));
		for(int i=0; i<n; i++) {
			System.out.printf("%5d\t|", i);
			for(int j=0; j<l; j++)
				System.out.printf("%3.f ", M[i][j]);
			System.out.println();
		}
		System.out.println();
	}
}

