package org.example.controller;

import org.example.pojo.LoginRequest;
import org.example.pojo.LoginResponse;
import org.example.pojo.RegisterRequest;
import org.example.pojo.Result;
import org.example.pojo.User;
import org.example.service.OperationLogService;
import org.example.service.UserService;
import org.example.util.JwtUtil;
import org.example.util.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private OperationLogService operationLogService;

    @PostMapping("/register")
    public Result register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String email) {
        String trimmedUsername = username != null ? username.trim() : null;
        if (trimmedUsername == null || !trimmedUsername.matches("^\\S{5,16}$")) {
            return Result.error("用户名必须是5-16位非空字符");
        }
        if (password == null || !password.matches("^\\S{5,16}$")) {
            return Result.error("密码必须是5-16位非空字符");
        }
        if (email == null || !email.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$")) {
            return Result.error("邮箱格式不正确");
        }
        User u = userService.getUserByName(trimmedUsername);
        if (u == null) {
            userService.register(trimmedUsername, password, email);
            try {
                operationLogService.insertLog("user", "用户注册: " + trimmedUsername, trimmedUsername);
            } catch (Exception e) {}
            return Result.success();
        } else {
            return Result.error("用户名已被占用");
        }
    }

    @PostMapping("/login")
    public Result login(@RequestParam String username,
                        @RequestParam String password) {
        if (username == null || username.trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            return Result.error("密码不能为空");
        }
        User user = userService.login(username, password);
        if (user != null) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", user.getId());
            claims.put("username", user.getUsername());
            String token = JwtUtil.genToken(claims);
            LoginResponse response = new LoginResponse(token, user);
            try {
                operationLogService.insertLog("user", "用户登录: " + username, username);
            } catch (Exception e) {}
            return Result.success(response);
        }
        return Result.error("用户名或密码错误");
    }

    @GetMapping("/info")
    public Result getUserInfo() {
        Map<String, Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        User user = userService.getUserByName(username);
        return Result.success(user);
    }

    @GetMapping
    public Result getAllUsers() {
        List<User> users = userService.getAllUsers();
        return Result.success(users);
    }

    @GetMapping("/{id}")
    public Result getUserById(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        if (user != null) {
            return Result.success(user);
        }
        return Result.error("用户不存在");
    }

    @PutMapping
    public Result updateUser(@RequestBody User user) {
        userService.updateUser(user);
        try {
            Map<String, Object> claims = ThreadLocalUtil.get();
            String operator = claims != null ? (String) claims.get("username") : "system";
            operationLogService.insertLog("user", "修改用户信息: " + user.getUsername(), operator);
        } catch (Exception e) {}
        return Result.success();
    }

    @PostMapping
    public Result addUser(@RequestBody User user) {
        if (user.getUsername() == null || !user.getUsername().matches("^\\S{5,16}$")) {
            return Result.error("用户名必须是5-16位非空字符");
        }
        User existing = userService.getUserByName(user.getUsername());
        if (existing != null) {
            return Result.error("用户名已被占用");
        }
        userService.insertUser(user);
        try {
            Map<String, Object> claims = ThreadLocalUtil.get();
            String operator = claims != null ? (String) claims.get("username") : "system";
            operationLogService.insertLog("user", "添加用户: " + user.getUsername(), operator);
        } catch (Exception e) {}
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result deleteUser(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        String username = user != null ? user.getUsername() : "未知用户";
        userService.deleteUser(id);
        try {
            Map<String, Object> claims = ThreadLocalUtil.get();
            String operator = claims != null ? (String) claims.get("username") : "system";
            operationLogService.insertLog("user", "删除用户: " + username, operator);
        } catch (Exception e) {}
        return Result.success();
    }
}