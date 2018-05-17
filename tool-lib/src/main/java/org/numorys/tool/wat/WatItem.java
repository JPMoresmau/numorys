package org.numorys.tool.wat;

public abstract class WatItem {

	public WatItem() {
		
	}
	
	public void toString(WatWriter sb,int indent) {
		sb.incrIndent(indent);
		try {
			toString(sb);
		} finally {
			sb.decrIndent(indent);
		}
	}
	
	public abstract void toString(WatWriter sb);
	

	@Override
	public String toString() {
		WatWriter sb=new WatWriter();
		toString(sb);
		return sb.toString();
	}
	

}
