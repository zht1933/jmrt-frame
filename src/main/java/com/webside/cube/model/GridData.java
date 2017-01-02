package com.webside.cube.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GridData extends HashMap<String, Object>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GridData(List<Map<String, Object>> items){
		put("items", items);
	}
	
	public GridData(List<Map<String, Object>> items, int total){
		put("items", items);
		put("total", total);
	}
}
