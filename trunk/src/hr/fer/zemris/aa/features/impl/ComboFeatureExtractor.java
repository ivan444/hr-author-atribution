package hr.fer.zemris.aa.features.impl;

import hr.fer.zemris.aa.features.FeatureVector;
import hr.fer.zemris.aa.features.IFeatureExtractor;

/**
 * Feature extractor koji se sastoji od više feature extractora.
 * 
 * @author Ivan Krišto
 *
 */
public class ComboFeatureExtractor implements IFeatureExtractor {
	private IFeatureExtractor[] extractors;
	
	public ComboFeatureExtractor(IFeatureExtractor ... extractors) {
		this.extractors = extractors;
	}
	
	@Override
	public FeatureVector getFeatures(String text) {
		FeatureVector[] fVectors = new FeatureVector[extractors.length];
		int sumVectorSizes = 0;
		
		for (int i = 0; i < extractors.length; i++) {
			fVectors[i] = extractors[i].getFeatures(text);
			sumVectorSizes += fVectors[i].getFeaturesDimension();
		}
		
		FeatureVector comboFVector = new FeatureVector(sumVectorSizes);
		comboFVector.describe(fVectors[0].getAuthor(), fVectors[0].getTitle());

		int cfvIndex = 0;
		for (int i = 0; i < fVectors.length; i++) {
			for (int j = 0; j < fVectors[i].getFeaturesDimension(); j++) {
				comboFVector.put(cfvIndex, fVectors[i].get(j));
				cfvIndex++;
			}
		}
		
		return comboFVector;
	}

	@Override
	public String getName() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < extractors.length; i++) {
			if (i != 0) sb.append(" + ");
			sb.append(extractors[i].getName());
		}
		return sb.toString();
	}

}
