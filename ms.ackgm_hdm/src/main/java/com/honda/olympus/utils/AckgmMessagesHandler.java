package com.honda.olympus.utils;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.honda.olympus.service.LogEventService;
import com.honda.olympus.vo.EventVO;
import com.honda.olympus.vo.MaxTransitResponseVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AckgmMessagesHandler {

	@Autowired
	LogEventService logEventService;

	@Value("${service.name}")
	private String serviceName;
	
	@Value("${service.success.message}")
	private String successMessage;

	private static final String MAJOR_EQUAL_VALIDATION = "No tiene un valor mayor o igual a cero reqst_identfr: %s, respuesta de MAXTRANSIT: %s ";
	private static final String MAX_TRANSIT_VALIDATION = "La respuesta de MAXTRANSIT no tiene elementos: %s ";
	private static final String REQUST_IDTFR_VALIDATION = "No se encontró requst_idntfr: %s en la tabla AFE_FIXED_ORDERS_EV";
	private static final String QUERY_VALIDATION = 	"Fallo en la ejecución del query de inserción en la tabla AFE_FIXED_ORDERS_EV con el query: %s ";
	private static final String STATUS_VALIDATION = "El reqst_status no es valido: %s";
	private static final String FIXED_ORDER_NO_EXIST_ACK = "No existe el fixed_order_id: %s en la tabla AFE_ACK_EV";
	private static final String QUERY_EXECUTION_FAIL = "Fallo en la ejecución del query de actualización en la tabla AFE_FIXED_ORDERS_EV con el query: %s";
	private static final String NO_CANCEL_FAIL = "La orden: %s tiene un esatus: %s NO es posible cancelarla en la tabla AFE_ACK_EV ";
	private static final String QUERY_UPDATE_ACK_FAIL = "Fallo en la ejecución del query de actualización en la tabla AFE_ACK_EV con el query: %s";
	private static final String QUERY_UPDATE_ACTION_FAIL = "No se encontró la acción: %s en la tabla AFE_ACTION  con el query: %s";	
	private static final String ACTION_SUCCESS = "El proceso fué realizado con éxito para la orden: %s y estatus: %s";
	private static final String ORDER_HISTORY_FAIL = "Fallo de inserción en la tabla AFE_ORDER_HISOTRY con el query: %s";
	
	
	

	private String message = null;
	EventVO event = null;

	public void createAndLogMessage(List<MaxTransitResponseVO> maxTransitData) {

		this.message = String.format(MAX_TRANSIT_VALIDATION, maxTransitData.toString());
		this.event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS, message, "");

		sendAndLog();
	}

	public void createAndLogMessage(Long rqstIdentifier, MaxTransitResponseVO maxTransitDetail) {

		this.message = String.format(MAJOR_EQUAL_VALIDATION, rqstIdentifier, maxTransitDetail.toString());
		this.event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS, message, "");

		sendAndLog();
	}

	public void createAndLogMessage(Long rqstIdentifier) {

		this.message = String.format(REQUST_IDTFR_VALIDATION, rqstIdentifier);
		this.event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS, message, "");

		sendAndLog();
	}
	
	
	public void createAndLogMessage(String query) {

		this.message = String.format(QUERY_VALIDATION, query);
		this.event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS, message, "");

		sendAndLog();
	}
	
	
	public void createAndLogMessage(MaxTransitResponseVO maxTransitDetail) {

		this.message = String.format(STATUS_VALIDATION, maxTransitDetail.getReqstStatus());
		this.event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS, message, "");

		sendAndLog();
	}
	

	public void successMessage() {
	
		this.event = new EventVO(serviceName, AckgmConstants.ONE_STATUS,successMessage, "");
		
		logEventService.sendLogEvent(this.event);
		log.debug("{}:: {}",serviceName,successMessage);
	}
	
	public void createAndLogMessageFixedOrderAck(Long fixedOrderId) {

		this.message = String.format(FIXED_ORDER_NO_EXIST_ACK,fixedOrderId);
		this.event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS, message, "");

		sendAndLog();
	}
	
	public void createAndLogMessageQueryFailed(String query) {

		this.message = String.format(QUERY_EXECUTION_FAIL, query);
		this.event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS, message, "");

		sendAndLog();
	}
	
	public void createAndLogMessageNoCancelOrder(Long fixedOrderId) {

		this.message = String.format(NO_CANCEL_FAIL, fixedOrderId,AckgmConstants.FAILED_STATUS);
		this.event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS, message, "");

		sendAndLog();
	}
	
	public void createAndLogMessageAckUpdateFail(String query) {

		this.message = String.format(QUERY_UPDATE_ACK_FAIL, query);
		this.event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS, message, "");

		sendAndLog();
	}
	
	
	public void createAndLogMessageNoAction(MaxTransitResponseVO maxTransitDetail,String query) {

		this.message = String.format(QUERY_UPDATE_ACTION_FAIL,maxTransitDetail.getAction(),query);
		this.event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS, message, "");

		sendAndLog();
	}
	
	public void createAndLogMessageSuccessAction(MaxTransitResponseVO maxTransitDetail) {

		this.message = String.format(ACTION_SUCCESS,maxTransitDetail.getRqstIdentfr(),maxTransitDetail.getReqstStatus());
		this.event = new EventVO(serviceName, AckgmConstants.ONE_STATUS, message, "");

		sendAndLog();
	}

	public void createAndLogMessageOrderHistoryFail(String query) {

		this.message = String.format(ORDER_HISTORY_FAIL,query);
		this.event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS, message, "");

		sendAndLog();
	}

	private void sendAndLog() {
		logEventService.sendLogEvent(this.event);
		log.debug(this.message);
	}

}
