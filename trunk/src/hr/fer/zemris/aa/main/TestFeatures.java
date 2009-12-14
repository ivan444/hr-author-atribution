package hr.fer.zemris.aa.main;

import java.io.File;
import java.util.Set;

import hr.fer.zemris.aa.features.AdvancedFeatureExtractor;
import hr.fer.zemris.aa.features.FeatureClass;
import hr.fer.zemris.aa.features.FeatureGenerator;
import hr.fer.zemris.aa.features.IFeatureExtractor;
import hr.fer.zemris.aa.features.SimpleFeatureExtractor;
import hr.fer.zemris.aa.xml.XMLMiner;

public class TestFeatures {

	public static void main(String[] args) {
		
		if (args.length != 1) {
			System.err.println("arg[0] = put do xml datoteke!");
			System.exit(99);
		}
		
		XMLMiner miner = new XMLMiner(args[0]);
		IFeatureExtractor extractor = new SimpleFeatureExtractor(new File("config/fwords.txt"));
		IFeatureExtractor extractor2 = new AdvancedFeatureExtractor(new File("config/fwords.txt"));
		
		FeatureGenerator generator = new FeatureGenerator(extractor2);
		
		Set<FeatureClass> r = generator.generateFeatureVectors(miner);
		
		FeatureClass[] classArray = r.toArray(new FeatureClass[0]);
		FeatureClass c = classArray[2];
		
		for (int i = 0; i < c.size(); ++i) {
			System.out.println(c.get(i));
		}
		
	}
	
}
