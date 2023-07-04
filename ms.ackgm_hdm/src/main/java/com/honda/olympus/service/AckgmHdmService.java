package com.honda.olympus.service;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.exception.JDBCConnectionException;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.honda.olympus.dao.AfeAckMsgEntity;
import com.honda.olympus.dao.AfeActionEvEntity;
import com.honda.olympus.dao.AfeFixedOrdersEvEntity;
import com.honda.olympus.dao.AfeOrdersActionHistoryEntity;
import com.honda.olympus.repository.AfeAckMsgEvRepository;
import com.honda.olympus.repository.AfeActionRepository;
import com.honda.olympus.repository.AfeFixedOrdersEvRepository;
import com.honda.olympus.repository.AfeOrdersActionHistoryRepository;
import com.honda.olympus.utils.AckgmConstants;
import com.honda.olympus.utils.AckgmMessagesHandler;
import com.honda.olympus.utils.AckgmUtils;
import com.honda.olympus.vo.EventVO;
import com.honda.olympus.vo.MaxTransitCallVO;
import com.honda.olympus.vo.MaxTransitResponseVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AckgmHdmService {

	@Autowired
	private AckgmMessagesHandler ackgmMessagesHandler;

	@Autowired
	private MaxTransitService maxTransitService;

	@Autowired
	private AfeFixedOrdersEvRepository afeFixedOrdersEvRepository;

	@Autowired
	private AfeOrdersActionHistoryRepository afeOrdersHistoryRepository;

	@Autowired
	private AfeActionRepository afeActionRepository;

	@Autowired
	private AfeAckMsgEvRepository afeAckMsgEvRepository;

	private String ipAddress;

	public void callAckgmCheckHd(String ipAddress) throws JDBCConnectionException {
		Boolean successFlag = Boolean.FALSE;

		this.ipAddress = ipAddress;

		MaxTransitCallVO maxTransitMessage = new MaxTransitCallVO();

		maxTransitMessage.setRequest(AckgmConstants.ACK_ORDER_REQUEST);

		List<MaxTransitResponseVO> maxTransitData = maxTransitService.generateCallMaxtransit(maxTransitMessage);

		if (maxTransitData.isEmpty()) {

			ackgmMessagesHandler.createAndLogMessage(maxTransitData);
		}

		Iterator<MaxTransitResponseVO> it = maxTransitData.iterator();
		while (it.hasNext()) {

			MaxTransitResponseVO maxTransitDetail = it.next();
			String rqstIdentifierMxtrs = maxTransitDetail.getRqst_identfr().trim();
			String actionMxtrs = maxTransitDetail.getAction();

			log.debug("AckgmHdm:: ----action----:: {}", rqstIdentifierMxtrs);

			if (rqstIdentifierMxtrs.length() < 0) {

				ackgmMessagesHandler.createAndLogMessage(rqstIdentifierMxtrs, maxTransitDetail);
				continue;
			}

			// Create Flow
			if (AckgmConstants.CREATE_STATUS.equalsIgnoreCase(actionMxtrs)) {
				log.debug("Start:: Create flow");

				// QUERY1
				List<AfeFixedOrdersEvEntity> fixedOrders = afeFixedOrdersEvRepository
						.findAllByRqstId(rqstIdentifierMxtrs.trim());

				if (fixedOrders.isEmpty()) {

					ackgmMessagesHandler.createAndLogMessageNoRqstIdtfr(rqstIdentifierMxtrs,
							"SELECT * FROM AFE_FIXED_ORDERS_EV WHERE REQST_IDENTFR");
					continue;
				}

				AfeFixedOrdersEvEntity fixedOrder = fixedOrders.get(0);

				if (createFlow(maxTransitDetail, fixedOrder)) {
					successFlag = Boolean.TRUE;
					log.debug("End:: Accepted flow");
				}

			} else {

				if (AckgmConstants.CHANGE_STATUS.equalsIgnoreCase(actionMxtrs)) {

					if (changeFlow(maxTransitDetail)) {
						successFlag = Boolean.TRUE;
						log.debug("End:: Accepted flow");

					}

				}

				if (AckgmConstants.CANCEL_STATUS.equalsIgnoreCase(actionMxtrs)) {
					if (canceledFlow(maxTransitDetail)) {
						successFlag = Boolean.TRUE;
						log.debug("End:: Accepted flow");
					}
				}

				// request_status invalid
				ackgmMessagesHandler.createAndLogMessage(maxTransitDetail);

			}

		}
		if (successFlag) {

			ackgmMessagesHandler.successMessage();
		}

	}

	private Boolean changeFlow(final MaxTransitResponseVO maxTransitDetail) {
		log.debug("Start:: Changed Flow ");

		String strRqstIdtfr = maxTransitDetail.getRqst_identfr();
		String vehOrderNumber = maxTransitDetail.getVeh_order_nbr();
		String actionMxtrsp = maxTransitDetail.getAction();
		String requestIdtfrMxtrsp = maxTransitDetail.getRqst_identfr();
		String ordrNbrMxtrsp = maxTransitDetail.getVeh_order_nbr();
		String rqstStatusMxtrsp = maxTransitDetail.getReqst_status();

		// QUERY7
		List<AfeFixedOrdersEvEntity> fixedOrders = afeFixedOrdersEvRepository
				.findByRequestAndOrderNumber(vehOrderNumber.trim(), strRqstIdtfr.trim());

		if (fixedOrders.isEmpty()) {

			ackgmMessagesHandler.createAfeAckMessageNoFixedOrderByOrdNmbr(strRqstIdtfr, vehOrderNumber,
					"SELECT * FROM AFE_FIXED_ORDERS WHERE ORDER_NMBR AND REQUEST_IDTFR");
			return Boolean.FALSE;
		}

		Long fixedOrderQ8 = fixedOrders.get(0).getId();

		// QUERY8
		List<AfeActionEvEntity> actions = afeActionRepository.findAllByAction(actionMxtrsp);
		if (actions.isEmpty()) {

			ackgmMessagesHandler.createAndLogMessageNoAction(maxTransitDetail, "");
			return Boolean.FALSE;
		}

		Long actionIdQ8 = actions.get(0).getId();
		Long modelColorIdQ8 = fixedOrders.get(0).getModelColorId();
		Boolean envioFlagQ8 = fixedOrders.get(0).getEnvioFlagGm();

		// QUERY9
		AfeOrdersActionHistoryEntity orderHistory = new AfeOrdersActionHistoryEntity();

		try {
			orderHistory.setActionId(actionIdQ8);
			orderHistory.setFixedOrderId(fixedOrderQ8);
			orderHistory.setModelColorId(modelColorIdQ8);
			orderHistory.setEnvioFlagGm(envioFlagQ8);

			orderHistory.setCreationTimeStamp(new Date());
			orderHistory
					.setObs(String.format("Client IP: %s , TimeStamp: %s", this.ipAddress, AckgmUtils.getTimeStamp()));
			orderHistory.setBstate(1);

			// QUERY10
			afeOrdersHistoryRepository.save(orderHistory);

		} catch (Exception e) {

			ackgmMessagesHandler.createAndLogMessageOrderHistoryFail("INSERT * INTO AFE_ORDER_HISOTRY");
			return Boolean.FALSE;

		}

		AfeAckMsgEntity ackMessage = new AfeAckMsgEntity();

		if (!maxTransitDetail.getReqst_status().equalsIgnoreCase(AckgmConstants.ACCEPTED_STATUS)) {

			try {

				ackMessage.setAfeOrderActionHistoryId(orderHistory.getId());
				ackMessage.setAckStatus(maxTransitDetail.getReqst_status());
				ackMessage.setAckMesage(maxTransitDetail.getMesagge().toString());
				ackMessage.setLastChangeTimestamp(maxTransitDetail.getVo_last_chg_timestamp());
				ackMessage.setCreationTimeStamp(new Date());
				ackMessage.setEnvioFlagAhAck(Boolean.FALSE);
				ackMessage.setCreateAckTimestamp(new Date());

				ackMessage.setObs(
						String.format("Client IP: %s , TimeStamp: %s", this.ipAddress, AckgmUtils.getTimeStamp()));
				ackMessage.setBstate(1);

				// QUERY12
				afeAckMsgEvRepository.saveAndFlush(ackMessage);

				ackgmMessagesHandler.createAfeAckMessageAlternInsert(requestIdtfrMxtrsp, ordrNbrMxtrsp,
						rqstStatusMxtrsp, actionMxtrsp);
				log.debug("End:: changeFlow");
				return Boolean.TRUE;
			} catch (Exception e) {
				ackgmMessagesHandler.createAfeAckMessageFail("INSERT * INTO AFE_ACK_MESSAGE",e.getLocalizedMessage());
				return Boolean.FALSE;
			}

		}

		// QUERY11
		try {

			ackMessage.setAfeOrderActionHistoryId(orderHistory.getActionId());
			ackMessage.setAckStatus(maxTransitDetail.getReqst_status());
			ackMessage.setAckMesage(maxTransitDetail.getMesagge().toString());
			ackMessage.setLastChangeTimestamp(maxTransitDetail.getVo_last_chg_timestamp());
			ackMessage.setCreateAckTimestamp(new Date());
			ackMessage.setEnvioFlagAhAck(Boolean.FALSE);
			ackMessage.setCreationTimeStamp(new Date());

			ackMessage
					.setObs(String.format("Client IP: %s , TimeStamp: %s", this.ipAddress, AckgmUtils.getTimeStamp()));
			ackMessage.setBstate(1);

			afeAckMsgEvRepository.saveAndFlush(ackMessage);
		} catch (Exception e) {
			ackgmMessagesHandler.createAfeAckMessageFail("INSERT * INTO AFE_ACK_MESSAGE",e.getLocalizedMessage());
			return Boolean.FALSE;
		}

		ackgmMessagesHandler.createAfeAckMessageInsertSuccess(maxTransitDetail.getRqst_identfr(),
				maxTransitDetail.getVeh_order_nbr());
		log.debug("End:: Changed Flow ");

		return Boolean.TRUE;

	}

	private Boolean canceledFlow(final MaxTransitResponseVO maxTransitDetail) {
		log.debug("Start:: Cancel Flow ");

		String strRqstIdtfr = maxTransitDetail.getRqst_identfr();
		String vehOrderNumber = maxTransitDetail.getVeh_order_nbr();
		String actionMxtrsp = maxTransitDetail.getAction();
		String requestIdtfrMxtrsp = maxTransitDetail.getRqst_identfr();
		String ordrNbrMxtrsp = maxTransitDetail.getVeh_order_nbr();
		String rqstStatusMxtrsp = maxTransitDetail.getReqst_status();

		// QUERY13
		List<AfeFixedOrdersEvEntity> fixedOrders = afeFixedOrdersEvRepository
				.findByRequestAndOrderNumber(vehOrderNumber, strRqstIdtfr);

		if (fixedOrders.isEmpty()) {

			ackgmMessagesHandler.createAfeAckMessageNoFixedOrderByOrdNmbr(strRqstIdtfr, vehOrderNumber,
					"SELECT * FROM AFE_FIXED_ORDERS WHERE ORDER_NMBR AND REQUEST_IDTFR");
			return Boolean.FALSE;
		}

		Long fixedOrderQ8 = fixedOrders.get(0).getId();

		// QUERY14
		List<AfeActionEvEntity> actions = afeActionRepository.findAllByAction(actionMxtrsp);
		if (actions.isEmpty()) {

			ackgmMessagesHandler.createAndLogMessageNoAction(maxTransitDetail, "");
			return Boolean.FALSE;
		}

		Long actionIdQ8 = actions.get(0).getId();
		Long modelColorIdQ8 = fixedOrders.get(0).getModelColorId();
		Boolean envioFlagQ8 = fixedOrders.get(0).getEnvioFlagGm();

		// QUERY15
		AfeOrdersActionHistoryEntity orderHistory = new AfeOrdersActionHistoryEntity();

		try {
			orderHistory.setActionId(actionIdQ8);
			orderHistory.setFixedOrderId(fixedOrderQ8);
			orderHistory.setModelColorId(modelColorIdQ8);
			orderHistory.setEnvioFlagGm(envioFlagQ8);

			orderHistory.setCreationTimeStamp(new Date());
			orderHistory
					.setObs(String.format("Client IP: %s , TimeStamp: %s", this.ipAddress, AckgmUtils.getTimeStamp()));
			orderHistory.setBstate(1);

			// QUERY16
			afeOrdersHistoryRepository.save(orderHistory);

		} catch (Exception e) {

			ackgmMessagesHandler.createAndLogMessageOrderHistoryFail("INSERT * INTO AFE_ORDER_HISOTRY");
			return Boolean.FALSE;

		}

		AfeAckMsgEntity ackMessage = new AfeAckMsgEntity();
		if (!maxTransitDetail.getReqst_status().equalsIgnoreCase(AckgmConstants.ACCEPTED_STATUS)) {

			try {

				ackMessage.setAfeOrderActionHistoryId(orderHistory.getId());
				ackMessage.setAckStatus(maxTransitDetail.getReqst_status());
				ackMessage.setAckMesage(maxTransitDetail.getMesagge().toString());
				ackMessage.setLastChangeTimestamp(maxTransitDetail.getVo_last_chg_timestamp());
				ackMessage.setCreationTimeStamp(new Date());
				ackMessage.setEnvioFlagAhAck(Boolean.FALSE);
				ackMessage.setCreateAckTimestamp(new Date());

				ackMessage.setObs(
						String.format("Client IP: %s , TimeStamp: %s", this.ipAddress, AckgmUtils.getTimeStamp()));
				ackMessage.setBstate(1);

				// QUERY12
				afeAckMsgEvRepository.saveAndFlush(ackMessage);

				ackgmMessagesHandler.createAfeAckMessageAlternInsert(requestIdtfrMxtrsp, ordrNbrMxtrsp,
						rqstStatusMxtrsp, actionMxtrsp);
				log.debug("End:: changeFlow");
				return Boolean.TRUE;
			} catch (Exception e) {
				log.error("Exception ocurred due to {}",e.getLocalizedMessage());
				ackgmMessagesHandler.createAfeAckMessageFail("INSERT * INTO AFE_ACK_MESSAGE",e.getLocalizedMessage());
				return Boolean.FALSE;
			}

		}

		// QUERY11
		try {

			ackMessage.setAfeOrderActionHistoryId(orderHistory.getActionId());
			ackMessage.setAckStatus(maxTransitDetail.getReqst_status());
			ackMessage.setAckMesage(maxTransitDetail.getMesagge().toString());
			ackMessage.setLastChangeTimestamp(maxTransitDetail.getVo_last_chg_timestamp());
			ackMessage.setCreateAckTimestamp(new Date());
			ackMessage.setEnvioFlagAhAck(Boolean.FALSE);
			ackMessage.setCreationTimeStamp(new Date());

			ackMessage
					.setObs(String.format("Client IP: %s , TimeStamp: %s", this.ipAddress, AckgmUtils.getTimeStamp()));
			ackMessage.setBstate(1);

			afeAckMsgEvRepository.saveAndFlush(ackMessage);
		} catch (Exception e) {
			ackgmMessagesHandler.createAfeAckMessageFail("INSERT * INTO AFE_ACK_MESSAGE",e.getLocalizedMessage());
			return Boolean.FALSE;
		}

		ackgmMessagesHandler.createAfeAckMessageInsertSuccess(maxTransitDetail.getRqst_identfr(),
				maxTransitDetail.getVeh_order_nbr());
		log.debug("End:: Canceled Flow ");

		return Boolean.TRUE;

	}

	private Boolean createFlow(MaxTransitResponseVO maxTransitDetail, AfeFixedOrdersEvEntity fixedOrder) {

		log.debug("Start:: finalFlow");
		EventVO event;

		Long fixedOrderIdQ1 = fixedOrder.getId();
		Long modelColorIdQ1 = fixedOrder.getModelColorId();
		String requstIdQ1 = fixedOrder.getRequestId();
		Boolean envioFlagQ1 = fixedOrder.getEnvioFlagGm();
		String actionMxtrsp = maxTransitDetail.getAction();
		String requestIdtfrMxtrsp = maxTransitDetail.getRqst_identfr();
		String ordrNbrMxtrsp = maxTransitDetail.getVeh_order_nbr();
		String rqstStatusMxtrsp = maxTransitDetail.getReqst_status();

		// QUERY2
		List<AfeActionEvEntity> actions = afeActionRepository.findAllByAction(actionMxtrsp);
		if (actions.isEmpty()) {

			ackgmMessagesHandler.createAndLogMessageNoAction(maxTransitDetail, "");
			return Boolean.FALSE;
		}

		Long actionIdQ2 = actions.get(0).getId();

		// QUERY3
		AfeOrdersActionHistoryEntity orderHistory = new AfeOrdersActionHistoryEntity();

		try {
			orderHistory.setActionId(actionIdQ2);
			orderHistory.setFixedOrderId(fixedOrderIdQ1);
			orderHistory.setModelColorId(modelColorIdQ1);
			orderHistory.setEnvioFlagGm(envioFlagQ1);

			orderHistory.setCreationTimeStamp(new Date());
			orderHistory
					.setObs(String.format("Client IP: %s , TimeStamp: %s", this.ipAddress, AckgmUtils.getTimeStamp()));
			orderHistory.setBstate(1);

			afeOrdersHistoryRepository.save(orderHistory);

		} catch (Exception e) {

			ackgmMessagesHandler.createAndLogMessageOrderHistoryFail("INSERT * INTO AFE_ORDER_HISOTRY");
			return Boolean.FALSE;

		}

		// QUERY4
		Long idActionOrderHistoryQ4 = orderHistory.getId();

		AfeAckMsgEntity ackMessage = new AfeAckMsgEntity();

		if (!maxTransitDetail.getReqst_status().equalsIgnoreCase(AckgmConstants.ACCEPTED_STATUS)) {

			try {

				ackMessage.setAfeOrderActionHistoryId(idActionOrderHistoryQ4);
				ackMessage.setAckStatus(maxTransitDetail.getReqst_status());
				ackMessage.setAckMesage(maxTransitDetail.getMesagge().toString());
				ackMessage.setLastChangeTimestamp(maxTransitDetail.getVo_last_chg_timestamp());
				ackMessage.setCreationTimeStamp(new Date());
				ackMessage.setEnvioFlagAhAck(Boolean.TRUE);
				ackMessage.setObs(
						String.format("Client IP: %s , TimeStamp: %s", this.ipAddress, AckgmUtils.getTimeStamp()));
				ackMessage.setBstate(1);

				afeAckMsgEvRepository.saveAndFlush(ackMessage);

				ackgmMessagesHandler.createAfeAckMessageAlternInsert(requestIdtfrMxtrsp, ordrNbrMxtrsp,
						rqstStatusMxtrsp, actionMxtrsp);
				log.debug("End:: finalFlow");
				return Boolean.TRUE;
			} catch (Exception e) {
				ackgmMessagesHandler.createAfeAckMessageFail("INSERT * INTO AFE_ACK_MESSAGE",e.getLocalizedMessage());
				return Boolean.FALSE;
			}

		}

		// QUERY5
		try {

			ackMessage.setAfeOrderActionHistoryId(idActionOrderHistoryQ4);
			ackMessage.setAckStatus(maxTransitDetail.getReqst_status());
			ackMessage.setAckMesage(maxTransitDetail.getMesagge().toString());
			ackMessage.setLastChangeTimestamp(maxTransitDetail.getVo_last_chg_timestamp());
			ackMessage.setCreationTimeStamp(new Date());
			ackMessage.setEnvioFlagAhAck(Boolean.FALSE);
			ackMessage.setCreateAckTimestamp(new Date());
			ackMessage
					.setObs(String.format("Client IP: %s , TimeStamp: %s", this.ipAddress, AckgmUtils.getTimeStamp()));
			ackMessage.setBstate(1);

			afeAckMsgEvRepository.saveAndFlush(ackMessage);
		} catch (Exception e) {
			ackgmMessagesHandler.createAfeAckMessageFail("INSERT * INTO AFE_ACK_MESSAGE",e.getLocalizedMessage());
			return Boolean.FALSE;
		}

		try {

			AfeFixedOrdersEvEntity updateFixedOrder = afeFixedOrdersEvRepository.findAllById(fixedOrderIdQ1);

			updateFixedOrder.setOrderNumber(maxTransitDetail.getVeh_order_nbr());
			updateFixedOrder.setUpdateTimeStamp(new Date());

			afeFixedOrdersEvRepository.saveAndFlush(updateFixedOrder);

		} catch (Exception e) {
			
			log.error("Error updating AFE_FIXED_ORDERS due to {} ",e.getLocalizedMessage());
			
			ackgmMessagesHandler.createAndLogMessageQueryFailed("UPDATE * INTO AFE_FIXED_ORDERS",e.getLocalizedMessage());
			return Boolean.FALSE;
		}

		ackgmMessagesHandler.createAfeAckMessageInsertSuccess(maxTransitDetail.getRqst_identfr(),
				maxTransitDetail.getVeh_order_nbr());
		log.debug("End:: finalFlow");
		return Boolean.TRUE;

	}

}
