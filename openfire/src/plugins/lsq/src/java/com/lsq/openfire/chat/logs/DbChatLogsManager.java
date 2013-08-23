package com.lsq.openfire.chat.logs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jivesoftware.database.DbConnectionManager;

import com.lsq.openfire.chat.logs.entity.ChatLogs;

public class DbChatLogsManager {

	private static final Logger Log = LoggerFactory
			.getLogger(DbChatLogsManager.class);

	private static final DbChatLogsManager CHAT_LOGS_MANAGER = new DbChatLogsManager();

	private DbChatLogsManager() {

	}

	public static DbChatLogsManager getInstance() {

		return CHAT_LOGS_MANAGER;

	}

	private static final String LOGS_COUNT = "SELECT count(1) FROM ofChatLogs";

	private static final String LOGS_LAST_MESSAGE_ID = "SELECT max(messageId) FROM ofChatLogs";

	private static final String LOGS_FIND_BY_ID = "SELECT messageId, sessionJID, sender, receiver, createDate, length, content FROM ofChatLogs where messageId = ?";

	private static final String LOGS_REMOVE = "UPDATE ofChatLogs set state = 1 where messageId = ?";// "DELETE FROM ofChatLogs WHERE messageId = ?";

	private static final String LOGS_INSERT = "INSERT INTO ofChatLogs(sessionJID, sender, receiver, createDate, length, content, detail, state) VALUES(?,?,?,?,?,?,?,?)";

	private static final String LOGS_QUERY = "SELECT messageId, sessionJID, sender, receiver, createDate, length, content FROM ofChatLogs where state = 0";

	private static final String LOGS_SEARCH = "SELECT * FROM ofChatLogs where state = 0";

	private static final String LOGS_LAST_CONTACT = "SELECT distinct receiver FROM ofChatLogs where state = 0 and sender = ?";

	private static final String LOGS_ALL_CONTACT = "SELECT distinct sessionJID FROM ofChatLogs where state = 0";

