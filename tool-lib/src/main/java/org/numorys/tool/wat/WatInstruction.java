package org.numorys.tool.wat;

public abstract class WatInstruction extends WatItem {

	public WatInstruction() {
		
	}

	public static class WatGetLocal extends WatInstruction {
		private int index;
			
		public WatGetLocal(int index) {
			this.index=index;
		}

		public int getIndex() {
			return index;
		}
		
		@Override
		public void toString(WatWriter sb) {
			sb.append("get_local ");
			sb.append(String.valueOf(index));
		}
		
		
	}
	
	
	public static class WatI32Add extends WatInstruction {
		@Override
		public void toString(WatWriter sb) {
			sb.append("i32.add");
		}
	
	}
	
	public static class WatI32Const extends WatInstruction {
		private String value;
		
		public WatI32Const(String value) {
			super();
			this.value = value;
		}

		@Override
		public void toString(WatWriter sb) {
			sb.append("i32.const ");
			sb.append(value);
			
		}
	}
	
	public static class WatCall extends WatInstruction {
		private String name;
		
		public WatCall(String name) {
			super();
			this.name = name;
		}


		@Override
		public void toString(WatWriter sb) {
			sb.append("call $");
			sb.append(name);
		}
	}
	
	
}
