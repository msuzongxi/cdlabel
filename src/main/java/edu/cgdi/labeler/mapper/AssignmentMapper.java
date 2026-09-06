package edu.cgdi.labeler.mapper;

import edu.cgdi.labeler.model.AdminFilingRow;
import edu.cgdi.labeler.model.AssignableUser;
import edu.cgdi.labeler.model.AssignmentPair;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AssignmentMapper {

    @Select("""
        SELECT id, username, role
        FROM app_user
        WHERE active = TRUE
          AND role IN ('LABELER', 'SUPERVISOR')
        ORDER BY CASE role WHEN 'LABELER' THEN 1 ELSE 2 END, username
        """)
    List<AssignableUser> findAssignableUsers();

    @Select("""
        SELECT
            f.id AS filing_id,
            f.company,
            f.fiscal_year,
            f.filename,
            f.master_cik,
            COUNT(DISTINCT p.id) FILTER (WHERE p.candidate = TRUE)::INTEGER AS paragraph_count,
            COUNT(DISTINCT a.id)::INTEGER AS assignment_count,
            CASE
                WHEN LOWER(COALESCE(f.source_path, '')) LIKE '%blind_pilot%'
                  OR LOWER(COALESCE(f.source_path, '')) LIKE '%blind-pilot%' THEN 'BLIND_PILOT'
                WHEN LOWER(COALESCE(f.source_path, '')) LIKE '%formal_validation%'
                  OR LOWER(COALESCE(f.source_path, '')) LIKE '%formal-validation%' THEN 'FORMAL_VALIDATION'
                WHEN LOWER(COALESCE(f.source_path, '')) LIKE '%calibration%' THEN 'CALIBRATION'
                ELSE 'OTHER'
            END AS dataset_stage
        FROM filing f
        LEFT JOIN paragraph p ON p.filing_id = f.id
        LEFT JOIN assignment a ON a.filing_id = f.id
        GROUP BY f.id, f.company, f.fiscal_year, f.filename, f.master_cik, f.source_path
        ORDER BY f.fiscal_year, f.company
        """)
    List<AdminFilingRow> findFilings();

    @Select("""
        SELECT
            a.filing_id,
            a.labeler_id AS user_id,
            u.username,
            a.assignment_type,
            a.batch_code
        FROM assignment a
        JOIN app_user u ON u.id = a.labeler_id
        ORDER BY a.filing_id, a.assignment_type, u.username
        """)
    List<AssignmentPair> findAssignments();

    @Select("""
        SELECT role
        FROM app_user
        WHERE id = #{userId}
          AND active = TRUE
        """)
    String findUserRole(@Param("userId") Long userId);

    @Insert("""
        INSERT INTO assignment(
            filing_id,
            labeler_id,
            batch_code,
            assignment_order,
            assignment_type,
            updated_at
        )
        VALUES (
            #{filingId},
            #{userId},
            #{batchCode},
            0,
            #{assignmentType},
            NOW()
        )
        ON CONFLICT (filing_id, labeler_id)
        DO UPDATE SET
            batch_code = EXCLUDED.batch_code,
            assignment_type = EXCLUDED.assignment_type,
            updated_at = NOW()
        """)
    int addAssignment(
        @Param("filingId") Long filingId,
        @Param("userId") Long userId,
        @Param("batchCode") String batchCode,
        @Param("assignmentType") String assignmentType
    );

    @Delete("""
        DELETE FROM assignment
        WHERE filing_id = #{filingId}
          AND labeler_id = #{userId}
        """)
    int removeAssignment(
        @Param("filingId") Long filingId,
        @Param("userId") Long userId
    );

    @Delete("""
        <script>
        DELETE FROM annotation an
        USING paragraph p
        WHERE an.paragraph_id = p.id
          AND p.filing_id IN
          <foreach item="filingId" collection="filingIds" open="(" separator="," close=")">
            #{filingId}
          </foreach>
          AND an.labeler_id IN
          <foreach item="userId" collection="userIds" open="(" separator="," close=")">
            #{userId}
          </foreach>
        </script>
        """)
    int clearAnnotations(
        @Param("filingIds") List<Long> filingIds,
        @Param("userIds") List<Long> userIds
    );
}