	public int getLastId() {

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int count = -1;
		try {
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement(LOGS_LAST_MESSAGE_ID);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			} else {
				count = 0;
			}
		} catch (SQLException sqle) {
			Log.error(sqle.getMessage(), sqle);
			return 0;
		} finally {
			DbConnectionManager.closeConnection(pstmt, con);
		}
		return count;

	}


	public int getCount() {

		Connection con = null;

		PreparedStatement pstmt = null;

		ResultSet rs = null;

		int count = -1;

		try {

			con = DbConnectionManager.getConnection();

			pstmt = con.prepareStatement(LOGS_COUNT);

			rs = pstmt.executeQuery();

			if (rs.next()) {

				count = rs.getInt(1);

			} else {

				count = 0;

			}

		} catch (SQLException sqle) {

			Log.error(sqle.getMessage(), sqle);

			return 0;

		} finally {

			DbConnectionManager.closeConnection(pstmt, con);

		}

		return count;

	}


	public boolean remove(Integer id) {

		Connection con = null;

		PreparedStatement pstmt = null;

		try {

			con = DbConnectionManager.getConnection();

			pstmt = con.prepareStatement(LOGS_REMOVE);

			pstmt.setInt(1, id);

			return pstmt.execute();

		} catch (SQLException sqle) {

			Log.error("chatLogs remove exception: {}", sqle);

			return false;

		} finally {

			DbConnectionManager.closeConnection(pstmt, con);

		}

	}

	public boolean add(ChatLogs logs) {

		Connection con = null;

		PreparedStatement pstmt = null;

		try {
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement(LOGS_INSERT);
			int i = 1;
			pstmt.setString(i++, logs.getSessionJID());
			pstmt.setString(i++, logs.getSender());
			pstmt.setString(i++, logs.getReceiver());
			pstmt.setTimestamp(i++, logs.getCreateDate());
			pstmt.setInt(i++, logs.getLength());
			pstmt.setString(i++, logs.getContent());
			pstmt.setString(i++, logs.getDetail());
			pstmt.setInt(i++, logs.getState());

			return pstmt.execute();

		} catch (SQLException sqle) {
			Log.error("chatLogs add exception: {}", sqle);
			return false;
		} finally {
			DbConnectionManager.closeConnection(pstmt, con);
		}
	}


	public ChatLogs find(int id) {

		Connection con = null;

		PreparedStatement pstmt = null;

		ChatLogs logs = null;

		try {

			con = DbConnectionManager.getConnection();

			pstmt = con.prepareStatement(LOGS_FIND_BY_ID);

			pstmt.setInt(1, id);

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {

				logs = new ChatLogs();

				logs.setMessageId(rs.getInt("messageId"));

				logs.setContent(rs.getString("content"));

				logs.setCreateDate(rs.getTimestamp("createDate"));

				logs.setLength(rs.getInt("length"));

				logs.setSessionJID(rs.getString("sessionJID"));

				logs.setSender(rs.getString("sender"));

				logs.setReceiver(rs.getString("receiver"));

			}

			return logs;

		} catch (SQLException sqle) {

			Log.error("chatLogs find exception: {}", sqle);

			return logs;

		} finally {

			DbConnectionManager.closeConnection(pstmt, con);

		}

	}

	public List<ChatLogs> query(ChatLogs entity) {

		Connection con = null;

		Statement pstmt = null;

		ChatLogs logs = null;

		List<ChatLogs> result = new ArrayList<ChatLogs>();

		try {

			con = DbConnectionManager.getConnection();

			pstmt = con.createStatement();

			String sql = LOGS_QUERY;

			if (entity != null) {

				if (!StringUtils.isEmpty(entity.getSender())
						&& !StringUtils.isEmpty(entity.getReceiver())) {

					sql += " and (sender = '" + entity.getSender()
							+ "' and receiver = '" + entity.getReceiver()
							+ "')";

					sql += " or (receiver = '" + entity.getSender()
							+ "' and sender = '" + entity.getReceiver() + "')";

				} else {

					if (!StringUtils.isEmpty(entity.getSender())) {

						sql += " and sender = '" + entity.getSender() + "'";

					}

					if (!StringUtils.isEmpty(entity.getReceiver())) {

						sql += " and receiver = '" + entity.getReceiver() + "'";

					}

				}

				if (!StringUtils.isEmpty(entity.getContent())) {

					sql += " and content like '%" + entity.getContent() + "%'";

				}

				if (entity.getCreateDate() != null) {

					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

					String crateatDate = df.format(new Date(entity
							.getCreateDate().getTime()));

					// sql += " and to_char(createDate, 'yyyy-mm-dd') = '" +
					// crateatDate + "'";

					sql += " and createDate like '" + crateatDate + "%'";

				}

			}

			sql += " order by createDate asc";

			ResultSet rs = pstmt.executeQuery(sql);

			while (rs.next()) {

				logs = new ChatLogs();

				logs.setMessageId(rs.getInt("messageId"));

				logs.setContent(rs.getString("content"));

				logs.setCreateDate(rs.getTimestamp("createDate"));

				logs.setLength(rs.getInt("length"));

				logs.setSessionJID(rs.getString("sessionJID"));

				logs.setSender(rs.getString("sender"));

				logs.setReceiver(rs.getString("receiver"));

				result.add(logs);

			}

			return result;

		} catch (SQLException sqle) {

			Log.error("chatLogs search exception: {}", sqle);

			return result;

		} finally {

			DbConnectionManager.closeConnection(pstmt, con);

		}

	}


	public List<HashMap<String, Object>> search(ChatLogs entity) {

		Connection con = null;

		Statement pstmt = null;

		List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();

		try {

			con = DbConnectionManager.getConnection();

			pstmt = con.createStatement();

			String sql = LOGS_SEARCH;

			if (entity != null) {

				if (!StringUtils.isEmpty(entity.getSender())
						&& !StringUtils.isEmpty(entity.getReceiver())) {

					sql += " and (sender = '" + entity.getSender()
							+ "' and receiver = '" + entity.getReceiver()
							+ "')";

					sql += " or (receiver = '" + entity.getSender()
							+ "' and sender = '" + entity.getReceiver() + "')";

				} else {

					if (!StringUtils.isEmpty(entity.getSender())) {

						sql += " and sender = '" + entity.getSender() + "'";

					}

					if (!StringUtils.isEmpty(entity.getReceiver())) {

						sql += " and receiver = '" + entity.getReceiver() + "'";

					}

				}

				if (!StringUtils.isEmpty(entity.getContent())) {

					sql += " and content like '%" + entity.getContent() + "%'";

				}

				if (entity.getCreateDate() != null) {

					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

					String crateatDate = df.format(new Date(entity
							.getCreateDate().getTime()));

					sql += " and to_char(createDate, 'yyyy-mm-dd') = '"
							+ crateatDate + "'";

				}

			}

			sql += " order by createDate asc";

			ResultSet rs = pstmt.executeQuery(sql);

			ResultSetMetaData rsmd = rs.getMetaData();


			int columnCount = rsmd.getColumnCount();

			while (rs.next()) {

				HashMap<String, Object> map = new HashMap<String, Object>();


				for (int i = 1; i <= columnCount; ++i) {

					String columnVal = rs.getString(i);

					if (columnVal == null) {

						columnVal = "";

					}

					map.put(rsmd.getColumnName(i), columnVal);

				}

				/** 把装有一行数据的HashMap存入list */

				result.add(map);

			}

			return result;

		} catch (SQLException sqle) {

			Log.error("chatLogs search exception: {}", sqle);

			return result;

		} finally {

			DbConnectionManager.closeConnection(pstmt, con);

		}

	}

	public List<String> findLastContact(ChatLogs entity) {

		Connection con = null;

		PreparedStatement pstmt = null;

		List<String> result = new ArrayList<String>();

		try {

			con = DbConnectionManager.getConnection();

			pstmt = con.prepareStatement(LOGS_LAST_CONTACT);

			pstmt.setString(1, entity.getSender());

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {

				result.add(rs.getString("receiver"));

			}

			return result;

		} catch (SQLException sqle) {

			Log.error("chatLogs find exception: {}", sqle);

			return result;

		} finally {

			DbConnectionManager.closeConnection(pstmt, con);

		}

	}

	public List<String> findAllContact() {

		Connection con = null;

		PreparedStatement pstmt = null;

		List<String> result = new ArrayList<String>();

		try {

			con = DbConnectionManager.getConnection();

			pstmt = con.prepareStatement(LOGS_ALL_CONTACT);

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {

				result.add(rs.getString("sessionJID"));

			}

			return result;

		} catch (SQLException sqle) {

			Log.error("chatLogs find exception: {}", sqle);

			return result;

		} finally {

			DbConnectionManager.closeConnection(pstmt, con);

		}

	}
}
