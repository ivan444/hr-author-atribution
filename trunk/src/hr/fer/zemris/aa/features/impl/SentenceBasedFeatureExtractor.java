package hr.fer.zemris.aa.features.impl;

import hr.fer.zemris.aa.features.FeatureVector;
import hr.fer.zemris.aa.features.IFeatureExtractor;
import hr.fer.zemris.aa.features.TextStatistics;


public class SentenceBasedFeatureExtractor implements IFeatureExtractor {

	int n;
	
	public SentenceBasedFeatureExtractor(int n) {
		this.n = n;
	}
	
	@Override
	public FeatureVector getFeatures(String text) {
		
		FeatureVector result = new FeatureVector(n);
		String[] sentences = text.split("[.!?;]+\\s");
		String[] words = null;
		int count;
		int fr[] = new int[n];		
		
		for (String x : sentences) {
			words = x.trim().split(" ");
			count = 0;
			
			for (String w : words) {
				if (TextStatistics.cleanNew(w).isEmpty())
					continue;
				
				count++;
			}
		
			if (count < n)
				fr[count]++;
			else
				fr[n-1]++;
			
		}
		
		int d = sentences.length;
		
		for (int i=0; i<n; ++i)
			result.put(i, fr[i]/(float)d);
	
		return result;
	}

	@Override
	public String getName() {
		return "SentenceBasedFeatureExtractor";
	}

}
