package com.expl0itz.worldwidechat.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.ChronoUnit;

import com.amazonaws.services.translate.model.InvalidRequestException;
import com.expl0itz.worldwidechat.WorldwideChat;
import com.expl0itz.worldwidechat.translators.AmazonTranslation;
import com.expl0itz.worldwidechat.translators.GoogleTranslation;
import com.expl0itz.worldwidechat.translators.TestTranslation;
import com.expl0itz.worldwidechat.translators.WatsonTranslation;
import com.google.cloud.translate.TranslateException;
import com.google.common.base.CharMatcher;
import com.ibm.cloud.sdk.core.service.exception.NotFoundException;

import fr.minuskube.inv.SmartInventory;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.ChatColor;

public class CommonDefinitions {
    
    /* Important vars */
    public static String[] supportedMCVersions = {
    	"1.17",
        "1.16",
        "1.15",
        "1.14"
    };
    
    public static String[] supportedPluginLangCodes = {
        "af",
        "sq",
        "am",
        "ar",
        "hy",
        "az",
        "bn",
        "bs",
        "bg",
        "ca",
        "zh",
        "zh-TW",
        "hr",
        "cs",
        "da",
        "fa-AF",
        "nl",
        "en",
        "et",
        "fa",
        "tl",
        "fi",
        "fr",
        "fr-CA",
        "ka",
        "de",
        "el",
        "gu",
        "ht",
        "ha",
        "he",
        "hi",
        "hu",
        "is",
        "id",
        "it",
        "ja",
        "kn",
        "kk",
        "ko",
        "lv",
        "lt",
        "mk",
        "ms",
        "ml",
        "mt",
        "mn",
        "no",
        "fa",
        "ps",
        "pl",
        "pt",
        "ro",
        "ru",
        "sr",
        "si",
        "sk",
        "sl",
        "so",
        "es",
        "es-MX",
        "sw",
        "sv",
        "tl",
        "ta",
        "te",
        "th",
        "tr",
        "uk",
        "ur",
        "uz",
        "vi",
        "cy"
    };

    /* Getters */ 
    public static boolean isSameLang(String first, String second) {
    	for (SupportedLanguageObject eaLang : WorldwideChat.getInstance().getSupportedTranslatorLanguages()) {
            if ((eaLang.getLangName().equals(getSupportedTranslatorLang(first).getLangName()) 
                && eaLang.getLangName().equals(getSupportedTranslatorLang(second).getLangName()))) {
                return true;
            }
        }
        return false;
    }
    
    public static SupportedLanguageObject getSupportedTranslatorLang(String in) {
        for (SupportedLanguageObject eaLang : WorldwideChat.getInstance().getSupportedTranslatorLanguages()) {
            if ((eaLang.getLangCode().equalsIgnoreCase(in)
                || eaLang.getLangName().equalsIgnoreCase(in))) {
                return eaLang;
            }
        }
        return null;
    }
    
    public static String getFormattedValidLangCodes() {
        String out = "\n";
        for (SupportedLanguageObject eaLang : WorldwideChat.getInstance().getSupportedTranslatorLanguages()) {
    		out += "(" + eaLang.getLangCode() + " - " + eaLang.getLangName() + "), ";
    	}
        if (out.indexOf(",") != -1) {
            out = out.substring(0, out.lastIndexOf(","));
        }
        return out;
    }
    
    public static void closeAllInventories() {
    	// Close all active GUIs
        WorldwideChat.getInstance().getPlayersUsingGUI().clear();
        for (Player eaPlayer : Bukkit.getOnlinePlayers()) {
        	try {
        		if (WorldwideChat.getInstance().getInventoryManager().getInventory(eaPlayer).get() instanceof SmartInventory && WorldwideChat.getInstance().getInventoryManager().getInventory(eaPlayer).get().getManager().equals(WorldwideChat.getInstance().getInventoryManager())) {
            		eaPlayer.closeInventory();
            	}
        	} catch (NoSuchElementException e) {
        		continue;
        	}
        }
    }
    
    public static void sendDebugMessage(String inMessage) {
    	if (WorldwideChat.getInstance().getDebugMode()) {
    		WorldwideChat.getInstance().getLogger().warning("DEBUG: " + inMessage);
    	}
    }
    
