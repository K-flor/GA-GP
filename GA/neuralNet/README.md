	neural network for XOR problem
	
	- two inputs
	- one Hidden layer & 2 nodes
	- one output
	
	* weights & bias
		
		- Input & Hidden
		
				(	| w1 w2 b1 | | x1 | )   | h1 |
		sigmoid	(	| w3 w4	b2 | | x2 |	) = | h2 |
				(				 |  1 | )
		
		
		- Hidden & Output
		
				(				 | h1 | )
		sigmoid (	| w5 w6 b3 | | h2 | ) = | o1 |
				(				 |  1 | )
		
	* chromosome
		[w1 w2 b1 w3 w4 b2 w5 w6 b3]
