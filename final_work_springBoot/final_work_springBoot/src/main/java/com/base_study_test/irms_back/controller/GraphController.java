package com.base_study_test.irms_back.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.web.bind.annotation.*;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;

import java.util.*;

@RestController
@RequestMapping("/graph")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5174", "http://localhost:5173"})

public class GraphController {

    private final Neo4jClient neo4jClient;

    /**
     * 获取完整的知识图谱数据
     */
    @GetMapping("/data")
    public Map<String, Object> getGraphData() {
        try {
            System.out.println("=== 开始查询知识图谱数据 ===");

            List<Map<String, Object>> nodes = new ArrayList<>();
            List<Map<String, Object>> links = new ArrayList<>();
            Set<Long> processedNodes = new HashSet<>();

            // 1. 查询所有节点及其属性
            String nodeQuery = "MATCH (n) RETURN n ORDER BY id(n)";
            System.out.println("执行节点查询: " + nodeQuery);

            Collection<Map<String, Object>> nodeResults = neo4jClient.query(nodeQuery).fetch().all();
            System.out.println("找到节点数量: " + nodeResults.size());

            // 处理所有节点
            for (Map<String, Object> row : nodeResults) {
                Node node = (Node) row.get("n");
                if (node != null && !processedNodes.contains(node.id())) {
                    Map<String, Object> nodeData = createNodeData(node);
                    nodes.add(nodeData);
                    processedNodes.add(node.id());

                    System.out.println("处理节点: " + nodeData.get("name") +
                            " (ID: " + nodeData.get("id") +
                            ", 类别: " + nodeData.get("category") + ")");
                }
            }

            // 2. 查询所有关系
            String relationshipQuery = "MATCH (n)-[r]->(m) RETURN n, r, m ORDER BY id(r)";
            System.out.println("执行关系查询: " + relationshipQuery);

            Collection<Map<String, Object>> relationshipResults = neo4jClient.query(relationshipQuery).fetch().all();
            System.out.println("找到关系数量: " + relationshipResults.size());

            // 处理所有关系
            for (Map<String, Object> row : relationshipResults) {
                Node sourceNode = (Node) row.get("n");
                Node targetNode = (Node) row.get("m");
                Relationship relationship = (Relationship) row.get("r");

                if (sourceNode != null && targetNode != null && relationship != null) {
                    Map<String, Object> linkData = createLinkData(sourceNode, targetNode, relationship);
                    links.add(linkData);

                    String sourceName = getNodeDisplayName(sourceNode);
                    String targetName = getNodeDisplayName(targetNode);
                    System.out.println("处理关系: " + sourceName + " --[" + relationship.type() + "]--> " + targetName);
                }
            }

            System.out.println("=== 查询完成 ===");
            System.out.println("返回节点数量: " + nodes.size());
            System.out.println("返回关系数量: " + links.size());

            Map<String, Object> result = new HashMap<>();
            result.put("nodes", nodes);
            result.put("links", links);
            result.put("totalNodes", nodes.size());
            result.put("totalLinks", links.size());
            result.put("timestamp", System.currentTimeMillis());

            return result;

        } catch (Exception e) {
            System.err.println("知识图谱查询异常: " + e.getMessage());
            e.printStackTrace();
            return createEmptyResult("查询失败: " + e.getMessage());
        }
    }

