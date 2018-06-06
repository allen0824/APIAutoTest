package com.alien.emmatesting.testcase.user; 

import java.util.Map;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;
import com.alien.emmatesting.util.JdbcUtil;
import com.alien.emmatesting.util.UnirestUtil;
import com.alien.emmatesting.base.BaseParpare;

public class test_001_test extends BaseParpare { 
	static Logger logger = Logger.getLogger(test_001_test.class.getName());
	@Test(dataProvider = "testData") 
	public void testCase(Map<String, String> data) { 
		String pre_condition = data.get("pre_condition"); 
		/******************************************************************************** 
		 *********************** 用例中的pre_condition列不为空时，执行相应的SQL语句******************** 
		 ********************************************************************************/ 
		if (pre_condition.length() > 0) { 
		logger.info("**********************************正在执行数据准备操作*********************************"); 
			String[] Array = pre_condition.split(";\n"); 
			for (int i = 0; i < Array.length; i++) { 
				JdbcUtil.executeNonQuery(Array[i]); 
			} 
		logger.info("**********************************执行数据准备操作完成*********************************"); 
		} 
		/******************************************************************************** 
		 *********************************** 进行接口测试，并验证测试结果*************************** 
		 ********************************************************************************/ 
		UnirestUtil.InterfaceTest(data);  
	}
}