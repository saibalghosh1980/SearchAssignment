package com.cts.bo;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@XmlSeeAlso({ FileSearchResultBO.class })
@XmlRootElement(name = "SEARCHRESULT")
@ApiModel(value = "SEARCHRESULT")
public class SearchBO<T> {

	@ApiModelProperty(required = true, name = "TOTAL")
	@JsonProperty("TOTAL")
	private int totalMatch;
	@ApiModelProperty(required = true, name = "MATCHED-FILES")
	@JsonProperty("MATCHED-FILES")
	private List<T> matchedFiles = new ArrayList<>();

	@XmlElement(name = "TOTAL")
	public int getTotalMatch() {
		return totalMatch;
	}

	public void setTotalMatch(int totalMatch) {
		this.totalMatch = totalMatch;
	}

	@XmlElement(name = "MATCHED-FILES")
	public List<T> getMatchedFiles() {
		return matchedFiles;
	}

	public void setMatchedFiles(List<T> matchedFiles) {
		this.matchedFiles = matchedFiles;
	}

}
