package hr.fer.zemris.aa.main;

import hr.fer.zemris.aa.features.IFeatureExtractor;
import hr.fer.zemris.aa.features.impl.ComboFeatureExtractor;
import hr.fer.zemris.aa.features.impl.SimpleFeatureExtractor;
import hr.fer.zemris.aa.recognizers.AuthorRecognizer;
import hr.fer.zemris.aa.recognizers.RecognizerTrainer;
import hr.fer.zemris.aa.recognizers.impl.LibsvmRecognizer;

/**
 * Command line interface za treniranje prepoznavatelja.
 *
 */
public class CLITrainer {
	public static void main(String[] args) {
		// TODO: Zasad je implementacija prepoznavatelja hardkodirana. Ako ih bude više, odkodira se.
		// TODO: Sve parametre vući iz args-a.
		// TODO: Staviti odgovarajući featureExt
		IFeatureExtractor featExtrac = new ComboFeatureExtractor(new SimpleFeatureExtractor(null));
		RecognizerTrainer trainer = new LibsvmRecognizer(featExtrac);
		// TODO: Učitavanje podataka za treniranje iz datoteke (kao u Experimenteru)
		// TODO: Provjeriti je li savePath datoteka postoji (tj. može li se zapisivati tamo)
		//AuthorRecognizer recog = trainer.train(trainData, savePath);
	}
}
