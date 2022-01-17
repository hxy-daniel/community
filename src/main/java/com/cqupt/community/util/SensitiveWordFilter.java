package com.cqupt.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 敏感词过滤
 */
@Component
public class SensitiveWordFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveWordFilter.class);
    /**
     * 前缀树
     */
    private TreeNode root = new TreeNode();

    private final String REPLACESTR = "***";

    /**
     * 构造方法执行后进行前缀树初始化
     */
    @PostConstruct
    private void init() {
        // 获取敏感词
        try (
                InputStream inputStream = SensitiveWordFilter.class.getClassLoader().getResourceAsStream("sensitiveword.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        ){
            String sWord;
            while ((sWord = reader.readLine()) != null) {
                addSensitiveWord(sWord);
            }
        } catch (IOException e) {
            logger.error("获取敏感词出错：" + e);
        }
        // 构造前缀树
    }

    /**
     * 向前缀树中添加敏感词
     * @param sensitiveWord 敏感词
     */
    private void addSensitiveWord(String sensitiveWord) {
        TreeNode node = root;
        if (StringUtils.isBlank(sensitiveWord)) {
            return;
        }
        for (int i = 0; i < sensitiveWord.length(); i++) {
            char c = sensitiveWord.charAt(i);
            TreeNode subNode = node.getSubNode(c);
            if (subNode == null) {
                subNode = new TreeNode();
                node.addSubNode(c, subNode);
            }

            node = subNode;

            if (i == sensitiveWord.length() - 1) {
                subNode.setSensitiveWordEnd(true);
            }
        }
    }

    /**
     * 敏感词过滤
     * @param text 待过滤字符串
     * @return 过滤后的字符串
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        int start = 0;
        int end = 0;
        TreeNode node = root;
        // 存放结果字符串
        StringBuilder sb = new StringBuilder();

        while (end < text.length()) {
            // 注意是获取end而不是start
            char c = text.charAt(end);
            // 处理符号情况
            if (isSymbol(c)) {
                if (node == root) {
                    sb.append(c);
                    start++;
                }
                end++;
                continue;
            }

            // 注意这里是上面的node = 子node，而不是tempNode
            // 检查下级节点
            node = node.getSubNode(c);
            // 以begin开头的字符串不是敏感词
            if(node == null) {
                // 注意是添加start处的字符
                // 以start开头的字符串不是敏感词
                sb.append(text.charAt(start));
                // 处理下一个字符
                start++;
                end = start;
                // 重新指向根节点
                node = root;
            } else if (node.isSensitiveWordEnd()) {
                // 发现敏感词,将start-end字符串替换掉
                sb.append(REPLACESTR);
                end++;
                start = end;
                node = root;
            } else {
                // 检查下一个字符
                end++;
            }
        }

        // 将最后一批字符计入结果
        sb.append(text.substring(start));

        return sb.toString();
    }

    /**
     * 判断某字符是否为符号
     * @param c
     * @return
     */
    private boolean isSymbol(Character c) {
        // 0x2E80~0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }


    /**
     * 前缀树节点
     */
    class TreeNode {
        /**
         * 敏感词结尾
         */
        private boolean isSensitiveWordEnd = false;
        /**
         * 子节点
         */
        private Map<Character, TreeNode> subNodes = new HashMap<>();

        public void addSubNode(Character c, TreeNode node) {
            subNodes.put(c, node);
        }

        public boolean isSensitiveWordEnd() {
            return isSensitiveWordEnd;
        }

        public TreeNode getSubNode(Character c) {
            return subNodes.get(c);
        }

        public void setSensitiveWordEnd(boolean sensitiveWordEnd) {
            isSensitiveWordEnd = sensitiveWordEnd;
        }
    }
}
