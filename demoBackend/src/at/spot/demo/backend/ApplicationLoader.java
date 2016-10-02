package at.spot.demo.backend;

import at.spot.core.infrastructure.exception.ModelNotFoundException;
import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.persistence.model.User;

public class ApplicationLoader extends at.spot.core.ApplicationLoader {
	@Override
	public void run() {
		User user = getModelService().create(User.class);

		user.name = "test";
		getLoggingService().info(user.name);

		try {
			getModelService().save(user);
		} catch (ModelSaveException e) {
			getLoggingService().exception(e.getMessage());
		}

		try {
			User loadedUser = getModelService().get(User.class, user.pk);
			getLoggingService().info("loaded user again");
		} catch (ModelNotFoundException e) {
			getLoggingService().exception(e.getMessage());
		}

	}
}
