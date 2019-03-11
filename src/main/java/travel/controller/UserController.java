package travel.controller;

import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.xpath.internal.operations.Bool;
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
import travel.entity.AuthCode;
import travel.entity.JWTToken;
import travel.entity.User;
import travel.mapper.AuthCodeMapper;
import travel.mapper.JWTTokenMapper;
import travel.mapper.UserMapper;
import travel.util.EncrptUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JWTTokenMapper jwtMapper;

    @Autowired
    private AuthCodeMapper authCodeMapper;

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
    public ResponseEntity<?> register(@RequestBody JSONObject request) throws Exception{
        if(request.get("phone") == null || request.get("password") == null || request.get("code") == null){
            return new ResponseEntity<HttpError>(new HttpError(400, "注册信息缺失"), HttpStatus.BAD_REQUEST);
        }
        String phone = request.get("phone").toString();
        List<User> existUserPhone = userMapper.findByPhone(phone);
        if(existUserPhone != null && existUserPhone.size() > 0){
            return new ResponseEntity<HttpError>(new HttpError(400, "手机号已存在，无法重复注册"), HttpStatus.BAD_REQUEST);
        }
        String password = EncrptUtil.decrypt(request.get("password").toString());
        String code = request.get("code").toString();
        Date now = new Date();
        List<AuthCode> codes = authCodeMapper.findValidCode(code, phone, now);
        if(codes == null || codes.size() == 0){
            return new ResponseEntity<HttpError>(new HttpError(401, "验证码无效"), HttpStatus.UNAUTHORIZED);
        }
        authCodeMapper.setUsed(true, codes.get(0).getId());
        User user = new User();
        user.setUsername(phone);
        user.setPassword(password);
        user.setPhone(phone);
        user.setCreated(now);
        user.setModified(now);
        int r = userMapper.insertUser(user);
        if(r != 1){
            return new ResponseEntity<HttpError>(new HttpError(500, "创建用户失败"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/updateUserWarn")
    public ResponseEntity<?> updateUserWarn(@RequestBody JSONObject request,  HttpServletRequest r1) throws Exception{
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        User user = new User();
        user.setId(currentUser.getId());
        user.setEmail_warn(Boolean.parseBoolean(request.get("email_warn").toString()));
        user.setMessage_warn(Boolean.parseBoolean(request.get("message_warn").toString()));
        user.setWechat_warn(Boolean.parseBoolean(request.get("wechat_warn").toString()));
        int count = userMapper.updateUserWarn(user);
        JSONObject obj = new JSONObject();
        obj.put("count", count);
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }

    @PostMapping(value = "/updateSpecifySource")
    public ResponseEntity<?> updateSpecifySource(@RequestBody JSONObject request,  HttpServletRequest r1) throws Exception{
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        if(request.get("specifySource") == null){
            return new ResponseEntity<HttpError>(new HttpError(400, "缺少元素"), HttpStatus.BAD_REQUEST);
        }
        int count = userMapper.updateSpecifySource(Boolean.parseBoolean(request.get("specifySource").toString()), currentUser.getId());
        JSONObject obj = new JSONObject();
        obj.put("count", count);
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }

    /**
     * 用户名密码登录
     * @param request
     * @return token
     */
    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody JSONObject request) throws Exception{
        if(request.get("username") == null || request.get("password") == null){
            return new ResponseEntity<HttpError>(new HttpError(400, "登录信息缺失"), HttpStatus.BAD_REQUEST);
        }
        Subject subject = SecurityUtils.getSubject();
        try {
            UsernamePasswordToken token = new UsernamePasswordToken(request.get("username").toString(), EncrptUtil.decrypt(request.get("password").toString()));
            subject.login(token);
            User user = (User) subject.getPrincipal();
            String newToken = userService.generateJwtToken(user.getUsername());
            JWTToken t = new JWTToken(newToken);
            t.setUserId(user.getId());
            t.setExpire(new Date(System.currentTimeMillis()+3600*1000*6));
            JSONObject obj = new JSONObject();
            jwtMapper.insertToken(t);
            obj.put("token", t.getToken());
            obj.put("userId", t.getUserId());
            obj.put("expire", t.getExpire());
            return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
        } catch (AuthenticationException e) {
            //logger.error("User {} login fail, Reason:{}", loginInfo.getUsername(), e.getMessage());
            System.out.println(e);
            return new ResponseEntity<HttpError>(new HttpError(401, "用户名或密码错误"),HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<HttpError>(new HttpError(500, "系统内部错误"),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> profile(HttpServletRequest r1) {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        JSONObject result = new JSONObject();
        result.put("id", currentUser.getId());
        result.put("name", currentUser.getName());
        result.put("phone", currentUser.getPhone());
        result.put("roles", currentUser.getRoles());
        result.put("message_warn", currentUser.isMessage_warn());
        result.put("email_warn", currentUser.isEmail_warn());
        result.put("wechat_warn", currentUser.isWechat_warn());
        result.put("specifySource", currentUser.isSpecify_source());
        return new ResponseEntity<JSONObject>(result ,HttpStatus.OK);
    }




}
