package hr.fer.zemris.aa.features;

import hr.fer.zemris.aa.features.Descriptor.Tag;
import hr.fer.zemris.aa.xml.XMLMiner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.eaio.stringsearch.BoyerMooreHorspoolRaita;
import com.eaio.stringsearch.StringSearch;


/**
 * Pomoćni razred. Razne statistike dokumenta.
 * 
 * @author Ivan Krišto
 */
public class TextStatistics implements Iterable<String> {
	
	private List<String> words;
	private Set<String> lowerCasedWordsSet;
	private final int quotesNum;
	private final int aposNum;
	private final int dotsNum;
	private final int questionMarksNum;
	private final int exclamNum;
	private final int numbersNum;
	private Map<String, Float> wordFreqL2;
	private Map<String, Float> wordFreqLog;
	
	/** Vowels: a,e,i,o,u */
	private final int[] vowelsNum;
	
	public TextStatistics(String text) {
		Pattern quotes = Pattern.compile("\"");
		Pattern apos = Pattern.compile("'");
		Pattern dots = Pattern.compile("\\.");
		Pattern questionMarks = Pattern.compile("\\?");
		Pattern exclams = Pattern.compile("!");
		Pattern numbers = Pattern.compile("\\d+");
		
		vowelsNum = new int[5];
		int textLen = text.length();
		for (int i = 0; i < textLen; i++) {
			char ch = Character.toLowerCase(text.charAt(i));
			if (ch == 'a') vowelsNum[0]++;
			else if (ch == 'e') vowelsNum[1]++;
			else if (ch == 'i') vowelsNum[2]++;
			else if (ch == 'o') vowelsNum[3]++;
			else if (ch == 'u') vowelsNum[4]++;
		}
		
		extractWords(text);
		this.quotesNum = occurNum(text, quotes);
		this.aposNum = occurNum(text, apos);
		this.dotsNum = occurNum(text, dots);
		this.questionMarksNum = occurNum(text, questionMarks);
		this.exclamNum = occurNum(text, exclams);
		this.numbersNum = occurNum(text, numbers);
		
		lowerCasedWordsSet = new HashSet<String>();
		for (String word : words) {
			lowerCasedWordsSet.add(word.toLowerCase());
		}
		
		calcWordsFrequencys();
	}
	
	public static Map<String, Float> calcIdf(String wordsFilePath, List<Article> arhive) throws FileNotFoundException {
		Map<String, Float> idf = new HashMap<String, Float>();
		List<String> words = listWords(wordsFilePath);
		int[] wordsOcc = new int[words.size()];
		int documentsNum = arhive.size();
		StringSearch so = new BoyerMooreHorspoolRaita();
		
		for (Article art : arhive) {
			String text = art.getText().toLowerCase();
			for (int i = 0; i < wordsOcc.length; i++) {
				if (so.searchString(text, words.get(i)) != -1) {
					wordsOcc[i]++;
				}
			}
		}
		
		for (int i = 0; i < wordsOcc.length; i++) {
			float idfVal = (float) Math.log(documentsNum/(wordsOcc[i]+1.0));
			idf.put(words.get(i), Float.valueOf(idfVal));
		}
		
		return idf;
	}
	
	public static Map<String, Float> calcIdf(String wordsFilePath, List<Article> arhive, String idfSavePath)
			throws FileNotFoundException {
		Map<String, Float> fwIdf = calcIdf(wordsFilePath, arhive);
		
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(idfSavePath));
			for (String word : fwIdf.keySet()) {
				writer.write(word + "\t" + fwIdf.get(word) + "\n");
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return fwIdf;
	}
	
