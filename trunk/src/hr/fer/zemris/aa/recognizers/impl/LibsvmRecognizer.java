package hr.fer.zemris.aa.recognizers.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_print_interface;
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
	
	private float[] fmax = null;
	private float[] fmin = null;
	private boolean scale;
	private double C;
	private double gamma;
	
	private final static float eps = 1e-7F;
	
	/** Mapiranje naziva klasa (libsvm radi sa brojčanim podatcima) */
	private Map<Double, String> classNames;
	
	public LibsvmRecognizer(IFeatureExtractor featureExtractor, boolean scale, double C, double gamma) {
		this.model = null;
		this.scale = scale;
		this.classNames = new HashMap<Double, String>();
		this.featureExtractor = featureExtractor;
		this.C = C;
		this.gamma = gamma;
	}
	
	/**
	 * Konstruktor za klasifikator bez postojećeg modela.
	 * 
	 * @param featureExtractor Izlučitelj značajki teksta.
	 * @param Skalirati ili ne.
	 */
	public LibsvmRecognizer(IFeatureExtractor featureExtractor, boolean scale) {
		this(featureExtractor, scale, 16, 0.25);
	}
	
	public LibsvmRecognizer(IFeatureExtractor featureExtractor) {
		this(featureExtractor, true);
	}
	
	/**
	 * Konstruktor za klasifikator sa postojećim modelom.
	 * 
	 * @param modelPath Putanja do datoteke s modelom. Očekuje se da je u istom direktoriju
	 * datoteka sa mapiranjem razreda (brojčanaVrijednost<TAB>nazivRazreda) (završava na ".cn") i također
	 * datoteka s parametrima skaliranja (završava na ".scale") 
	 * @param featureExtractor Izlučitelj značajki teksta.
	 * @throws IllegalArgumentException Ukoliko je došlo do problema sa učitavanjem SVM modela ili datoteke s mapiranjem razreda.
	 */
	public LibsvmRecognizer(String modelPath, IFeatureExtractor featureExtractor) {
		this(modelPath, featureExtractor, true);
	}
	
	public LibsvmRecognizer(String modelPath, IFeatureExtractor featureExtractor, boolean scale) {
		this.scale = scale;
		try {
			this.model = svm.svm_load_model(modelPath);
			if (scale) {
				loadScale(modelPath);
			}
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
	public String classifyAuthor(String text) {
		if (model == null) throw new IllegalStateException("SVM model nije definiran!");
		
		FeatureVector features = featureExtractor.getFeatures(text);
		int featuresDim = features.getFeaturesDimension();
		svm_node[] svmFeatures = new svm_node[featuresDim];
		for (int i = 0; i < featuresDim; i++) {
			svmFeatures[i] = new svm_node();
			svmFeatures[i].index = i;
			
			//skaliranje
			if (scale && (fmax[i]-fmin[i])>eps)
				svmFeatures[i].value = (features.get(i)-fmin[i])/(fmax[i]-fmin[i]);
			else {
				svmFeatures[i].value = features.get(i);
			}
		}
		
		double recognizedCls = svm.svm_predict(model, svmFeatures);
		
		return classNames.get(Double.valueOf(recognizedCls));
	}
	
	

	@Override
	public AuthorRecognizer train(List<FeatureClass> trainData) {
		
		//prvo ucimo distribuciju atributa
		scaleDistribution(trainData);
		
		// Set SVM parameters
		svm_parameter param = new svm_parameter();
		// default values
		param.svm_type = svm_parameter.C_SVC;
		param.kernel_type = svm_parameter.RBF;
		param.degree = 3;
		param.coef0 = 1;
		param.nu = 0.5;
		param.cache_size = 700;
		param.C = C;
		param.eps = 1e-3;
		param.p = 0.1;
		param.shrinking = 1;
		param.probability = 0;
		param.nr_weight = 0;
		param.weight_label = new int[0];
		param.weight = new double[0];
		param.gamma = gamma;
		
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
					
					//skaliranje
					if (scale && fmax[i]-fmin[i]>eps)
						sample[i].value = (featureVector.get(i)-fmin[i])/(fmax[i]-fmin[i]);
					else
						sample[i].value = featureVector.get(i);
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
		
		// Postavljanje 'quiet modea'
		svm.svm_print_string = new svm_print_interface() { 
			public void print(String s){}
		};
		
		// Provjera
		String errorMsg = svm.svm_check_parameter(prob, param);
		if (errorMsg != null) {
			throw new IllegalStateException("Neispravni parametri!");
		}
		
		// Učenje
		this.model = svm.svm_train(prob, param);
		
		return this;
	}
	
	private void scaleDistribution(List<FeatureClass> trainData) {
		int dim = trainData.get(0).get(0).getFeaturesDimension();
		
		this.fmax = new float[dim];
		this.fmin = new float[dim];
		
		float tmp;
		
		Arrays.fill(fmax, Float.MIN_VALUE);
		Arrays.fill(fmin, Float.MAX_VALUE);
		
		//1. prolaz - trazimo min/max
		for (FeatureClass fClass : trainData) {
			for (FeatureVector x : fClass) {
				for (int i=0; i < dim; ++i) {
					if (x.getFeaturesDimension() <= i) break;
					tmp = x.get(i);
					if (fmax[i] < tmp)
						fmax[i] = tmp;
					if (fmin[i] > tmp)
						fmin[i] = tmp;
				}
			}
		}
	}

	/**
	 * Metoda koja trazi pogodne parametre gamma i C pomocu cross-validacije
	 * @param trainData
	 */
	public void gridSearch(List<FeatureClass> trainData) {
		gridSearch(trainData, null);
	}
	
	public void gridSearch(List<FeatureClass> trainData, Writer writer) {
		scaleDistribution(trainData);
		
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
					
					if (scale && fmax[i]-fmin[i]>eps)
						sample[i].value = (featureVector.get(i)-fmin[i])/(fmax[i]-fmin[i]);
					else
						sample[i].value = featureVector.get(i);
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
		
		// Set SVM parameters
		svm_parameter param = new svm_parameter();
		// default values
		param.svm_type = svm_parameter.C_SVC;
		param.kernel_type = svm_parameter.RBF;
		param.cache_size = 1200;
		param.eps = 1e-3;
		param.p = 0.1;
		param.shrinking = 1;
		param.probability = 0;
		param.nr_weight = 0;
		
		double bestAcc = 0;
		double bestC = 0;
		double bestGamma = 0;
	
		// Postavljanje 'quiet modea'
		svm.svm_print_string = new svm_print_interface() { 
			public void print(String s){}
		};
		
		double[] target = null;
		
		for (int i = 15; i > -6; i-=2) {
			for (int j = 3; j > -16; j-=2) {
		
				//setting additional params
				if (i < 0)
					param.C = 1./(1<<-i);
				else
					param.C = 1<<i;
				
				if (j < 0)
					param.gamma = 1./(1<<-j);
				else
					param.gamma = 1<<j;
		
				String errorMsg = svm.svm_check_parameter(prob, param);
				if (errorMsg != null) {
					throw new IllegalStateException("Neispravni parametri!");
				}
		
				target = new double[prob.l];
				
				svm.svm_cross_validation(prob, param, 5, target);
				
				int totalCorrect = 0;
				for (int k = 0; k < prob.l; k++)
					if (target[k] == prob.y[k])
						++totalCorrect;
				
				if (bestAcc < ((double)totalCorrect)/prob.l) {
					bestAcc = ((double)totalCorrect)/prob.l;
					bestGamma = param.gamma;
					bestC = param.C;
				}
				
				if (writer == null) {
					System.out.println("C == "+i+", g == "+j+": "+((double)totalCorrect)/prob.l);
				} else {
					try {
						writer.write("C == "+i+", g == "+j+": "+((double)totalCorrect)/prob.l + "\n");
						writer.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		if (writer == null) {
			System.out.println(bestAcc+": C = "+bestC+", gamma = "+bestGamma);
		} else {
			try {
				writer.write(bestAcc+": C = "+bestC+", gamma = "+bestGamma + "\n");
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Metoda koja sluzi za finije pretrazivanje prostora parametara za pogodnim
	 * parametrima gamma i C
	 * @param trainData
	 */
	//TODO: dovrsiti
	public void fineSearch(List<FeatureClass> trainData) {
		
		scaleDistribution(trainData);
		
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
					
					if (scale && fmax[i]-fmin[i]>eps)
						sample[i].value = (featureVector.get(i)-fmin[i])/(fmax[i]-fmin[i]);
					else
						sample[i].value = featureVector.get(i);
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
		
		// Set SVM parameters
		svm_parameter param = new svm_parameter();
		// default values
		param.svm_type = svm_parameter.C_SVC;
		param.kernel_type = svm_parameter.RBF;
		param.cache_size = 700;
		param.eps = 1e-3;
		param.p = 0.1;
		param.shrinking = 1;
		param.probability = 0;
		param.nr_weight = 0;
		
		double bestAcc = 0;
		double bestC = 0;
		double bestGamma = 0;
	
		// Postavljanje 'quiet modea'
		svm.svm_print_string = new svm_print_interface() { 
			public void print(String s){}
		};
		
		double[] target = null;
		
		for (int i = 15; i > -6; --i) {
			for (int j = 3; j > -16; --j) {
		
				//setting additional params
				if (i < 0)
					param.C = 1./(1<<-i);
				else
					param.C = 1<<i;
				
				if (j < 0)
					param.gamma = 1./(1<<-j);
				else
					param.gamma = 1<<j;
		
				String errorMsg = svm.svm_check_parameter(prob, param);
				if (errorMsg != null) {
					throw new IllegalStateException("Neispravni parametri!");
				}
		
				target = new double[prob.l];
				
				svm.svm_cross_validation(prob, param, 5, target);
				
				int totalCorrect = 0;
				for (int k = 0; k < prob.l; k++)
					if (target[k] == prob.y[k])
						++totalCorrect;
				
				if (bestAcc < ((double)totalCorrect)/prob.l) {
					bestAcc = ((double)totalCorrect)/prob.l;
					bestGamma = param.gamma;
					bestC = param.C;
				}
				
				System.out.println("C == "+i+", g == "+j+": "+((double)totalCorrect)/prob.l);
			}
		}
		
		System.out.println(bestAcc+": C = "+bestC+", gamma = "+bestGamma);
	}
	
	@Override
	public AuthorRecognizer train(List<FeatureClass> trainData, String savePath) {
		train(trainData);
		
		try {
			svm.svm_save_model(savePath, this.model);
			if (scale) {
				saveScale(savePath);
			}
			saveClassNames(savePath+".cn");
		} catch (IOException e) {
			throw new IllegalArgumentException("Problem s zapisivanjem modela u datoteku!");
		}
		
		return this;
	}

	private void saveClassNames(String path) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
		for (Double cls : classNames.keySet()) {
			writer.write(cls + "\t" + classNames.get(cls) + "\n");
		}
		writer.close();
	}

	/**
	 * Metoda koja sprema parametre skaliranja. Ime datoteke jednako je imenu daototeke u koju
	 * se sprema svm model s dodanim nastavkom ".scale"
	 * @param savePath ime datoteke u koju se sprema model
	 */
	private void saveScale(String savePath) {
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(savePath+".scale"));
			oos.writeObject(fmin);
			oos.writeObject(fmax);
			oos.flush();
			oos.close();
		} catch (Exception e) {
			throw new IllegalArgumentException("Problem s zapisivanjem parametara skaliranja u datoteku "+savePath+".scale !");
		}
		
	}

	/**
	 * Metoda koja ucitava parametre skaliranja. Ime datoteke jednako je imenu daototeke iz kojeg
	 * se ucitava svm model s dodanim nastavkom ".scale"
	 * @param modelPath ime datoteke u kojoj je spremljen svm model
	 */
	private void loadScale(String modelPath) {
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(modelPath+".scale"));
			fmin = (float[])ois.readObject();
			fmax = (float[])ois.readObject();
			ois.close();
		} catch (Exception e) {
			throw new IllegalArgumentException("Problem s citanjem parametara skaliranja iz datoteke"+modelPath+".scale !");
		}
	}
}
