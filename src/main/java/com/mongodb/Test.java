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
public class Test 
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

		/* Query query = new Query("seçim");
            query.setCount(1000);
            QueryResult result;
            result = twitter.search(query);
            System.out.println("Getting Tweets...");
            List<Status> tweets = result.getTweets();*/
			int pageno = 1;
			String user = "Cihan_Haber";
			List<Status> tweets = new ArrayList();
			
			while (true) {

				  try {

				    int size = tweets.size(); 
				    Paging page = new Paging(pageno++, 100);
				    tweets.addAll(twitter.getUserTimeline(user, page));
				    if (tweets.size() == size)
				      break;
				  }
				  catch(TwitterException e) {

				    e.printStackTrace();
				  }
				}

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
		
        
}
}