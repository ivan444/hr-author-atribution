package hr.fer.zemris.aa.features.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hr.fer.zemris.aa.features.Descriptor;
import hr.fer.zemris.aa.features.FeatureVector;
import hr.fer.zemris.aa.features.IFeatureExtractor;
import hr.fer.zemris.aa.features.TextStatistics;
import hr.fer.zemris.aa.features.Descriptor.Tag;

public class WordGroupExtractor implements IFeatureExtractor {

	Map<String, Set<String>> fWords = new LinkedHashMap<String, Set<String>>();
	
	public WordGroupExtractor(File inputFile) {
	
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
		
		int size = Tag.values().length;
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
				
				if (!was[t]) {
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
