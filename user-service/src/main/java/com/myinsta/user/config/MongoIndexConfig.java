package com.myinsta.user.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexOperations;

/**
 * @author rima.devwork@gmail.com 
 */
@Configuration
public class MongoIndexConfig {

	private static final Logger logger = LoggerFactory.getLogger(MongoIndexConfig.class);
	
	private final MongoTemplate mongoTemplate;
	
	public MongoIndexConfig(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
	
	public void initIndexes() {
        String collectionName = "users";

        // Check if the collection exists; create it if it doesn't
        if (!mongoTemplate.collectionExists(collectionName)) {
            logger.debug("Creating Collection - " + collectionName);
            mongoTemplate.createCollection(collectionName);
        }

        // Create a unique index on the "uuid" field
        IndexOperations indexOps = mongoTemplate.indexOps(collectionName);
        indexOps.ensureIndex(
            new org.springframework.data.mongodb.core.index.Index()
                .on("uuid", org.springframework.data.domain.Sort.Direction.ASC)
                .unique()
        );

        logger.debug("Index on 'uuid' created or already exists.");
    }
	
}
