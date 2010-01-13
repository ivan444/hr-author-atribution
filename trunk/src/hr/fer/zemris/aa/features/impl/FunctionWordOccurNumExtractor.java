package hr.fer.zemris.aa.features.impl;

import hr.fer.zemris.aa.features.FeatureVector;
import hr.fer.zemris.aa.features.IFeatureExtractor;
import hr.fer.zemris.aa.features.TextStatistics;

import java.io.FileNotFoundException;
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
	
	public FunctionWordOccurNumExtractor(String inputFile) throws FileNotFoundException {
		fWordsSet = new HashSet<String>();
		fWordsList = TextStatistics.listWords(inputFile);
		
		for (String fw : fWordsList) {
			fWordsSet.add(fw);
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
			tmp = TextStatistics.clean(words[i]);
			
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

	@Override
	public String getName() {
		return "FunctionWordOccurNumExtractor";
	}
}
