package com.webside.cube.util;

import java.util.UUID;

public class UUIDUtil {
	public static String getId(){
		UUID uuid = UUID.randomUUID();
		long mostSigBits = uuid.getMostSignificantBits();
		long leastSigBits = uuid.getLeastSignificantBits();
		return (digits(mostSigBits >> 32, 8) +
				digits(mostSigBits >> 16, 4) +
				digits(mostSigBits, 4) +
				digits(leastSigBits >> 48, 4) +
				digits(leastSigBits, 12));
	}
    private static String digits(long val, int digits) {
    	long hi = 1L << (digits * 4);
    	return Long.toHexString(hi | (val & (hi - 1))).substring(1);
    }
    public static void main(String[] args){
    	System.out.println(getId().length());
    }
}
