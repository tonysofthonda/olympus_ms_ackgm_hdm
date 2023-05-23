package com.honda.olympus.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.honda.olympus.dao.AfeActionEntity;
import com.honda.olympus.dao.AfeFixedOrdersEvEntity;
import com.honda.olympus.dao.AfeOrdersHistoryEntity;
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
	private AfeActionRepository afeActionRepository;

	public void callAckgmHd() {
		EventVO event;

		Boolean successFlag = Boolean.FALSE;

		MaxTransitCallVO maxTransitMessage = new MaxTransitCallVO();
		
		maxTransitMessage.setRequest(AckgmConstants.ACK);

		List<MaxTransitResponseVO> maxTransit = maxTransitService.generateCallMaxtransit(maxTransitMessage);

		if (maxTransit.isEmpty()) {

			logEventService.sendLogEvent(new EventVO(serviceName, AckgmConstants.ZERO_STATUS,
					"La respuesta de MAXTRANSIT no tiene elementos: " + maxTransit.toString(), ""));
			System.out.println("Error calling service, Timeout");
		}

		for (MaxTransitResponseVO response : maxTransit) {

			Long rqstIdentifier = response.getRqstIdentfr();

			if (rqstIdentifier < 0) {
				logEventService.sendLogEvent(new EventVO(serviceName, AckgmConstants.ZERO_STATUS,
						"No tiene un valor mayor o igual a cero: " + rqstIdentifier, ""));

				System.out.println("No tiene un valor mayor o igual a cero: " + rqstIdentifier);

			}

			List<AfeFixedOrdersEvEntity> fixedOrders = afeFixedOrdersEvRepository.findAllById(rqstIdentifier);

			if (fixedOrders.isEmpty()) {
				System.out.println("End first altern flow");
				event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS,
						"No se encontro requstIdentifier: " + rqstIdentifier + " en la tabla AFE_FIXED_ORDERS_EV", "");
				logEventService.sendLogEvent(event);

				// return to main line process loop
				return;
			}

			List<AfeActionEntity> actions = afeActionRepository.findAllByAction(fixedOrders.get(0).getAckId());
			if (actions.isEmpty()) {
				System.out.println("End fifth altern flow");
				event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS,
						"Existe el MODEL_TYPE: en la tabla afedb.afe_model_type con el query: ", "");
				logEventService.sendLogEvent(event);

				// return to main line process loop
				return;
			}

			AfeOrdersHistoryEntity orderHistory = new AfeOrdersHistoryEntity();
			orderHistory.setActionId(actions.get(0).getId());
			orderHistory.setFixedOrderId(fixedOrders.get(0).getId());
			orderHistory.setCreationTimeStamp(new Date());
			try {
				afeOrdersHistoryRepository.save(orderHistory);

				System.out.println("El proceso fue exitoso para la orden: " + fixedOrders.get(0).getId());
				event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS,
						"El proceso fue exitoso para la orden: " + fixedOrders.get(0).getId(), "");
				logEventService.sendLogEvent(event);
			} catch (Exception e) {
				System.out.println("Error inserting afedb.afe_fixed_orders_ev");
				event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS,
						"Fallo de inserción en la tabla AFE_ORDER_HISOTRY", "");
				logEventService.sendLogEvent(event);

			}

			successFlag = false;

		}

	}

}
