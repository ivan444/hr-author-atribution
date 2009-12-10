package hr.fer.zemris.aa.main;

import java.util.List;
import java.util.Set;

import hr.fer.zemris.aa.features.Article;
import hr.fer.zemris.aa.features.FeatureClass;
import hr.fer.zemris.aa.features.FeatureGenerator;
import hr.fer.zemris.aa.features.FeatureVector;
import hr.fer.zemris.aa.xml.XMLMiner;

public class TestFeatures {

	public static void main(String[] args) {

		if (args.length != 1) {
			System.err.println("arg[0] = put do XML datoteke!");
			System.exit(99);
		}

		XMLMiner littleChineseGuy = new XMLMiner(args[0]);
		
		
		//zakomentirati jednu od metoda za bolju preglednost:
		
		getVectorsOfAuthor("Nino Đula", littleChineseGuy);
		createAllClasses(littleChineseGuy);

	}

	private static void createAllClasses(XMLMiner littleChineseGuy) {
		
		FeatureGenerator fGen = new FeatureGenerator();
		
		Set<FeatureClass> allClasses = fGen.generateFeatureVectors(littleChineseGuy);
		
		System.out.println("Stvoreno " + allClasses.size() + " klasa:");
		
		int i = 0;
		int j = 0;
		
		for (FeatureClass vektori : allClasses) {
			
			System.out.print("Omega(" + i++ + ") = { ");
			
			for (int k = 0; k < vektori.size(); k++) {
				System.out.print("x(" + j++ + "), ");
			}
			
			System.out.println("}");
		}
		
	}

	private static void getVectorsOfAuthor(String a, XMLMiner littleChineseGuy) {
		
		FeatureGenerator fGen = new FeatureGenerator();
		System.out.println(fGen.vectorRepresentation());
		List<Article> lista = littleChineseGuy.getArticlesByAuthor(a);

		int i = 0;
		for (Article article : lista) {
			
			FeatureVector xi = fGen.vectorize(article);
			System.out.println("\n(" + (i + 1) + ") " + xi.getAuthor()
					+ ": \"" + xi.getTitle() + "\"");
			System.out.println(xi.toString());
			i++;
			
		}
	}

}
