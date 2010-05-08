package hr.fer.zemris.aa.features.impl;

import java.util.Arrays;
import java.util.List;

import hr.fer.zemris.aa.features.Descriptor;
import hr.fer.zemris.aa.features.FeatureVector;
import hr.fer.zemris.aa.features.IFeatureExtractor;
import hr.fer.zemris.aa.features.TextStatistics;
import hr.fer.zemris.aa.features.Descriptor.Case;
import hr.fer.zemris.aa.features.Descriptor.Degree;
import hr.fer.zemris.aa.features.Descriptor.Number;
import hr.fer.zemris.aa.features.Descriptor.Form;
import hr.fer.zemris.aa.features.Descriptor.Gender;
import hr.fer.zemris.aa.features.Descriptor.Person;
import hr.fer.zemris.aa.features.Descriptor.Special;

public class MorphosyntaticFeatureExtractor implements IFeatureExtractor {
	
	//ocajno rjesenje :D
	int size = 22;
	
	@Override
	public FeatureVector getFeatures(String text) {
		
		FeatureVector result = new FeatureVector(size);
		int[] fr = new int[size];
		boolean was[] = new boolean[size];
		
		List<Descriptor> desList = null;
		int wordCount = 0;
		int tmp;
		
		String[] words = text.split(" ");
		
		for (String x : words) {
			
			if (TextStatistics.cleanNew(x).isEmpty())
				continue;
			
			wordCount++;
			
			try {
				desList = Descriptor.parse(TextStatistics.getDescription(x));
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
			
			//poredak Case, Degree, Form, Gender, Number, Person, Special
			Arrays.fill(was, false);
			for (Descriptor d : desList) {
				int t=0;
				
				tmp = d.getCase().ordinal();
				if (d.getCase() != Case.NONE && !was[t+tmp]) {
					was[t+tmp] = true;
					fr[t+tmp]++;
				}
				t += Case.values().length-1;
				
				tmp = d.getDegree().ordinal();
				if (d.getDegree() != Degree.NONE && !was[t+tmp]) {
					was[t+tmp] = true;
					fr[t+tmp]++;
				}
				t += Degree.values().length-1;
				
				tmp = d.getForm().ordinal();
				if (d.getForm() != Form.NONE && !was[t+tmp]) {
					was[t+tmp] = true;
					fr[t+tmp]++;
				}
				t += Form.values().length-1;
				
				tmp = d.getGender().ordinal();
				if (d.getGender() != Gender.NONE && !was[t+tmp]) {
					was[t+tmp] = true;
					fr[t+tmp]++;
				}
				t += Gender.values().length-1;
				
				tmp = d.getNumber().ordinal();
				if (d.getNumber() != Number.NONE && !was[t+tmp]) {
					was[t+tmp] = true;
					fr[t+tmp]++;
				}
				t += Number.values().length-1;
				
				tmp = d.getPerson().ordinal();
				if (d.getPerson() != Person.NONE && !was[t+tmp]) {
					was[t+tmp] = true;
					fr[t+tmp]++;
				}
				t += Person.values().length-1;
				
				tmp = d.getSpecial().ordinal();
				if (d.getSpecial() != Special.NONE && !was[t+tmp]) {
					was[t+tmp] = true;
					fr[t+tmp]++;
				}
			}
		}
		
	
		for (int i=0; i<size;++i) {
			result.put(i, fr[i]/(float)wordCount);
		}
		
		return result;
		
	}
	
	@Override
	public String getName() {
		return "MorphosyntacticFeatureExtractor";
	}

	@Override
	public String getShortName() {
		return "M";
	}

}
