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
@Table(name = "afe_ack_msg", schema = "afedb")
public class AfeAckMsgEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "\"id\"")
	private Long id;

	@Column(name = "\"afe_Order_Action_History_Id\"")
	private Long afeOrderActionHistoryId;

	@Column(name = "\"ack_Status\"", length = 8)
	private String ackStatus;

	@Column(name = "\"ack_Msg\"", length = 300)
	private String ackMesage;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"last_Change_Timestamp\"")
	Date lastChangeTimestamp;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"create_Ack_Timestamp\"")
	Date createAckTimestamp;

	@Column(name = "\"envio_Flag_Ah_Ack\"")
	private Boolean envioFlagAhAck;

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

	public AfeAckMsgEntity() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getAfeOrderActionHistoryId() {
		return afeOrderActionHistoryId;
	}

	public void setAfeOrderActionHistoryId(Long afeOrderActionHistoryId) {
		this.afeOrderActionHistoryId = afeOrderActionHistoryId;
	}

	public String getAckStatus() {
		return ackStatus;
	}

	public void setAckStatus(String ackStatus) {
		this.ackStatus = ackStatus;
	}

	public String getAckMesage() {
		return ackMesage;
	}

	public void setAckMesage(String ackMesage) {
		this.ackMesage = ackMesage;
	}

	public Date getLastChangeTimestamp() {
		return lastChangeTimestamp;
	}

	public void setLastChangeTimestamp(Date lastChangeTimestamp) {
		this.lastChangeTimestamp = lastChangeTimestamp;
	}

	public Date getCreateAckTimestamp() {
		return createAckTimestamp;
	}

	public void setCreateAckTimestamp(Date createAckTimestamp) {
		this.createAckTimestamp = createAckTimestamp;
	}

	public Boolean getEnvioFlagAhAck() {
		return envioFlagAhAck;
	}

	public void setEnvioFlagAhAck(Boolean envioFlagAhAck) {
		this.envioFlagAhAck = envioFlagAhAck;
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
