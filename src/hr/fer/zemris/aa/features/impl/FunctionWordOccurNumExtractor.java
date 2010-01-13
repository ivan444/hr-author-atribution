package hr.fer.zemris.aa.features.impl;

import hr.fer.zemris.aa.features.FeatureVector;
import hr.fer.zemris.aa.features.IFeatureExtractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Razred koji implementira napradnije izlucivanje znacaji.
 * Znacajke su broj pojavljivanja odredjene funkcijske rijeci 
 * @author TOMISLAV
 *
 */

public class FunctionWordOccurNumExtractor implements IFeatureExtractor {
	Set<String> fWordsSet; // Set nam služi samo radi brže provjere je li riječ funkcijska.
	List<String> fWordsList;
	
	public FunctionWordOccurNumExtractor(File inputFile) {
		fWordsSet = new HashSet<String>();
		fWordsList = new ArrayList<String>();
		
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile));
			
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				line = line.trim().toLowerCase();
				if (line.length() == 0 || line.startsWith("#"))
					continue;
				if (line.startsWith("."))
					continue;
				fWordsSet.add(line);
				fWordsList.add(line);
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public FeatureVector getFeatures(String text) {
		FeatureVector result = new FeatureVector(fWordsList.size());
		
		int[] freq = new int[fWordsList.size()];
		
		String[] words = text.split(" ");
		String tmp;
		int wordIdx;
		
		int wordsCount = 0;
		
		for (int i=0; i < words.length; ++i) {
			tmp = clean(words[i]);
			
			if (tmp.length() != 0) {
				wordsCount++;
			}
		
			if (fWordsSet.contains(tmp)) {
				wordIdx = fWordsList.indexOf(tmp);
				freq[wordIdx]++;
			}
		}
		
		for (int j = 0; j < freq.length; j++) {
			result.put(j, freq[j]/(float)wordsCount);
		}
		
		return result;
	}
	
	public String clean(String x) {
		return x.replaceAll("[^a-zA-ZčćžšđČĆŽŠĐ]", "").toLowerCase();
	}
}
