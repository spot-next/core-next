package io.spotnext.core.infrastructure.support.impex;

import java.util.ArrayList;
import java.util.List;

import io.spotnext.infrastructure.type.Item;

/**
 * <p>WorkUnit class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class WorkUnit {
	private Class<? extends Item> itemType;
	private ImpexCommand command;

	private final List<String> rawScriptRows = new ArrayList<>();

	private final List<ColumnDefinition> headerColumns = new ArrayList<>();
	private final List<List<String>> dataRows = new ArrayList<>();

	/**
	 * <p>Getter for the field <code>itemType</code>.</p>
	 *
	 * @return a {@link java.lang.Class} object.
	 */
	public Class<? extends Item> getItemType() {
		return itemType;
	}

	/**
	 * <p>Setter for the field <code>itemType</code>.</p>
	 *
	 * @param itemType a {@link java.lang.Class} object.
	 */
	public void setItemType(Class<? extends Item> itemType) {
		this.itemType = itemType;
	}

	/**
	 * <p>Getter for the field <code>command</code>.</p>
	 *
	 * @return a {@link io.spotnext.infrastructure.support.impex.ImpexCommand} object.
	 */
	public ImpexCommand getCommand() {
		return command;
	}

	/**
	 * <p>Setter for the field <code>command</code>.</p>
	 *
	 * @param command a {@link io.spotnext.infrastructure.support.impex.ImpexCommand} object.
	 */
	public void setCommand(ImpexCommand command) {
		this.command = command;
	}

	/**
	 * <p>Getter for the field <code>headerColumns</code>.</p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public List<ColumnDefinition> getHeaderColumns() {
		return headerColumns;
	}

	/**
	 * <p>addHeaderColumn.</p>
	 *
	 * @param column a {@link io.spotnext.infrastructure.support.impex.ColumnDefinition} object.
	 */
	public void addHeaderColumn(ColumnDefinition column) {
		this.headerColumns.add(column);
	}

	/**
	 * <p>Setter for the field <code>headerColumns</code>.</p>
	 *
	 * @param columns a {@link java.util.List} object.
	 */
	public void setHeaderColumns(List<ColumnDefinition> columns) {
		if (columns != null) {
			this.headerColumns.addAll(columns);
		}
	}

	/**
	 * <p>Getter for the field <code>dataRows</code>.</p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public List<List<String>> getDataRows() {
		return dataRows;
	}

	/**
	 * <p>addDataRow.</p>
	 *
	 * @param rowValues a {@link java.util.List} object.
	 */
	public void addDataRow(List<String> rowValues) {
		if (rowValues != null) {
			this.dataRows.add(rowValues);
		}
	}

	/**
	 * <p>Setter for the field <code>dataRows</code>.</p>
	 *
	 * @param rows a {@link java.util.List} object.
	 */
	public void setDataRows(List<List<String>> rows) {
		this.dataRows.addAll(rows);
	}

	/**
	 * <p>Getter for the field <code>rawScriptRows</code>.</p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public List<String> getRawScriptRows() {
		return rawScriptRows;
	}

	/**
	 * <p>addRawScriptRow.</p>
	 *
	 * @param row a {@link java.lang.String} object.
	 */
	public void addRawScriptRow(String row) {
		this.rawScriptRows.add(row);
	}

	/**
	 * <p>Setter for the field <code>rawScriptRows</code>.</p>
	 *
	 * @param rows a {@link java.util.List} object.
	 */
	public void setRawScriptRows(List<String> rows) {
		this.rawScriptRows.addAll(rows);
	}

}
