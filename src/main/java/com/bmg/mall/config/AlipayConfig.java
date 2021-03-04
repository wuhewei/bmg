package com.bmg.mall.config;

import java.io.FileWriter;
import java.io.IOException;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *修改日期：2017-04-05
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipayConfig {
	
//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

	// 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
	public static String app_id = "2021000116699347";
	
	// 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCStu6K6761aT3awCDUot+nAvtu2g8gIcw28HxIvixDI+Xe2obwpkPclqqXeQzvkqkEExw+E5b7jeuH8R8MXNUrlcXHBWq29dKK31pWLyiblJI7No4P8xa0LLe3xEnW7nTMyVZu5lXWzoxO3+frIm9WPIFudXlfzm6YeT2bBOBA2hJsKuv41LtbUS5OvOUh6VTya3IhGbtuK+QgvZlC8I6xZagqvow26LReyufHkSxCPrm6rrQdR7f8pD/I6EqlFdpenwv9eczrp0kaVwt8p4SINfBpbW+fbWXqfg3FBgrtO/X0BF9/l617XVxt1bVtX7yuemaTUljb8Xj2qFJUNl+JAgMBAAECggEBAIjlVjmb9QvS0ltt0HR4MVXl63Qo1do2jxVoWZ+Ki0Rul4vUvfqIOWP31d+WnOJiJ4Dc0AqAPu4kI3RqIX1olaOgkbW9sTBcuYLJG8K1KB/bhoLnybDKMxIVc6tt5TQ+tg9rglHibJANlE/4jq8RJ3AA7wiBZqpltT5iSoB+ET2QiuuOBlfmG6NuHKs6KN3+eX5CGyo0Z1pgHbmmkrTZJlamcZ2dff79fJYOgMDoc8pXbmPskBcTOvduW1gZNdbw9dKiDXzxYRcvghUpkxiunul+CjRTLR9ZNkcNb8NbYvSm1QtfTEnB6dDNzv9glVBjX/Cl/eqzJeQCTNHSpCcT1T0CgYEA8e85736uColQsQFB87aGkwGsK0Et6vyVAfnG2M8Cq9ZO+GUulGoyJBYIekDIN8TtTOh54goTw1lEk2kZ8L3IRVyL5kd/ns5w3njqZcOjKEWGr1Rj4nmyA8P/fXZ///N6FvA6RAQy2uXpi3IL1OHaO4q7P0v4fulLKq3YW5HPZSsCgYEAmz6F4gP35cRifd/w0ncDmG36xYgbEDYUDhsSyDmjlOo/kts2uYz5S8J3BQ7zGbHOQlyQz8QCHem0BE+cZDWvYWztjJLpssPPdMNPasgrLH9zJMF4yQOpd6Rbw2XduY8k6t+36A+MV/ZdLnj3daE1SLlja2m5kvv2pq7cfVXSHBsCgYEA3eWJoMTywg0xqjc6E1rs0TZuf/m3MNjbnkVGgSJVE+Bq6955u3wvYvOmcOKsAKKo96od6BlQCRL+F6oFNc4LOrroQZyAS9qSoSoL6qxmIBCsuHDRcC4n/NYuphcizVG2QAWzmyCyuBy5eMFwYp+HtqbGmQaH83bsD0gFse7pZqMCgYA2gsm4BvSwtThwQoXpIOkeSN/ZJGV8V8DLAy6hJ7wMugQAqQCaPqn6tBneqNUrnId0PdSLX6PupPUU3m9nsIIYj4sJ5TwyDRd3RG1KbWbbhRUFPvoOez6ySAAAqHAmi+DGqC+HSI5bBKLLgqZBo412L8J9IO6McfS1qoi5ab0RKQKBgHVI9+vhMMdDL/2BmbaWukLgYal7B+WiiElWzanKWwOo/UuCsa/B1lyaeP4h6jB6YaJjboo8r7d7lZV9ZS7n3Wv+ibBDvIMMTuU/nR0+1WT8kOeG1VhC60lgMQxvHmZifo4LjC3+07/zhr6jWgUhyB3Lkxh+Dc0RwAjhYQcqQgLg";
	
	// 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjraRxxJGJ/nih2dIx0vJYNigwCbfmD66msunv+BD+4+pyyxPF39PhIutTJpsCRLcZ1nhD8guJFFyD7lM0AVeXc+C1t+l8Q5zMavFNK5W/pIfVppH/7FWR6opjcdQGUSxtGLGKaTcVNpIIxO3pKaOnRhZ2Km1FgVzGCQGjISApDPS29nsq5UR1DXOI9ZBBHBDg1sZU0/DV7fixEgUWWA+Y3ZSG+JojD3i4IerouzgQ4WMSQ3E9XifNziGYUHrEtbH5R3K+Z7sqx7xXLyXQaw3LiMO7zNMlvZf5zg0vd0pdD8WaZKM3tRiO6vYf1b9Pmi9B2K8144lf2GuGpWPmc/kXQIDAQAB";

	// 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public static String notify_url = "http://localhost:8085/ispay";

	// 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public static String return_url = "http://localhost:8085/ispay";

	// 签名方式
	public static String sign_type = "RSA2";
	
	// 字符编码格式
	public static String charset = "utf-8";
	
	// 支付宝网关
//	public static String gatewayUrl = "https://openapi.alipay.com/gateway.do";
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";
	
	// 支付宝网关
	public static String log_path = "C:\\";

//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /** 
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

