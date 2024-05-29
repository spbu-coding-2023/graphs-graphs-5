## About the project

This app allows you to analyze graphs (_directed_, _undirected,_ _weighted_ and _unweighted_), using various algorithms. To load graphs, you can use SQLite and Neo4j databases.

## Getting started
Clone the repo to your computer:
```
  git clone git@github.com:spbu-coding-2023/graphs-graphs-5.git
  ```
Run the following command to install the dependencies:

```
  ./gradlew build
  ```
Use the following command to run the application: 
```
  ./gradlew run
  ```
You are all set!

Now, if you want, you can run tests:
```
  ./gradlew test
  ```
For integration test that uses Neo4j you will need Docker. In terminal run this command:
```
  docker run \
    --name neo4j \
    -e NEO4J_AUTH=neo4j/testtesttest\
    -p 7475:7474 -p 7689:7687 \
    -d neo4j:latest
  ```
If this port is not free, you can adjust it and uri specifically for your computer.
   
## How to load and store graphs

You can use SQLite or Neo4j to load and store graphs. You will need: 

for `SQLite` - path to database  

for `Neo4j` - URI, username and password. You can also store results of 3 main algorithms 
<p float'"left>  
  <img width="450" src=app/src/main/resources/SqliteDialogWindow.png align="center">
  <img width="450" src=app/src/main/resources/neo4jDialogWindow.png align="center">
</p>

## How to use for analyzing graphs
There 3 main algorithms that are avaliable for all graphs: Clustering, highlighting Key Vertices, and Graph Layout (set as default when you load the app). 
For directed graph you can you use: 
* `Cycles` finds cycle that chosen vertex is in
* `Strong components` finds strongly connected components usin Kosaraju's algorithm
* `Min path(Dijkstra)` and `Min path(Ford-Bellman)` find the shortest paths between two chosen vertices

<img width="300" src=app/src/main/resources/menuDirected.png align="center">

For undirected graph you can use: 
* `Bridges`
* `Min Spannig Tree`
* `Min path(Dijkstra)` finds the shortest paths between two chosen vertices  

<img width="300" src=app/src/main/resources/menuUndirected.png align="center">

## Features
* Zooming in and out of graph surface  
* Dragging graph's vertices
* Showing labels: vertices' indices and edges' weights
* Applying theme (Classic Vintage or Barbie Coded)  
* Running algorithms
<p float'"left>  
  <img width="600" src=app/src/main/resources/Clustering.png align="center">
  <img width="600" src=app/src/main/resources/KeyVerticesAlgo.png align="center">
</p>


Have fun!
## Developers and contacts
* [p1onerka](https://github.com/p1onerka) (tg @p10nerka)  
* [sofyak0zyreva](https://github.com/sofyak0zyreva) (tg @soffque)  
* [shvorobsofia](https://github.com/shvorobsofia) (tg @fshv23)

## License
The product is distributed under GNU General Public License v3.0. Check LICENSE for more information

## Third-Party Libraries
This project uses the following third-party libraries:

[Gephi Toolkit](https://github.com/gephi/gephi) licensed under the GNU General Public License v3.0. The source code for the Gephi Toolkit can be found at [Gephi Toolkit Source](https://github.com/gephi/gephi)
