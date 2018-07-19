package at.spot.core.infrastructure.support.impex;

public enum ImpexMergeMode {
	ADD("add"), APPEND("append"), REMOVE("remove"), REPLACE("replace");

	private String code;

	private ImpexMergeMode(String code) {
		this.code = code;
	}
}
