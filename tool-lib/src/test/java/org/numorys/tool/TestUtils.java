package org.numorys.tool;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class TestUtils {


	public static List<String> run(String...command) throws IOException {
		ProcessBuilder pb=new ProcessBuilder(command);
		pb.redirectErrorStream(true);
		Process p=pb.start();
		
		List<String> output=TestUtils.readStreamLines(p.getInputStream());
		try {
			int code=p.waitFor();
			if (code!=0) {
				fail("Error while running:"+code+"\n"+output.toString());
			}
		} catch (InterruptedException ie) {
			ie.printStackTrace();
			fail(ie.getMessage());
		}
		return output;
	}
	
	
	public static List<String> readStreamLines(InputStream is) throws IOException {
		try(BufferedReader reader = 
                new BufferedReader(new InputStreamReader(is))){
			List<String> lines=new LinkedList<>();
			String line = null;
			while ( (line = reader.readLine()) != null) {
			   lines.add(line);
			}
			return lines;
		}
	}
	
	public static String readStream(InputStream is) throws IOException {
		return String.join("\n", readStreamLines(is));
	}

	public static String readResource(String resource) {
		try (InputStream is=TestUtils.class.getResourceAsStream(resource)){
			return String.join("\n", readStreamLines(is));
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail(ioe.getMessage());
		}
		return null;
	}
}
