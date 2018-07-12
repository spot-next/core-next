package at.spot.core.infrastructure.support.impex;

public enum ImpexMergeMode {
	ADD("add"), REMOVE("remove"), REPLACE("replace");

	private String code;

	private ImpexMergeMode(String code) {
		this.code = code;
	}

}
