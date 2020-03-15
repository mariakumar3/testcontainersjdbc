package com.tech.mkblogs;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MySQLContainer;

import lombok.extern.log4j.Log4j2;

@Log4j2
@ExtendWith(SpringExtension.class)
public class TestMysqlContainer {

    public static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8")
    													.withInitScript("schema-mysql.sql");
	
	private String insertQuery = "INSERT INTO account "
			+ "(account_name,account_type,amount,created_by,created_name,created_ts,VERSION) "
			+ "VALUES(?,?,?,?,?,?,?)";
          

	@BeforeAll
	public static void beforeEachTest() {
		log.info("==================================================================================");
		log.info("This is executed before each Test");
		mysqlContainer.start();
	}

	@AfterAll
	public static void afterEachTest() {		
		log.info("This is exceuted after each Test");
		log.info("==================================================================================");
		mysqlContainer.stop();
	}
	
	@org.junit.jupiter.api.Test
	public void testthis() throws Exception {
		
		Connection conn = DriverManager.getConnection( mysqlContainer.getJdbcUrl(),
				mysqlContainer.getUsername(), mysqlContainer.getPassword() );
		
		//ResultSet resultSet = conn.createStatement().executeQuery("SELECT foo FROM bar");
		ResultSet rs = conn.createStatement().executeQuery("SELECT VERSION()");
		System.out.println(rs);
		if(rs.next()) {
			String firstColumnValue = rs.getString(1);
			System.out.println(firstColumnValue);
		}
		
        System.out.println("Done");
       // mysqlContainer.stop();
	}
	
	@org.junit.jupiter.api.Test
	public void saveAccount() throws Exception {
		
		Connection connection = DriverManager.getConnection( mysqlContainer.getJdbcUrl(),
				mysqlContainer.getUsername(), mysqlContainer.getPassword() );
		
		PreparedStatement preparedStatement = connection.prepareStatement(insertQuery,new String[]{"id"});
		
		preparedStatement.setString(1, "Testthis");
		preparedStatement.setString(2, "Savings");
        preparedStatement.setBigDecimal(3, new BigDecimal(100));
        
        preparedStatement.setInt(4, 1);
        preparedStatement.setString(5, "Test");            
        preparedStatement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
        
        preparedStatement.setInt(7, 0);

        int row = preparedStatement.executeUpdate();
		
        log.info("Inserted Count ::"+row);
        
       ResultSet rs = preparedStatement.getGeneratedKeys();
       Long key = rs.next() ? rs.getLong(1) : 0;
       
       log.info("Generated Id ::"+key);
        
	}
}
