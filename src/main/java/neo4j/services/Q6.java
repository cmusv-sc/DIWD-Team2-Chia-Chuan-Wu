package neo4j.services;

import neo4j.json.Graph;
import neo4j.json.Node;
import neo4j.json.Relationship;
import util.LRUCache;
import util.MapUtil;
import util.Rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by tongtongbao on 12/7/15.
 */
public class Q6 {
    LRUCache<String, Map<String, Object>> cache = new LRUCache(100);

    public Map<String, Object> parse(String centralAuthor, int hop) {
        if (cache.containsKey(centralAuthor + "_" + hop)) return cache.get(centralAuthor + "_" + hop);
        String query = String.format("MATCH p = (bacon:Author {name:\\\"%s\\\"})-[*1..%d]-(another:Author) RETURN p", centralAuthor, hop);
        System.out.println(query);
        Map<String, Object> map = toMap(centralAuthor, query);
        cache.put(centralAuthor + "_" + hop, map);
        return map;
    }

    private java.util.Map<String, Object> toMap(String centralAuthor, String query) {
        Graph g = Rest.query(query);
        List<Map<String, Object>> nodes = new ArrayList<>();
        List<java.util.Map<String, Object>> rels = new ArrayList<>();

        for (Node node : g.getNodes()) {
            if (node.getLabels().get(0).equals("Paper"))
                nodes.add(MapUtil.map5("id", node.getId(), "label", node.getProperties().getTitle(), "cluster", "1", "value", 2, "group", "paper"));
            else if (node.getProperties().getName().equals(centralAuthor))
                nodes.add(MapUtil.map6("id", node.getId(), "label", node.getProperties().getName(), "cluster", "2", "value", 1, "group", "author", "color", "red"));
            else
                nodes.add(MapUtil.map5("id", node.getId(), "label", node.getProperties().getName(), "cluster", "2", "value", 1, "group", "author"));
        }

        for (Relationship relationship : g.getRelationships()) {
            rels.add(MapUtil.map3("from", relationship.getStartNode(), "to", relationship.getEndNode(), "title", "PUBLISH"));
        }

        return MapUtil.map("nodes", nodes, "edges", rels);
    }
}
