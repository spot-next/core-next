package at.spot.jfly.event;

public enum JsEvent {
	click("click"),
	mousedown("mousedown"),
	mouseup("mouseup"),
	focus("focus"),
	blur("blur"),
	keydown("keydown"),
	change("change"),
	dblclick("dblclick"),
	mousemove("mousemove"),
	mouseover("mouseover"),
	mouseout("mouseout"),
	mousewheel("mousewheel"),
	keyup("keyup"),
	keypress("keypress"),
	textInput("textInput"),
	touchstart("touchstart"),
	touchmove("touchmove"),
	touchend("touchend"),
	touchcancel("touchcancel"),
	resize("resize"),
	scroll("scroll"),
	zoom("zoom"),
	select("select"),
	submit("submit"),
	reset("reset");

	private String id;

	private JsEvent(final String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return this.id;
	}
}
