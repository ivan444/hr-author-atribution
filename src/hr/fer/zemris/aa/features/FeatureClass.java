package hr.fer.zemris.aa.features;

import java.util.ArrayList;
import java.util.List;

/**
 * Razred predstavalja klasu vektora značajki.
 * Klasa sadrži sve vektore sa značajkama jednog autora.
 * @author igorbel
 *
 */
public class FeatureClass {
	
	private List<FeatureVector> featureVectors;
	
	/**
	 * konstruktor
	 * @param size broj vektora u klasi
	 */
	public FeatureClass(int size){
		this.featureVectors = new ArrayList<FeatureVector>(size);
	}
	
	/**
	 * Metoda za dodavanje vektora u klasu
	 * @param x vektor za dodati
	 * @return true ako uspješno dodan
	 */
	public boolean add(FeatureVector x){
		return this.featureVectors.add(x);
	}
	
	/**
	 * Metoda za dohvačanje vektora
	 * @param index pozicija vektora
	 * @return vektor značajki na poziciji index
	 */
	public FeatureVector get(int index){
		return this.featureVectors.get(index);
	}
	
	/**
	 * Metoda za dohvat veličine klase vektora značajki
	 * @return broj vektora u klasi
	 */
	public int size(){
		return this.featureVectors.size();
	}
}
