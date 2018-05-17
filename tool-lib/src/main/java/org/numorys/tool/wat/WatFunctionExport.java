package org.numorys.tool.wat;

public class WatFunctionExport extends WatExport {
	private String functionName;
	
	
	public WatFunctionExport(String exportName, String functionName) {
		super(exportName);
		this.functionName=functionName;
	}
	
	public String getFunctionName() {
		return functionName;
	}
	
	
	@Override
	public void toString(WatWriter sb) {
		sb.append("(export \"");
		sb.append(getExportName());
		sb.append("\" (func $");
		sb.append(functionName);
		sb.append("))");
		
	}
}
