package rdfs.redundancy;

import java.util.ArrayList;

public class Query {
	final static String PREFICES = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
			+ "prefix owl: <http://www.w3.org/2002/07/owl#> "
			+ "prefix xsd: <http://www.w3.org/2001/XMLSchema#> "
			+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>";
	final static String DOMAIN_QUERY = "CONSTRUCT {?s ?p ?o} WHERE { ?p1 rdfs:domain ?o . ?s ?p1 ?x . ?s ?p ?o . FILTER (?p = rdf:type) . }";
	final static String RANGE_QUERY = "CONSTRUCT {?s ?p ?o} WHERE { ?p1 rdfs:range ?o . ?x ?p1 ?s . ?s ?p ?o . FILTER (?p = rdf:type) . }";
	final static String SUBCLASSOFF_QUERY = "CONSTRUCT {?s ?p ?o} WHERE { ?x rdfs:subClassOf ?o . ?s ?p ?x . ?s ?p ?o . FILTER (?p = rdf:type) . FILTER (?x != ?o) . }";
	final static String SUBPROPERTYOF_QUERY = "CONSTRUCT {?s ?p ?o} WHERE {  ?p1 rdfs:subPropertyOf ?p . ?s ?p1 ?o . ?s ?p ?o . FILTER (?p1 != ?p) . }";
	
	public static ArrayList<String> getRedundancyQueries() {
		
		ArrayList<String> queries = new ArrayList<String>() {{
			add(Query.PREFICES + Query.SUBCLASSOFF_QUERY);
			add(Query.PREFICES + Query.DOMAIN_QUERY);
			add(Query.PREFICES + Query.RANGE_QUERY);
			add(Query.PREFICES + Query.SUBPROPERTYOF_QUERY);
		}};
		
		return queries;
	}
}
