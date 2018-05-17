package org.numorys.tool.type;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.numorys.tool.TestSample;

@RunWith(Parameterized.class)
public class TypeCheckerTest {

	@Parameters(name="{0}")
	public static Iterable<TestSample> data() {
	    return TestSample.getTestSamples();
	}
	
	
	@Parameter 
	public TestSample sample;

	@Test
	public void test() {
		TypeChecker tc=new TypeChecker();
		TypecheckedModule tcm=tc.checkModule(sample.getModule());
		assertNotNull(tcm);
		assertEquals(sample.getTypecheckedModule().toString(),tcm.toString());
	}
	
}
