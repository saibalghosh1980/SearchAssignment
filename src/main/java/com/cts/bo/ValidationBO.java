package com.cts.bo;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "VALIDATION-ERROR")
@ApiModel(value="VALIDATION-ERROR")
public class ValidationBO{
	
	@ApiModelProperty(required = true)
	@JsonProperty("VALIDATION-MESSAGE")
	private String validateErrorMessage;

	@XmlElement(name = "VALIDATION-MESSAGE")
	public String getValidateErrorMessage() {
		return validateErrorMessage;
	}

	
	public void setValidateErrorMessage(String validateErrorMessage) {
		this.validateErrorMessage = validateErrorMessage;
	}

	public ValidationBO(String validateErrorMessage) {		
		this.validateErrorMessage = validateErrorMessage;
	}
	
	public ValidationBO(){
		
	}
	
	

}
