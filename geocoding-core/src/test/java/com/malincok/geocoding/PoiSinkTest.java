package com.malincok.geocoding;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.container.v0_6.NodeContainer;
import org.openstreetmap.osmosis.core.container.v0_6.WayContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.*;

import java.util.*;
import java.util.stream.Collectors;

public class PoiSinkTest {

    private PoiSink poiSink;

    @Before
    public void setUp() {
        poiSink = new PoiSink();
    }

    @Test
    public void addNodeTest() {
        EntityContainer node = createNode(1, 48.00001, 21.00001, Map.of(
                "name", "batman",
                "name:cs", "batman",
                "name:it", "bat-man",
                "old_name", "dark knight"
        ));
        poiSink.process(node);
        List<Poi> res = poiSink.getPois();
        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
        checkResult(node, res.get(0), Set.of("batman", "bat-man", "dark knight"));
    }

    @Test
    public void skipNodeTest() {
        EntityContainer node = createNode(1, 48.00001, 21.00001, null);
        poiSink.process(node);
        List<Poi> res = poiSink.getPois();
        Assert.assertNotNull(res);
        Assert.assertTrue(res.isEmpty());
    }

    @Test
    public void skipWayTest() {
        EntityContainer way = createWay(1, 1, 48.00001, 21.00001);
        poiSink.process(way);
        List<Poi> res = poiSink.getPois();
        Assert.assertNotNull(res);
        Assert.assertTrue(res.isEmpty());
    }

    @Test
    public void test() {
        List<EntityContainer> input = Arrays.asList(
                createNode(5, 48.00005, 21.00005, Map.of(
                        "name", "",
                        "name:cs", "gotham"
                )),
                createNode(6, 48.00006, 21.00006, Map.of(
                        "name", "",
                        "old_name", "   "
                )),
                createWay(7, 1, 48.00007, 21.00007),
                createNode(8, 48.00008, 21.00008, Map.of(
                        "name", "robin",
                        "name:cs", "robin",
                        "old_name", "robin"
                )),
                createWay(9, 2, 48.00009, 21.00009),
                createNode(10, 48.00010, 21.00010, Map.of(
                        "name", "  batman",
                        "name:cs", "batman\t",
                        "old_name", "\ndark knight  "
                )),
                createNode(11, 48.00011, 21.00011, Map.of(
                        "name", "\n\t",
                        "name:cs", "\n"
                )),
                createNode(12, 48.00012, 21.00012, Map.of())
        );
        input.forEach(entityContainer -> poiSink.process(entityContainer));

        List<Poi> res = poiSink.getPois();
        Assert.assertNotNull(res);
        Assert.assertEquals(3, res.size());
        checkResult(input.get(0), res.get(0), Set.of("gotham"));
        checkResult(input.get(3), res.get(1), Set.of("robin"));
        checkResult(input.get(5), res.get(2), Set.of("batman", "dark knight"));
    }

    private void checkResult(EntityContainer input, Poi result, Set<String> expectedNames) {
        Assert.assertEquals(EntityType.Node, input.getEntity().getType());
        Node inputNode = (Node) input.getEntity();
        Assert.assertEquals(inputNode.getId(), result.getId());
        Assert.assertEquals(inputNode.getLatitude(), result.getLat(), 0);
        Assert.assertEquals(inputNode.getLongitude(), result.getLon(), 0);
        Map<String, String> tags = inputNode.getTags().stream()
                .collect(Collectors.toMap(Tag::getKey, Tag::getValue));
        Assert.assertEquals(tags.size(), result.getTagCount());
        Assert.assertEquals(expectedNames,result.getNames());
    }

    private EntityContainer createNode(long id, double lat, double lon, Map<String, String> names) {
        CommonEntityData commonEntityData = new CommonEntityData(id, 0, new Date(), OsmUser.NONE, 0);
        Node node = new Node(commonEntityData, lat, lon);
        node.getTags().add(new Tag("who_cares", "notMe"));
        if (names != null) {
            for (Map.Entry<String, String> name : names.entrySet()) {
                node.getTags().add(new Tag(name.getKey(), name.getValue()));
            }
        }
        return new NodeContainer(node);
    }

    private EntityContainer createWay(long wayId, long nodeId, double lat, double lon) {
        CommonEntityData commonEntityData = new CommonEntityData(wayId, 0, new Date(), OsmUser.NONE, 0);
        Way way = new Way(commonEntityData);
        way.getWayNodes().add(new WayNode(nodeId, lat, lon));
        return new WayContainer(way);
    }
}
