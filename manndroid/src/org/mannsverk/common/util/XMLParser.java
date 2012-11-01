package org.mannsverk.common.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.mannsverk.activity.SettingsActivity;
import org.mannsverk.common.enom.CalendarEnum;
import org.mannsverk.common.enom.ErrorEnum;
import org.mannsverk.common.exception.ManndroidException;
import org.mannsverk.common.vo.Event;
import org.mannsverk.common.vo.User;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;
import android.util.Log;

public class XMLParser {
	private static final String TAG = "XMLParser";
	private Context context;
	
	public XMLParser(Context context){
		this.context = context;
	}

	private Document setup(Object object) throws ManndroidException, ParserConfigurationException, SAXException, IOException{
		DocumentBuilderFactory factory= DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document dom = null;
		if(object instanceof String){
			dom = builder.parse((String)object);
		}else if(object instanceof InputStream) {
			dom = builder.parse((InputStream)object);
		}else if(object instanceof File){
			dom = builder.parse((File)object);				
		}
		return dom;
	}

	/**
	 * Converts a xml to a Java object
	 * @param object (must be File file, InputStream inputStream or String url)
	 * @param xmlType - type of calendar xml fetched
	 * @return Event, List<Event> or null
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public Object parse(Object object, CalendarEnum xmlType) throws ManndroidException, ParserConfigurationException, SAXException, IOException{		
		switch (xmlType) {
		case EVENT: return parseEvent(setup(object));
		case EVENTS: return parseEvents(setup(object));
		}

		return null;
	}

	/**
	 * Parses calendar event xml
	 * @param object (must be String url or InputStream inputStream)
	 * @return Event
	 * @throws ManndroidException 
	 */
	private List<User> parseEvent(Document dom) throws ManndroidException {
		Log.d(TAG, "Enter");
		Event event = new Event();
		List <User> userList = null;

		try {			
			Element root = dom.getDocumentElement();

			errorCheck(root);

			NodeList items = root.getElementsByTagName("event");

			for(int i = 0; i < items.getLength(); i++){

				Node item = items.item(i);
				NodeList properties = item.getChildNodes();

				for(int j = 0; j < properties.getLength(); j++){					
					Node property = properties.item(j);
					String name = property.getNodeName();

					if(name.equalsIgnoreCase("event_id")){
						Log.i(TAG, "Event id: " + property.getFirstChild().getNodeValue());
						event.setEventId(property.getFirstChild().getNodeValue());
					}
					else if(name.equalsIgnoreCase("event_name")){
						Log.i(TAG, "Event name: " + property.getFirstChild().getNodeValue());
						event.setEventName(property.getFirstChild().getNodeValue());
					}
					else if(name.equalsIgnoreCase("event_date")){
						Log.i(TAG, "Event date: " + property.getFirstChild().getNodeValue());
						event.setEventDate(property.getFirstChild().getNodeValue());
					}
					else if(name.equalsIgnoreCase("event_type")){
						Log.i(TAG, "Event type: " + property.getFirstChild().getNodeValue());
						event.setEventType(property.getFirstChild().getNodeValue());
					}
					else if(name.equalsIgnoreCase("event_time")){
						Log.i(TAG, "Event time: " + property.getFirstChild().getNodeValue());
						event.setEventTime(property.getFirstChild().getNodeValue());
					}
					else if(name.equalsIgnoreCase("event_active")){
						boolean active = property.getFirstChild().getNodeValue().equalsIgnoreCase("1") ? true : false;
						Log.i(TAG, "Event active: " + active);						
						event.setActive(active);
					}
					else if(name.equalsIgnoreCase("football_event_id")){
						Log.i(TAG, "Football event id: " + property.getFirstChild().getNodeValue());
						event.setFootballEventId(property.getFirstChild().getNodeValue());
					}
					else if(name.equalsIgnoreCase("poker_event_id")){
						Log.i(TAG, "Poker event id: " + property.getFirstChild().getNodeValue());
						event.setPokerEventId(property.getFirstChild().getNodeValue());
					}
					else if(name.equalsIgnoreCase("participants")){
						Log.i(TAG, "Found participants");
						Node participant = properties.item(j);
						NodeList participants = participant.getChildNodes(); // get all child nodes of <participants> --> <participant>

						userList = new ArrayList<User>();

						for(int k = 0; k < participants.getLength(); k++){
							User user = new User();
							Node elem = participants.item(k); //participant
							NodeList participantChildren = elem.getChildNodes(); // get all child nodes of <participant> --> <name> and <registered>

							for(int c = 0; c < participantChildren.getLength(); c++){								
								Node participantChild = participantChildren.item(c);

								String child = participantChild.getNodeName();
								String value = "";
								if(participantChild.getFirstChild() != null && participantChild.getFirstChild().getNodeValue() != null){
									value = participantChild.getFirstChild().getNodeValue();
								}

								if(child.equalsIgnoreCase("name")){								
									Log.i(TAG, "Participant name: " + value);
									user.setName(value);
								}
								else if(child.equalsIgnoreCase("registered")){
									Log.i(TAG, "Registered: " + value);
									user.setRegistered(value);
								}
								else if(child.equalsIgnoreCase("avatar_url")){
									Log.i(TAG, "Avatar: " + value);
									if(value != null && value != ""){
										if(!value.startsWith("http")){
											if(value.substring(0, 1).matches("-?\\d+(.\\d+)?")){ // check if value is numeric
												Log.i(TAG, "Avatar is numeric");
												String prefix = Util.AVATAR_FILE;
												value = prefix + value;
											}else {
												String prefix = Util.AVATAR_GALLERY;
												value = prefix + value;
											}
										}
										Log.i(TAG, "Final avatar: " + value);
										user.setAvatarUrl(value);										
									}else{
										user.setAvatarUrl(null);
									}
								}
							}	
							userList.add(user);
						}
						event.setUsers(userList);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new ManndroidException(ErrorEnum.XML_PARSE_FAILURE, e.getMessage());
		}		
		return userList;
	}

	private void errorCheck(Element root) throws ManndroidException{
		Log.i(TAG, "Root is " + root.getNodeName());
		if(root.getNodeName().equalsIgnoreCase("response_code")){
			NodeList errorList = root.getChildNodes();

			for(int i= 0; i < errorList.getLength(); i++){
				Node node = errorList.item(i);
				String name = node.getNodeName();
				String value = node.getFirstChild().getNodeValue();

				if(name.equalsIgnoreCase("value")){
					Log.i(TAG, "Value is " + value);
					if(value.equalsIgnoreCase("99")){
						throw new ManndroidException(ErrorEnum.AUTENTICATION_FAILURE, "Brukernavn og/eller passord er feil");
					}
				}
			}
		}
	}
	
	/**
	 * Parser for CalendarActivity
	 * @param dom
	 * @return a calendar list (List<Event>)
	 * @throws ManndroidException
	 */
	private List<Event> parseEvents(Document dom) throws ManndroidException{
		Log.d(TAG, "Enter parseEvents");
		List<Event> eventList = new ArrayList<Event>();

		try {			
			Element root = dom.getDocumentElement();	

			errorCheck(root);

			NodeList items = root.getElementsByTagName("events");

			for(int i = 0; i < items.getLength(); i++){
				Log.i(TAG, "First loop");

				Node item = items.item(i);
				NodeList properties = item.getChildNodes();

				for(int j = 0; j < properties.getLength(); j++){
					Log.i(TAG, "Second loop");
					Event event = new Event();
					Node eventTag = properties.item(j);
					NodeList eventTagList = eventTag.getChildNodes();

					for(int k = 0; k < eventTagList.getLength(); k++){
						Node node = eventTagList.item(k);
						String name = node.getNodeName();

						if(name.equalsIgnoreCase("event_id")){
							Log.i(TAG, "Event id: " + node.getFirstChild().getNodeValue());
							event.setEventId(node.getFirstChild().getNodeValue());
						} 
						else if(name.equalsIgnoreCase("event_name")){
							Log.i(TAG, "Event name: " + node.getFirstChild().getNodeValue());
							event.setEventName(node.getFirstChild().getNodeValue());
						}
						else if(name.equalsIgnoreCase("event_date")){
							Log.i(TAG, "Event date: " + node.getFirstChild().getNodeValue());
							event.setEventDate(node.getFirstChild().getNodeValue());
						}
						else if(name.equalsIgnoreCase("event_type")){
							Log.i(TAG, "Event type: " + node.getFirstChild().getNodeValue());
							event.setEventType(node.getFirstChild().getNodeValue());
						}
						else if(name.equalsIgnoreCase("event_time")){
							String time = "";
							if(node.getFirstChild() != null){
								time = node.getFirstChild().getNodeValue();
							}
							Log.i(TAG, "Event time: " + time);
							event.setEventTime(time);
						}
						else if(name.equalsIgnoreCase("event_active")){
							boolean active = node.getFirstChild().getNodeValue().equalsIgnoreCase("1") ? true : false;
							Log.i(TAG, "Event active: " + active);						
							event.setActive(active);
						}
						else if(name.equalsIgnoreCase("football_event_id")){
							Log.i(TAG, "Football event id: " + node.getFirstChild().getNodeValue());
							event.setFootballEventId(node.getFirstChild().getNodeValue());
						}
						else if(name.equalsIgnoreCase("poker_event_id")){
							Log.i(TAG, "Poker event id: " + node.getFirstChild().getNodeValue());
							event.setPokerEventId(node.getFirstChild().getNodeValue());
						}
					}
					
					if(SettingsActivity.getCalendars(context).equalsIgnoreCase("all")){
						eventList.add(event);
					}else if(SettingsActivity.getCalendars(context).equalsIgnoreCase("football") && event.getFootballEventId() != null){
						eventList.add(event);
					}else if(SettingsActivity.getCalendars(context).equalsIgnoreCase("poker") && event.getPokerEventId() != null){
						eventList.add(event);
					}					
				}				
			}

		}catch(Exception e){
			e.printStackTrace();
			throw new ManndroidException(ErrorEnum.XML_PARSE_FAILURE, e.getMessage());
		}
		return eventList;
	}
}

