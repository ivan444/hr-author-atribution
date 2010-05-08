package hr.fer.zemris.aa.features.impl;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hr.fer.zemris.aa.features.Article;
import hr.fer.zemris.aa.features.FeatureVector;
import hr.fer.zemris.aa.features.IFeatureExtractor;
import hr.fer.zemris.aa.features.TextStatistics;

public class FunctionWordTFIDFExtractor implements IFeatureExtractor {
	private Map<String, Float> fwIdf;
	
	public FunctionWordTFIDFExtractor(String fwPath, List<Article> arhive) throws FileNotFoundException {
		fwIdf = TextStatistics.calcIdf(fwPath, arhive);
	}
	
	public FunctionWordTFIDFExtractor(String fwPath, List<Article> arhive, String idfSavePath) throws FileNotFoundException {
		fwIdf = TextStatistics.calcIdf(fwPath, arhive, idfSavePath);
	}
	
	public FunctionWordTFIDFExtractor(String fwIdfPath) throws FileNotFoundException {
		fwIdf = TextStatistics.readIdf(fwIdfPath);
	}
	
	@Override
	public FeatureVector getFeatures(String text) {
		int fWordsNum = fwIdf.keySet().size();
		List<String> fWordsList = new ArrayList<String>(fwIdf.keySet());
		FeatureVector result = new FeatureVector(fWordsNum);
		
		int[] freq = new int[fWordsNum];
		
		String[] words = text.split(" ");
		String current;
		int wordIdx;
		
		int wordsCount = 0;
		
		for (int i=0; i < words.length; ++i) {
			current = TextStatistics.clean(words[i]);
			
			if (current.length() != 0) {
				wordsCount++;
			}
		
			if (fwIdf.containsKey(current)) {
				wordIdx = fWordsList.indexOf(current);
				freq[wordIdx]++;
			}
		}
		
		for (int j = 0; j < freq.length; j++) {
			result.put(j, (freq[j]/(float)wordsCount)*fwIdf.get(fWordsList.get(j)));
		}
		
		return result;
	}

	@Override
	public String getName() {
		return "FunctionWordsTFIDF";
	}

	@Override
	public String getShortName() {
		return "I";
	}
	
	

}
