package hr.fer.zemris.aa.features.impl;

import hr.fer.zemris.aa.features.FeatureVector;
import hr.fer.zemris.aa.features.IFeatureExtractor;
import hr.fer.zemris.aa.features.TextStatistics;

public class WordLengthFeatureExtractor implements IFeatureExtractor {

	@Override
	public FeatureVector getFeatures(String text) {
		
		//maksimalna duljina rijeci koja ce imati svoju kategoriju
		int N = 10;
		
		FeatureVector result = new FeatureVector(N+1);
		
		String[] words = text.split(" ");
		String tmp = null;
		
		int[] wordSizeCount = new int[N+1];
		int wordsCount = 0;
		int size;
		
		for (int i=0; i < words.length; ++i) {
			tmp = TextStatistics.clean(words[i]);
			
			size = tmp.length();
			if (size > 0) {
				if (size > N)
					size = N+1;
				wordSizeCount[size-1]++;
				wordsCount++;
			}
		}
		
		for (int i=0; i<wordSizeCount.length;++i) {
			result.put(i, wordSizeCount[i]/(float)wordsCount);
		}
		
		return result;
	}

	@Override
	public String getName() {
		return "WordLengthFeatureExtractor";
	}

}
