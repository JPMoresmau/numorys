package org.numorys.tool;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.numorys.tool.ast.Binding;
import org.numorys.tool.ast.CompoundType;
import org.numorys.tool.ast.Function;
import org.numorys.tool.ast.Invocation;
import org.numorys.tool.ast.Module;
import org.numorys.tool.ast.Name;
import org.numorys.tool.ast.Signature;
import org.numorys.tool.ast.SimpleType;
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
		ts.setModule(new Module("Empty"));
		ts.setTypecheckedModule(new TypecheckedModule(ts.getModule()));
		ts.setWat("(module\n)");
		return ts;
	}
	
	private static TestSample addI32() {
		return add("addi32.nmr",SimpleType.INT_32,"i32","3");
	}
	
	private static TestSample addI64() {
		return add("addi64.nmr",SimpleType.INT_64,"i64","3");
	}
	
	private static TestSample addF32() {
		return add("addf32.nmr",SimpleType.FLOAT_32,"f32","3.000000");
	}
	
	private static TestSample addF64() {
		return add("addf64.nmr",SimpleType.FLOAT_64,"f64","3.000000");
	}
	
	private static TestSample add(String resource,Type t,String watType,String result) {
		
		TestSample ts=new TestSample("/samples/"+resource);
		
		Module m=new Module("Add");
		Signature fsAdd=new Signature("add");
		fsAdd.setType(new CompoundType(t,t,t));
		m.getSignatures().add(fsAdd);
		
		Binding bAdd=new Binding("add");
		bAdd.getParameters().add(new Name("a"));
		bAdd.getParameters().add(new Name("b"));
		Name nPlus=new Name("+");
		Name nA=new Name("a");
		Name nB=new Name("b");
		Invocation iPlus=new Invocation(nPlus,nA,nB);
		iPlus.setInfix(true);
		bAdd.setExpression(iPlus);
		
		m.getBindings().add(bAdd);
		
		Signature fsMain=new Signature("main");
		fsMain.setType(t);
		m.getSignatures().add(fsMain);
		
		Binding bMain=new Binding("main");
		Name nAdd=new Name("add");
		org.numorys.tool.ast.Number n1=new org.numorys.tool.ast.Number("1");
		org.numorys.tool.ast.Number n2=new org.numorys.tool.ast.Number("2");
		Invocation iAdd=new Invocation(nAdd,n1,n2);
		bMain.setExpression(iAdd);
		
		m.getBindings().add(bMain);
		// clone so we can add types
		ts.setModule((Module)m.clone());
		TypecheckedModule tcm=new TypecheckedModule(m);
		
		Function add=new Function("add");
		add.setSignature(fsAdd);
		bAdd.getExpression().setType(t);
		for (Name n:bAdd.getParameters()) {
			n.setType(t);
		}
		nA.setType(t);
		nB.setType(t);
		nPlus.setType(fsAdd.getType());
		
		add.getBindings().add(bAdd);
		
		
		Function main=new Function("main");
		main.setSignature(fsMain);
		bMain.getExpression().setType(t);
		nAdd.setType(fsAdd.getType());
		n1.setType(t);
		n2.setType(t);
		
		main.getBindings().add(bMain);
		
		tcm.getFunctions().add(add);
		tcm.getFunctions().add(main);
		
		
		ts.setTypecheckedModule(tcm);
		
		ts.setWat(("(module\n"
				+ "  (func $add (param TYPE TYPE) (result TYPE)\n"
			    + "    get_local 0\n"
			    + "    get_local 1\n"
			    + "    TYPE.add\n"
			    + "  )\n"
			    + "  (func $main (result TYPE)\n"
			    + "    TYPE.const 1\n"
			    + "    TYPE.const 2\n"
			    + "    call $add\n"
			    + "  )\n"
			    + "  (export \"add\" (func $add))\n"
			    + "  (export \"main\" (func $main))\n"
			    + ")").replaceAll("TYPE", watType)
				);
		
		ts.setMainResult(watType+":"+result);
		
		return ts;
	}
	
	
	
	public static List<TestSample> getTestSamples() {
		return Arrays.asList(empty(),addI32(),addI64(),addF32(),addF64());
	}
	
	public static List<TestSample> getTestSamplesWithResult() {
		return getTestSamples().stream().filter(ts->ts.getMainResult()!=null).collect(Collectors.toList());
	}
}
