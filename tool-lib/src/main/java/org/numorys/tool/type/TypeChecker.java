package org.numorys.tool.type;

import java.util.Iterator;
import java.util.List;

import org.numorys.tool.ast.Binding;
import org.numorys.tool.ast.Function;
import org.numorys.tool.ast.Module;
import org.numorys.tool.ast.Name;
import org.numorys.tool.ast.Signature;
import org.numorys.tool.ast.Type;

public class TypeChecker {

	public TypecheckedModule checkModule(Module m) {
		TypecheckedModule tm=new TypecheckedModule(m);
		
		ModuleState ms=new ModuleState();
		
		createFunctions(tm, ms);
		
		typeCheck(tm, ms);
		
		return tm;
	}
	
	
	private void createFunctions(TypecheckedModule tm,ModuleState ms) {
		for (Binding b:tm.getModule().getBindings()) {
			Function f=ms.getFunctions().get(b.getName());
			if (f==null) {
				f=new Function(b.getName());
				ms.getFunctions().put(b.getName(), f);
				tm.getFunctions().add(f);
			}
			f.getBindings().add(b);
		}
		for (Signature s:tm.getModule().getSignatures()) {
			Function f=ms.getFunctions().get(s.getName());
			if (f==null) {
				tm.getErrors().add(new TypeCheckError("No binding for function "+s.getName()));
			} else if (f.getSignature()!=null) {
				tm.getErrors().add(new TypeCheckError("Duplicate signature for function "+s.getName()));
			} else {
				f.setSignature(s);
			}
		}
		for (Function f:tm.getFunctions()) {
			int cnt=-1;
			int idx=0;
			for(Binding b:f.getBindings()) {
				if (cnt==-1) {
					cnt=b.getParameters().size();
				} else if (cnt!=b.getParameters().size()){
					tm.getErrors().add(new TypeCheckError("Mismatch number of parameters in binding "+idx+" for function "+f.getName()));
				}
				idx++;
			}
		}
	}
	
	private void typeCheck(TypecheckedModule tm,ModuleState ms) {
		for (Function f:tm.getFunctions()) {
			List<Type> types=null;
			if (f.getSignature()!=null) {
				types=f.getSignature().getParameterTypes();
				for(Binding b:f.getBindings()) {
					Iterator<Type> it=types.iterator();
					for (Name n:b.getParameters()) {
						n.getTypes().add(it.next());
					}
					while (it.hasNext()) {
						b.getExpression().getTypes().add(it.next());
					}
				}
			}
		}
	}
	
}
