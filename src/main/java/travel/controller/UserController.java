package travel.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import travel.auth.HttpError;
import travel.auth.UserService;
import travel.entity.*;
import travel.mapper.*;
import travel.util.EncrptUtil;
import travel.util.HttpUtil;
import weixin.popular.api.MessageAPI;
import weixin.popular.api.SnsAPI;
import weixin.popular.api.TokenAPI;
import weixin.popular.api.UserAPI;
import weixin.popular.bean.message.templatemessage.TemplateMessage;
import weixin.popular.bean.message.templatemessage.TemplateMessageItem;
import weixin.popular.bean.sns.SnsToken;
import weixin.popular.bean.token.Token;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
    private WechatAccessTokenMapper wechatAccessTokenMapper;

    @Autowired
    private WarningSetMapper warningSetMapper;

    @Autowired
    private DataMapper dataMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleDataMapper articleDataMapper;

    @Autowired
    private DataRequestMapper dataRequestMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private Environment env;

    public String getAccessToken() {
        WechatAccessToken token = wechatAccessTokenMapper.findToken(new Date());
        if(token == null) {
            Token t = TokenAPI.token(env.getProperty("wechat.appId"), env.getProperty("wechat.appSecret"));
            WechatAccessToken newToken = new WechatAccessToken();
            newToken.setAccessToken(t.getAccess_token());
            Date now = new Date();
            newToken.setExpire(new Date(now.getTime() + t.getExpires_in() * 1000 - 300 * 1000));
            wechatAccessTokenMapper.insertWechatAccessToken(newToken);
            return t.getAccess_token();
        }else{
            return token.getAccessToken();
        }
    }

    @RequestMapping("/hello1")
    public void  hello1() throws IOException, ParseException {
        String url = "http://travel.natapp1.cc/gxyq-web/article/newscount?article_type="+4+"&sentiment="+3+"&keywordlist=&website=&page_size=1000&starttime=&endtime=";
        JSONObject r = request(url);
        int num = Integer.parseInt(r.get("allnum").toString());
        System.out.println(num);
        for(int i=1; i<num+1; i++){
            String u = "http://travel.natapp1.cc/gxyq-web/article/news?article_type="+4+"&sentiment="+3+"&keywordlist=&website=&pagenum="+i+"&page_size=1000&starttime=&endtime=";
            JSONObject records = request(u);
            JSONArray array = records.getJSONArray("data");
            Date now = new Date();
            if(array != null && array.size() > 0){
                for(int j=0; j<array.size(); j++){
                    JSONObject obj = array.getJSONObject(j).getJSONObject("result");
                    ArticleData data = new ArticleData();
                    if(obj.get("article_id") != null){
                        String articleId = obj.get("article_id").toString();
                        String title = "";
                        if(obj.get("title") == null ){
                            if(obj.get("content") != null){
                                if(obj.get("content").toString().length() > 20){
                                    title = obj.get("content").toString().substring(0, 19);
                                }else{
                                    title = obj.get("content").toString();
                                }
                            }
                        }else{
                            title = obj.get("title").toString();
                        }
                        int c = articleDataMapper.findCountByArticleId(articleId, title);
                        if(c == 0) {
                            try {
                                data.setArticleId(obj.get("article_id") == null ? "" : obj.get("article_id").toString());
                                data.setContent(obj.get("content") == null ? "" : obj.get("content").toString());
                                data.setTitle(title);
                                data.setSource_name(obj.get("source_name") == null ? "" : obj.get("source_name").toString());
                                data.setArticletype(obj.get("articletype") == null ? "" : obj.get("articletype").toString());
                                data.setSentiment(obj.get("sentiment") == null ? "" : obj.get("sentiment").toString());
                                data.setSource_url(obj.get("source_url") == null ? "" : obj.get("source_url").toString());
                                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0");
                                data.setPublish_time(obj.get("publish_time") == null ? now : format1.parse(obj.get("publish_time").toString()));
                                Float pos = obj.get("postive") == null ? new Float(0.5) : Float.parseFloat(obj.get("postive").toString());
                                Float neg = obj.get("negative") == null ? new Float(0.5) : Float.parseFloat(obj.get("negative").toString());
                                data.setPostive(pos);
                                data.setNegtive(neg);
                                if(neg > 0.8){
                                    data.setType("1");
                                }else if(neg > 0.65){
                                    data.setType("2");
                                }else if(pos > 0.65){
                                    data.setType("4");
                                }else {
                                    data.setType("3");
                                }
                                String locations = "";
                                List<String> guilinWords = Arrays.asList("桂林,秀峰区,叠彩区,七星区,象山区,雁山区,临桂区,灵川县,兴安县,全州县,灌阳县,资源县,永福县,阳朔,荔浦,平乐县,龙胜各族,恭城瑶族".split(","));
                                for(int t=0; t<guilinWords.size(); t++){
                                    if(data.getTitle().indexOf(guilinWords.get(t))>=0 || data.getContent().indexOf(guilinWords.get(t))>=0 ){
                                        locations = "桂林";
                                    }
                                }
                                data.setLocations(locations);
                                try{
                                    articleDataMapper.insert(data);
                                }catch(Exception e){
                                    System.out.println(e);
                                }
                            } catch (Exception e) {
                                continue;
                            }
                        }
                    }
                }
            }
        }

    }

    @RequestMapping("/hello")
    public void  hello() throws IOException, ParseException {
        Date now = new Date();
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(now);
        rightNow.add(Calendar.MINUTE, -90);
        Date start = rightNow.getTime();
        Calendar e = Calendar.getInstance();
        e.setTime(start);
        e.add(Calendar.MINUTE, 30 );
        Date end = e.getTime();
        System.out.println(start);
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String st = format.format(start);
        String ed = format.format(end);
        System.out.println(st);
        System.out.println(ed);
        String url = "http://travel.natapp1.cc/gxyq-web/article/news?article_type=4&sentiment=4&keywordlist=&website=&pagenum=1&page_size=1000&starttime="+st+"&endtime="+ed;
        JSONObject r = request(url);
        JSONArray array = r.getJSONArray("data");
        System.out.println(r.getJSONArray("data").size());
        System.out.println(r.getJSONArray("data").getJSONObject(0).getJSONObject("result"));
        List<User> users = userMapper.findUsers();
        for(int i=0; i<array.size(); i++){
            JSONObject obj = array.getJSONObject(i).getJSONObject("result");
            int readNum = 0;
            int discussNum = 0;
            int shareNum = 0;
            double postive = 1;
            double negtive = 0;
            String source_name = "";
            String path = "";
            String title = "";
            String content = "";
            if(obj.get("readnum") != null && Integer.parseInt(obj.get("readnum").toString()) > 0){
                readNum = Integer.parseInt(obj.get("readnum").toString());
            }
            if(obj.get("contentnum") != null && Integer.parseInt(obj.get("contentnum").toString()) > 0){
                discussNum = Integer.parseInt(obj.get("contentnum").toString());
            }
            if(obj.get("spreadnum") != null && Integer.parseInt(obj.get("spreadnum").toString()) > 0){
                shareNum = Integer.parseInt(obj.get("spreadnum").toString());
            }
            if(obj.get("postive") != null && Double.parseDouble(obj.get("postive").toString()) > 0){
                postive = Double.parseDouble(obj.get("postive").toString());
            }
            if(obj.get("negative") != null && Double.parseDouble(obj.get("negative").toString()) > 0){
                negtive = Double.parseDouble(obj.get("negative").toString());
            }
            if(obj.get("source_name") != null){
                source_name = obj.get("source_name").toString();
            }
            if(obj.get("title") != null){
                if(obj.get("title").toString().length() > 20){
                    title = obj.get("title").toString().substring(0,19);
                }else{
                    title = obj.get("title").toString();
                }
            }else{
                if(obj.get("content").toString().length() > 20){
                    title = obj.get("content").toString().substring(0,19);
                }else{
                    title = obj.get("content").toString();
                }
            }
            if(obj.get("content") != null){
                content = obj.get("content").toString();
            }
            if(obj.get("source_url") != null && obj.get("source_url").toString().startsWith("http")){
                path = obj.get("source_url").toString();
            }else if(obj.get("source_name") != null && obj.get("source_name").toString().startsWith("http")){
                path = obj.get("source_name").toString();
            }
            System.out.println(readNum);
            System.out.println(discussNum);
            System.out.println(shareNum);
            System.out.println(postive);
            System.out.println(negtive);
            for(int j =0; j<users.size(); j++){
                System.out.println(users.get(j).isSpecify_warn_setting());
                boolean push = false;
                String type = "0";
                if(users.get(j).isSpecify_warn_setting() == false){
                    System.out.println("default");
                    List<WarningSet> warningSets = warningSetMapper.findDefaultByUserId(users.get(j).getId());
                    if(warningSets != null && warningSets.size() > 0){
                        WarningSet w = warningSets.get(0);
                        System.out.println(w.getNegValue());
                        if(w.getReadNum() > 0 && w.getReadNum() < readNum){
                            push = true;
                            type = "1";
                        }
                        if(w.getDiscussNum() > 0 && w.getDiscussNum() < discussNum){
                            push = true;
                            type = "2";
                        }
                        if(w.getShareNum() > 0 && w.getShareNum() < shareNum){
                            push = true;
                            type = "3";
                        }
                        if(w.getNegValue() > 0 && w.getNegValue() > (1-negtive)){
                            System.out.println("trigger 4");
                            System.out.println(negtive);
                            System.out.println( w.getNegValue());
                            push = true;
                            type = "4";
                        }
                    }
                }else{
                    List<WarningSet> sWarningSets =  warningSetMapper.findSpecifyByUserId(users.get(j).getId());
                    for(int a = 0; a<sWarningSets.size(); a++){
                        WarningSet record = sWarningSets.get(a);
                        if(record.getSourceName().equals(source_name)){
                            if(record.getReadNum() > 0 && record.getReadNum() < readNum){
                                push = true;
                                type = "1";
                            }
                            if(record.getDiscussNum() > 0 && record.getDiscussNum() < discussNum){
                                push = true;
                                type = "2";
                            }
                            if(record.getShareNum() > 0 && record.getShareNum() < shareNum){
                                push = true;
                                type = "3";
                            }
                            if(record.getNegValue() > 0 && record.getNegValue() > (1-negtive)){
                                push = true;
                                System.out.println("trigger 5");
                                System.out.println(negtive);
                                System.out.println( record.getNegValue());
                                type = "4";
                            }
                        }
                    }
                }
                if(push == true){
                    Article article = new Article();
                    article.setArticleId(obj.get("article_id") == null ? "" : obj.get("article_id").toString());
                    article.setContent(obj.get("content") == null ? "" : obj.get("content").toString());
                    article.setCreated(new Date());
                    article.setStatus("1");
                    System.out.println(title);
                    article.setTitle(title);
                    article.setUserId(users.get(j).getId());
                    article.setSource_name(obj.get("source_name") == null ? "" : obj.get("source_name").toString());
                    article.setPublish_time(obj.get("publish_time") == null ? "" : obj.get("publish_time").toString());
                    article.setPostive(Float.parseFloat(String.valueOf(1-negtive)));
                    article.setType(type);
                    List<Article> artlcles = articleMapper.findArticleById(users.get(j).getId(), obj.get("article_id").toString());
                    if(artlcles != null && artlcles.size() > 0){
                        continue;
                    }
                    articleMapper.insertArticle(article);
                    if(users.get(j).isMessage_warn() == true && users.get(j).getWarn_phones() != null){
                        String[] phones = users.get(j).getWarn_phones().split(",");
                        for(int c=0; c<phones.length; c++){
                            if(!phones[c].equals("") && phones[c].length() == 11){
                                String u = "http://v.juhe.cn/sms/send.php?mobile="+phones[c]+"&tpl_id=151692&tpl_value=" + URLEncoder.encode("#title#="+title+"&#url#="+path)+"&key=e7ca3042db7bb9ef00e4ba86b6a61e71";
                                System.out.println(u);
                            }
                        }
                    }
                    if(users.get(j).isEmail_warn() == true && users.get(j).getWarn_emails() != null){
                        String con = "消息标题="+title+", 消息地址："+path;
                        String[] emails = users.get(j).getWarn_emails().split(",");
                        System.out.println("end email1");
                        System.out.println(con);
                    }
                }
            }

            System.out.println("send notification111");
            List<DataRequest> requests = dataRequestMapper.findAll("运行中");
            for(int b=0; b<requests.size(); b++){
                if(!requests.get(b).getType().equals("即时发送")){
                    continue;
                }
                boolean send = false;
                System.out.println(title);
                System.out.println(source_name);
                String urls = requests.get(b).getUrls();
                if(urls.indexOf(source_name) >= 0){
                    List<String> words= Arrays.asList(requests.get(b).getWords().split(","));
                    for(int c=0; c<words.size(); c++){
                        if(content.indexOf(words.get(c)) >= 0){
                            send = true;
                            break;
                        }
                    }
                    if(send == true){
                        if(requests.get(b).getEmail() != null){
                            String con = "消息标题="+title+", 消息地址："+path;
                            System.out.println("end email2");
                            System.out.println(con);
                        }
                        if(requests.get(b).getPhone() != null){
                            String u = "http://v.juhe.cn/sms/send.php?mobile="+requests.get(b).getPhone()+"&tpl_id=151692&tpl_value=" +URLEncoder.encode("#title#="+title+"&#url#="+path)+"&key=e7ca3042db7bb9ef00e4ba86b6a61e71";
                            System.out.println(u);
                            //HttpUtil.sendGet(u);
                        }
                    }
                }
            }
        }

    }

    private JSONObject request(String url)  throws IOException{
        String result = HttpUtil.sendGet(url.replace(" ", "%20"));
        System.out.println(url.replace(" ", "%20"));
        if(result == "fail"){
            return null;
        }
        JSONObject obj = JSONObject.parseObject(result);
        return obj;
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
        user.setSpecify_warn_setting(false);
        user.setSpecify_source(false);
        int r = userMapper.insertUser(user);
        if(r != 1){
            return new ResponseEntity<HttpError>(new HttpError(500, "创建用户失败"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        List<User> cUser = userMapper.findByPhone(phone);
        if(cUser != null && cUser.size() > 0){
            WarningSet set = new WarningSet();
            set.setUserId(cUser.get(0).getId());
            set.setType("default");
            set.setNegValue(0.2);
            set.setReadNum(0);
            set.setDiscussNum(0);
            set.setShareNum(0);
            set.setCreated(new Date());
            warningSetMapper.insertWarningSet(set);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * 注册
     * @param request
     * @return user
     */
    @PostMapping(value = "/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestBody JSONObject request) throws Exception{
        if(request.get("phone") == null || request.get("password") == null || request.get("code") == null){
            return new ResponseEntity<HttpError>(new HttpError(400, "用户信息缺失"), HttpStatus.BAD_REQUEST);
        }
        String phone = request.get("phone").toString();
        List<User> existUserPhone = userMapper.findByPhone(phone);
        if(existUserPhone == null || existUserPhone.size() == 0){
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
        User user = existUserPhone.get(0);
        user.setPassword(password);
        user.setModified(now);
        int r = userMapper.updatePassword(user);
        if(r != 1){
            return new ResponseEntity<HttpError>(new HttpError(500, "重制密码失败"), HttpStatus.INTERNAL_SERVER_ERROR);
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
        user.setApp_warn(Boolean.parseBoolean(request.get("app_warn").toString()));
        int count = userMapper.updateUserWarn(user);
        JSONObject obj = new JSONObject();
        obj.put("count", count);
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }

    @PostMapping(value = "/updateWarnInfo")
    public ResponseEntity<?> updateWarnInfo(@RequestBody JSONObject request,  HttpServletRequest r1) throws Exception{
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        User user = new User();
        user.setId(currentUser.getId());
        user.setWarn_emails(request.get("warn_emails").toString());
        user.setWarn_phones(request.get("warn_phones").toString());
        int count = userMapper.updateWarnInfo(user);
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

    @PostMapping(value = "/updateSpecifyWarning")
    public ResponseEntity<?> updateSpecifyWarning(@RequestBody JSONObject request,  HttpServletRequest r1) throws Exception{
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        if(request.get("specifyWarning") == null){
            return new ResponseEntity<HttpError>(new HttpError(400, "缺少元素"), HttpStatus.BAD_REQUEST);
        }
        int count = userMapper.updateSpecifyWarning(Boolean.parseBoolean(request.get("specifyWarning").toString()), currentUser.getId());
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
            User u = userMapper.findById(t.getUserId());
            if(u.getRoles() != null){
                return new ResponseEntity<HttpError>(new HttpError(401, "管理员请从管理后台登陆"),HttpStatus.UNAUTHORIZED);
            }
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

    @PostMapping(value = "/adminLogin")
    public ResponseEntity<?> adminLogin(@RequestBody JSONObject request) throws Exception{
        if(request.get("username") == null || request.get("password") == null) {
            return new ResponseEntity<HttpError>(new HttpError(400, "登录信息缺失"), HttpStatus.BAD_REQUEST);
        }
        Subject subject = SecurityUtils.getSubject();
        try {
            UsernamePasswordToken token = new UsernamePasswordToken(request.get("username").toString(), request.get("password").toString());
            subject.login(token);
            User user = (User) subject.getPrincipal();
            User u = userMapper.findById(user.getId());
            String roles = u.getRoles();
            if(roles.indexOf("admin") == -1 && roles.indexOf("office") == -1){
                return new ResponseEntity<HttpError>(new HttpError(401, "不是管理员"),HttpStatus.UNAUTHORIZED);
            }
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
        result.put("username", currentUser.getUsername());
        result.put("name", currentUser.getName());
        result.put("phone", currentUser.getPhone());
        result.put("roles", currentUser.getRoles());
        result.put("message_warn", currentUser.isMessage_warn());
        result.put("email_warn", currentUser.isEmail_warn());
        result.put("wechat_warn", currentUser.isWechat_warn());
        result.put("app_warn", currentUser.isApp_warn());
        result.put("specifySource", currentUser.isSpecify_source());
        result.put("headerUrl", currentUser.getHeaderUrl());
        result.put("warn_emails", currentUser.getWarn_emails());
        result.put("warn_phones", currentUser.getWarn_phones());
        result.put("specify_warn_setting", currentUser.isSpecify_warn_setting());
        return new ResponseEntity<JSONObject>(result ,HttpStatus.OK);
    }

    @GetMapping("/managers")
    public ResponseEntity<?> getManagers(HttpServletRequest r1) {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        List<User> users = userMapper.findManagers();
        return new ResponseEntity<List<User>>(users ,HttpStatus.OK);
    }

    @GetMapping("/userById")
    public ResponseEntity<?> getUserById(@RequestParam(required=false, defaultValue = "") String id, HttpServletRequest r1) {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        User user = userMapper.findById(Long.parseLong(id));
        user.setPassword(null);
        return new ResponseEntity<User>(user ,HttpStatus.OK);
    }

    @GetMapping("/role_managers")
    public ResponseEntity<?> getRoleManager(HttpServletRequest r1) {
        List<User> users = userMapper.findManager();
        return new ResponseEntity<List<User>>(users ,HttpStatus.OK);
    }

    @PostMapping("/wechatOpenId")
    public ResponseEntity<?> wechatOpenId(@RequestBody JSONObject request){
        SnsToken token = SnsAPI.oauth2AccessToken(env.getProperty("wechat.appId"), env.getProperty("wechat.appSecret"), request.get("code").toString());
        if(token == null || token.getAccess_token() == null || token.getOpenid() == null) {
            return new ResponseEntity<HttpError>(new HttpError(401, "code无效") ,HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<SnsToken>(token,HttpStatus.OK);
    }

    @PostMapping("/wechatBind")
    public ResponseEntity<?> wechatBind(@RequestBody JSONObject request) throws IOException {
        String phone = null;
        String code = null;
        String openId = null;
        if(request.get("phone") != null){
            phone = request.get("phone").toString();
        }else{
            return new ResponseEntity<HttpError>(new HttpError(400, "没有手机号"),HttpStatus.BAD_REQUEST);
        }
        if(request.get("code") != null){
            code = request.get("code").toString();
        }else{
            return new ResponseEntity<HttpError>(new HttpError(400, "没有验证码"),HttpStatus.BAD_REQUEST);
        }
        if(request.get("openId") != null){
            openId = request.get("openId").toString();
        }else{
            return new ResponseEntity<HttpError>(new HttpError(400, "微信code"),HttpStatus.BAD_REQUEST);
        }
        List<AuthCode> codes = authCodeMapper.findValidCode(code, phone, new Date());
        if(codes == null || codes.size() == 0){
            return new ResponseEntity<HttpError>(new HttpError(401, "验证码无效"), HttpStatus.UNAUTHORIZED);
        }
        List<User> us = userMapper.findByPhone(phone);
        if(us == null || us.size() == 0 ){
            return new ResponseEntity<HttpError>(new HttpError(401, "您不是云平台注册用户，请先去网页端注册"),HttpStatus.BAD_REQUEST);
        }
        User currentUser = us.get(0);
        if(currentUser.getOpenId() == null || !currentUser.getOpenId().equals(openId)){
            weixin.popular.bean.user.User u = UserAPI.userInfo(getAccessToken(), openId);
            JSONObject profile = JSON.parseObject(JSON.toJSONString(u));
            System.out.println(profile);
            currentUser.setOpenId(openId);
            currentUser.setHeaderUrl(profile.get("headimgurl") == null ? "" : profile.get("headimgurl").toString());

            userMapper.updateWechatInfo(currentUser);

//            TemplateMessage message = new TemplateMessage();
//            message.setTemplate_id(env.getProperty("wechat.notification.bind"));
//            message.setTouser(currentUser.getOpenId());
//            LinkedHashMap<String, TemplateMessageItem> d = new LinkedHashMap();
//            d.put("first", new TemplateMessageItem("绑定注册成功", "#000000"));
//            d.put("keyword1", new TemplateMessageItem(currentUser.getEmployeeId(), "#000000"));
//            d.put("keyword2", new TemplateMessageItem(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), "#000000"));
//            d.put("remark", new TemplateMessageItem("姓名： "+currentUser.getNickName(), "#000000"));
//            message.setData(d);
//            MessageAPI.messageTemplateSend(getAccessToken(), message);
//            TemplateMessage message1 = new TemplateMessage();
//            message1.setTemplate_id(env.getProperty("wechat.notification.score"));
//            message1.setTouser(currentUser.getOpenId());
//            LinkedHashMap<String, TemplateMessageItem> d1 = new LinkedHashMap();
//            d1.put("first", new TemplateMessageItem("获得积分通知", "#000000"));
//            d1.put("keyword1", new TemplateMessageItem(currentUser.getNickName(), "#000000"));
//            d1.put("keyword2", new TemplateMessageItem(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), "#000000"));
//            d1.put("keyword3", new TemplateMessageItem("+200", "#000000"));
//            d1.put("keyword4", new TemplateMessageItem(currentUser.getScore()+"", "#000000"));
//            d1.put("keyword5", new TemplateMessageItem("绑定注册", "#000000"));
//            d1.put("remark", new TemplateMessageItem("可使用积分到商城兑换商品", "#000000"));
//            message1.setData(d1);
//            MessageAPI.messageTemplateSend(getAccessToken(), message1);
        }

        String newToken = userService.generateJwtToken(currentUser.getUsername());
        JWTToken t = new JWTToken(newToken);
        t.setUserId(currentUser.getId());
        t.setExpire(new Date(System.currentTimeMillis()+3600*1000*6));
        JSONObject obj = new JSONObject();
        jwtMapper.insertToken(t);
        obj.put("token", t.getToken());
        obj.put("userId", t.getUserId());
        obj.put("expire", t.getExpire());
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);

    }

    @PostMapping("/wechatAuth")
    public ResponseEntity<?> wechatAuth(@RequestBody JSONObject request) throws IOException{
        if(request.get("openId") == null){
            return new ResponseEntity<HttpError>(new HttpError(400, "openId不存在") ,HttpStatus.BAD_REQUEST);
        }
        String openId = request.get("openId").toString();
        User currentUser = userMapper.findByOpenId(openId);
        if(currentUser == null) {
            return new ResponseEntity<HttpError>(new HttpError(401, "您的微信未绑定，请绑定") ,HttpStatus.UNAUTHORIZED);
        }
        String newToken = userService.generateJwtToken(currentUser.getUsername());
        JWTToken t = new JWTToken(newToken);
        t.setUserId(currentUser.getId());
        t.setExpire(new Date(System.currentTimeMillis()+3600*1000*6));
        JSONObject obj = new JSONObject();
        jwtMapper.insertToken(t);
        obj.put("token", t.getToken());
        obj.put("userId", t.getUserId());
        obj.put("expire", t.getExpire());
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }


    @PostMapping("/upsertManager")
    public ResponseEntity<?> upsertManager(@RequestBody JSONObject request) {
        User newUser;
        if(request.get("id") != null){
            newUser = userMapper.findById(Long.parseLong(request.get("id").toString()));
        }else{
            newUser = new User();
            if(request.get("username") == null){
                return new ResponseEntity<HttpError>(new HttpError(400, "userId不存在") ,HttpStatus.BAD_REQUEST);
            }
            User existUser = userMapper.findByUsername(request.get("username").toString());
            if(existUser != null){
                return new ResponseEntity<HttpError>(new HttpError(400, "用户名已经存在") ,HttpStatus.BAD_REQUEST);
            }
        }
        if(request.get("username") != null){
            newUser.setUsername(request.get("username").toString());
        }
        if(request.get("password") != null){
            newUser.setPassword(request.get("password").toString());
        }
        if(request.get("roles") != null){
            newUser.setRoles(request.get("roles").toString());
        }
        if(request.get("phone") != null){
            newUser.setPhone(request.get("phone").toString());
        }
        if(request.get("id") != null){
            userMapper.updateUser(newUser);
        }else{
            newUser.setCreated(new Date());
            newUser.setSpecify_warn_setting(false);
            newUser.setSpecify_source(false);
            int r = userMapper.insertUser(newUser);
            if(r != 1){
                return new ResponseEntity<HttpError>(new HttpError(500, "创建用户失败"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            User cUser = userMapper.findByUsername(request.get("username").toString());
            if(cUser != null){
                WarningSet set = new WarningSet();
                set.setUserId(cUser.getId());
                set.setType("default");
                set.setNegValue(0.2);
                set.setReadNum(100);
                set.setDiscussNum(100);
                set.setShareNum(100);
                set.setCreated(new Date());
                warningSetMapper.insertWarningSet(set);
            }
        }
        User u;
        if(request.get("id") != null){
            u = userMapper.findById(newUser.getId());
        }else{
            u = userMapper.findByUsername(request.get("username").toString());
        }
        return new ResponseEntity<User>(u ,HttpStatus.OK);
    }

    @PostMapping("/checkToken")
    public ResponseEntity<?> checkToken(@RequestBody JSONObject request) {
        if(request.get("accessToken") == null){
            return new ResponseEntity<HttpError>(new HttpError(400, "没有token"),HttpStatus.BAD_REQUEST);
        }
        JWTToken token = jwtMapper.findByToken(request.get("accessToken").toString());
        if(token == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "不是有效的令牌"),HttpStatus.BAD_REQUEST);
        }else{
            JSONObject obj = new JSONObject();
            obj.put("token", token.getToken());
            obj.put("userId", token.getUserId());
            obj.put("expire", token.getExpire());
            return new ResponseEntity<JSONObject>(obj,HttpStatus.OK);
        }
    }

    @PostMapping("/clientToken")
    public ResponseEntity<?> clientToken(@RequestBody JSONObject request, HttpServletRequest r1) {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        if(request.get("clientId") == null){
            return new ResponseEntity<HttpError>(new HttpError(400, "客户id不存在"), HttpStatus.UNAUTHORIZED);
        }
        User client = userMapper.findById(Long.parseLong(request.get("clientId").toString()));
        if(client == null){
            return new ResponseEntity<HttpError>(new HttpError(400, "客户不存在"), HttpStatus.UNAUTHORIZED);
        }
        String newToken = userService.generateJwtToken(client.getUsername());
        JWTToken t = new JWTToken(newToken);
        t.setUserId(client.getId());
        t.setExpire(new Date(System.currentTimeMillis()+3600*1000*6));
        JSONObject obj = new JSONObject();
        jwtMapper.insertToken(t);
        obj.put("token", t.getToken());
        obj.put("userId", t.getUserId());
        obj.put("expire", t.getExpire());
        User u = userMapper.findById(t.getUserId());
        if(u.getRoles() != null){
            obj.put("roles", u.getRoles());
        }
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }


}
