package hr.fer.zemris.aa.features;

/**
 * Izlučivanje značajki.
 * @author TOMISLAV
 *
 */
public interface IFeatureExtractor {
	
	/**
	 * Izlučivanje značajki iz danog teksta.
	 * 
	 * @param text ulazni tekst iz kojeg se izlučuju značajke.
	 * @return Vektor značajki danog teksta.
	 */
	public FeatureVector getFeatures(String text);
	
}
