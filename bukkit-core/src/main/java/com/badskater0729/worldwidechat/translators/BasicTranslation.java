package com.badskater0729.worldwidechat.translators;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

import com.badskater0729.worldwidechat.WorldwideChat;

public class BasicTranslation {
	
	public WorldwideChat main = WorldwideChat.instance;
	
	public String textToTranslate;
	public String inputLang;
	public String outputLang;

	public ExecutorService callbackExecutor;
	public boolean isInitializing;
	
	public BasicTranslation(String textToTranslate, String inputLang, String outputLang, ExecutorService callbackExecutor) {
		isInitializing = false;
		this.textToTranslate = textToTranslate;
		this.inputLang = inputLang;
		this.outputLang = outputLang;
		this.callbackExecutor = callbackExecutor;
	}

	public BasicTranslation(boolean isInitializing, ExecutorService callbackExecutor) {
		this.callbackExecutor = callbackExecutor;
		this.isInitializing = isInitializing;
	}
	
	public String useTranslator() throws TimeoutException, ExecutionException, InterruptedException {
		return textToTranslate;
	}
}