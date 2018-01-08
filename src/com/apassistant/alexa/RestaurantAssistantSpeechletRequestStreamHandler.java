package com.apassistant.alexa;

import java.util.HashSet;
import java.util.Set;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class could be the handler for an AWS Lambda function powering an Alexa Skills Kit
 * experience. To do this, simply set the handler field in the AWS Lambda console to
 * "ScoreKeeperSpeechletRequestStreamHandler" For this to work, you'll also need to
 * build this project using the {@code lambda-compile} Ant task and upload the resulting zip file to
 * power your function.
 */
public final class RestaurantAssistantSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {
    private static final Logger log = LoggerFactory.getLogger(RestaurantAssistantSpeechletRequestStreamHandler.class);
    private static final Set<String> supportedApplicationIds;

    static {
        /*
         * This Id can be found on https://developer.amazon.com/edw/home.html#/ "Edit" the relevant
         * Alexa Skill and put the relevant Application Ids in this Set.
         */
        supportedApplicationIds = new HashSet<String>();
        supportedApplicationIds.add("amzn1.ask.skill.da95c31f-419c-4515-8804-b57063a8baff");
    }

    public RestaurantAssistantSpeechletRequestStreamHandler() {
        super(new RestaurantAssistantSpeechlet(), supportedApplicationIds);
        log.info("Launch handler");
    }
}
