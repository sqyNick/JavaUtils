package com.fhzz.core.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;


public class JsonUtil {
	
	private Map<String, Object> jsonMap = new HashMap<String, Object>();
	
	private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * 过滤的属性名
	 */
	private String[] filters = null; 

	public JsonUtil() {
	}
	
	public JsonUtil(String[] filters) {
		this.filters = filters;
	}

	public void clear() {
		jsonMap.clear();
	}


	public Map<String, Object> put(String key, Object value) {
		jsonMap.put(key, value);
		return jsonMap;
	}

	private static boolean isNoQuote(Object value) {
		return (value instanceof Integer || value instanceof Boolean
				|| value instanceof Double || value instanceof Float
				|| value instanceof Short || value instanceof Long || value instanceof Byte);
	}

	private static boolean isQuote(Object value) {
		return (value instanceof String || value instanceof Character);
	}
	
	private static boolean isContains(String fieldName,String[] filters) {
		if (null == fieldName || null == filters || filters.length == 0)
			return false;
		for (int i = 0; i < filters.length; i++)
			if (fieldName.equals(filters[i]))
				return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		Set<Entry<String, Object>> set = jsonMap.entrySet();
		for (Entry<String, Object> entry : set) {
			Object value = entry.getValue();
			if (value == null) {
				continue;
			}
			sb.append("\"").append(String.valueOf(entry.getKey())).append("\":");
			if (value instanceof JsonUtil) {
				sb.append(value.toString());
			} else if (isNoQuote(value)) {
				sb.append(value);
			} else if (value instanceof Date) {
				sb.append("\"").append(formatter.format(value)).append("\"");
			} else if (isQuote(value)) {
				sb.append("\"").append(formateJson(value.toString())).append("\"");
			} else if (value.getClass().isArray()) {
				sb.append(ArrayToStr(value));
			} else if (value instanceof Map) {
				sb.append(fromObject((Map<String, Object>) value).toString());
			} else if (value instanceof List) {
				sb.append(ListToStr((List<Object>) value,filters));
			} else if(value instanceof Integer){
				sb.append(value);
			}else {
				sb.append(fromObject(value).toString());
			}
			sb.append(",");
		}
		int len = sb.length();
		if (len > 1) {
			sb.delete(len - 1, len);
		}
		sb.append("}");
		return sb.toString();
	}

	public static String ArrayToStr(Object array) {
		if (!array.getClass().isArray())
			return "[]";
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		int len = Array.getLength(array);
		Object v = null;
		for (int i = 0; i < len; i++) {
			v = Array.get(array, i);
			if (v instanceof Date) {
				sb.append("\"").append(formatter.format(v)).append("\"").append(
						",");
			} else if (isQuote(v)) {
				sb.append("\"").append(v).append("\"").append(",");
			} else if (isNoQuote(v)) {
				sb.append(v).append(",");
			} else {
				sb.append(fromObject(v)).append(",");
			}
		}
		len = sb.length();
		if (len > 1)
			sb.delete(len - 1, len);
		sb.append("]");
		return sb.toString();
	}
    
	@SuppressWarnings("unchecked")
	public static String ListToStr(List<? extends Object> list,String[] filters) {
		if (list == null)
			return null;
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		Object value = null;
		for (java.util.Iterator it = list.iterator(); it.hasNext();) {
			value = it.next();
			if (value instanceof Map) {
				sb.append(fromObject((Map) value).toString()).append(",");
			} else if (isNoQuote(value)) {
				sb.append(value).append(",");
			} else if (isQuote(value)) {
				sb.append("\"").append(formateJson(value.toString())).append("\"").append(",");
			} else {
				sb.append(fromObject(value,filters).toString()).append(",");
			}
		}
		int len = sb.length();
		if (len > 1)
			sb.delete(len - 1, len);
		sb.append("]");
		return sb.toString();
	}
	
	public static String ListToStr(List<? extends Object> list) {
		return ListToStr(list,null);
	}

