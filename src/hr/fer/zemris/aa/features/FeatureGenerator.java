package hr.fer.zemris.aa.features;

import hr.fer.zemris.aa.xml.XMLMiner;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class FeatureGenerator {
	
	private IFeatureExtractor extractor;
	
	public FeatureGenerator(IFeatureExtractor extractor){
		this.extractor = extractor;
	}
	
	public List<FeatureClass> generateFeatureVectors(XMLMiner littleChineseGuy){
		
		Set<String> authors = littleChineseGuy.getAuthors();
		List<FeatureClass> allClasses = new LinkedList<FeatureClass>();
		
		for (String author : authors) {
			
			List<Article> lista = littleChineseGuy.getArticlesByAuthor(author);
			FeatureClass omega = new FeatureClass(lista.size());
			
			for (Article article : lista) {
				FeatureVector xi = extractor.getFeatures(article.getText());
				xi.describe(article.getAuthor(), article.getTitle());
				omega.add(xi);
			}
			
			allClasses.add(omega);
		}
		
		return allClasses;
	}
}
