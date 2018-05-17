package org.numorys.tool.wat;

public abstract class WatExport extends WatItem{
	private String exportName;
	
	
	
	public WatExport(String exportName) {
		super();
		this.exportName = exportName;
	}

	public String getExportName() {
		return exportName;
	}
}