    /**
     * Java Bean 转成Json
     * @param bean
     * @param filters 过滤部分属性
     * @return
     */
	@SuppressWarnings("unchecked")
	public static JsonUtil fromObject(Object bean, String[] filters) {
		JsonUtil json = new JsonUtil();
		if (bean == null)
			return json;
		Class cls = bean.getClass();
		Field[] fs = cls.getDeclaredFields();
		Object value = null;
		String fieldName = null;
		Method method = null;
		int len = fs.length;
		for (int i = 0; i < len; i++) {
			fieldName = fs[i].getName();
			if (isContains(fieldName, filters))
				continue;
			try {
				method = cls.getMethod(getGetter(fieldName), (Class[]) null);
				value = method.invoke(bean, (Object[]) null);
			} catch (Exception e) {
				// System.out.println(method.getName());
				// e.printStackTrace();
				continue;
			}
			json.put(fieldName, value);
		}
		return json;
	}
	
	public static JsonUtil fromObject(Object bean) {
		return fromObject(bean,null);
	}

	public static JsonUtil fromObject(Map<String, Object> map) {
		JsonUtil json = new JsonUtil();
		if (map == null)
			return json;
		json.getMap().putAll(map);
		return json;
	}

	private static String getGetter(String property) {
		return "get" + property.substring(0, 1).toUpperCase()
				+ property.substring(1, property.length());
	}

	public Map<String, Object> getMap() {
		return this.jsonMap;
	}
	
	public static Map<String,String> decodeJSONtoMap(String json){
		//{"method":"getCmuCuRegister","ip":"10.10.3.222","port":12345,"id":"11112345345345456"};
		Map<String,String> map = new HashMap<String,String>();
		if (json.startsWith("{") && json.endsWith("}")) {
			json = json.substring(1,json.length() - 1);
			String[] allNodes = json.split(",");
			for (String node : allNodes) {
				String[] nd = node.split(":");
				// 以下四行代码 added by LuoChao 2013.02.27
				String key = nd[0];
				//处理这两类情况：1、“2013-02-27 10:43:24”；2、；http://127.0.0.1:8080/nms/index.do
				String value = (nd.length == 4) ? (nd[1] + ":" + nd[2] + ":" + nd[3]) : nd[1];
				key = key.replace("\"", "").replace("\"", "").replace("\'", "").replace("\'", "");
				value = value.replace("\"", "").replace("\"", "").replace("\'", "").replace("\'", "");
				
				map.put(key, value);
			}
		} else {
			throw new RuntimeException("json formate error...");
		}
		return map;
	}
	
	/**
	 * 处理Json 中的特殊字符 比如回车换行 双引号等（单引号不作处理）
	 * @param s
	 * @return
	 */
	private static String formateJson(String s) {
		 StringBuffer sb = new StringBuffer();     
		 for (int i = 0; i < s.length(); i++) {
			 
			 char c = s.charAt(i);     
			 switch (c) {
			 	case'\"':     //双引号
			 		sb.append("\\\"");     
			 		break;     
			 	case'/':      //左斜杠
			 		sb.append("\\/");     
			 		break;     
			 	case'\b':      //退格
			 		sb.append("\\b");     
			 		break;     
			 	case'\f':      //走纸换页
			 		sb.append("\\f");     
			 		break;     
			 	case'\n':     
			 		sb.append("\\n");//换行    
			 		break;     
			 	case'\r':      //回车
			 		sb.append("\\r");     
			 		break;     
			 	case'\t':      //横向跳格
			 		sb.append("\\t");     
			 		break;     
			 	default:     
			 		sb.append(c);    
			 }
		 }
		 return sb.toString();
	}
	
	/**
	 * json(json每个元素都是一个新的json)转换为jsonToJsonArray(array的每一个元素都是一个json) added by LuoChao 2013.2.28
	 */
	public static String[] jsonToJsonArray(String json) {
		try {
			String[] jsonList = json.split("\\},\\{");
			if (jsonList.length > 1) {
				for (int i = 0; i < jsonList.length; i++) {
					if (i == 0) {
						jsonList[i] = jsonList[i].substring(1, jsonList[i]
								.length());
						jsonList[i] = jsonList[i] + "}";
					} else if (i == (jsonList.length - 1)) {
						jsonList[i] = jsonList[i].substring(0, jsonList[i]
								.length() - 1);
						jsonList[i] = "{" + jsonList[i];
					} else if (i != 0 && i != (jsonList.length - 1)) {
						jsonList[i] = "{" + jsonList[i] + "}";
					}
				}
			} else if (jsonList.length == 1) {
				jsonList[0] = jsonList[0]
						.substring(1, jsonList[0].length() - 1);
			}
			return jsonList;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
