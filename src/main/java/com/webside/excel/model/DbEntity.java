package com.webside.excel.model;

import java.util.Date;

import org.apache.ibatis.type.Alias;

import com.webside.base.basemodel.BaseEntity;

import cc.aicode.e2e.annotation.ExcelEntity;
import cc.aicode.e2e.annotation.ExcelProperty;

/**
 * 
 * @ClassName: ExcelEntity
 * @Description: 用户账户信息
 * @date 2016年7月12日 下午2:39:12
 *
 */
@Alias("dbEntity") // mybatis通过@Alias注解一个实体JavaBean，需要属性实现set和get方法
@ExcelEntity
public class DbEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	// @ExcelProperty("主键")
	private String ryjzxxb_id;
	@ExcelProperty("序号")
	private String ryjzxxb_xh;
	@ExcelProperty("工作单位")
	private String ryjzxxb_gzdw;
	@ExcelProperty("车间")
	private String ryjzxxb_cj;
	@ExcelProperty("姓  名")
	private String ryjzxxb_xm;
	@ExcelProperty("学历")
	private String ryjzxxb_xl;
	@ExcelProperty("现岗位")
	private String ryjzxxb_xgw;
	@ExcelProperty("驾驶证类别")
	private String ryjzxxb_jszlb;
	@ExcelProperty("身份证号")
	private String ryjzxxb_sfzh;
	@ExcelProperty("驾驶证编号")
	private String ryjzxxb_jszbh;
	// @ExcelProperty("创建人ID")
	private String cjrid;
	// @ExcelProperty("修改人ID")
	private String xgrid;
	@ExcelProperty("备注")
	private String ryjzxxb_bz;

	@ExcelProperty("晋升机车机车司机时间")
	private String ryjzxxb_jsjcsjrq;
	@ExcelProperty("发证日期")
	private String ryjzxxb_fzrq;
	@ExcelProperty("到期换发新证日期")
	private String ryjzxxb_dqhfxzrq;
	// @ExcelProperty("创建日期")
	private Date cjrq;
	// @ExcelProperty("修改日期")
	private Date xgrq;

	public DbEntity() {

	}

	public DbEntity(DbEntity dbEntity) {
		this.ryjzxxb_id = dbEntity.getRyjzxxb_id();
		this.ryjzxxb_xh = dbEntity.getRyjzxxb_xh();
		this.ryjzxxb_gzdw = dbEntity.getRyjzxxb_gzdw();
		this.ryjzxxb_cj = dbEntity.getRyjzxxb_cj();
		this.ryjzxxb_xm = dbEntity.getRyjzxxb_xm();
		this.ryjzxxb_xl = dbEntity.getRyjzxxb_xl();
		this.ryjzxxb_xgw = dbEntity.getRyjzxxb_xgw();
		this.ryjzxxb_jszlb = dbEntity.getRyjzxxb_jszlb();
		this.ryjzxxb_sfzh = dbEntity.getRyjzxxb_sfzh();
		this.ryjzxxb_jszbh = dbEntity.getRyjzxxb_jszbh();
		this.cjrid = dbEntity.getCjrid();
		this.xgrid = dbEntity.getXgrid();
		this.ryjzxxb_bz = dbEntity.getRyjzxxb_bz();
		this.ryjzxxb_jsjcsjrq = dbEntity.getRyjzxxb_jsjcsjrq();
		this.ryjzxxb_fzrq = dbEntity.getRyjzxxb_fzrq();
		this.ryjzxxb_dqhfxzrq = dbEntity.getRyjzxxb_dqhfxzrq();
		this.cjrq = dbEntity.getCjrq();
		this.xgrq = dbEntity.getXgrq();
	}

	public String getRyjzxxb_id() {
		return ryjzxxb_id;
	}

	public void setRyjzxxb_id(String ryjzxxb_id) {
		this.ryjzxxb_id = ryjzxxb_id;
	}

	public String getRyjzxxb_xh() {
		return ryjzxxb_xh;
	}

	public void setRyjzxxb_xh(String ryjzxxb_xh) {
		this.ryjzxxb_xh = ryjzxxb_xh;
	}

	public String getRyjzxxb_gzdw() {
		return ryjzxxb_gzdw;
	}

	public void setRyjzxxb_gzdw(String ryjzxxb_gzdw) {
		this.ryjzxxb_gzdw = ryjzxxb_gzdw;
	}

	public String getRyjzxxb_cj() {
		return ryjzxxb_cj;
	}

	public void setRyjzxxb_cj(String ryjzxxb_cj) {
		this.ryjzxxb_cj = ryjzxxb_cj;
	}

	public String getRyjzxxb_xm() {
		return ryjzxxb_xm;
	}

	public void setRyjzxxb_xm(String ryjzxxb_xm) {
		this.ryjzxxb_xm = ryjzxxb_xm;
	}

	public String getRyjzxxb_xl() {
		return ryjzxxb_xl;
	}

	public void setRyjzxxb_xl(String ryjzxxb_xl) {
		this.ryjzxxb_xl = ryjzxxb_xl;
	}

	public String getRyjzxxb_xgw() {
		return ryjzxxb_xgw;
	}

	public void setRyjzxxb_xgw(String ryjzxxb_xgw) {
		this.ryjzxxb_xgw = ryjzxxb_xgw;
	}

	public String getRyjzxxb_jszlb() {
		return ryjzxxb_jszlb;
	}

	public void setRyjzxxb_jszlb(String ryjzxxb_jszlb) {
		this.ryjzxxb_jszlb = ryjzxxb_jszlb;
	}

	public String getRyjzxxb_sfzh() {
		return ryjzxxb_sfzh;
	}

	public void setRyjzxxb_sfzh(String ryjzxxb_sfzh) {
		this.ryjzxxb_sfzh = ryjzxxb_sfzh;
	}

	public String getRyjzxxb_jszbh() {
		return ryjzxxb_jszbh;
	}

	public void setRyjzxxb_jszbh(String ryjzxxb_jszbh) {
		this.ryjzxxb_jszbh = ryjzxxb_jszbh;
	}

	public String getCjrid() {
		return cjrid;
	}

	public void setCjrid(String cjrid) {
		this.cjrid = cjrid;
	}

	public String getXgrid() {
		return xgrid;
	}

	public void setXgrid(String xgrid) {
		this.xgrid = xgrid;
	}

	public String getRyjzxxb_bz() {
		return ryjzxxb_bz;
	}

	public void setRyjzxxb_bz(String ryjzxxb_bz) {
		this.ryjzxxb_bz = ryjzxxb_bz;
	}

	public String getRyjzxxb_jsjcsjrq() {
		return ryjzxxb_jsjcsjrq;
	}

	public void setRyjzxxb_jsjcsjrq(String ryjzxxb_jsjcsjrq) {
		this.ryjzxxb_jsjcsjrq = ryjzxxb_jsjcsjrq;
	}

	public String getRyjzxxb_fzrq() {
		return ryjzxxb_fzrq;
	}

	public void setRyjzxxb_fzrq(String ryjzxxb_fzrq) {
		this.ryjzxxb_fzrq = ryjzxxb_fzrq;
	}

	public String getRyjzxxb_dqhfxzrq() {
		return ryjzxxb_dqhfxzrq;
	}

	public void setRyjzxxb_dqhfxzrq(String ryjzxxb_dqhfxzrq) {
		this.ryjzxxb_dqhfxzrq = ryjzxxb_dqhfxzrq;
	}

	public Date getCjrq() {
		return cjrq;
	}

	public void setCjrq(Date cjrq) {
		this.cjrq = cjrq;
	}

	public Date getXgrq() {
		return xgrq;
	}

	public void setXgrq(Date xgrq) {
		this.xgrq = xgrq;
	}
	// ==========================================================

	public String getRyjzxxbId() {
		return ryjzxxb_id;
	}

	public void setRyjzxxbId(String ryjzxxb_id) {
		this.ryjzxxb_id = ryjzxxb_id;
	}

	public String getRyjzxxbXh() {
		return ryjzxxb_xh;
	}

	public void setRyjzxxbXh(String ryjzxxb_xh) {
		this.ryjzxxb_xh = ryjzxxb_xh;
	}

	public String getRyjzxxbGzdw() {
		return ryjzxxb_gzdw;
	}

	public void setRyjzxxbGzdw(String ryjzxxb_gzdw) {
		this.ryjzxxb_gzdw = ryjzxxb_gzdw;
	}

	public String getRyjzxxbCj() {
		return ryjzxxb_cj;
	}

	public void setRyjzxxbCj(String ryjzxxb_cj) {
		this.ryjzxxb_cj = ryjzxxb_cj;
	}

	public String getRyjzxxbXm() {
		return ryjzxxb_xm;
	}

	public void setRyjzxxbXm(String ryjzxxb_xm) {
		this.ryjzxxb_xm = ryjzxxb_xm;
	}

	public String getRyjzxxbXl() {
		return ryjzxxb_xl;
	}

	public void setRyjzxxbXl(String ryjzxxb_xl) {
		this.ryjzxxb_xl = ryjzxxb_xl;
	}

	public String getRyjzxxbXgw() {
		return ryjzxxb_xgw;
	}

	public void setRyjzxxbXgw(String ryjzxxb_xgw) {
		this.ryjzxxb_xgw = ryjzxxb_xgw;
	}

	public String getRyjzxxbJszlb() {
		return ryjzxxb_jszlb;
	}

	public void setRyjzxxbJszlb(String ryjzxxb_jszlb) {
		this.ryjzxxb_jszlb = ryjzxxb_jszlb;
	}

	public String getRyjzxxbSfzh() {
		return ryjzxxb_sfzh;
	}

	public void setRyjzxxbSfzh(String ryjzxxb_sfzh) {
		this.ryjzxxb_sfzh = ryjzxxb_sfzh;
	}

	public String getRyjzxxbJszbh() {
		return ryjzxxb_jszbh;
	}

	public void setRyjzxxbJszbh(String ryjzxxb_jszbh) {
		this.ryjzxxb_jszbh = ryjzxxb_jszbh;
	}

	public String getRyjzxxbBz() {
		return ryjzxxb_bz;
	}

	public void setRyjzxxbBz(String ryjzxxb_bz) {
		this.ryjzxxb_bz = ryjzxxb_bz;
	}

	public String getRyjzxxbJsjcsjrq() {
		return ryjzxxb_jsjcsjrq;
	}

	public void setRyjzxxbJsjcsjrq(String ryjzxxb_jsjcsjrq) {
		this.ryjzxxb_jsjcsjrq = ryjzxxb_jsjcsjrq;
	}

	public String getRyjzxxbFzrq() {
		return ryjzxxb_fzrq;
	}

	public void setRyjzxxbFzrq(String ryjzxxb_fzrq) {
		this.ryjzxxb_fzrq = ryjzxxb_fzrq;
	}

	public String getRyjzxxbDhfxzrq() {
		return ryjzxxb_dqhfxzrq;
	}

	public void setRyjzxxbDqhfxzrq(String ryjzxxb_dqhfxzrq) {
		this.ryjzxxb_dqhfxzrq = ryjzxxb_dqhfxzrq;
	}

}
