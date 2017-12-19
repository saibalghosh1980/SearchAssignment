package com.cts.bo;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "FILE")
public class FileSearchResultBO {	
	@ApiModelProperty(required = true)
	@JsonProperty("FILE-NAME")
	private String fileName;
	@ApiModelProperty(required = true)
	@JsonProperty("FILE-LOCATION")
	private String fileLocation;
	public String getFileName() {
		return fileName;
	}
	@XmlElement(name="FILE-NAME")
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileLocation() {
		return fileLocation;
	}
	@XmlElement(name="FILE-LOCATION")
	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}	
	

}
