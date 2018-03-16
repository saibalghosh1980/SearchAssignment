package com.cts.search;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;

import com.cts.search.controller.SearchController;

@RunWith(SpringRunner.class)
@WebMvcTest(SearchController.class)
public class SearchServiceApplicationTests {
	@Autowired
	private MockMvc mvc;

	@Test
	public void testSingleWordCaseSensitive() throws Exception {
		mvc.perform(get("/search/files").param("recursivesearch", "Y").param("casesensitive", "Y")
				.param("searchwords", "aaaa").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.TOTAL", is(281)));
	}

	@Test
	public void testSingleWordCaseInSensitive() throws Exception {
		mvc.perform(get("/search/files").param("recursivesearch", "Y").param("casesensitive", "N")
				.param("searchwords", "AaAa").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.TOTAL", is(281)));
	}

	@Test
	public void testMultiWordCaseSensitive() throws Exception {
		mvc.perform(get("/search/files").param("recursivesearch", "Y").param("casesensitive", "Y")
				.param("searchwords", "aaaa Saibal").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.TOTAL", is(281)));
	}

	@Test
	public void testMultiWordCaseInSensitive() throws Exception {
		mvc.perform(get("/search/files").param("recursivesearch", "Y").param("casesensitive", "N")
				.param("searchwords", "AaAa SaiBal").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.TOTAL", is(281)));
	}

}
