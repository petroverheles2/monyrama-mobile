package com.monyrama.util;

import java.util.Locale;

public class SumLocalizer {
	public static String localize(String sum) {
		String localizedSum = sum;
		
		String lang2code = Locale.getDefault().getLanguage();
		if("uk".equals(lang2code) || "ru".equals(lang2code)) {
			if(sum != null) {
				localizedSum = sum.replace('.', ',');	
			}			
		}
		
		return localizedSum;
	}
}
