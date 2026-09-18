package edu.cgdi.labeler.mapper;

import edu.cgdi.labeler.model.AssignableUser;
import edu.cgdi.labeler.model.ReviewAnnotationRow;
import edu.cgdi.labeler.model.ReviewAttributeRow;
import edu.cgdi.labeler.model.ReviewDimensionRow;
import edu.cgdi.labeler.model.ReviewFilingRow;
import edu.cgdi.labeler.model.ReviewParagraphRow;
import edu.cgdi.labeler.model.ReviewQueueParagraphRow;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ReviewMapper {

    @Select("""
        SELECT u.id, u.username, u.role
        FROM assignment a
        JOIN app_user u ON u.id = a.labeler_id
        WHERE a.filing_id = #{filingId}
          AND a.assignment_type IN ('LABELER', 'SUPERVISOR')
          AND u.active = TRUE
        ORDER BY u.id
        """)
    List<AssignableUser> findAssignedLabelers(@Param("filingId") Long filingId);

    @Select("""
        SELECT
            f.id AS filing_id,
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
            END AS dataset_stage,
            (
                SELECT MIN(a2.batch_code)
                FROM assignment a2
                WHERE a2.filing_id = f.id
                  AND a2.assignment_type IN ('LABELER', 'SUPERVISOR')
            ) AS batch_code,
            (
                SELECT COUNT(*)::INTEGER
                FROM paragraph p2
                WHERE p2.filing_id = f.id
                  AND p2.candidate = TRUE
            ) AS total_count,
            (
                SELECT COUNT(*)::INTEGER
                FROM assignment a3
                WHERE a3.filing_id = f.id
                  AND a3.assignment_type IN ('LABELER', 'SUPERVISOR')
            ) AS labeler_count,
            (
                SELECT COUNT(*)::INTEGER
                FROM annotation an
                JOIN paragraph p3 ON p3.id = an.paragraph_id
                JOIN assignment a4
                  ON a4.filing_id = f.id
                 AND a4.labeler_id = an.labeler_id
                 AND a4.assignment_type IN ('LABELER', 'SUPERVISOR')
                WHERE p3.filing_id = f.id
                  AND p3.candidate = TRUE
                  AND an.status = 'SUBMITTED'
            ) AS submitted_count,
            (
                SELECT STRING_AGG(u.username, ', ' ORDER BY u.username)
                FROM assignment a5
                JOIN app_user u ON u.id = a5.labeler_id
                WHERE a5.filing_id = f.id
                  AND a5.assignment_type IN ('LABELER', 'SUPERVISOR')
            ) AS labelers
        FROM filing f
        WHERE EXISTS (
            SELECT 1 FROM assignment ax
            WHERE ax.filing_id = f.id
              AND ax.assignment_type IN ('LABELER', 'SUPERVISOR')
        )
        ORDER BY f.fiscal_year, f.company
        """)
    List<ReviewFilingRow> findReviewFilings();

    @Select("""
        SELECT
            f.id AS filing_id,
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
            END AS dataset_stage,
            (
                SELECT MIN(a2.batch_code)
                FROM assignment a2
                WHERE a2.filing_id = f.id
                  AND a2.assignment_type IN ('LABELER', 'SUPERVISOR')
            ) AS batch_code,
            (
                SELECT COUNT(*)::INTEGER FROM paragraph p2
                WHERE p2.filing_id = f.id AND p2.candidate = TRUE
            ) AS total_count,
            (
                SELECT COUNT(*)::INTEGER FROM assignment a3
                WHERE a3.filing_id = f.id AND a3.assignment_type IN ('LABELER', 'SUPERVISOR')
            ) AS labeler_count,
            (
                SELECT COUNT(*)::INTEGER
                FROM annotation an
                JOIN paragraph p3 ON p3.id = an.paragraph_id
                JOIN assignment a4
                  ON a4.filing_id = f.id
                 AND a4.labeler_id = an.labeler_id
                 AND a4.assignment_type IN ('LABELER', 'SUPERVISOR')
                WHERE p3.filing_id = f.id
                  AND p3.candidate = TRUE
                  AND an.status = 'SUBMITTED'
            ) AS submitted_count,
            (
                SELECT STRING_AGG(u.username, ', ' ORDER BY u.username)
                FROM assignment a5
                JOIN app_user u ON u.id = a5.labeler_id
                WHERE a5.filing_id = f.id
                  AND a5.assignment_type IN ('LABELER', 'SUPERVISOR')
            ) AS labelers
        FROM filing f
        WHERE f.id = #{filingId}
        """)
    ReviewFilingRow findReviewFiling(@Param("filingId") Long filingId);

    @Select("""
        SELECT
            p.id AS paragraph_db_id,
            p.paragraph_id,
            p.section_hint,
            p.heading_context,
            p.text,
            p.sort_order
        FROM paragraph p
        WHERE p.filing_id = #{filingId}
          AND p.candidate = TRUE
        ORDER BY p.sort_order, p.id
        """)
    List<ReviewParagraphRow> findParagraphs(@Param("filingId") Long filingId);

    @Select("""
        SELECT
            p.id AS paragraph_db_id,
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
        FROM paragraph p
        JOIN assignment a
          ON a.filing_id = p.filing_id
         AND a.assignment_type IN ('LABELER', 'SUPERVISOR')
        JOIN app_user u ON u.id = a.labeler_id
        LEFT JOIN annotation an
          ON an.paragraph_id = p.id
         AND an.labeler_id = u.id
        WHERE p.filing_id = #{filingId}
          AND p.candidate = TRUE
        ORDER BY p.sort_order, u.username
        """)
    List<ReviewAnnotationRow> findAnnotations(@Param("filingId") Long filingId);

    @Select("""
        SELECT ad.annotation_id, ad.dimension_code
        FROM annotation_dimension ad
        JOIN annotation an ON an.id = ad.annotation_id
        JOIN paragraph p ON p.id = an.paragraph_id
        WHERE p.filing_id = #{filingId}
        ORDER BY ad.annotation_id, ad.dimension_code
        """)
    List<ReviewDimensionRow> findDimensions(@Param("filingId") Long filingId);

    @Select("""
        SELECT ad.annotation_id, ad.dimension_code, aa.attribute_code
        FROM annotation_attribute aa
        JOIN annotation_dimension ad ON ad.id = aa.annotation_dimension_id
        JOIN annotation an ON an.id = ad.annotation_id
        JOIN paragraph p ON p.id = an.paragraph_id
        WHERE p.filing_id = #{filingId}
          AND aa.present = TRUE
        ORDER BY ad.annotation_id, ad.dimension_code, aa.attribute_code
        """)
    List<ReviewAttributeRow> findAttributes(@Param("filingId") Long filingId);
    @Select("""
        SELECT DISTINCT u.id, u.username, u.role
        FROM app_user u
        JOIN assignment a ON a.labeler_id = u.id
        WHERE u.active = TRUE
          AND a.assignment_type IN ('LABELER', 'SUPERVISOR')
        ORDER BY u.id
        """)
    List<AssignableUser> findComparisonLabelers();

    @Select("""
        <script>
        SELECT
            f.id AS filing_id,
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
            END AS dataset_stage,
            (
                SELECT MIN(ax.batch_code)
                FROM assignment ax
                WHERE ax.filing_id = f.id
                  AND ax.assignment_type IN ('LABELER', 'SUPERVISOR')
            ) AS batch_code,
            p.id AS paragraph_db_id,
            p.paragraph_id,
            p.section_hint,
            p.heading_context,
            p.text,
            p.sort_order
        FROM filing f
        JOIN paragraph p ON p.filing_id = f.id
        WHERE p.candidate = TRUE
          AND EXISTS (
              SELECT 1 FROM assignment aa
              WHERE aa.filing_id = f.id
                AND aa.labeler_id = #{labelerA}
                AND aa.assignment_type IN ('LABELER', 'SUPERVISOR')
          )
          AND EXISTS (
              SELECT 1 FROM assignment ab
              WHERE ab.filing_id = f.id
                AND ab.labeler_id = #{labelerB}
                AND ab.assignment_type IN ('LABELER', 'SUPERVISOR')
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
        ORDER BY f.fiscal_year, f.company, p.sort_order, p.id
        </script>
        """)
    List<ReviewQueueParagraphRow> findQueueParagraphs(
        @Param("labelerA") Long labelerA,
        @Param("labelerB") Long labelerB,
        @Param("stage") String stage
    );

    @Select("""
        <script>
        SELECT
            p.id AS paragraph_db_id,
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
        FROM filing f
        JOIN paragraph p ON p.filing_id = f.id
        JOIN app_user u ON u.id IN (#{labelerA}, #{labelerB})
        JOIN assignment au
          ON au.filing_id = f.id
         AND au.labeler_id = u.id
         AND au.assignment_type IN ('LABELER', 'SUPERVISOR')
        LEFT JOIN annotation an
          ON an.paragraph_id = p.id
         AND an.labeler_id = u.id
        WHERE p.candidate = TRUE
          AND EXISTS (
              SELECT 1 FROM assignment aa
              WHERE aa.filing_id = f.id
                AND aa.labeler_id = #{labelerA}
                AND aa.assignment_type IN ('LABELER', 'SUPERVISOR')
          )
          AND EXISTS (
              SELECT 1 FROM assignment ab
              WHERE ab.filing_id = f.id
                AND ab.labeler_id = #{labelerB}
                AND ab.assignment_type IN ('LABELER', 'SUPERVISOR')
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
        ORDER BY f.fiscal_year, f.company, p.sort_order, u.id
        </script>
        """)
    List<ReviewAnnotationRow> findQueueAnnotations(
        @Param("labelerA") Long labelerA,
        @Param("labelerB") Long labelerB,
        @Param("stage") String stage
    );

    @Select("""
        <script>
        SELECT ad.annotation_id, ad.dimension_code
        FROM annotation_dimension ad
        JOIN annotation an ON an.id = ad.annotation_id
        JOIN paragraph p ON p.id = an.paragraph_id
        JOIN filing f ON f.id = p.filing_id
        WHERE p.candidate = TRUE
          AND an.labeler_id IN (#{labelerA}, #{labelerB})
          AND EXISTS (
              SELECT 1 FROM assignment aa
              WHERE aa.filing_id = f.id
                AND aa.labeler_id = #{labelerA}
                AND aa.assignment_type IN ('LABELER', 'SUPERVISOR')
          )
          AND EXISTS (
              SELECT 1 FROM assignment ab
              WHERE ab.filing_id = f.id
                AND ab.labeler_id = #{labelerB}
                AND ab.assignment_type IN ('LABELER', 'SUPERVISOR')
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
        ORDER BY ad.annotation_id, ad.dimension_code
        </script>
        """)
    List<ReviewDimensionRow> findQueueDimensions(
        @Param("labelerA") Long labelerA,
        @Param("labelerB") Long labelerB,
        @Param("stage") String stage
    );

    @Select("""
        <script>
        SELECT ad.annotation_id, ad.dimension_code, aa.attribute_code
        FROM annotation_attribute aa
        JOIN annotation_dimension ad ON ad.id = aa.annotation_dimension_id
        JOIN annotation an ON an.id = ad.annotation_id
        JOIN paragraph p ON p.id = an.paragraph_id
        JOIN filing f ON f.id = p.filing_id
        WHERE p.candidate = TRUE
          AND aa.present = TRUE
          AND an.labeler_id IN (#{labelerA}, #{labelerB})
          AND EXISTS (
              SELECT 1 FROM assignment aaa
              WHERE aaa.filing_id = f.id
                AND aaa.labeler_id = #{labelerA}
                AND aaa.assignment_type IN ('LABELER', 'SUPERVISOR')
          )
          AND EXISTS (
              SELECT 1 FROM assignment aab
              WHERE aab.filing_id = f.id
                AND aab.labeler_id = #{labelerB}
                AND aab.assignment_type IN ('LABELER', 'SUPERVISOR')
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
        ORDER BY ad.annotation_id, ad.dimension_code, aa.attribute_code
        </script>
        """)
    List<ReviewAttributeRow> findQueueAttributes(
        @Param("labelerA") Long labelerA,
        @Param("labelerB") Long labelerB,
        @Param("stage") String stage
    );

}
