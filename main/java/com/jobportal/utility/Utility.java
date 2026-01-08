package com.jobportal.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.jobportal.entity.Sequence;
import com.jobportal.exception.JobPortalException;

@Component
public class Utility {

	private static MongoOperations mongoOperations;

	@Autowired
	public void setMongoOperations(MongoOperations mongoOperations) {
		Utility.mongoOperations = mongoOperations;
	}

	public static Long getNextSequence(String key) {
		Query query = new Query(Criteria.where("_id").is(key));
		Update update = new Update().inc("sequence", 1);
		FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true).upsert(true);

		Sequence sequence = mongoOperations.findAndModify(query, update, options, Sequence.class);

		if (sequence == null) {
			throw new JobPortalException("Unable to get sequence ID for key: " + key);
		}

		return sequence.getSequence();
	}
}

