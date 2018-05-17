package org.numorys.tool.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.numorys.tool.TestSample;
import org.numorys.tool.ast.ASTNode;
import org.numorys.tool.ast.ASTBuilder;
import org.numorys.tool.ast.Module;
import org.numorys.tool.parser.NumorysParser.ModuleContext;

@RunWith(Parameterized.class)
public class ParseTest {

	@Parameters(name="{0}")
	public static Iterable<TestSample> data() {
	    return TestSample.getTestSamples();
	}
	
	
	
	public static void assertModule(String resource,Module expected) {
		try (InputStream is=ParseTest.class.getResourceAsStream(resource)){
			assertNotNull(is);
			
			ASTBuilder v=new ASTBuilder();
			NumorysLexer l=new NumorysLexer(CharStreams.fromStream(is, StandardCharsets.UTF_8));
			CommonTokenStream s=new CommonTokenStream(l);
			NumorysParser p=new NumorysParser(s);
			p.setBuildParseTree(true);
			p.getInterpreter().setPredictionMode(PredictionMode.SLL);
			p.addErrorListener(new BaseErrorListener() {
				@Override
				public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
						int charPositionInLine, String msg, RecognitionException e) {
					fail(msg);
				}
				
			});
			p.addErrorListener(new ConsoleErrorListener());
			ModuleContext ctx=p.module();
			new FailingOnErrorVisitor().visit(ctx);
			
			List<ASTNode> ns=v.visitModule(ctx);
			assertNotNull(ns);
			assertEquals(1,ns.size());
			ASTNode n0=ns.get(0);
			assertTrue(n0 instanceof Module);
			assertEquals(expected, n0);
			
			
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail(ioe.getMessage());
		}

	}
	
	static class FailingOnErrorVisitor extends AbstractParseTreeVisitor<Void>{

		@Override
		public Void visitErrorNode(ErrorNode node) {
			fail(node.toString());
			return null;
		}
		
	}
	
	@Parameter 
	public TestSample sample;

	@Test
	public void test() {
		assertModule(sample.getResource(),sample.getModule());
	}
	
	
}
