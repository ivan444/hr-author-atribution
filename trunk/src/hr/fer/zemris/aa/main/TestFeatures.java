package hr.fer.zemris.aa.main;

import java.util.List;
import java.util.Set;

import hr.fer.zemris.aa.features.Article;
import hr.fer.zemris.aa.features.FeatureGenerator;
import hr.fer.zemris.aa.xml.XMLMiner;

public class TestFeatures {

	public static void main(String[] args) {

		if (args.length != 1) {
			System.err.println("arg[0] = put do XML datoteke!");
			System.exit(99);
		}

		XMLMiner littleChineseGuy = new XMLMiner(args[0]);
		
		//getVectorsOfAuthor("Nino Đula", littleChineseGuy);
		
		createAllClasses(littleChineseGuy);

	}

	private static void createAllClasses(XMLMiner littleChineseGuy) {
		
		FeatureGenerator fGen = new FeatureGenerator();
		
		Set<List<int[]>> allClasses = fGen.generateFeatureVectors(littleChineseGuy);
		
		System.out.println("Stvoreno " + allClasses.size() + " klasa:");
		
		int i = 0;
		int j = 0;
		
		for (List<int[]> vektori : allClasses) {
			
			System.out.print("Omega(" + i++ + ") = { ");
			
			for (int[] x : vektori) {
				System.out.print("x(" + j++ + "), ");
				
			}
			System.out.println("}");
		}
		
	}

	private static void getVectorsOfAuthor(String a, XMLMiner littleChineseGuy) {
		
		FeatureGenerator fGen = new FeatureGenerator();
		
		List<Article> lista = littleChineseGuy.getArticlesByAuthor(a);

		int i = 0;
		for (Article article : lista) {
			System.out.println("\n(" + (i + 1) + ") " + article.getAuthor()
					+ ": \"" + article.getTitle() + "\"");

			int[] xi = fGen.vectorize(article.getText());
			ispisVektora(xi);
			i++;
			// add vektor x tu class Omega(author)
		}
		
	}

	private static void ispisVektora(int[] x) {
		System.out.print("x = [ ");
		for (int xi : x) {
			System.out.print(xi + " ");
		}
		System.out.println("]");

	}

}
