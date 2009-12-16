package hr.fer.zemris.aa.recognizers.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;

import hr.fer.zemris.aa.features.FeatureClass;
import hr.fer.zemris.aa.features.FeatureVector;
import hr.fer.zemris.aa.features.IFeatureExtractor;
import hr.fer.zemris.aa.recognizers.AuthorRecognizer;
import hr.fer.zemris.aa.recognizers.RecognizerTrainer;

public class LibsvmRecognizer implements AuthorRecognizer, RecognizerTrainer {
	private svm_model model;
	private IFeatureExtractor featureExtractor;
	
	/** Mapiranje naziva klasa (libsvm radi sa brojčanim podatcima) */
	private Map<Double, String> classNames;
	
	public LibsvmRecognizer() {
		this.model = null;
		this.classNames = null;
	}
	
	/**
	 * Konstruktor za klasifikator sa postojećim modelom.
	 * 
	 * @param modelPath Putanja do datoteke s modelom.
	 * @param classNamesPath Putanja do datoteke sa mapiranjem razreda (brojčanaVrijednost<TAB>nazivRazreda)
	 * @param featureExtractor Izlučitelj značajki teksta.
	 * @throws IllegalArgumentException Ukoliko je došlo do problema sa učitavanjem SVM modela ili datoteke s mapiranjem razreda.
	 */
	public LibsvmRecognizer(String modelPath, String classNamesPath, IFeatureExtractor featureExtractor) {
		try {
			this.model = svm.svm_load_model(modelPath);
		} catch (IOException e) {
			throw new IllegalArgumentException("Problem pri učitavanju SVM modela iz datoteke " + modelPath + "!");
		}
		this.featureExtractor = featureExtractor;
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(classNamesPath));
		} catch (FileNotFoundException e1) {
			throw new IllegalArgumentException("Datoteka " + classNamesPath + " ne postoji.");
		}
		classNames = new HashMap<Double, String>();
		String line = null;
		int lineNum = 0;
		try {
			while (true) {
				lineNum++;
				line = reader.readLine();
				if (line == null) break;
				line = line.trim();
				if (line.equals("")) break;
				
				String[] valCls = line.split("\t");
				if (valCls.length != 2) throw new IllegalArgumentException("Neispravan format "
						+ classNamesPath + " datoteke na liniji " + lineNum
						+ "! Traženi format: <decimalnaVrijedRazreda><tab><stringVrijedRazreda>.");
				double val = 0;
				
				try {
					val = Double.parseDouble(valCls[0]);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("Neispravan format "
							+ classNamesPath + " datoteke na liniji " + lineNum
							+ "! Vrijednost treba biti decimalni broj!");
				}
				classNames.put(Double.valueOf(val), valCls[1]);
				
			}
		} catch (IOException e) {
			throw new IllegalArgumentException("Problem s čitanjem datoteke " + classNamesPath + "!");
		}
	}
	
	@Override
	public String classifyAutor(String text) {
		if (model == null) throw new IllegalStateException("SVM model nije definiran!");
		
		FeatureVector features = featureExtractor.getFeatures(text);
		int featuresDim = features.getFeaturesDimension();
		svm_node[] svmFeatures = new svm_node[featuresDim];
		for (int i = 0; i < featuresDim; i++) {
			svmFeatures[i] = new svm_node();
			svmFeatures[i].index = i;
			svmFeatures[i].value = features.get(i)*1.0;
		}
		
		double recognizedCls = svm.svm_predict(model, svmFeatures);
		
		return classNames.get(Double.valueOf(recognizedCls));
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
