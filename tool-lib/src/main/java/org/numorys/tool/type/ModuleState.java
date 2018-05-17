package org.numorys.tool.type;

import java.util.HashMap;
import java.util.Map;

import org.numorys.tool.ast.Function;

public class ModuleState {
	private Map<String,Function> functions=new HashMap<>();
	
	public Map<String, Function> getFunctions() {
		return functions;
	}
	
}
