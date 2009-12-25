package hr.fer.zemris.aa.main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import hr.fer.zemris.aa.features.Article;
import hr.fer.zemris.aa.xml.XMLMiner;

/**
 * Ispis statistika arhive.
 * 
 * @author Ivan Krišto
 *
 */
public class ArhiveStatistics {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String path = "podatci-skripta/jutarnji-kolumne-arhiva-2009-11-14.xml";
		int authorsNum = 0;
		int articleNum = 0;
		long wordNumSum = 0;
		
		XMLMiner xml = new XMLMiner(path);
		
		Set<String> authors = xml.getAuthors();
		authorsNum = authors.size();
		
		System.out.println("Ukupno autora: " + authorsNum);
		System.out.println();
		
		Map<String, Long> authorArticleLenSum = new HashMap<String, Long>();
		Map<String, Integer> authorArticleNum = new HashMap<String, Integer>();
		
		Pattern wordSplit = Pattern.compile("[\\w]+");
		int index = 0;
		System.out.println("Članaka po autoru:");
		for (String a : authors) {
			List<Article> articles = xml.getArticlesByAuthor(a);
			index++;
			System.out.println(index + "\t" + articles.size());
			articleNum += articles.size();
			authorArticleNum.put(a, articles.size());
			
			long authorWordSum = 0;
			for (Article art : articles) {
				int artLen = wordSplit.split(art.getText().trim()).length;
				authorWordSum += artLen;
				wordNumSum += artLen;
			}
			authorArticleLenSum.put(a, authorWordSum);
		}
		System.out.println();
		
		System.out.println("Ukupno članaka: " + articleNum);
		System.out.println("Ukupno riječi: " + wordNumSum);
		System.out.println("Riječi po autoru:");
		index = 0;
		for (String a : authors) {
			index++;
			System.out.println(index + "\t" + authorArticleLenSum.get(a));
		}
		System.out.println();
		
		index = 0;
		System.out.println("Prosječna duljina članka po autoru:");
		for (String a : authors) {
			index++;
			float avg = authorArticleLenSum.get(a)*1.f/authorArticleNum.get(a);
			System.out.println(index + "\t" + avg);
		}

	}

}
