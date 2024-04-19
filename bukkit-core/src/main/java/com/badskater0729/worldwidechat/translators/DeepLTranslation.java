package com.badskater0729.worldwidechat.translators;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.badskater0729.worldwidechat.util.CommonRefs;
import org.apache.commons.lang3.StringUtils;

import com.badskater0729.worldwidechat.WorldwideChat;
import com.badskater0729.worldwidechat.util.SupportedLang;
import com.deepl.api.Language;
import com.deepl.api.TextResult;
import com.deepl.api.Translator;

public class DeepLTranslation extends BasicTranslation {

	public DeepLTranslation(String textToTranslate, String inputLang, String outputLang, ExecutorService callbackExecutor) {
		super(textToTranslate, inputLang, outputLang, callbackExecutor);
	}
	
	public DeepLTranslation(String apikey, boolean isInitializing, ExecutorService callbackExecutor) {
		super(isInitializing, callbackExecutor);
		System.setProperty("DEEPL_API_KEY", apikey);
	}

	@Override
	public String useTranslator() throws TimeoutException, ExecutionException, InterruptedException {
		Future<String> process = callbackExecutor.submit(new translationTask());
		String finalOut = "";
		
		/* Get translation */
		finalOut = process.get(WorldwideChat.translatorConnectionTimeoutSeconds, TimeUnit.SECONDS);
		
		return finalOut;
	}

	private class translationTask implements Callable<String> {
		CommonRefs refs = main.getServerFactory().getCommonRefs();
		@Override
		public String call() throws Exception {
			/* Initialize translation object again */
			Translator translate = new Translator(System.getProperty("DEEPL_API_KEY"));

			if (isInitializing) {
				/* Parse Supported Languages */
				List<SupportedLang> sourceLangs = new ArrayList<SupportedLang>();
				for (Language eaLang : translate.getSourceLanguages()) {
					sourceLangs.add(new SupportedLang(eaLang.getCode(), StringUtils.deleteWhitespace(eaLang.getName())));
				}
				List<SupportedLang> targetLangs = new ArrayList<SupportedLang>();
				for (Language eaLang : translate.getTargetLanguages()) {
					targetLangs.add(new SupportedLang(eaLang.getCode(), StringUtils.deleteWhitespace(eaLang.getName())));
				}

				/* Set languages list */
				main.setOutputLangs(refs.fixLangNames(targetLangs, true));
				main.setInputLangs(refs.fixLangNames(sourceLangs, true));

				/* Setup test translation */
				inputLang = "en";
				outputLang = "es";
				textToTranslate = "How are you?";
			}
			
			/* Get language code of current input/output language. 
			 * APIs generally recognize language codes (en, es, etc.)
			 * instead of full names (English, Spanish) */
			if (!isInitializing) {
				if (!inputLang.equals("None")) {
					inputLang = refs.getSupportedLang(inputLang, "in").getLangCode();
				}
				outputLang = refs.getSupportedLang(outputLang, "out").getLangCode();
			}

			/* If inputLang set to None, set as null for translateText() */
			if (inputLang.equals("None")) { // if we do not know the input
				inputLang = null;
			}

			/* Actual translation */
			TextResult result = translate.translateText(textToTranslate, inputLang,
					outputLang);
			return result.getText();
		}
	}
	
}
