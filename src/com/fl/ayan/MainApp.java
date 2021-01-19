package com.fl.ayan;

import com.fl.ayan.predictor.LanguagePredictor;
import java.io.File;

public class MainApp {

	public static void main(String[] args) {

		try {
			final String localFolderPath = args[0];
			if(localFolderPath == null || localFolderPath.length()<1){
				System.out.println("Path cannot be null!!");
				return;
			}

			final File localDirectory = new File(localFolderPath);
			if(!localDirectory.isDirectory()){
				System.out.println("Path doesn't point to a valid directory!!");
				return;
			}

			int n = 2;
			String secondArg = null;
			if ( (args.length>1) && ( (secondArg=args[1]) != null ) && (secondArg.length() > 0) ) {
				n = Integer.parseInt(secondArg);
			}

			final LanguagePredictor predictor = new LanguagePredictor();
			final String language = predictor.detectClosestLanguage(localDirectory, n);
			System.out.println("The most suited Language is: " + language);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}