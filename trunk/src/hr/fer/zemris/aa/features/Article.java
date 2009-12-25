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
	private String name;
	private String url;
	private String columnTitle;
	private String creationDate;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getColumnTitle() {
		return columnTitle;
	}

	public void setColumnTitle(String columnTitle) {
		this.columnTitle = columnTitle;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public SimpleDateFormat getDateFormat() {
		return dateFormat;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("\t<doc name=\"").append(name).append("\">\n");
		sb.append("\t\t<content language=\"hr\">\n");
		sb.append("\t\t\t<title>").append(title.replace("&", "&amp;").replace("\"", "&quot;").replace("'", "&apos;")).append("</title>\n");
		sb.append("\t\t\t<body>").append(text.replace("&", "&amp;").replace("\"", "&quot;").replace("'", "&apos;")).append("</body>\n");
		sb.append("\t\t</content>\n\n");
		sb.append("\t\t<extraInfo>\n");
		sb.append("\t\t\t<author>").append(author).append("</author>\n");
		sb.append("\t\t\t<date>").append(dateFormat.format(date)).append("</date>\n");
		sb.append("\t\t\t<url>").append(url).append("</url>\n");
		sb.append("\t\t\t<columntitle>").append(columnTitle).append("</columntitle>\n");
		sb.append("\t\t\t<creation-date>").append(creationDate).append("</creation-date>\n");
		sb.append("\t\t</extraInfo>\n\n");
		sb.append("\t</doc>\n\n");
		
		return sb.toString();
	}

}
