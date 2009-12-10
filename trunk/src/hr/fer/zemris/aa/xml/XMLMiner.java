package hr.fer.zemris.aa.xml;

import hr.fer.zemris.aa.features.Article;

import java.io.IOException;
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
	    
		try {
			
			this.document = builder.build(path);
			
	    } catch (JDOMException e) {
			e.printStackTrace();
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
	 * Metoda za dohvaćanje svih članaka određenog autora.
	 * @param author Ime autora
	 * @return Lista svih članaka zadanog autora
	 */
	public List<Article> getArticlesByAuthor(String author){
		
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
				
				Article article = new Article(
						author,
						body.getText(),
						title.getText(),
						getChild(extra, "date").getText()
				);
		
				index.add(article);
			}
			 
		}
		
		return index;
	}
	
	/*
	 * Custom metoda trenutno, optimizacija kasnije... Mozda i nepotrebna
	 * ako XML bude const pa mozemo preko indeksa dohvacat
	 */
	private Element getChild(Element elem, String name){
		
		List<Element> children = elem.getChildren();
		
		for (Element c : children){
	    	  if (c.getName().equals(name)){
	    		  return c;
	    	  }
	    }
		
		return null; //no such element
	}

}
