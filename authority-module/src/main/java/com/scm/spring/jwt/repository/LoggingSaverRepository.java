package com.scm.spring.jwt.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.scm.spring.jwt.model.LoggingSaver;

@Repository
public class LoggingSaverRepository {

	@Autowired
	private DynamoDBMapper mapper;

	private static final String ACCESS_KEY = "AKIA55C6QLOZJWN2BRIZ";
	private static final String SECRET_KEY = "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";

	public LoggingSaver addLog(LoggingSaver loggingSaver) {
		mapper.save(loggingSaver);
		return loggingSaver;
	}

	public LoggingSaver findLogById(String logId) {
		return mapper.load(LoggingSaver.class, logId);
	}

	public String deletePerson(LoggingSaver loggingSaver) {
		mapper.delete(loggingSaver);
		return "log removed";
	}

	public void queryItems() {
		Map<String, AttributeValue> eav = new HashMap<>();
		eav.put(":val1", new AttributeValue().withS("viewHomePage"));
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression().withFilterExpression("methodName = :val1")
				.withExpressionAttributeValues(eav);
		List<LoggingSaver> scanResult = mapper.scan(LoggingSaver.class, scanExpression);
		System.out.println(scanResult.size());
		for (LoggingSaver bicycle : scanResult) {
			System.out.println("The value is = " + bicycle);
		}
	}

	public List<LoggingSaver> getAllLogs() {

		BasicAWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
		AmazonDynamoDBClient client = new AmazonDynamoDBClient(credentials).withRegion(Regions.US_EAST_2);
		List<LoggingSaver> lstEmployees = new ArrayList<>();
		try {
			ScanRequest scanRequest = new ScanRequest().withTableName("loggeraop");
			ScanResult result = null;
			do {
				if (result != null) {
					scanRequest.setExclusiveStartKey(result.getLastEvaluatedKey());
				}
				result = client.scan(scanRequest);
				List<Map<String, AttributeValue>> rows = result.getItems();
				for (Map<String, AttributeValue> mapEmployeeRecord : rows) {
					LoggingSaver employee = parseEmployeeInfo(mapEmployeeRecord);
					lstEmployees.add(employee);
				}
			} while (result.getLastEvaluatedKey() != null);
		} catch (AmazonClientException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
		return lstEmployees;
	}

	private LoggingSaver parseEmployeeInfo(Map<String, AttributeValue> mapEmployeeRecord) {
		LoggingSaver employee = new LoggingSaver();
		try {
			AttributeValue logId = mapEmployeeRecord.get(LoggingSaver.LOGID);
			AttributeValue methodName = mapEmployeeRecord.get(LoggingSaver.METHODNAME);
			AttributeValue className = mapEmployeeRecord.get(LoggingSaver.CLASSNAME);
			AttributeValue dateTime = mapEmployeeRecord.get(LoggingSaver.DATETIME);
			AttributeValue logStatement = mapEmployeeRecord.get(LoggingSaver.LOGSTATEMENT);
			employee.setLogId(logId.getS());
			if (methodName != null) {
				employee.setMethodName(mapEmployeeRecord.get(LoggingSaver.METHODNAME).getS());
			}
			if (className != null) {
				employee.setClassName(mapEmployeeRecord.get(LoggingSaver.CLASSNAME).getS());
			}
			if (dateTime != null) {
				employee.setDateTime(mapEmployeeRecord.get(LoggingSaver.DATETIME).getS());
			}
			if (logStatement != null) {
				employee.setLogStatement(mapEmployeeRecord.get(LoggingSaver.LOGSTATEMENT).getS());
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return employee;
	}
}