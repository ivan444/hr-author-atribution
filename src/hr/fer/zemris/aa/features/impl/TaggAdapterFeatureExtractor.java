package hr.fer.zemris.aa.features.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hr.fer.zemris.aa.features.FeatureVector;
import hr.fer.zemris.aa.features.IFeatureExtractor;

/**
 * Feature extractor koji se sastoji od više feature extractora.
 * 
 * @author Ivan Krišto
 *
 */
public class TaggAdapterFeatureExtractor implements IFeatureExtractor {
	private IFeatureExtractor extractor;
	private static Pattern descriptionP = Pattern.compile("/\\{([^\\}]*)\\}");
	
	public TaggAdapterFeatureExtractor(IFeatureExtractor extractor) {
		this.extractor = extractor;
	}
	
	@Override
	public FeatureVector getFeatures(String text) {
		Matcher m = descriptionP.matcher(text);
		text = m.replaceAll("");
		return extractor.getFeatures(text);
	}

	@Override
	public String getName() {
		return extractor.getName();
	}

	@Override
	public String getShortName() {
		return extractor.getShortName();
	}

}
