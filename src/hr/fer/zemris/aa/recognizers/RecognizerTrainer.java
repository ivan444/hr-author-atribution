package hr.fer.zemris.aa.recognizers;

import hr.fer.zemris.aa.features.FeatureClass;

import java.util.List;

/**
 * 
 * @author Ivan Krišto
 *
 */
public interface RecognizerTrainer {
	
	/**
	 * Treniranje klasifikatora (prepoznavatelja autora) nad skupom podataka za učenje.
	 * 
	 * @param trainData Skup podataka za učenje.
	 * @return Naučeni klasifikator (prepoznavatelj autora).
	 */
	AuthorRecognizer train(List<FeatureClass> trainData);
	
	/**
	 * Treniranje klasifikatora (prepoznavatelja autora) nad skupom podataka za učenje
	 * i spremanje naučenog klasifikatora na disk.
	 * 
	 * @param trainData Skup podataka za učenje.
	 * @param savePath Putanja na koju spremamo naučeni klasifikator.
	 * @return Naučeni klasifikator (prepoznavatelj autora).
	 */
	AuthorRecognizer train(List<FeatureClass> trainData, String savePath);
}
