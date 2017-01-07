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
	
	var component = jfly.findComponent(message.uuid);
	component[message.method].apply(component, message.methodParams);
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

jfly.findComponent = function(uuid) {
	var component = $("[uuid='" + uuid + "']");
	
	return component;
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