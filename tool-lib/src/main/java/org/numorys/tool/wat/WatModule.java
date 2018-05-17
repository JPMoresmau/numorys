package org.numorys.tool.wat;

import java.util.LinkedList;
import java.util.List;

public class WatModule extends WatItem {
	private List<WatFunction> functions=new LinkedList<>();
	private List<WatExport> exports=new LinkedList<>();
	
	public WatModule() {
		
	}
	
	public List<WatExport> getExports() {
		return exports;
	}
	
	public List<WatFunction> getFunctions() {
		return functions;
	}
	
	@Override
	public void toString(WatWriter sb) {
		sb.indent();
		sb.append("(module\n");
		sb.incrIndent(2);
		for (WatFunction f:getFunctions()) {
			sb.indent();
			f.toString(sb);
			sb.newLine();
		}
		for (WatExport e:getExports()) {
			sb.indent();
			e.toString(sb);
			sb.newLine();
		}
		sb.decrIndent(2);
		sb.append(")");
	}

}
