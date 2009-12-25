package hr.fer.zemris.aa.xml;

import hr.fer.zemris.aa.features.Article;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Razred za izvlačenje podataka iz XML-a.
 * @author igorbel
 */
public class XMLMiner {
	
	private Document document;
	
	public XMLMiner(String path){
		SAXBuilder builder = new SAXBuilder();
	    
		// TODO: Možda malo drugačije izvesti ovu obradu pogreške...
		try {
			this.document = builder.build(path);
	    } catch (JDOMException e) {
	    	System.err.println("Neispravan XML zapis datoteke: " + path);
		} catch (IOException e) {
			System.err.println("Neuspješno otvaranje datoteke: " + path);
		}
	}
	
	public Set<String> getAuthors(){
		Set<String> authors = new HashSet<String>();
		
		Element root = this.document.getRootElement();
		List<Element> children = root.getChildren();
		
		for (Element doc : children){
			
			Element author = getChild(getChild(doc, "extraInfo"), "author");
			authors.add(author.getText());
			
		}
		
		return authors;
	}
	
	/**
	 * Metoda za dohvaćanje svih članaka iz datoteke određene sa {@code filePath}.
	 * @param filePath Putanja do datoteke sa člancima.
	 * @return Lista svih članaka iz datoteke {@code filePath}.
	 */
	public static List<Article> getArticles(String filePath) throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		Document document = null;
		
		document = builder.build(filePath);
		
		Element root = document.getRootElement();
		List<Element> children = root.getChildren();
		List<Article> index = new LinkedList<Article>();
	    
		for (Element doc : children){

			Element extra = getChild(doc, "extraInfo");
			//if (extra == null) continue; 
			
			Element a = getChild(extra, "author");
			//if (a == null) continue;
			
		
			Element upperDoc = a.getParentElement().getParentElement();
			Element content = (Element)upperDoc.getChildren().get(0);
			Element title = getChild(content, "title");
			Element body = getChild(content, "body");
			String date = getChild(extra, "date").getText();
			
			//ovo je fix za "prije x sati":
			if (date.startsWith("prije")){
				String created = getChild(extra, "creation-date").getText();
				String[] dateArray = created.split("-");
				date = dateArray[2] + "." + dateArray[1] + "." + dateArray[0];
			}
			
			Article article = new Article(
					a.getText(),
					body.getText(),
					title.getText(),
					date
			);
	
			index.add(article);
		}
		
