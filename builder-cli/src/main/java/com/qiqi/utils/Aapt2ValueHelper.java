package com.qiqi.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Aapt2ValueHelper {

    private Set<String> mFilePath = new HashSet<>();

    public void parserXml(String path) {
        try {
            mFilePath.add(path);
            parserXmlIn(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeXml() {
        String res = BuildUtils.getResourcesPath() + "\\apk\\res\\values";
        String newRes = BuildUtils.getResourcesPath() + "\\apk\\new_res\\values";
        FileUtil.ensumeDir(new File(newRes));
        if (FileUtil.dirExists(res)) {
            File resFile = new File(res);
            for (File file : resFile.listFiles()) {//修改原来的值
                try {
                    changeXml(file.getAbsolutePath(), newRes + "\\" + file.getName(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        for (String path : mFilePath) {//新增的值
            File file = new File(path);
            try {
                changeXml(file.getAbsolutePath(), newRes + "\\new_" + MD5Util.getMd5(file.getAbsolutePath()) + ".xml", false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Map<String, Node> mUpdateData = new HashMap<>();
    private Set<String> mWipeData = new HashSet<>();

    private List<String> mPathList = new ArrayList<>();

    public List<String> getPathList() {
        return mPathList;
    }

    //判断需要修改或删除
    private void parserXmlIn(String path) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(path));
        Element root = document.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Node attribute = node.getAttributes().getNamedItem("name");
                if (checkNeedUpdate(node)) {
                    mUpdateData.put(node.getNodeName() + "@" + attribute.getNodeValue(), node);
                } else {
                    mWipeData.add(node.getNodeName() + "@" + attribute.getNodeValue());
                }
            }
        }
//        System.out.println("解析完毕 " + path);
    }

    /**
     * 去重不支持的数据标签
     * @param item
     * @return
     */
    private boolean checkNeedUpdate(Node item) {
        if (item != null && item.getChildNodes() != null && item.getChildNodes().getLength() > 0) {
            NodeList nodeList = item.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if ("xliff:g".equals(node.getNodeName())) {
                    return false;
                }
            }
        }
        if (item != null && item.getAttributes() != null && item.getAttributes().getLength() > 0) {
            NamedNodeMap attList = item.getAttributes();
            for (int i = 0; i < attList.getLength(); i++) {
                Node attribute = attList.item(i);
                if (attribute.getNodeName() != null && attribute.getNodeName().contains("tools")) {
                    return false;
                }
            }
        }
        return true;
    }

    public void changeXml(String path, String newPath, boolean isUpdate) throws Exception {
        boolean hasChange = false;
        File file = new File(path);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);
        Element root = document.getDocumentElement();
        root.removeAttribute("xmlns:xliff");//处理xmlns:xliff  Xliff是XML Localization Interchange File Format
        root.removeAttribute("xmlns:tools");//处理xmlns:tools
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Node attribute = node.getAttributes().getNamedItem("name");

                String key = node.getNodeName() + "@" + attribute.getNodeValue();
                if (isUpdate) {
                    Node newNode = mUpdateData.get(key);
                    if (newNode != null) {
//                        System.out.println("修改：" + file.getName() + " " + key);
                        root.removeChild(node);
                        mWipeData.add(key);
                        root.appendChild(document.importNode(newNode, true));
                        hasChange = true;
                    }
                } else {
                    if (mWipeData.contains(key)) {
//                        System.out.println("移除：" + file.getName() + " " + key);
                        root.removeChild(node);
                        hasChange = true;
                    }
                }
            }
//            if (!isUpdate){
//                System.out.println("all " + node.getNodeType() + " " + node.getClass() + " " + node.toString());
//            }
        }
        if (hasChange) {
            writeFile(document, newPath);
            mPathList.add(newPath);

            if (!isUpdate) {
                try {
                    rewrite(newPath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Log.i("修改完毕" + path);
        }
    }

    private void rewrite(String newPath) {
        List<String> lines = FileUtil.getStrings(newPath);
        List<String> newLines = new ArrayList<>();
        for (String l : lines) {
            if (!l.trim().startsWith("//") && !l.trim().equals("")) {//去掉注释
//                System.out.println(l);
                newLines.add(l);
            }
        }
        FileUtil.writeFile(newLines, newPath);
    }

    /**
     * 写入文件
     */
    private void writeFile(Document document, String path) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        PrintWriter printWriter = new PrintWriter(new FileOutputStream(new File(path)));
        StreamResult result = new StreamResult(printWriter);
        DOMSource source = new DOMSource(document);

        transformer.transform(source, result);
    }

}
