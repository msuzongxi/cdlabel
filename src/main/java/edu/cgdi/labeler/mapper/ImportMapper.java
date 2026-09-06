package edu.cgdi.labeler.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface ImportMapper {

    @Select(value = """
        INSERT INTO filing(filename, master_cik, company, fiscal_year, source_path)
        VALUES (#{filename}, #{masterCik}, #{company}, #{fiscalYear}, #{sourcePath})
        ON CONFLICT (filename)
        DO UPDATE SET
            master_cik = EXCLUDED.master_cik,
            company = EXCLUDED.company,
            fiscal_year = EXCLUDED.fiscal_year,
            source_path = EXCLUDED.source_path
        RETURNING id
        """, affectData = true)
    Long upsertFiling(
        @Param("filename") String filename,
        @Param("masterCik") String masterCik,
        @Param("company") String company,
        @Param("fiscalYear") Integer fiscalYear,
        @Param("sourcePath") String sourcePath
    );

    @Update("""
        UPDATE paragraph
        SET candidate = FALSE
        WHERE filing_id = #{filingId}
        """)
    int clearSelectedFlag(@Param("filingId") Long filingId);

    @Select(value = """
        INSERT INTO paragraph(
            filing_id,
            paragraph_id,
            text,
            section_hint,
            heading_context,
            sort_order,
            candidate,
            word_count,
            table_like_flag
        )
        VALUES (
            #{filingId},
            #{paragraphId},
            #{text},
            #{sectionHint},
            #{headingContext},
            #{sortOrder},
            TRUE,
            #{wordCount},
            #{tableLikeFlag}
        )
        ON CONFLICT (filing_id, paragraph_id)
        DO UPDATE SET
            text = EXCLUDED.text,
            section_hint = EXCLUDED.section_hint,
            heading_context = EXCLUDED.heading_context,
            sort_order = EXCLUDED.sort_order,
            candidate = TRUE,
            word_count = EXCLUDED.word_count,
            table_like_flag = EXCLUDED.table_like_flag
        RETURNING id
        """, affectData = true)
    Long upsertParagraph(
        @Param("filingId") Long filingId,
        @Param("paragraphId") String paragraphId,
        @Param("text") String text,
        @Param("sectionHint") String sectionHint,
        @Param("headingContext") String headingContext,
        @Param("sortOrder") Integer sortOrder,
        @Param("wordCount") Integer wordCount,
        @Param("tableLikeFlag") Boolean tableLikeFlag
    );
}
