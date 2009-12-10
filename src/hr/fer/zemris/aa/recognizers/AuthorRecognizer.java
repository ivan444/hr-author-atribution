package hr.fer.zemris.aa.recognizers;

/**
 * Prepoznavatelj autora teksta.
 * 
 * @author Ivan Krišto
 */
public interface AuthorRecognizer {
	
	/**
	 * Prepoznavanje autora datog texta.
	 * 
	 * @param text Tekst kojem određujemo autora.
	 * @return Određeni autor teksta.
	 */
	String classifyAutor(String text);
}
