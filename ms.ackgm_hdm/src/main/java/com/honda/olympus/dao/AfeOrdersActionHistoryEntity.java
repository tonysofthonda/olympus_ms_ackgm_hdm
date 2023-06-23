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
@Table(name = "afe_order_action_history", schema = "afedb")
public class AfeOrdersActionHistoryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "\"id\"")
	private Long id;
	
	@Column(name = "\"action_Id\"")
	private Long actionId;
	
	@Column(name = "\"fixed_Order_Id\"")
	private Long fixedOrderId;
	
	@Column(name = "\"model_Color_Id\"")
	private Long modelColorId;
	
	@Column(name = "\"envio_Flag_Gm\"")
	private Boolean envioFlagGm;
	
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

	public AfeOrdersActionHistoryEntity() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getActionId() {
		return actionId;
	}

	public void setActionId(Long actionId) {
		this.actionId = actionId;
	}

	public Long getFixedOrderId() {
		return fixedOrderId;
	}

	public void setFixedOrderId(Long fixedOrderId) {
		this.fixedOrderId = fixedOrderId;
	}

	public Long getModelColorId() {
		return modelColorId;
	}

	public void setModelColorId(Long modelColorId) {
		this.modelColorId = modelColorId;
	}

	public Boolean getEnvioFlagGm() {
		return envioFlagGm;
	}

	public void setEnvioFlagGm(Boolean envioFlagGm) {
		this.envioFlagGm = envioFlagGm;
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
