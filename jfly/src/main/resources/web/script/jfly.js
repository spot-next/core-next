jfly = {};

jfly.connection = new WebSocket('ws://' + window.location.hostname + ":" + window.location.port + '/com'); 

// When the connection is open, send some data to the server
jfly.connection.onopen = function () {
	// jfly.sendMessage({})); // Send the message 'Ping' to
	// the server
};

// Log errors
jfly.connection.onerror = function (error) {
	console.log('WebSocket Error ' + error);
};

// Log messages from the server
jfly.connection.onmessage = function (e) {
	console.log('Server: ' + e.data);
	
	var message = JSON.parse(e.data);
	
	if (message.type == "objectManipulation") {
		var component = jfly.findComponent(message.componentUuid);
		component[message.method].apply(component, message.params);
	} else if (message.type == "functionCall") {
		var func = window[message.object][message.func];
		func.apply(func, message.params)
	}
};

jfly.toString = function(object) {
	return JSON.stringify(object);
}

jfly.handleEvent = function(element, event) {
	var message = {
		"uuid": $(element).attr("uuid"),
		"event": event.type,
		"payload": event,
	}
	
	jfly.sendMessage(message);
};

jfly.sendMessage = function(message) {
	jfly.connection.send(jfly.toString(message));
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

/*
 * Find all generated ui components and listen for all possible events
 */
jfly.init = function() {
	$("[uuid]").forEach(function(elem) {
		var elem = $(elem);
		
		var events = elem.attr("registeredEvents");
		
		if (events !== "") {
			elem.on(events, function(event){
				jfly.handleEvent(this, event);
			});
		}
	});
};

/*
 * Init jfly when page is ready
 */
Zepto(function($) {
	jfly.init();
});