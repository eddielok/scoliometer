package com.level.scoliometer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
//import android.preference.PreferenceManager;

public class SharedPreference {

	public SharedPreference() {
		super();
	}

	public void save(Context context, String text, String PREFS_NAME, String PREFS_KEY) {
        if (text == null) text = "0";
            SharedPreferences settings;
            Editor editor;
            settings = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE); //1
            editor = settings.edit(); //2
            editor.putString(PREFS_NAME, text); //3
            editor.commit(); //4
	}

	public String getValue(Context context, String PREFS_NAME, String PREFS_KEY) {
		SharedPreferences settings;
		String text;
		settings = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
		text = settings.getString(PREFS_NAME, null);
		if (text == null)
			text = "0";
		return text;
	}
	
	public void clearSharedPreference(Context context, String PREFS_KEY) {
		SharedPreferences settings;
		Editor editor;
		settings = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
		editor = settings.edit();

		editor.clear();
		editor.commit();
	}

	public void removeValue(Context context, String PREFS_KEY) {
		SharedPreferences settings;
		Editor editor;

		settings = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
		editor = settings.edit();

		editor.remove(PREFS_KEY);
		editor.commit();
	}	
}
