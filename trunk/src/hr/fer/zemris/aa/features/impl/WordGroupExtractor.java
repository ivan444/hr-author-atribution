package hr.fer.zemris.aa.features.impl;

import java.util.Arrays;
import java.util.List;

import hr.fer.zemris.aa.features.Descriptor;
import hr.fer.zemris.aa.features.FeatureVector;
import hr.fer.zemris.aa.features.IFeatureExtractor;
import hr.fer.zemris.aa.features.TextStatistics;

public class WordGroupExtractor implements IFeatureExtractor {
	
	@Override
	public FeatureVector getFeatures(String text) {
		
		//koristimo NOUN, VERB, ADJECTIVE, PRONOUN.
		int size = 4;
		FeatureVector result = new FeatureVector(size);
		int[] fr = new int[size];
		boolean was[] = new boolean[size];
		
		List<Descriptor> desList = null;
		int wordCount = 0;
		
		String[] words = text.split(" ");
		
		for (String x : words) {
			
			if (TextStatistics.cleanNew(x).isEmpty())
				continue;
			
			wordCount++;
			
			try {
				desList = Descriptor.parse(TextStatistics.getDescription(x));
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
			
			Arrays.fill(was, false);
			for (Descriptor d : desList) {
				int t = d.getTag().ordinal();
				
				if (t < size && !was[t]) {
					was[t] = true;
					fr[t]++;
				}
			}
		}
		
	
		for (int i=0; i<size;++i) {
			result.put(i, fr[i]/(float)wordCount);
		}
		
		return result;
	}

	@Override
	public String getName() {
		return "WordGroupExtractor";
	}

}
