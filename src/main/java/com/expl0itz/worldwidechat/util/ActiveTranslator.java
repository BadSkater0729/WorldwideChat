package com.expl0itz.worldwidechat.util;

import org.threeten.bp.Instant;

public class ActiveTranslator {

	private int rateLimit = 0;

	private String playerUUID = "";
	private String inLangCode = "";
	private String outLangCode = "";
	private String rateLimitPreviousTime = "None";

	private boolean hasBeenShownColorCodeWarning = false;
	private boolean translatingBook = false;
	private boolean translatingSign = false;
	private boolean translatingItem = false;

	public ActiveTranslator(String uuid, String langIn, String langOut, boolean hasBeenShownColorCodeWarning) {
		playerUUID = uuid;
		inLangCode = langIn;
		outLangCode = langOut;
		this.hasBeenShownColorCodeWarning = hasBeenShownColorCodeWarning;
	}

	/* Setters */
	public void setRateLimit(int i) {
		rateLimit = i;
	}

	public void setUUID(String i) {
		playerUUID = i;
	}

	public void setInLangCode(String i) {
		inLangCode = i;
	}

	public void setOutLangCode(String i) {
		outLangCode = i;
	}

	public void setCCWarning(boolean i) {
		hasBeenShownColorCodeWarning = i;
	}

	public void setTranslatingBook(boolean i) {
		translatingBook = i;
	}

	public void setTranslatingSign(boolean i) {
		translatingSign = i;
	}

	public void setTranslatingItem(boolean i) {
		translatingItem = i;
	}

	public void setRateLimitPreviousTime(Instant i) {
		rateLimitPreviousTime = i.toString();
	}

	/* Getters */
	public int getRateLimit() {
		return rateLimit;
	}

	public String getUUID() {
		return playerUUID;
	}

	public String getInLangCode() {
		return inLangCode;
	}

	public String getOutLangCode() {
		return outLangCode;
	}

	public boolean getCCWarning() {
		return hasBeenShownColorCodeWarning;
	}

	public boolean getTranslatingBook() {
		return translatingBook;
	}

	public boolean getTranslatingSign() {
		return translatingSign;
	}

	public boolean getTranslatingItem() {
		return translatingItem;
	}

	public String getRateLimitPreviousTime() {
		return rateLimitPreviousTime;
	}
}