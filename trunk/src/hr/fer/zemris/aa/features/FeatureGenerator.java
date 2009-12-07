package hr.fer.zemris.aa.features;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeatureGenerator {
	
	private final List<String> fWords;
	private Map<String, Integer> count;
	
	public FeatureGenerator(List<String> fWords){
		this.fWords = fWords;
		this.count = new HashMap<String, Integer>();
	}
	
	/**
	 * first hand solution, don't kill me, will optimize later :)
	 */
	public int[] vectorize(String plainText){
		
		int[] x = new int[fWords.size()];
		
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
			x[i] = count.get(key);
			i++;
		}
		
		return x;
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

}
