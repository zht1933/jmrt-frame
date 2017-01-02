package com.webside.cube.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FilterUtil {
	public static Map<String, Object> getFilter(String filterStr) throws IOException {
	    Map map = new HashMap();
	    ObjectMapper mapper = new ObjectMapper();
	    if (filterStr != null)
	    {
	      List<Map> filters = (List)mapper.readValue(filterStr, 
	        List.class);
	      for (Map filter : filters) {
	        map.put((String)filter.get("property"), filter.get("value"));
	      }
	    }
	    return map;
	  }
}
