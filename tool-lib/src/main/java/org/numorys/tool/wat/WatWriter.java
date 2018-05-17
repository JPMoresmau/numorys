package org.numorys.tool.wat;

public class WatWriter {
	private int indent = 0;
	
	private StringBuilder sb=new StringBuilder();
	
	public WatWriter() {
		
	}
	
	@Override
	public String toString() {
		return sb.toString();
	}

	public void indent() {
		for (int a=0;a<indent;a++) {
			sb.append(" ");
		}
	}

	public void incrIndent(int incr) {
		indent+=incr;
	}
	
	public void decrIndent(int decr) {
		indent-=decr;
	}
	
	public void append(String s) {
		sb.append(s);
	}
	
	public void newLine() {
		sb.append("\n");
	}
}
