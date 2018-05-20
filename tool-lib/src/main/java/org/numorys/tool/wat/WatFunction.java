package org.numorys.tool.wat;

import java.util.LinkedList;
import java.util.List;

public class WatFunction extends WatBlock {
	private String name;
	
	public WatFunction(String name) {
		this.name=name;
	}
	
	public String getName() {
		return name;
	}
	
	private List<String> parameters=new LinkedList<>();
	
	public List<String> getParameters() {
		return parameters;
	}
	
	private String result;
	
	public String getResult() {
		return result;
	}
	
	public void setResult(String result) {
		this.result = result;
	}
	
	
	@Override
	public void toString(WatWriter sb) {
		sb.append("(func $");
		sb.append(name);
		
		for (String p:parameters) {
			sb.append(" (param");
			sb.append(" ");
			sb.append(p);
			sb.append(")");
		}
			
		sb.append(" (result ");
		sb.append(result);
		sb.append(")");
		sb.newLine();
		super.toString(sb);
		sb.newLine();
		sb.indent();
		sb.append(")");
		
	}
	

}
