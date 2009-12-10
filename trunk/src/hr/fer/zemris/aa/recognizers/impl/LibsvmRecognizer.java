package hr.fer.zemris.aa.recognizers.impl;

import java.util.List;

import hr.fer.zemris.aa.features.FeatureClass;
import hr.fer.zemris.aa.recognizers.AuthorRecognizer;
import hr.fer.zemris.aa.recognizers.RecognizerTrainer;

public class LibsvmRecognizer implements AuthorRecognizer, RecognizerTrainer {

	@Override
	public String classifyAutor(String text) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AuthorRecognizer train(List<FeatureClass> trainData) {
		// TODO Auto-generated method stub
		return new LibsvmRecognizer();
	}

	@Override
	public AuthorRecognizer train(List<FeatureClass> trainData, String savePath) {
		LibsvmRecognizer recog = (LibsvmRecognizer) train(trainData);
		// Spremi na disk
		return recog;
	}

}
