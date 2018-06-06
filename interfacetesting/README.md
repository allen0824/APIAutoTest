接口框架内容：

  1、支持多数据源配置

    在工程目录文件config/db.properties中添加数据库的Driver、url、user、password参数,命名格式如下：
    mysqlDriver=com.mysql.jdbc.Driver
    mysqlURL=jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf-8&useSSL=false
    mysqlUser=root
    mysqlPwd=123456

2、测试用例数据文件只支持.xls后缀格式的excel文件
   
    测试用例数据文件存放格式为：data\moduleName\functionName.xls,如：data\pay\payment.xls

3、支持数据准备
   
    在测试用例数据文件pre_condition列中可以写多个insert、update、delete语句，每个语句以;结尾，且每个语句后需换行。

4、支持多种格式的数据校验

    a、具体内容校验

    b、数据库校验
    
    c、json格式数据校验

5、支持数据驱动。
   
    a、可以为一个接口设置不同的参数
    b、也可以在一个sheet页对不同接口进行测试。
   

接口框架使用说明：

    1、在相应的模块下建相应的接口测试用例excel文件。如：data\pay\payment.xls

    2、在TestCaseFactory.java文件中配置Test_Case_Factory("?", "?");该行内容，第一个参数为模块名称，第二个参数为excel名称。如：
       Test_Case_Factory("pay", "payment");
 
    3、右键-->run as-->Java Aplication运行TestCaseFactory这个类。
    
    4、运行成功后，刷新过程可以看到在相应的包下生成了相应的测试脚本。

    5、执行测试,配置testng.xml,指定运行那个测试类。在suite节点下添加即可,例如：
    <test name="pay01">
		<classes>
			<class
				name="com.alien.emmatesting.testcase.pay.payment_001_test" />
		</classes>
	</test>	

    6、使用maven运行测试后,会在target/surefire-reports目录下生成emailable-report.html格式的测试报告，及在target/log目录下生成相应的测试脚本的日志文件。


测试用例填写说明：

    1、excel中可以有多个sheet页。
    2、每个sheet页中包含id、case_desc、pre_condition、interface_url、parameters、expect列。
       id:序号，为1、2、3、4。。。。
       case_desc:用例描述，说明这条用例是做什么的。
       pre_condition:数据准备,可以写多个insert、update、delete语句，每个语句以;结尾，且每个语句后需换行。
       interface_url:接口地址
       parameters:接口参数，为JSON格式，如：
       {"commodity_id":"349"}或{"commodity_id":"349","member_id":"1"}
       或{'msgId': '21001','request': {'version': '2.2.0', 'os': 'ios', 'resolvingPower': '720'}}、
       {
       "msgId": "21001", 
       "request": {
           "version": "2.2.0", 
           "os": "ios", 
           "resolvingPower": "720"
          }
      }
      expect:预期结果，具体内容或者select语句
        数据校验：
			1、判断是否包含select语句
			---判断是否包含select count(1)
			   ---a.包含，执行SQL查询，结果为1表示pass，0表示fail
			   ---b.不包含，执行SQL查询，将结果和Response内容比较
			2、未包含select语句
			---直接和Response内容比较

3、若存在接口依赖

   1、依赖的接口中参数，可以使用参数的格式，如"code":"{code}"，见下
    
    {"client_id":"1","param":{"registerInfo":{"mobileNo":"15824195942","password":"e10adc3949ba59abbe56e057f20f883e"
    ,"pwdLevel":"1","code":"{code}","sourceType":"web"}},"service_name":"kusercen.UserFacade.register",
    "key":"Communication_IKUKO@kaike.la"}
    
   2、在测试脚本中可以通过数据库查询到该参数的内容，再进行参数替换进行测试。如下：

    case 2://利用手机验证码进行注册
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			String qsql1 = 
            "select left(right(substring_index(t.content,'[',1),9),4) from sms_record t where t.mobile = '15824195942' " +
					"order by send_time desc LIMIT 1";
			// 查询
			ResultSet qrs1 = JdbcUtil.executeQuery( qsql1);
			// 输出
			String verificationCode = null;
			try {
				while (qrs1.next()) {
					// System.out.println("验证码为：" + qrs1.getString(1));
					verificationCode = qrs1.getString(1);
				}
			} catch (SQLException e) {
				logger.error("未获取到验证码！！！");
			}
			logger.info("获取验证码完毕，验证码为：" + verificationCode);
			// 关闭
			DBHelper.free(qrs1);
            String newParameters;
            newParameters = data.get("parameters").replace("{code}", verificationCode);
			data.put("parameters", newParameters);//更新验证码
			UnirestUtil.InterfaceTest(data);
			break;
