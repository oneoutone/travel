package travel.auth;

import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import travel.entity.User;

import java.util.List;

public class DbShiroRealm extends AuthorizingRealm {

    //数据库存储的用户密码的加密salt，正式环境不能放在源代码里
    //private static final String encryptSalt = "gxypt#";
    private UserService userService;

    public DbShiroRealm(UserService userService) {
        this.userService = userService;
        //因为数据库中的密码做了散列，所以使用shiro的散列Matcher
        this.setCredentialsMatcher(new travel.auth.PasswordMatcher());
    }
    /**
     *  找它的原因是这个方法返回true
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        System.out.println("support1");
        System.out.println(token instanceof UsernamePasswordToken);
        return token instanceof UsernamePasswordToken;
    }
    /**
     *  这一步我们根据token给的用户名，去数据库查出加密过用户密码，然后把加密后的密码和盐值一起发给shiro，让它做比对
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        System.out.println("doGetAuthenticationInfo(AuthenticationToken token)");
        UsernamePasswordToken userpasswordToken = (UsernamePasswordToken)token;
        String username = userpasswordToken.getUsername();
        User user = userService.getUserInfo(userpasswordToken.getUsername(), String.valueOf(userpasswordToken.getPassword()));
        if(user == null)
            throw new AuthenticationException("用户名或者密码错误");
        AuthenticationInfo r = new SimpleAuthenticationInfo(user, new Sha256Hash(user.getPassword()),"dbRealm");
        return r;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.out.println("doGetAuthenticationInfo(PrincipalCollection principals)");
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        User user = (User) principals.getPrimaryPrincipal();
        List<String> roles = user.getRoles();
        if (roles != null)
            simpleAuthorizationInfo.addRoles(roles);

        return simpleAuthorizationInfo;
    }
}
