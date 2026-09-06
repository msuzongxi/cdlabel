package edu.cgdi.labeler.service;

import edu.cgdi.labeler.mapper.OntologyMapper;
import edu.cgdi.labeler.model.AttributeDef;
import edu.cgdi.labeler.model.DimensionDef;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OntologyService {

    private final OntologyMapper ontologyMapper;

    public OntologyService(OntologyMapper ontologyMapper) {
        this.ontologyMapper = ontologyMapper;
    }

    public List<DimensionDef> getOntology() {
        List<DimensionDef> dimensions = ontologyMapper.findDimensions();
        List<AttributeDef> attributes = ontologyMapper.findAttributes();

        Map<String, List<AttributeDef>> byDimension = attributes.stream()
            .collect(Collectors.groupingBy(AttributeDef::getDimensionCode));

        dimensions.forEach(d ->
            d.setAttributes(byDimension.getOrDefault(d.getCode(), List.of()))
        );

        return dimensions;
    }

    public void assertDimension(String code) {
        if (code == null || ontologyMapper.dimensionExists(code) != 1) {
            throw new IllegalArgumentException("Unknown dimension: " + code);
        }
    }

    public void assertAttribute(String attributeCode, String dimensionCode) {
        if (attributeCode == null
            || ontologyMapper.attributeBelongsToDimension(attributeCode, dimensionCode) != 1) {
            throw new IllegalArgumentException(
                "Attribute " + attributeCode + " does not belong to " + dimensionCode
            );
        }
    }
}
