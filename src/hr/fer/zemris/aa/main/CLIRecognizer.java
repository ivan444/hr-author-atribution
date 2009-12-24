package hr.fer.zemris.aa.main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import hr.fer.zemris.aa.features.IFeatureExtractor;
import hr.fer.zemris.aa.features.impl.ComboFeatureExtractor;
import hr.fer.zemris.aa.features.impl.FunctionWordFreqExtractor;
import hr.fer.zemris.aa.recognizers.AuthorRecognizer;
import hr.fer.zemris.aa.recognizers.impl.LibsvmRecognizer;

public class CLIRecognizer {

	/**
	 * @param args <putanja-do-teksta> <putanja-do-modela>
	 */
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Neispravni parametri! <putanja-do-teksta> <putanja-do-modela>");
		}
		// TODO: Zasad je implementacija prepoznavatelja hardkodirana. Ako ih bude više, odhardkodira se.
		// TODO: Staviti odgovarajući featureExt
		IFeatureExtractor featExtrac = new ComboFeatureExtractor(new FunctionWordFreqExtractor(null));
		AuthorRecognizer recognizer = new LibsvmRecognizer(args[1], featExtrac);
		
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(args[0]));
		} catch (FileNotFoundException e) {
			System.err.println("Ne postoji datoteka: " + args[0]);
		}
		
		try {
			String line;
			while (true) {
				line = reader.readLine();
				if (line == null) break;
				sb.append(line).append('\n');
			}
			reader.close();
		} catch (IOException e) {}
		
		System.out.println(recognizer.classifyAutor(sb.toString())); 
	}

}
