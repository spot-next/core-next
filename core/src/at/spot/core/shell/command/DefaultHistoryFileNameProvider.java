package at.spot.core.shell.command;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DefaultHistoryFileNameProvider
		extends org.springframework.shell.plugin.support.DefaultHistoryFileNameProvider {

	public String getHistoryFileName() {
		return "my.log";
	}

	@Override
	public String getProviderName() {
		return "My history file name provider";
	}

}
