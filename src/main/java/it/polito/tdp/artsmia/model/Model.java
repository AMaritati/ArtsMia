package it.polito.tdp.artsmia.model;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import it.polito.tdp.artsmia.db.ArtsmiaDAO;

public class Model {
	private SimpleWeightedGraph<ArtObject, DefaultWeightedEdge> grafo;
	private Map<Integer, ArtObject> idMap;
	private ArtsmiaDAO dao;
	
	public Model() {
		idMap = new HashMap<Integer,ArtObject>();
		dao = new ArtsmiaDAO();
		this.dao.loadArtObjects(idMap);
	}
	
	public void creaGrafo() {
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(this.grafo, idMap.values());
		
		

		List<Adiacenza> coppie = dao.getAdiacenze(idMap);
		
		for (Adiacenza c : coppie) {
			Graphs.addEdge(this.grafo, c.getA1(), c.getA2(), c.getPeso());
		}
		
		

	}
	

	public List<ArtObject> getReachableCountries(ArtObject selectedCountry) {

		if (!grafo.vertexSet().contains(selectedCountry)) {
			throw new RuntimeException("Selected Country not in graph");
		}

		List<ArtObject> reachableCountries = this.displayAllNeighboursIterative(selectedCountry);
		System.out.println("Reachable countries: " + reachableCountries.size());
		reachableCountries = this.displayAllNeighboursJGraphT(selectedCountry);
		System.out.println("Reachable countries: " + reachableCountries.size());
		reachableCountries = this.displayAllNeighboursRecursive(selectedCountry);
		System.out.println("Reachable countries: " + reachableCountries.size());

		return reachableCountries;
	}
	
	/*
	 * VERSIONE ITERATIVA
	 */
	private List<ArtObject> displayAllNeighboursIterative(ArtObject selectedCountry) {

		// Creo due liste: quella dei noti visitati ..
		List<ArtObject> visited = new LinkedList<ArtObject>();

		// .. e quella dei nodi da visitare
		List<ArtObject> toBeVisited = new LinkedList<ArtObject>();

		// Aggiungo alla lista dei vertici visitati il nodo di partenza.
		visited.add(selectedCountry);

		// Aggiungo ai vertici da visitare tutti i vertici collegati a quello inserito
		toBeVisited.addAll(Graphs.neighborListOf(grafo, selectedCountry));

		while (!toBeVisited.isEmpty()) {

			// Rimuovi il vertice in testa alla coda
			ArtObject temp = toBeVisited.remove(0);

			// Aggiungi il nodo alla lista di quelli visitati
			visited.add(temp);

			// Ottieni tutti i vicini di un nodo
			List<ArtObject> listaDeiVicini = Graphs.neighborListOf(grafo, temp);

			// Rimuovi da questa lista tutti quelli che hai già visitato..
			listaDeiVicini.removeAll(visited);

			// .. e quelli che sai già che devi visitare.
			listaDeiVicini.removeAll(toBeVisited);

			// Aggiungi i rimanenenti alla coda di quelli che devi visitare.
			toBeVisited.addAll(listaDeiVicini);
		}

		// Ritorna la lista di tutti i nodi raggiungibili
		return visited;
	}

	/*
	 * VERSIONE LIBRERIA JGRAPHT
	 */
	private List<ArtObject> displayAllNeighboursJGraphT(ArtObject selectedCountry) {

		List<ArtObject> visited = new LinkedList<ArtObject>();

		// Versione 1 : utilizzo un BreadthFirstIterator
//		GraphIterator<Country, DefaultEdge> bfv = new BreadthFirstIterator<Country, DefaultEdge>(graph,
//				selectedCountry);
//		while (bfv.hasNext()) {
//			visited.add(bfv.next());
//		}

		// Versione 2 : utilizzo un DepthFirstIterator
		GraphIterator<ArtObject, DefaultWeightedEdge> dfv = new DepthFirstIterator<ArtObject, DefaultWeightedEdge>(grafo, selectedCountry);
		while (dfv.hasNext()) {
			visited.add(dfv.next());
		}

		return visited;
	}

	/*
	 * VERSIONE RICORSIVA
	 */
	private List<ArtObject> displayAllNeighboursRecursive(ArtObject selectedCountry) {

		List<ArtObject> visited = new LinkedList<ArtObject>();
		recursiveVisit(selectedCountry, visited);
		return visited;
	}

	private void recursiveVisit(ArtObject n, List<ArtObject> visited) {
		// Do always
		visited.add(n);

		// cycle
		for (ArtObject c : Graphs.neighborListOf(grafo, n)) {	
			// filter
			if (!visited.contains(c))
				recursiveVisit(c, visited);
				// DO NOT REMOVE!! (no backtrack)
		}
	}

	
	public int getNVertici() {
		return this.grafo.vertexSet().size();
	}

	public Map<Integer, ArtObject> getIdMap() {
		return idMap;
	}

	public int getNArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public static void main(String args[]) {
		Model m = new Model();
		m.creaGrafo();
		
		System.out.println(m.idMap.get(11));
		m.getReachableCountries(m.idMap.get(11));
		
		
		
		
	}
}
