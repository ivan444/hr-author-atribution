package hr.fer.zemris.aa.main;

import hr.fer.zemris.aa.features.FeatureClass;
import hr.fer.zemris.aa.features.FeatureGenerator;
import hr.fer.zemris.aa.features.IFeatureExtractor;
import hr.fer.zemris.aa.features.impl.ComboFeatureExtractor;
import hr.fer.zemris.aa.features.impl.FunctionWordOccurNumExtractor;
import hr.fer.zemris.aa.features.impl.PunctuationMarksExtractor;
import hr.fer.zemris.aa.features.impl.VowelsExtractor;
import hr.fer.zemris.aa.features.impl.WordLengthFeatureExtractor;
import hr.fer.zemris.aa.recognizers.RecognizerTrainer;
import hr.fer.zemris.aa.recognizers.impl.LibsvmRecognizer;
import hr.fer.zemris.aa.xml.XMLMiner;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Command line interface za treniranje prepoznavatelja.
 *
 */
public class CLITrainer {
	
	private static List<FeatureClass> loadTrainData(String trainDataPath, IFeatureExtractor extractor) {
		FeatureGenerator fg = new FeatureGenerator(extractor);
		XMLMiner miner = new XMLMiner(trainDataPath);
		
		return fg.generateFeatureVectors(miner);
	}
	
	/**
	 * 
	 * @param args <train-data-path> <save-model-path>
	 */
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Neispravni parametri! <train-data-path> <save-model-path>");
			System.exit(-1);
		}
		
		IFeatureExtractor featExtrac = null;
		try {
			featExtrac = new ComboFeatureExtractor(
					new PunctuationMarksExtractor(new File("config/marks.txt")),
					new FunctionWordOccurNumExtractor("config/fwords.txt"),
					new VowelsExtractor(),
					new WordLengthFeatureExtractor()
			);
		} catch (FileNotFoundException e) {
			System.err.println("Greška! " + e.getMessage());
			System.exit(-1);
		}
		
		RecognizerTrainer trainer = new LibsvmRecognizer(featExtrac);
		List<FeatureClass> trainData = loadTrainData(args[0], featExtrac);
		trainer.train(trainData, args[1]);
	}
}
