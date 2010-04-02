package hr.fer.zemris.aa.features.impl;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import hr.fer.zemris.aa.features.FeatureVector;
import hr.fer.zemris.aa.features.IFeatureExtractor;
import hr.fer.zemris.aa.features.TextStatistics;

/**
 * Frekvencije 3-grama tipova.
 * 
 * @author Ivan Krišto
 *
 */
public class WordType3gramsFreqExtractor implements IFeatureExtractor {
	private Set<String> nGramSet; // Set nam služi samo radi brže provjere
	private List<String> nGramList;
	private boolean useOnlyTypes;
	
	public WordType3gramsFreqExtractor(String inputFile) throws FileNotFoundException {
		this(inputFile, false);
	}
	
	public WordType3gramsFreqExtractor(String inputFile, boolean useOnlyTypes) throws FileNotFoundException {
		nGramList = TextStatistics.listWords(inputFile);
		nGramSet = new HashSet<String>(nGramList);
		this.useOnlyTypes = useOnlyTypes;
	}
	
	@Override
	public FeatureVector getFeatures(String text) {
		FeatureVector result = new FeatureVector(nGramList.size());
		
		int[] freq = new int[nGramList.size()];
		
		List<String> types = new LinkedList<String>();
		try {
			if (useOnlyTypes) {
				TextStatistics.listOnlyTypes(text, types);
			} else {
				TextStatistics.listTypes(text, types);
			}
		} catch (Exception e) {
			System.err.println("Greška pri čitanju tipova!");
			return null;
		}
		
		int ngramIdx;
		int typeCount = types.size();
		String ngram;
		for (int i = 0; i+3 < types.size(); i++) {
			ngram = types.get(i) + "\t" + types.get(i+1) + "\t" + types.get(i+2);
			if (nGramSet.contains(ngram)) {
				ngramIdx = nGramList.indexOf(ngram);
				freq[ngramIdx]++;
			}
		}
		
		for (int j = 0; j < freq.length; j++) {
			result.put(j, freq[j]/(float)(typeCount-3));
		}
		
		return result;
	}

	@Override
	public String getName() {
		if (useOnlyTypes) return "WordType3grams1";
		else return "WordType3grams2";
	}

}