		return index;
	}
	
	/**
	 * Metoda za dohvaćanje svih članaka određenog autora.
	 * @param author Ime autora
	 * @return Lista svih članaka zadanog autora
	 */
	public List<Article> getArticlesByAuthor(String author) {
		return getArticlesByAuthor(author, false);
	}
	
	/**
	 * Metoda za dohvaćanje svih članaka određenog autora.
	 * @param author Ime autora
	 * @param allInfo True ako želimo staviti sve informacije XML-a u sam article.
	 * @return Lista svih članaka zadanog autora
	 */
	public List<Article> getArticlesByAuthor(String author, boolean allInfo) {
		
		Element root = this.document.getRootElement();
		List<Element> children = root.getChildren();
		List<Article> index = new LinkedList<Article>();
	    
		for (Element doc : children){

			Element extra = getChild(doc, "extraInfo");
			//if (extra == null) continue; 
			
			Element a = getChild(extra, "author");
			//if (a == null) continue;
			
			if (a.getText().equals(author)){
		
				Element upperDoc = a.getParentElement().getParentElement();
				Element content = (Element)upperDoc.getChildren().get(0);
				Element title = getChild(content, "title");
				Element body = getChild(content, "body");
				String date = getChild(extra, "date").getText();
				
				//ovo je fix za "prije x sati":
				if (date.startsWith("prije")){
					String created = getChild(extra, "creation-date").getText();
					String[] dateArray = created.split("-");
					date = dateArray[2] + "." + dateArray[1] + "." + dateArray[0];
				}
				
				Article article = new Article(
						author,
						body.getText(),
						title.getText(),
						date
				);
				
				if (allInfo) {
					article.setName(upperDoc.getAttributeValue("name"));
					article.setUrl(getChild(extra, "url").getText());
					article.setColumnTitle(getChild(extra, "columntitle").getText());
					article.setCreationDate(getChild(extra, "creation-date").getText());
				}
		
				index.add(article);
			}
			 
		}
		
		return index;
	}
	
	/*
	 * Custom metoda trenutno, optimizacija kasnije... Mozda i nepotrebna
	 * ako XML bude const pa mozemo preko indeksa dohvacat
	 */
	@SuppressWarnings("unchecked")
	private static Element getChild(Element elem, String name){
		
		List<Element> children = (List<Element>) elem.getChildren();
		
		for (Element c : children){
	    	  if (c.getName().equals(name)){
	    		  return c;
	    	  }
	    }
		
		return null; //no such element
	}
	
	/**
	 * Metoda razdvaja listu članaka na n najnovijih i ostatak starijih
	 * @param n broj najnovijih clanaka
	 * @param articles lista svih članaka
	 * @return niz od dvije liste: na prvom mjestu lista starijih, a na drugom mjestu n najnovijih članaka
	 */
	public  List[] split(int n,  List<Article> articles){
		
		if (n > articles.size()) throw new IllegalArgumentException("n veci od velicine liste! To nije OK.");
		
		List<Article> newest = new LinkedList<Article>();
		List<Article> older = new LinkedList<Article>();
		Collections.sort(articles);
		
		int upperLimit = articles.size()-1;
		int boundryLimit = upperLimit - n;
		
		for (int i = 0; i <= upperLimit; i++) {
			
			if (i <= boundryLimit){
				older.add(articles.get(i));
			} else {
				newest.add(articles.get(i));
			}
			
		}
		
		//FIXME: Ovdje mi neda da stavim generic list u array, ovo
		//se vjerojatno moze lijepse
		List[] ret = {older, newest}; 
		return ret;
	}
	
	/**
	 * Metoda razdvaja listu članaka na P posto najnovijih i ostatak starijih
	 * @param P postotak najnovijih clanaka
	 * @param articles lista svih članaka
	 * @return niz od dvije liste: na prvom mjestu lista starijih, a na drugom mjestu P posto najnovijih članaka
	 */
	public  List[] split(double P,  List<Article> articles){
		
		if ((P > 1.0)||(P < 0.0)) throw new IllegalArgumentException("P mora biti iz intervala [0,1]!");
		
		int n = (int) (P * articles.size());
		if (n < 1) n = 1; //minimalno uzimamo 1 clanak
		
		return this.split(n, articles);
	}
	
	/**
	 * Metoda za dohvat N najnovijih članaka
	 * @param n broj najnovijih članaka
	 * @param articles lista članaka, ne nužno sortirana
	 * @return lista N najnovijih članaka
	 */
	@Deprecated
	public  List<Article> getNewest(int n,  List<Article> articles){
		
		if (n > articles.size()) throw new IllegalArgumentException("N veci od velicine liste!");
		
		Collections.sort(articles);
		List<Article> newest = new LinkedList<Article>();
		
		int upperLimit = articles.size()-1;
		int lowerLimit = upperLimit - n;
		
		for (int i = upperLimit; i > lowerLimit; i--) {
			newest.add(articles.get(i));
		}
		
		return newest;
	}
	
	/**
	 * Metoda za dohvat P posto najnovijih članaka
	 * @param n postotak najnovijih članaka
	 * @param articles lista članaka, ne nužno sortirana
	 * @return lista P posto najnovijih članaka
	 */
	@Deprecated
	public  List<Article> getNewest(double P,  List<Article> articles){
		
		if ((P > 1.0)||(P < 0.0)) throw new IllegalArgumentException("P mora biti iz intervala [0,1]!");
		if (P == 1) return articles; // 100% clanaka, nema potrebe za daljnjim igranjem
		
		int n = (int) (P * articles.size());
		if (n < 1) n = 1; //minimalno uzimamo 1 clanak
		
		return this.getNewest(n, articles);
	}
	

}
