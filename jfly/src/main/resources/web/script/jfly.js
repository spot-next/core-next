jfly = {};

jfly.connection = {};

// When the connection is open, send some data to the server
jfly.websockethandler = {
		// initializes the handler
		init: function() {
			jfly.websockethandler.connection = new WebSocket('ws://' + window.location.hostname + ":" + window.location.port + '/com');
			jfly.websockethandler.connection.onmessage = jfly.websockethandler.onmessage;
			jfly.websockethandler.connection.onopen = jfly.websockethandler.onopen;
			jfly.websockethandler.connection.onerror = jfly.websockethandler.onerror;
		},
		
		// send a message to the server
		send(message) {
			var msg = jfly.toString(message);
			jfly.websockethandler.connection.send(msg);
		},
		
		// event handling
		onopen: function (event) {
			console.log('WebSocket onopen ' + event);
		},
		
		onclose: function (event) {
			console.log('WebSocket onclose ' + event);
		},
		
		// Log errors
		onerror: function (event) {
			console.log('WebSocket onerror ' + event);
		},
		
		// Log messages from the server
		onmessage: function (event) {
			console.log('WebSocket onmessage');
			
			var message = JSON.parse(event.data);
			
			if (message.type == "componentInitialization") {
				jfly.callAsync(function() {
					jfly.initVue(message.componentStates);
				});
			} else if (message.type == "objectManipulation") {
				var component = jfly.findComponent(message.componentUuid);
				component[message.method].apply(component, message.params);
			} else if (message.type == "functionCall") {
				var func = window[message.object][message.func];
				func.apply(func, message.params)
			} else if (message.type == "componentUpdate") {
				//
			}
		},
		
}

//converts objects to json strings
jfly.toString = function(object) {
	return JSON.stringify(object);
},

jfly.callAsync = function(func) {
	setTimeout(func, 0);
}

jfly.handleEvent = function(element, event) {
	var message = {
		"messageType": "event",
		"uuid": $(element).attr("uuid"),
		"event": event.type,
		"payload": event,
	}
	
	jfly.websockethandler.send(message);
};


jfly.reloadApp = function() {
	location.reload();
};

jfly.findComponent = function(componentUuid) {
	var component = $("[uuid='" + componentUuid + "']");
	
	return component;
};

jfly.removeComponent = function(componentUuid) {
	jfly.findComponent(componentUuid).remove();
};

jfly.removeChildComponent = function(containerUuid, childUuid) {
	jfly.findComponent(containerUuid).find("[uuid='" + uuid + "']").remove();
};

jfly.addChildComponent = function(containerUuid, child) {
	jfly.findComponent(containerUuid).append(child);
};

jfly.replaceComponent = function(componentUuid, component) {
	var elem = $(component);
	
	jfly.findComponent(componentUuid).replaceWith(elem);
	jfly.setupEventHandling(jfly.findComponent(componentUuid));
};

jfly.hideComponent = function(componentUuid) {
	jfly.findComponent(componentUuid).addClass("hidden");
};

jfly.showComponent = function(componentUuid) {
	jfly.findComponent(componentUuid).removeClass("hidden");
};

jfly.registerEvent = function(componentUuid, event) {
	jfly.findComponent(componentUuid).on(event, function(event){
		jfly.handleEvent(this, event);
	});
};

jfly.unregisterEvent = function(componentUuid, event) {
	jfly.findComponent(componentUuid).unbind(event);
};

jfly.findElementById = function(id) {
	return $("#" + id);
};

jfly.replaceElementWithId = function(id, element) {
	jfly.findElementById(id).replaceWith(element);
};

jfly.setupEventHandling = function(element, events) {
	if (events !== "") {
		element.on(events, function(event){
			jfly.handleEvent(this, event);
		});
	}
};

jfly.initEventHandling = function() {
	$("[uuid]").forEach(function(elem) {
		jfly.setupEventHandling($(elem), $(elem).attr("registeredEvents"));
	});
}

jfly.init = function() {
	jfly.websockethandler.init();
	jfly.initEventHandling();
};

jfly.initVue = function(states) {
	jfly.websockethandler.init();
}

/*
 * Init jfly when page is ready
 */
Zepto(function($) {
	jfly.init();
	
});

