package hr.fer.zemris.aa.recognizers.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

import hr.fer.zemris.aa.features.FeatureClass;
import hr.fer.zemris.aa.features.FeatureVector;
import hr.fer.zemris.aa.features.IFeatureExtractor;
import hr.fer.zemris.aa.recognizers.AuthorRecognizer;
import hr.fer.zemris.aa.recognizers.RecognizerTrainer;

/**
 * Klasifikator temeljen na libsvm biblioteci.
 * 
 * @author Ivan Krišto
 * 
 */
public class LibsvmRecognizer implements AuthorRecognizer, RecognizerTrainer {
	private svm_model model;
	private IFeatureExtractor featureExtractor;
	
	/** Mapiranje naziva klasa (libsvm radi sa brojčanim podatcima) */
	private Map<Double, String> classNames;
	
	/**
	 * Konstruktor za klasifikator bez postojećeg modela.
	 * 
	 * @param featureExtractor Izlučitelj značajki teksta.
	 */
	public LibsvmRecognizer(IFeatureExtractor featureExtractor) {
		this.model = null;
		this.classNames = new HashMap<Double, String>();
		this.featureExtractor = featureExtractor;
	}
	
	/**
	 * Konstruktor za klasifikator sa postojećim modelom.
	 * 
	 * @param modelPath Putanja do datoteke s modelom. Očekuje se da je u istom direktoriju
	 * datoteka sa mapiranjem razreda (brojčanaVrijednost<TAB>nazivRazreda). Završava na ".cn".
	 * @param featureExtractor Izlučitelj značajki teksta.
	 * @throws IllegalArgumentException Ukoliko je došlo do problema sa učitavanjem SVM modela ili datoteke s mapiranjem razreda.
	 */
	public LibsvmRecognizer(String modelPath, IFeatureExtractor featureExtractor) {
		try {
			this.model = svm.svm_load_model(modelPath);
		} catch (IOException e) {
			throw new IllegalArgumentException("Problem pri učitavanju SVM modela iz datoteke " + modelPath + "!");
		}
		this.featureExtractor = featureExtractor;
		
		String classNamesPath = modelPath + ".cn";
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
		// Set SVM parameters
		svm_parameter param = new svm_parameter();
		// default values
		param.svm_type = svm_parameter.C_SVC;
		param.kernel_type = svm_parameter.RBF;
		param.degree = 3;
		param.coef0 = 0;
		param.nu = 0.5;
		param.cache_size = 100;
		param.C = 1;
		param.eps = 1e-3;
		param.p = 0.1;
		param.shrinking = 1;
		param.probability = 0;
		param.nr_weight = 0;
		param.weight_label = new int[0];
		param.weight = new double[0];
		param.gamma = 0; // 1/num_features
		
		param.gamma = 1.0/trainData.get(0).size();
		
		// Postavljanje uzoraka
		Vector<Double> authors = new Vector<Double>();
		Vector<svm_node[]> values = new Vector<svm_node[]>();
		double authDbl = 0.0;
		
		for (FeatureClass fc : trainData) {
			authDbl += 1.0;
			
			// TODO: Ovo će se možda mijenjati... Mislim da nije ok da feature vector ima autora
			classNames.put(Double.valueOf(authDbl), fc.get(0).getAuthor());
			
			for (FeatureVector featureVector : fc) {
				authors.add(Double.valueOf(authDbl));
				int size = featureVector.getFeaturesDimension();
				svm_node[] sample = new svm_node[size];
				for (int i = 0; i < size; i++) {
					sample[i] = new svm_node();
					sample[i].index = i;
					sample[i].value = featureVector.get(i)*1.0;
				}
				values.add(sample);
			}
		}
		
		// Postavljanje problema
		svm_problem prob = new svm_problem();
		prob.l = authors.size();
		prob.x = new svm_node[prob.l][];
		for (int i = 0; i < prob.l; i++) {
			prob.x[i] = values.elementAt(i);
		}
		prob.y = new double[prob.l];
		for (int i = 0; i < prob.l; i++) {
			prob.y[i] = authors.elementAt(i);
		}
		
		// Provjera
		String errorMsg = svm.svm_check_parameter(prob, param);
		if (errorMsg != null) {
			throw new IllegalStateException("Neispravni parametri!");
		}
		
		// Učenje
		this.model = svm.svm_train(prob, param);
		
		return this;
	}

	@Override
	public AuthorRecognizer train(List<FeatureClass> trainData, String savePath) {
		// TODO: Možda provjera može li se uopće tamo pisati?
		train(trainData);
		
		try {
			svm.svm_save_model(savePath, this.model);
		} catch (IOException e) {
			throw new IllegalArgumentException("Problem s zapisivanjem modela u datoteku!");
		}
		
		return this;
	}

}
