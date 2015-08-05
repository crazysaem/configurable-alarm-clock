package com.crazysaem.confclock.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

public class TTSCheck implements OnInitListener {	
	/*
	public static final Locale	CANADA	Locale constant for en_CA.
	public static final Locale	CANADA_FRENCH	Locale constant for fr_CA.
	public static final Locale	CHINA	Locale constant for zh_CN.
	public static final Locale	CHINESE	Locale constant for zh.
	public static final Locale	ENGLISH	Locale constant for en.
	public static final Locale	FRANCE	Locale constant for fr_FR.
	public static final Locale	FRENCH	Locale constant for fr.
	public static final Locale	GERMAN	Locale constant for de.
	public static final Locale	GERMANY	Locale constant for de_DE.
	public static final Locale	ITALIAN	Locale constant for it.
	public static final Locale	ITALY	Locale constant for it_IT.
	public static final Locale	JAPAN	Locale constant for ja_JP.
	public static final Locale	JAPANESE	Locale constant for ja.
	public static final Locale	KOREA	Locale constant for ko_KR.
	public static final Locale	KOREAN	Locale constant for ko.
	public static final Locale	PRC	Locale constant for zh_CN.
	public static final Locale	ROOT	Locale constant for the root locale.
	public static final Locale	SIMPLIFIED_CHINESE	Locale constant for zh_CN.
	public static final Locale	TAIWAN	Locale constant for zh_TW.
	public static final Locale	TRADITIONAL_CHINESE	Locale constant for zh_TW.
	public static final Locale	UK	Locale constant for en_GB.
	public static final Locale	US	Locale constant for en_US.
	*/
	private TextToSpeech tts;
	
	public TTSCheck(Context context) {			
		tts = new TextToSpeech(context, this);
	}
	
	//public List<Locale> getTTSAvailableLanguages(Context context) {}
	
	@Override
	public void onInit(int arg0) {
		List<Locale> ttsLocales = new ArrayList<Locale>();	
		if(langCheck(tts.isLanguageAvailable(Locale.CANADA))) {
			ttsLocales.add(Locale.CANADA);
		}
		if(langCheck(tts.isLanguageAvailable(Locale.CANADA_FRENCH))) {
			ttsLocales.add(Locale.CANADA_FRENCH);
		}
		if(langCheck(tts.isLanguageAvailable(Locale.CHINESE))) {
			ttsLocales.add(Locale.CHINESE);
		}
		if(langCheck(tts.isLanguageAvailable(Locale.ENGLISH))) {
			ttsLocales.add(Locale.ENGLISH);
		}
		if(langCheck(tts.isLanguageAvailable(Locale.FRENCH))) {
			ttsLocales.add(Locale.FRENCH);
		}
		if(langCheck(tts.isLanguageAvailable(Locale.GERMAN))) {
			ttsLocales.add(Locale.GERMAN);
		}
		if(langCheck(tts.isLanguageAvailable(Locale.ITALIAN))) {
			ttsLocales.add(Locale.ITALIAN);
		}
		if(langCheck(tts.isLanguageAvailable(Locale.JAPANESE))) {
			ttsLocales.add(Locale.JAPANESE);
		}
		if(langCheck(tts.isLanguageAvailable(Locale.KOREAN))) {
			ttsLocales.add(Locale.KOREAN);
		}
		if(langCheck(tts.isLanguageAvailable(Locale.US))) {
			ttsLocales.add(Locale.US);
		}
		if(langCheck(tts.isLanguageAvailable(Locale.UK))) {
			ttsLocales.add(Locale.UK);
		}
		
		//return ttsLocales;
	}
	
	public static boolean langCheck(int i) {
		if(((i|TextToSpeech.LANG_AVAILABLE)>0) || ((i|TextToSpeech.LANG_COUNTRY_AVAILABLE)>0) || ((i|TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE)>0)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static List<String> localesToString(List<Locale> locales) {
		List<String> ret = new ArrayList<String>();	
		for(int i=0;i<locales.size();i++) {
			ret.add(locales.get(i).getDisplayLanguage()+" ("+locales.get(i).getDisplayCountry()+")");
		}
		return ret;
	}

	
}
