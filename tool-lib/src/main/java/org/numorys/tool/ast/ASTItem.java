package org.numorys.tool.ast;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonTypeInfo(use=Id.CLASS, include=As.PROPERTY, property="class")
public abstract class ASTItem implements Cloneable{
	
	public String toString() {
		try {
			return new ObjectMapper()
					.writerWithDefaultPrettyPrinter()
					.writeValueAsString(this);
		} catch (Exception e) {
			throw new ASTException("Error writing JSON",e);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return toString().equals(obj.toString());
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	public ASTItem clone() {
		String s=toString();
		try {
			return new ObjectMapper().readValue(s, ASTItem.class);
		} catch (Exception e) {
			throw new ASTException("Error cloning via JSON",e);
		}
	}
	
	public abstract void accept(ASTVisitor v);
}
