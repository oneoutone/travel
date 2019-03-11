package travel.auth;

import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import travel.entity.AuthCode;
import travel.entity.JWTToken;
import travel.entity.User;
import travel.mapper.AuthCodeMapper;
import travel.mapper.JWTTokenMapper;
import travel.mapper.UserMapper;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JWTTokenMapper jwtTokenMapper;

    @Autowired
    private Environment env;

    /**
     * 保存user登录信息，返回token
     * @param username
     */
    public String generateJwtToken(String username) {
        //String salt = "12345";//JwtUtils.generateSalt();
        /**
         *
         * @todo 将salt保存到数据库或者缓存中
         * redisTemplate.opsForValue().set("token:"+username, salt, 3600, TimeUnit.SECONDS);
         */
        return JwtUtils.sign(username, env.getProperty("jwt.salt"), 3600*6); //生成jwt token，设置过期时间为1小时
    }

    /**
     * 获取上次token生成时的salt值和登录用户信息
     * @param username
     * @return
     */
    public User getJwtTokenInfo(String username) {
      return null;
    }


    /**
     * 获取数据库中保存的用户信息，主要是加密后的密码
     * @param userName
     * @return
     */
    public User getUserInfo(String userName) {
        User u = userMapper.findByUsername(userName);
        return u;
    }

    /**
     * 获取数据库中保存的用户信息，主要是加密后的密码
     * @param userName
     * @param password
     * @return
     */
    public User getUserInfo(String userName, String password) {
        User u = userMapper.findByUsernameAndPassword(userName, password);
        return u;
    }

    public User getUserByHeader(HttpServletRequest r1){
        String token = r1.getHeader("Authorization");
        if(token == null){
            return null;
        }
        JWTToken jwt = jwtTokenMapper.findByToken(token);
        if(jwt == null){
            return null;
        }
        User user = userMapper.findById(jwt.getUserId());
        return user;
    }

    /**
     * 获取用户角色列表，强烈建议从缓存中获取
     * @param userId
     * @return
     */
    public List<String> getUserRoles(Long userId){
        return Arrays.asList("admin");
    }

    public Boolean validToken(String token){
        Date now = new Date();
        int num = jwtTokenMapper.countByToken(token, now);
        if(num > 0){
            return true;
        }
        return false;
    }
}
