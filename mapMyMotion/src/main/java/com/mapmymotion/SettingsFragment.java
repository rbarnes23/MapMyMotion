package com.mapmymotion;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mapmymotion.R;

public class SettingsFragment extends PreferenceFragment {

 @Override
 public void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  
  // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
  Context context=this.getActivity();
  context.setTheme(R.style.PreferenceStyle);
  
 }
 
 @Override
 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
     View view = super.onCreateView(inflater, container, savedInstanceState);
     view.setBackgroundColor(getResources().getColor(R.color.actionbar_orange));
     return view;
 } 
}