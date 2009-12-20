package hr.fer.zemris.aa.features;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Razred predstavalja klasu vektora značajki.
 * Klasa sadrži sve vektore sa značajkama jednog autora.
 * @author igorbel
 *
 */
public class FeatureClass implements Iterable<FeatureVector> {
	
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

	@Override
	public Iterator<FeatureVector> iterator() {
		return new Iterator<FeatureVector>() {
			int current = -1;
			boolean nextCalled = false;
			
			@Override
			public void remove() {
				if (nextCalled) {
					throw new IllegalStateException("Još ni jednom nije pozvan next() nad ovim iteratorom!");
				}
				featureVectors.remove(current);
				current--;
			}
			
			@Override
			public FeatureVector next() {
				if (!hasNext()) {
					throw new NoSuchElementException("Nema više elemenata!");
				}
				nextCalled = true;
				current++;
				return get(current);
			}
			
			@Override
			public boolean hasNext() {
				return current+1 < size();
			}
		};
	}
}
