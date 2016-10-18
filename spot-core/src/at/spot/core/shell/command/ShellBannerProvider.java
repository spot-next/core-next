package at.spot.core.shell.command;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultBannerProvider;
import org.springframework.stereotype.Component;

import at.spot.core.infrastructure.service.ConfigurationService;
import at.spot.core.infrastructure.spring.support.Registry;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ShellBannerProvider extends DefaultBannerProvider {

	@Override
	public String getBanner() {
		StringBuffer buf = new StringBuffer();
		buf.append("");
		return buf.toString();
	}

	@Override
	public String getVersion() {
		ConfigurationService configService = Registry.getApplicationContext().getBean(ConfigurationService.class,
				"configurationService");

		return configService.getString("spot.core.version");
	}

	@Override
	public String getProviderName() {
		return "spOt core shell";
	}
}