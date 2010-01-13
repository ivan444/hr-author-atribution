package hr.fer.zemris.aa.main;

import hr.fer.zemris.aa.features.Article;
import hr.fer.zemris.aa.features.FeatureClass;
import hr.fer.zemris.aa.features.FeatureGenerator;
import hr.fer.zemris.aa.features.IFeatureExtractor;
import hr.fer.zemris.aa.features.impl.ComboFeatureExtractor;
import hr.fer.zemris.aa.features.impl.FunctionWordTFIDFExtractor;
import hr.fer.zemris.aa.features.impl.PunctuationMarksExtractor;
import hr.fer.zemris.aa.features.impl.VowelsExtractor;
import hr.fer.zemris.aa.recognizers.AuthorRecognizer;
import hr.fer.zemris.aa.recognizers.RecognizerTrainer;
import hr.fer.zemris.aa.recognizers.impl.LibsvmRecognizer;
import hr.fer.zemris.aa.xml.XMLMiner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.JDOMException;

/**
 * Razred za eksperimentiranje s različitim parametrima.
 * Slobodno proširiti po volji i potrebi.
 * 
 * @author Ivan Krišto
 */
public class Experimenter {
	
	private static AuthorRecognizer trainRecognizer(RecognizerTrainer trainer, List<FeatureClass> trainData) {
		System.out.println("Započinje učenje.");
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
	private static float testRecognizer(AuthorRecognizer recognizer, List<Article> testData) {
		int hits = 0;
		int misses = 0;
		
		if (testData == null || testData.size() == 0) {
			throw new IllegalArgumentException("Skup za testiranje je prazan!");
		}
		
		
		Set<String> authors = new HashSet<String>();
		Map<String, Integer> articleNumAuthor = new HashMap<String, Integer>();
		
		Integer tmp = null;
		for (Article a : testData) {
			authors.add(a.getAuthor());
			tmp = articleNumAuthor.get(a.getAuthor());
			if (tmp == null)
				articleNumAuthor.put(a.getAuthor(), 1);
			else
				articleNumAuthor.put(a.getAuthor(), tmp+1);
		}
		
		Map<String, Integer> hitsNumAuthor = new HashMap<String, Integer>();
		
		System.out.println("Započinje testiranje.");
		System.out.println("Broj autora: " + authors.size());
		System.out.println("Autori u skupu za testiranje: " + authors);
		System.out.println("Ukupno clanaka: " + testData.size());
		
		for (Article article : testData) {
			String recogAuthor = recognizer.classifyAutor(article.getText());
			if (recogAuthor == null || recogAuthor.equals("") || 
					!recogAuthor.equals(article.getAuthor())) {
				misses++;
			} else {
				
				tmp = hitsNumAuthor.get(article.getAuthor());
				if (tmp == null)
					hitsNumAuthor.put(article.getAuthor(), 1);
				else
					hitsNumAuthor.put(article.getAuthor(), tmp+1);
				
				hits++;
			}
		}
		
		float accuracy = hits*1.f/(hits+misses);
		
		for (String author : authors) {
			tmp = hitsNumAuthor.get(author);
			if (tmp != null) {
				System.out.format("%20s: %20f (%d/%d)%n",author,
						tmp/(float)articleNumAuthor.get(author),
						tmp, articleNumAuthor.get(author) );
			} else {
				System.out.format("%20s: %20f (%d/%d)%n",author,0.,0,articleNumAuthor.get(author));
			}
		}
		
		System.out.println("Testiranje je završilo!");
		System.out.println("Točnost: " + accuracy + " (" + hits + "/" + misses + ").");
		
		return accuracy;
	}
	
	private static List<FeatureClass> loadTrainData(String trainDataPath, IFeatureExtractor extractor) {
		FeatureGenerator fg = new FeatureGenerator(extractor);
		XMLMiner miner = new XMLMiner(trainDataPath);
		
		return fg.generateFeatureVectors(miner);
	}
	
	private static List<Article> loadTestData(String testDataPath) {
		try {
			return XMLMiner.getArticles(testDataPath);
		} catch (JDOMException e) {
			System.err.println("Format datoteke " + testDataPath + " je neispravan!");
		} catch (IOException e) {
			System.err.println("Ne mogu otvoriti datoteku " + testDataPath);
		}
		
		return null;
	}
	
	public static void preformExperiment(IFeatureExtractor featExtrac, RecognizerTrainer trainer, String trainDataPath, String testDataPath) {
		System.out.println("Započeo eksperiment!");
		List<FeatureClass> trainData = loadTrainData(trainDataPath, featExtrac);
		AuthorRecognizer recognizer = trainRecognizer(trainer, trainData);
		
		List<Article> testData = loadTestData(testDataPath);
		testRecognizer(recognizer, testData);
	}
	
	public static void preformMultiExperiment(String trainDataPath, String testDataPath, IFeatureExtractor ... arrFeatExtrac) {
		System.out.println("Započeo multieksperiment!");
		
		List<Article> testData = loadTestData(testDataPath);
		for (int i = 0; i < arrFeatExtrac.length; i++) {
			System.out.println("### FeatureExtractor: " + arrFeatExtrac[i].getName());
			List<FeatureClass> trainData = loadTrainData(trainDataPath, arrFeatExtrac[i]);
			
			RecognizerTrainer trainer = new LibsvmRecognizer(arrFeatExtrac[i], true);
			AuthorRecognizer recognizer = trainRecognizer(trainer, trainData);
			testRecognizer(recognizer, testData);
			System.out.println();
			trainer = null;
			recognizer = null;
			trainData = null;
			System.gc();
		}
	}

	public static void main(String[] args) {
		IFeatureExtractor featExtrac = null;
		try {
			featExtrac = new ComboFeatureExtractor(
					new PunctuationMarksExtractor(new File("config/marks.txt")),
//					new FunctionWordOccurNumExtractor("config/fwords.txt"),
					new VowelsExtractor(),
					new FunctionWordTFIDFExtractor("config/fw-idf.txt")
			);
		} catch (FileNotFoundException e) {
			System.err.println("Greška! " + e.getMessage());
			System.exit(-1);
		}
		
		RecognizerTrainer trainer = new LibsvmRecognizer(featExtrac, true);
		preformExperiment(featExtrac, trainer, "podatci-skripta/jutarnji-kolumne-arhiva-2009-11-14.train.xml", "podatci-skripta/jutarnji-kolumne-arhiva-2009-11-14.test.xml");
		
		// Za koristiti ovaj test treba povećati java heap! VM params u runu, npr. -Xms512m -Xmx1024m 
//		IFeatureExtractor fe1 = null;
//		IFeatureExtractor fe2 = null;
//		IFeatureExtractor fe3 = null;
//		IFeatureExtractor fe4 = null;
//		
//		try {
//			fe1 = new PunctuationMarksExtractor(new File("config/marks.txt"));
//			fe2 = new FunctionWordOccurNumExtractor("config/fwords.txt");
//			fe3 = new VowelsExtractor();
//			fe4 = new FunctionWordTFIDFExtractor("config/fw-idf.txt");
//		} catch (FileNotFoundException e) {
//			System.err.println("Greška! " + e.getMessage());
//			System.exit(-1);
//		}
//		preformMultiExperiment("podatci-skripta/jutarnji-kolumne-arhiva-2009-11-14.train.xml", "podatci-skripta/jutarnji-kolumne-arhiva-2009-11-14.test.xml",
//				new ComboFeatureExtractor(fe2,fe4),
//				new ComboFeatureExtractor(fe3,fe4),
//				new ComboFeatureExtractor(fe1, fe2,fe4),
//				new ComboFeatureExtractor(fe3, fe2,fe4),
//				new ComboFeatureExtractor(fe1, fe3,fe4),
//				new ComboFeatureExtractor(fe1, fe2, fe3, fe4)
//		);
	}
}