    /**
     * 根据类别筛选节点
     */
    @GetMapping("/data/by-category/{category}")
    public Map<String, Object> getGraphDataByCategory(@PathVariable String category) {
        try {
            System.out.println("=== 查询类别为 '" + category + "' 的节点 ===");

            List<Map<String, Object>> nodes = new ArrayList<>();
            List<Map<String, Object>> links = new ArrayList<>();
            Set<Long> processedNodes = new HashSet<>();

            // 查询指定类别的节点
            String nodeQuery = "MATCH (n) WHERE $category IN labels(n) RETURN n ORDER BY id(n)";
            System.out.println("执行节点查询: " + nodeQuery + " (category: " + category + ")");

            Collection<Map<String, Object>> nodeResults = neo4jClient.query(nodeQuery)
                    .bind(category).to("category")
                    .fetch().all();
            System.out.println("找到节点数量: " + nodeResults.size());

            // 处理节点
            for (Map<String, Object> row : nodeResults) {
                Node node = (Node) row.get("n");
                if (node != null && !processedNodes.contains(node.id())) {
                    Map<String, Object> nodeData = createNodeData(node);
                    nodes.add(nodeData);
                    processedNodes.add(node.id());
                }
            }

            // 查询这些节点之间的关系
            String relationshipQuery = "MATCH (n)-[r]->(m) WHERE $category IN labels(n) AND $category IN labels(m) RETURN n, r, m";
            Collection<Map<String, Object>> relationshipResults = neo4jClient.query(relationshipQuery)
                    .bind(category).to("category")
                    .fetch().all();

            // 处理关系
            for (Map<String, Object> row : relationshipResults) {
                Node sourceNode = (Node) row.get("n");
                Node targetNode = (Node) row.get("m");
                Relationship relationship = (Relationship) row.get("r");

                if (sourceNode != null && targetNode != null && relationship != null) {
                    Map<String, Object> linkData = createLinkData(sourceNode, targetNode, relationship);
                    links.add(linkData);
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("nodes", nodes);
            result.put("links", links);
            result.put("category", category);
            result.put("totalNodes", nodes.size());
            result.put("totalLinks", links.size());

            return result;

        } catch (Exception e) {
            System.err.println("按类别查询异常: " + e.getMessage());
            e.printStackTrace();
            return createEmptyResult("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取节点的详细信息
     */
    @GetMapping("/node/{id}")
    public Map<String, Object> getNodeDetails(@PathVariable Long id) {
        try {
            String query = "MATCH (n) WHERE id(n) = $id RETURN n";
            Collection<Map<String, Object>> result = neo4jClient.query(query)
                    .bind(id).to("id")
                    .fetch().all();

            if (result.isEmpty()) {
                return createErrorResult("节点不存在");
            }

            Node node = (Node) result.iterator().next().get("n");
            Map<String, Object> nodeData = createNodeData(node);

            // 添加邻居节点信息
            String neighborsQuery = "MATCH (n)-[r]-(m) WHERE id(n) = $id RETURN DISTINCT m, r";
            Collection<Map<String, Object>> neighbors = neo4jClient.query(neighborsQuery)
                    .bind(id).to("id")
                    .fetch().all();

            List<Map<String, Object>> neighborNodes = new ArrayList<>();
            for (Map<String, Object> neighborRow : neighbors) {
                Node neighborNode = (Node) neighborRow.get("m");
                Relationship relationship = (Relationship) neighborRow.get("r");

                Map<String, Object> neighborData = createNodeData(neighborNode);
                neighborData.put("relationship", relationship.type());
                neighborNodes.add(neighborData);
            }

            nodeData.put("neighbors", neighborNodes);
            nodeData.put("neighborCount", neighborNodes.size());

            return nodeData;
        } catch (Exception e) {
            e.printStackTrace();
            return createErrorResult("查询失败: " + e.getMessage());
        }
    }

    /**
     * 搜索节点
     */
    @GetMapping("/search")
    public Map<String, Object> searchNodes(@RequestParam String keyword) {
        try {
            String query = "MATCH (n) WHERE ANY(prop IN keys(n) WHERE toString(n[prop]) CONTAINS $keyword) RETURN n LIMIT 50";
            Collection<Map<String, Object>> result = neo4jClient.query(query)
                    .bind(keyword).to("keyword")
                    .fetch().all();

            List<Map<String, Object>> nodes = new ArrayList<>();
            for (Map<String, Object> row : result) {
                Node node = (Node) row.get("n");
                Map<String, Object> nodeData = createNodeData(node);
                nodes.add(nodeData);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("nodes", nodes);
            response.put("count", nodes.size());
            response.put("keyword", keyword);

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return createErrorResult("搜索失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有类别
     */
    @GetMapping("/categories")
    public Map<String, Object> getCategories() {
        try {
            String query = "CALL db.labels()";
            Collection<Map<String, Object>> result = neo4jClient.query(query).fetch().all();

            List<String> categories = new ArrayList<>();
            for (Map<String, Object> row : result) {
                String label = (String) row.get("label");
                if (label != null) {
                    categories.add(label);
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("categories", categories);
            response.put("count", categories.size());

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return createErrorResult("获取类别失败: " + e.getMessage());
        }
    }

    /**
     * 获取图谱统计信息
     */
    @GetMapping("/stats")
    public Map<String, Object> getGraphStats() {
        try {
            // 节点统计
            String nodeCountQuery = "MATCH (n) RETURN count(n) as nodeCount";
            Collection<Map<String, Object>> nodeResult = neo4jClient.query(nodeCountQuery).fetch().all();
            long nodeCount = (Long) nodeResult.iterator().next().get("nodeCount");

            // 关系统计
            String linkCountQuery = "MATCH ()-[r]->() RETURN count(r) as linkCount";
            Collection<Map<String, Object>> linkResult = neo4jClient.query(linkCountQuery).fetch().all();
            long linkCount = (Long) linkResult.iterator().next().get("linkCount");

            // 类别统计
            String categoryQuery = "CALL db.labels()";
            Collection<Map<String, Object>> categoryResult = neo4jClient.query(categoryQuery).fetch().all();
            int categoryCount = categoryResult.size();

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalNodes", nodeCount);
            stats.put("totalLinks", linkCount);
            stats.put("totalCategories", categoryCount);
            stats.put("timestamp", System.currentTimeMillis());

            return stats;
        } catch (Exception e) {
            e.printStackTrace();
            return createErrorResult("获取统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 创建节点数据
     */
    private Map<String, Object> createNodeData(Node node) {
        Map<String, Object> nodeData = new HashMap<>();

        // 基本信息
        nodeData.put("id", String.valueOf(node.id()));
        nodeData.put("name", getNodeDisplayName(node));
        nodeData.put("category", getNodeCategory(node));
        nodeData.put("labels", node.labels().toString());

        // 所有属性
        Map<String, Object> properties = node.asMap();
        nodeData.put("properties", properties);

        // 属性统计
        nodeData.put("propertyCount", properties.size());

        // 属性列表（用于前端显示）
        List<Map<String, Object>> propertyList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            Map<String, Object> prop = new HashMap<>();
            prop.put("key", entry.getKey());
            prop.put("value", entry.getValue());
            prop.put("type", entry.getValue() != null ? entry.getValue().getClass().getSimpleName() : "null");
            propertyList.add(prop);
        }
        nodeData.put("propertyList", propertyList);

        return nodeData;
    }

    /**
     * 创建关系数据
     */
    private Map<String, Object> createLinkData(Node sourceNode, Node targetNode, Relationship relationship) {
        Map<String, Object> linkData = new HashMap<>();

        linkData.put("source", String.valueOf(sourceNode.id()));
        linkData.put("target", String.valueOf(targetNode.id()));
        linkData.put("label", relationship.type());
        linkData.put("properties", relationship.asMap());

        // 添加源节点和目标节点的名称（便于前端显示）
        linkData.put("sourceName", getNodeDisplayName(sourceNode));
        linkData.put("targetName", getNodeDisplayName(targetNode));

        return linkData;
    }

    /**
     * 获取节点显示名称
     */
    private String getNodeDisplayName(Node node) {
        Map<String, Object> properties = node.asMap();

        // 按优先级尝试不同的名称属性
        String[] nameFields = {"name", "title", "label", "姓名", "名称", "标题"};
        for (String field : nameFields) {
            if (properties.containsKey(field) && properties.get(field) != null) {
                return properties.get(field).toString();
            }
        }

        // 如果都没有，使用第一个非空属性值
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().toString().trim().isEmpty()) {
                return entry.getValue().toString();
            }
        }

        // 最后使用ID
        return "节点_" + node.id();
    }

    /**
     * 获取节点类别
     */
    private String getNodeCategory(Node node) {
        Iterator<String> labels = node.labels().iterator();
        return labels.hasNext() ? labels.next() : "未知";
    }

    /**
     * 创建错误结果
     */
    private Map<String, Object> createErrorResult(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("error", message);
        result.put("nodes", new ArrayList<>());
        result.put("links", new ArrayList<>());
        result.put("totalNodes", 0);
        result.put("totalLinks", 0);
        return result;
    }

    /**
     * 创建空结果
     */
    private Map<String, Object> createEmptyResult(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("message", message);
        result.put("nodes", new ArrayList<>());
        result.put("links", new ArrayList<>());
        result.put("totalNodes", 0);
        result.put("totalLinks", 0);
        return result;
    }
}


