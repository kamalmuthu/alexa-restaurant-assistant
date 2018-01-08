package com.apassistant.alexa;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.*;
import com.amazonaws.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Restaurant Assistant's VUI flow manager
 */
public class RAManager {

    private static final Logger log = LoggerFactory.getLogger(RAManager.class);

    private static final String FOOD_TYPE_SLOT = "foodType";
    private static final String PRICE_SLOT = "price";
    private static final String RATING_SLOT = "rating";

    YelpProxyService ypProxy = new YelpProxyService();

    /**
     * Creates an initial launch response based on user invocation style
     * @param request
     * @param session
     * @return
     */
    public SpeechletResponse getLaunchResponse(LaunchRequest request, Session session) {
        String speechText =  "I can help you find a restaurant. What type of food are you thinking?";
        String repromptText = "You can tell me something like 'Find me a good pizza place'.";

        return getAskSpeechletResponse(speechText, repromptText);
    }

    public SpeechletResponse getFindRestaurantResponse(Intent intent, Session session) {
        String foodType = intent.getSlot(FOOD_TYPE_SLOT).getValue();
        String price = intent.getSlot(PRICE_SLOT).getValue();
        String rating = intent.getSlot(RATING_SLOT).getValue();

        String speechText;

        if(StringUtils.isNullOrEmpty(foodType)) {
            foodType =  (String) session.getAttributes().get(FOOD_TYPE_SLOT);
        }

        // Need at least the food type
        if(StringUtils.isNullOrEmpty(foodType)) {
            speechText = "I would need to know the type of food you want to search for restaurants";
            return getAskSpeechletResponse(speechText, speechText);
        } else {
            session.getAttributes().put(FOOD_TYPE_SLOT, foodType);
        }

        PriceEnum priceEnum = PriceEnum.AVERAGE;
        if(!StringUtils.isNullOrEmpty(price)) {
            priceEnum = PriceEnum.mapStringToPrice(price);
        }
        session.getAttributes().put(PRICE_SLOT, priceEnum.getPriceTxt());

        RatingsEnum ratingsEnum = RatingsEnum.HIGH;
        if(!StringUtils.isNullOrEmpty(rating)) {
            ratingsEnum = RatingsEnum.mapStringToRatings(rating);
        }
        session.getAttributes().put(RATING_SLOT, ratingsEnum.getStars());

        speechText = "<speak>Got it. I will look for restaurants that serve " + foodType + ", has a yelp rating of " + RatingsEnum.mapStringToRatings(rating).getStars() + " or more " +
                " and is priced " + PriceEnum.mapStringToPrice(price).getPriceTxt() + ".<break time=\"1s\"/>";
        speechText += "Just say sure or start over.</speak>";
        return getAskSsmlSpeechletResponse(speechText, speechText);
    }


    public SpeechletResponse getRefineSearchResponse(Intent intent, Session session) {
        String foodType = intent.getSlot(FOOD_TYPE_SLOT).getValue();
        String price = intent.getSlot(PRICE_SLOT).getValue();
        String rating = intent.getSlot(RATING_SLOT).getValue();

        // if foodType is available, fire yelp search to get a list of restaurants
        if(!StringUtils.isNullOrEmpty(foodType)) {
            session.getAttributes().put(FOOD_TYPE_SLOT, foodType);
        } else {
            foodType = (String) session.getAttribute(FOOD_TYPE_SLOT);
        }

        PriceEnum priceEnum;
        if(!StringUtils.isNullOrEmpty(price)) {
            priceEnum = PriceEnum.mapStringToPrice(price);
        } else {
            price = (String) session.getAttributes().get(PRICE_SLOT);
            priceEnum = PriceEnum.mapStringToPrice(price);
        }
        session.getAttributes().put(PRICE_SLOT, priceEnum.getPriceTxt());


        RatingsEnum ratingsEnum;
        if(!StringUtils.isNullOrEmpty((rating))) {
            ratingsEnum = RatingsEnum.mapStringToRatings(rating);
        } else {
            rating = (String) session.getAttributes().get(RATING_SLOT);
            ratingsEnum = RatingsEnum.mapStringToRatings(rating);
        }
        session.getAttributes().put(RATING_SLOT, ratingsEnum.getStars());

        String speechText = "<speak>Got it. I will look for restaurants that serve " + foodType + ", has a yelp rating of " + ratingsEnum.getStars() + " or more " +
                " and is priced " + priceEnum.getPriceTxt() + ".<break time=\"500ms\"/> ";
        speechText += "Just say sure or start over.</speak>";

        return getAskSsmlSpeechletResponse(speechText, speechText);
    }

