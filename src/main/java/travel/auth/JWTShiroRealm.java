package travel.auth;

import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import travel.entity.JWTToken;
import travel.entity.User;

public class JWTShiroRealm extends AuthorizingRealm {
    protected UserService userService;

    public JWTShiroRealm(UserService userService){
        this.userService = userService;
        //这里使用我们自定义的Matcher
        this.setCredentialsMatcher(new JWTCredentialsMatcher());
    }
    /**
     * 限定这个Realm只支持我们自定义的JWT Token
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        System.out.println("support1");
        System.out.println(token instanceof JWTToken);
        return token instanceof JWTToken;
    }

    /**
     * 更controller登录一样，也是获取用户的salt值，给到shiro，由shiro来调用matcher来做认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
        System.out.println("doGetAuthenticationInfo(authcToken)");
        JWTToken jwtToken = (JWTToken) authcToken;
        String token = jwtToken.getToken();
        System.out.println(token);
        System.out.println(JwtUtils.getUsername(token));
        User user = userService.getJwtTokenInfo(JwtUtils.getUsername(token));
        System.out.println("check");
        if(user == null)
            throw new AuthenticationException("token过期，请重新登录");

        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(user, token, "jwtRealm");

        return authenticationInfo;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.out.println("doGetAuthorizationInfo(PrincipalCollection principals)");
        return new SimpleAuthorizationInfo();
    }
}
