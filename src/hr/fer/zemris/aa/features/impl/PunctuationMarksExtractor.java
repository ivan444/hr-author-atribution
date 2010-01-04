package hr.fer.zemris.aa.features.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import hr.fer.zemris.aa.features.FeatureVector;
import hr.fer.zemris.aa.features.IFeatureExtractor;

public class PunctuationMarksExtractor implements IFeatureExtractor {
	
	List<String> marks = new ArrayList<String>();
	
	public PunctuationMarksExtractor(File inputFile) {
		try {
			
			BufferedReader bufferedReader = new BufferedReader(
					new FileReader(inputFile));

			
			String line = null;

			while ((line = bufferedReader.readLine()) != null) {
				
				line = line.trim().toLowerCase();
				
				if (line.length() == 0 || line.startsWith("#"))
					continue;
				
				marks.add(line);
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public FeatureVector getFeatures(String text) {
		
		FeatureVector result = new FeatureVector(marks.size());
		int freq[] = new int[marks.size()];
		
		for (int i=0; i < text.length(); ++i) {
			
			for (int j=0; j < marks.size(); ++j) {
				if (text.startsWith(marks.get(j), i))
					freq[j]++;
					
			}
			
		}
		String[] words = text.split(" ");
		String tmp;
		int wordsCount = 0;
		
		for (int i=0; i < words.length; ++i) {
			tmp = clean(words[i]);
			
			if (tmp.length() != 0)
				wordsCount++;
		}
		
		for (int i=0; i < freq.length; ++i) {
			result.put(i, freq[i]/(float)wordsCount);
		}
		
		return result;
	}

	public String clean(String x) {
		return x.replaceAll("[^a-zA-ZčćžšđČĆŽŠĐ]", "").toLowerCase();
	}
}
