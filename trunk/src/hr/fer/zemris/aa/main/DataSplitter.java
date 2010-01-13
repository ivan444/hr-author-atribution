package hr.fer.zemris.aa.main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import hr.fer.zemris.aa.features.Article;
import hr.fer.zemris.aa.xml.XMLMiner;

/**
 * Dijeljenje skupa tekstova na skup za učenje i skup za testiranje.
 * Skup se fizički dijeli, tj. dijeljenje rezultira dvjema novim datotekama
 * od kojih jedna sadrži tekstove za učenje, druga za testiranje. Inicijalno
 * je postavljeno da se skupovi dijele s omjerom ~80:20. Svaki autor će imati
 * bar jedan svoj članak u skupu za testiranje.
 * 
 * Ako trebate dijeljenje unutar memorije, koristite metode: {@code split} metodu
 * razreda {@code XMLMiner}.
 * 
 * @author Ivan Krišto
 *
 */
public class DataSplitter {
	/** 
	 * Omjer podjele skupa na manje skupove, tj. omjer veličina
	 * manjih skupova.
	 */
	private static final double OMJER = 20.0/80.0;
	
	/**
	 * @param args Putanja do skupa tekstova.
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Neispravni parametri! <putanja-do-skupa-tekstova>");
			System.exit(-1);
		}
		
		String dat = args[0];
		
		XMLMiner miner = new XMLMiner(dat);
		
		System.out.println("Dohvaćam autore.");
		Set<String> authors = miner.getAuthors();
		List<Article> trainArticles = new LinkedList<Article>();
		List<Article> testArticles = new LinkedList<Article>();
		
		Comparator<Article> articleCompDate = new Comparator<Article>() {
			@Override
			public int compare(Article o1, Article o2) {
				return o2.getDate().compareTo(o1.getDate());
			}
		};
		
		System.out.println("Dohvaćam tekstove i dijelim arhivu.");
		for (String a : authors) {
			List<Article> authorArticles = miner.getArticlesByAuthor(a, true);
			
			Collections.sort(authorArticles, articleCompDate);
			
			int size = authorArticles.size();
			if (size == 1) {
				// Preskacemo. Nema smisla raditi s ovako malo uzoraka
				continue;
			}
			
			int testNum = (int) Math.round(size*OMJER);
			if (testNum == 0) testNum = 1;
			testArticles.addAll(authorArticles.subList(0, testNum));
			trainArticles.addAll(authorArticles.subList(testNum, size));
		}
		
		Comparator<Article> articleComp = new Comparator<Article>() {
			@Override
			public int compare(Article o1, Article o2) {
				int first = Integer.parseInt(o1.getName().substring(16));
				int second = Integer.parseInt(o2.getName().substring(16));
				
				return second-first;
			}
		};
		
		System.out.println("Sortiram nove skupove (zapravo liste).");
		Collections.sort(testArticles, articleComp);
		Collections.sort(trainArticles, articleComp);
		
		System.out.println("Zapisujem skup za testiranje.");
		writeXML(testArticles, dat.substring(0, dat.length()-4)+".test.xml");
		
		System.out.println("Zapisujem skup za učenje.");
		writeXML(trainArticles, dat.substring(0, dat.length()-4)+".train.xml");
		System.out.println("Gotovo!");
	}
	
	public static void writeXML(List<Article> articles, String outPath) {
		String header = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
		String start = "<documentSet name=\"jutarnji-kolumne-arhiva-2009-11-14\" type=\"\" description=\"Arhiva kolumni Jutarnjeg lista do 2009-11-14\" xmlns=\"http://ktlab.fer.hr\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://ktlab.fer.hr http://ktlab.fer.hr/download/documentSet.xsd\">\n";
		String end = "</documentSet>\n";
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(outPath));
			
			writer.write(header);
			writer.write(start);
			for (Article art : articles) {
				writer.write(art.toString());
			}
			writer.write(end);
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
