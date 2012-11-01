package org.mannsverk.common.vo;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class EventList extends ArrayList<Event> implements Parcelable{

	public EventList(){	
		
	}
	
	public EventList(Parcel in){		 
		readFromParcel(in);

	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}	

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

		public EventList createFromParcel(Parcel in) {
			return new EventList(in);
		}

		public Object[] newArray(int arg0) {
			return null;
		}
	};

	private void readFromParcel(Parcel in) {

		this.clear();

		//First we have to read the list size
		int size = in.readInt();

		//Reading remember that we wrote first the Name and later the Phone Number.
		//Order is fundamental        

		for (int i = 0; i < size; i++) {
			Event event = new Event();
			event.setEventDate(in.readString()); 
			event.setEventId(in.readString());
			event.setEventName(in.readString());
			event.setEventTime(in.readString());
			event.setEventType(in.readString());
			event.setFootballEventId(in.readString());
			event.setPokerEventId(in.readString());
			event.setActive(in.readInt() == 1 ? true : false);
			event.setChanged(in.readInt() == 1 ? true : false);
			this.add(event);
		}
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		 
        int size = this.size();
        //We have to write the list size, we need him recreating the list
        dest.writeInt(size);

        //We decided arbitrarily to write first the Name and later the Phone Number.
        for (int i = 0; i < size; i++) {

                Event event = this.get(i);

                dest.writeString(event.getEventDate());
                dest.writeString(event.getEventId());
                dest.writeString(event.getEventName());
                dest.writeString(event.getEventTime());
                dest.writeString(event.getEventType());
                dest.writeString(event.getFootballEventId());
                dest.writeString(event.getPokerEventId());
                dest.writeInt(event.isActive() ? 1 : 0);
                dest.writeInt(event.isChanged() ? 1 : 0);
        }

}

}
