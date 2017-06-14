package com.huinong.truffle.payment.order.mono.util;


import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class ParamUtil {
	/**
	 * @param dto
	 */
	public static Map<String, Object> putParam(Object dto) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
			if (dto != null) {
				Field[] fields = dto.getClass().getDeclaredFields();
				if (fields != null) {
					for (Field f : fields) {
						String property = f.getName();
						if (property != null) {
							Method getMethod;
							try {
								// getMethod =
								// bean.getClass().getDeclaredMethod(property);
								PropertyDescriptor pdp = new PropertyDescriptor(property, dto.getClass());
								getMethod = pdp.getReadMethod();
								if (getMethod != null) {
									returnMap.put(property, getMethod.invoke(dto));
								}
							} catch (Exception e) {
								continue;
							}

						}
					}
				}

				Class<?> superClass = dto.getClass().getSuperclass();
				fields = superClass.getDeclaredFields();
				if (fields != null) {
					for (Field f : fields) {
						String property = f.getName();
						if (property != null) {
							Method getMethod;
							try {
								// getMethod =
								// bean.getClass().getDeclaredMethod(property);
								PropertyDescriptor pdp = new PropertyDescriptor(property, dto.getClass());
								getMethod = pdp.getReadMethod();
								if (getMethod != null) {
									returnMap.put(property, getMethod.invoke(dto));
								}
							} catch (Exception e) {
								continue;
							}

						}
					}
				}
			}
		} catch (Exception e) {
            e.printStackTrace();
		}
		return returnMap;
	}
	
	/**
     * 将DTO里的属性值反射到MAP参数列表中
     * 
     * @param param
     * @param dto
     */
    public static Map<String, String> putParamStr(Object dto) {
        Map<String, String> param = new HashMap<String, String>();
        try {
            if (dto != null) {
                for (Field field : dto.getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    param.put(field.getName(), field.get(dto)==null?null:field.get(dto).toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return param;
    }

	public static int getTotalPages(int dataCount, int pageSize) {
		int totalPages = dataCount / pageSize;
		totalPages += dataCount % pageSize > 0 ? 1 : 0;
		return totalPages;
	}
	
	/**
	 * @param parameterMap
	 * @param is
	 * @return
	 */
    public static Map<String, String> getParameter2Map(Map<String, String[]> parameterMap) {
        Map<String, String> params = new TreeMap<String, String>();
        for (Iterator iter = parameterMap.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = parameterMap.get(name);
            String valueStr = "";
            for (int i = 0, len = values.length; i < len; i++) {
                valueStr += (i == len - 1) ? values[i] : values[i] + ",";
            }
            // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }
        return params;
    }
}
