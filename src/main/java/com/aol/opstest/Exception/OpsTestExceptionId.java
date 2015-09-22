package com.aol.opstest.Exception;

public class OpsTestExceptionId {
	private final Long exceptionId;
    private String name;

    private OpsTestExceptionId( String name, int id ) {
    	this.name = name;
    	this.exceptionId = Long.valueOf(id);
    }
    
    public String toString() { return this.name; }
    public Long value() { return this.exceptionId; }
    
    //-----------------------------------------
    // EXCEPTION DEFINITIONS
    //-----------------------------------------
    
    // BASIC EXCEPTIONS
    public static final OpsTestExceptionId INITIALIZATION_FAILED = 
        	new OpsTestExceptionId("OpsTest Initialization Exception: " +
        			"Initialization of class not performed.", 0x00000);
}
