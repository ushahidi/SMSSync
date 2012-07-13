package org.addhen.smssync.database;

public interface ISyncUrlSchema {

	public static final String ID = "_id";

	public static final String TITLE = "title";

	public static final String KEYWORDS = "keywords";

	public static final String URL = "url";

	public static final String STATUS = "status";

	public static final String SECRET = "secret";

	public static final String TABLE = "syncurl";

	public static final String[] COLUMNS = new String[] { ID, TITLE, KEYWORDS,
			URL, SECRET, STATUS };

	public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ STATUS + " INTEGER , " + KEYWORDS + " TEXT, " + TITLE
			+ " TEXT NOT NULL, " + SECRET + " TEXT, " + URL + " TEXT NOT NULL "
			+ ")";
}