    /**
     * Starrt over intent. Clear session and start from the beginning
     *
     * @param intent
     * @param session
     * @return
     */
    public SpeechletResponse getStartOverIntent(Intent intent, Session session) {
        clearSession(session);
        String speechText = "Okay, let's start again. Tell me what type of food you were thinking of. You can say something like burgers or chinese.";
        return getAskSpeechletResponse(speechText, speechText);
    }

    /**
     * Creates a {@code SpeechletResponse} for the help intent.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    public SpeechletResponse getHelpResponse() {
        String speechText = "I can help you find a restaurant. Just tell me what type of food you like.";
        return getAskSpeechletResponse(speechText, speechText);
    }

    /**
     * Creates a {@code SpeechletResponse} for cancel or stop intent
     * @return
     */
    public SpeechletResponse getCancelResponse(Intent intent, Session session) {
        clearSession(session);
        String speechText = "Okay. Have a nice day!";
        return getAskSpeechletResponse(speechText, speechText);
    }


    /**
     * Returns an ask Speechlet response for a speech and reprompt text.
     *
     * @param speechText
     *            Text for speech output
     * @param repromptText
     *            Text for reprompt output
     * @return ask Speechlet response for a speech and reprompt text
     */
    private SpeechletResponse getAskSpeechletResponse(String speechText, String repromptText) {
        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("Session");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        // Create reprompt
        PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
        repromptSpeech.setText(repromptText);
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(repromptSpeech);

        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }

    /**
     * Wrapper for creating the Ask response from the input strings using SSML
     *
     * @param stringOutput the output to be spoken
     * @param repromptText the reprompt for if the user doesn't reply or is misunderstood.
     * @return SpeechletResponse the speechlet response
     */
    private SpeechletResponse getAskSsmlSpeechletResponse(String stringOutput,
                                                          String repromptText) {
        OutputSpeech outputSpeech, repromptOutputSpeech;

        outputSpeech = new SsmlOutputSpeech();
        ((SsmlOutputSpeech) outputSpeech).setSsml(stringOutput);

        repromptOutputSpeech = new SsmlOutputSpeech();
        ((SsmlOutputSpeech) repromptOutputSpeech).setSsml(repromptText);

        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(repromptOutputSpeech);
        return SpeechletResponse.newAskResponse(outputSpeech, reprompt);
    }


    private void clearSession(Session session) {
        session.getAttributes().put(FOOD_TYPE_SLOT, null);
        session.getAttributes().put(PRICE_SLOT, PriceEnum.AVERAGE);
        session.getAttributes().put(RATING_SLOT, RatingsEnum.HIGH);
    }

    /**
     * When user says yes, fire the search and return search results
     * TODO: use a state machine through Session to ensure you are in the right state before firing the search
     *
     * @param intent
     * @param session
     * @return
     */
    public SpeechletResponse getYesIntent(Intent intent, Session session) {
        // Check to see if you have all the required values
        String foodType = (String) session.getAttributes().get(FOOD_TYPE_SLOT);
        String price = (String) session.getAttributes().get(PRICE_SLOT);
        String rating = (String) session.getAttributes().get(RATING_SLOT);

        // If we don't have any of these then return to collecting search filters
        if(StringUtils.isNullOrEmpty(foodType) || StringUtils.isNullOrEmpty(price) || StringUtils.isNullOrEmpty(rating)) {
            return getFindRestaurantResponse(intent, session);
        }

        List<RARestaurant> raRestaurants = ypProxy.findRestaurants(foodType, PriceEnum.mapStringToPrice(price), RatingsEnum.mapStringToRatings(rating));

        StringBuilder speechText = new StringBuilder();
        speechText.append("<speak>I will list top five based on reviews <break time=\"500ms\"/>" );

        for(RARestaurant ra : raRestaurants) {
            speechText.append(ra.getName()).append(" with ").append(ra.getRating()).append(".<break time=\"350ms\"/>");
        }
        speechText.append("To get more details on any of these just say, 'More details for Restaurant'.</speak>");

        log.info("Responding Yes:" + speechText.toString());
        return getAskSsmlSpeechletResponse(speechText.toString(), speechText.toString());
    }
}
