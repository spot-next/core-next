package at.spot.core.dataaccess.controller;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import at.spot.core.data.model.Item;
import at.spot.core.infrastructure.service.TypeService;

@RestController
@RequestMapping("/types")
public class TypesRestController {

	@Autowired
	TypeService typeService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody List<String> listItemTypes() {
		List<Class<? extends Item>> types = typeService.getAvailableTypes();

		List<String> itemTypes = new ArrayList<>();

		for (Class<? extends Item> c : types) {
			itemTypes.add(c.getName());
		}

		return itemTypes;
	}

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody Map<String, String> listItemAttributes(
			@RequestParam(value = "typeCode", required = true) String typeCode) {

		Class<? extends Item> type = typeService.getType(typeCode);
		Map<String, Member> members = typeService.getItemProperties(type);

		Map<String, String> propertyDefinition = new HashMap<>();

		for (String key : members.keySet()) {
			Member m = members.get(key);

			propertyDefinition.put(key, m.getDeclaringClass().getSimpleName());
		}

		return propertyDefinition;
	}
}
