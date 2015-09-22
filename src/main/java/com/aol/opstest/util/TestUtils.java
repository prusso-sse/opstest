package com.aol.opstest.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestUtils {
	public static final String CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	public static SecureRandom RANDOM_GENERATOR = new SecureRandom();
	private static final int MAX_WORD_SIZE = 12;
	private static final int MAX_WORDS_IN_LINE = 50;
	private static final int MIN_WORDS_IN_LINE = 10;
		
	public static String createRandomString(int length) {
		RANDOM_GENERATOR.setSeed(System.currentTimeMillis());

		StringBuilder result = new StringBuilder(length);
		for(int i = 0; i < length; i++) {
			result.append(CHARSET.charAt(RANDOM_GENERATOR.nextInt(CHARSET.length())));
		}
		
		return result.toString();
	}
	
	public static String createRandomLineContaining(List<String> theseWords, int wordCount) {
		RANDOM_GENERATOR.setSeed(System.currentTimeMillis());
		
		StringBuilder result = new StringBuilder();
		
		Map<Integer, String> wordList = new HashMap<Integer, String>();
		for(int i = 0; i < theseWords.size(); ) {
			String value = theseWords.get(i);
			Integer index = RANDOM_GENERATOR.nextInt(wordCount);
			
			if(!wordList.containsKey(index)) {
				wordList.put(index, value);
				i++;
			}
		}
		
		for(int i = 0; i < wordCount; i++) {
			if(wordList.containsKey(i)) {
				result.append(wordList.get(i));
			} else {
				result.append(TestUtils.createRandomString(RANDOM_GENERATOR.nextInt(MAX_WORD_SIZE) + 1));
			}
			
			// Randomly concat the next string without a space
			if(RANDOM_GENERATOR.nextInt(100) < 90) { 
				result.append(" ");
			}
		}
		
		/*
		 * Append a newline
		 */
		result.append("\n");
		
		return result.toString();
	}
	
	public static void createRandomDataFile(File usingFileContents, int lineCount) {
		List<String> words = new ArrayList<String>();
		try(BufferedReader br = new BufferedReader(new FileReader(usingFileContents))) {
		    for(String line; (line = br.readLine()) != null; ) {
		        String name = line.trim();
		        
		        if(name.isEmpty()) {
		        	continue;
		        }
		        
		        words.add(name);
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		TestUtils.RANDOM_GENERATOR.setSeed(System.currentTimeMillis());
		
		int randomFilename = RANDOM_GENERATOR.nextInt(Integer.MAX_VALUE);
		String filename = "randomData_" + randomFilename + ".txt";
		
		FileWriter fw = null;
		try {
			fw = new FileWriter(filename);
		 
			for (int i = 0; i < lineCount; i++) {
				int wordsInLine = RANDOM_GENERATOR.nextInt(MAX_WORDS_IN_LINE - MIN_WORDS_IN_LINE) + MIN_WORDS_IN_LINE;
				/*
				 * See if we put words in this line
				 */
				List<String> wordsToUse = new ArrayList<String>();				
				if(RANDOM_GENERATOR.nextInt(100) < 50) {
					/*
					 * Lets add at least 1 word
					 */
					String thisWord = words.get(RANDOM_GENERATOR.nextInt(words.size()));
					
					/*
					 * Now, do we mess with the case?
					 */
					int caseChange = RANDOM_GENERATOR.nextInt(100); 
					if(caseChange < 33) {
						thisWord = thisWord.toUpperCase();
					} else if(caseChange < 66) {
						thisWord = thisWord.toLowerCase();
					}
					
					/*
					 * Add the word
					 */
					wordsToUse.add(thisWord);
					
					if(RANDOM_GENERATOR.nextInt(100) < 50) {
						/*
						 * We're going to use multiple words in this line (up to 4 more)
						 */
						int wordCountToUse = RANDOM_GENERATOR.nextInt(2) + 2;
						for(int j = 0; j < wordCountToUse; j++) {
							String thisWordToo = words.get(RANDOM_GENERATOR.nextInt(words.size()));
							
							/*
							 * Now, do we mess with the case?
							 */
							int caseChangeTwo = RANDOM_GENERATOR.nextInt(100); 
							if(caseChangeTwo < 33) {
								thisWordToo = thisWordToo.toUpperCase();
							} else if(caseChangeTwo < 66) {
								thisWordToo = thisWordToo.toLowerCase();
							}
							wordsToUse.add(thisWordToo);
						}
					}
				}
				
				try {
					fw.write(createRandomLineContaining(wordsToUse, wordsInLine));
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(fw != null) {
					fw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("\nWrote random data to file " + filename +".");
	}
}
