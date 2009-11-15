package hr.fer.zemris.aa.features;

/**
 * Klasa predstavlja jedan članak određenog autora, sa svojim
 * naslovom, tekstom i datumom.
 * @author igorbel
 */
public class Article {
	
	private final String author;
	private final String text;
	private final String date;
	private final String title;
	
	public Article(String auth, String text, String title, String date){
		this.author = auth;
		this.text = text;
		this.date = date;
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public String getText() {
		return text;
	}

	public String getDate() {
		return date;
	}
	
	public String getTitle() {
		return title;
	}

}
