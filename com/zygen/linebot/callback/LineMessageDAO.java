package com.zygen.linebot.callback;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import com.zygen.linebot.callback.LineMessage;


public class LineMessageDAO {
	private DataSource dataSource;
	
    public LineMessageDAO(DataSource newDataSource) throws SQLException {
        setDataSource(newDataSource);
    }

    public DataSource getDataSource() {
        return dataSource;
    }
    public void setDataSource(DataSource newDataSource) throws SQLException {
        this.dataSource = newDataSource;
        checkTable();
    }
    public void addLineMessage(LineMessage lineMessage) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            PreparedStatement pstmt = connection
                    .prepareStatement("INSERT INTO LINEMESSAGES (ID, HEADER, BODY) VALUES (?, ?, ?)");
            pstmt.setString(1, UUID.randomUUID().toString());
            pstmt.setString(2, lineMessage.getHeader());
            pstmt.setString(3, lineMessage.getBody());
            pstmt.executeUpdate();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
    public List<LineMessage> selectAllMessage() throws SQLException {
        Connection connection = dataSource.getConnection();
        try {
            PreparedStatement pstmt = connection
                    .prepareStatement("SELECT ID, HEADER, BODY FROM LINEMESSAGES");
            ResultSet rs = pstmt.executeQuery();
            ArrayList<LineMessage> list = new ArrayList<LineMessage>();
            while (rs.next()) {
                LineMessage p = new LineMessage();
                p.setId(rs.getString(1));
                p.setHeader(rs.getString(2));
                p.setBody(rs.getString(3));
                list.add(p);
            }
            return list;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
    private void checkTable() throws SQLException {
        Connection connection = null;

        try {
            connection = dataSource.getConnection();
            if (!existsTable(connection)) {
                createTable(connection);
            }
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
    private boolean existsTable(Connection conn) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        ResultSet rs = meta.getTables(null, null, "LINEMESSAGES", null);
        while (rs.next()) {
            String name = rs.getString("TABLE_NAME");
            if (name.equals("LINEMESSAGES")) {
                return true;
            }
        }
        return false;
    }
    private void createTable(Connection connection) throws SQLException {
        PreparedStatement pstmt = connection
                .prepareStatement("CREATE TABLE LINEMESSAGES "
                        + "(ID VARCHAR(255) PRIMARY KEY, "
                        + "HEADER VARCHAR (1000),"
                        + "BODY VARCHAR (1000))");
        pstmt.executeUpdate();
    }
    
}
