package com.honda.olympus.utils;

import java.util.List;

import com.honda.olympus.service.NotificationService;
import com.honda.olympus.vo.MessageVO;
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

	@Autowired
	private NotificationService notificationService;

	@Value("${service.name}")
	private String serviceName;
	
	@Value("${service.success.message}")
	private String successMessage;

	private static final String MAJOR_EQUAL_VALIDATION = "No tiene un valor mayor o igual a cero reqst_identfr: %s, respuesta de MAXTRANSIT: %s ";
	private static final String MAX_TRANSIT_VALIDATION = "La respuesta de MAXTRANSIT no tiene elementos: %s ";
	private static final String REQUST_IDTFR_VALIDATION = "No se encontró reqst_idntfr: %s en la tabla AFE_FIXED_ORDERS_EV con el query: %s";
	private static final String QUERY_VALIDATION = 	"Fallo en la ejecución del query de inserción en la tabla AFE_FIXED_ORDERS_EV con el query: %s ";
	private static final String STATUS_VALIDATION = "El reqst_status no es valido: %s";
	private static final String FIXED_ORDER_NO_EXIST_ACK = "No existe el fixed_order_id: %s en la tabla AFE_ACK_EV";
	private static final String QUERY_EXECUTION_FAIL = "Fallo en la ejecución del query de actualización en la tabla AFE_FIXED_ORDERS_EV con el query: %s Due to: %s";
	private static final String NO_CANCEL_FAIL = "La orden: %s tiene un esatus: %s NO es posible cancelarla en la tabla AFE_ACK_EV ";
	private static final String QUERY_UPDATE_ACK_FAIL = "Fallo en la ejecución del query de actualización en la tabla AFE_ACK_EV con el query: %s";
	private static final String QUERY_UPDATE_ACTION_FAIL = "NO EXISTE la acción: %s en la tabla AFE_ACTION  con el query: %s";	
	private static final String ACTION_SUCCESS = "El proceso fué realizado con éxito para la orden: %s y estatus: %s";
	private static final String ORDER_HISTORY_FAIL = "Fallo en la jecución de inserción de la tabla AFE_ORDER_HISOTRY con el query: %s";
	private static final String ORDER_ACK_MESSAGE_FAIL = "Fallo en la jecución de inserción de la tabla AFE_ACK_MESSAGE con el query: %s Due to: %s";
	private static final String ORDER_ACK_MESSAGE_ALTERN_INSERT = "El registro: %s y %s tuvo un estatus de %s con la acción: %s";
	private static final String FIXED_ORDER_NOT_FOUN_ORDR_NBMR = "No se encontró EL reqst_idntfr: %s y el order_number: %s en la tabla AFE_FIXED_ORDERS_EV con el query: %s";
	private static final String INSERT_SUCCESS = "Actualización exitosa del registro: %s y el order_number: %s en la tabla AFE_FIXED_ORDERS_EV";
	
	
	
	

	private String message = null;
	EventVO event = null;

	public void createAndLogMessage(List<MaxTransitResponseVO> maxTransitData) {

		this.message = String.format(MAX_TRANSIT_VALIDATION, maxTransitData.toString());
		this.event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS, message, "");

		this.notificationService.generatesNotification(new MessageVO(serviceName, AckgmConstants.ZERO_STATUS, "104 Error al obtener la información. No se pudo obtener la información correctamente de MAXTRANSIT, favor de revisar", ""));
		sendAndLog();
	}

	public void createAndLogMessage(String rqstIdentifier, MaxTransitResponseVO maxTransitDetail) {

		this.message = String.format(MAJOR_EQUAL_VALIDATION, rqstIdentifier, maxTransitDetail.toString());
		this.event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS, message, "");

		this.notificationService.generatesNotification(new MessageVO(serviceName, AckgmConstants.ZERO_STATUS, "104 Error al obtener la información. No se pudo obtener la información correctamente de MAXTRANSIT, favor de revisar", ""));

		sendAndLog();
	}

	public void createAndLogMessageNoRqstIdtfr(String rqstIdentifier,String query) {

		this.message = String.format(REQUST_IDTFR_VALIDATION, rqstIdentifier,query);
		this.event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS, message, "");

		this.notificationService.generatesNotification(new MessageVO(serviceName, AckgmConstants.ZERO_STATUS, String.format("103 El request ID no se encontró en AFE. El request ID %s recibido de MAXTRANSIT no se encontro en la BD de AFE. Favor de revisar", rqstIdentifier), ""));

		sendAndLog();
	}
	
	
	public void createAndLogMessage(String query) {

		this.message = String.format(QUERY_VALIDATION, query);
		this.event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS, message, "");

		sendAndLog();
	}
	
	
	public void createAndLogMessage(MaxTransitResponseVO maxTransitDetail) {

		this.message = String.format(STATUS_VALIDATION, maxTransitDetail.getReqst_status());
		this.event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS, message, "");

		sendAndLog();
	}
	

	public void successMessage() {
	
		this.event = new EventVO(serviceName, AckgmConstants.ONE_STATUS,successMessage, "");
		
		logEventService.sendLogEvent(this.event);
		this.notificationService.generatesNotification(new MessageVO(serviceName, AckgmConstants.ONE_STATUS, "203 Guardado con Exito. Se proceso correctamente las ordenes de MAXTRANSIT", ""));
		log.debug("{}:: {}",serviceName,successMessage);
	}
	
	public void createAndLogMessageFixedOrderAck(Long fixedOrderId) {

		this.message = String.format(FIXED_ORDER_NO_EXIST_ACK,fixedOrderId);
		this.event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS, message, "");

		sendAndLog();
	}
	
	public void createAndLogMessageQueryFailed(String query,String cause) {

		this.message = String.format(QUERY_EXECUTION_FAIL, query,cause);
		this.event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS, message, "");

		sendAndLog();
	}
	
	public void createAndLogMessageNoCancelOrder(Long fixedOrderId) {

		this.message = String.format(NO_CANCEL_FAIL, fixedOrderId,AckgmConstants.CENCELED_STATUS);
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

		this.notificationService.generatesNotification(new MessageVO(serviceName, AckgmConstants.ZERO_STATUS, "104 Error al obtener la información. No se pudo obtener la información correctamente de MAXTRANSIT, favor de revisar", ""));

		sendAndLog();
	}
	
	public void createAndLogMessageSuccessAction(MaxTransitResponseVO maxTransitDetail) {

		this.message = String.format(ACTION_SUCCESS,maxTransitDetail.getReqst_identfr(),maxTransitDetail.getReqst_status());
		this.event = new EventVO(serviceName, AckgmConstants.ONE_STATUS, message, "");

		sendAndLog();
	}

	public void createAndLogMessageOrderHistoryFail(String query) {

		this.message = String.format(ORDER_HISTORY_FAIL,query);
		this.event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS, message, "");

		sendAndLog();
	}
	
	public void createAfeAckMessageFail(String query,String cause) {

		this.message = String.format(ORDER_ACK_MESSAGE_FAIL,query,cause);
		this.event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS, message, "");

		sendAndLog();
	}
	
	public void createAfeAckMessageAlternInsert(String reqstIdtfr, String orderNbr,String reqstStatus,String action) {

		this.message = String.format(ORDER_ACK_MESSAGE_ALTERN_INSERT,reqstIdtfr,orderNbr,reqstStatus,action);
		this.event = new EventVO(serviceName, AckgmConstants.ONE_STATUS, message, "");

		sendAndLog();
	}
	
	public void createAfeAckMessageNoFixedOrderByOrdNmbr(String reqstIdtfr, String orderNbr,String query) {

		this.message = String.format(FIXED_ORDER_NOT_FOUN_ORDR_NBMR,reqstIdtfr,orderNbr,query);
		this.event = new EventVO(serviceName, AckgmConstants.ONE_STATUS, message, "");

		sendAndLog();
	}
	
	public void createAfeAckMessageInsertSuccess(String reqstIdtfr, String orderNbr) {

		this.message = String.format(INSERT_SUCCESS,reqstIdtfr,orderNbr);
		this.event = new EventVO(serviceName, AckgmConstants.ONE_STATUS, message, "");

		sendAndLog();
	}
	
	
	

	private void sendAndLog() {
		logEventService.sendLogEvent(this.event);
		log.debug(this.message);
	}

}
