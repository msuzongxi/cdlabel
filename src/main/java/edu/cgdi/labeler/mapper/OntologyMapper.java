package edu.cgdi.labeler.mapper;

import edu.cgdi.labeler.model.AttributeDef;
import edu.cgdi.labeler.model.DimensionDef;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OntologyMapper {

    @Select("""
        SELECT code, aspect, name, question, definition, positive_example, negative_example, boundary_note, display_order
        FROM dimension_def
        WHERE active = TRUE
        ORDER BY display_order
        """)
    List<DimensionDef> findDimensions();

    @Select("""
        SELECT code, dimension_code, label, meaning, positive_example, negative_example, display_order
        FROM attribute_def
        WHERE active = TRUE
        ORDER BY dimension_code, display_order
        """)
    List<AttributeDef> findAttributes();

    @Select("""
        SELECT COUNT(*)
        FROM dimension_def
        WHERE code = #{code}
          AND active = TRUE
        """)
    int dimensionExists(@Param("code") String code);

    @Select("""
        SELECT COUNT(*)
        FROM attribute_def
        WHERE code = #{attributeCode}
          AND dimension_code = #{dimensionCode}
          AND active = TRUE
        """)
    int attributeBelongsToDimension(
        @Param("attributeCode") String attributeCode,
        @Param("dimensionCode") String dimensionCode
    );
}
