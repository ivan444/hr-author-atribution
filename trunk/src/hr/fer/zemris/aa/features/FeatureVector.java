package hr.fer.zemris.aa.features;

/**
 * Razred predstavlja vektor značajki. Komponente vektora su različite značajke teksta kojeg
 * reprezentiraju.
 * @author igorbel
 *
 */
public class FeatureVector {
	
	private float[] rawVector;
	private String author, title;
	
	/**
	 * Konstruktor
	 * @param size dimenzija vektora
	 */
	public FeatureVector(int size){
		rawVector = new float[size];
	}
	
	/**
	 * Metoda za dodavanje komponente vektora.
	 * @param index Index komponente
	 * @param value Vrijednost komponente
	 * @return true ako uspjesno
	 */
	public boolean put(int index, float value){
		
		this.rawVector[index] = value;
		
		return true; //FIXME: provjera?
	}
	
	/**
	 * Metoda za dohvaćanje komponente vektora na poziciji index.
	 * @param index Index komponente u vektoru
	 * @return vrijednost komponente na zadanom indexu
	 */
	public float get(int index){
		return this.rawVector[index];
	}
	
	public int getFeaturesDimension() {
		return this.rawVector.length;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("x = [ ");
		for (float xi : this.rawVector) {
			sb.append(xi + " ");
		}
		sb.append("]");
		return sb.toString();
	}

	public void describe(String author, String title) {
		this.author = author;
		this.title = title;	
	}
	
	public String getAuthor(){
		return this.author;
	}
	
	public String getTitle(){
		return this.title;
	}

}
