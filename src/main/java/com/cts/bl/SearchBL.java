package com.cts.bl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.cts.bo.FileSearchResultBO;
import com.cts.bo.SearchBO;

@Service("springManagedSearchBL")
public class SearchBL {
	private static Logger log = Logger.getLogger(SearchBL.class);

	public SearchBO<FileSearchResultBO> getMatchedFiles(String[] wordsToMatch) {
		ForkJoinPool fjpForSearchThroughFiles = new ForkJoinPool(4 * 10);
		SearchBO<FileSearchResultBO> searchResults = new SearchBO<FileSearchResultBO>();
		try {
			List<Path> filesToLookFor1 = Files.list(new File("D://TestData//1").toPath()).parallel()
					.filter(Files::isRegularFile).filter(file -> file.toString().toLowerCase().endsWith(".txt"))
					.collect(Collectors.toList());
			List<Path> filesToLookFor = getAllFilesUnderDirectory(new File("D://TestData"));
			// log.info("Total number of file--" + filesToLookFor.size());
			// .forEach(p->log.info(p.getFileName()));

			try {
				/*
				 * fjpForSearchThroughFiles.submit(() ->
				 * filesToLookFor.parallelStream() .filter(file ->
				 * isFileContainsWords(file.toFile(),
				 * wordsToMatch)).map(matchedFile -> { FileSearchResultBO
				 * fileResult = new FileSearchResultBO();
				 * fileResult.setFileName(matchedFile.toFile().getName());
				 * fileResult.setFileLocation(matchedFile.toString()); return
				 * fileResult; }).forEach(file -> {
				 * searchResults.getMatchedFiles().add(file);
				 * 
				 * })).get();
				 */
				List<FileSearchResultBO> matchedFiles = fjpForSearchThroughFiles
						.submit(() -> filesToLookFor.parallelStream()
								.filter(file -> isFileContainsWords(file.toFile(), wordsToMatch)).map(matchedFile -> {
									FileSearchResultBO fileResult = new FileSearchResultBO();
									fileResult.setFileName(matchedFile.toFile().getName());
									fileResult.setFileLocation(matchedFile.toString());
									return fileResult;
								}).collect(Collectors.toList()))
						.get();
				searchResults.setMatchedFiles(matchedFiles);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error(ExceptionUtils.getStackFrames(e));
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		searchResults.setTotalMatch(searchResults.getMatchedFiles().size());
		return searchResults;
	}

	private boolean isFileContainsWords(File fileToConsider, String[] wordsToMatch) {
		// log.info("The name of the thread handling this is -->" +
		// Thread.currentThread().getName());
		boolean result = Arrays.stream(wordsToMatch).parallel().filter(p -> isFileContainsWord(fileToConsider, p))
				.count() == wordsToMatch.length;
		if (!result)
			log.info("The file did not matched" + fileToConsider.getName());
		return result;
	}

	private boolean isFileContainsWord(File fileToConsider, String wordToMatch) {
		// System.out.println(Thread.currentThread().getName()+"--> Sleeping");
		try {
			return Files.readAllLines(fileToConsider.toPath()).parallelStream()
					.anyMatch(line -> line.indexOf(wordToMatch) != -1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error(ExceptionUtils.getStackFrames(e));
		}
		return false;
	}

	private List<Path> getAllFilesUnderDirectory(File directory) {
		// log.info("Called -->" + directory.getName());
		final List<Path> files = new ArrayList<>();
		try {
			files.addAll(Files.list(directory.toPath()).parallel().filter(Files::isRegularFile)
					.filter(file -> file.toString().toLowerCase().endsWith(".txt")).collect(Collectors.toList()));
			Files.list(directory.toPath()).filter(Files::isDirectory).forEach(d -> {
				files.addAll(getAllFilesUnderDirectory(d.toFile()));

			});

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return files;

	}

}
