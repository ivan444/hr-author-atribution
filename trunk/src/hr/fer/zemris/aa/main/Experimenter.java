package hr.fer.zemris.aa.main;

import hr.fer.zemris.aa.features.Article;
import hr.fer.zemris.aa.features.FeatureClass;
import hr.fer.zemris.aa.features.FeatureGenerator;
import hr.fer.zemris.aa.features.IFeatureExtractor;
import hr.fer.zemris.aa.features.impl.ComboFeatureExtractor;
import hr.fer.zemris.aa.features.impl.FunctionWordGroupFreqExtractor;
import hr.fer.zemris.aa.features.impl.FunctionWordOccurNumExtractor;
import hr.fer.zemris.aa.features.impl.FunctionWordTFIDFExtractor;
import hr.fer.zemris.aa.features.impl.MorphosyntaticFeatureExtractor;
import hr.fer.zemris.aa.features.impl.PunctuationMarksExtractor;
import hr.fer.zemris.aa.features.impl.SentenceBasedFeatureExtractor;
import hr.fer.zemris.aa.features.impl.TaggAdapterFeatureExtractor;
import hr.fer.zemris.aa.features.impl.VowelsExtractor;
import hr.fer.zemris.aa.features.impl.WordGroupExtractor;
import hr.fer.zemris.aa.features.impl.WordLengthFeatureExtractor;
import hr.fer.zemris.aa.features.impl.WordType3gramsFreqExtractor;
import hr.fer.zemris.aa.recognizers.AuthorRecognizer;
import hr.fer.zemris.aa.recognizers.RecognizerTrainer;
import hr.fer.zemris.aa.recognizers.impl.LibsvmRecognizer;
import hr.fer.zemris.aa.xml.XMLMiner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
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
		Map<String, Integer> failNumAuthor = new HashMap<String, Integer>();
		
		System.out.println("Započinje testiranje.");
		System.out.println("Broj autora: " + authors.size());
		System.out.println("Autori u skupu za testiranje: " + authors);
		System.out.println("Ukupno clanaka: " + testData.size());
		
		for (Article article : testData) {
			String recogAuthor = recognizer.classifyAuthor(article.getText());
			if (recogAuthor == null || recogAuthor.equals("") || 
					!recogAuthor.equals(article.getAuthor())) {
				misses++;
				tmp = failNumAuthor.get(recogAuthor);
				if (tmp == null)
					failNumAuthor.put(recogAuthor, 1);
				else
					failNumAuthor.put(recogAuthor, tmp + 1);
			} else {
				
				tmp = hitsNumAuthor.get(article.getAuthor());
				if (tmp == null)
					hitsNumAuthor.put(article.getAuthor(), 1);
				else
					hitsNumAuthor.put(article.getAuthor(), tmp+1);
				
				hits++;
			}
		}
		
		float precision = hits*1.f/(hits+misses);
		
		Integer tmp2 = null;
		float f1 = 0;
		
		System.out.format("%20s  %20s %20s \tOmjer%n","Autor","Preciznost","Odziv");
		for (String author : authors) {
			tmp = hitsNumAuthor.get(author);
			if (tmp == null) tmp = 0;
			tmp2 = failNumAuthor.get(author);
			if (tmp2 == null) tmp2 = 0;
		
			if (tmp != 0)
				f1 += 2*(tmp/(float)(tmp+tmp2)*tmp/(float)articleNumAuthor.get(author)) / (tmp/(float)(tmp+tmp2)+tmp/(float)articleNumAuthor.get(author))*articleNumAuthor.get(author);
			
			System.out.format("%20s: %20f %20f \t(%d/%d)%n",author,
					tmp/(float)(tmp+tmp2),
					tmp/(float)articleNumAuthor.get(author),
					tmp, articleNumAuthor.get(author) );
		}
		
		System.out.println("Testiranje je završilo!");
		System.out.println("Uspješnost: " + precision + " (" + hits + "/" + misses + ").");
		System.out.println(f1/testData.size());
		
		return precision;
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
	
	/**
	 * Stvaranje vektora značajki po konfig datoteci.
	 * @param configPath
	 * @return
	 * @throws FileNotFoundException
	 */
	public static List<IFeatureExtractor> createFeatureSets(String configPath) throws FileNotFoundException {
		Scanner s = new Scanner(new FileInputStream(configPath));
		String sufix = null;
		while (s.hasNextLine()) {
			String line = s.nextLine();
			if (line.startsWith("#")) continue;
			else if (line.startsWith("!") && line.length() > 1) {
				sufix = line.substring(1);
				break;
			} else {
				System.err.println("Sufiks nije definiran!");
				System.exit(-1);
			}
		}
		
		List<IFeatureExtractor> allFeatures = new LinkedList<IFeatureExtractor>();
		while (s.hasNextLine()) {
			String line = s.nextLine();
			line = line.trim();
			if (line.equals("")) continue;
			
			String[] strFeat = line.split(",");
			IFeatureExtractor[] features = new IFeatureExtractor[strFeat.length];
			
			boolean tagged = false;
			for (int i = 0; i < strFeat.length; i++) {
				if (strFeat[i].equals("M") || strFeat[i].equals("N1") || strFeat[i].equals("N2")) {
					tagged = true;
				}
			}
			for (int i = 0; i < strFeat.length; i++) {
				if (strFeat[i].equals("F")) {
					features[i] = new FunctionWordOccurNumExtractor("config/fwords.txt");
					if (tagged) {
						features[i] = new TaggAdapterFeatureExtractor(features[i]);
					}
				} else if (strFeat[i].equals("I")) {
					features[i] = new FunctionWordTFIDFExtractor("config/fw-idf"+sufix+".txt");
					if (tagged) {
						features[i] = new TaggAdapterFeatureExtractor(features[i]);
					}
				} else if (strFeat[i].equals("C")) {
					features[i] = new FunctionWordGroupFreqExtractor(new File("config/fwords.txt"));
					if (tagged) {
						features[i] = new TaggAdapterFeatureExtractor(features[i]);
					}
				} else if (strFeat[i].equals("P")) {
					features[i] = new PunctuationMarksExtractor(new File("config/marks.txt"));
					if (tagged) {
						features[i] = new TaggAdapterFeatureExtractor(features[i]);
					}
				} else if (strFeat[i].equals("V")) {
					features[i] = new VowelsExtractor();
					if (tagged) {
						features[i] = new TaggAdapterFeatureExtractor(features[i]);
					}
				} else if (strFeat[i].equals("L")) {
					features[i] = new WordLengthFeatureExtractor();
					if (tagged) {
						features[i] = new TaggAdapterFeatureExtractor(features[i]);
					}
				} else if (strFeat[i].equals("S")) {
					features[i] = new SentenceBasedFeatureExtractor(20);
					if (tagged) {
						features[i] = new TaggAdapterFeatureExtractor(features[i]);
					}
				} else if (strFeat[i].equals("N1")) {
					features[i] = new WordType3gramsFreqExtractor("config/n-grami-cisti-najcesci.txt", true);
				} else if (strFeat[i].equals("N2")) {
					features[i] = new WordType3gramsFreqExtractor("config/n-grami-najcesci"+sufix+".txt", false);
				} else if (strFeat[i].equals("M")) {
					features[i] = new MorphosyntaticFeatureExtractor();
				} 
			}
			allFeatures.add(new ComboFeatureExtractor(features));
		}
		
		return allFeatures;
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
	
	public static void smth() throws FileNotFoundException {
		IFeatureExtractor[] arrFeatExtrac = new IFeatureExtractor[6];
		double[] Cs = new double[6];
		double[] gammas = new double[6];
		
//		arrFeatExtrac[0] = new FunctionWordTFIDFExtractor("config/fw-idf.txt");
//		Cs[0] = 8192.0;
//		gammas[0] = 0.125;
//		
		arrFeatExtrac[1] = new PunctuationMarksExtractor(new File("config/marks.txt"));
		Cs[1] = 8192.0;
		gammas[1] = 0.125;
		
		arrFeatExtrac[2] = new VowelsExtractor();
		Cs[2] = 128.0;
		gammas[2] = 0.125;
		
		arrFeatExtrac[3] = new WordLengthFeatureExtractor();
		Cs[3] = 128.0;
		gammas[3] = 0.125;
		
		arrFeatExtrac[4] = new FunctionWordOccurNumExtractor("config/fwords.txt");
		Cs[4] = 8192.0;
		gammas[4] = 0.125;
		
		arrFeatExtrac[5] = new SentenceBasedFeatureExtractor(20);
		Cs[5] = 128.0;
		gammas[5] = 0.125;
//		
//		arrFeatExtrac[6] = new ComboFeatureExtractor(
//				new PunctuationMarksExtractor(new File("config/marks.txt")),
//				new FunctionWordOccurNumExtractor("config/fwords.txt")
//		);
//		Cs[6] = 8.0;
//		gammas[6] = 0.03125;
//		
//		arrFeatExtrac[7] = new ComboFeatureExtractor(
//				new PunctuationMarksExtractor(new File("config/marks.txt")),
//				new FunctionWordOccurNumExtractor("config/fwords.txt"),
//				new WordLengthFeatureExtractor()
//		);
//		Cs[7] = 128.0;
//		gammas[7] = 0.03125;
//		
		arrFeatExtrac[0] = new ComboFeatureExtractor(
				new SentenceBasedFeatureExtractor(20),
				new PunctuationMarksExtractor(new File("config/marks.txt")),
				new FunctionWordOccurNumExtractor("config/fwords.txt"),
				new WordLengthFeatureExtractor()
		);
		Cs[0] = 128.0;
		gammas[0] = 0.03125;
//		
//		arrFeatExtrac[9] = new ComboFeatureExtractor(
//				new SentenceBasedFeatureExtractor(20),
//				new PunctuationMarksExtractor(new File("config/marks.txt")),
//				new FunctionWordOccurNumExtractor("config/fwords.txt"),
//				new VowelsExtractor(),
//				new WordLengthFeatureExtractor()
//		);
//		Cs[9] = 128.0;
//		gammas[9] = 0.03125;
		
		String testDataPath = "podatci-skripta/blog-hr-aa-arhiva-2010-04-02.short.test.xml";
		String trainDataPath = "podatci-skripta/blog-hr-aa-arhiva-2010-04-02.short.train.xml";
//		String testDataPath = "podatci-skripta/jutarnji-kolumne-arhiva-2010-02-05.test.xml";
//		String trainDataPath = "podatci-skripta/jutarnji-kolumne-arhiva-2010-02-05.train.xml";
		List<Article> testData = loadTestData(testDataPath);
		for (int i = 0; i < arrFeatExtrac.length; i++) {
			try {
				System.out.println("### FeatureExtractor: " + arrFeatExtrac[i].getName());
				List<FeatureClass> trainData = loadTrainData(trainDataPath, arrFeatExtrac[i]);
				
				RecognizerTrainer trainer = new LibsvmRecognizer(arrFeatExtrac[i], true, Cs[i], gammas[i]);
				AuthorRecognizer recognizer = trainRecognizer(trainer, trainData);
				testRecognizer(recognizer, testData);
				System.out.println();
				trainer = null;
				recognizer = null;
				trainData = null;
				System.gc();
			} catch (Exception e) {
				System.err.println("Greška\n" + e.getMessage());
			}
		}
	}
	
	public static void smth2() throws FileNotFoundException {
		IFeatureExtractor[] arrFeatExtrac = new IFeatureExtractor[5];
		double[] Cs = new double[5];
		double[] gammas = new double[5];
//		SentenceBasedFeatureExtractor(20) + PunctuationMarksExtractor + WordType3grams(N2) + WordLengthFeatureExtractor
//		0.8475912408759124: C = 8.0, gamma = 0.03125
		
		arrFeatExtrac[0] = new ComboFeatureExtractor(
				new TaggAdapterFeatureExtractor(new PunctuationMarksExtractor(new File("config/marks.txt"))),
				new TaggAdapterFeatureExtractor(new FunctionWordOccurNumExtractor("config/fwords.txt")),
				new TaggAdapterFeatureExtractor(new WordLengthFeatureExtractor()),
				new WordGroupExtractor()
		);
		Cs[0] = 128.0;
		gammas[0] = 0.03125;
		
		arrFeatExtrac[1] = new ComboFeatureExtractor(
				new TaggAdapterFeatureExtractor(new PunctuationMarksExtractor(new File("config/marks.txt"))),
				new TaggAdapterFeatureExtractor(new FunctionWordOccurNumExtractor("config/fwords.txt")),
				new TaggAdapterFeatureExtractor(new WordLengthFeatureExtractor()),
				new WordType3gramsFreqExtractor("config/n-grami-najcesci.txt", false)
		);
		Cs[1] = 128.0;
		gammas[1] = 0.03125;
		
		arrFeatExtrac[2] = new ComboFeatureExtractor(
				new TaggAdapterFeatureExtractor(new PunctuationMarksExtractor(new File("config/marks.txt"))),
				new TaggAdapterFeatureExtractor(new FunctionWordOccurNumExtractor("config/fwords.txt")),
				new TaggAdapterFeatureExtractor(new WordLengthFeatureExtractor()),
				new MorphosyntaticFeatureExtractor(),
				new WordGroupExtractor()
		);
		Cs[2] = 128.0;
		gammas[2] = 0.03125;
		
		arrFeatExtrac[3] = new ComboFeatureExtractor(
				new TaggAdapterFeatureExtractor(new PunctuationMarksExtractor(new File("config/marks.txt"))),
				new TaggAdapterFeatureExtractor(new FunctionWordOccurNumExtractor("config/fwords.txt")),
				new TaggAdapterFeatureExtractor(new WordLengthFeatureExtractor()),
				new MorphosyntaticFeatureExtractor(),
				new WordType3gramsFreqExtractor("config/n-grami-najcesci.txt", false)
		);
		Cs[3] = 128.0;
		gammas[3] = 0.03125;
		
		arrFeatExtrac[4] = new ComboFeatureExtractor(
				new TaggAdapterFeatureExtractor(new PunctuationMarksExtractor(new File("config/marks.txt"))),
				new TaggAdapterFeatureExtractor(new FunctionWordOccurNumExtractor("config/fwords.txt")),
				new TaggAdapterFeatureExtractor(new WordLengthFeatureExtractor()),
				new MorphosyntaticFeatureExtractor(),
				new WordGroupExtractor(),
				new WordType3gramsFreqExtractor("config/n-grami-najcesci.txt", false)
		);
		Cs[4] = 128.0;
		gammas[4] = 0.03125;
		
//		arrFeatExtrac[0] = new WordGroupExtractor();
//		Cs[0] = 512.0;
//		gammas[0] = 2.0;
//		
//		arrFeatExtrac[1] = new WordType3gramsFreqExtractor("config/n-grami-cisti-najcesci.txt", true);
//		Cs[1] = 512.0;
//		gammas[1] = 0.125;
//		
//		arrFeatExtrac[2] = new WordType3gramsFreqExtractor("config/n-grami-najcesci.txt", false);
//		Cs[2] = 512.0;
//		gammas[2] = 0.125;
//		
//		arrFeatExtrac[1] = new MorphosyntaticFeatureExtractor();
//		Cs[1] = 512.0;
//		gammas[1] = 0.125;
//		
//		arrFeatExtrac[2] = new ComboFeatureExtractor(
//				new WordGroupExtractor(),
//				new MorphosyntaticFeatureExtractor()
//		);
//		Cs[2] = 8192.0;
//		gammas[2] = 0.03125;
//		
//		arrFeatExtrac[5] = new ComboFeatureExtractor(
//				new TaggAdapterFeatureExtractor(new FunctionWordOccurNumExtractor("config/fwords.txt")),
//				new MorphosyntaticFeatureExtractor()
//		);
//		Cs[5] = 128.0;
//		gammas[5] = 0.03125;
		
//		arrFeatExtrac[6] = new ComboFeatureExtractor(
//				new TaggAdapterFeatureExtractor(new FunctionWordOccurNumExtractor("config/fwords.txt")),
//				new WordType3gramsFreqExtractor("config/n-grami-cisti-najcesci.txt", true)
//		);
//		Cs[6] = 128.0;
//		gammas[6] = 0.03125;
		
//		arrFeatExtrac[7] = new ComboFeatureExtractor(
//				new TaggAdapterFeatureExtractor(new FunctionWordOccurNumExtractor("config/fwords.txt")),
//				new WordType3gramsFreqExtractor("config/n-grami-cisti-najcesci.txt", false)
//		);
//		Cs[7] = 128.0;
//		gammas[7] = 0.03125;
//		
//		arrFeatExtrac[8] = new ComboFeatureExtractor(
//				new TaggAdapterFeatureExtractor(new FunctionWordTFIDFExtractor("config/fw-idf.txt")),
//				new MorphosyntaticFeatureExtractor()
//		);
//		Cs[8] = 128.0;
//		gammas[8] = 0.03125;
//		
//		arrFeatExtrac[3] = new ComboFeatureExtractor(
//				new WordType3gramsFreqExtractor("config/n-grami-cisti-najcesci.txt", true),
//				new MorphosyntaticFeatureExtractor()
//		);
//		Cs[3] = 128.0;
//		gammas[3] = 0.03125;
//		
//		arrFeatExtrac[10] = new ComboFeatureExtractor(
//				new TaggAdapterFeatureExtractor(new FunctionWordTFIDFExtractor("config/fw-idf.txt")),
//				new MorphosyntaticFeatureExtractor(),
//				new WordGroupExtractor()
//		);
//		Cs[10] = 128.0;
//		gammas[10] = 0.03125;
//		
//		arrFeatExtrac[11] = new ComboFeatureExtractor(
//				new TaggAdapterFeatureExtractor(new PunctuationMarksExtractor(new File("config/marks.txt"))),
//				new TaggAdapterFeatureExtractor(new FunctionWordOccurNumExtractor("config/fwords.txt")),
//				new TaggAdapterFeatureExtractor(new WordLengthFeatureExtractor()),
//				new MorphosyntaticFeatureExtractor()
//		);
//		Cs[11] = 32768.0;
//		gammas[11] = 0.03125;
//		
//		arrFeatExtrac[12] = new ComboFeatureExtractor(
//				new TaggAdapterFeatureExtractor(new FunctionWordOccurNumExtractor("config/fwords.txt")),
//				new MorphosyntaticFeatureExtractor(),
//				new WordGroupExtractor(),
//				new WordType3gramsFreqExtractor("config/n-grami-cisti-najcesci.txt", true)
//		);
//		Cs[12] = 128.0;
//		gammas[12] = 0.03125;
//		
//		arrFeatExtrac[13] = new ComboFeatureExtractor(
//				new TaggAdapterFeatureExtractor(new SentenceBasedFeatureExtractor(20)),
//				new TaggAdapterFeatureExtractor(new PunctuationMarksExtractor(new File("config/marks.txt"))),
//				new TaggAdapterFeatureExtractor(new FunctionWordOccurNumExtractor("config/fwords.txt")),
//				new TaggAdapterFeatureExtractor(new VowelsExtractor()),
//				new TaggAdapterFeatureExtractor(new WordLengthFeatureExtractor()),
//				new MorphosyntaticFeatureExtractor()
//		);
//		Cs[13] = 128.0;
//		gammas[13] = 0.03125;
//		
//		arrFeatExtrac[14] = new ComboFeatureExtractor(
//				new TaggAdapterFeatureExtractor(new SentenceBasedFeatureExtractor(20)),
//				new TaggAdapterFeatureExtractor(new PunctuationMarksExtractor(new File("config/marks.txt"))),
//				new TaggAdapterFeatureExtractor(new FunctionWordOccurNumExtractor("config/fwords.txt")),
//				new TaggAdapterFeatureExtractor(new VowelsExtractor()),
//				new TaggAdapterFeatureExtractor(new WordLengthFeatureExtractor()),
//				new MorphosyntaticFeatureExtractor(),
//				new WordType3gramsFreqExtractor("config/n-grami-cisti-najcesci.txt", true)
//		);
//		Cs[14] = 128.0;
//		gammas[14] = 0.03125;
//		
//		arrFeatExtrac[15] = new ComboFeatureExtractor(
//				new TaggAdapterFeatureExtractor(new SentenceBasedFeatureExtractor(20)),
//				new TaggAdapterFeatureExtractor(new PunctuationMarksExtractor(new File("config/marks.txt"))),
//				new TaggAdapterFeatureExtractor(new FunctionWordOccurNumExtractor("config/fwords.txt")),
//				new TaggAdapterFeatureExtractor(new VowelsExtractor()),
//				new TaggAdapterFeatureExtractor(new WordLengthFeatureExtractor()),
//				new MorphosyntaticFeatureExtractor(),
//				new WordType3gramsFreqExtractor("config/n-grami-cisti-najcesci.txt", true),
//				new WordGroupExtractor()
//		);
//		Cs[15] = 128.0;
//		gammas[15] = 0.03125;
		
		
		String testDataPath = "podatci-skripta/jutarnji-kolumne-arhiva-2010-02-05_clean_tagged.test.xml";
		String trainDataPath = "podatci-skripta/jutarnji-kolumne-arhiva-2010-02-05_clean_tagged.train.xml";
		List<Article> testData = loadTestData(testDataPath);
		for (int i = 0; i < arrFeatExtrac.length; i++) {
			try {
				System.out.println("### FeatureExtractor: " + arrFeatExtrac[i].getName());
				List<FeatureClass> trainData = loadTrainData(trainDataPath, arrFeatExtrac[i]);
				RecognizerTrainer trainer = new LibsvmRecognizer(arrFeatExtrac[i], true, Cs[i], gammas[i]);
				AuthorRecognizer recognizer = trainRecognizer(trainer, trainData);
				testRecognizer(recognizer, testData);
				System.out.println();
				trainer = null;
				recognizer = null;
				trainData = null;
				System.gc();
			} catch (Exception e) {
				System.err.println("Greška\n" + e.getMessage());
			}
		}
	}
	
	public static void findParams(String prefix, String trainDataPath, IFeatureExtractor ... arrFeatExtrac) throws IOException {
		System.out.println("Započelo traženje parametara!");
		for (int i = 0; i < arrFeatExtrac.length; i++) {
			try {
				System.out.println("### FeatureExtractor: " + arrFeatExtrac[i].getName());
				System.out.println("Vrijeme početka: " + new Date());
				List<FeatureClass> trainData = loadTrainData(trainDataPath, arrFeatExtrac[i]);
				LibsvmRecognizer svm = new LibsvmRecognizer(arrFeatExtrac[i], true);
	//			BufferedWriter writer = new BufferedWriter(new FileWriter(prefix+arrFeatExtrac[i].getName()));
				svm.gridSearch(trainData, null);
	//			writer.close();
				System.out.println("Vrijeme završetka: " + new Date());
				System.out.println();
				svm = null;
				trainData = null;
				System.gc();
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
	}

	public static void main(String[] args) throws IOException {
//		IFeatureExtractor featExtrac = null;
//		try {
//			featExtrac = new ComboFeatureExtractor(
//					//new WordType3gramsFreqExtractor("config/n-grami-najcesci.txt", false)
//					//new FunctionWordOccurNumExtractor("config/fwords.txt")
//					new FunctionWordOccurNumExtractor("config/fwords.txt")
//			);
//		} catch (Exception e) {
//			System.err.println("Greška! " + e.getMessage());
//			System.exit(-1);
//		}
		
//		smth();
		//smth2();
		
		List<IFeatureExtractor> featExtractors = createFeatureSets(args[0]);
		int idx = 1;
		for (IFeatureExtractor fe : featExtractors) {
			System.out.println(idx + ": " + fe.getName());
			idx++;
		}
//		RecognizerTrainer trainer = new LibsvmRecognizer(featExtrac, true, 16.0, 0.25);
//		preformExperiment(featExtrac, trainer, "podatci-skripta/blog-hr-aa-arhiva-2010-04-02.short.train.xml", "podatci-skripta/blog-hr-aa-arhiva-2010-04-02.short.test.xml");
		findParams("blogovi_", "podatci-skripta/blog-hr-aa-arhiva-2010-04-02.short.train.xml", featExtractors.toArray(new IFeatureExtractor[0]));
		// Za koristiti ovaj test treba povećati java heap! VM params u runu, npr. -Xms512m -Xmx1024m 
//		IFeatureExtractor fe1 = null;
//		IFeatureExtractor fe2 = null;
//		IFeatureExtractor fe3 = null;
//		IFeatureExtractor fe4 = null;
//		IFeatureExtractor fe5 = null;
//		IFeatureExtractor fe6 = null;
//		
//		try {
//			fe1 = new PunctuationMarksExtractor(new File("config/marks.txt"));
//			fe2 = new FunctionWordOccurNumExtractor("config/fwords.txt");
//			fe3 = new VowelsExtractor();
//			fe4 = new FunctionWordTFIDFExtractor("config/fw-idf.txt");
//			fe5 = new WordLengthFeatureExtractor();
//			fe6 = new FunctionWordGroupFreqExtractor(new File("config/fwords.txt"));
//		} catch (FileNotFoundException e) {
//			System.err.println("Greška! " + e.getMessage());
//			System.exit(-1);
//		}
//		//preformMultiExperiment("podatci-skripta/jutarnji-kolumne-arhiva-2009-11-14.train.xml", "podatci-skripta/jutarnji-kolumne-arhiva-2009-11-14.test.xml",
//		preformMultiExperiment(args[0], args[1],
//				fe1,fe2,fe3,fe4,fe5,fe6,
//				new ComboFeatureExtractor(fe2, fe4),
//				new ComboFeatureExtractor(fe3, fe4),
//				new ComboFeatureExtractor(fe1, fe2, fe4),
//				new ComboFeatureExtractor(fe1, fe2, fe3, fe4),
//				new ComboFeatureExtractor(fe1, fe2, fe5),
//				new ComboFeatureExtractor(fe1, fe3, fe5, fe4),
//				new ComboFeatureExtractor(fe1, fe2, fe3, fe5),
//				new ComboFeatureExtractor(fe1, fe2, fe3, fe4, fe5)
//		);
		
		
//		IFeatureExtractor feCom1 = new ComboFeatureExtractor(fe1, fe3, fe5, fe2);
//		IFeatureExtractor feCom2 = new ComboFeatureExtractor(fe1, fe3, fe5, fe4);
//		findParams("params_", "podatci-skripta/jutarnji-kolumne-arhiva-2009-11-14.train.xml", feCom1, feCom2);
	}
}
