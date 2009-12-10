package hr.fer.zemris.aa.features;

import hr.fer.zemris.aa.xml.XMLMiner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FeatureGenerator {
	
	private final List<String> fWords;
	private Map<String, Integer> count;
	
	public FeatureGenerator(){
		this.fWords = getFunctionWords();
		this.count = new HashMap<String, Integer>();
		for (String fWord : this.fWords) {
			count.put(fWord, new Integer(0));
		}
	}
	
	public Set<FeatureClass> generateFeatureVectors(XMLMiner littleChineseGuy){
		
		Set<String> authors = littleChineseGuy.getAuthors();
		Set<FeatureClass> allClasses = new HashSet<FeatureClass>();
		
		for (String author : authors) {
			
			List<Article> lista = littleChineseGuy.getArticlesByAuthor(author);
			FeatureClass omega = new FeatureClass(lista.size());
			
			for (Article article : lista) {
				FeatureVector xi = this.vectorize(article);
				omega.add(xi);
			}
			
			allClasses.add(omega);
		}
		
		return allClasses;
		
	}
	
	/**
	 * first hand solution, don't kill me, will optimize later :)
	 */
	public FeatureVector vectorize(Article article){
		
		String plainText = article.getText();
		FeatureVector x = new FeatureVector(fWords.size());
		x.describe(article.getAuthor(), article.getTitle());
		
		//reset
		for (String fWord : this.fWords) {
			count.put(fWord, new Integer(0));
		}
		
		String[] words = plainText.split(" ");
		
		for (String word : words) {
			word = cleanup(word);
			
			if (this.fWords.contains(word)){
				
				int freq = count.get(word);
				freq++;
				count.put(word, freq);
				
			}
		}
		
		int i = 0;
		for (String key : count.keySet()) {
			//System.out.println(key + " = " + count.get(key));
			x.put(i,count.get(key));
			i++;
		}
		return x;
	}
	
	
	public String vectorRepresentation(){
		StringBuilder sb = new StringBuilder();
		
		sb.append("x = [ ");
		for (String key : this.count.keySet()) {
			sb.append(key + " ");
		}
		sb.append("]");
		
		return sb.toString();
	}

	//TODO: ovo je temp solution, treba bolje
	private String cleanup(String word) {
		String clean = word.toLowerCase();
		if (clean.endsWith(".")
				|| clean.endsWith(",")
				|| clean.endsWith("\"")
				|| clean.endsWith(")")
				|| clean.endsWith("!")
				|| clean.endsWith("?")
				)  {
			
			clean = clean.substring(0, word.length()-1);
		}
		
		if (clean.startsWith("(")){
			clean = word.substring(1, word.length());
		}
		
		return clean;
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

}
