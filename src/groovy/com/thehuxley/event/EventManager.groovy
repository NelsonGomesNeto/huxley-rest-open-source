package com.thehuxley.event

class EventManager {

	private static EventManager instance

	Map<String, List<Listener>> eventListeners = [:]

	enum Event {
			SUBMISSION_NEW,
			SUBMISSION_REEVALUATE,
	}


	private EventManager() {}


	synchronized static EventManager getInstance() {
		if (!instance) {
			instance = new EventManager()
		}

		return instance
	}


	def addListener(Listener listener, Event event) {

		if (!eventListeners[event.name()]) {
			eventListeners[event.name()] = []
		}

		if (!eventListeners[event.name()].name.contains(listener.name)) {
			eventListeners[event.name()].add(listener)
		}

	}


	def notify(Event event, Map params) {

		eventListeners[event.name()].eachParallel {
			it.receiveUpdate(event, params)
		}

	}

}
