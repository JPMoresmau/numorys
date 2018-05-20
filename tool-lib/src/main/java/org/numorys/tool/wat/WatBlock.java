package org.numorys.tool.wat;

import java.util.LinkedList;
import java.util.List;

public class WatBlock extends WatItem {

	
	public WatBlock() {
	}
	
	private List<WatInstruction> instructions=new LinkedList<>();
	
	public List<WatInstruction> getInstructions() {
		return instructions;
	}
	
	public void addInstruction(WatInstruction wi) {
		instructions.add(wi);
	}
	
	@Override
	public void toString(WatWriter sb) {
		sb.incrIndent(2);
		boolean sep=false;
		for (WatInstruction i:getInstructions()) {
			if (sep) {
				sb.newLine();
			}
			sep=true;
			sb.indent();
			i.toString(sb);
			
		}
		sb.decrIndent(2);
	}

}
