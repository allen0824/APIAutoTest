package com.alien.emmatesting.util;

import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.testng.Assert;

import java.io.*;

/**
 * 
 * 
 * @Description: 根据要求填写测试用例的excel后，运行该类能够自动生成指定模块用例的测试代码
 * @author 吴丁飞
 * @date 2017-6-13
 */
public class TestCaseFactory {
	public static void main(String[] args) {
		Test_Case_Factory("user", "test");
	}

	/**
	 * 
	 * @Description: 根据模块名、功能名(excel名称)来自动生成测试用例脚本
	 * @param @param moduleName 模块名
	 * @param @param functionName 功能名
	 * @return void
	 * @throws
	 * @author 吴丁飞
	 * @date 2017-6-13
	 */
	public static void Test_Case_Factory(String moduleName, String functionName) {
		final String caseFolder = "src/main/java/com/alien/emmatesting/testcase/";// 测试代码包路径
		File sourceFile = null;// 测试用例excel源文件
		String sheetName = null;// 测试用例excel中sheet的名字
		int sheetNum = 0;// sheet的号码
		String classname = "";
		try {
			// 如果包名不存在，就新建
			File packAge = new File(caseFolder + "/" + moduleName);
			if (packAge.exists()) {
				System.out.println(moduleName + "包已经存在！");
				System.out.println("正在生成用例到" + moduleName + "包下，请稍等...");
			} else {
				packAge.mkdir();
				System.out.println(moduleName + "包已创建！");
				System.out.println("正在生成用例到" + moduleName + "包下，请稍等...");
			}

			String dir = "data/";
			String path = "";
			File file = new File(dir + File.separator + moduleName + "/"
					+ functionName + ".xls");
			path = file.getPath();
			// 根据传入的excel文件路径获得模块中sheet数量,也就是用例个数
			for (int i = 0; i < getSheetNum(path); i++) {
				try {
					sheetName = getSheetName(i, path); // 取得sheetName，从i开始
					sheetNum = getSheetNum(path);
				} catch (BiffException e1) {
					e1.printStackTrace();
				}
				sourceFile = new File(caseFolder + moduleName.toLowerCase()
						+ File.separator + functionName + "_" + sheetName
						+ "_test.java");// 创建测试用例源码，指定存放路径
				FileWriter writer = new FileWriter(sourceFile);
				classname = functionName + "_" + sheetName + "_test";

				// 生成测试用例代码的头文件
				writer.write("package com.alien.emmatesting.testcase."
						+ moduleName
						+ "; \n"
						+ "\n"
						+ "import java.util.Map;\n"
						+ "import org.apache.log4j.Logger;\n"
						+ "import org.testng.annotations.Test;\n"
						+ "import com.alien.emmatesting.util.JdbcUtil;\n"
						+ "import com.alien.emmatesting.util.UnirestUtil;\n"
						+ "import com.alien.emmatesting.base.BaseParpare;\n"
						+ "\n" + "public class " + classname
						+ " extends BaseParpare { \n");

				// 使用log4j日志
				writer.write("	static Logger logger = Logger.getLogger("
						+ classname + ".class.getName());\n");

				// @Test的主体部分，也就是测试用例的方法
				writer.write("	@Test(dataProvider = \"testData\") \n"
						+ "	public void testCase(Map<String, String> data) { \n"
						+ "		String pre_condition = data.get(\"pre_condition\"); \n"
						+ "		/******************************************************************************** \n"
						+ "		 *********************** 用例中的pre_condition列不为空时，执行相应的SQL语句******************** \n"
						+ "		 ********************************************************************************/ \n"
						+ "		if (pre_condition.length() > 0) { \n"
						+ "		logger.info(\"**********************************正在执行数据准备操作*********************************\"); \n"
						+ "			String[] Array = pre_condition.split(\";\\n\"); \n"
						+ "			for (int i = 0; i < Array.length; i++) { \n"
						+ "				JdbcUtil.executeNonQuery(Array[i]); \n"
						+ "			} \n"
						+ "		logger.info(\"**********************************执行数据准备操作完成*********************************\"); \n"
						+ "		} \n"
						+ "		/******************************************************************************** \n"
						+ "		 *********************************** 进行接口测试，并验证测试结果*************************** \n"
						+ "		 ********************************************************************************/ \n"
						+ "		UnirestUtil.InterfaceTest(data);  \n" + "	}\n");
				// 代码结尾大括号
				writer.write("}");
				writer.close();
			}
		} catch (IOException e) {
			Assert.fail("IO异常", e);
		}
		System.out.println("模块[" + moduleName + "]下[" + functionName
				+ "]功能的用例已经生成完毕，共计：" + (sheetNum) + "条，请到" + caseFolder
				+ moduleName.toLowerCase() + "路径下查阅！");
	}

	/**
	 * 
	 * @Description: 获得当前excel的sheet数量 - 每个模块功能下的用例数
	 * @param @param filePath 文件路径
	 * @param @return 获得excel的sheet数量
	 * @param @throws FileNotFoundException
	 * @param @throws IOException
	 * @return int
	 * @throws
	 * @author 吴丁飞
	 * @date 2017-6-13
	 */
	public static int getSheetNum(String filePath)
			throws FileNotFoundException, IOException {
		int casesNum = 0;
		Workbook book = null;
		try {
			book = Workbook.getWorkbook(new FileInputStream(filePath));
		} catch (BiffException e) {
			e.printStackTrace();
		}
		casesNum = book.getSheets().length;
		return casesNum;
	}

	/**
	 * 
	 * @Description: 获取指定excel文件的sheet页名称
	 * @param @param sheetIndex sheet的位置
	 * @param @param filePath excel文件路径相对的
	 * @param @return 返回sheet的名字
	 * @param @throws BiffException
	 * @param @throws IOException
	 * @return String
	 * @throws
	 * @author 吴丁飞
	 * @date 2017-6-13
	 */
	public static String getSheetName(int sheetIndex, String filePath)
			throws BiffException, IOException {
		String casesName = null;
		Workbook book = null;
		try {
			book = Workbook.getWorkbook(new FileInputStream(filePath));
		} catch (BiffException e) {
			e.printStackTrace();
		}
		casesName = book.getSheet(sheetIndex).getName();
		return casesName;

	}

}
