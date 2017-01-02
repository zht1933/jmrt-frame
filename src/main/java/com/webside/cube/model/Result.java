package com.webside.cube.model;

import java.util.HashMap;

public class Result extends HashMap<String, Object>{
	private static final long serialVersionUID = 1L;

	public Result(boolean success) {
		this(success, "");
	}

	public Result(boolean success, String msg) {
		super();
		put("success", success);
		put("msg", msg);
	}

	public Result(boolean success, Object data) {
		super();
		put("success", success);
		put("data", data);
	}

}
