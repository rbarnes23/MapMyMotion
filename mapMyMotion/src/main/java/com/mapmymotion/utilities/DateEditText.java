/**
 * 
 */
package com.mapmymotion.utilities;

import java.util.Calendar;
import java.util.TimeZone;

import com.mapmymotion.Constants;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

/**
 * @author Rick
 * DateEditText is used as a custom edit text for date values
 */
public class DateEditText extends EditText implements InputFilter {
	/**
	 * @param context
	 * @param attrs
	 */
	private EditText txt;
	String dateText="";
	boolean dateValidated;
	private Context mContext;


	public DateEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		txt = this;
		//txt.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
		txt.setFilters(new InputFilter[] { new PartialRegexInputFilter(
				Constants.DATE_YYYYMMDD_REGEX) });
		txt.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				String value = s.toString();
				if (value.matches(Constants.DATE_YYYYMMDD_REGEX)) {
					txt.setTextColor(Color.BLUE);
					dateValidated = true;
				} else {
					txt.setTextColor(Color.RED);
					dateValidated = false;
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				s=dateText;
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

		});

		txt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		        DatePickerDialog datePicker = new DatePickerDialog(mContext,
		                datePickerListener,
		                cal.get(Calendar.YEAR), 
		                                    cal.get(Calendar.MONTH),
		                cal.get(Calendar.DAY_OF_MONTH));
		        datePicker.setCancelable(true);
		        datePicker.setTitle("Select the date");
		        datePicker.show();
			}

		});

}


private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {


@Override
public void onDateSet(DatePicker view, int year, int month, int day) {
dateText =String.format("%d/%02d/%02d", year,month+1,day);
txt.setText(dateText);
}
};	
	

	
	/*
	 * isValidated returns true/false for possible further processing
	 * 	 */
	public boolean isValidated() {
		return dateValidated;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.text.InputFilter#filter(java.lang.CharSequence, int, int,
	 * android.text.Spanned, int, int)
	 */
	@Override
	public CharSequence filter(CharSequence source, int start, int end,
			Spanned dest, int dstart, int dend) {
		return null;
	}
}