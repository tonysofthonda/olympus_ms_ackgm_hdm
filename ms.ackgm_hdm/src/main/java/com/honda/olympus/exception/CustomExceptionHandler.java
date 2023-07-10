package com.honda.olympus.exception;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.honda.olympus.service.LogEventService;
import com.honda.olympus.vo.EventVO;
import org.hibernate.exception.JDBCConnectionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.honda.olympus.service.NotificationService;
import com.honda.olympus.utils.AckgmConstants;
import com.honda.olympus.vo.MessageVO;
import com.honda.olympus.vo.ResponseVO;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler{
	
	@Value("${service.name}")
	private String serviceName;
	
	@Value("${spring.datasource.url}")
	private String host;
	
	@Value("${spring.datasource.username}")
	private String user;
	
	@Autowired
	NotificationService notificationService;

	@Autowired
	private LogEventService logEventService;
	
	@ExceptionHandler(Exception.class)
	public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request){

		String notificationMessage = String.format( "No es posible conectarse a la base de datos: %s User: %s", host,user);
		this.logEventService.sendLogEvent(new EventVO(serviceName, AckgmConstants.ZERO_STATUS, notificationMessage, ""));

		MessageVO message = new MessageVO(serviceName, AckgmConstants.ZERO_STATUS, "104 Error al obtener la información. No se pudo obtener la información correctamente de MAXTRANSIT, favor de revisar", "");
		notificationService.generatesNotification(message);


		ResponseVO error = new ResponseVO(serviceName,0L,"Unknown", "");
		
		return new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(NotificationEmailException.class)
	public final ResponseEntity<Object> handleNotificationEmail(Exception ex, WebRequest request){
		
		List<String> details = new ArrayList<>();
		
		details.add(ex.getLocalizedMessage());
		ResponseVO error = new ResponseVO(serviceName,0L,ex.getMessage(),"");
		
		return new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(JDBCConnectionException.class)
	public final ResponseEntity<Object> handleDataBaseException(Exception ex, WebRequest request){
		
		List<String> details = new ArrayList<>();
		
		details.add(ex.getLocalizedMessage());
		ResponseVO error = new ResponseVO(serviceName,0L,"DataBaseException","");
		
		return new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	 @Override
	    protected ResponseEntity<Object> handleMethodArgumentNotValid(
	            MethodArgumentNotValidException ex,
	            HttpHeaders headers, HttpStatus status, WebRequest request) {
	             
	        Map<String, Object> responseBody = new LinkedHashMap<String, Object>();
	        responseBody.put("timestamp", new Date());
	        responseBody.put("status", status.value());
	         
	        List<String> errors = ex.getBindingResult().getFieldErrors()
	            .stream()
	            .map(x -> x.getDefaultMessage())
	            .collect(Collectors.toList());
	         
	        responseBody.put("errors", errors);
	         
	        return new ResponseEntity<>(responseBody, headers, status);
	    }

}
