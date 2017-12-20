package com.cts.bo;

import java.util.UUID;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "ERROR")
@ApiModel(value="ERROR")
@XmlType(propOrder = { "uniqueErrorId", "errorMessage", "stackTrace" })
@JsonPropertyOrder({ "id", "message", "errorStackTrace" })
public class ExceptionBO {

	@ApiModelProperty(required = true)
	@JsonProperty("id")
	private String uniqueErrorId;

	@JsonProperty("message")
	private String errorMessage;

	@JsonProperty("errorStackTrace")
	private String stackTrace;
	@JsonIgnore
	private Exception exception;

	public ExceptionBO() {
		super();
	}

	public ExceptionBO(Exception exception) {
		super();
		this.exception = exception;
		setUniqueErrorId(UUID.randomUUID().toString());
		setStackTrace(ExceptionUtils.getStackTrace(exception));
	}

	public ExceptionBO(String errorMessage, Exception exception) {
		super();
		this.exception = exception;
		setUniqueErrorId(UUID.randomUUID().toString());
		setStackTrace(ExceptionUtils.getStackTrace(exception));
		this.errorMessage = errorMessage;
	}

	public String getUniqueErrorId() {
		return uniqueErrorId;
	}

	@XmlElement(name = "ID")
	public void setUniqueErrorId(String uniqueErrorId) {
		this.uniqueErrorId = uniqueErrorId;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	@XmlElement(name = "MESSAGE")
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getStackTrace() {
		return stackTrace;
	}

	@XmlElement(name = "STACK-TRACE")
	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}

}
