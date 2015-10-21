package com.mongodb;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.UserMentionEntity;
import twitter4j.conf.ConfigurationBuilder;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
        MongoDatabase db = mongoClient.getDatabase("TwitterDatabase");
        MongoCollection<Document> coll=db.getCollection("tweets");
        //coll.dropCollection();
        //Document smith=new Document ("name","Smith");
        //coll.insertOne(smith);
        
        //------------------------------------------------------------------------------
        
        ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey("eFI1glCg94lhqQvqeRirwjwa9")
		  .setOAuthConsumerSecret("6dqIrS46t9jI2b73Kg7uSXxFu1zetpRgJPqOtvXe5oel6xKv7k")
		  .setOAuthAccessToken("783207474-dqzwWrDnoGXq7ya1QTqjmPwFMrKyeIin0OVagp5z")
		  .setOAuthAccessTokenSecret("MJJFYg6wti3x2WjTTOTJ9A5HsEavjwi8WAwXt54v4JFti");
		
		Twitter twitter = new TwitterFactory(cb.build()).getInstance();

		try {
            Query query = new Query("#sözcü");
            //query.setCount(100);
            QueryResult result;
            result = twitter.search(query);
            System.out.println("Getting Tweets...");
            List<Status> tweets = result.getTweets();

            for (Status tweet : tweets) {
                Document basicObj = new Document();
                basicObj.put("user_name", tweet.getUser().getScreenName());
                basicObj.put("retweet_count", tweet.getRetweetCount());
                basicObj.put("tweet_followers_count", tweet.getUser().getFollowersCount());
                basicObj.put("source",tweet.getSource());
                basicObj.put("coordinates",tweet.getGeoLocation());

                UserMentionEntity[] mentioned = tweet.getUserMentionEntities();
                basicObj.put("tweet_mentioned_count", mentioned.length);
                basicObj.put("tweet_ID", tweet.getId());
                basicObj.put("tweet_text", tweet.getText());
                System.out.println(basicObj.toString());
                try {
                    coll.insertOne(basicObj);
                    
                } catch (Exception e) {
                    System.out.println("MongoDB Connection Error : " + e.getMessage());
                    //loadMenu();
                }
            }
            System.out.println("We got'em");

        } catch (TwitterException te) {
            System.out.println("te.getErrorCode() " + te.getErrorCode());
            System.out.println("te.getExceptionCode() " + te.getExceptionCode());
            System.out.println("te.getStatusCode() " + te.getStatusCode());
            if (te.getStatusCode() == 401) {
                System.out.println("Twitter Error : nAuthentication credentials (https://dev.twitter.com/pages/auth) were missing or incorrect.nEnsure that you have set valid consumer key/secret, access token/secret, and the system clock is in sync.");
            } else {
                System.out.println("Twitter Error : " + te.getMessage());
            }

        }	       
		
        
}
}