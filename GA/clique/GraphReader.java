// [2021-01-07]  : 함수 adjacency_graph, adjacency_mtx 추가

import java.util.*;
import java.util.Scanner;
import java.io.*;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.apache.commons.lang3.StringUtils;
import org.jgrapht.Graph;
import org.jgrapht.GraphMetrics;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class GraphReader {
	/**
	*	tsp file을 읽어 vertex사이의 거리를 double[][]로 돌려준다
	*	adjacency 행렬과 동일한 형태이나 연결된 정점의 표시를 1이 아닌 거리값으로 돌려준다.
	*/
	public static double[][] tspDouble(String tspfile)throws IOException {
		double[][] vertex = new double[1][2];
		int size=1;
		EuclideanDistance distance = new EuclideanDistance();

		Scanner sc = new Scanner(new File(tspfile));

		while(sc.hasNextLine()){
			String s = sc.nextLine();
			StringTokenizer line = new StringTokenizer(s);

			String[] tokens = new String[line.countTokens()];
			int index = 0;
			while(line.hasMoreTokens()){
				tokens[index] = line.nextToken();
				index++;
			}

			if(tokens.length == 0) 
				continue;
			else if (tokens[0].equals("EOF"))
				break;
			else if(tokens[0].equals("DIMENSION")) {
				size = Integer.parseInt(tokens[2]);
				vertex = new double[size][2];
				continue;
			}
			else if(tokens[0].equals("DIMENSION:")){
				size = Integer.parseInt(tokens[1]);
				vertex = new double[size][2];
				continue;
			}

			for(int i=1;i<=size;i++){
				if( tokens[0].equals(Integer.toString(i)) ){
					vertex[i-1][0] =  Double.parseDouble(tokens[1]);
					vertex[i-1][1] =  Double.parseDouble(tokens[2]);
					break;
				}
			}
		}
		sc.close();
		
		double[][] adj_M = new double[size][size];
		for (int i=0; i<adj_M.length; i++){
			for(int j=i+1; j<adj_M[0].length; j++)
				adj_M[i][j] = adj_M[j][i] = distance.compute(vertex[i],vertex[j]);
		}

		return adj_M;
	}
	

	/**
	*	tspDouble함수로 파일을 읽으면 double[][]을 돌려주지만 이함수는 Math.round()를 이용하여
	*	int[][]를 반환한다
	*/
	public static int[][] tspInt(String tspfile) throws IOException{
		double[][] adj = tspDouble(tspfile);
		int[][] Adj = new int[adj.length][adj[0].length];

		for(int i=0; i<adj.length; i++){
			for(int j=0; j<adj[0].length; j++)
				Adj[i][j] = (int)Math.round(adj[i][j]);
		}
		return Adj;
	}
	

	/**
	*	주어진 Dimac파일(.col)의 Adjacency를 2차 byte 배열로 돌려준다.
	*	[주의] 함수의 매개변수 v01을 통해 파일의 정점이 0부터 시작하는지 1부터 시작하는지 알려줘야함.
	*/
	public static byte[][] adjacency(String file, int v01)throws IOException{
		int size=0;
		byte[][] G;
		Scanner scan = new Scanner(new File(file));
		
		while(scan.hasNextLine()){
			String s = scan.nextLine();
			String[] word = s.split("\\s+");
			if(word[0].equals("p")) size = Integer.parseInt(word[2]);
		}
		G = new byte[size][size];
		scan.close();

		scan = new Scanner(new File(file));

		if(v01 == 1){ // 파일의 정점 번호가 1부터 시작하는 경우
			while(scan.hasNextLine()){
				String s = scan.nextLine();
				String[] vertex = s.split("\\s+");

				if(vertex[0].equals("e")){
					int a = Integer.parseInt(vertex[1])-1;
					int b = Integer.parseInt(vertex[2])-1;
					G[a][b] = G[b][a]=1;
				}
			}
			scan.close();
		}
		else { // 파일의 정점 번호가 0부터 시작하는 경우
				while(scan.hasNextLine()){
				String s = scan.nextLine();
				String[] vertex = s.split("\\s+");

				if(vertex[0].equals("e")){
					int a = Integer.parseInt(vertex[1]);
					int b = Integer.parseInt(vertex[2]);
					G[a][b] = G[b][a]=1;
				}
			}
			scan.close();
		}
		return G;
	}
	

	/**
	*	graph file format
	*/
	public static byte[][] adjacency_graph(String file) throws IOException {
		int size = 0;
		String type = "";
		byte[][] G;

		Scanner scan = new Scanner(new File(file));
		int line = 0;
		while( scan.hasNextLine() ){
			String s = scan.nextLine();
			String[] word = s.split("\\s+");
			++line;
			if( line == 1){
				size = Integer.parseInt(word[0]);
				type = word[2];
				break;
			}
		}
		scan.close();
		System.out.println(size);

		G = new byte[size][size];
		line = -1;
		scan = new Scanner(new File(file));
		
		if( type.equals("0") ){
			while( scan.hasNextLine() ){
				String s = scan.nextLine();
				String[] word = s.split("\\s+");
				
				++line;
				if( line == 0 )
					continue;

				if( word.length == 0 || word[0].equals("") )
					continue;


				//System.out.println("line number " + line +", word length "+word.length);
				int a = line-1;
				for(int i=0, m = word.length; i<m; i++){
					int b = Integer.parseInt(word[i])-1;
					G[a][b] = G[b][a] = 1;
				}
			}
			scan.close();
		}
		else {  // weight 있음
			while( scan.hasNextLine() ){
				String s = scan.nextLine();
				String[] word = s.split("\\s+");

				++line;
				if( line == 0 )
					continue;

				if( word.length == 0 )
					continue;
				
				int a = line - 1; 
				for(int i=0, m = word.length; i < m; i = i + 2){
					int b = Integer.parseInt(word[i])-1;
					G[a][b] = G[b][a] = 1;
				}
			}
			scan.close();
		}
		
		return G;
	}


	/**
	*	MatrixMarket matrix Format (.mtx)
	*/
	public static byte[][] adjacency_mtx(String file) throws IOException {
		int line = 0;
		int size = 0;
		byte[][] G;

		Scanner scan = new Scanner(new File(file));

		while( scan.hasNextLine() ){
			String s = scan.nextLine();
			
			if (StringUtils.startsWith(s, "%") || s.isEmpty())
					continue;
			
			String[] word = s.split("\\s+");
			++line;
			if( line == 1 ) {
				size = Integer.parseInt(word[0]);
				break;
			}
		}
		scan.close();

		G = new byte[size][size];
		scan = new Scanner(new File(file));
		line = 0;
		while( scan.hasNextLine() ) {
			String s = scan.nextLine();
			String[] word = s.split("\\s+");

			if (StringUtils.startsWith(s, "%") || s.isEmpty())
					continue;
			
			++line;
			if( line == 1 )
				continue;
			
			int a = Integer.parseInt(word[0])-1;
			int b = Integer.parseInt(word[1])-1;
			if( a != b)
				G[a][b] = G[b][a]= 1;
		}
		scan.close();

		return G;
	}
	
	
	/**
	*	가중치가있는 그래프(가중치는 int값)를 읽어 2차 배열로 돌려준다 
	*/
	public static int[][] weightAdj(String file, int v01)throws IOException {
		int size;
		int u, v, w;
		int[][] G = new int[1][1];
		Scanner scan = new Scanner(new File(file));

		if(v01 == 0){
			while (scan.hasNextLine()) {
				String str = scan.nextLine();

				if (StringUtils.startsWith(str, "c") || str.isEmpty())
					continue;

				String[] token = str.split("\\s+");

				if (token[0].equals("p")) {
					size = Integer.parseInt(token[2]);
					G = new int[size][size];
				}

				if (token[0].equals("e")) {
					u = Integer.parseInt(token[1]);
					v = Integer.parseInt(token[2]);
					w = Integer.parseInt(token[3]);
					G[u][v] = G[v][u] = w;
				}
			}
			scan.close();
		}
		else{
			while (scan.hasNextLine()) {
				String str = scan.nextLine();
				if (StringUtils.startsWith(str, "c") || str.isEmpty())
					continue;

				String[] token = str.split("\\s+");

				if (token[0].equals("p")) {
					size = Integer.parseInt(token[2]);
					G = new int[size][size];
				}

				if (token[0].equals("e")) {
					u = Integer.parseInt(token[1])-1;
					v = Integer.parseInt(token[2])-1;
					w = Integer.parseInt(token[3]);
					G[u][v] = G[v][u] = w;
				}
			}
			scan.close();
		}

		return G;
	}
	

	/**
	*	 함수 adjacency로 읽은 인접행렬을 이용하여
	*	 각 정점이 이어진 정점을 jagged array로 반환한다
	*		connectV1은 모든 열 0번째 행에는 연결된 정점수 + 연결된 정점을 저장하고있다.
	*		connectV2는 연결된 정점만 저장.
	*/
	public static int[][] connectV1(String file, int v01)throws IOException{
		byte[][] G = adjacency(file,v01);
		int[][] jag = new int[G.length][];
		
		for(int i=0; i<G.length;i++){
			int count = 0;
			for(int j=0; j<G[0].length;j++)
				if(G[i][j]==1) count++;
			jag[i] = new int[count+1];
			jag[i][0] = count;
		}

		for(int i=0; i<G.length; i++){
			int k=1;
			for(int j=0; j<G[0].length; j++){
				if(G[i][j]==1) {
					jag[i][k] = j;
					k++;
				}
			}
		}
		return jag;
	}
	
	public static int[][] connectV1(byte[][]G){
		int[][] jag = new int[G.length][];
		
		for(int i=0; i<G.length;i++){
			int count = 0;
			for(int j=0; j<G[0].length;j++)
				if(G[i][j]==1) count++;
			jag[i] = new int[count+1];
			jag[i][0] = count;
		}

		for(int i=0; i<G.length; i++){
			int k=1;
			for(int j=0; j<G[0].length; j++){
				if(G[i][j]==1) {
					jag[i][k] = j;
					k++;
				}
			}
		}
		return jag;
	}

	public static int[][] connectV2(String file, int v01)throws IOException{
		byte[][] G = adjacency(file, v01);
		int[][] jag = new int[G.length][];
		
		for(int i=0; i<G.length;i++){
			int count = 0;
			for(int j=0; j<G[0].length;j++)
				if(G[i][j]==1) count++;
			jag[i] = new int[count];
		}

		for(int i=0; i<G.length; i++){
			int k=0;
			for(int j=0; j<G[0].length; j++){
				if(G[i][j]==1) {
					jag[i][k] = j;
					k++;
				}
			}
		}
		return jag;
	}
	
	public static int[][] connectV2(byte[][] G){
		int[][] jag = new int[G.length][];

		for(int i=0; i<G.length;i++){
			int count = 0;
			for(int j=0; j<G[0].length;j++)
				if(G[i][j]==1) count++;
			jag[i] = new int[count];
		}

		for(int i=0; i<G.length; i++){
			int k=0;
			for(int j=0; j<G[0].length; j++){
				if(G[i][j]==1) {
					jag[i][k] = j;
					k++;
				}
			}
		}
		return jag;
	}
	
	//----------------------------------------------------------------------------------------------------------------
	//
	// 											Moving to GraphTool class
	//
	//----------------------------------------------------------------------------------------------------------------
	
	/**
	 * 
	 * adjacency 파일을 JGraphT의 SimpleGraph로 변환하여 전달.
	 * 
	 */
	@Deprecated
	public static Graph<Integer, DefaultEdge> adj2SimpleGraph(byte[][] adj) {
		Graph<Integer, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
		
		for(int i=0, m=adj.length; i<m; i++)
			g.addVertex(i);
		
		for(int i=0, m=adj.length; i<m-1; i++) {
			for(int j=i+1, n=adj[0].length; j<n; j++) {
				if( adj[i][j] == 1)
					g.addEdge(i,j);
			}
		}
		
		return g;
	}
	
	
	/****
	 * 
	 * subgraph의 adjacency를 구하는 함수
	 * 
	 */
	@Deprecated
	public static byte[][] subAdjacency(byte[][] adj, int[] S) { // S : array of vertex of subgraph. 
		int k = S.length;
		byte[][] subadj = new byte[k][k];
				
		for(int i=0; i<k; i++) {
			int u = S[i];
			for(int j=0; j<k; j++) {
				if( adj[u][S[j]] != 0 )
					subadj[i][j] = adj[u][S[j]];
			}
		}
		
		return subadj;
	}
	
	@Deprecated
	public static byte[][] subAdjacency(byte[][] adj, ArrayList<Integer> S) { // S : array of vertex of subgraph. 
		int[] arr_S = S.stream().mapToInt(i->i).toArray();
		
		int k = arr_S.length;
		byte[][] subadj = new byte[k][k];
		
		for(int i = 0; i < k; i++) {
			int u = arr_S[i];
			for( int j = 0; j < k; j++) {
				if ( adj[u][arr_S[j]] != 0 )
					subadj[i][j] = adj[u][arr_S[j]];
			}
		}
		
		return subadj;
	}
	
	/***
	 * 
	 *	Complement adjacency matrix
	 *
	 *  - 원 그래프의 adjacency 행렬을 사용해 complement adjacency를 구한다.
	 * @return 
	 * 
	 **/
	public static byte[][] complementAdj(byte[][] adj) {
		int n = adj.length;
		byte[][] com_adj = new byte[n][n];
		
		for(int i=0; i<n; i++) {
			for(int j = 0; j<n ; j++) {
				if(i == j)
					com_adj[i][j] = 0;
				else
					com_adj[i][j] = (byte) ((adj[i][j]+1) % 2) ;
			}
		}
		
		return com_adj;
	}
	
	/****
	 * 
	 * 	Power Graph Adjacency matrix
	 *  @param adj 	: adjacency matrix of graph G
	 *  @param k	: power of adjacency matrix, adj. 
	 *  
	 *  @return
	 *  	a = G^k
	 * 
	 * */
	public static byte[][] power(byte[][] adj, int k) {
		byte[][] a = BasicTools.matrixMul(adj, adj);
		for(int i=1; i<k-1; i++)
			a = BasicTools.matrixMul(a, adj);
		
		return a;
	}
	
	public static byte[][] power_arrange(byte[][] adj, int k){
		byte[][] a = BasicTools.matrixMul(adj, adj);
		for(int i=1; i<k-1; i++)
			a = BasicTools.matrixMul(a, adj);
		
		int n = a.length;
		for(int i=0; i<n; i++) {
			for(int j=i; j<n; j++) {
				if( i == j )
					a[i][j] = a[j][i] = 0;
				else if (a[i][j] >= 1)
					a[i][j] = a[j][i] = 1;
			}
		}
		
		return a;
	}
	
	/**
	 * 
	 * k-th power of G=(V,E). 
	 * this function returns G^k = (V, E^k) where E^k = {(i,j) | degree_G(i,j) <= k, i < j}
	 * 
	 * @param G : adjacency matrix
	 * @param k : power of G
	 * @return 
	 * 
	 * @return G^k
	 * */
	public static byte[][] kth_powerG(byte[][] G, int k) {
		int n = G.length;
		
		int[][] shortestpath = GraphTools.chainlength_BFS(G);
		byte[][] G_k = new byte[n][n];
		
		for(int i=0; i<n; i++) {
			for(int j=i+1; j<n; j++) {
				if(shortestpath[i][j] <= k)
					G_k[i][j] = G_k[j][i] = 1;
			}
		}
		
		return G_k;
	}

	
	/**
	 * @deprecated
	 * getDiamter_jg 는 JGraphT를 사용해 diameter를 구한다.
	 * 
	 * @param adj : adjacency matrix of graph
	 * @return diameter of graph
	 */
	public static double getDiameter_jg(byte[][] adj) {
		Graph<Integer, DefaultEdge> g = adj2SimpleGraph(adj);
		
		return GraphMetrics.getDiameter(g);
	}
	
	
	/**
	 * @deprecated
	 * getDiamter_jg 는 JGraphT를 사용해 subgraph의 diameter를 구한다.
	 * 
	 * @param adj : adjacency matrix of graph G
	 * @param S : ArrayLis of vertex of sub-graph of G
	 * 
	 * @return diameter of subgraph
	 */
	public static double getDiameter_jg_sub(byte[][] adj, ArrayList<Integer> S) {
		byte[][] subadj = subAdjacency(adj, S);
		Graph<Integer, DefaultEdge> g = adj2SimpleGraph(subadj);
		
		return GraphMetrics.getDiameter(g);
	}
	
	@Deprecated
	public static double getDiameter_jg_sub(byte[][] adj, int[] S) {
		byte[][] subadj = subAdjacency(adj, S);
		Graph<Integer, DefaultEdge> g = adj2SimpleGraph(subadj);
		
		return GraphMetrics.getDiameter(g);
	}
	
	
	/*
	 * 
	 * getDiameter 는  JgraphT의 getDiameter가 너무 느려서 대체하기위해 생성.
	 * 
	 * Dijkstra's algorithm으로 한개 정점의 shortest path를 구하고 
	 * 
	 **/
	@Deprecated
	public static void getDiameter(byte[][] adj) {
		
	}
	
	
	/**
	 * JGraphT의 DijkstraShortestPath를 사용해서 
	 * 모든 정점쌍에 대한 shortest path를 2차원 배열에 저장해 return 한다.
	 * 
	 * @param adj
	 * @return
	 */
	@Deprecated
	public static int[][] shortestChainLength(byte[][] adj) {
		int n=adj.length;
		Graph<Integer, DefaultEdge> g = adj2SimpleGraph(adj);
		DijkstraShortestPath<Integer, DefaultEdge> sp = new DijkstraShortestPath<>(g);
		
		int[][] arr = new int[n][n];
		for(int i=0; i<n; i++) {
			for(int j=i+1; j<n; j++) {
				 int s = (int) sp.getPathWeight(i, j);
				 arr[i][j] = arr[j][i] = s;
			}
		}
		
		return arr;
	}

	/**
	 * 
	 * isHamiltonianPath는 
	 * @param adj : 그래프의 adjacency matrix  
	 * @param s : 정점 순서 array s 
	 * 
	 * adj에서 s가 hamiltonian path인지 검사.
	 * 
	 */
	public static boolean isHamiltonianPath(byte[][] adj, int[] s) {
		if( s.length != adj.length) {
			throw new IllegalArgumentException( 
					"[!] Wrong!! a number of vetex between adj and s is diffenrent \n # of vertex of adj = "+adj.length+"\n # of vertex of s = "+s.length );
		}
		
		int n = adj.length;
		
		for(int i=0; i < n-1; i++) {
			if( adj[s[i]][s[i+1]] == 0 )
				return false;
		}
		
		return true;
	}
	
	
	/**
	 *	Graph의 특징 추출하는 함수들
	 *
	 *		- getDegreeVector : 각 정점의 degree 값을 저장하는 int 배열을 구한다.
	 *		- numEdge : 본 method 는 주어진 그래프의 간선 개수를 count 한 후 이를 int type 으로 리턴한다. 
	 *		- containsVE : 그래프의 정점과 간선 수를 int array로 return.(para : JgraphT의 그래프)
	 * 
	 * */
	@Deprecated
	public static int[] getDegreeVector(byte[][] adj) {
		int n = adj.length;
		int[] deg = new int[n];
		
		for(int i=0; i<n; i++)
			deg[i] = BasicTools.sum_element(adj[i]);
		
		return deg;
	}
	
	public static int numEdge(byte[][] G) {
		int n = G.length;
		int cnt = 0;

		for (int i = 0; i < n; i++)
			for (int j = i + 1; j < n; j++)
				if (G[i][j] > 0)
					cnt++;

		return cnt;
	}
	
	@Deprecated
	public static int[] containsVE(Graph<Integer, DefaultEdge> g) {
		Set<Integer> vs = g.vertexSet();
		Set<DefaultEdge> es = g.edgeSet();
		
		int[] sizes = {vs.size(), es.size()};
		return sizes;
	}
	
	
	public static int[][] edgeSet(byte[][] G) {
		int n = G.length;
		int e = numEdge(G);
		int[][] E = new int[2][e];

		int cnt = 0;
		for (int i = 0; i < n-1; i++) {
			for (int j = i+1; j < n; j++) {
				if (G[i][j] == 1) {
					E[0][cnt] = i;
					E[1][cnt++] = j;
				}
			}
		}
		return E;
	}

	// graph G에 몇개의 요소가 존재하는가.
	// 교수님 코드
	public static int numComponent(byte[][] G) {
		int n = G.length;
		ConnectedComponent cc = new ConnectedComponent(G);
		return cc.getNumOfComponent();
	}
	
	// 알고리즘 찾아서 구현
	public static int numComponent2(byte[][] adj) {
		int n = adj.length;
		boolean[] visited = new boolean[n];
		for(int i=0; i<n; i++)
			visited[i] = false;
		
		int component_num = 0;
		for(int i=0; i<n; i++) {
			if(visited[i] == false) {
				DFS(adj, i, visited);
				component_num += 1;
			}
		}
		
		return component_num;
	}
	
	static void DFS(byte[][] adj, int v, boolean[] visited) {
		visited[v] = true;

		for(int u=0; u < adj[v].length; u++) {
			if( adj[v][u] == 1 && visited[u] == false )
				DFS(adj, u, visited);
		}
	}
	
}