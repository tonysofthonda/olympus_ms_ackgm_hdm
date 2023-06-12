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
import com.honda.olympus.dao.AfeFixedOrdersEvEntity;
import com.honda.olympus.dao.AfeOrdersHistoryEntity;
import com.honda.olympus.repository.AfeAckEvRepository;
import com.honda.olympus.repository.AfeActionRepository;
import com.honda.olympus.repository.AfeFixedOrdersEvRepository;
import com.honda.olympus.repository.AfeOrdersHistoryRepository;
import com.honda.olympus.utils.AckgmConstants;
import com.honda.olympus.utils.AckgmMessagesHandler;
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
	private AfeOrdersHistoryRepository afeOrdersHistoryRepository;

	@Autowired
	AfeAckEvRepository afeAckEvRepository;

	@Autowired
	private AfeActionRepository afeActionRepository;

	public void callAckgmCheckHd() throws JDBCConnectionException{
		EventVO event;

		Boolean successFlag = Boolean.FALSE;

		MaxTransitCallVO maxTransitMessage = new MaxTransitCallVO();

		maxTransitMessage.setRequest(AckgmConstants.ACK);

		List<MaxTransitResponseVO> maxTransitData = maxTransitService.generateCallMaxtransit(maxTransitMessage);

		if (maxTransitData.isEmpty()) {
			
			ackgmMessagesHandler.createAndLogMessage(maxTransitData);
		}

		// Node 4
		Iterator<MaxTransitResponseVO> it = maxTransitData.iterator();
		while (it.hasNext()) {
			MaxTransitResponseVO maxTransitDetail = it.next();
			Long rqstIdentifier = maxTransitDetail.getRqstIdentfr();
			log.debug("----rqstIdentifier----:: " + rqstIdentifier);

			if (rqstIdentifier < 0) {
				
				ackgmMessagesHandler.createAndLogMessage(rqstIdentifier, maxTransitDetail);
				break;

			}

			// QUERY1
			List<AfeFixedOrdersEvEntity> fixedOrders = afeFixedOrdersEvRepository.findAllByRqstId(rqstIdentifier);

			if (fixedOrders.isEmpty()) {
				
				ackgmMessagesHandler.createAndLogMessage(rqstIdentifier);
				
				// return to main line process loop
				break;
			}

			AfeFixedOrdersEvEntity fixedOrder = fixedOrders.get(0);

			if (AckgmConstants.ACCEPTED_STATUS.equalsIgnoreCase(maxTransitDetail.getReqstStatus())) {
				log.debug("Start:: Accepted flow");
				try {
					// QUERY2
					AfeAckEvEntity ackEntity = new AfeAckEvEntity();

					ackEntity.setFixedOrderId(fixedOrder.getId());
					ackEntity.setAckStatus(maxTransitDetail.getReqstStatus());

					JSONArray jsArray = new JSONArray(maxTransitDetail.getMesagge());
					
					ackEntity.setAckMsg(jsArray.toString());
					ackEntity.setAckRequestTimestamp(new Date());
					afeAckEvRepository.saveAndFlush(ackEntity);
				} catch (Exception e) {
					
					ackgmMessagesHandler.createAndLogMessage("INSERT * INTO AFE_FIXED_ORDERS_EV ");
					break;
				}

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
		
		if(successFlag) {		
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
		
		if(!acks.get(0).getAckStatus().equalsIgnoreCase(AckgmConstants.FAILED_STATUS)){
			
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
			
		}else {
			
			ackgmMessagesHandler.createAndLogMessageNoCancelOrder(fixedOrderId);
			return Boolean.FALSE;
			
		}

		log.debug("End:: Failed Flow ");

		return Boolean.TRUE;

	}

	private Boolean finalFlow(MaxTransitResponseVO maxTransitDetail, AfeFixedOrdersEvEntity fixedOrder) {

		log.debug("Start:: finalFlow");
		EventVO event;
		try {
			// QUERY3
			fixedOrder.setEnvioFlag(Boolean.FALSE);
			fixedOrder.setUpdateTimeStamp(new Date());
			afeFixedOrdersEvRepository.saveAndFlush(fixedOrder);

		} catch (Exception e) {

			ackgmMessagesHandler.createAndLogMessageAckUpdateFail("UPDATE * AFE_ACK_EV");
			return Boolean.FALSE;
		}

		// QUERY4
		List<AfeActionEntity> actions = afeActionRepository.findAllByAction(maxTransitDetail.getAction());
		if (actions.isEmpty()) {
			
			ackgmMessagesHandler.createAndLogMessageNoAction(maxTransitDetail, "");
			return Boolean.FALSE;
		}

		// QUERY5
		AfeOrdersHistoryEntity orderHistory = new AfeOrdersHistoryEntity();

		try {
			orderHistory.setActionId(actions.get(0).getId());
			orderHistory.setFixedOrderId(fixedOrder.getId());
			orderHistory.setCreationTimeStamp(new Date());
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
