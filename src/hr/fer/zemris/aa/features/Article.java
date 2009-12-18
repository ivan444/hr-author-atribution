package hr.fer.zemris.aa.features;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Klasa predstavlja jedan članak određenog autora, sa svojim
 * naslovom, tekstom i datumom.
 * @author igorbel
 */
public class Article implements Comparable<Article>{
	
	private final String author;
	private final String text;
	private final String title;
	private final Date date;
	
	private final String DATE_FORMAT = "dd.MM.yyyy";
	
	public Article(String auth, String text, String title, Date date){
		this.author = auth;
		this.text = text;
		this.date = date;
		this.title = title;
	}
	
	//FIXME: izbaciti funkcionalnost string formatiranja datuma iz ove klase u XMLMiner
	public Article(String auth, String text, String title, String date) {
		this.author = auth;
		this.text = text;
		this.title = title;
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		
		Date tempDate;
		try {
			tempDate = dateFormat.parse(date);
		} catch (ParseException e) {
			System.err.println("Greska kod parsiranja clanka: " + auth + " - \"" + title + "\"");
			System.err.println("Pogresno zadan datum: " + date);
			tempDate = new Date(1);
			System.err.println("UPOZORENJE: datum clanka postavljen na 1.1.1970.");
		}
		
		this.date = tempDate;
	}

	public String getAuthor() {
		return author;
	}

	public String getText() {
		return text;
	}

	public Date getDate() {
		return date;
	}
	
	public String getDateString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		return dateFormat.format(this.date);
	}
	
	public String getTitle() {
		return title;
	}

	@Override
	public int compareTo(Article other) {
		
		return this.date.compareTo(other.date);
		
	}

}
