package com.honda.olympus.service;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.exception.JDBCConnectionException;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.honda.olympus.dao.AfeAckEvEntity;
import com.honda.olympus.dao.AfeActionEntity;
import com.honda.olympus.dao.AfeActionEvEntity;
import com.honda.olympus.dao.AfeFixedOrdersEvEntity;
import com.honda.olympus.dao.AfeOrdersActionHistoryEntity;
import com.honda.olympus.dao.AfeOrdersHistoryEntity;
import com.honda.olympus.repository.AfeAckEvRepository;
import com.honda.olympus.repository.AfeActionRepository;
import com.honda.olympus.repository.AfeFixedOrdersEvRepository;
import com.honda.olympus.repository.AfeOrdersActionHistoryRepository;
import com.honda.olympus.utils.AckgmConstants;
import com.honda.olympus.utils.AckgmMessagesHandler;
import com.honda.olympus.utils.AckgmUtils;
import com.honda.olympus.utils.ProcessFileUtils;
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
	private AfeAckEvRepository afeAckEvRepository;

	@Autowired
	private AfeActionRepository afeActionRepository;
	
	
	private String ipAddress;

	public void callAckgmCheckHd(String ipAddress) throws JDBCConnectionException {
		EventVO event;

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
			String rqstIdentifierMxtrs = maxTransitDetail.getRqstIdentfr();
			String actionMxtrs = maxTransitDetail.getAction();

			log.debug("----action----:: " + rqstIdentifierMxtrs);

			if (rqstIdentifierMxtrs.length() < 0) {

				ackgmMessagesHandler.createAndLogMessage(rqstIdentifierMxtrs, maxTransitDetail);
				break;
			}

			if (AckgmConstants.CREATE_STATUS.equalsIgnoreCase(actionMxtrs)) {
				log.debug("Start:: Create flow");

				// QUERY1
				List<AfeFixedOrdersEvEntity> fixedOrders = afeFixedOrdersEvRepository
						.findAllByRqstId(rqstIdentifierMxtrs);

				if (fixedOrders.isEmpty()) {

					ackgmMessagesHandler.createAndLogMessage(rqstIdentifierMxtrs);
					break;
				}

				AfeFixedOrdersEvEntity fixedOrder = fixedOrders.get(0);

				

				if (finalFlow(maxTransitDetail, fixedOrder)) {
					successFlag = Boolean.TRUE;
					log.debug("End:: Accepted flow");
				}

			} else {

				if (AckgmConstants.FAILED_STATUS.equalsIgnoreCase(maxTransitDetail.getReqstStatus())) {

					if (failedFlow(fixedOrder.getId(), maxTransitDetail)) {

						if (finalFlow(maxTransitDetail, fixedOrder)) {
							successFlag = Boolean.TRUE;

						}
					}

				}

				if (AckgmConstants.CANCELED_STATUS.equalsIgnoreCase(maxTransitDetail.getReqstStatus())) {
					if (canceledFlow(fixedOrder.getId(), maxTransitDetail)) {
						if (finalFlow(maxTransitDetail, fixedOrder)) {
							successFlag = Boolean.TRUE;

						}
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

	private Boolean failedFlow(final Long fixedOrderId, MaxTransitResponseVO maxTransitDetail) {
		EventVO event;
		log.debug("Start:: Failed Flow ");

		// QUERY6
		List<AfeAckEvEntity> acks = afeAckEvRepository.findAllByFixedOrderId(fixedOrderId);

		if (acks.isEmpty()) {

			ackgmMessagesHandler.createAndLogMessageFixedOrderAck(fixedOrderId);
			return Boolean.FALSE;
		}

		try {
			// QUERY7
			acks.get(0).setAckStatus(maxTransitDetail.getReqstStatus());

			JSONArray jsArray = new JSONArray(maxTransitDetail.getMesagge());

			acks.get(0).setAckMsg(jsArray.toString());
			acks.get(0).setUpdateTimeStamp(new Date());
			afeAckEvRepository.saveAndFlush(acks.get(0));
		} catch (Exception e) {

			ackgmMessagesHandler.createAndLogMessageAckUpdateFail("UPDATE * AFE_ACK_EV ");
			return Boolean.FALSE;
		}

		log.debug("End:: Failed Flow ");

		return Boolean.TRUE;

	}

	private Boolean canceledFlow(final Long fixedOrderId, MaxTransitResponseVO maxTransitDetail) {
		log.debug("Start:: Failed Flow ");
		EventVO event;

		List<AfeAckEvEntity> acks = afeAckEvRepository.findAllByFixedOrderId(fixedOrderId);

		if (acks.isEmpty()) {

			ackgmMessagesHandler.createAndLogMessageFixedOrderAck(fixedOrderId);
			return Boolean.FALSE;
		}

		if (!acks.get(0).getAckStatus().equalsIgnoreCase(AckgmConstants.FAILED_STATUS)) {

			try {
				// QUERY7
				acks.get(0).setAckStatus(maxTransitDetail.getReqstStatus());

				JSONArray jsArray = new JSONArray(maxTransitDetail.getMesagge());

				acks.get(0).setAckMsg(jsArray.toString());
				acks.get(0).setUpdateTimeStamp(new Date());
				afeAckEvRepository.saveAndFlush(acks.get(0));
			} catch (Exception e) {

				ackgmMessagesHandler.createAndLogMessageAckUpdateFail("UPDATE * AFE_ACK_EV ");

				return Boolean.FALSE;
			}

		} else {

			ackgmMessagesHandler.createAndLogMessageNoCancelOrder(fixedOrderId);
			return Boolean.FALSE;

		}

		log.debug("End:: Failed Flow ");

		return Boolean.TRUE;

	}

	private Boolean finalFlow(MaxTransitResponseVO maxTransitDetail, AfeFixedOrdersEvEntity fixedOrder) {

		log.debug("Start:: finalFlow");
		EventVO event;
		Long fixedOrderIdQ1 = fixedOrder.getId();
		Long modelColorIdQ1 = fixedOrder.getModelColorId();
		String requstIdQ1 = fixedOrder.getRequestId();
		Boolean envioFlagQ1 = fixedOrder.getEnvioFlagGm();
		
		String actionMxtrsp = maxTransitDetail.getAction();

		// QUERY2
		List<AfeActionEvEntity> actions = afeActionRepository.findAllByAction(actionMxtrsp);
		if (actions.isEmpty()) {

			ackgmMessagesHandler.createAndLogMessageNoAction(maxTransitDetail, "");
			return Boolean.FALSE;
		}
		
		Long actionIdq2 = actions.get(0).getId();

		// QUERY3
		AfeOrdersActionHistoryEntity orderHistory = new AfeOrdersActionHistoryEntity();

		try {
			orderHistory.setActionId(actionIdq2);
			orderHistory.setFixedOrderId(fixedOrderIdQ1);
			orderHistory.setModelColorId(modelColorIdQ1);
			orderHistory.setEnvioFlagGm(envioFlagQ1);
			
			orderHistory.setObs(
					String.format("Client IP: %s , TimeStamp: %s", this.ipAddress, AckgmUtils.getTimeStamp()));
			orderHistory.setBstate(1);
			afeOrdersHistoryRepository.save(orderHistory);

			ackgmMessagesHandler.createAndLogMessageSuccessAction(maxTransitDetail);
		} catch (Exception e) {

			ackgmMessagesHandler.createAndLogMessageOrderHistoryFail("INSERT * INTO AFE_ORDER_HISOTRY");
			return Boolean.FALSE;

		}

		log.debug("End:: finalFlow");
		return Boolean.TRUE;

	}

}
