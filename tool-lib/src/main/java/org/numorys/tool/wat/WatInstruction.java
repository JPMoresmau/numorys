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
	
	public static class WatSimpleInstruction extends WatInstruction {
		private String instruction;
		
		public WatSimpleInstruction(String instruction) {
			super();
			this.instruction = instruction;
		}

		@Override
		public void toString(WatWriter sb) {
			sb.append(instruction);
		}
	}
	
	public abstract static class WatStringValueInstruction extends WatInstruction {
		private String prefix;
		private String value;
		
		public WatStringValueInstruction(String prefix, String value) {
			super();
			this.prefix = prefix;
			this.value = value;
		}
		
		@Override
		public void toString(WatWriter sb) {
			sb.append(prefix);
			sb.append(value);
		}
		
	}
		
	public static WatSimpleInstruction WatI32Add = new WatSimpleInstruction("i32.add");
	public static WatSimpleInstruction WatI64Add = new WatSimpleInstruction("i64.add");
	public static WatSimpleInstruction WatF32Add = new WatSimpleInstruction("f32.add");
	public static WatSimpleInstruction WatF64Add = new WatSimpleInstruction("f64.add");
	
	
	public static class WatI32Const extends WatStringValueInstruction {
		public WatI32Const(String value) {
			super("i32.const ",value);
		}
	}
	
	public static class WatI64Const extends WatStringValueInstruction {
		public WatI64Const(String value) {
			super("i64.const ",value);
		}
	}
	
	public static class WatF32Const extends WatStringValueInstruction {
		public WatF32Const(String value) {
			super("f32.const ",value);
		}
	}
	
	public static class WatF64Const extends WatStringValueInstruction {
		public WatF64Const(String value) {
			super("f64.const ",value);
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
