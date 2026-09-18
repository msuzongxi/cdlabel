package edu.cgdi.labeler.mapper;

import edu.cgdi.labeler.model.AssignableUser;
import edu.cgdi.labeler.model.ExportAnnotationRow;
import edu.cgdi.labeler.model.ExportAttributeRow;
import edu.cgdi.labeler.model.ExportDimensionRow;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ExportMapper {

    @Select("""
        SELECT DISTINCT u.id, u.username, u.role
        FROM app_user u
        JOIN assignment a ON a.labeler_id = u.id
        WHERE u.active = TRUE
          AND a.assignment_type IN ('LABELER', 'SUPERVISOR')
        ORDER BY u.id
        """)
    List<AssignableUser> findExportLabelers();

    @Select("""
        <script>
        SELECT
            CASE
                WHEN LOWER(COALESCE(f.source_path, '')) LIKE '%blind_pilot%'
                  OR LOWER(COALESCE(f.source_path, '')) LIKE '%blind-pilot%' THEN 'BLIND_PILOT'
                WHEN LOWER(COALESCE(f.source_path, '')) LIKE '%formal_validation%'
                  OR LOWER(COALESCE(f.source_path, '')) LIKE '%formal-validation%' THEN 'FORMAL_VALIDATION'
                WHEN LOWER(COALESCE(f.source_path, '')) LIKE '%calibration%' THEN 'CALIBRATION'
                ELSE 'OTHER'
            END AS dataset_stage,
            a.batch_code,
            f.id AS filing_id,
            f.filename,
            f.master_cik,
            f.company,
            f.fiscal_year,
            p.id AS paragraph_db_id,
            p.paragraph_id,
            p.section_hint,
            p.heading_context,
            p.sort_order,
            p.text,
            u.id AS user_id,
            u.username,
            an.id AS annotation_id,
            an.cyber_nexus,
            an.registrant_attribution,
            an.governance_process,
            an.eligible,
            an.ontology_gap,
            an.gap_note,
            an.notes,
            an.status,
            an.submitted_at
        FROM annotation an
        JOIN paragraph p ON p.id = an.paragraph_id
        JOIN filing f ON f.id = p.filing_id
        JOIN app_user u ON u.id = an.labeler_id
        JOIN assignment a
          ON a.filing_id = f.id
         AND a.labeler_id = u.id
         AND a.assignment_type IN ('LABELER', 'SUPERVISOR')
        WHERE p.candidate = TRUE
          <if test="labelerId != null">
          AND u.id = #{labelerId}
          </if>
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
          <if test="status != null and status != '' and status != 'ALL'">
          AND an.status = #{status}
          </if>
        ORDER BY f.fiscal_year, f.company, p.sort_order, u.id
        </script>
        """)
    List<ExportAnnotationRow> findAnnotationRows(
        @Param("stage") String stage,
        @Param("labelerId") Long labelerId,
        @Param("status") String status
    );

    @Select("""
        <script>
        SELECT
            ad.annotation_id,
            ad.dimension_code,
            ad.evidence_text,
            ad.evidence_start,
            ad.evidence_end,
            ad.confidence,
            ad.ambiguity_note
        FROM annotation_dimension ad
        JOIN annotation an ON an.id = ad.annotation_id
        JOIN paragraph p ON p.id = an.paragraph_id
        JOIN filing f ON f.id = p.filing_id
        JOIN app_user u ON u.id = an.labeler_id
        JOIN assignment a
          ON a.filing_id = f.id
         AND a.labeler_id = u.id
         AND a.assignment_type IN ('LABELER', 'SUPERVISOR')
        WHERE p.candidate = TRUE
          <if test="labelerId != null">
          AND u.id = #{labelerId}
          </if>
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
          <if test="status != null and status != '' and status != 'ALL'">
          AND an.status = #{status}
          </if>
        ORDER BY ad.annotation_id, ad.dimension_code
        </script>
        """)
    List<ExportDimensionRow> findDimensionRows(
        @Param("stage") String stage,
        @Param("labelerId") Long labelerId,
        @Param("status") String status
    );

    @Select("""
        <script>
        SELECT
            an.id AS annotation_id,
            ad.dimension_code,
            aa.attribute_code,
            aa.present,
            aa.value_text
        FROM annotation_attribute aa
        JOIN annotation_dimension ad ON ad.id = aa.annotation_dimension_id
        JOIN annotation an ON an.id = ad.annotation_id
        JOIN paragraph p ON p.id = an.paragraph_id
        JOIN filing f ON f.id = p.filing_id
        JOIN app_user u ON u.id = an.labeler_id
        JOIN assignment a
          ON a.filing_id = f.id
         AND a.labeler_id = u.id
         AND a.assignment_type IN ('LABELER', 'SUPERVISOR')
        WHERE p.candidate = TRUE
          <if test="labelerId != null">
          AND u.id = #{labelerId}
          </if>
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
          <if test="status != null and status != '' and status != 'ALL'">
          AND an.status = #{status}
          </if>
        ORDER BY an.id, ad.dimension_code, aa.attribute_code
        </script>
        """)
    List<ExportAttributeRow> findAttributeRows(
        @Param("stage") String stage,
        @Param("labelerId") Long labelerId,
        @Param("status") String status
    );
}
