package config;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class DBConnectionTest {

    @Test
    void testMySQLConnection() {
        assertDoesNotThrow(() -> {
            try (Connection conn = DBConnection.getConnection()) {
                assertNotNull(conn, "connection variable should not be null");

                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT 1");

                if (rs.next()) {
                    int result = rs.getInt(1);
                    assertEquals(1, result);
                }
                System.out.println(">> Connect to MySQL successfully!");
            }
        });
    }
}
