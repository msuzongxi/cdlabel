package edu.cgdi.labeler.mapper;

import edu.cgdi.labeler.model.UserAccount;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserMapper {

    @Select("""
        SELECT id, username, password_hash, role, active
        FROM app_user
        WHERE username = #{username}
        """)
    UserAccount findByUsername(@Param("username") String username);

    @Select("""
        SELECT id
        FROM app_user
        WHERE username = #{username}
        """)
    Long findIdByUsername(@Param("username") String username);

    @Insert("""
        INSERT INTO app_user(username, password_hash, role, active)
        VALUES (#{username}, #{passwordHash}, #{role}, TRUE)
        ON CONFLICT (username) DO NOTHING
        """)
    int insertIfAbsent(
        @Param("username") String username,
        @Param("passwordHash") String passwordHash,
        @Param("role") String role
    );
}
