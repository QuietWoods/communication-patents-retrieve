package com.icdd.lucence;

public class GeneralHashCode {
private volatile int hashCode;
@Override 
public int hashCode(){
	int result = hashCode;
	
	System.out.println(result);
	if(result == 0){
		result = 17;
		hashCode = result;
	}
	return result;
	
}
}
