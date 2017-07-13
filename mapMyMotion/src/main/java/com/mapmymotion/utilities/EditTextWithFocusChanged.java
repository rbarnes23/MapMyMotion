/**
 * 
 */
package com.mapmymotion.utilities;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

/**
 * @author Rick
 * DateEditText is used as a custom edit text with focuschanged
 */
public class EditTextWithFocusChanged extends EditText {
	/**
	 * @param context
	 * @param attrs
	 */
	private EditText txt;


	public EditTextWithFocusChanged(Context context, AttributeSet attrs) {
		super(context, attrs);
		txt = this;
		txt.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					v.setBackgroundColor(Color.rgb(255, 248, 220));
				} else {
					// set the row background white
					v.setBackgroundColor(Color.rgb(255, 255, 255));
				}
			}
		});

	}


}