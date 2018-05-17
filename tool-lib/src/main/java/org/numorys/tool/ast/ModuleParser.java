package org.numorys.tool.ast;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.numorys.tool.parser.NumorysLexer;
import org.numorys.tool.parser.NumorysParser;
import org.numorys.tool.parser.NumorysParser.ModuleContext;

public class ModuleParser {

	public ModuleParser() {
		
	}
	
	public Module parse(InputStream is) throws IOException {
		ASTBuilder v=new ASTBuilder();
		NumorysLexer l=new NumorysLexer(CharStreams.fromStream(is, StandardCharsets.UTF_8));
		CommonTokenStream s=new CommonTokenStream(l);
		NumorysParser p=new NumorysParser(s);
		p.setBuildParseTree(true);
		p.getInterpreter().setPredictionMode(PredictionMode.SLL);
		
		ModuleContext ctx=p.module();
		List<ASTNode> ns=v.visitModule(ctx);
		return (Module)ns.get(0);

	}

}
