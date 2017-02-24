package com.webside.excel.controller;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.webside.base.basecontroller.BaseController;
import com.webside.common.Common;
import com.webside.excel.model.DbEntity;
import com.webside.excel.service.ExcelService;
import com.webside.user.model.UserEntity;

import cc.aicode.e2e.ExcelHelper;

@Controller // @Controller 负责注册一个bean 到spring 上下文中，bean 的ID 默认为类名称开头字母小写
@Scope("prototype") // @Scope定义一个Bean 的作用范围,prototype:定义bean可以被多次实例化（使用一次就创建一次）
@RequestMapping("/excel/") // @RequestMapping 映射路径，可以声明到类或方法上
public class ExcelController extends BaseController {

	@Autowired // @Autowired 根据bean 类型从spring 上线文中进行查找，注册类型必须唯一，否则报异常。
	private ExcelService excelService;

	@RequestMapping("excelHome.html")
	public String excelHome(HttpServletRequest req,Model model) {

		int num=0 ;
		if (req.getParameter("num") != null && !req.getParameter("num").equals(""))
			num = Integer.valueOf((String) req.getParameter("num"));

		int count=0 ;
		if (req.getParameter("count") != null && !req.getParameter("count").equals(""))
			count = Integer.valueOf((String) req.getParameter("count"));
		
		// 获取第n页，m条内容
		if (num==0 && count==0) {
			PageHelper.startPage(1, 20);
		}else{
			PageHelper.startPage(num, count);
		}
		
		// 紧跟着的第一个查询方法会被分页
		List<DbEntity> list = excelService.queryAllDb();
		model.addAttribute("list", list);

		// 用PageInfo对结果进行包装
		PageInfo<DbEntity> page = new PageInfo<DbEntity>(list);
		model.addAttribute("page", page);

		return Common.BACKGROUND_PATH + "/excel/excelOperation";
	}

	/**
	 * 文件上传
	 * 
	 * @return
	 */
	@RequestMapping(value = "fileUpLoad.html", method = RequestMethod.POST)
	@ResponseBody
	public Object fileUpLoad(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		MultipartFile myfile = multipartRequest.getFile("myfile");
		if (myfile.isEmpty()) {
			map.put("result", "error");
			map.put("msg", "上传文件不能为空");
		} else {
			String originalFilename = myfile.getOriginalFilename();
			String fileBaseName = FilenameUtils.getBaseName(originalFilename);
			try {
				String geneFilePath = request.getSession().getServletContext().getRealPath("/upload/excelTmp/");
				System.out.println("上传路径：" + geneFilePath);
				// 把上传的文件放到服务器的文件夹下
				FileUtils.copyInputStreamToFile(myfile.getInputStream(), new File(geneFilePath,
						"excelTmp" + originalFilename.substring(originalFilename.lastIndexOf("."))));
				// coding 文件上传后的处理程序
				map.put("result", "success");
			} catch (Exception e) {
				map.put("result", "error");
				map.put("msg", e.getMessage());
			}
		}
		return map;
	}

	@RequestMapping(value = "readExcel2DB.html", method = RequestMethod.POST)
	@ResponseBody
	public Object readExcel2DB(HttpServletRequest req) {
		Map<String, Object> map = new HashMap<String, Object>();
		String geneFilePath = req.getSession().getServletContext().getRealPath("/upload/excelTmp/excelTmp.xls");

		System.out.println(geneFilePath);

		List<DbEntity> entitys = null;
		try {
			ExcelHelper eh = ExcelHelper.readExcel(geneFilePath);

			entitys = eh.toEntitys(DbEntity.class);
			excelService.deleteAll();

			for (DbEntity d : entitys) {

				// 通过session回话获取当前登录用户的信息
				UserEntity userEntity = (UserEntity) req.getSession().getAttribute("defUserEntity");
				d.setCjrq(new Date());
				d.setCjrid(userEntity.getId().toString());

				excelService.insert(d);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		map.put("result", "success");
		return map;
	}

	@RequestMapping("writeExcel4DB.html")
	public Object writeExcel4DB(HttpServletRequest req, HttpServletResponse response, Model model)
			throws UnsupportedEncodingException {
		Map<String, Object> map = new HashMap<String, Object>();

		List<DbEntity> listData = excelService.queryAllDb();
		model.addAttribute("list", listData);
		String[] excelHeader = { "工作单位", "姓名", "身份证" };
		String fileName = "乘务员驾证信息报表";
		String sheetName = "乘务员驾证信息";

		response.setContentType("application/vnd.ms-excel");
		fileName = new String(fileName.getBytes(), "ISO-8859-1");
		response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xls");
		OutputStream ouputStream;
		try {
			ouputStream = response.getOutputStream();
			HSSFWorkbook wb = export(listData, excelHeader, sheetName);
			wb.write(ouputStream);
			ouputStream.flush();
			ouputStream.close();
			map.put("result", "success");
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public HSSFWorkbook export(List<DbEntity> list, String[] excelHeader, String sheetName)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(sheetName);
		HSSFRow row = sheet.createRow((int) 0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		for (int i = 0; i < excelHeader.length; i++) {
			HSSFCell cell = row.createCell(i);
			cell.setCellValue(excelHeader[i]);
			cell.setCellStyle(style);
			sheet.autoSizeColumn(i);
			sheet.setColumnWidth(i, excelHeader[i].getBytes().length * 2 * 256);
		}

		for (int i = 0; i < list.size(); i++) {
			row = sheet.createRow(i + 1);
			DbEntity entity = list.get(i);
			row.createCell(0).setCellValue(entity.getRyjzxxb_gzdw());
			row.createCell(1).setCellValue(entity.getRyjzxxb_xm());
			row.createCell(2).setCellValue(entity.getRyjzxxb_sfzh());
		}
		return wb;
	}

}
