package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {

    private static Connection conn;

    public static Connection getConnection() {
        if (conn == null) {
            try {
                // 🔧 여기 URL / 계정 / 비밀번호를 네 MySQL 서버에 맞게 수정해줘
            	String url = "jdbc:mysql://nsyun.synology.me:3306/db?serverTimezone=UTC&characterEncoding=UTF-8";
            	String uid = "user";
            	String pwd = "user1234";

                conn = DriverManager.getConnection(url, uid, pwd);
            } catch (SQLException e) {
                throw new RuntimeException("MySQL 연결 실패", e);
            }
        }
        return conn;
    }
}

