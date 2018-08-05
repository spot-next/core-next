package io.spotnext.core.infrastructure.support.impex;

import java.util.ArrayList;
import java.util.List;

import io.spotnext.core.types.Item;

public class WorkUnit {
	private Class<? extends Item> itemType;
	private ImpexCommand command;

	private final List<String> rawScriptRows = new ArrayList<>();

	private final List<ColumnDefinition> headerColumns = new ArrayList<>();
	private final List<List<String>> dataRows = new ArrayList<>();

	public Class<? extends Item> getItemType() {
		return itemType;
	}

	public void setItemType(Class<? extends Item> itemType) {
		this.itemType = itemType;
	}

	public ImpexCommand getCommand() {
		return command;
	}

	public void setCommand(ImpexCommand command) {
		this.command = command;
	}

	public List<ColumnDefinition> getHeaderColumns() {
		return headerColumns;
	}

	public void addHeaderColumn(ColumnDefinition column) {
		this.headerColumns.add(column);
	}

	public void setHeaderColumns(List<ColumnDefinition> columns) {
		if (columns != null) {
			this.headerColumns.addAll(columns);
		}
	}

	public List<List<String>> getDataRows() {
		return dataRows;
	}

	public void addDataRow(List<String> rowValues) {
		if (rowValues != null) {
			this.dataRows.add(rowValues);
		}
	}

	public void setDataRows(List<List<String>> rows) {
		this.dataRows.addAll(rows);
	}

	public List<String> getRawScriptRows() {
		return rawScriptRows;
	}

	public void addRawScriptRow(String row) {
		this.rawScriptRows.add(row);
	}

	public void setRawScriptRows(List<String> rows) {
		this.rawScriptRows.addAll(rows);
	}

}
