# Sample data import
During initial and sample data import by default the `.impex` files from the `spot-core` library are imported. If the prefix "template" is removed from the files in the `initial` and `sample` folders, these files will be used instead. Additional files can be added - although they have to be imported manually in the `Init` class:

```java
	@Override
	protected void importInitialData() throws ModuleInitializationException {
		super.importInitialData();

		importScript("/data/initial/custom.impex", "Importing custom stuff");
	}

	@Override
	protected void importSampleData() throws ModuleInitializationException {
		super.importSampleData();

		importScript("/data/sample/custom.impex", "Importing custom stuff");
	}
```
