package com.bmg.mall.config;

import com.bmg.mall.entity.bmgOrderAddress;
import com.bmg.mall.util.Result;
import com.bmg.mall.util.ResultGenerator;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * 快递鸟电子面单接口
 *
 * @技术QQ群: 340378554
 * @see: http://kdniao.com/api-eorder
 * @copyright: 深圳市快金数据技术服务有限公司
 * 
 * ID和Key请到官网申请：http://kdniao.com/reg
 */
 
public class KdGoldAPIDemo {
	
	//电商ID
//	private String EBusinessID="1671352";
	//沙箱测试
	private String EBusinessID="test1671352";
	//电商加密私钥，快递鸟提供，注意保管，不要泄漏
//	private String AppKey="0df2a3af-42aa-4d87-8295-f18e97d1ea03";
	private String AppKey="dbd46a6a-e08e-4605-ac94-f558e435abc4";

	//请求url, 正式环境地址：http://api.kdniao.com/api/Eorderservice    测试环境地址：http://testapi.kdniao.com:8081/api/EOrderService
	//沙箱环境http://sandboxapi.kdniao.com:8080/kdniaosandbox/gateway/exterfaceInvoke.json
	private String ReqURL="http://sandboxapi.kdniao.com:8080/kdniaosandbox/gateway/exterfaceInvoke.json";


	/**
     * Json方式 电子面单
	 * @throws Exception 
     */
	public String orderOnlineByJson(bmgOrderAddress bmgOrderAddress) throws Exception{
		String address = bmgOrderAddress.getUserAddress().replace("\n", "").trim();

		String[] list = address.split(",");
		if (list == null || list.length!=3) {
			return null;
		}

		//解析收件地址
		Map<String,String> deil = addressResolution(list[2]);

		String requestData= "{'OrderCode': '"+bmgOrderAddress.getOrderNo()+"'," +
                "'ShipperCode':'YD '," +
                "'PayType':1," +
				"'CustomerName':'testyd'," +
				"'CustomerPwd':'testydpwd'," +
                "'ExpType':1," +
                "'Cost':1.0," +
                "'OtherCost':1.0," +
                "'Sender':" +
                "{" +
                "'Company':'LV','Name':'阿良3','Mobile':'15018442396','ProvinceName':'广东','CityName':'广州','ExpAreaName':'白云','Address':'明珠路73号'}," +
                "'Receiver':" +
                "{" +
                "'Company':'GCCUI','Name':'"+list[0]+"','Mobile':'"+list[1]+"','ProvinceName':'"+deil.get("province")+"','CityName':'"+deil.get("city")+"','ExpAreaName':'"+deil.get("county")+"','Address':'"+deil.get("village")+"'}," +
                "'Commodity':" +
                "[{" +
                "'GoodsName':'商品','Goodsquantity':1,'GoodsWeight':1.0}]," +
                "'Weight':1.0," +
                "'Quantity':1," +
                "'Volume':0.0," +
                "'Remark':'小心轻放'," +
                "'IsReturnPrintTemplate':1}";
		Map<String, String> params = new HashMap<String, String>();
		params.put("RequestData", urlEncoder(requestData, "UTF-8"));
		params.put("EBusinessID", EBusinessID);
		params.put("RequestType", "1007");
		String dataSign=encrypt(requestData, AppKey, "UTF-8");
		params.put("DataSign", urlEncoder(dataSign, "UTF-8"));
		params.put("DataType", "2");
		
		String result=sendPost(ReqURL, params);	
		
		//根据公司业务处理返回的信息......
		
		return result;
	}
	/**
     * MD5加密
     * @param str 内容       
     * @param charset 编码方式
	 * @throws Exception 
     */
	@SuppressWarnings("unused")
	private String MD5(String str, String charset) throws Exception {
	    MessageDigest md = MessageDigest.getInstance("MD5");
	    md.update(str.getBytes(charset));
	    byte[] result = md.digest();
	    StringBuffer sb = new StringBuffer(32);
	    for (int i = 0; i < result.length; i++) {
	        int val = result[i] & 0xff;
	        if (val <= 0xf) {
	            sb.append("0");
	        }
	        sb.append(Integer.toHexString(val));
	    }
	    return sb.toString().toLowerCase();
	}
	
