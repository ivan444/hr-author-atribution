package hr.fer.zemris.aa.features;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Razred koji implementira jednostavno generiranje znacajki.
 * Znacajke koje se koriste su funkcijske rijeci podijeljene po vrsti.
 * Rezultirajuci vektor je vektor koji sadrzi frekvencije pojavljivanja odredjene vrste
 * funkcijskih rijeci.
 * @author TOMISLAV
 *
 */
public class SimpleFeatureExtractor implements IFeatureExtractor {

	Map<String, Set<String>> fWords = new LinkedHashMap<String, Set<String>>();
	
	/**
	 * Datoteka s funkcijskim rijecima. Format datoteke je sljedeci:
	 * <list>
	 *  <li> Retci s komentarima zapocinju znakom #
	 *  <li> Svaka grupa za novu vrstu rijeci mora zapoceti sa svojom oznakom predznacenom tockom
	 *  <li> Datoteka mora zavrsiti s tockom
	 * </list>
	 * 
	 * @param inputFile
	 */
	public SimpleFeatureExtractor(File inputFile) {
		
		try {
			
			BufferedReader bufferedReader = new BufferedReader(
					new FileReader(inputFile));

			
			String line = null;
			String wordType = null;
			Set<String> wordSet = null;
			

			while ((line = bufferedReader.readLine()) != null) {
				
				line = line.trim().toLowerCase();
				
				if (line.length() == 0 || line.startsWith("#"))
					continue;
				
				if (line.startsWith(".")) {
					if (wordType != null && wordSet != null)
						fWords.put(wordType, wordSet);
					
					if (line.length() != 1) {
						wordType = line.substring(1);
						wordSet = new HashSet<String>();
					}
				}
				else
					wordSet.add(line);
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	@Override
	public FeatureVector getFeatures(String text) {

		FeatureVector result = new FeatureVector(fWords.size());
		int[] freq = new int[fWords.size()];
		
		String[] words = text.split(" " );
		String tmp;
		int j;
		
		for (int i=0; i < words.length; ++i) {
			tmp = clean(words[i]);
		
			j = 0;
			for (Set<String> x : fWords.values()) {
				if (x.contains(tmp)) {
					freq[j]++;
					break;
				}
				++j;
			}
		}
		
		for (int i=0; i < freq.length; ++i) {
			result.put(i, freq[i]);
		}
		
		return result;
	}
	
	public String clean(String x) {
		
		return x.replaceAll("[^a-zA-Z]", "").toLowerCase();
	}

}
