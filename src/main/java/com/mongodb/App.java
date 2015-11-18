package com.mongodb;

import java.util.Scanner;

public class App 
{
	private static String collectionName;
	private static String serverName;
	private static String databaseName;

	private static int socketNumber;


	public static void main( String[] args ) throws Exception
	{
		serverName = "localhost";
		socketNumber = 27017;
		databaseName = "TwitterDatabase";
		Scanner reader = new Scanner(System.in);  
		System.out.println("Enter a query: ");
		collectionName = reader.nextLine();
		new PullTwitterData().fetchTwitterData(collectionName, serverName, socketNumber, databaseName);	 
		new RealTimeStreaming(collectionName, serverName, socketNumber, databaseName).realTime();;
		reader.close();

	}




}