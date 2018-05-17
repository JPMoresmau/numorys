package org.numorys.tool;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.numorys.tool.ast.Binding;
import org.numorys.tool.ast.Function;
import org.numorys.tool.ast.Invocation;
import org.numorys.tool.ast.Module;
import org.numorys.tool.ast.Name;
import org.numorys.tool.ast.Number;
import org.numorys.tool.ast.Signature;
import org.numorys.tool.ast.Type;
import org.numorys.tool.type.TypecheckedModule;

public class TestSample {
	private String resource;
	
	private Module module;
	
	private TypecheckedModule typecheckedModule;
	
	private String wat;
	
	private String mainResult;
	




	public TestSample(String resource) {
		super();
		this.resource = resource;
	}



	public String getResource() {
		return resource;
	}



	public void setResource(String resource) {
		this.resource = resource;
	}



	public Module getModule() {
		return module;
	}



	public void setModule(Module module) {
		this.module = module;
	}



	public TypecheckedModule getTypecheckedModule() {
		return typecheckedModule;
	}



	public void setTypecheckedModule(TypecheckedModule typecheckedModule) {
		this.typecheckedModule = typecheckedModule;
	}

	public String getWat() {
		return wat;
	}
	
	public void setWat(String wat) {
		this.wat = wat;
	}
	
	public String getMainResult() {
		return mainResult;
	}



	public void setMainResult(String mainResult) {
		this.mainResult = mainResult;
	}
	
	@Override
	public String toString() {
		return resource;
	}


	private static TestSample empty() {
		TestSample ts=new TestSample("/samples/empty.nmr");
		ts.setModule(new Module("empty"));
		ts.setTypecheckedModule(new TypecheckedModule(ts.getModule()));
		ts.setWat("(module\n)");
		return ts;
	}
	
	private static TestSample addTyped() {
		TestSample ts=new TestSample("/samples/add.nmr");
		
		Module m=new Module("add");
		Signature fsAdd=new Signature("add");
		fsAdd.getParameterTypes().add(Type.INT_32);
		fsAdd.getParameterTypes().add(Type.INT_32);
		fsAdd.getParameterTypes().add(Type.INT_32);
		m.getSignatures().add(fsAdd);
		
		Binding bAdd=new Binding("add");
		bAdd.getParameters().add(new Name("a"));
		bAdd.getParameters().add(new Name("b"));
		Invocation iPlus=new Invocation(new Name("+"),new Name("a"),new Name("b"));
		iPlus.setInfix(true);
		bAdd.setExpression(iPlus);
		
		m.getBindings().add(bAdd);
		
		Signature fsMain=new Signature("main");
		fsMain.getParameterTypes().add(Type.INT_32);
		m.getSignatures().add(fsMain);
		
		Binding bMain=new Binding("main");
		Invocation iAdd=new Invocation(new Name("add"),new Number("1"),new Number("2"));
		bMain.setExpression(iAdd);
		
		m.getBindings().add(bMain);
		
		ts.setModule(m);
		TypecheckedModule tcm=new TypecheckedModule(ts.getModule());
		Iterator<Binding> itb=tcm.getModule().getBindings().iterator();
		
		Function add=new Function("add");
		add.setSignature(fsAdd);
		Binding tcbAdd=itb.next();
		tcbAdd.getExpression().getTypes().add(Type.INT_32);
		for (Name n:tcbAdd.getParameters()) {
			n.getTypes().add(Type.INT_32);
		}
		add.getBindings().add(tcbAdd);
		
		
		Function main=new Function("main");
		main.setSignature(fsMain);
		Binding tcbMain=itb.next();
		tcbMain.getExpression().getTypes().add(Type.INT_32);
		main.getBindings().add(tcbMain);
		
		tcm.getFunctions().add(add);
		tcm.getFunctions().add(main);
		
		
		ts.setTypecheckedModule(tcm);
		
		ts.setWat("(module\n"
				+ "  (func $add (param i32 i32) (result i32)\n"
			    + "    get_local 0\n"
			    + "    get_local 1\n"
			    + "    i32.add\n"
			    + "  )\n"
			    + "  (func $main (result i32)\n"
			    + "    i32.const 1\n"
			    + "    i32.const 2\n"
			    + "    call $add\n"
			    + "  )\n"
			    + "  (export \"add\" (func $add))\n"
			    + "  (export \"main\" (func $main))\n"
			    + ")"
				);
		
		ts.setMainResult("i32:3");
		
		return ts;
	}
	
	public static List<TestSample> getTestSamples() {
		return Arrays.asList(empty(),addTyped());
	}
	
	public static List<TestSample> getTestSamplesWithResult() {
		return getTestSamples().stream().filter(ts->ts.getMainResult()!=null).collect(Collectors.toList());
	}
}
