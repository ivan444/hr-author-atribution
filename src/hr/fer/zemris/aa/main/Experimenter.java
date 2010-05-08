package hr.fer.zemris.aa.main;

import hr.fer.zemris.aa.features.Article;
import hr.fer.zemris.aa.features.FeatureClass;
import hr.fer.zemris.aa.features.FeatureGenerator;
import hr.fer.zemris.aa.features.IFeatureExtractor;
import hr.fer.zemris.aa.features.impl.ComboFeatureExtractor;
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
import java.text.ParseException;
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
			ComboFeatureExtractor feat = createFeature(line, sufix);
			if (feat == null) continue;
			else allFeatures.add(feat);
		}
		
		return allFeatures;
	}
	
	/**
	 * Stvaranje skupova za testiranje po konfig datoteci.
	 * @param configPath
	 * @return
	 * @throws FileNotFoundException
	 * @throws ParseException 
	 */
	public static List<TestSet> createTestSets(String configPath) throws FileNotFoundException, ParseException {
		Scanner s = new Scanner(new FileInputStream(configPath));
		String sufix = null;
		int lineNum = 0;
		while (s.hasNextLine()) {
			String line = s.nextLine();
			lineNum++;
			if (line.startsWith("#")) continue;
			else if (line.startsWith("!") && line.length() > 1) {
				sufix = line.substring(1);
				break;
			} else {
				System.err.println("Sufiks nije definiran!");
				System.exit(-1);
			}
		}
		
		List<TestSet> allTests = new LinkedList<TestSet>();
		while (s.hasNextLine()) {
			String line = s.nextLine();
			lineNum++;
			line = line.trim();
			if (line.equals("")) continue;
			String[] parts = line.split(" ");
			if (parts.length != 3) throw new ParseException("Konfig datoteka je neispravna!", lineNum);
			
			ComboFeatureExtractor feat = createFeature(parts[0], sufix);
			if (feat == null) throw new ParseException("Konfig datoteka je neispravna (ne postoji značajka)!", lineNum);
			
			double c = Double.parseDouble(parts[1]);
			double gamma = Double.parseDouble(parts[2]);
			
			allTests.add(new TestSet(c, gamma, feat));
		}
		
		return allTests;
	}
	
	/**
	 * Stvaranje vektora značajki iz konfiguracijskog stringa (jedna linija).
	 * 
	 * @param line Linija s opisom vektora (npr. P,F).
	 * @param sufix Sufiks skupa za treniranje (npr. _blogovi)
	 * @return null ako je konfiguracijski string prazan, inače stvorena (Combo) značajka.
	 * @throws FileNotFoundException Ukoliko neka konfig datoteka nedostaje.
	 */
	private static ComboFeatureExtractor createFeature(String line, String sufix) throws FileNotFoundException {
		line = line.trim();
		if (line.equals("")) return null;
		
		String[] strFeat = line.split(",");
		IFeatureExtractor[] features = new IFeatureExtractor[strFeat.length];
		
		boolean tagged = false;
		for (int i = 0; i < strFeat.length; i++) {
			if (strFeat[i].equals("M") || strFeat[i].equals("N1") || strFeat[i].equals("N2") || strFeat[i].equals("C")) {
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
				features[i] = new WordGroupExtractor();
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
		
		return new ComboFeatureExtractor(features);
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
	
	/**
	 * Izvođenje eksperimenata iz pripremljenih test setova.
	 * 
	 * @param testSets
	 * @param trainDataPath
	 * @param testDataPath
	 */
	public static void preformTestSetsExperiments(List<TestSet> testSets, String trainDataPath, String testDataPath) {
		int idx = 1;
		for (TestSet testSet : testSets) {
			System.out.println(idx + ": " + testSet);
			idx++;
		}
		
		List<Article> testData = loadTestData(testDataPath);
		for (int i = 0; i < testSets.size(); i++) {
			try {
				testSets.get(i).runTest(trainDataPath, testData);
				testSets.set(i, null);
				System.gc();
			} catch (Exception e) {
				System.err.println("Greška\n" + e.getMessage());
			}
		}
	}
	
	/**
	 * Cross-validacija - traženje najboljih parametara C i gamma za skup koji se nalazi na {@code trainDataPathu}.
	 *  
	 * @param prefix Prefiks datoteke za ispis.
	 * @param trainDataPath
	 * @param arrFeatExtrac
	 * @throws IOException
	 */
	public static void findParams(String prefix, String trainDataPath, IFeatureExtractor ... arrFeatExtrac) throws IOException {
		for (int i = 0; i < arrFeatExtrac.length; i++) {
			System.out.println((i+1) + ": " + arrFeatExtrac[i].getName());
		}
		
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

	public static void main(String[] args) throws IOException, ParseException {
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
		
//		List<IFeatureExtractor> featExtractors = createFeatureSets(args[0]);
//		RecognizerTrainer trainer = new LibsvmRecognizer(featExtrac, true, 16.0, 0.25);
//		preformExperiment(featExtrac, trainer, "podatci-skripta/blog-hr-aa-arhiva-2010-04-02.short.train.xml", "podatci-skripta/blog-hr-aa-arhiva-2010-04-02.short.test.xml");
//		findParams("blogovi_", "podatci-skripta/blog-hr-aa-arhiva-2010-04-02.short.train_tagged.xml", featExtractors.toArray(new IFeatureExtractor[0]));
		
		List<TestSet> testSets = createTestSets(args[0]);
		preformTestSetsExperiments(testSets, args[1], args[2]);
	}
	
	/**
	 * Opisnik testa.
	 */
	private static class TestSet {
		final private double C;
		final private double gamma;
		final private IFeatureExtractor featExtractor;
		
		public TestSet(double c, double gamma, IFeatureExtractor featExtractor) {
			C = c;
			this.gamma = gamma;
			this.featExtractor = featExtractor;
		}
		
//		public void runTest(String trainDataPath, String testDataPath) {
//			List<Article> testData = loadTestData(testDataPath);
//			runTest(trainDataPath, testData);
//		}
		
		public void runTest(String trainDataPath, List<Article> testData) {
			System.out.println("### FeatureExtractor: " + featExtractor.getName());
			List<FeatureClass> trainData = loadTrainData(trainDataPath, featExtractor);
			RecognizerTrainer trainer = new LibsvmRecognizer(featExtractor, true, C, gamma);
			AuthorRecognizer recognizer = trainRecognizer(trainer, trainData);
			testRecognizer(recognizer, testData);
			System.out.println(featExtractor.getShortName());
			System.out.println();
		}
		
		public String toString() {
			return featExtractor.getShortName() + " C:" + C + " gamma:" + gamma;
		}
	}
}
