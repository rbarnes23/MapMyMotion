package com.mapmymotion.utilities;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;

import java.util.GregorianCalendar;
import java.util.TimeZone;

public class CalendarUtils {

/*
 * Add a new event into a native Google calendar. Add alert notification by setting <code>isRemind</code> as <code>true</code>.
 * @param mContext - ContentResolver
 * @param title - Event title
 * @param addInfo - Event description
 * @param place - Event place
 * @param status -  <code>int</code> This information is sufficient for most entries: tentative (0), confirmed (1) or canceled (2):
 * @param startDate - <code>long</code> event start time in mls
 * @param isRemind - <code>boolean</code> need to remind about event before?
 * @param isMailService - <code>boolean</code>. Adding attendees to the meeting
 * @return <code>long</code> eventID
 */
//@SuppressLint("InlinedApi")
public static long addEventToCalendar(Context context, String title, String addInfo, String place, int status,
        long startDate, boolean isRemind, boolean isMailService) {
	ContentResolver cr = context.getContentResolver();
    String eventUriStr = "content://com.android.calendar/events";
    ContentValues event = new ContentValues();
    // id, We need to choose from our mobile foEr primary its 1
    event.put(Events.CALENDAR_ID, 1); 
    event.put(Events.TITLE, title);
    event.put(Events.DESCRIPTION, addInfo);
    event.put(Events.EVENT_LOCATION, place);
    event.put(Events.EVENT_TIMEZONE,  TimeZone.getDefault().getID());
    // For next 1hr
    long endDate = startDate + 1000 * 60 * 60; 
    event.put(Events.DTSTART, startDate);
    event.put(Events.DTEND, endDate);
    //If it is bithday alarm or such kind (which should remind me for whole day) 0 for false, 1 for true
    // values.put("allDay", 1);
    //status =  CalendarContract.Events.AVAILABILITY_TENTATIVE;
    event.put(Events.STATUS, status); //CalendarContract.Events.AVAILABILITY_BUSY
    event.put(Events.HAS_ALARM, 1);

    Uri eventUri = cr.insert(Uri.parse(eventUriStr), event);
    long eventID = Long.parseLong(eventUri.getLastPathSegment());

    if (isRemind) {
    	int  method=1;
    	int minutes=35;
		addAlarms(cr, eventID,minutes,method);
//        String reminderUriString = "content://com.android.calendar/reminders";
//        ContentValues reminderValues = new ContentValues();
//        reminderValues.put(Reminders.EVENT_ID, eventID);
//        // Default value of the system. Minutes is a integer
//        reminderValues.put(Reminders.MINUTES, 15);
//        // Alert Methods: Default(0), Alert(1), Email(2), SMS(3)
//        reminderValues.put(Reminders.METHOD,Reminders.METHOD_ALARM); 
//
//        cr.insert(Uri.parse(reminderUriString), reminderValues);
    }
    if (isMailService) {
    	
String attendeeName="Rick";
String attendeeEmail="rbarnes23@gmail.com";
int  attendeeRelationship=2;
int  attendeeType=2;
int  attendeeStatus=1;
addAttendees(cr, eventID, attendeeName, attendeeEmail, attendeeRelationship, attendeeType, attendeeStatus);
attendeeName="Rick";
attendeeEmail="rbarnes0154@verizon.net";
attendeeRelationship=4;
attendeeType=2;
attendeeStatus=3;
addAttendees(cr, eventID, attendeeName, attendeeEmail, attendeeRelationship, attendeeType, attendeeStatus);

    }

    return eventID;
}

private static void addAttendees(ContentResolver cr, long eventId,String attendeeName,String attendeeEmail,int attendeeRelationship,int attendeeType, int attendeeStatus){
    String attendeuesesUriString = "content://com.android.calendar/attendees";
    /********* To add multiple attendees need to insert ContentValues multiple times ***********/
    ContentValues attendeesValues = new ContentValues();
    attendeesValues.put(CalendarContract.Attendees.EVENT_ID, eventId);
    // Attendees name
    attendeesValues.put(CalendarContract.Attendees.ATTENDEE_NAME, attendeeName);
    // Attendee email
    attendeesValues.put(CalendarContract.Attendees.ATTENDEE_EMAIL, attendeeEmail);
    // ship_Attendee(1), Relationship_None(0), Organizer(2), Performer(3), Speaker(4)
    attendeesValues.put(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP, attendeeRelationship);
    // None(0), Optional(1), Required(2), Resource(3)
    attendeesValues.put(CalendarContract.Attendees.ATTENDEE_TYPE, attendeeType);
    // None(0), Accepted(1), Decline(2), Invited(3), Tentative(4)
    attendeesValues.put(CalendarContract.Attendees.ATTENDEE_STATUS, attendeeStatus);
    cr.insert(Uri.parse(attendeuesesUriString), attendeesValues); //Uri attendeuesesUri =

}
private static void addAlarms(ContentResolver cr, long eventId, int minutes, int method){
    String reminderUriString = "content://com.android.calendar/reminders";
    ContentValues reminderValues = new ContentValues();
    reminderValues.put(Reminders.EVENT_ID, eventId);
    // Default value of the system. Minutes is a integer
    reminderValues.put(Reminders.MINUTES,minutes);
    // Alert Methods: Default(0), Alert(1), Email(2), SMS(3)
    reminderValues.put(Reminders.METHOD,method); 

    cr.insert(Uri.parse(reminderUriString), reminderValues);

}
public static Intent addCalendarEventbyIntent() {
    Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra(Events.TITLE, "Learn Android");
        intent.putExtra(Events.EVENT_LOCATION, "Home suit home");
        intent.putExtra(Events.DESCRIPTION, "Download Examples");
        intent.putExtra(Events.ACCOUNT_NAME, "rbarnes23@gmail.com");
//Setting dates
        GregorianCalendar calDate = new GregorianCalendar(2014,03 ,13);
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                calDate.getTimeInMillis());
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                calDate.getTimeInMillis());

//Make it a full day event
        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);

//Make it a recurring Event
        intent.putExtra(Events.RRULE, "FREQ=WEEKLY;COUNT=1;WKST=SU;BYDAY=TU,TH");

//Making it private and shown as busy
        intent.putExtra(Events.ACCESS_LEVEL, Events.ACCESS_PRIVATE);
        intent.putExtra(Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_TENTATIVE);

        //intent.setData(CalendarContract.Events.CONTENT_URI);
        //startActivity(intent);
        return intent;
}

}