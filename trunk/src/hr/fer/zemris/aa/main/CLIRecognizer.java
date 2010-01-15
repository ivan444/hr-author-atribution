package hr.fer.zemris.aa.main;

import hr.fer.zemris.aa.features.IFeatureExtractor;
import hr.fer.zemris.aa.features.impl.ComboFeatureExtractor;
import hr.fer.zemris.aa.features.impl.FunctionWordOccurNumExtractor;
import hr.fer.zemris.aa.features.impl.PunctuationMarksExtractor;
import hr.fer.zemris.aa.features.impl.VowelsExtractor;
import hr.fer.zemris.aa.features.impl.WordLengthFeatureExtractor;
import hr.fer.zemris.aa.recognizers.AuthorRecognizer;
import hr.fer.zemris.aa.recognizers.impl.LibsvmRecognizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CLIRecognizer {

	/**
	 * @param args <putanja-do-teksta> <putanja-do-modela>
	 */
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Neispravni parametri! <putanja-do-teksta> <putanja-do-modela>");
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
		
		AuthorRecognizer recognizer = new LibsvmRecognizer(args[1], featExtrac);
		
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(args[0]));
		} catch (FileNotFoundException e) {
			System.err.println("Ne postoji datoteka: " + args[0]);
			System.exit(-1);
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
		
		System.out.println(recognizer.classifyAuthor(sb.toString())); 
	}

}
