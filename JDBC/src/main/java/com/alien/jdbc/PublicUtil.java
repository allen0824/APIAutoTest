package com.alien.jdbc;

import com.mashape.unirest.http.JsonNode;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.testng.Assert;

import java.util.Iterator;

public class PublicUtil {
    public static Logger logger = Logger.getLogger(PublicUtil.class);

    /**
     * 将response里面中的数组json存储到一个子json中
     *
     * @param arrName response里面中的含有数组json的字段名
     * @return
     */
    public static JSONObject arrJson(String arrName) {
        JsonNode responseBody = null;
        @SuppressWarnings("null")
        JSONObject root = JSONObject.fromObject(responseBody.toString());// 返回的response
        JSONObject jsonObj = null;
        if (arrName != null && arrName.length() > 0) {
            JSONObject root_data = root.getJSONObject("data");// 返回的response的data部分数据
            JSONArray arrInfo = root_data.getJSONArray(arrName);// response中的arrName数组
            jsonObj = new JSONObject();// 存放commodityInfo数组的json
            JSONObject arrayJson = new JSONObject();// 存放commodityInfo数组里面的json
            JSONArray array = new JSONArray();// 存放commodityInfo数组
            // 循环遍历内容并存放到对应的json数组中
            for (int i = 0; i < arrInfo.size(); i++) {
                JSONObject obj = arrInfo.getJSONObject(i);
                Iterator<?> iter = obj.keys();
                while (iter.hasNext()) {
                    String key = (String) iter.next();
                    arrayJson.put(key, obj.getString(key));
                }
                array.add(arrayJson);
            }
            jsonObj.put("array", array);
        } else {
            jsonObj = root.getJSONObject("data");
        }
        return jsonObj;
    }

    /**
     * 将response里面中的含有数组的子json和数据库查询出来转换为json的数据进行比较。
     *
     * @param database 连接数据库名
     * @param arrName  response里面中的含有数组json的字段名
     * @param sqlStr   SQL字符串
     */
    public static void checkArrJsonData(String database, String arrName,
                                        String sqlStr) {
        JSONObject jsonObj = null;
        JSONObject sqlJson = null;
        jsonObj = arrJson(arrName);
        logger.info("json为：" + jsonObj);
        sqlJson = JdbcUtil.sqlToJson(sqlStr);// 将SQL结果存放到json中
        logger.info("sqlJson:" + sqlJson);
        System.out.println("查询数据库的结果" + ":" + sqlJson);
        boolean flag;
        if (jsonObj.equals(sqlJson)) {
            logger.info("接口测试数据正常返回,数据一致");
        } else {
            flag = false;
            logger.error("接口测试数据正常返回,但返回的数据不正确");
            Assert.assertTrue(flag);
        }
    }

    /* ***********************************************************************************************************
     * 比较两个json数据的一致性，支持全比较、部分比较、含复杂list的json比较***************************************
     * ****判断sortJson里面是否包含json数组***********************************************************************
     * *******a、包含，则进行两个json里面的数组的比较(支持全比较、部分比较)***************************************
     * *******b、不包含，则进行两个json里面数据的比较(支持全比较、部分比较)***************************************
     * **********判断longJson里面是否包含sortJson相应的key********************************************************
     * *************b1、包含，则进而比较相应key对应的value********************************************************
     * ****************最终结果全为true，则校验成功，若有一个为flase，则比较结果为失败****************************
     * *************b2、不包含，则从data节点进行解析校验**********************************************************
     *  ***************判断longJson的data节点里面是否包含sortJson相应的key****************************************
     * *******************b21、包含，则进而比较相应key对应的value*************************************************
     * *******************b22、最终结果全为true，则校验成功，若有一个为flase，则比较结果为失败********************
     *
     * @param longJson 即Response内容
     * @param sortJson 即SQL查询结果转换的json数据
     */
    public static void jsonCompare(JSONObject longJson, JSONObject sortJson) {
        boolean flag = true;
        if (sortJson.toString().contains("[")) {
            jsonArrayValueCompare(longJson, sortJson);
        } else {
            Iterator<?> iter = sortJson.keys();
            while (iter.hasNext()) {
                String key = (String) iter.next();
                String value = (String) sortJson.get(key);
                if (longJson.has(key)) {// 包含相应的key的情况
                    if (longJson.get(key).equals(value)) {// 相应的key对应的value相等
                        flag = true;
                    } else {// 相应的key对应的value不相等
                        flag = false;
                        logger.error("接口测试数据校验失败,预期结果为:" + longJson
                                + ",而实际结果为:" + sortJson);
                        Assert.assertTrue(flag);
                        break;
                    }
                } else {// 不包含相应的key的情况,则从data节点进行解析校验
                    JSONObject newJson = longJson.getJSONObject("data");
                    if (newJson.has(key)) {// data节点包含相应的key的情况
                        if (newJson.get(key).equals(value)) {// 相应的key对应的value相等
                            flag = true;
                        } else {// data节点相应的key对应的value不相等
                            flag = false;
                            logger.error("接口测试数据校验失败,预期结果为:" + longJson
                                    + ",而实际结果为:" + sortJson);
                            Assert.assertTrue(flag);
                            break;
                        }
                    } else {
                        JSONObject newJson2 = newJson
                                .getJSONObject("resultObject");
                        if (newJson2.has(key)) {// resultObject节点包含相应的key的情况
                            if (newJson2.get(key).equals(value)) {// 相应的key对应的value相等
                                flag = true;
                            } else {// resultObject节点相应的key对应的value不相等
                                flag = false;
                                logger.error("接口测试数据校验失败,预期结果为:" + longJson
                                        + ",而实际结果为:" + sortJson);
                                Assert.assertTrue(flag);
                                break;
                            }
                        } else {
                            flag = false;
                            logger.error("接口测试数据校验失败,预期结果为:" + longJson
                                    + ",而实际结果为:" + sortJson);
                            Assert.assertTrue(flag);
                            break;
                        }
                    }
                }
            }
            if (flag == true) {
                logger.info("接口测试数据正常返回,数据一致");
                Assert.assertTrue(true);
            }

        }

    }

