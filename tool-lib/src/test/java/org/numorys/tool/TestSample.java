package org.numorys.tool;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.numorys.tool.ast.Binding;
import org.numorys.tool.ast.CompoundType;
import org.numorys.tool.ast.Expression;
import org.numorys.tool.ast.Function;
import org.numorys.tool.ast.Invocation;
import org.numorys.tool.ast.Module;
import org.numorys.tool.ast.Name;
import org.numorys.tool.ast.Number;
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
		for (Expression n:bAdd.getParameters()) {
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
				+ "  (func $add (param TYPE) (param TYPE) (result TYPE)\n"
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
	
	private static TestSample factorialRecursive() {
		Type valueType=SimpleType.INT_32;
		TestSample ts=new TestSample("/samples/facRecursive.nmr");
		Module m=new Module("Factorial");
		Signature fsFac=new Signature("factorial");
		fsFac.setType(new CompoundType(valueType,valueType));
		m.getSignatures().add(fsFac);
		
		Binding bZero=new Binding("factorial");
		Number n0=new Number("0");
		bZero.getParameters().add(n0);
		Number n1=new Number("1");
		bZero.setExpression(n1);
		
		m.getBindings().add(bZero);
		
		Binding bRec=new Binding("factorial");
		Name n=new Name("n");
		bRec.getParameters().add(n);
		
		Name nTimes=new Name("*");
		Name nA=new Name("n");
		Name nB=new Name("n");
		
		Name nFac=new Name("factorial");
		Name nMinus=new Name("-");
		Number n1A=new Number("1");
		Invocation iMinus=new Invocation(nMinus,nB,n1A);
		iMinus.setInfix(true);
		
		Invocation iFac=new Invocation(nFac,iMinus);
		
		Invocation iTimes=new Invocation(nTimes,nA,iFac);
		iTimes.setInfix(true);
		bRec.setExpression(iTimes);
		m.getBindings().add(bRec);
		
		Signature fsMain=new Signature("main");
		fsMain.setType(valueType);
		m.getSignatures().add(fsMain);
		
		Binding bMain=new Binding("main");
		Name nFac2=new Name("factorial");
		org.numorys.tool.ast.Number n5=new org.numorys.tool.ast.Number("5");
		Invocation iAdd=new Invocation(nFac2,n5);
		bMain.setExpression(iAdd);
		
		m.getBindings().add(bMain);
		
		ts.setModule((Module)m.clone());
		
		TypecheckedModule tcm=new TypecheckedModule(m);
		
		Function fac=new Function("factorial");
		tcm.getFunctions().add(fac);
		fac.setSignature(fsFac);
		fac.getBindings().add(bZero);
		bZero.getExpression().setType(valueType);
		n0.setType(valueType);
		fac.getBindings().add(bRec);
		n.setType(valueType);
		bRec.getExpression().setType(valueType);
		nTimes.setType(new CompoundType(valueType,valueType,valueType));
		nA.setType(valueType);
		nB.setType(valueType);
		n1A.setType(valueType);
		nMinus.setType(new CompoundType(valueType,valueType,valueType));
		iMinus.setType(valueType);
		nFac.setType(fsFac.getType());
		iFac.setType(valueType);
		
		Function main=new Function("main");
		tcm.getFunctions().add(main);
		main.setSignature(fsMain);
		main.getBindings().add(bMain);
		bMain.getExpression().setType(valueType);
		n5.setType(valueType);
		nFac2.setType(fsFac.getType());
		
		ts.setTypecheckedModule(tcm);
		
		ts.setWat(TestUtils.readResource("/samples/facRecursive.wat"));
		
		ts.setMainResult("i32:120");
		
		return ts;
	}
	
	
	
	public static List<TestSample> getTestSamples() {
		return Arrays.asList(empty(),addI32(),addI64(),addF32(),addF64(),factorialRecursive());
	}
	
	public static List<TestSample> getTestSamplesWithResult() {
		return getTestSamples().stream().filter(ts->ts.getMainResult()!=null).collect(Collectors.toList());
	}
}
