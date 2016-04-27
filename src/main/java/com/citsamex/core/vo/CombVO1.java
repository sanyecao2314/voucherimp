package com.citsamex.core.vo;

/**
 * COMBOVALUE1 = "公募基金费用表";
	protected final static String COMBOVALUE1 = "公募基金费用表";
	protected final static String COMBOVALUE2 = "公司手续费收入划拨明细表";
	protected final static String COMBOVALUE3 = "尾随佣金汇总表";
	protected final static String COMBOVALUE4 = "尾随佣金汇总表_比例";
	protected final static String COMBOVALUE5 = "直销手续费明细";
	protected final static String COMBOVALUE6 = "专户基金费用表";
 * @author fans.fan
 *
 */
public class CombVO1 {

	private String startdate;

	private String enddate;
	
	private String code;
	
	private String name;
	
	private String glfhz;

	private String tgf;
	
	private String xsfy;

	private String bzj;

	private String jaccsubjname;
	
	private String daccsubjname;

	public String getStartdate() {
		return startdate;
	}

	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}

	public String getEnddate() {
		return enddate;
	}

	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getJaccsubjname() {
		return jaccsubjname;
	}

	public void setJaccsubjname(String jaccsubjname) {
		this.jaccsubjname = jaccsubjname;
	}

	public String getDaccsubjname() {
		return daccsubjname;
	}

	public void setDaccsubjname(String daccsubjname) {
		this.daccsubjname = daccsubjname;
	}

	public String getGlfhz() {
		return glfhz;
	}

	public void setGlfhz(String glfhz) {
		this.glfhz = glfhz;
	}

	public String getTgf() {
		return tgf;
	}

	public void setTgf(String tgf) {
		this.tgf = tgf;
	}

	public String getXsfy() {
		return xsfy;
	}

	public void setXsfy(String xsfy) {
		this.xsfy = xsfy;
	}

	public String getBzj() {
		return bzj;
	}

	public void setBzj(String bzj) {
		this.bzj = bzj;
	}
	
}
