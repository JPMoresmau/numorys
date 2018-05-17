package org.numorys.tool.type;

public class TypeCheckError {
	private String message;
	
	public TypeCheckError() {
		
	}
	
	
	
	public TypeCheckError(String message) {
		super();
		this.message = message;
	}



	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

}
