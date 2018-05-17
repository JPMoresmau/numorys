package org.numorys.tool.wat;

import java.util.List;

import org.numorys.tool.ast.Binding;
import org.numorys.tool.ast.Function;
import org.numorys.tool.ast.Type;
import org.numorys.tool.type.TypecheckedModule;

public class Generator {

	public Generator() {
	}

	public WatModule generate(TypecheckedModule module) {
		WatModule wm=new WatModule();
		
		GeneratorState gs=new GeneratorState();
		
		for (Function f:module.getFunctions()) {
			String name=getFunctionName(gs, f);
			WatFunction wf=new WatFunction(name);
			wm.getFunctions().add(wf);
			gs.getWatFunctions().put(wf.getName(), wf);
			if (!f.getName().startsWith("_")) {
				wm.getExports().add(new WatFunctionExport(f.getName(), name));
			}
			
			List<Type> types=f.getSignature().getParameterTypes();
			int cnt=types.size();
			for (Type t:types) {
				String tn=getTypeName(gs, t);
				if (cnt==1) {
					wf.setResult(tn);
				} else {
					wf.getParameters().add(tn);
				}
				cnt--;
			}
			
		}
		
		for (Function f:module.getFunctions()) {
			String name=gs.getFunction2Wat().get(f.getName());
			WatFunction wf=gs.getWatFunctions().get(name);
			generateInstructions(gs,f,wf);
		}
		
		return wm;
	}
	
	
	private String getFunctionName(GeneratorState gs,Function f) {
		String name=f.getName();
		gs.getFunction2Wat().put(f.getName(), name);
		return name;
	}
	
	private String getTypeName(GeneratorState gs,Type t) {
		return gs.getType2Wat().get(t);
	}
	
	private void generateInstructions(GeneratorState gs,Function f,WatFunction wf) {
		gs.setCurrentFunction(wf);
		for (Binding b:f.getBindings()) {
			b.accept(gs);
		}
		gs.setCurrentFunction(null);
	}
}
