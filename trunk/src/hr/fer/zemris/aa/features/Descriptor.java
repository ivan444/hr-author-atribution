package hr.fer.zemris.aa.features;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Descriptor {
	
	public static enum Tag {
		NOUN, VERB, ADJECTIVE, PRONOUN,
		CONJUNCTION, INTERJECTION, ADPOSITION, UNKNOWN;
	}
	
	public static enum Gender {
		MASCULINE("m"), FEMININE("f"), NEUTER("n"), NONE("-=");
		
		String x;
		Gender(String x) { this.x = x; };
		public static Gender parse(char c) throws ParseException {
			for (Gender a : values())
				if (a.x.indexOf(c)>-1) return a;
			throw new ParseException("Nepoznata oznaka: "+c,0);
		}
	}
	
	public static enum Number {
		SINGULAR("s"), PLURAL("p"), NONE("-=");
		
		String x;
		Number(String x) { this.x = x; };
		public static Number parse(char c) throws ParseException {
			for (Number a : values())
				if (a.x.indexOf(c)>-1) return a;
			throw new ParseException("Nepoznata oznaka: "+c,0);
		}

	}
	
	public static enum Case {
		NOMINATIVE("n"), GENITIVE("g"), DATIVE("d"), ACCUSATIVE("a"),
		VOCATIVE("v"), LOCATIVE("l"), INSTRUMENTAL("i"), NONE("-=");
		
		String x;
		Case(String x) { this.x = x; };
		public static Case parse(char c) throws ParseException {
			for (Case a : values())
				if (a.x.indexOf(c)>-1) return a;
			throw new ParseException("Nepoznata oznaka: "+c,0);
		}

	}
	
	public static enum Person {
		FIRST("1"), SECOND("2"), THIRD("3"), NONE("-=");
		
		String x;
		Person(String x) { this.x = x; };
		public static Person parse(char c) throws ParseException {
			for (Person a : values())
				if (a.x.indexOf(c)>-1) return a;
			throw new ParseException("Nepoznata oznaka: "+c,0);
		}
	}
	
	public static enum Form {
		INDICATIVE("i"), INFINITIVE("n"), NONE("-=");
		
		String x;
		Form(String x) { this.x = x; };
		public static Form parse(char c) throws ParseException {
			for (Form a : values())
				if (a.x.indexOf(c)>-1) return a;
			return NONE;
		}
	}
	
	public static enum Degree {
		POSITIVE("p"), COMPARATIVE("c"), SUPERLATIVE("s"), NONE("-=");
		
		String x;
		Degree(String x) { this.x = x; };
		public static Degree parse(char c) throws ParseException {
			for (Degree a : values())
				if (a.x.indexOf(c)>-1) return a;
			throw new ParseException("Nepoznata oznaka: "+c,0);
		}
	}
	
	public static enum Special{
		UPPER, LOWER, NONE;
	}
		
	private Tag		tag;
	private Gender	gender;
	private Number	number;
	private Case	tcase;
	private Person	person;
	private Form	form;
	private Degree	degree;
	private Special special;

	
	private Descriptor(Tag tag, Gender gender, Number number, Case tcase,
			Person person, Form form, Degree degree, Special specail) {
		this.tag = tag;
		this.gender = gender;
		this.number = number;
		this.tcase = tcase;
		this.person = person;
		this.form = form;
		this.degree = degree;
		this.special = specail;
	}
	
	/**
	 * Iz liste opisa rijeci stvara se lista deskriptora (format 
	 * liste opisa je MULTEXT East gdje je svaki element liste odvojen
	 * znakom "|")
	 * @param input
	 * @return
	 * @throws ParseException 
	 */
	public static List<Descriptor> parse(String input) throws ParseException {
		
		List<Descriptor> resultList = new ArrayList<Descriptor>();
		String[] tags = input.split("\\|");
		
		for (String current : tags) {
			
			if (current.length() == 0)
				throw new IllegalArgumentException("Nedostaje oznaka");
			
			switch (current.charAt(0)) {
			case 'N' : 
				resultList.add(parseNoun(current));
				break;
			case 'V' :
				resultList.add(parseVerb(current));
				break;
			case 'A' :
				resultList.add(parseAdjective(current));
				break;
			case 'P': 
				resultList.add(justTag(Tag.PRONOUN));
				break;
			case 'C' :
				resultList.add(justTag(Tag.CONJUNCTION));
				break;
			case 'I' :
				resultList.add(justTag(Tag.INTERJECTION));
				break;
			case 'S' :
				resultList.add(justTag(Tag.ADPOSITION));
				break;
			case 'U' : 
				resultList.add(parseUnknwon(current));
				break;
			}
		}
		
		
		return resultList;
	}

	//privatne metode

	private static Descriptor parseUnknwon(String current) {
		if (current.length() == 0)
			throw new IllegalArgumentException("Nedostaje oznaka");
		if (current.charAt(1) == 'u')
			return new Descriptor(Tag.UNKNOWN,Gender.NONE, Number.NONE,
					Case.NONE, Person.NONE, Form.NONE, Degree.NONE, Special.UPPER);
		if (current.charAt(1) == 'l')
			return new Descriptor(Tag.UNKNOWN,Gender.NONE, Number.NONE,
					Case.NONE, Person.NONE, Form.NONE, Degree.NONE, Special.LOWER);
		throw new IllegalArgumentException("Ilegalna oznaka");
	}

	private static Descriptor justTag(Tag t) {
		return new Descriptor(t, Gender.NONE, Number.NONE,
				Case.NONE, Person.NONE, Form.NONE, Degree.NONE, Special.NONE);
	}

	private static Descriptor parseAdjective(String current) throws ParseException {
		Gender gender = Gender.NONE;
		Number number = Number.NONE;
		Case tcase = Case.NONE;
		Degree degree = Degree.NONE;
		
		//stupanj
		if (current.length()>2)
			degree = Degree.parse(current.charAt(2));
		//rod
		if (current.length()>3)
			gender = Gender.parse(current.charAt(3));
		//broj
		if (current.length()>4)
			number = Number.parse(current.charAt(4));
		//padez
		if (current.length()>5)
			tcase = Case.parse(current.charAt(5));
		
		return new Descriptor(Tag.ADJECTIVE, gender, number, tcase, Person.NONE, Form.NONE, degree, Special.NONE);
	}

	private static Descriptor parseVerb(String current) throws ParseException {
		Form form = Form.NONE;
		Gender gender = Gender.NONE;
		Number number = Number.NONE;
		Person person = Person.NONE;
		
		//oblik
		if (current.length()>2)
			form = Form.parse(current.charAt(2));
		//lice
		if (current.length()>4)
			person = Person.parse(current.charAt(4));
		//broj
		if (current.length()>5)
			number = Number.parse(current.charAt(5));
		//rod
		if (current.length()>6)
			gender = Gender.parse(current.charAt(6));
		
		return new Descriptor(Tag.VERB, gender, number, Case.NONE, person, form, Degree.NONE, Special.NONE);
	}

	private static Descriptor parseNoun(String current) throws ParseException {
		Gender gender = Gender.NONE;
		Number number = Number.NONE;
		Case tcase = Case.NONE;
		
		//rod
		if (current.length()>2)
			gender = Gender.parse(current.charAt(2));
		//broj
		if (current.length()>3)
			number = Number.parse(current.charAt(3));
		//padez
		if (current.length()>4)
			tcase = Case.parse(current.charAt(4));
		
		return new Descriptor(Tag.NOUN, gender, number, tcase, Person.NONE, Form.NONE, Degree.NONE, Special.NONE);
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
	
	public Special getSpecial() {
		return special;
	}
	
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		sb.append(tag);
		sb.append(" ");
		sb.append(gender);
		sb.append(" ");
		sb.append(number);
		sb.append(" ");
		sb.append(tcase);
		sb.append(" ");
		sb.append(person);
		sb.append(" ");
		sb.append(form);
		sb.append(" ");
		sb.append(degree);
		sb.append(" ");
		sb.append(special);
		
		return sb.toString();
	}
}