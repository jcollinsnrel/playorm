package com.alvazan.orm.api.base;

import java.util.List;

import com.alvazan.orm.api.spi.layer2.NoSqlSession;
import com.alvazan.orm.layer1.base.BaseEntityManagerImpl;
import com.google.inject.ImplementedBy;

@ImplementedBy(BaseEntityManagerImpl.class)
public interface NoSqlEntityManager {

	/**
	 * Retrieve underlying interface to write raw columns to.  This works the same as the NoSqlEntityManager
	 * in that you must call flush to execute all the calls to persist.
	 * 
	 * @return 
	 */
	public NoSqlSession getSession();

	//public String generateUniqueKey();
	
	public void put(Object entity);
	
	public void putAll(List<Object> entities);
	
	public <T> T find(Class<T> entityType, Object key);
	
	public <T> List<KeyValue<T>> findAll(Class<T> entityType, List<Object> keys);
	
	public <T> T getReference(Class<T> entityType, Object key);
	
	/**
	 * Mainly for framework code but a nice way to get the key of an unknown entity
	 * where you don't care about the entity but just need the key
	 * @param entity
	 * @return
	 */
	public Object getKey(Object entity);
	
	/**
	 * Unlike RDBMS, there are no transactions, BUT all the calls to putAll are cached
	 * in-memory into flush is called.  This allows us to easily queue up all writes to
	 * the datastore and time how long all the writes take.  It is also a bit more likely
	 * to keep things more consistent
	 */
	public void flush();
	
	/**
	 * Best explained with an example.  Let's say you have a table with 1 billion rows
	 * and let's say you have 1 million customers each with on avera 1000 rows in that
	 * table(ie. total 1 billion).  In that case, it would be good to create 1 million
	 * indexes with 1000 nodes in them each rather than one 1 billion node index as it
	 * would be 1000's of times faster to fetch the small index and query it.  Pretend
	 * the entity representing this 1 billion row table was ActionsTaken.class, then to
	 * get the index for all rows relating to a user that you can then query you would
	 * call entityMgr.getIndex(ActionsTaken.class, "/byUser/"+user.getId());
	 * 
	 * Going on, let's say that same 1 billion activity table is also related to 500k
	 * commentors.  You may have another 500k indexes for querying when you have the 
	 * one commentor and want to search on other stuff so you would get the index to
	 * query like so entityMgr.getIndex(ActionsTaken.class, "/byCommentor/"+commentor.getId());
	 * 
	 * In this methodology you are just breaking up HUGE tables into small subtables that
	 * can be queried.
	 * 
	 * @param forEntity
	 * @param indexName
	 * @return
	 */
	public <T> Index<T> getIndex(Class<T> forEntity, String indexName);
	
}