package com.honda.olympus.service;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
import com.honda.olympus.vo.EventVO;
import com.honda.olympus.vo.MaxTransitCallVO;
import com.honda.olympus.vo.MaxTransitResponseVO;

@Service
public class AckgmHdmService {

	@Value("${maxtransit.timewait}")
	private Long timeWait;

	@Value("${service.name}")
	private String serviceName;

	@Autowired
	LogEventService logEventService;

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

	public void callAckgmHd() {
		EventVO event;

		Boolean successFlag = Boolean.FALSE;

		MaxTransitCallVO maxTransitMessage = new MaxTransitCallVO();

		maxTransitMessage.setRequest(AckgmConstants.ACK);

		List<MaxTransitResponseVO> maxTransitData = maxTransitService.generateCallMaxtransit(maxTransitMessage);

		if (maxTransitData.isEmpty()) {

			logEventService.sendLogEvent(new EventVO(serviceName, AckgmConstants.ZERO_STATUS,
					"La respuesta de MAXTRANSIT no tiene elementos: " + maxTransitData.toString(), ""));
			System.out.println("MAXTRANSIT empty response");
		}

		// Node 4
		Iterator<MaxTransitResponseVO> it = maxTransitData.iterator();
		while (it.hasNext()) {
			MaxTransitResponseVO maxTransitDetail = it.next();
			Long rqstIdentifier = maxTransitDetail.getRqstIdentfr();
			System.out.println("----rqstIdentifier----:: " + rqstIdentifier);

			if (rqstIdentifier < 0) {
				logEventService.sendLogEvent(new EventVO(serviceName, AckgmConstants.ZERO_STATUS,
						"No tiene un valor mayor o igual a cero reqst_identfr, " + rqstIdentifier
								+ " respuesta de MAXTRANSIT: " + maxTransitDetail.toString(),
						""));

				System.out.println("No tiene un valor mayor o igual a cero reqst_identfr:" + rqstIdentifier);
				break;

			}

			// QUERY1
			List<AfeFixedOrdersEvEntity> fixedOrders = afeFixedOrdersEvRepository.findAllByRqstId(rqstIdentifier);

			if (fixedOrders.isEmpty()) {
				System.out.println(
						"No se encontro requst_idntfr: " + rqstIdentifier + " en la tabla AFE_FIXED_ORDERS_EV");
				event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS,
						"No se encontro requst_idntfr: " + rqstIdentifier + " en la tabla AFE_FIXED_ORDERS_EV", "");
				logEventService.sendLogEvent(event);

				// return to main line process loop
				break;
			}

			AfeFixedOrdersEvEntity fixedOrder = fixedOrders.get(0);

			if (AckgmConstants.ACCEPTED_STATUS.equalsIgnoreCase(maxTransitDetail.getReqstStatus())) {
				System.out.println("Start:: Accepted flow");
				try {
					// QUERY2
					AfeAckEvEntity ackEntity = new AfeAckEvEntity();

					ackEntity.setFixedOrderId(fixedOrder.getId());
					ackEntity.setAckStatus(maxTransitDetail.getReqstStatus());
					ackEntity.setAckMsg(maxTransitDetail.getMesagge().toString());
					ackEntity.setAckRequestTimestamp(new Date());
					afeAckEvRepository.saveAndFlush(ackEntity);
				} catch (Exception e) {
					System.out.println(
							"Fallo en la ejecución del query de inserción en la tabla AFE_FIXED_ORDERS_EV con el query ");
					event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS,
							"Fallo en la ejecución del query de inserción en la tabla AFE_FIXED_ORDERS_EV con el query ",
							"");
					logEventService.sendLogEvent(event);
					break;
				}

				if (finalFlow(maxTransitDetail, fixedOrder)) {
					successFlag = Boolean.TRUE;
					System.out.println("End:: Accepted flow");
				}

			} else {
				if (AckgmConstants.FAILED_STATUS.equalsIgnoreCase(maxTransitDetail.getReqstStatus())) {

					if (failedFlow(fixedOrder.getId(), maxTransitDetail)) {

						if (finalFlow(maxTransitDetail, fixedOrder)) {
							successFlag = Boolean.TRUE;
							System.out.println("End:: Accepted flow");
						}

					}

				} else

				if (AckgmConstants.CANCELED_STATUS.equalsIgnoreCase(maxTransitDetail.getReqstStatus())) {
					canceledFlow();
					
					if (finalFlow(maxTransitDetail, fixedOrder)) {
						successFlag = Boolean.TRUE;
						System.out.println("End:: Accepted flow");
					}

				}

			}

		}

	}

	private Boolean failedFlow(final Long fixedOrderId, MaxTransitResponseVO maxTransitDetail) {
		EventVO event;
		System.out.println("Start:: Failed Flow ");

		// QUERY6
		List<AfeAckEvEntity> acks = afeAckEvRepository.findAllByFixedOrderId(fixedOrderId);

		if (acks.isEmpty()) {
			System.out.println("No existe el fixed_order_id: " + fixedOrderId + " en la tabla AFE_ACK_EV");
			event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS,
					"No existe el fixed_order_id: " + fixedOrderId + " en la tabla AFE_ACK_EV", "");
			logEventService.sendLogEvent(event);
			return Boolean.FALSE;
		}

		try {
			// QUERY7
			acks.get(0).setAckStatus(maxTransitDetail.getReqstStatus());
			acks.get(0).setAckMsg(maxTransitDetail.getMesagge().toString());
			acks.get(0).setUpdateTimeStamp(new Date());
			afeAckEvRepository.saveAndFlush(acks.get(0));
		} catch (Exception e) {
			System.out.println(
					"Fallo en la ejecución del query de actualización en la tabla AFE_FIXED_ORDERS_EV con el query ");
			event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS,
					"Fallo en la ejecución del query de actualización en la tabla AFE_FIXED_ORDERS_EV con el query ",
					"");
			logEventService.sendLogEvent(event);
			return Boolean.FALSE;
		}

		System.out.println("End:: Failed Flow ");

		return Boolean.TRUE;

	}

	private Boolean canceledFlow() {
		System.out.println("Start:: Failed Flow ");
		System.out.println("End:: Failed Flow ");

		return Boolean.TRUE;

	}

	private Boolean finalFlow(MaxTransitResponseVO maxTransitDetail, AfeFixedOrdersEvEntity fixedOrder) {

		System.out.println("Start:: finalFlow");
		EventVO event;
		try {
			// QUERY3
			fixedOrder.setEnvioFlag(Boolean.FALSE);
			fixedOrder.setUpdateTimeStamp(new Date());
			afeFixedOrdersEvRepository.saveAndFlush(fixedOrder);

		} catch (Exception e) {
			System.out.println("Fallo en la ejecución del query de actualización en la tabla AFE_ACK_EV con el query ");
			event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS,
					"Fallo en la ejecución del query de inserción en la tabla AFE_ACK_EV con el query ", "");
			logEventService.sendLogEvent(event);
			return Boolean.FALSE;
		}

		// QUERY4
		List<AfeActionEntity> actions = afeActionRepository.findAllByAction(maxTransitDetail.getAction());
		if (actions.isEmpty()) {
			System.out.println(
					"No se encontro la accion: \"+maxTransitDetail.getAction()+\" en la tabla AFE_ACTION  con el query\"");
			event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS, "No se encontro la accion: "
					+ maxTransitDetail.getAction() + " en la tabla AFE_ACTION  con el query", "");
			logEventService.sendLogEvent(event);
			return Boolean.FALSE;
		}

		// QUERY5
		AfeOrdersHistoryEntity orderHistory = new AfeOrdersHistoryEntity();

		try {
			orderHistory.setActionId(actions.get(0).getId());
			orderHistory.setFixedOrderId(fixedOrder.getId());
			orderHistory.setCreationTimeStamp(new Date());
			afeOrdersHistoryRepository.save(orderHistory);

			System.out.println("El proceso fue exitoso para la orden: " + fixedOrder.getId());
			event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS,
					"El proceso fue exitoso para la orden: " + fixedOrder.getId(), "");
			logEventService.sendLogEvent(event);
		} catch (Exception e) {
			System.out.println("Fallo de inserción en la tabla AFE_ORDER_HISOTRY");
			event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS,
					"Fallo de inserción en la tabla AFE_ORDER_HISOTRY con el query ", "");
			logEventService.sendLogEvent(event);
			return Boolean.FALSE;

		}

		System.out.println("End:: finalFlow");
		return Boolean.TRUE;

	}

}
