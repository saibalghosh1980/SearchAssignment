package com.cts.bl;

import static org.assertj.core.api.Assertions.linesOf;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.cts.bo.FileSearchResultBO;
import com.cts.bo.SearchBO;

@Service("springManagedSearchBL")
public class SearchBL {

	public SearchBO<FileSearchResultBO> getMatchedFiles(String[] wordsToMatch) {
		ForkJoinPool myPool = new ForkJoinPool(4*10);
		SearchBO<FileSearchResultBO> searchResults = new SearchBO<FileSearchResultBO>();
		try {
			List<Path> filesToLookFor=Files.list(new File("D://").toPath()).parallel().filter(Files::isRegularFile)
					.filter(p -> p.toString().toLowerCase().endsWith(".txt")).collect(Collectors.toList());
			try {
				myPool.submit(() ->filesToLookFor.parallelStream().filter(p -> isFileContainsWords(p.toFile(), wordsToMatch)).map(matchedFile -> {
							FileSearchResultBO fileResult = new FileSearchResultBO();
							fileResult.setFileName(matchedFile.toFile().getName());
							fileResult.setFileLocation(matchedFile.toString());
							return fileResult;
						}).forEach(file -> {
							searchResults.getMatchedFiles().add(file);

						})).get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		searchResults.setTotalMatch(searchResults.getMatchedFiles().size());
		return searchResults;
	}

	private boolean isFileContainsWords(File fileToConsider, String[] wordsToMatch) {
		try {
			System.out.println(Thread.currentThread().getName()+"--> Sleeping");
			Thread.sleep(0);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Arrays.stream(wordsToMatch).parallel().filter(p -> isFileContainsWord(fileToConsider, p))
				.count() == wordsToMatch.length;
	}

	private boolean isFileContainsWord(File fileToConsider, String wordToMatch) {
		//System.out.println(Thread.currentThread().getName()+"--> Sleeping");
		try {
			return Files.readAllLines(fileToConsider.toPath()).parallelStream()
					.anyMatch(line -> line.indexOf(wordToMatch) != -1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
