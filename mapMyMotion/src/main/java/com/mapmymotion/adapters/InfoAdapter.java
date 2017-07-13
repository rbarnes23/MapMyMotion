package com.mapmymotion.adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.mapmymotion.R;

public class InfoAdapter implements InfoWindowAdapter {



	  private View popup=null;
	  private LayoutInflater inflater=null;

	  public InfoAdapter(LayoutInflater inflater) {
	    this.inflater=inflater;
	  }

	  @Override
	  public View getInfoWindow(Marker marker) {
	    return(null);
	  }

	  @Override
	  public View getInfoContents(Marker marker) {
	    if (popup == null) {
	      popup=inflater.inflate(R.layout.infowindowpopup, null);
	      popup.setAlpha(5);
	    }

	    TextView tv=(TextView)popup.findViewById(R.id.title);

	    tv.setText(marker.getTitle());
	    tv=(TextView)popup.findViewById(R.id.snippet);
	    tv.setText(marker.getSnippet());

	    return(popup);
	  }
	}

