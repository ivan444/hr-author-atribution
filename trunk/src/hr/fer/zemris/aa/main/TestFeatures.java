package hr.fer.zemris.aa.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import hr.fer.zemris.aa.features.Article;
import hr.fer.zemris.aa.features.FeatureGenerator;
import hr.fer.zemris.aa.xml.XMLMiner;

public class TestFeatures {

	public static void main(String[] args) {

		if (args.length != 1) {
			System.err.println("arg[0] = put do XML datoteke!");
			System.exit(99);
		}

		List<String> fWords = getFunctionWords();
		FeatureGenerator fGen = new FeatureGenerator(fWords);
		XMLMiner littleChineseGuy = new XMLMiner(args[0]);
		List<Article> lista = littleChineseGuy.getArticlesByAuthor("Nino Đula");

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

	// FIXME: ovo privremeno ovako, inace bolje napraviti dodavanje funkcijskih
	// rijeci
	private static List<String> getFunctionWords() {

		List<String> fWords = new ArrayList<String>();

		try {

			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					"config/fwords.txt"));

			String line = null;

			while ((line = bufferedReader.readLine()) != null) {
				fWords.add(line.toLowerCase());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return fWords;
	}

	private static void ispisVektora(int[] x) {
		System.out.print("x = [ ");
		for (int xi : x) {
			System.out.print(xi + " ");
		}
		System.out.println("]");

	}

}
