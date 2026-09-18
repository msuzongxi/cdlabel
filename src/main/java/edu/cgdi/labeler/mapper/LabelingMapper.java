package edu.cgdi.labeler.mapper;

import edu.cgdi.labeler.model.AnnotationAttributeRecord;
import edu.cgdi.labeler.model.AnnotationDimensionRecord;
import edu.cgdi.labeler.model.AnnotationRecord;
import edu.cgdi.labeler.model.FilingSummary;
import edu.cgdi.labeler.model.ParagraphListItem;
import edu.cgdi.labeler.model.ParagraphTask;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface LabelingMapper {

    @Select("""
        SELECT
            f.id AS filing_id,
            f.company,
            f.fiscal_year,
            f.filename,
            f.master_cik,
            COUNT(p.id) FILTER (WHERE p.candidate = TRUE)::INTEGER AS total_count,
            COUNT(p.id) FILTER (WHERE p.candidate = TRUE AND an.id IS NULL)::INTEGER AS new_count,
            COUNT(p.id) FILTER (WHERE p.candidate = TRUE AND an.status = 'MODIFIED')::INTEGER AS modified_count,
            COUNT(p.id) FILTER (WHERE p.candidate = TRUE AND an.status = 'SUBMITTED')::INTEGER AS submitted_count
        FROM assignment a
        JOIN app_user u ON u.id = a.labeler_id
        JOIN filing f ON f.id = a.filing_id
        LEFT JOIN paragraph p ON p.filing_id = f.id
        LEFT JOIN annotation an
          ON an.paragraph_id = p.id
         AND an.labeler_id = u.id
        WHERE u.username = #{username}
          AND a.assignment_type IN ('LABELER', 'SUPERVISOR')
        GROUP BY f.id, f.company, f.fiscal_year, f.filename, f.master_cik, a.assignment_order
        ORDER BY a.assignment_order, f.fiscal_year, f.company
        """)
    List<FilingSummary> findMyFilings(@Param("username") String username);

    @Select("""
        SELECT
            f.id AS filing_id,
            f.company,
            f.fiscal_year,
            f.filename,
            f.master_cik,
            COUNT(p.id) FILTER (WHERE p.candidate = TRUE)::INTEGER AS total_count,
            COUNT(p.id) FILTER (WHERE p.candidate = TRUE AND an.id IS NULL)::INTEGER AS new_count,
            COUNT(p.id) FILTER (WHERE p.candidate = TRUE AND an.status = 'MODIFIED')::INTEGER AS modified_count,
            COUNT(p.id) FILTER (WHERE p.candidate = TRUE AND an.status = 'SUBMITTED')::INTEGER AS submitted_count
        FROM assignment a
        JOIN app_user u ON u.id = a.labeler_id
        JOIN filing f ON f.id = a.filing_id
        LEFT JOIN paragraph p ON p.filing_id = f.id
        LEFT JOIN annotation an
          ON an.paragraph_id = p.id
         AND an.labeler_id = u.id
        WHERE u.username = #{username}
          AND a.assignment_type IN ('LABELER', 'SUPERVISOR')
          AND f.id = #{filingId}
        GROUP BY f.id, f.company, f.fiscal_year, f.filename, f.master_cik, a.assignment_order
        """)
    FilingSummary findMyFiling(
        @Param("username") String username,
        @Param("filingId") Long filingId
    );

    @Select("""
        SELECT
            p.id,
            p.filing_id,
            p.paragraph_id,
            p.section_hint,
            p.heading_context,
            p.sort_order,
            p.word_count,
            COALESCE(an.status, 'NEW') AS status
        FROM assignment a
        JOIN app_user u ON u.id = a.labeler_id
        JOIN paragraph p ON p.filing_id = a.filing_id
        LEFT JOIN annotation an
          ON an.paragraph_id = p.id
         AND an.labeler_id = u.id
        WHERE u.username = #{username}
          AND a.assignment_type IN ('LABELER', 'SUPERVISOR')
          AND a.filing_id = #{filingId}
          AND p.candidate = TRUE
          AND (
              (#{status} = 'NEW' AND an.id IS NULL)
              OR
              (#{status} <> 'NEW' AND an.status = #{status})
          )
        ORDER BY p.sort_order
        """)
    List<ParagraphListItem> findParagraphsByStatus(
        @Param("username") String username,
        @Param("filingId") Long filingId,
        @Param("status") String status
    );

    @Select("""
        <script>
        SELECT
            p.id,
            p.filing_id,
            p.paragraph_id,
            p.section_hint,
            p.heading_context,
            p.sort_order,
            p.word_count,
            COALESCE(an.status, 'NEW') AS status,
            f.company,
            f.fiscal_year,
            f.filename,
            f.master_cik,
            CASE
                WHEN LOWER(COALESCE(f.source_path, '')) LIKE '%blind_pilot%'
                  OR LOWER(COALESCE(f.source_path, '')) LIKE '%blind-pilot%' THEN 'BLIND_PILOT'
                WHEN LOWER(COALESCE(f.source_path, '')) LIKE '%formal_validation%'
                  OR LOWER(COALESCE(f.source_path, '')) LIKE '%formal-validation%' THEN 'FORMAL_VALIDATION'
                WHEN LOWER(COALESCE(f.source_path, '')) LIKE '%calibration%' THEN 'CALIBRATION'
                ELSE 'OTHER'
            END AS dataset_stage
        FROM assignment a
        JOIN app_user u ON u.id = a.labeler_id
        JOIN filing f ON f.id = a.filing_id
        JOIN paragraph p ON p.filing_id = f.id
        LEFT JOIN annotation an
          ON an.paragraph_id = p.id
         AND an.labeler_id = u.id
        WHERE u.username = #{username}
          AND a.assignment_type IN ('LABELER', 'SUPERVISOR')
          AND p.candidate = TRUE
          AND (
              (#{status} = 'NEW' AND an.id IS NULL)
              OR
              (#{status} &lt;&gt; 'NEW' AND an.status = #{status})
          )
          <if test="stage != null and stage != '' and stage != 'ALL'">
          AND CASE
                WHEN LOWER(COALESCE(f.source_path, '')) LIKE '%blind_pilot%'
                  OR LOWER(COALESCE(f.source_path, '')) LIKE '%blind-pilot%' THEN 'BLIND_PILOT'
                WHEN LOWER(COALESCE(f.source_path, '')) LIKE '%formal_validation%'
                  OR LOWER(COALESCE(f.source_path, '')) LIKE '%formal-validation%' THEN 'FORMAL_VALIDATION'
                WHEN LOWER(COALESCE(f.source_path, '')) LIKE '%calibration%' THEN 'CALIBRATION'
                ELSE 'OTHER'
              END = #{stage}
          </if>
        ORDER BY p.id
        </script>
        """)
    List<ParagraphListItem> findParagraphQueueByStatus(
        @Param("username") String username,
        @Param("status") String status,
        @Param("stage") String stage
    );

    @Select("""
        SELECT
            p.id,
            p.filing_id,
            p.paragraph_id,
            p.text,
            p.section_hint,
            p.heading_context,
            p.sort_order,
            p.candidate,
            p.word_count,
            f.company,
            f.fiscal_year,
            f.filename,
            f.master_cik,
            f.source_path
        FROM assignment a
        JOIN app_user u ON u.id = a.labeler_id
        JOIN filing f ON f.id = a.filing_id
        JOIN paragraph p ON p.filing_id = f.id
        WHERE u.username = #{username}
          AND a.assignment_type IN ('LABELER', 'SUPERVISOR')
          AND f.id = #{filingId}
          AND p.id = #{paragraphDbId}
          AND p.candidate = TRUE
        LIMIT 1
        """)
    ParagraphTask findAccessibleParagraph(
        @Param("username") String username,
        @Param("filingId") Long filingId,
        @Param("paragraphDbId") Long paragraphDbId
    );

    @Select("""
        SELECT
            an.id,
            an.cyber_nexus,
            an.registrant_attribution,
            an.governance_process,
            an.eligible,
            an.ontology_gap,
            an.gap_note,
            an.notes,
            an.source_mode,
            an.status,
            an.updated_at,
            an.submitted_at
        FROM annotation an
        JOIN app_user u ON u.id = an.labeler_id
        WHERE u.username = #{username}
          AND an.paragraph_id = #{paragraphDbId}
        """)
    AnnotationRecord findAnnotation(
        @Param("username") String username,
        @Param("paragraphDbId") Long paragraphDbId
    );

    @Select("""
        SELECT
            id,
            dimension_code,
            evidence_text,
            evidence_start,
            evidence_end,
            confidence,
            ambiguity_note
        FROM annotation_dimension
        WHERE annotation_id = #{annotationId}
        ORDER BY dimension_code
        """)
    List<AnnotationDimensionRecord> findAnnotationDimensions(
        @Param("annotationId") Long annotationId
    );

    @Select("""
        SELECT
            aa.annotation_dimension_id,
            aa.attribute_code,
            aa.present,
            aa.value_text
        FROM annotation_attribute aa
        JOIN annotation_dimension ad ON ad.id = aa.annotation_dimension_id
        WHERE ad.annotation_id = #{annotationId}
        ORDER BY aa.attribute_code
        """)
    List<AnnotationAttributeRecord> findAnnotationAttributes(
        @Param("annotationId") Long annotationId
    );

    @Select(value = """
        INSERT INTO annotation(
            paragraph_id,
            labeler_id,
            cyber_nexus,
            registrant_attribution,
            governance_process,
            eligible,
            ontology_gap,
            gap_note,
            notes,
            source_mode,
            status,
            submitted_at,
            updated_at
        )
        VALUES (
            #{paragraphDbId},
            #{labelerId},
            #{cyberNexus},
            #{registrantAttribution},
            #{governanceProcess},
            #{eligible},
            #{ontologyGap},
            #{gapNote},
            #{notes},
            'QUEUE',
            #{status},
            CASE WHEN #{status} = 'SUBMITTED' THEN NOW() ELSE NULL END,
            NOW()
        )
        ON CONFLICT (paragraph_id, labeler_id)
        DO UPDATE SET
            cyber_nexus = EXCLUDED.cyber_nexus,
            registrant_attribution = EXCLUDED.registrant_attribution,
            governance_process = EXCLUDED.governance_process,
            eligible = EXCLUDED.eligible,
            ontology_gap = EXCLUDED.ontology_gap,
            gap_note = EXCLUDED.gap_note,
            notes = EXCLUDED.notes,
            source_mode = 'QUEUE',
            status = EXCLUDED.status,
            submitted_at = CASE WHEN EXCLUDED.status = 'SUBMITTED' THEN NOW() ELSE NULL END,
            updated_at = NOW()
        RETURNING id
        """, affectData = true)
    Long upsertAnnotation(
        @Param("paragraphDbId") Long paragraphDbId,
        @Param("labelerId") Long labelerId,
        @Param("cyberNexus") String cyberNexus,
        @Param("registrantAttribution") String registrantAttribution,
        @Param("governanceProcess") String governanceProcess,
        @Param("eligible") Boolean eligible,
        @Param("ontologyGap") boolean ontologyGap,
        @Param("gapNote") String gapNote,
        @Param("notes") String notes,
        @Param("status") String status
    );

    @Delete("DELETE FROM annotation_dimension WHERE annotation_id = #{annotationId}")
    int deleteDimensions(@Param("annotationId") Long annotationId);

    @Select(value = """
        INSERT INTO annotation_dimension(
            annotation_id,
            dimension_code,
            evidence_text,
            evidence_start,
            evidence_end,
            confidence,
            ambiguity_note
        )
        VALUES (
            #{annotationId},
            #{dimensionCode},
            #{evidenceText},
            #{evidenceStart},
            #{evidenceEnd},
            #{confidence},
            #{ambiguityNote}
        )
        RETURNING id
        """, affectData = true)
    Long insertDimension(
        @Param("annotationId") Long annotationId,
        @Param("dimensionCode") String dimensionCode,
        @Param("evidenceText") String evidenceText,
        @Param("evidenceStart") Integer evidenceStart,
        @Param("evidenceEnd") Integer evidenceEnd,
        @Param("confidence") String confidence,
        @Param("ambiguityNote") String ambiguityNote
    );

    @Insert("""
        INSERT INTO annotation_attribute(
            annotation_dimension_id,
            attribute_code,
            present,
            value_text
        )
        VALUES (
            #{annotationDimensionId},
            #{attributeCode},
            #{present},
            #{valueText}
        )
        """)
    int insertAttribute(
        @Param("annotationDimensionId") Long annotationDimensionId,
        @Param("attributeCode") String attributeCode,
        @Param("present") boolean present,
        @Param("valueText") String valueText
    );
}
