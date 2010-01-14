package hr.fer.zemris.aa.tests;

import hr.fer.zemris.aa.features.FeatureClass;
import hr.fer.zemris.aa.features.FeatureGenerator;
import hr.fer.zemris.aa.features.IFeatureExtractor;
import hr.fer.zemris.aa.features.impl.ComboFeatureExtractor;
import hr.fer.zemris.aa.features.impl.FunctionWordTFIDFExtractor;
import hr.fer.zemris.aa.features.impl.PunctuationMarksExtractor;
import hr.fer.zemris.aa.features.impl.VowelsExtractor;
import hr.fer.zemris.aa.xml.XMLMiner;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class TestFeatures {

	public static void main(String[] args) throws FileNotFoundException {
		
		if (args.length != 1) {
			System.err.println("arg[0] = put do xml datoteke!");
			System.exit(99);
		}
		
		double mb = 1024*1024.0;
		Runtime runtime = Runtime.getRuntime();
		double memBefore = (runtime.totalMemory() - runtime.freeMemory()) / mb;
		long startTime = System.currentTimeMillis();
		
		XMLMiner miner = new XMLMiner(args[0]);
		//IFeatureExtractor extractor = new SimpleFeatureExtractor(new File("config/fwords.txt"));
		//IFeatureExtractor extractor2 = new FunctionWordOccurNumExtractor("config/fwords.txt");
		IFeatureExtractor extractor2 = new ComboFeatureExtractor(
				new PunctuationMarksExtractor(new File("config/marks.txt")),
				new VowelsExtractor(),
				new FunctionWordTFIDFExtractor("config/fw-idf.txt")
		);
		
		FeatureGenerator generator = new FeatureGenerator(extractor2);
		
		List<FeatureClass> r = generator.generateFeatureVectors(miner);
		
		FeatureClass[] classArray = r.toArray(new FeatureClass[0]);
		FeatureClass c = classArray[2];
		
		for (int i = 0; i < c.size(); ++i) {
			System.out.println(c.get(i));
		}
		
		long endTime = System.currentTimeMillis();
		double memAfter = (runtime.totalMemory() - runtime.freeMemory()) / mb;
		
		System.out.println("\n\n----------------------------------");
		System.out.println("Execution time: " + ((endTime - startTime)/1000.0) + " sec");
		System.out.println("Memory used: " + (memAfter - memBefore) + " MB\n");
	}
	
}
