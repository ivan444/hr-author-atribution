package hr.fer.zemris.aa.main;

import hr.fer.zemris.aa.features.IFeatureExtractor;
import hr.fer.zemris.aa.features.impl.ComboFeatureExtractor;
import hr.fer.zemris.aa.features.impl.SimpleFeatureExtractor;
import hr.fer.zemris.aa.recognizers.AuthorRecognizer;
import hr.fer.zemris.aa.recognizers.impl.LibsvmRecognizer;

public class CLIRecognizer {

	/**
	 * @param args <putanja-do-teksta> <putanja-do-modela>
	 */
	public static void main(String[] args) {
		// TODO: Zasad je implementacija prepoznavatelja hardkodirana. Ako ih bude više, odkodira se.
		// TODO: Sve parametre vući iz args-a.
		// TODO: Staviti odgovarajući featureExt
		IFeatureExtractor featExtrac = new ComboFeatureExtractor(new SimpleFeatureExtractor(null));
		//AuthorRecognizer recognizer = new LibsvmRecognizer(modelPath, featExtrac)
		// TODO: Text je također datoteka!
		//System.out.println(recognizer.classifyAutor(text)); 
	}

}
