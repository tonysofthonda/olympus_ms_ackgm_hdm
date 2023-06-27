package com.honda.olympus.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MaxTransitResponseVO {

	private String action;
	private String veh_order_nbr;
	private String modelYearNbr;
	private String sellingSrcCd;
	private String optionCode;
	private String originType;
	private String extern_config_identfr;
	private String orderTyoeCd;
	private String mdseModlDesgtr;
	private String chrgBusnsAsctCd;
	private String chrgBusnsFncCd;
	private String shipBusnsAsctCd;
	private String shioBusnsFncCd;
	private String rqst_identfr;
	
	private String reqst_status;
	private List<String> mesagge = new ArrayList<>();
	private Date vo_last_chg_timestamp;
	

	public MaxTransitResponseVO() {
		super();
	}


	public String getAction() {
		return action;
	}


	public void setAction(String action) {
		this.action = action;
	}


	public String getVeh_order_nbr() {
		return veh_order_nbr;
	}


	public void setVeh_order_nbr(String veh_order_nbr) {
		this.veh_order_nbr = veh_order_nbr;
	}


	public String getModelYearNbr() {
		return modelYearNbr;
	}


	public void setModelYearNbr(String modelYearNbr) {
		this.modelYearNbr = modelYearNbr;
	}


	public String getSellingSrcCd() {
		return sellingSrcCd;
	}


	public void setSellingSrcCd(String sellingSrcCd) {
		this.sellingSrcCd = sellingSrcCd;
	}


	public String getOptionCode() {
		return optionCode;
	}


	public void setOptionCode(String optionCode) {
		this.optionCode = optionCode;
	}


	public String getOriginType() {
		return originType;
	}


	public void setOriginType(String originType) {
		this.originType = originType;
	}


	public String getExtern_config_identfr() {
		return extern_config_identfr;
	}


	public void setExtern_config_identfr(String extern_config_identfr) {
		this.extern_config_identfr = extern_config_identfr;
	}


	public String getOrderTyoeCd() {
		return orderTyoeCd;
	}


	public void setOrderTyoeCd(String orderTyoeCd) {
		this.orderTyoeCd = orderTyoeCd;
	}


	public String getMdseModlDesgtr() {
		return mdseModlDesgtr;
	}


	public void setMdseModlDesgtr(String mdseModlDesgtr) {
		this.mdseModlDesgtr = mdseModlDesgtr;
	}


	public String getChrgBusnsAsctCd() {
		return chrgBusnsAsctCd;
	}


	public void setChrgBusnsAsctCd(String chrgBusnsAsctCd) {
		this.chrgBusnsAsctCd = chrgBusnsAsctCd;
	}


	public String getChrgBusnsFncCd() {
		return chrgBusnsFncCd;
	}


	public void setChrgBusnsFncCd(String chrgBusnsFncCd) {
		this.chrgBusnsFncCd = chrgBusnsFncCd;
	}


	public String getShipBusnsAsctCd() {
		return shipBusnsAsctCd;
	}


	public void setShipBusnsAsctCd(String shipBusnsAsctCd) {
		this.shipBusnsAsctCd = shipBusnsAsctCd;
	}


	public String getShioBusnsFncCd() {
		return shioBusnsFncCd;
	}


	public void setShioBusnsFncCd(String shioBusnsFncCd) {
		this.shioBusnsFncCd = shioBusnsFncCd;
	}


	public String getRqst_identfr() {
		return rqst_identfr;
	}


	public void setRqst_identfr(String rqst_identfr) {
		this.rqst_identfr = rqst_identfr;
	}


	public String getReqst_status() {
		//TODO Fix database size
		if(reqst_status.equalsIgnoreCase("CANCELLED"))
			return reqst_status.subSequence(0, 8).toString();	
		
		return reqst_status;
	}


	public void setReqst_status(String reqst_status) {
		this.reqst_status = reqst_status;
	}


	public List<String> getMesagge() {
		return mesagge;
	}


	public void setMesagge(List<String> mesagge) {
		this.mesagge = mesagge;
	}


	public Date getVo_last_chg_timestamp() {
		return vo_last_chg_timestamp;
	}


	public void setVo_last_chg_timestamp(Date vo_last_chg_timestamp) {
		this.vo_last_chg_timestamp = vo_last_chg_timestamp;
	}
	
	
	
	

}
