package hr.fer.zemris.aa.main;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hr.fer.zemris.aa.features.Article;
import hr.fer.zemris.aa.features.FeatureClass;
import hr.fer.zemris.aa.features.IFeatureExtractor;
import hr.fer.zemris.aa.features.impl.ComboFeatureExtractor;
import hr.fer.zemris.aa.features.impl.SimpleFeatureExtractor;
import hr.fer.zemris.aa.recognizers.AuthorRecognizer;
import hr.fer.zemris.aa.recognizers.RecognizerTrainer;
import hr.fer.zemris.aa.recognizers.impl.LibsvmRecognizer;

/**
 * Razred za eksperimentiranje s različitim parametrima.
 * Slobodno proširiti po volji i potrebi.
 * 
 * @author Ivan Krišto
 */
public class Experimenter {
	
	public static AuthorRecognizer trainRecognizer(RecognizerTrainer trainer, List<FeatureClass> trainData) {
		System.out.println("Započinje treniranje.");
		System.out.println("Broj razreda: " + trainData.size());
		
		long trainSetSize = 0;
		for (FeatureClass featureClass : trainData) {
			trainSetSize += featureClass.size();
		}
		System.out.println("Ukupno uzoraka: " + trainSetSize);
		
		AuthorRecognizer recognizer = trainer.train(trainData);
		System.out.println("Učenje je završilo!");
		System.out.println();
		
		return recognizer;
	}
	
	/**
	 * Testiranje prepoznavatelja autora.
	 * 
	 * @param recognizer Prepoznavatelj koji testiramo.
	 * @param testData Skup članaka nad kojim testiramo autora.
	 * @return Postotak uspješno prepoznatih podataka.
	 */
	public static float testRecognizer(AuthorRecognizer recognizer, List<Article> testData) {
		int hits = 0;
		int misses = 0;
		
		if (testData == null || testData.size() == 0) {
			throw new IllegalArgumentException("Skup za testiranje je prazan!");
		}
		
		
		Set<String> authors = new HashSet<String>();
		for (Article a : testData) {
			authors.add(a.getAuthor());
		}
		
		System.out.println("Započinje testiranje.");
		System.out.println("Broj autora: " + authors.size());
		System.out.println("Autori u skupu za testiranje: " + authors);
		System.out.println("Ukupno clanaka: " + testData.size());
		
		for (Article article : testData) {
			String recogAuthor = recognizer.classifyAutor(article.getText());
			if (recogAuthor == null || recogAuthor.equals("")) {
				misses++;
			} else {
				hits++;
			}
		}
		
		float precision = hits*1.f/(hits+misses);
		
		System.out.println("Testiranje je završilo!");
		System.out.println("Preciznost: " + precision + " (" + hits + "/" + misses + ").");
		
		return precision;
	}
	
	public static List<FeatureClass> loadTrainData(String trainDataXMLPath, IFeatureExtractor extractor) {
		// TODO: Napiši.
		return null;
	}
	
	public static List<Article> loadTestData(String testDataXMLPath) {
		// TODO: Napiši.
		return null;
	}
	
	public static void preformExperiment(IFeatureExtractor featExtrac, RecognizerTrainer trainer, String trainDataPath, String testDataPath) {
		List<FeatureClass> trainData = loadTrainData(trainDataPath, featExtrac);
		AuthorRecognizer recognizer = trainRecognizer(trainer, trainData);
		
		List<Article> testData = loadTestData(testDataPath);
		testRecognizer(recognizer, testData);
	}

	public static void main(String[] args) {
		IFeatureExtractor featExtrac = new ComboFeatureExtractor(new SimpleFeatureExtractor(null));
		RecognizerTrainer trainer = new LibsvmRecognizer(featExtrac);
		preformExperiment(featExtrac, trainer, "putanja do train korpusa; args?", "putanja do test korpusa; args?");
	}
}