    /**
     * @param @param longJson 如：{"resultObject": {"courseid": "1059","lessonList": [{"lessonId": "1157","title": "第一节"},{"lessonId": "1158","title": "第二节"}],
     *               "teacherList": [{"teacherid": "1","name": "苏强"},{"teacherid": "2","name": "胡仔"}]}}
     * @param @param sortJson 如：{"array":[{"lessonId":"1157","title":"第一节"},{"lessonId":"1158","title":"第二节"}]}
     * @return void  包含则返回true，否则返回false
     * @throws
     * @Description: TODO 判断一个json数组中对应的key、value是否包含在另外一个json数组中，包含则返回true，否则返回false
     * @author 吴丁飞
     * @date 2017-8-11
     */
    public static void jsonArrayValueCompare(JSONObject longJson,
                                             JSONObject sortJson) {
        String[] array = StringUtils.substringsBetween(longJson.toString(),
                "[", "]");
        String[] array2 = StringUtils.substringsBetween(sortJson.toString(),
                "[", "]");
        int Count = 0;
        int arrLength = array2.length;
        for (int i = 0; i < array.length; i++) {
            JSONObject longObj = JSONObject.fromObject(array[i]);
            for (int j = 0; j < arrLength; j++) {
                JSONObject shortObj = JSONObject.fromObject(array2[j]);
                if (jsonFieldCompare(longObj, shortObj)) {
                    if (jsonValueCompare(longObj, shortObj)) {
                        Count = Count + 1;
                    }
                }
            }
        }
        if (Count == arrLength) {
            logger.info("接口测试数据正常返回,数据一致");
            Assert.assertTrue(true);
        } else {
            logger.error("接口测试数据校验失败,预期结果为:" + sortJson + ",而实际结果为:" + longJson);
            Assert.assertTrue(false);
        }
    }

    /**
     * @param @param  longJson 如：{"lessonId":"1157","title":"第一节:你所不知道的木兰辞","name":"李白"}
     * @param @param  sortJson 如：{"lessonId":"1157","title":"第一节:你所不知道的木兰辞"}
     * @param @return
     * @return boolean  包含则返回true，否则返回false
     * @throws
     * @Description: TODO 判断一个json数据中对应的key、value是否包含在另外一个json中，包含则返回true，否则返回false
     * @author 吴丁飞
     * @date 2017-8-11
     */
    public static boolean jsonValueCompare(JSONObject longJson,
                                           JSONObject sortJson) {
        boolean flag = true;
        Iterator<?> iter = sortJson.keys();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            String value = (String) sortJson.get(key);
            if (longJson.has(key)) {// 包含相应的key的情况
                if (longJson.get(key).equals(value)) {// 相应的key对应的value相等
                    flag = true;
                } else {// 相应的key对应的value不相等
                    flag = false;
                    break;
                }
            } else {// 不包含相应的key的情况
                flag = false;
                break;
            }
        }
        return flag;
    }

    /**
     * @param @param  longJson 如：{"lessonId":"1157","title":"第一节:你所不知道的木兰辞","name":"李白"}
     * @param @param  sortJson 如：{"lessonId":"1157","title":"第一节:你所不知道的木兰辞"}
     * @param @return
     * @return boolean  包含则返回true，否则返回false
     * @throws
     * @Description: TODO 判断一个json数据中是否包含另外一个json的所有字段，包含则返回true，否则返回false
     * @author 吴丁飞
     * @date 2017-8-11
     */
    public static boolean jsonFieldCompare(JSONObject longJson,
                                           JSONObject sortJson) {
        boolean flag = true;
        Iterator<?> iter = sortJson.keys();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            if (longJson.containsKey(key)) {
                flag = true;
            } else {
                flag = false;
                break;
            }
        }
        return flag;
    }

}
