package org.example.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.groups.Default;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {

    @NotNull(message = "用户ID不能为空", groups = Update.class)
    private Integer id;

    private String username;

    /**
     * 注意：@JsonIgnore 会导致反序列化时无法接收前端传来的密码！
     *
     * 问题：当使用 @RequestBody User user 接收参数时，password 字段会被忽略，导致值为 null
     *
     * 解决方案：
     * 1. 注册功能：使用 RegisterRequest DTO 接收密码（已完成）
     * 2. 修改密码：使用 UpdatePasswordRequest DTO 接收密码
     * 3. 登录功能：直接使用两个参数接收，不涉及 User 对象
     *
     * 这样既保证了密码安全（不返回给前端），又能正确接收前端传来的密码
     */

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotEmpty(message = "昵称不能为空", groups = {Add.class, Update.class})
    @Pattern(regexp = "^\\S{1,10}$", message = "昵称必须是1-10位非空字符")
    private String nickname;

    @NotEmpty(message = "邮箱不能为空", groups = {Add.class, Update.class})
    @Email(message = "邮箱格式不正确")
    private String email;

    private String userPic;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 分组接口
    public interface Add extends Default {}
    public interface Update extends Default {}
}
