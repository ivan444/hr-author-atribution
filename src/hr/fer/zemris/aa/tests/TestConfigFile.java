package hr.fer.zemris.aa.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class TestConfigFile {

	public static void main(String[] args) {
		
		File inputFile = new File("config/fwords.txt");
		Map<String, Set<String>> fWords = new LinkedHashMap<String, Set<String>>();
		
		try {
			
			BufferedReader bufferedReader = new BufferedReader(
					new FileReader(inputFile));

			
			String line = null;
			String wordType = null;
			Set<String> wordSet = null;
			

			while ((line = bufferedReader.readLine()) != null) {
				
				line = line.trim().toLowerCase();
				
				if (line.length() == 0 || line.startsWith("#"))
					continue;
				
				if (line.startsWith(".")) {
					if (wordType != null && wordSet != null)
						fWords.put(wordType, wordSet);
					
					if (line.length() != 1) {
						wordType = line.substring(1);
						wordSet = new HashSet<String>();
					}
				}
				else {
					if (wordSet.contains(line))
						System.out.println("DUPLIKAT unutar grupe "+wordType+": "+line);
					wordSet.add(line);
				}
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		Set<String> tmp;

		for (String x : fWords.keySet()) {
			for (String y : fWords.keySet())
				if (!x.equals(y)) {
					tmp = new HashSet<String>(fWords.get(x));
					tmp.retainAll(fWords.get(y));
					if (tmp.size() != 0)
						System.out.println("DUPLIKATI između grupa "+x+", "+y+": "+tmp);
				}
		}
	}
	
}
