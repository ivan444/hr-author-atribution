package hr.fer.zemris.aa.tests;

import java.util.List;

import hr.fer.zemris.aa.features.Article;
import hr.fer.zemris.aa.features.TextStatistics;
import hr.fer.zemris.aa.xml.XMLMiner;

public class TestArticleStat {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("arg[0] = put do xml datoteke!");
			System.exit(99);
		}
		
		XMLMiner littleChineseGuy = new XMLMiner(args[0]);
		
		List<Article> svi = littleChineseGuy.getArticlesByAuthor("Živko Kustić");
		
		TextStatistics as1 = new TextStatistics(svi.get(1).getText());
		System.out.println(as1.toString());
		
		TextStatistics as2 = new TextStatistics(svi.get(2).getText());
		System.out.println(as2.toString());
	}

}
