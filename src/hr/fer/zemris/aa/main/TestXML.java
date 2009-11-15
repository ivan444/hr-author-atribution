package hr.fer.zemris.aa.main;

import hr.fer.zemris.aa.features.Article;
import hr.fer.zemris.aa.xml.XMLMiner;

import java.util.List;

public class TestXML {

	public static void main(String[] args) {
		//putanja trenutno arg[0], nisam htio staviti u projekt XML file jer je ogroman pa
		//da ne opterecujemo svn, sami si upisite gdje ga drzite...
		
		if (args.length != 1) {
			System.err.println("arg[0] = put do xml datoteke!");
			System.exit(99);
		}
	
		XMLMiner littleChineseGuy = new XMLMiner(args[0]);
		List<Article> lista = littleChineseGuy.getArticlesByAuthor("Branimir Pofuk");//("Živko Kustić");
		
		for (Article a : lista){
			System.out.println();
			System.out.println(a.getAuthor() + ": \"" + a.getTitle() + "\" [" + a.getDate() + "]");
			System.out.println("---------------------------------------------");
			System.out.println(a.getText());
			System.out.println();
		}

	}

}
