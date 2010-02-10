package hr.fer.zemris.aa.tests;

import java.util.List;

import hr.fer.zemris.aa.features.Article;
import hr.fer.zemris.aa.features.Descriptor;
import hr.fer.zemris.aa.features.TextStatistics;
import hr.fer.zemris.aa.xml.XMLMiner;

public class TestDescriptor {
	
	public static void main(String[] args) throws Exception {
		
		List<Article> list = XMLMiner.getArticles("podatci-skripta/jutarnji-kolumne-arhiva-2010-02-05_clean_tagged.xml");
		
		String[] words = null;
		for (Article a : list) {
			words = a.getText().split(" ");
			for (String x : words) {
				if (TextStatistics.cleanNew(x).length()==0)
					continue ;
				try {
					Descriptor.parse(TextStatistics.getDescription(x));
				} catch (Exception e) {
					throw new Exception(x, e);
				}
			}
		}
		
	}
}
