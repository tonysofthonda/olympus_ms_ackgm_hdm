package com.honda.olympus.dao;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "afe_fixed_orders_ev", schema = "afedb")
public class AfeFixedOrdersEvEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "\"id\"")
	private Long id;

	@Column(name = "\"envio_Flag_Gm\"")
	private Boolean envioFlagGm;
	
	@Column(name = "\"action_Id\"")
	private Long actionId;

	@Column(name = "\"model_Color_Id\"")
	private Long modelColorId;
	
	@Column(name = "\"order_Number\"",length = 6)
	private String orderNumber;

	@Column(name = "\"selling_Code\"",length = 2)
	private String sellingCode;

	@Column(name = "\"origin_Type\"",length = 8)
	private String originType;

	@Column(name = "\"extern_Config_Id\"",length = 32)
	private String externConfigId;

	@Column(name = "\"order_Type\"",length = 3)
	private String orderType;

	@Column(name = "\"chrg_Asct\"")
	private Long chrgAsct;

	@Column(name = "\"chrg_Fcn\"")
	private Long chrgFcn;

	@Column(name = "\"ship_Sct\"")
	private Long shipSct;

	@Column(name = "\"ship_Fcn\"")
	private Long shipFcn;

	@Column(name = "\"request_Id\"",length = 40)
	private String requestId;

	@Column(name = "\"vin_Number\"",length = 17)
	private String vinNumber;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"prod_Week_Start_Day\"")
	Date prodWeekStartDay;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"ord_Due_Dt\"")
	Date ordDueDt;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"create_Ord_Timestamp\"")
	Date createOrdTimestamp;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"cancel_Ord_Timestamp\"")
	Date cancelOrdTimestamp;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"change_Ord_Timestamp\"")
	Date changeOrdTimestamp;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"create_Timestamp\"")
	Date creationTimeStamp;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"update_timestamp\"")
	Date updateTimeStamp;
	
	@Column(name = "\"obs\"")
	private String obs;

	@Column(name = "\"bstate\"")
	private Integer bstate;

	public AfeFixedOrdersEvEntity() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getEnvioFlagGm() {
		return envioFlagGm;
	}

	public void setEnvioFlagGm(Boolean envioFlagGm) {
		this.envioFlagGm = envioFlagGm;
	}

	public Long getActionId() {
		return actionId;
	}

	public void setActionId(Long actionId) {
		this.actionId = actionId;
	}

	public Long getModelColorId() {
		return modelColorId;
	}

	public void setModelColorId(Long modelColorId) {
		this.modelColorId = modelColorId;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getSellingCode() {
		return sellingCode;
	}

	public void setSellingCode(String sellingCode) {
		this.sellingCode = sellingCode;
	}

	public String getOriginType() {
		return originType;
	}

	public void setOriginType(String originType) {
		this.originType = originType;
	}

	public String getExternConfigId() {
		return externConfigId;
	}

	public void setExternConfigId(String externConfigId) {
		this.externConfigId = externConfigId;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public Long getChrgAsct() {
		return chrgAsct;
	}

	public void setChrgAsct(Long chrgAsct) {
		this.chrgAsct = chrgAsct;
	}

	public Long getChrgFcn() {
		return chrgFcn;
	}

	public void setChrgFcn(Long chrgFcn) {
		this.chrgFcn = chrgFcn;
	}

	public Long getShipSct() {
		return shipSct;
	}

	public void setShipSct(Long shipSct) {
		this.shipSct = shipSct;
	}

	public Long getShipFcn() {
		return shipFcn;
	}

	public void setShipFcn(Long shipFcn) {
		this.shipFcn = shipFcn;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getVinNumber() {
		return vinNumber;
	}

	public void setVinNumber(String vinNumber) {
		this.vinNumber = vinNumber;
	}

	public Date getProdWeekStartDay() {
		return prodWeekStartDay;
	}

	public void setProdWeekStartDay(Date prodWeekStartDay) {
		this.prodWeekStartDay = prodWeekStartDay;
	}

	public Date getOrdDueDt() {
		return ordDueDt;
	}

	public void setOrdDueDt(Date ordDueDt) {
		this.ordDueDt = ordDueDt;
	}

	public Date getCreateOrdTimestamp() {
		return createOrdTimestamp;
	}

	public void setCreateOrdTimestamp(Date createOrdTimestamp) {
		this.createOrdTimestamp = createOrdTimestamp;
	}

	public Date getCancelOrdTimestamp() {
		return cancelOrdTimestamp;
	}

	public void setCancelOrdTimestamp(Date cancelOrdTimestamp) {
		this.cancelOrdTimestamp = cancelOrdTimestamp;
	}

	public Date getChangeOrdTimestamp() {
		return changeOrdTimestamp;
	}

	public void setChangeOrdTimestamp(Date changeOrdTimestamp) {
		this.changeOrdTimestamp = changeOrdTimestamp;
	}

	public Date getCreationTimeStamp() {
		return creationTimeStamp;
	}

	public void setCreationTimeStamp(Date creationTimeStamp) {
		this.creationTimeStamp = creationTimeStamp;
	}

	public Date getUpdateTimeStamp() {
		return updateTimeStamp;
	}

	public void setUpdateTimeStamp(Date updateTimeStamp) {
		this.updateTimeStamp = updateTimeStamp;
	}

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	public Integer getBstate() {
		return bstate;
	}

	public void setBstate(Integer bstate) {
		this.bstate = bstate;
	}
	
}
