package io.spotnext.core.infrastructure.support.impex;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import io.spotnext.core.types.Item;

public class ValueResolutionDescriptorParser {

	public List<Node> parse(String desc, Class<Item> itemType) {
		desc = desc.replace(" ", "");
		return parse(desc, 0, desc.length());
	}

	public List<Node> parse(String desc, int start, int end) {
		// I know .. it's not pretty ...
		List<Node> nodes = new ArrayList<>();

		Node tempNode = null;
		String tempToken = "";

		int lastDelimiter = 0;

		for (int x = start; x < end; x++) {
			char c = desc.charAt(x);

			// we found a simple leave node
			if (c == ',') {
				lastDelimiter = x;
				if (StringUtils.isNotBlank(tempToken)) {
					tempNode = new Node(tempToken);
					nodes.add(tempNode);
					System.out.println("Consumed: " + tempToken);
					tempToken = "";
				} else {
					continue;
				}
			} else if (c == '(') {
				// this is a tree node
				if (StringUtils.isNotBlank(tempToken)) {
					tempNode = new Node(tempToken);
					nodes.add(tempNode);
					System.out.println("Consumed: " + tempToken);
					tempToken = "";

					String subDesc = StringUtils.substring(desc, x + 1, desc.length());

					List<Node> children = parse(subDesc, 0, subDesc.length());
					tempNode.nodes.addAll(children);

					// moves the cursor forward -> the node's toString() has to be the exact content
					// of it's consumed text parts
					x = lastDelimiter + tempNode.toString().length() - 1;
				}
			} else if (c == ')') {
				if (StringUtils.isNotBlank(tempToken)) {
					System.out.println("Consumed: " + tempToken);
					tempNode = new Node(tempToken);
					nodes.add(tempNode);
				}
				break;
			} else {
				tempToken += c;
			}
		}

		return nodes;
	}

	public static class Node {
		private String name;
		private final List<Node> nodes = new ArrayList<>();

		public Node(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<Node> getNodes() {
			return nodes;
		}

		@Override
		public String toString() {
			return name + (nodes.size() > 0
					? "(" + nodes.stream().map(n -> n.toString()).collect(Collectors.joining(",")) + ")"
					: "");
		}
	}
}
