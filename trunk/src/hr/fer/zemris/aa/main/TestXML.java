package hr.fer.zemris.aa.main;

import hr.fer.zemris.aa.features.Article;
import hr.fer.zemris.aa.xml.XMLMiner;

import java.util.List;
import java.util.Set;

public class TestXML {

	public static void main(String[] args) {
		
		if (args.length != 1) {
			System.err.println("arg[0] = put do xml datoteke!");
			System.exit(99);
		}
		
		XMLMiner littleChineseGuy = new XMLMiner(args[0]);
				
		//printAllAuthors(littleChineseGuy);
		//printArticlesByAuthor("Živko Kustić", littleChineseGuy);
		testNewest(littleChineseGuy);
		
	}

	private static void testNewest(XMLMiner littleChineseGuy) {
		
		List<Article> svi = littleChineseGuy.getArticlesByAuthor("Živko Kustić");
		
		//Prvi parametar odreduje koja metoda se poziva (int ili double
		List<Article> najnoviji = littleChineseGuy.getNewest(0.01, svi);
		
		for (Article article : najnoviji) {
			System.out.println(article.getDateString());
		}
		
		System.out.println("\nsvi:");
		for (Article article : svi) {
			System.out.print(article.getDateString()+ ", ");
		}
		
	}

	private static void printAllAuthors(XMLMiner littleChineseGuy) {
		Set<String> authors = littleChineseGuy.getAuthors();
		
		System.out.println("------ AUTORI -------");
		
		int i = 0;
		for (String a : authors) {
			i++;
			System.out.println(i + ". " + a);
		}
		
	}

	private static void printArticlesByAuthor(String author, XMLMiner littleChineseGuy) {
		
		List<Article> lista = littleChineseGuy.getArticlesByAuthor(author);
		
		for (Article a : lista){
			System.out.println();
			System.out.println(a.getAuthor() + ": \"" + a.getTitle() + "\" [" + a.getDateString() + "]");
			System.out.println("---------------------------------------------");
			System.out.println(a.getText());
			System.out.println();
		}
		
	}

}
