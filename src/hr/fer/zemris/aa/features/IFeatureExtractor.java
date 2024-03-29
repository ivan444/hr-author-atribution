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
	FeatureVector getFeatures(String text);
	
	/**
	 * 
	 * @return Naziv extractora.
	 */
	String getName();
	
	/**
	 * 
	 * @return Kratki naziv ekstractora (jedno ili dva velika slova).
	 */
	String getShortName();
	
}
