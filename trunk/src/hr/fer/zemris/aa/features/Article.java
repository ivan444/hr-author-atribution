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

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
	
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
		
		Date tempDate;
		try {
			tempDate = this.dateFormat.parse(date);
		} catch (ParseException e) {
			//silent fail - da program ne pukne stavlja se default datum
			System.err.println("Greska kod parsiranja clanka: " + auth + " - \"" + title + "\"");
			System.err.println("Pogresno zadan datum: " + date);
			tempDate = new Date(1);
			System.err.println("UPOZORENJE: datum clanka postavljen na 1.1.1970.");
		}
		
		this.date = tempDate;
	}

	public String getAuthor() {
		return this.author;
	}

	public String getText() {
		return this.text;
	}
	
	public Date getDate(){
		return this.date;
	}
	
	public String getDateString() {
		return this.dateFormat.format(this.date);
	}
	
	public String getTitle() {
		return title;
	}
	
	@Override
	public int compareTo(Article other) {
		
		return this.date.compareTo(other.getDate());
		
	}

	

}
