# Genetic Programming

* evolutionary algorithm

### Simple Example

피보나치 수열을 사칙연산을 통해 표현하기    
* operation = +, *, -, /
* constant = 2, 3, 4, 5

### class 
1. Node class
  두 개 자식 노드를 갖는 노드로 operation, constant, variable 중 한 요소를 포함한다.
     
2. Tree class
  최대 높이 MAX_H 를 갖는 트리로 Full tree 또는 Grow tree 두 가지 종류로 생성될 수 있다. 
  이를 통해 생성되는 트리의 다양성을 보장한다.
    * Full tree : 반드시 MAX_H 를 갖는 트리이고 leaf node 를 제외한 모든 node는 반드시 2개 자식을 갖는다.
    * Grow tree : 최대 MAX_H 를 갖는 트리이며 full tree와 다르게 한 개 자식을 갖는 node가 나타날 수 있다.
    
3. SimpleGP class
  GP가 실행되는 main 클래스. 트리로 구성된 population을 생성하고 유전 연산자인 selection, crossover, mutation 등을 수행한다.
   
4. EvalThS class
  Evaluation을 병렬 프로그래밍으로 수행하기 위한 클래스이다.
    
5. Symbol class
  helper class 로 node 의 key 가 되는 symbol들의 정보를 저장한다.
    
6. Fibb class
  피보나치 수열을 계산하는 recursive programm 
  
