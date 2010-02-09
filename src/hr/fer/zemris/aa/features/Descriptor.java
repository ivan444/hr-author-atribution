package hr.fer.zemris.aa.features;

import java.util.List;

public class Descriptor {

	public static enum Tag {
		NOUN, VERB, ADJECTIVE, PRONOUN, CONJUNCTION, INTERJECTION,  ADPOSITION, UNKNOWN;
		private static Tag[] values = Tag.values();
		
		public static Tag get(char c) {
			char shortTag[] = {'N', 'V', 'A', 'P', 'C', 'I', 'S', 'U'};
			for (int i=0; i<shortTag.length;++i)
				if (c == shortTag[i])
					return values[i];
			return Tag.UNKNOWN;
		}
	}
	
	public static enum Gender {
		MASCULINE, FEMININE, NEUTER, NONE;
		private static Gender[] values = Gender.values();
		
		public static Gender get(char c) {
			char shortGender[] = {'m', 'f', 'n'};
			for (int i=0; i<shortGender.length; ++i)
				if (c == shortGender[i])
					return values[i];
			return Gender.NONE;
		}
	}
	
	public static enum Number {
		SINGULAR, PLURA, NONE;
		private static Number[] values = Number.values();
		
		public static Number get(char c) {
			char shortNumber[] = {'s','p'};
			for (int i=0; i<shortNumber.length; ++i)
				if (c == shortNumber[i])
					return values[i];
			return Number.NONE;
		}
	}
	
	public static enum Case {
		NOMINATIVE, GENITIVE, DATIVE, ACCUSATIVE, VOCATIVE, LOCATIVE, INSTRUMENTAL, NONE
	}
	private static char shortCase[] = {'n','g','d','a','v','l','i'};
	
	public static enum Person {
		FIRST, SECOND, THIRD, NONE
	}
	public static enum Form {
		INDICATIVE, IMPERATIVE, CONDITIONAL, INFINITIVE, PARTICIPLE, NONE
	}
	public static enum Degree {
		POSITIVE, COMPARATIVE, SUPERLATIVE, NONE
	}
		
	private Tag		tag;
	private Gender	gender;
	private Number	number;
	private Case	tcase;
	private Person	person;
	private Form	form;
	private Degree	degree;

	
	private Descriptor(Tag tag, Gender gender, Number number, Case tcase,
			Person person, Form form, Degree degree) {
		this.tag = tag;
		this.gender = gender;
		this.number = number;
		this.tcase = tcase;
		this.person = person;
		this.form = form;
		this.degree = degree;
	}
	
	/**
	 * Iz liste opisa rijeci stvara se lista deskriptora (format 
	 * liste opisa je MULTEXT East gdje je svaki element liste odvojen
	 * znakom "|")
	 * @param input
	 * @return
	 */
	public static List<Descriptor> parse(String input) {
		
		String[] tags = input.split("|");
		
		for (String current : tags) {
			
			Tag tag = Tag.UNKNOWN;
			Gender gender = Gender.NONE;
			Number number = Number.NONE;
			Case tcase = Case.NONE;
			Person person = Person.NONE;
			Form form = Form.NONE;
			Degree degree = Degree.NONE;
			
			//provjeravamo vrstu
			
			
			//ako je imenica
			if (tag == Tag.NOUN) {
				
			}
		}
		
		
		return null;
	}

	public Tag getTag() {
		return tag;
	}

	public Gender getGender() {
		return gender;
	}

	public Number getNumber() {
		return number;
	}

	public Case getCase() {
		return tcase;
	}

	public Person getPerson() {
		return person;
	}

	public Form getForm() {
		return form;
	}

	public Degree getDegree() {
		return degree;
	}
	
}