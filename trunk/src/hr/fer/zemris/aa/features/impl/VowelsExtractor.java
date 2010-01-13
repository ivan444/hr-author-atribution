package hr.fer.zemris.aa.features.impl;

import hr.fer.zemris.aa.features.FeatureVector;
import hr.fer.zemris.aa.features.IFeatureExtractor;

public class VowelsExtractor implements IFeatureExtractor {

	@Override
	public FeatureVector getFeatures(String text) {
		FeatureVector fv = new FeatureVector(5);
		int[]vowelsNum = new int[5];
		int textLen = text.length();
		for (int i = 0; i < textLen; i++) {
			char ch = Character.toLowerCase(text.charAt(i));
			if (ch == 'a') vowelsNum[0]++;
			else if (ch == 'e') vowelsNum[1]++;
			else if (ch == 'i') vowelsNum[2]++;
			else if (ch == 'o') vowelsNum[3]++;
			else if (ch == 'u') vowelsNum[4]++;
		}
		
		for (int i = 0; i < 5; i++) {
			fv.put(i, 1.f*vowelsNum[i]);
		}
		
		return fv;
	}

}
