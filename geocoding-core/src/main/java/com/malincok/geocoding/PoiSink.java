package com.malincok.geocoding;

import lombok.Getter;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Sink that extracts named OSM nodes into POI instances
 */
@Getter
public class PoiSink implements Sink {
    private final List<Poi> pois = new ArrayList<>();

    /**
     * Filters nodes with valid name tags and converts them into POIs, prioritizing the primary 'name' tag.
     * @param entityContainer container with OSM data
     */
    @Override
    public void process(EntityContainer entityContainer) {
        if (!(entityContainer.getEntity() instanceof Node node)) {
            return;
        }

        Set<String> names = node.getTags().stream()
                .filter(tag -> isNameTag(tag.getKey()))
                .sorted(Comparator.comparing(tag -> !"name".equals(tag.getKey())))
                .map(Tag::getValue)
                .filter(name -> name != null && !name.isBlank())
                .map(String::trim)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (!names.isEmpty()) {
            pois.add(new Poi(
                    node.getId(),
                    node.getLatitude(),
                    node.getLongitude(),
                    node.getTimestamp(),
                    names,
                    node.getTags().size()
            ));
        }
    }

    @Override
    public void initialize(Map<String, Object> map) {
    }

    @Override
    public void complete() {
    }

    @Override
    public void close() {
    }

    private boolean isNameTag(String key) {
        return "name".equals(key) || "old_name".equals(key) || key.startsWith("name:") || key.startsWith("old_name:");
    }
}
