import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFFormat;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;

public class EliminateRedundancy {
	
	// DATA INPUT
	public static String DATA = "../example/data.nt";
	public static String DATA_OUTPUT = "../example/output/output.nt";
	public static String DATA_REDUNDANT = "../example/output/redundant-triples.nt";
	public static RDFFormat DATA_FORMAT = RDFFormat.NTRIPLES;

	// ONTOLOGY
	public static String ONTOLOGY = "../ontologies/musicontology.rdf"; 
	public static RDFFormat ONTOLOGY_FORMAT = RDFFormat.RDFXML;

	public static void main(String[] args) {
		try {
			getReducedDataset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void getReducedDataset() throws IOException 
	{
		// read data
		Model aModel = ModelFactory.createDefaultModel();
		aModel.read(FileManager.get().open(DATA), null, DATA_FORMAT.getLang().getName());
		
		// read ontology
		Model tModel = getFullyMaterializedOntology();
	
		// merge abox and tbox
		Model kb = aModel.union(tModel);
		
		// eventually redundant and reduced models
		Model redundantModel = ModelFactory.createDefaultModel();
		Model reducedModel = ModelFactory.createDefaultModel();
		
		// construct redundant graph
		ArrayList<String> queries = Query.getRedundancyQueries();
		Iterator<String> it = queries.iterator();
		while (it.hasNext()) {
			String queryString = it.next();
			Query query = QueryFactory.create(queryString) ;
			QueryExecution qexec = QueryExecutionFactory.create(query, kb) ;
			redundantModel = redundantModel.union(qexec.execConstruct());
			qexec.close();
		}
		
		reducedModel = aModel.difference(redundantModel);
		FileWriter outRedundant = new FileWriter(DATA_REDUNDANT);
		FileWriter outReduced = new FileWriter(DATA_OUTPUT);
		try {
			redundantModel.write(outRedundant, DATA_FORMAT.toString());
			reducedModel.write(outReduced, DATA_FORMAT.toString());
		}
		finally {
		   try {
			   outRedundant.close();
			   outReduced.close();
		   }
		   catch (IOException closeException) {
		   }
		}
		
		System.out.println("Original size in total : " + aModel.size());
		System.out.println("Removed triples in total : " + (aModel.size() - reducedModel.size()));
		System.out.println("Percentage of the original size : " + (((reducedModel.size() / aModel.size())  * 100 )) + "%");
	}

	public static Model getFullyMaterializedOntology()
			throws IOException {

		// read ontology from file
		Model tModel = ModelFactory.createDefaultModel();
		tModel.read(FileManager.get().open(ONTOLOGY), null);

		// create reasoner and reason
		com.hp.hpl.jena.reasoner.Reasoner reasoner = ReasonerRegistry.getRDFSReasoner();

		reasoner.setParameter(ReasonerVocabulary.PROPsetRDFSLevel, ReasonerVocabulary.RDFS_SIMPLE);
		InfModel tInfModel = ModelFactory.createInfModel(reasoner, tModel);

		return tInfModel;
	}
}
