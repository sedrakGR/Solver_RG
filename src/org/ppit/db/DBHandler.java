package org.ppit.db;

import java.net.UnknownHostException;

import org.ppit.util.ConfigManager;
import org.ppit.util.Definitions;

import com.mongodb.DB;
import com.mongodb.Mongo;
/**
 * 
 * @author SedrakG
 * @brief MongoOpener, this is the class
 * to connect to DB and disconnect from it  
 */
public class  DBHandler {
	/**
	 * @brief static m_DB, DB to be used 
	 */
	private static DB m_DB;
	/**
	 * @brief static Mongo m_mongo, this is get the DB (it is mongo) we need
	 */
	private static Mongo m_mongo;
	
	/**
	 * instance of handler
	 */
	private static DBHandler m_instance = null;
	
	/**
	 * private constructor
	 */
	private DBHandler() {
		// if the DBLoadingMode is "concepts", then loading main DB
		// else loading test DB (in the case test is enabled)
		String DBLoadingMode = null;
		if (ConfigManager.getInstance().isTestEnabled()) {
			DBLoadingMode = Definitions.testDBName;
		}
		else {
			DBLoadingMode = Definitions.mainDBName;
		}
		try {
			// don't specify port and local host, it can automatically find the DB
			m_mongo = new Mongo();
			// load appropriate DB
			m_DB = m_mongo.getDB(DBLoadingMode);
			        
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @brief get the instance of Db handler
	 * @return DBHandler
	 */
	public static DBHandler getInstance()
	{
		if (m_instance == null)
			m_instance = new DBHandler();
		return m_instance;
	}
	
	/**
	 * @brief getDB() get DB
	 * @return returns DB
	 */
	public DB getDB()
	{
		return m_DB;
	}
	
	/**
	 * @brief closeMongo() This is to close the connection with DB
	 */
	public void closeMongo()
	{
		if (m_mongo != null) {
			m_mongo.close();
			m_mongo = null;
		}
	}
	
	/**
	 * @brief Resets dataBase
	 */
	public void resetDB()
	{
		m_DB.dropDatabase();
	}
}
