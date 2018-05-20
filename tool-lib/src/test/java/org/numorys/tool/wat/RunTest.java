package org.numorys.tool.wat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.numorys.tool.TestSample;
import org.numorys.tool.TestUtils;
import org.numorys.tool.ast.Module;
import org.numorys.tool.ast.ModuleParser;
import org.numorys.tool.parser.ParseTest;
import org.numorys.tool.type.TypeChecker;
import org.numorys.tool.type.TypecheckedModule;

@RunWith(Parameterized.class)
public class RunTest {

	public RunTest() {
		
	}

	public void runMain(String resource,String expected) {
		try (InputStream is=ParseTest.class.getResourceAsStream(resource)){
			assertNotNull(is);
			ModuleParser mp=new ModuleParser();
			Module m=mp.parse(is);
			assertNotNull(m);
			TypeChecker tc=new TypeChecker();
			TypecheckedModule tcm=tc.checkModule(m);
			assertTrue(tcm.getErrors().isEmpty());
			
			Generator gen=new Generator();
			WatModule wm=gen.generate(tcm);
			assertNotNull(wm);
			//System.out.println(tcm.toString());
			try (InputStream isProps=ParseTest.class.getResourceAsStream("test.properties")){
				
				Properties p=new Properties();
				if (isProps!=null) {
					p.load(isProps);
				}
				String wabtPath=p.getProperty("wabt.path", "../../wabt/bin");
				
				Path f=Files.createTempDirectory("wabt");
				Path i=f.resolve("input.wat");
				Files.write(i, wm.toString().getBytes(StandardCharsets.UTF_8));
				Path o=f.resolve("input.wasm");
				TestUtils.run(wabtPath+"/wat2wasm",i.toFile().getAbsolutePath(),"-o",o.toFile().getAbsolutePath());
				List<String> outputs=TestUtils.run(wabtPath+"/wasm-interp",o.toFile().getAbsolutePath(),"--run-all-exports");
				
				i.toFile().delete();
				o.toFile().delete();
				
				boolean found=false;
				for (String output:outputs) {
					if (output.startsWith("main()")) {
						int ix=output.indexOf("=>");
						String result=output.substring(ix+2).trim();
						assertEquals(expected,result);
						found=true;
					}
				}
				assertTrue(found);
			}
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail(ioe.getMessage());
		}
	}
	
	
	

	@Parameters(name="{0}")
	public static Iterable<TestSample> data() {
	    return TestSample.getTestSamplesWithResult();
	}
	
	
	@Parameter 
	public TestSample sample;
	
	@Test
	public void test() {
		runMain(sample.getResource(), sample.getMainResult());
	}
}
