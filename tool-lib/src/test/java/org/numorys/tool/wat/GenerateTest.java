package org.numorys.tool.wat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.numorys.tool.TestSample;

@RunWith(Parameterized.class)
public class GenerateTest {

	@Parameters(name="{0}")
	public static Iterable<TestSample> data() {
	    return TestSample.getTestSamples();
	}
	
	
	@Parameter 
	public TestSample sample;

	@Test
	public void test() {
		Generator gen=new Generator();
		WatModule wm=gen.generate(sample.getTypecheckedModule());
		assertNotNull(wm);
		assertEquals(sample.getWat(),wm.toString());
	}

}
