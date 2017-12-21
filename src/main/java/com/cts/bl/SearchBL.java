package com.cts.bl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.cts.bo.FileSearchResultBO;
import com.cts.bo.SearchBO;

@Service("springManagedSearchBL")
@PropertySource("classpath:application.yml")
public class SearchBL {
	private static Logger log = Logger.getLogger(SearchBL.class);
	@Value("${search-app.root-dir}")
	private String rootDirectory;
	@Value("${recursive-dir-search}")
	private boolean recursiveDirectorySearch;
	@Value("${case-sensitive}")
	private boolean caseSensitiveSearch;
	@Value("${file-ext-to-search}")
	private String[] allowedFileExtensions;

	// -----------------------Setters for option which may be passed from front
	// end--------------------------------
	/**
	 * Set the value for recursive directory search
	 * 
	 * @param recursiveDirectorySearch
	 *            True if the directory and all it's subdirectory are searched
	 */
	public void setRecursiveDirectorySearch(boolean recursiveDirectorySearch) {
		this.recursiveDirectorySearch = recursiveDirectorySearch;
	}

	/**
	 * Set the value for case sensitive search
	 * 
	 * @param caseSensitiveSearch
	 *            True if the search is case sensitive
	 */
	public void setCaseSensitiveSearch(boolean caseSensitiveSearch) {
		this.caseSensitiveSearch = caseSensitiveSearch;
	}

	/**
	 * This methods returned the files which contain the words which were
	 * searched
	 * 
	 * @param wordsToMatch
	 *            This are the words to match
	 * @return Search Result containing number of files matched with details of
	 *         the file like name and path
	 * @throws Exception
	 *             Throw an exception which is handled in the controller to show
	 *             appropiate message.
	 */
	public SearchBO<FileSearchResultBO> getMatchedFiles(String[] wordsToMatch) throws Exception {
		//Setting the pool size of threads for parallel processing of search
		ForkJoinPool fjpForSearchThroughFiles = new ForkJoinPool(Runtime.getRuntime().availableProcessors() * 1000);
		SearchBO<FileSearchResultBO> searchResults = new SearchBO<FileSearchResultBO>();
		try {
			List<Path> allFilesToSearch = getAllFilesUnderDirectory(new File(rootDirectory));
			List<FileSearchResultBO> matchedFiles = fjpForSearchThroughFiles
					.submit(() -> allFilesToSearch.parallelStream()
							.filter(file -> isFileContainsWords(file.toFile(), wordsToMatch)).map(matchedFile -> {
								FileSearchResultBO fileResult = new FileSearchResultBO();
								fileResult.setFileName(matchedFile.toFile().getName());
								fileResult.setFileLocation(matchedFile.toString());
								return fileResult;
							}).collect(Collectors.toList()))
					.get();
			searchResults.setMatchedFiles(matchedFiles);

			searchResults.setTotalMatch(searchResults.getMatchedFiles().size());
			return searchResults;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(ExceptionUtils.getStackFrames(e));
			throw e;
		}
	}

	/**
	 * This method searches a single file for the words which were searched for
	 * 
	 * @param fileToConsider
	 *            File in which search will take place
	 * @param wordsToMatch
	 *            Words which will be searched
	 * @return true or false
	 */
	private boolean isFileContainsWords(File fileToConsider, String[] wordsToMatch) {
		boolean result = Arrays.stream(wordsToMatch).parallel().filter(p -> isFileContainsWord(fileToConsider, p))
				.count() == wordsToMatch.length;
		if (!result)
			log.info("The file did not matched" + fileToConsider.getName());
		return result;
	}

	/**
	 * This method looks for a word in the file supplied
	 * 
	 * @param fileToConsider
	 *            File in which search will take place
	 * @param wordToMatch
	 *            Word to search
	 * @return Whether the word is found or not
	 */
	private boolean isFileContainsWord(File fileToConsider, String wordToMatch) {
		try {
			// Search within a text file
			if (FilenameUtils.getExtension(fileToConsider.getName()).equalsIgnoreCase("txt"))
				return Files.readAllLines(fileToConsider.toPath()).parallelStream().anyMatch(line -> {
					if (caseSensitiveSearch)
						return (line.indexOf(wordToMatch) != -1);
					else
						return line.toLowerCase().indexOf(wordToMatch.toLowerCase()) != 1;
				});
			else if (FilenameUtils.getExtension(fileToConsider.getName()).equalsIgnoreCase("pdf")) {
				log.info("Implementation not done for pdf file");
				return false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("We are skipping the processsing of this file--" + fileToConsider.getName()
					+ ". The reason is as follows");
			log.error(ExceptionUtils.getStackFrames(e));
		}
		return false;
	}

	/**
	 * This method returns all the files under a directory
	 * 
	 * @param directory
	 *            Directory to search
	 * @return All the files under that directory (depends upon a parameter
	 *         whether the search will be recursive or at that directory level
	 *         only
	 */
	private List<Path> getAllFilesUnderDirectory(File directory) {
		final List<Path> files = new ArrayList<>();

		try {
			files.addAll(Files.list(directory.toPath()).parallel().filter(Files::isRegularFile)
					.filter(file -> Arrays.asList(allowedFileExtensions)
							.contains(FilenameUtils.getExtension(file.toFile().getName().toLowerCase())))
					.collect(Collectors.toList()));
			if (recursiveDirectorySearch) // If recursive search then get all
											// the files from sub-directories
				Files.list(directory.toPath()).filter(Files::isDirectory).forEach(d -> {
					files.addAll(getAllFilesUnderDirectory(d.toFile()));
				});

		} catch (IOException e) {
			log.error("We are skipping the processsing of this directory--" + directory.getName()
					+ ". The reason is as follows");
			log.error(ExceptionUtils.getStackFrames(e));
		}
		return files;

	}

}
