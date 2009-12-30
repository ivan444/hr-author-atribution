package hr.fer.zemris.aa.features;

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
}
