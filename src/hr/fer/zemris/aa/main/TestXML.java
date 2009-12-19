package hr.fer.zemris.aa.main;

import hr.fer.zemris.aa.features.Article;
import hr.fer.zemris.aa.xml.XMLMiner;

import java.util.List;
import java.util.Set;

/**
 * Pomocna klasa koja sluzi za testiranje čitanja XML-a i pretprocesirajne podataka
 * @author igorbel
 */
public class TestXML {

	public static void main(String[] args) {
		
		if (args.length != 1) {
			System.err.println("arg[0] = put do xml datoteke!");
			System.exit(99);
		}
		
		double mb = 1024*1024.0;
		Runtime runtime = Runtime.getRuntime();
		double memBefore = (runtime.totalMemory() - runtime.freeMemory()) / mb;
		long startTime = System.currentTimeMillis();
		
		XMLMiner littleChineseGuy = new XMLMiner(args[0]);

		
		//printAllAuthors(littleChineseGuy);
		//printArticlesByAuthor("Živko Kustić", littleChineseGuy);
		//testNewest(littleChineseGuy);
		testSplit(littleChineseGuy);
		
		
		long endTime = System.currentTimeMillis();
		double memAfter = (runtime.totalMemory() - runtime.freeMemory()) / mb;
		
		System.out.println("\n\n----------------------------------");
		System.out.println("Execution time: " + ((endTime - startTime)/1000.0) + " sec");
		System.out.println("Memory used: " + (memAfter - memBefore) + " MB\n");
		
	}

	private static void testSplit(XMLMiner littleChineseGuy) {
		
		List<Article> svi = littleChineseGuy.getArticlesByAuthor("Živko Kustić");
		
		//Prvi parametar odreduje koja metoda se poziva (int ili double)
		List<Article>[] splitano = littleChineseGuy.split(0.01, svi);
		
		List<Article> stariji = splitano[0];
		List<Article> najnoviji = splitano[1];
		
		for (Article article : najnoviji) {
			System.out.println(article.getDateString());
		}
		
		System.out.println("=====BOUNDRY======");
		
		for (Article article : stariji) {
			System.out.println(article.getDateString());
		}
		
		System.out.println("\nsvi datumi:");
		for (Article article : svi) {
			System.out.print(article.getDateString()+ ", ");
		}
		
	}

	private static void testNewest(XMLMiner littleChineseGuy) {
		
		List<Article> svi = littleChineseGuy.getArticlesByAuthor("Živko Kustić");
		
		//Prvi parametar odreduje koja metoda se poziva (int ili double
		List<Article> najnoviji = littleChineseGuy.getNewest(4, svi);
		
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