	public static Map<String, Float> readIdf(String idfFilePath) throws FileNotFoundException {
		Map<String, Float> idf = new HashMap<String, Float>();
		BufferedReader reader = new BufferedReader(new FileReader(idfFilePath));
		String line = null;
		try {
			while (true) {
				line = reader.readLine();
				if (line == null || line.equals("")) break;
				String[] parts = line.trim().split("\t");
				float idfVal = Float.parseFloat(parts[1]);
				idf.put(parts[0], Float.valueOf(idfVal));
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return idf;
	}
	
	public static String clean(String str) {
		return str.replaceAll("[^a-zA-ZčćžšđČĆŽŠĐ]", "").toLowerCase();
	}
	
	public static String cleanNew(String str) {
		int pos = str.indexOf("/{");
		if (pos != -1)
			str = str.substring(0, pos);
		
		return clean(str);
	}
	
	static Pattern descriptionP = Pattern.compile("/\\{([^\\}]*)\\}");
	
	public static String getDescription(String str) {
		Matcher m = descriptionP.matcher(str);
		
		if (!m.find())
				throw new IllegalArgumentException("Nedostaje opis: "+str);
		return m.group(1);
	}
	
	/**
	 * Parsiranje liste riječi iz datoteke.
	 * 
	 * @param filePath Datoteka s popisom riječi.
	 * @return Lista riječi iz datoteke.
	 * @throws FileNotFoundException
	 */
	public static List<String> listWords(String filePath) throws FileNotFoundException {
		List<String> wordsList = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		try {
			String line = null;
			while ((line = reader.readLine()) != null) {
				line = line.trim().toLowerCase();
				if (line.length() == 0 || line.startsWith("#")) {
					continue;
				}
				if (line.startsWith(".")) {
					continue;
				}
				wordsList.add(line);
			}
			reader.close();
		} catch (IOException ex) {
		}
		
		return wordsList;
	}
	
	private void extractWords(String text) {
		Pattern wordP = Pattern.compile("[a-zA-ZčćžšđČĆŽŠĐ]+");
		Matcher m = wordP.matcher(text);
		words = new LinkedList<String>();
		lowerCasedWordsSet = new HashSet<String>();
		
		while (m.find()) {
			words.add(m.group());
			lowerCasedWordsSet.add(m.group().toLowerCase());
		}
		
	}
	
	private int occurNum(String text, Pattern p) {
		Matcher m = p.matcher(text);
		int occurNum = 0;
		
		while (m.find()) {
			occurNum++;
		}
		
		return occurNum;
	}

	private void calcWordsFrequencys() {
		wordFreqL2 = new HashMap<String, Float>();
		wordFreqLog = new HashMap<String, Float>();
		float sumSquare = 0.f;
		
		for (String word : words) {
			String key = word.toLowerCase();
			if (!wordFreqL2.containsKey(key)) {
				wordFreqL2.put(key, Float.valueOf(1.0f));
			} else {
				Float val = wordFreqL2.get(key);
				val += 1.0f;
				wordFreqL2.put(key, val);
			}
		}
		
		for (String key : wordFreqL2.keySet()) {
			Float freq = wordFreqL2.get(key);
			sumSquare += freq*freq;
		}
		
		sumSquare = (float) Math.pow(sumSquare, 0.5f);
		if (sumSquare == 0.f) return;
		
		int wordsNum = wordFreqL2.keySet().size();
		
		for (String key : wordFreqL2.keySet()) {
			Float freq = wordFreqL2.get(key);
			wordFreqL2.put(key, freq/sumSquare);
			wordFreqLog.put(key, (float) Math.log(1+freq/wordsNum));
		}
	}

	public List<String> getWords() {
		return words;
	}

	public int getQuotesNum() {
		return quotesNum;
	}

	public int getAposNum() {
		return aposNum;
	}

	public int getDotsNum() {
		return dotsNum;
	}

	public int getQuestionMarksNum() {
		return questionMarksNum;
	}

	public int getNumbersNum() {
		return numbersNum;
	}

	public int getExclamNum() {
		return exclamNum;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Words: ").append(words.toString()).append('\n');
		sb.append("Words num: ").append(words.size()).append('\n');
		sb.append("Quotes num: ").append(quotesNum).append('\n');
		sb.append("Apos num: ").append(aposNum).append('\n');
		sb.append("Dots num: ").append(dotsNum).append('\n');
		sb.append("Question marks num: ").append(questionMarksNum).append('\n');
		sb.append("Exclams (!) num: ").append(exclamNum).append('\n');
		sb.append("Numbers num: ").append(numbersNum).append('\n');
		sb.append("Vowels num (a e i o u): ").append(Arrays.toString(vowelsNum)).append('\n');
		return sb.toString();
	}

	public boolean containsWord(String word) {
		return lowerCasedWordsSet.contains(word);
	}

	/**
	 * @param word Riječ za koju tražimo frekvenciju (case insensitive)
	 * @return Frekvencija pojavljivanja riječi (normalizirana L<sub>2</sub> normom)
	 */
	public Float getL2Freq(String word) {
		return wordFreqL2.get(word.toLowerCase());
	}
	
	/**
	 * @param word Riječ za koju tražimo frekvenciju (case insensitive)
	 * @return Frekvencija pojavljivanja riječi (normalizirana logaritmom L<sub>1</sub> norme)
	 */
	public Float getLogFreq(String word) {
		return wordFreqLog.get(word.toLowerCase());
	}
	
	public int getUniqWordsNum() {
		return lowerCasedWordsSet.size();
	}
	
	public int getWordsNum() {
		return words.size();
	}

	@Override
	public Iterator<String> iterator() {
		return new Iterator<String>() {
			private Iterator<String> iter = lowerCasedWordsSet.iterator();
			
			@Override
			public boolean hasNext() {
				return iter.hasNext();
			}

			@Override
			public String next() {
				return iter.next();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
	
	/**
	 * Lista svih tipova unutar teksta.
	 * Tipovi su noun, verb, adjective. Za sve ostale osim unknowna se koristi baš
	 * njihova (čista) vrijednost.
	 * 
	 * @param text
	 * @param types Lista u koju će tipovi biti nadodani.
	 * @throws Exception
	 */
	public static void listTypes(String text, List<String> types) throws Exception {
		boolean added;
		boolean textAdded;
		String[] words = null;
		words = text.split(" ");
		for (String x : words) {
			textAdded = false;
			String cleaned = TextStatistics.cleanNew(x);
			if (cleaned.length()==0)
				continue ;
			try {
				List<Descriptor> ld = Descriptor.parse(TextStatistics.getDescription(x));
				for (int i = 0; i < ld.size(); i++) {
					switch (ld.get(i).getTag()) {
					case NOUN:
					case VERB:
					case ADJECTIVE:
						Tag current = ld.get(i).getTag();
						added = false;
						for (int j = 0; j < i; j++) {
							if (ld.get(j).getTag().equals(current)) {
								added = true;
								break;
							}
						}
						if (!added) {
							types.add(current.toString().toLowerCase());
						}
						break;
					case PRONOUN:
					case CONJUNCTION:
					case INTERJECTION:
					case ADPOSITION:
						if (!textAdded) {
							textAdded = true;
							types.add(cleaned.toLowerCase());
						}
						break;
					default:
						break;
					}
					
				}
			} catch (Exception e) {
				throw new Exception(x, e);
			}
		}
	}
	
	/**
	 * Lista svih tipova unutar teksta (osim unknowna).
	 * 
	 * @param text
	 * @param types Lista u koju će tipovi biti nadodani.
	 * @throws Exception
	 */
	public static void listOnlyTypes(String text, List<String> types) throws Exception {
		boolean added;
		String[] words = null;
		words = text.split(" ");
		for (String x : words) {
			String cleaned = TextStatistics.cleanNew(x);
			if (cleaned.length()==0)
				continue ;
			try {
				List<Descriptor> ld = Descriptor.parse(TextStatistics.getDescription(x));
				for (int i = 0; i < ld.size(); i++) {
					switch (ld.get(i).getTag()) {
					case NOUN:
					case VERB:
					case ADJECTIVE:
					case PRONOUN:
					case CONJUNCTION:
					case INTERJECTION:
					case ADPOSITION:
						Tag current = ld.get(i).getTag();
						added = false;
						for (int j = 0; j < i; j++) {
							if (ld.get(j).getTag().equals(current)) {
								added = true;
								break;
							}
						}
						if (!added) {
							types.add(current.toString().toLowerCase());
						}
						break;
					default:
						break;
					}
					
				}
			} catch (Exception e) {
				throw new Exception(x, e);
			}
		}
	}
	
	public static void printAllType3grams(String filePath, String datOut) throws Exception {
		
		Set<String> validWords = new HashSet<String>(listWords("config/ngram-najcesci.txt"));
		List<Article> list = XMLMiner.getArticles(filePath);
		List<String> types = new LinkedList<String>();
		
		System.out.println("Parsing is done.");
		int idx = 1;
		int total = list.size();
		for (Article a : list) {
			System.out.println("Article: " + idx + "/" + total);
			idx++;
			listTypes(a.getText(), types);
		}
		
		Map<Text3gram, Integer> ngrams = new HashMap<Text3gram, Integer>();
		BufferedWriter writer = new BufferedWriter(new FileWriter(datOut));
		int ngidx = 1;
		String[] typesArr = types.toArray(new String[0]);
		for (int i = 0; i+3 < typesArr.length; i++) {
			if (ngidx % 50 == 0) {
				System.out.println("NG: " + ngidx + "/" + typesArr.length);
			}
			ngidx++;
			if (validWords.contains(typesArr[i])
					&& validWords.contains(typesArr[i+1])
					&& validWords.contains(typesArr[i+2])) {
				Text3gram tg = new TextStatistics.Text3gram(typesArr[i], typesArr[i+1], typesArr[i+2]);
				Integer count = ngrams.get(tg);
				if (count == null) count = Integer.valueOf(1);
				else count++;
				ngrams.put(tg, count);
//				writer.write(typesArr[i] + "\t" + typesArr[i+1] + "\t" + typesArr[i+2] + "\n");
			}
		}
		
		for (Text3gram nk : ngrams.keySet()) {
			writer.write(nk.toString() + "\t" + ngrams.get(nk) + "\n");
		}
		writer.close();
	}
	
//	public static void main(String[] args) throws Exception {
//		printAllType3grams("podatci-skripta/jutarnji-kolumne-arhiva-2010-02-05_clean_tagged.train.xml", "n-grami.txt");
//	}
	
	private static class Text3gram {
		public String first;
		public String second;
		public String third;
		
		public Text3gram(String first, String second, String third) {
			super();
			this.first = first;
			this.second = second;
			this.third = third;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((first == null) ? 0 : first.hashCode());
			result = prime * result
					+ ((second == null) ? 0 : second.hashCode());
			result = prime * result + ((third == null) ? 0 : third.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Text3gram other = (Text3gram) obj;
			if (first == null) {
				if (other.first != null)
					return false;
			} else if (!first.equals(other.first))
				return false;
			if (second == null) {
				if (other.second != null)
					return false;
			} else if (!second.equals(other.second))
				return false;
			if (third == null) {
				if (other.third != null)
					return false;
			} else if (!third.equals(other.third))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return first + "\t" + second + "\t" + third;
		}
		
		

	}
}
