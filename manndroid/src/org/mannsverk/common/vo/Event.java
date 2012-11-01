package org.mannsverk.common.vo;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable {
	private String eventId;
	private String eventName;
	private String eventDate;
	private String eventTime;
	private String eventType;
	private String footballEventId;
	private String pokerEventId;
	private boolean active;
	private List<User> users;
	private boolean changed; // used to indicate if this event is new or changes since last online calendar update.

	public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public String getEventDate() {
		return eventDate;
	}
	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}
	public String getEventTime() {
		return eventTime;
	}
	public void setEventTime(String eventTime) {
		this.eventTime = eventTime;
	}
	public String getFootballEventId() {
		return footballEventId;
	}
	public void setFootballEventId(String footballEventId) {
		this.footballEventId = footballEventId;
	}
	public String getPokerEventId() {
		return pokerEventId;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public void setPokerEventId(String pokerEventId) {
		this.pokerEventId = pokerEventId;
	}
	public List<User> getUsers() {
		return users;
	}
	public void setUsers(List<User> users) {
		this.users = users;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	
	/**
	 * Used to indicate if this event is new or changes since last online calendar update.
	 * @return true if changed or new
	 */
	public boolean isChanged() {
		return changed;
	}
	public void setChanged(boolean changed) {
		this.changed = changed;
	}
	
	@Override
	public boolean equals(Object compareObj)
	{
		if (this == compareObj) // Are they exactly the same instance?
			return true;

		if (compareObj == null) // Is the object being compared null?
			return false;

		if (!(compareObj instanceof Event)) // Is the object being compared also an Event?
			return false;
		
		Event event = (Event)compareObj; // Convert the object to an Event
		
		return this.toString().equals(event.toString()); // Are they equal?
	}

	@Override
	public String toString() {
		return "Event [eventDate=" + eventDate + ", eventId=" + eventId
		+ ", eventName=" + eventName + ", eventTime=" + eventTime
		+ ", eventType=" + eventType + ", footballEventId="
		+ footballEventId + ", active=" + active 
		+ ", participants=" + users
		+ ", pokerEventId=" + pokerEventId + "]";
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(footballEventId);
		dest.writeString(eventDate);
		dest.writeString(eventId);
		dest.writeString(eventName);
		dest.writeString(eventTime);
		dest.writeString(eventType);
		dest.writeString(pokerEventId);
		dest.writeBooleanArray(new boolean[] {active, changed});		
	}
	
	// this is used to regenerate the object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        public Event createFromParcel(Parcel in) {
            return new Event();
        }

        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}