	/**
     * base64编码
     * @param str 内容       
     * @param charset 编码方式
	 * @throws UnsupportedEncodingException 
     */
	private String base64(String str, String charset) throws UnsupportedEncodingException{
		String encoded = Base64.encode(str.getBytes(charset));
		return encoded;    
	}	
	
	@SuppressWarnings("unused")
	private String urlEncoder(String str, String charset) throws UnsupportedEncodingException{
		String result = URLEncoder.encode(str, charset);
		return result;
	}
	
	/**
     * 电商Sign签名生成
     * @param content 内容   
     * @param keyValue Appkey  
     * @param charset 编码方式
	 * @throws UnsupportedEncodingException ,Exception
	 * @return DataSign签名
     */
	@SuppressWarnings("unused")
	private String encrypt (String content, String keyValue, String charset) throws UnsupportedEncodingException, Exception
	{
		if (keyValue != null)
		{
			return base64(MD5(content + keyValue, charset), charset);
		}
		return base64(MD5(content, charset), charset);
	}
	
	 /**
     * 向指定 URL 发送POST方法的请求     
     * @param url 发送请求的 URL    
     * @param params 请求的参数集合     
     * @return 远程资源的响应结果
     */
	@SuppressWarnings("unused")
	private String sendPost(String url, Map<String, String> params) {
        OutputStreamWriter out = null;
        BufferedReader in = null;        
        StringBuilder result = new StringBuilder(); 
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn =(HttpURLConnection) realUrl.openConnection();
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // POST方法
            conn.setRequestMethod("POST");
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.connect();
            // 获取URLConnection对象对应的输出流
            out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            // 发送请求参数            
            if (params != null) {
		          StringBuilder param = new StringBuilder(); 
		          for (Map.Entry<String, String> entry : params.entrySet()) {
		        	  if(param.length()>0){
		        		  param.append("&");
		        	  }	        	  
		        	  param.append(entry.getKey());
		        	  param.append("=");
		        	  param.append(entry.getValue());		        	  
		        	  System.out.println(entry.getKey()+":"+entry.getValue());
		          }
		          System.out.println("param:"+param.toString());
		          out.write(param.toString());
            }
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {            
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result.toString();
    }

    //地址校验
	public static Map<String,String> addressResolution(String address){
		String regex="(?<province>[^省]+自治区|.*?省|.*?行政区|.*?市)(?<city>[^市]+自治州|.*?地区|.*?行政单位|.+盟|市辖区|.*?市|.*?县)(?<county>[^县]+县|.+区|.+市|.+旗|.+海域|.+岛)?(?<town>[^区]+区|.+镇)?(?<village>.*)";
		Matcher m= Pattern.compile(regex).matcher(address);
		String province=null,city=null,county=null,town=null,village=null;
		Map<String,String> row=null;
		while(m.find()){
			row=new LinkedHashMap<String,String>();
			province=m.group("province");
			row.put("province", province==null?"":province.trim());
			city=m.group("city");
			row.put("city", city==null?"":city.trim());
			county=m.group("county");
			row.put("county", county==null?"":county.trim());
			town=m.group("town");
			row.put("town", town==null?"":town.trim());
			village=m.group("village");
			row.put("village", village==null?"":village.trim());
		}
		return row;
	}

	public static void main(String[] args) throws Exception {
//		System.out.println(addressResolution("浙江省杭州市余杭区文一西路亲橙里20栋1025"));
//		Map<String,String> xxx = addressResolution("浙江省杭州市余杭区文一西路亲橙里20栋1025");
//		System.out.println(xxx.get("province"));
//		System.out.println(new KdGoldAPIDemo().orderOnlineByJson());
	}

	public boolean isNull(String add){
		if(add==null || add.trim().equals("")){
			return true;
		}
		return false;
	}
}
