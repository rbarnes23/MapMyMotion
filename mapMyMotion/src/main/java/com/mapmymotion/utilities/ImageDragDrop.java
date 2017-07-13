package com.mapmymotion.utilities;

import android.R;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.RelativeLayout;

public class ImageDragDrop extends View{
	  String msg;
	  private android.widget.RelativeLayout.LayoutParams layoutParams;	
    /**
     * Default constructor
     *
     * @param ct {@link android.content.Context}
     */
    public ImageDragDrop(final Context ct) {
        super(ct);

        init(ct);
    }

    public ImageDragDrop(final Context ct, final AttributeSet attrs) {
        super(ct, attrs);

        init(ct);
    }
    private void init(final Context ct) {
//    	View view =(ImageView ) this.setBackground(ct.getResources().getDrawable(R.id.checkbox ));
    	this.setBackgroundColor(Color.RED);
        this.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
               ClipData.Item item = new ClipData.Item((CharSequence)v.getTag());

               String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
               ClipData dragData = new ClipData(v.getTag().toString(), 
               mimeTypes, item);

               // Instantiates the drag shadow builder.
               View.DragShadowBuilder myShadow = new DragShadowBuilder(v);

               // Starts the drag
               v.startDrag(dragData,  // the data to be dragged
               myShadow,  // the drag shadow builder
               null,      // no need to use local data
               0          // flags (not currently used, set to 0)
               );
               return true;
            }
         });
        // Create and set the drag event listener for the View
        this.setOnDragListener( new OnDragListener(){
           @Override
           public boolean onDrag(View v,  DragEvent event){
               switch(event.getAction())                   
               {
                  case DragEvent.ACTION_DRAG_STARTED:
                     layoutParams = (RelativeLayout.LayoutParams) 
                     v.getLayoutParams();
                     Log.d(msg, "Action is DragEvent.ACTION_DRAG_STARTED");
                     // Do nothing
                     break;
                  case DragEvent.ACTION_DRAG_ENTERED:
                     Log.d(msg, "Action is DragEvent.ACTION_DRAG_ENTERED");
                     int x_cord = (int) event.getX();
                     int y_cord = (int) event.getY();  
                     break;
                  case DragEvent.ACTION_DRAG_EXITED :
                     Log.d(msg, "Action is DragEvent.ACTION_DRAG_EXITED");
                     x_cord = (int) event.getX();
                     y_cord = (int) event.getY();
                     layoutParams.leftMargin = x_cord;
                     layoutParams.topMargin = y_cord;
                     v.setLayoutParams(layoutParams);
                     break;
                  case DragEvent.ACTION_DRAG_LOCATION  :
                     Log.d(msg, "Action is DragEvent.ACTION_DRAG_LOCATION");
                     x_cord = (int) event.getX();
                     y_cord = (int) event.getY();
                     break;
                  case DragEvent.ACTION_DRAG_ENDED   :
                     Log.d(msg, "Action is DragEvent.ACTION_DRAG_ENDED");
                     // Do nothing
                     break;
                  case DragEvent.ACTION_DROP:
                     Log.d(msg, "ACTION_DROP event");
                     // Do nothing
                     break;
                  default: break;
                  }
                  return true;
               }
        	   

        });
     }
    }

