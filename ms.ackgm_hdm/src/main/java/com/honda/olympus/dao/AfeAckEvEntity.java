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
@Table(name = "afe_ack_ev", schema = "afedb")
public class AfeAckEvEntity {
	

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		@Column(name = "id")
		private Long id;

		@Column(name = "fixed_order_id")		
		private Long fixedOrderId;

		@Column(name = "ack_status")		
		private String ackStatus;

		@Column(name = "ack_msg")
		private String ackMsg;
		
		@Temporal(TemporalType.TIMESTAMP)
		@Column(name = "ack_request_timestamp")
		Date ackRequestTimestamp;

		@Temporal(TemporalType.TIMESTAMP)
		@Column(name = "lastChangeTimestamp")
		Date updateTimeStamp;
		
		
		

		public AfeAckEvEntity() {
			super();
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Long getFixedOrderId() {
			return fixedOrderId;
		}

		public void setFixedOrderId(Long fixedOrderId) {
			this.fixedOrderId = fixedOrderId;
		}

		public String getAckStatus() {
			return ackStatus;
		}

		public void setAckStatus(String ackStatus) {
			this.ackStatus = ackStatus;
		}

		public String getAckMsg() {
			return ackMsg;
		}

		public void setAckMsg(String ackMsg) {
			this.ackMsg = ackMsg;
		}

		public Date getAckRequestTimestamp() {
			return ackRequestTimestamp;
		}

		public void setAckRequestTimestamp(Date ackRequestTimestamp) {
			this.ackRequestTimestamp = ackRequestTimestamp;
		}

		public Date getUpdateTimeStamp() {
			return updateTimeStamp;
		}

		public void setUpdateTimeStamp(Date updateTimeStamp) {
			this.updateTimeStamp = updateTimeStamp;
		}
		
		

}
