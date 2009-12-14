package hr.fer.zemris.aa.features;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Razred koji implementira napradnije izlucivanje znacaji.
 * Znacajke su broj pojavljivanja odredjene funkcijske rijeci 
 * @author TOMISLAV
 *
 */

public class AdvancedFeatureExtractor implements IFeatureExtractor {
	
	Set<String> fWords = new LinkedHashSet<String>();
	
	public AdvancedFeatureExtractor(File inputFile) {
		
		try {
			
			BufferedReader bufferedReader = new BufferedReader(
					new FileReader(inputFile));

			
			String line = null;

			while ((line = bufferedReader.readLine()) != null) {
				
				line = line.trim().toLowerCase();
				
				if (line.length() == 0 || line.startsWith("#"))
					continue;
				
				if (line.startsWith("."))
					continue;
				
				fWords.add(line);
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	@Override
	public FeatureVector getFeatures(String text) {
		
		FeatureVector result = new FeatureVector(fWords.size());
		
		//incijalizacija mape kojom se broji pojavljivanje rijeci
		Map<String, Integer> countMap = new HashMap<String, Integer>();
		
		
		String[] words = text.split(" " );
		String tmp;
		
		for (int i=0; i < words.length; ++i) {
			tmp = clean(words[i]);
		
			if (fWords.contains(tmp)) {
				Integer x = countMap.get(tmp);
				if (x == null)
					countMap.put(tmp, 1);
				else
					countMap.put(tmp, x+1);
			}
			
		}
		
		int i = 0;
		for (String x : fWords) {
			Integer value = countMap.get(x);
			
			if (value != null)
				result.put(i, value);
			else
				result.put(i, 0);
			
			++i;
		}
		
		return result;
		
	}
	
	public String clean(String x) {
		
		return x.replaceAll("[^a-zA-Z]", "").toLowerCase();
	}

}
