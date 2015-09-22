package com.aol.opstest;

import java.io.File;

import com.aol.opstest.Exception.OpsTestException;
import com.aol.opstest.service.NameOccuranceMappingService;
import com.aol.opstest.util.TestUtils;

public class OpsTest {
	private static final String file1Arg		= "-f1";	// FILE1 Argument
	private static final String file2Arg		= "-f2";	// FILE2 Argument
	private static final String file3Arg		= "-N";		// Name File Argument
	private static final String fileSizeArg		= "-S";		// File Generated Size Argument
	private static final String caseArg			= "-c";		// Case Sensitive Argument
	private static final String helpArg 		= "-h";		// Help Argument
	
	private static File file1Value				= null;		// FILE1 Argument Value
	private static File file2Value				= null;		// FILE2 Argument Value
	private static File file3Value				= null;		// Name File Argument Value
	private static int fileSizeValue			= 100;		// Generated Size Value
	private static boolean caseValue			= false;	// Case Sensitive value
	
	private static final int MAX_LINES 			= 10000;	// max size of generated file (in lines)
	
	public static void main(String[] args) {
		// Retrieve all CLI arguments
		if (!getArgs(args)) {
			showHelp();
			System.exit(0);
		}
		
		if(file3Value != null) {
			System.out.println("Generating random data file using words in [" +
								file3Value.getName() + "] and a size of [" +
								fileSizeValue + "] lines.");
			
			TestUtils.createRandomDataFile(file3Value, fileSizeValue);
		} else {
			NameOccuranceMappingService nfm = new NameOccuranceMappingService(file1Value, file2Value, caseValue);
			try {
				nfm.processFiles();
			} catch (OpsTestException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Parse CLI Arguments
	 * 
	 * @param args - main arguments
	 * @return boolean - if arguments were passed correctly
	 */
	private static boolean getArgs(String[] args) {
		boolean gotFile1 = false;
		boolean gotFile2 = false;
		boolean gotFile3 = false;

		for (int i = 0; i < args.length; i++) {			
			if (args[i].equals(file1Arg)) {
				if ((i + 1) >= args.length) {
					return false;
				}

				if (args[i + 1] != null) {
					// Stat the file
					String file = args[i + 1];
					File f = new File(file);
					if (f.exists()) {
						file1Value = f;
						gotFile1 = true;
					} else {
						System.out.println("\n\nError: The file1 [" + file + "] does not exist.\n\n");
						return false;
					}
				}
			}
			
			if (args[i].equals(file2Arg)) {
				if ((i + 1) >= args.length) {
					return false;
				}

				if (args[i + 1] != null) {
					// Stat the file
					String file = args[i + 1];
					File f = new File(file);
					if (f.exists()) {
						file2Value = f;
						gotFile2 = true;
					} else {
						System.out.println("\n\nError: The file2 [" + file + "] does not exist.\n\n");
						return false;
					}
				}
			}
			
			if (args[i].equals(file3Arg)) {
				if ((i + 1) >= args.length) {
					return false;
				}

				if (args[i + 1] != null) {
					// Stat the file
					String file = args[i + 1];
					File f = new File(file);
					if (f.exists()) {
						file3Value = f;
						gotFile3 = true;
					} else {
						System.out.println("\n\nError: The file2 [" + file + "] does not exist.\n\n");
						return false;
					}
				}
			}
			
			if (args[i].equals(fileSizeArg)) {
				if ((i + 1) >= args.length) {
					return false;
				}

				if (args[i + 1] != null) {
					try {
						fileSizeValue = Integer.parseInt(args[i + 1]);
					} catch (NumberFormatException e) {
						System.out.println("\n\nError: The value used for " + fileSizeArg +
											" is not a number.");
						return false;
					}
					
					if(fileSizeValue > MAX_LINES) {
						System.out.println("\n\nError: The value used for " + fileSizeArg +
								" is greater than value allowed [" + MAX_LINES + "].");
						return false;
					}
				}
			}
			
			if (args[i].equals(caseArg)) {
				caseValue = true;
			}

			if (args[i].equals(helpArg)) {
				return false;
			}
		}
		
		if((gotFile3) || (gotFile1 && gotFile2)) {
			return true;
		}

		return false;
	}

	/**
	 * Show CLI Help
	 * 
	 * @param
	 * @return
	 */
	private static void showHelp() {
        String help = "\n   OpsTest v1.0.0\n"
                        + "   Copyright (c) 2015 Aol, Inc.\n\n"
                        + "     This program compares two name files and outputs\n"
                        + "     the line numbers in FILE1 that contain the names\n"
                        + "     listed in FILE2.\n"
                        + "\n\n"
                        + "   Usage:\n\n"
                        + "      java -jar OpsTest.jar [-f1 FILE1] [-f2 FILE2] [-v] [-h]\n\n"
                        + "      -f1 FILE1      :   File containing name data\n"
                        + "      -f2 FILE2      :   File containing names (one per line)\n"
                        + "      -c             :   Match names from FILE2 with case sensitivity.\n"
                        + "                         This means the name \"Bob\" does not\n"
                        + "                         match a line containing \"bOb\".\n"
                        + "                         Default is FALSE.\n"
                        + "      -N  FILE3      :   Using FILE3, create a randomly generated\n"
                        + "                         data file.  FILE3 contains a list of names\n"
                        + "                         that will sometimes be present in the contents\n"
                        + "                         of a line in the file.\n"
                        + "                         If -N is present, it overrides any other\n"
                        + "                         processing.\n"
                        + "      -S  count      :   The number of lines to write in a generated data\n"
                        + "                         file.\n"
                        + "                         Default = 100.\n"
                        + "                         Only applicable when -N is used.\n"
                        + "      -h             :   This help document.\n\n"
                        + "       --------------------------------------\n"
                        + "\n\n   --------------------------------------------------------------------------\n\n"
                        + "   Example:\n\n"
                        + "   >java -jar OpsTest.jar -f1 file1.txt -f2 file2.txt\n\n";

        System.out.println(help);

        return;
	}	
}
