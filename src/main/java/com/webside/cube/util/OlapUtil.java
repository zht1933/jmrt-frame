package com.webside.cube.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.olap4j.CellSet;
import org.olap4j.CellSetAxis;
import org.olap4j.OlapConnection;
import org.olap4j.OlapException;
import org.olap4j.metadata.Cube;
import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Level;
import org.olap4j.metadata.Member;

public class OlapUtil {
//	zht 获取列表数据
	public static List<Map<String, Object>> cellSetToList(CellSet cellSet){
		List<Map<String, Object>> datas = new ArrayList<>();
		CellSetAxis cols = cellSet.getAxes().get(0);
		CellSetAxis rows = cellSet.getAxes().get(1);
		for(int i = 0;i<rows.getPositionCount();i++){
			Map<String, Object> data = new HashMap<>();
			for(int j=0;j<cols.getPositionCount();j++){
				data.put("L"+j, cellSet.getCell(cols.getPositions().get(j),rows.getPositions().get(i)).getValue());
			}
			datas.add(data);
		}
		return datas;
	}
	
//	zht 获取Schema中全部立方体
	public static List<Map<String, Object>> getCubes(OlapConnection conn) throws OlapException{
		List<Map<String, Object>> datas = new ArrayList<Map<String,Object>>();
		List list = new ArrayList();
		List<Map<String, Object>> dList = new ArrayList<Map<String,Object>>();
		
		for(Cube cube:conn.getOlapSchema().getCubes()){
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("id", cube.getUniqueName());
			data.put("name", cube.getName());
			data.put("caption", cube.getCaption());
			datas.add(data);
			list.add(cube.getCaption());
		}
		
//		排序
		Collections.sort(list);
		
		for (int i = 0; i < list.size(); i++) {
			for (Map<String,Object> map : datas) {
				if (list.get(i).equals(map.get("caption"))) {
					dList.add(map);
					break;
				}
			}
		}
		
		return dList;
	}
	
//	zht 通过立方体id获取立方体
	public static Cube getCube(OlapConnection conn, String cubeId) throws OlapException{
		for(Cube cube:conn.getOlapSchema().getCubes()){
			if(StringUtils.equals(cube.getUniqueName(),cubeId))return cube;
		}
		return null;
	}
	
//	zht 获取立方体的维度和度量
	public static List<Map<String, Object>> getCubeDatas(OlapConnection conn, String cubeId) throws OlapException{
		List<Map<String, Object>> datas = new ArrayList<Map<String,Object>>();
		Cube cube = getCube(conn, cubeId);
		if(cube != null){
			for(Dimension dim:cube.getDimensions()){
				Map<String, Object> dData = new HashMap<String, Object>();
				dData.put("type", 0);
				dData.put("name", dim.getName());
				dData.put("caption", dim.getCaption());
				List<Map<String, Object>> dimChildren = new ArrayList<Map<String,Object>>();
				dData.put("children", dimChildren);
				for(Hierarchy h:dim.getHierarchies()){
					for(Level level:h.getLevels()){
						Map<String, Object> lData = new HashMap<String, Object>();
						lData.put("type", 1);
						lData.put("name", level.getName());
						lData.put("caption", level.getCaption());
						lData.put("dim", dim.getName());
						lData.put("hie", h.getName());
						lData.put("members", level.getUniqueName()+".members");
						lData.put("leaf", true);
						dimChildren.add(lData);
					}
				}
				datas.add(dData);
			}
		}
		return datas;
	}
	
//	zht 获取维度成员
	public static List<Map<String, Object>> getMembers(OlapConnection conn, String cubeName, String dimensionName, String hierarchyName, String levelName) throws OlapException{
		List<Map<String, Object>> datas = new ArrayList<Map<String,Object>>();
		Cube cube = getCube(conn, cubeName);
		if(cube != null){
			for(Dimension dim:cube.getDimensions()){
				if(!StringUtils.equals(dim.getName(), dimensionName))continue;
				for(Hierarchy h:dim.getHierarchies()){
					if(!StringUtils.equals(h.getName(), hierarchyName))continue;
					for(Level l:h.getLevels()){
						if(StringUtils.equals(l.getName(), levelName)){
							for(Member m: l.getMembers()){
								Map<String, Object> obj = new HashMap<>();
								obj.put("name", m.getUniqueName());
								obj.put("caption", m.getCaption());
								datas.add(obj);
							}
							return datas;
						}
					}
				}
			}
		}
		return datas;
	}

}
