package travel.controller;

import com.alibaba.fastjson.JSONObject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import travel.auth.HttpError;
import travel.auth.UserService;
import travel.entity.JWTToken;
import travel.entity.User;
import travel.mapper.JWTTokenMapper;
import travel.mapper.UserMapper;

import java.util.Date;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JWTTokenMapper jwtMapper;

    @Autowired
    private UserService userService;



    @RequestMapping("/hello")
    public User  hello(){
        User u = userMapper.findByUsername("alex");
        return u;
    }

    /**
     * 注册
     * @param request
     * @return user
     */
    @PostMapping(value = "/register")
    public ResponseEntity<Void> register(@RequestBody JSONObject request){
        if(request.get("username") == null || request.get("password") == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        String username = request.get("username").toString();
        String password = request.get("password").toString();
        Date now = new Date();
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        if(request.get("name") != null){
            user.setName(request.get("name").toString());
        }
        if(request.get("phone") != null){
            user.setPhone(request.get("phone").toString());
        }
        user.setCreated(now);
        user.setModified(now);
        int r = userMapper.insertUser(user);
        System.out.println("insert result");
        System.out.println(r);
        return ResponseEntity.ok().build();
    }

    /**
     * 用户名密码登录
     * @param request
     * @return token
     */
    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody JSONObject request){
        Subject subject = SecurityUtils.getSubject();
        try {
            UsernamePasswordToken token = new UsernamePasswordToken(request.get("username").toString(), request.get("password").toString());
            subject.login(token);
            User user = (User) subject.getPrincipal();
            String newToken = userService.generateJwtToken(user.getUsername());
            JWTToken t = new JWTToken(newToken);
            t.setUserId(user.getId());
            t.setExpire(new Date(System.currentTimeMillis()+3600*1000));
            JSONObject obj = new JSONObject();
            jwtMapper.insertToken(t);
            obj.put("token", t.getToken());
            obj.put("userId", t.getUserId());
            obj.put("expire", t.getExpire());
            return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
        } catch (AuthenticationException e) {
            //logger.error("User {} login fail, Reason:{}", loginInfo.getUsername(), e.getMessage());
            System.out.println(e);
            return new ResponseEntity<HttpError>(new HttpError(401, "用户没有后台管理权限"),HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<HttpError>(new HttpError(500, "系统内部错误"),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> profile(@PathVariable String id) {
        System.out.println("dssdff");
        User user = userMapper.findById(Long.parseLong(id));
        if(user == null){
            return new ResponseEntity<HttpError>(new HttpError(404, "找不到用户"),HttpStatus.UNAUTHORIZED);
        }
        JSONObject result = new JSONObject();
        result.put("id", user.getId());
        result.put("name", user.getName());
        result.put("phone", user.getPhone());
        result.put("roles", user.getRoles());
        return new ResponseEntity<JSONObject>(result ,HttpStatus.OK);
    }



}
