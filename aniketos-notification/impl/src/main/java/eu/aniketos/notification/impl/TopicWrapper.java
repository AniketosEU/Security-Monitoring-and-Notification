package eu.aniketos.notification.impl;

import java.util.HashMap;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Topic;

/**
 * 
 * Utility class used for wrapping a JMS topic along with its corresponding ActiveMQ MessageConsumer object.
 * Also enabled to keep track of the individual consumers of the topic.
 * 
 * @version 1.0
 * @author Erlend Andreas Gjære (SINTEF), erlendandreas.gjare@sintef.no
 */
public class TopicWrapper {

	/** A topic object reference to keep **/
	private Topic topic;
	/** A JMS message producer object **/
	private MessageProducer producer;
	/** A list of topic subscribers **/
	private HashMap<String,MessageConsumer> consumerList;
	
	
	/**
	 * Creates a new instance of the storage object
	 * @param topic The JMS topic object
	 */
	public TopicWrapper(Topic topic) {
		this.topic = topic;
		this.producer = null;
		consumerList = new HashMap<String,MessageConsumer>();
	}
	

	/**
	 * Creates a new instance of the storage object
	 * @param topic The JMS topic object
	 * @param producer The JMS producer object
	 */
	public TopicWrapper(Topic topic, MessageProducer producer) {
		this.topic = topic;
		this.producer = producer;
		consumerList = new HashMap<String,MessageConsumer>();
	}
	
	
	/**
	 * Add a subscriber to the list of subscribers
	 * @param subscriberId
	 * @param consumer
	 */
	public void addSubscriber(String subscriberId, MessageConsumer consumer) {
		consumerList.put(subscriberId, consumer);
	}
	

	/**
	 * Set the message producer of the subscriber 
	 * @param producer 
	 */
	public void setProducer(MessageProducer producer) {
		this.producer = producer;
	}
	
	/**
	 * Get the topic
	 * @return A topic object containing name
	 */
	public Topic getTopic() {
		return topic;
	}
	
	/**
	 * Get the message producer
	 * @return The message producer object
	 */
	public MessageProducer getProducer() {
		return producer;
	}
	
	/**
	 * Get a map of subscribers
	 * @return A hash map where subscribers are l
 	 */
	public HashMap<String,MessageConsumer> getSubscribers() {
		return consumerList;
	}
}
