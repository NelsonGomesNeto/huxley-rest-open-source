package com.thehuxley.event


interface Listener {

	String getName();
	void receiveUpdate(EventManager.Event event, Map params)

}