    public static String translateText(String inMessage, Player currPlayer) {
    	try {
    		/* If translator settings are invalid, do not do this... */
        	if (WorldwideChat.getInstance().getTranslatorName().equals("Invalid") || !(inMessage.length() > 0)) {
        		return inMessage;
        	}
        	
        	 /* Sanitize Inputs */
            //Warn user about color codes
            //EssentialsX chat and maybe others replace "&4Test" with " 4Test"
            //Therefore, we find the " #" regex or the "&" char, and warn the user about it
            boolean essentialsColorCodeWarning = false;
            Audience adventureSender = WorldwideChat.getInstance().adventure().sender(currPlayer);
            if (inMessage.matches(".*[0-9].*")) {
            	essentialsColorCodeWarning = true;
            }
            
            if (!(WorldwideChat.getInstance().getActiveTranslator("GLOBAL-TRANSLATE-ENABLED") instanceof ActiveTranslator) //don't do any of this if /wwcg is enabled; players may not be in ArrayList and this will throw an exception
                &&
                (essentialsColorCodeWarning || inMessage.indexOf("&") != -1) //check sent chat to make sure it includes a CC
                &&
                !(WorldwideChat.getInstance().getActiveTranslator(Bukkit.getServer().getPlayer(currPlayer.getName()).getUniqueId().toString()).getCCWarning())) //check if user has already been sent CC warning
            {
                final TextComponent watsonCCWarning = Component.text()
                    .append(WorldwideChat.getInstance().getPluginPrefix().asComponent())
                    .append(Component.text().content(WorldwideChat.getInstance().getConfigManager().getMessagesConfig().getString("Messages.watsonColorCodeWarning")).color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, true))
                    .build();
                adventureSender.sendMessage(watsonCCWarning);
                //Set got CC warning of current translator to true, so that they don't get spammed by it if they keep using CCs
                WorldwideChat.getInstance().getActiveTranslator(Bukkit.getServer().getPlayer(currPlayer.getName()).getUniqueId().toString()).setCCWarning(true);
                //we're still gonna translate it but it won't look pretty
            }
        	
        	/* Modify or create new player record */
            PlayerRecord currPlayerRecord = WorldwideChat.getInstance().getPlayerRecord(currPlayer.getUniqueId().toString(), true);
            currPlayerRecord.setAttemptedTranslations(currPlayerRecord.getAttemptedTranslations()+1);
        	
        	/* Initialize current ActiveTranslator, sanity checks */
            ActiveTranslator currActiveTranslator;
            if (!(WorldwideChat.getInstance().getActiveTranslator("GLOBAL-TRANSLATE-ENABLED") instanceof ActiveTranslator) && (WorldwideChat.getInstance().getActiveTranslator(currPlayer.getUniqueId().toString()) != null)) {
                currActiveTranslator = WorldwideChat.getInstance().getActiveTranslator(currPlayer.getUniqueId().toString());
            } else if ((WorldwideChat.getInstance().getActiveTranslator("GLOBAL-TRANSLATE-ENABLED") instanceof ActiveTranslator) && (WorldwideChat.getInstance().getActiveTranslator(currPlayer.getUniqueId().toString()) != null)){
                //global translation won't override per person
                currActiveTranslator = WorldwideChat.getInstance().getActiveTranslator(currPlayer.getUniqueId().toString()); 
            } else {
            	currActiveTranslator = WorldwideChat.getInstance().getActiveTranslator("GLOBAL-TRANSLATE-ENABLED");
            }
            
            /* Check cache */
            if (WorldwideChat.getInstance().getConfigManager().getMainConfig().getInt("Translator.translatorCacheSize") > 0) {
                //Check cache for inputs, since config says we should
            	List<CachedTranslation> currCache = WorldwideChat.getInstance().getCache();
            	synchronized (currCache) {
            		for (CachedTranslation currentTerm : currCache) {
            			if (currentTerm.getInputLang().equalsIgnoreCase(currActiveTranslator.getInLangCode())
                                && (currentTerm.getOutputLang().equalsIgnoreCase(currActiveTranslator.getOutLangCode()))
                                && (currentTerm.getInputPhrase().equalsIgnoreCase(inMessage))
                                ) {
                            currentTerm.setNumberOfTimes(currentTerm.getNumberOfTimes()+1);
                            // Update stats, return output
                            currPlayerRecord.setSuccessfulTranslations(currPlayerRecord.getSuccessfulTranslations()+1);    
                            currPlayerRecord.setLastTranslationTime();
                            return StringEscapeUtils.unescapeJava(ChatColor.translateAlternateColorCodes('&', currentTerm.getOutputPhrase())); //done :)
                        }
            		}
            	}
            }
            
            /* Rate limit check */
            try {
            	// Init vars
                boolean isExempt = false;
                boolean hasPermission = false;
                int personalRateLimit = 0;
                String permissionCheck = "";
            	
                // Get permission from Bukkit API synchronously, since we do not want to risk concurrency problems
                if (!WorldwideChat.getInstance().getTranslatorName().equals("JUnit/MockBukkit Testing Translator")) {
                	permissionCheck = Bukkit.getScheduler().callSyncMethod(WorldwideChat.getInstance(), () ->
        			{
        				return checkForRateLimitPermissions(currPlayer);
        			}).get();
                } else {
                	// It is extremely unlikely that we run into concurrency issues with MockBukkit.
                	// Until it supports callSyncMethod(), this will do.
                	permissionCheck = checkForRateLimitPermissions(currPlayer);
                }
    			
    			// If exempt, set exempt to true; else, get the delay from the end of the permission string
    			if (permissionCheck.equalsIgnoreCase("worldwidechat.ratelimit.exempt")) {
    	        	isExempt = true;
    	        } else {
    	        	String delayStr = CharMatcher.inRange('0', '9').retainFrom(permissionCheck);
    	        	if (!delayStr.isEmpty()) {
    	        		personalRateLimit = Integer.parseInt(delayStr);
    	        		hasPermission = true;
    	        	}
    	        }
    			
    			// Get user's personal rate limit, if permission is not set and they are an active translator.
    	        if (!isExempt && !hasPermission && WorldwideChat.getInstance().getActiveTranslator(currPlayer.getUniqueId().toString()) != null) {
    	        	personalRateLimit = WorldwideChat.getInstance().getActiveTranslator(currPlayer.getUniqueId().toString()).getRateLimit();
    	        }
    	        
    	        // Personal Limits (Override Global)
    	        if (!isExempt && personalRateLimit > 0) {
    	        	if (!(currActiveTranslator.getRateLimitPreviousTime().equals("None"))) {
    	        		Instant previous = Instant.parse(currActiveTranslator.getRateLimitPreviousTime());
    	        		Instant currTime = Instant.now();
    	        		if (currTime.compareTo(previous.plus(personalRateLimit, ChronoUnit.SECONDS)) < 0) {
    	        			        final TextComponent rateLimit = Component.text()
    	                                .append(WorldwideChat.getInstance().getPluginPrefix().asComponent())
    	                                .append(Component.text().content(WorldwideChat.getInstance().getConfigManager().getMessagesConfig().getString("Messages.wwcRateLimit")
    	                                		.replace("%i", "" + ChronoUnit.SECONDS.between(currTime, previous.plus(personalRateLimit, ChronoUnit.SECONDS)))).color(NamedTextColor.YELLOW))
    	                                .build();
    	                            WorldwideChat.getInstance().adventure().sender(currPlayer).sendMessage(rateLimit);
    	                            return inMessage;  	
    	        				} else {
    	        					currActiveTranslator.setRateLimitPreviousTime(Instant.now());
    	        				}
    	        	} else {
    	        		currActiveTranslator.setRateLimitPreviousTime(Instant.now());
    	        	}
    	        // Global Limits
    	        } else if (!isExempt && WorldwideChat.getInstance().getRateLimit() > 0) {
    	           if (!(currActiveTranslator.getRateLimitPreviousTime().equals("None"))) {
    	        		Instant previous = Instant.parse(currActiveTranslator.getRateLimitPreviousTime());
    	        		Instant currTime = Instant.now();
    	        		int globalLimit = WorldwideChat.getInstance().getRateLimit();
    	        		if (currTime.compareTo(previous.plus(globalLimit, ChronoUnit.SECONDS)) < 0) {
    	        			        final TextComponent rateLimit = Component.text()
    	                                .append(WorldwideChat.getInstance().getPluginPrefix().asComponent())
    	                                .append(Component.text().content(WorldwideChat.getInstance().getConfigManager().getMessagesConfig().getString("Messages.wwcRateLimit")
    	                                		.replace("%i", "" + ChronoUnit.SECONDS.between(currTime, previous.plus(globalLimit, ChronoUnit.SECONDS)))).color(NamedTextColor.YELLOW))
    	                                .build();
    	                            WorldwideChat.getInstance().adventure().sender(currPlayer).sendMessage(rateLimit);
    	                            return inMessage;  	
    	        				} else {
    	        					currActiveTranslator.setRateLimitPreviousTime(Instant.now());
    	        				}
    	        	} else {
    	        		currActiveTranslator.setRateLimitPreviousTime(Instant.now());
    	        	}	
    	        }
    		} catch (Exception e2) {
    			// Couldn't get user permissions: stop, drop, and roll.
    			e2.printStackTrace();
    			return inMessage;
    		}
            
        	/* Begin actual translation, set message to output */
            String out = "";
            if (WorldwideChat.getInstance().getTranslatorName().equals("Watson")) {
                try {
                    WatsonTranslation watsonInstance = new WatsonTranslation(inMessage,
                        currActiveTranslator.getInLangCode(),
                        currActiveTranslator.getOutLangCode(),
                        currPlayer);
                    //Get username + pass from config
                    out = watsonInstance.translate();
                } catch (NotFoundException lowConfidenceInAnswer) {
                    /* This exception happens if the Watson translator is auto-detecting the input language.
                     * By definition, the translator is unsure if the source language detected is accurate due to 
                     * confidence levels being below a certain threshold.
                     * Usually, either already translated input is given or occasionally a phrase is not fully translatable.
                     * This is where we catch that and send the player a message telling them that their message was unable to be
                     * parsed by the translator.
                     * You should be able to turn this off in the config.
                     */
                    final TextComponent lowConfidence = Component.text()
                        .append(WorldwideChat.getInstance().getPluginPrefix().asComponent())
                        .append(Component.text().content(WorldwideChat.getInstance().getConfigManager().getMessagesConfig().getString("Messages.watsonNotFoundExceptionNotification")).color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, true))
                        .build();
                    WorldwideChat.getInstance().adventure().sender(currPlayer).sendMessage(lowConfidence);
                    return inMessage;
                }
            } else if (WorldwideChat.getInstance().getTranslatorName().equals("Google Translate")) {
                try {
                    GoogleTranslation googleTranslateInstance = new GoogleTranslation(inMessage,
                        currActiveTranslator.getInLangCode(),
                        currActiveTranslator.getOutLangCode(),
                        currPlayer);
                    out = googleTranslateInstance.translate();
                } catch (TranslateException e) {
                    /* This exception happens for the same reason that Watson does: low confidence.
                     * Usually when a player tries to get around our same language translation block.
                     * Examples of when this triggers:
                     * .wwct en and typing in English.
                     */
                    final TextComponent lowConfidence = Component.text()
                        .append(WorldwideChat.getInstance().getPluginPrefix().asComponent())
                        .append(Component.text().content(WorldwideChat.getInstance().getConfigManager().getMessagesConfig().getString("Messages.watsonNotFoundExceptionNotification")).color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, true))
                        .build();
                    WorldwideChat.getInstance().adventure().sender(currPlayer).sendMessage(lowConfidence);
                    return inMessage;
                }
            } else if (WorldwideChat.getInstance().getTranslatorName().equals("Amazon Translate")) {
            	try {
            		AmazonTranslation amazonTranslateInstance = new AmazonTranslation(inMessage,
            			currActiveTranslator.getInLangCode(),
            			currActiveTranslator.getOutLangCode(),
            			currPlayer);
            		out = amazonTranslateInstance.translate();
            	} catch (InvalidRequestException e) {
            		/* Low confidence exception, Amazon Translate Edition */
            		final TextComponent lowConfidence = Component.text()
                            .append(WorldwideChat.getInstance().getPluginPrefix().asComponent())
                            .append(Component.text().content(WorldwideChat.getInstance().getConfigManager().getMessagesConfig().getString("Messages.watsonNotFoundExceptionNotification")).color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, true))
                            .build();
                    WorldwideChat.getInstance().adventure().sender(currPlayer).sendMessage(lowConfidence);
                    return inMessage;
            	}
            } else if (WorldwideChat.getInstance().getTranslatorName().equals("JUnit/MockBukkit Testing Translator")) {
            	TestTranslation testTranslator = new TestTranslation(inMessage,
        				currActiveTranslator.getInLangCode(),
        				currActiveTranslator.getOutLangCode(),
        				currPlayer);
            	out = testTranslator.translate();
            }
            
            /* Update stats */
            currPlayerRecord.setSuccessfulTranslations(currPlayerRecord.getSuccessfulTranslations()+1);    
            currPlayerRecord.setLastTranslationTime();
            
            /* Add to cache */
            if (WorldwideChat.getInstance().getConfigManager().getMainConfig().getInt("Translator.translatorCacheSize") > 0 && !(currActiveTranslator.getInLangCode().equals("None"))) {
                CachedTranslation newTerm = new CachedTranslation(currActiveTranslator.getInLangCode(), currActiveTranslator.getOutLangCode(), inMessage, out);   
                WorldwideChat.getInstance().addCacheTerm(newTerm);
            }
            return StringEscapeUtils.unescapeJava(ChatColor.translateAlternateColorCodes('&', out));
    	} catch (Exception e) {
    		/* Add 1 to error count */
    		WorldwideChat.getInstance().setErrorCount(WorldwideChat.getInstance().getErrorCount()+1);
    		final TextComponent playerError = Component.text()
                    .append(WorldwideChat.getInstance().getPluginPrefix().asComponent())
                    .append(Component.text().content(WorldwideChat.getInstance().getConfigManager().getMessagesConfig().getString("Messages.wwcTranslatorError")).color(NamedTextColor.RED))
                    .build();
            WorldwideChat.getInstance().adventure().sender(currPlayer).sendMessage(playerError);
            WorldwideChat.getInstance().getLogger().severe(WorldwideChat.getInstance().getConfigManager().getMessagesConfig().getString("Messages.wwcTranslatorErrorConsole").replace("%i", currPlayer.getName()));
            e.printStackTrace();
            
    		/* Write to log file */
            File errorLog = new File(WorldwideChat.getInstance().getDataFolder(), "errorLog.txt");
            try {
				FileWriter fw = new FileWriter(errorLog, true);
				LocalDate date = LocalDate.now();
				LocalTime time = LocalTime.now();
				String dateStr = date.format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy - ")) + time.format(DateTimeFormatter.ofPattern("hh:mm:ss a"));
				
				fw.write("========== " + dateStr + " ==========");
				fw.write(System.getProperty("line.separator"));
				fw.write(ExceptionUtils.getStackTrace(e));
				fw.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
    		
            /* If error count is greater than threshold set in config.yml, reload */
    		if (WorldwideChat.getInstance().getErrorCount() >= WorldwideChat.getInstance().getErrorLimit()) {
        		WorldwideChat.getInstance().getLogger().severe(WorldwideChat.getInstance().getConfigManager().getMessagesConfig().getString("Messages.wwcTranslatorErrorThresholdReached"));
        		WorldwideChat.getInstance().getLogger().severe(WorldwideChat.getInstance().getConfigManager().getMessagesConfig().getString("Messages.wwcTranslatorErrorThresholdReachedCheckLogs"));
        		WorldwideChat.getInstance().getConfigManager().getMainConfig().set("Translator.useWatsonTranslate", false);
				WorldwideChat.getInstance().getConfigManager().getMainConfig().set("Translator.useGoogleTranslate", false);
				WorldwideChat.getInstance().getConfigManager().getMainConfig().set("Translator.useAmazonTranslate", false);
				try {
					WorldwideChat.getInstance().getConfigManager().getMainConfig().save(WorldwideChat.getInstance().getConfigManager().getConfigFile());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
        		WorldwideChat.getInstance().reload();
    		}
    		return inMessage;
    	}
    }
    
    private static String checkForRateLimitPermissions(Player currPlayer) {
    	Set<PermissionAttachmentInfo> perms = currPlayer.getEffectivePermissions();
		for (PermissionAttachmentInfo perm : perms) {
			if (perm.getPermission().startsWith("worldwidechat.ratelimit.")) {
				//DEBUG
				//WorldwideChat.getInstance().getLogger().info(perm.getPermission());
				return perm.getPermission();
			}
		}
		return "";
    }
}