package hr.fer.zemris.aa.main;

import java.util.List;

import hr.fer.zemris.aa.features.FeatureClass;
import hr.fer.zemris.aa.features.FeatureGenerator;
import hr.fer.zemris.aa.features.IFeatureExtractor;
import hr.fer.zemris.aa.features.impl.ComboFeatureExtractor;
import hr.fer.zemris.aa.features.impl.SimpleFeatureExtractor;
import hr.fer.zemris.aa.recognizers.RecognizerTrainer;
import hr.fer.zemris.aa.recognizers.impl.LibsvmRecognizer;
import hr.fer.zemris.aa.xml.XMLMiner;

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
		// TODO: Zasad je implementacija prepoznavatelja hardkodirana. Ako ih bude više, odhardkodira se.
		// TODO: Provjera argsa!
		// TODO: Staviti odgovarajući featureExt
		IFeatureExtractor featExtrac = new ComboFeatureExtractor(new SimpleFeatureExtractor(null));
		RecognizerTrainer trainer = new LibsvmRecognizer(featExtrac);
		List<FeatureClass> trainData = loadTrainData(args[0], featExtrac);
		// TODO: Provjeriti je li savePath datoteka postoji (tj. može li se zapisivati tamo)
		trainer.train(trainData, args[1]);
	}
}
