package at.spot.core.shell.command;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DefaultPromptProvider extends org.springframework.shell.plugin.support.DefaultPromptProvider {

	@Override
	public String getPrompt() {
		return "hw-shell>";
	}

	@Override
	public String getProviderName() {
		return "My prompt provider";
	}

}
