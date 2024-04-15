package com.test.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.*;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
public class JsonXmlUtils {

    /**
     * 解决 CDATA 问题
     */
    private static Pattern pattern = Pattern.compile("[<>&\"',]");
    private static String escape(String string) {
        return pattern.matcher(string).find() ? "<![CDATA[" + string + "]]>" : string;
    }

    public static boolean isEmpty(String str) {

        if (str == null || str.trim().isEmpty() || "null".equals(str)) {
            return true;
        }
        return false;
    }

    /**
     * updateXml 处理xml头，以及根标签
     */
    private static String updateXml(String xmlStr) {
        xmlStr = xmlStr.trim();
        if (StringUtils.isBlank(xmlStr)) {
            return xmlStr;
        }

        // 过滤非法字符
        xmlStr = xmlStr.replaceAll("[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x1f]", "");

        StringBuilder xmlSb = new StringBuilder(xmlStr);
        if (!xmlStr.startsWith("<?")) {
            xmlSb.insert(0, "<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        }

        int idx = xmlSb.indexOf("?>") + 2;
        xmlSb.insert(idx, "<root_chinadaas>").append("</root_chinadaas>");
        return xmlSb.toString();
    }


    /**
     * xml转json的核心循环方法
     *
     * @param element
     * @param json
     */
    private static void dom4jToJson(Element element, JSONObject json) {
        // 如果是属性
        for (Object o : element.attributes()) {
            Attribute attr = (Attribute) o;
            if (!isEmpty(attr.getValue())) {
                json.put("@" + attr.getName(), attr.getValue());
            }
        }
        List<Element> chdEl = element.elements();
        if (chdEl.isEmpty() && !isEmpty(element.getText())) { // 如果没有子元素,只有一个值
            json.put(element.getName(), element.getText());
        }

        for (Element e : chdEl) { // 有子元素
            if (!e.elements().isEmpty() || e.attributes().size() > 0) { // 子元素也有子元素,
                JSONObject chdjson = new JSONObject();
                dom4jToJson(e, chdjson);
                Object o = json.get(e.getName());
                if (o != null) {
                    JSONArray jsona = null;
                    if (o instanceof JSONObject) { // 如果此元素已存在,则转为jsonArray
                        JSONObject jsono = (JSONObject) o;
                        json.remove(e.getName());
                        jsona = new JSONArray();
                        jsona.add(jsono);
                        jsona.add(chdjson);
                    }
                    if (o instanceof JSONArray) {
                        jsona = (JSONArray) o;
                        jsona.add(chdjson);
                    }
                    json.put(e.getName(), jsona);
                } else {
                    if (!chdjson.isEmpty()) {
                        json.put(e.getName(), chdjson);
                    }
                }
            } else { // 子元素没有子元素
                for (Object o : element.attributes()) {
                    Attribute attr = (Attribute) o;
                    if (!isEmpty(attr.getValue())) {
                        json.put("@" + attr.getName(), attr.getValue());
                    }
                }
                if (!e.getText().isEmpty()) {
                    json.put(e.getName(), e.getText());
                }
            }
        }
    }

    /**
     * xml转Json调用
     * @throws DocumentException
     */
    public static JSONObject xmlToJson(String xmlStr) throws DocumentException {
        xmlStr = updateXml(xmlStr);
        Document doc = DocumentHelper.parseText(xmlStr);
        JSONObject json = new JSONObject();
        dom4jToJson(doc.getRootElement(), json);
        return json;
    }


    /**
     * 根据at号获取所有的属性list
     */
    public static JSONObject getAttribute(JSONObject jdata) {
        JSONObject rdata = new JSONObject();
        try {
            Set<Map.Entry<String, Object>> setdata = jdata.entrySet();
            for (Iterator<Map.Entry<String, Object>> it = setdata.iterator(); it.hasNext();) {
                Map.Entry<String, Object> en = it.next();
                if(en.getKey().startsWith("@")){
                    rdata.put(en.getKey().substring(1),escape((String) en.getValue()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rdata;
    }

    /**
     * Json to xmlstr 核心循环方法
     */
    public static String jsonToXmlstr(JSONObject jObj, StringBuffer buffer) {
        Set<Map.Entry<String, Object>> se = jObj.entrySet();
        for (Iterator<Map.Entry<String, Object>> it = se.iterator(); it.hasNext();) {
            Map.Entry<String, Object> en = it.next();
            if (en.getValue().getClass().getName().equals("com.alibaba.fastjson.JSONObject")) {
                JSONObject jo = jObj.getJSONObject(en.getKey());
                buffer.append("<" + en.getKey());

                //处理xml标签的属性值
                JSONObject attrlist = getAttribute(jo);
                Set<Map.Entry<String, Object>> attr = attrlist.entrySet();
                for (Iterator<Map.Entry<String, Object>> mt = attr.iterator(); mt.hasNext();) {
                    Map.Entry<String, Object> amap = mt.next();
                    buffer.append(" " + amap.getKey() + "=\""+escape((String) amap.getValue()) + "\"");
                }
                buffer.append(">");
                if(jo.containsKey(en.getKey())){
                    //标签直接有值的话，直接赋值
                    buffer.append(jo.getString(en.getKey()));
                }else{
                    jsonToXmlstr(jo, buffer);
                }
                buffer.append("</" + en.getKey() + ">");
            } else if (en.getValue().getClass().getName().equals("com.alibaba.fastjson.JSONArray")) {
                JSONArray jarray = jObj.getJSONArray(en.getKey());
                for (int i = 0; i < jarray.size(); i++) {
                    buffer.append("<" + en.getKey());
                    JSONObject jsonobject = jarray.getJSONObject(i);

                    //处理xml标签的属性值
                    JSONObject attrlist = getAttribute(jsonobject);
                    Set<Map.Entry<String, Object>> attr = attrlist.entrySet();
                    for (Iterator<Map.Entry<String, Object>> mt = attr.iterator(); mt.hasNext();) {
                        Map.Entry<String, Object> amap = mt.next();
                        buffer.append(" " + amap.getKey() + "=\""+escape((String) amap.getValue()) + "\"");
                    }
                    buffer.append(">");

                    jsonToXmlstr(jsonobject, buffer);
                    buffer.append("</" + en.getKey() + ">");
                }
            } else if (en.getValue().getClass().getName().equals("java.lang.String")) {
                if(!en.getKey().startsWith("@")){//@号的是属性，在前面已经处理过，这里就不处理了
                    buffer.append("<" + en.getKey() + ">" + escape((String) en.getValue()));
                    buffer.append("</" + en.getKey() + ">");
                }
            }
        }
        return buffer.toString();
    }

    /**
     * json转xml调用
     *
     * @param json
     * @return java.lang.String
     */
    public static String jsonToXml(String json) {
        try {
            StringBuffer buffer = new StringBuffer();
            buffer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            JSONObject jObj = JSON.parseObject(json);
            jsonToXmlstr(jObj, buffer);
            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
