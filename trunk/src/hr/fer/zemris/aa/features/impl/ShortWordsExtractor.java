package hr.fer.zemris.aa.features.impl;

import java.util.HashSet;
import java.util.Set;

import hr.fer.zemris.aa.features.FeatureVector;
import hr.fer.zemris.aa.features.IFeatureExtractor;
import hr.fer.zemris.aa.features.TextStatistics;

public class ShortWordsExtractor implements IFeatureExtractor {

	@Override
	public FeatureVector getFeatures(String text) {
		TextStatistics stat = new TextStatistics(text);
		
		Set<String> shortWords = new HashSet<String>();
		for (String word : stat) {
			if (word.length() <= 3) {
				shortWords.add(word);
			}
		}
		
		FeatureVector fv = new FeatureVector(shortWords.size());
		int index = 0;
		for (String word : shortWords) {
			fv.put(index, stat.getLogFreq(word));
		}
		
		return fv;
	}

}
