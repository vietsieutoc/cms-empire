package com.yuri.empire.database;


import com.mongodb.DB;
import com.mongodb.MongoClient;

import java.net.UnknownHostException;

public class DataRepository {

//    private static final String HOST = "45.119.86.200";
    private static final String HOST = "localhost";
    private static final int PORT = 27017;

    public static DB masterDatabase() throws UnknownHostException {
        MongoClient mongoClient = new MongoClient(HOST, PORT);
        return mongoClient.getDB("master_empireLands_1_0");
    }

    public static DB settingDatabase() throws UnknownHostException {
        MongoClient mongoClient = new MongoClient(HOST, PORT);
        return mongoClient.getDB("setting_empireLands");
    }
}
