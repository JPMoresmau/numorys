package org.numorys.tool.wat;

import java.util.LinkedList;
import java.util.List;

public class WatFunction extends WatItem {
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
	
	private List<WatInstruction> instructions=new LinkedList<>();
	
	public List<WatInstruction> getInstructions() {
		return instructions;
	}
	
	@Override
	public void toString(WatWriter sb) {
		sb.append("(func $");
		sb.append(name);
		if (parameters.size()>0) {
			sb.append(" (param");
			for (String p:parameters) {
				sb.append(" ");
				sb.append(p);
			}
			sb.append(")");
		}
		sb.append(" (result ");
		sb.append(result);
		sb.append(")\n");
		
		sb.incrIndent(2);
		for (WatInstruction i:getInstructions()) {
			sb.indent();
			i.toString(sb);
			sb.newLine();
		}
		sb.decrIndent(2);
		sb.indent();
		sb.append(")");
		
	}
	

}
