package hr.fer.zemris.aa.features;

/**
 * Izlucivanje znacajki.
 * @author TOMISLAV
 *
 */
public interface IFeatureExtractor {
	
	/**
	 * Metoda koja sluzi za izlucivanje znacajki iz danog teksta.
	 * @param text ulazni tekst iz kojeg se izlucuju znacajke.
	 * @return
	 */
	public FeatureVector getFeatures(String text);
	
}
