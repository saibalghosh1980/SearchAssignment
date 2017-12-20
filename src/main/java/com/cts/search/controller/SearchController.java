package com.cts.search.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cts.bl.SearchBL;
import com.cts.bo.ExceptionBO;
import com.cts.bo.FileSearchResultBO;
import com.cts.bo.SearchBO;
import com.cts.bo.ValidationBO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/search")
@Api(value = "Search", description = "Operations pertaining to search")
public class SearchController {

	@Autowired
	@Qualifier("springManagedSearchBL")
	private SearchBL searchBusinessLogic;

	@CrossOrigin
	@ApiOperation(value = "Search words in the files and return the matched files", response = SearchBO.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "List of files (File Name and File Location) matched", response = SearchBO.class),
			@ApiResponse(code = 500, message = "Error Fetching", response = ExceptionBO.class),
			@ApiResponse(code = 400, message = "Validation Failed", response = ValidationBO.class) })
	@RequestMapping(value = "/files", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<?> getFileSearchResults(
			@ApiParam(value = "Please send search words. Multiple words should be placed one after another deliminated by space", required = false) @RequestParam(value = "searchwords", required = false) String searchParameters)
			throws Exception {

		if (StringUtils.isBlank(searchParameters))
			return new ResponseEntity<ValidationBO>(
					new ValidationBO("You have to enter atleast one word as 'searchWords' query parameter"),
					HttpStatus.BAD_REQUEST);

		String[] searchWords = searchParameters.replaceAll("\\s{2,}", " ").trim().split(" ");
		searchBusinessLogic.getMatchedFiles(searchWords);
		SearchBO<FileSearchResultBO> searchBO = searchBusinessLogic.getMatchedFiles(searchWords);

		return new ResponseEntity<SearchBO<FileSearchResultBO>>(searchBO, HttpStatus.OK);
	}

}
