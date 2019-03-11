package travel.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import travel.auth.HttpError;
import travel.auth.UserService;
import travel.entity.*;
import travel.mapper.DataSourceMapper;
import travel.mapper.JWTTokenMapper;
import travel.mapper.KeyWordMapper;
import travel.mapper.WarningSetMapper;
import travel.util.HttpUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/yuqing")
public class YuQingController {
    @Autowired
    private JWTTokenMapper jwtMapper;

    @Autowired
    private KeyWordMapper keyWordMapper;

    @Autowired
    private WarningSetMapper warningSetMapper;

    @Autowired
    private DataSourceMapper dataSourceMapper;

    @Autowired
    private UserService userService;


    @RequestMapping("/today_data")
    public ResponseEntity<?> today_data() throws IOException {
        String url = "http://travel.natapp1.cc/gxyq-web/count/today";
        return request(url);
    }

    @RequestMapping("/history_data")
    public ResponseEntity<?> history_data() throws IOException {
        String url = "http://travel.natapp1.cc/gxyq-web/count/history";
        return request(url);
    }

    @RequestMapping("/emotion_data")
    public ResponseEntity<?> emotion_data(@RequestParam(required=true, defaultValue = "") String start, @RequestParam(required=true, defaultValue = "") String end) throws IOException {
        String url = "http://travel.natapp1.cc/gxyq-web/count/mood?starttime="+start+"&endtime="+end;
        return request(url);
    }

    @RequestMapping("/data")
    public ResponseEntity<?> data(@RequestParam(required=false, defaultValue = "") String article_type, @RequestParam(required=false, defaultValue = "") String sentiment, @RequestParam(required=false, defaultValue = "") String keyword, @RequestParam int page, @RequestParam  int size, HttpServletRequest r1) throws IOException {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        List<DataSource> sources = dataSourceMapper.findAll(currentUser.getId());
        String resourceString = "";
        if (sources != null && sources.size() > 0) {
            for(int i=0; i<sources.size(); i++){
                resourceString += sources.get(i).getSourceName();
                if(i < sources.size() - 1){
                    resourceString += ",";
                }
            }
        }
        List<KeyWord> words = keyWordMapper.findAll(currentUser.getId());
        String keyWords = "";
        if(words != null && words.size() > 0){
            for(int j=0; j<words.size(); j++){
                keyWords += words.get(j).getWord();
                if(j < words.size() - 1){
                    keyWords += ",";
                }
            }
        }
        resourceString = "";
        keyWords = "";
        String url = "http://travel.natapp1.cc/gxyq-web/article/news?article_type="+article_type+"&sentiment="+sentiment+"&keywordlist="+keyWords+"&website_name="+resourceString+"&page_num="+page+"&page_size="+size+"&starttime=&endtime=";
        return request(url);
    }

    @RequestMapping("/weiboV")
    public ResponseEntity<?> weiboV() throws IOException {
        String url = "http://travel.natapp1.cc/gxyq-web/weibo/hot";
        return request(url);
    }

    @RequestMapping("/weiboHot")
    public ResponseEntity<?> weiboHot() throws IOException {
        String url = "http://travel.natapp1.cc/gxyq-web/article/weibohot";
        return request(url);
    }

    @RequestMapping("/sources")
    public ResponseEntity<?> sources(@RequestParam(required=false, defaultValue = "") String website_name, @RequestParam(required=false, defaultValue = "") String website_url, @RequestParam(required=false) int pagenum) throws IOException {
        String url = "http://travel.natapp1.cc/gxyq-web/data/source?website_name="+website_name+"&website_url="+website_url+"&page_size=10&pagenum="+pagenum;
        return request(url);
    }

    @RequestMapping("/article")
    public ResponseEntity<?> sources(@RequestParam(required=false, defaultValue = "") String id) throws IOException {
        String url = "http://travel.natapp1.cc/gxyq-web/article/get?article_id="+id;
        return request(url);
    }

    /**
     * 新建关键字
     * @param request
     * @param r1
     * @return token
     */
    @PostMapping(value = "/keyWord/add")
    public ResponseEntity<?> addKeyWord(@RequestBody JSONObject request, HttpServletRequest r1) {
        if(request.get("word") == null){
            return new ResponseEntity<HttpError>(new HttpError(400, "没有关键词"), HttpStatus.BAD_REQUEST);
        }
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        int existNum = keyWordMapper.findCountByWordAndUserId(request.get("word").toString(), currentUser.getId());
        if(existNum > 0){
            return new ResponseEntity<HttpError>(new HttpError(400, "关键词已存在"), HttpStatus.BAD_REQUEST);
        }
        Date now = new Date();
        KeyWord keyword = new KeyWord();
        keyword.setWord(request.get("word").toString());
        keyword.setUserId(currentUser.getId());
        keyword.setStatus("enabled");
        keyword.setCreated(now);
        keyword.setModified(now);
        int result = keyWordMapper.inserKeyWord(keyword);
        return new ResponseEntity<KeyWord>(keyword ,HttpStatus.OK);
    }

    @PostMapping(value = "/keyWord/update")
    public ResponseEntity<?> updateKeyWord(@RequestBody JSONObject request, HttpServletRequest r1) {
        if(request.get("word") == null){
            return new ResponseEntity<HttpError>(new HttpError(400, "没有关键词"), HttpStatus.BAD_REQUEST);
        }
        if(request.get("status") == null){
            return new ResponseEntity<HttpError>(new HttpError(400, "没有状态"), HttpStatus.BAD_REQUEST);
        }
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        int existNum = keyWordMapper.findCountByWordAndUserId(request.get("word").toString(), currentUser.getId());
        if(existNum == 0){
            return new ResponseEntity<HttpError>(new HttpError(400, "关键词不存在"), HttpStatus.BAD_REQUEST);
        }
        int result = keyWordMapper.updateStatusByWordAndUserId(request.get("status").toString(), new Date(), request.get("word").toString(), currentUser.getId());
        JSONObject obj = new JSONObject();
        obj.put("count", result);
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }

    @PostMapping(value = "/keyWord/delete")
    public ResponseEntity<?> deleteKeyWord(@RequestBody JSONObject request, HttpServletRequest r1) {
        if(request.get("word") == null){
            return new ResponseEntity<HttpError>(new HttpError(400, "没有关键词"), HttpStatus.BAD_REQUEST);
        }
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        int result = keyWordMapper.deleteByWordAndUserId(request.get("word").toString(), currentUser.getId());
        JSONObject obj = new JSONObject();
        obj.put("count", result);
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }

    @RequestMapping("/keyWord/list")
    public ResponseEntity<?> keyWordList(@RequestParam(required=false, defaultValue = "") int page, @RequestParam(required=false, defaultValue = "") int size,@RequestParam(required=false, defaultValue = "") String word, HttpServletRequest r1) throws IOException {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        List<KeyWord> words = keyWordMapper.findByUserId(currentUser.getId(), (page-1)*size, size, word == "" ? null : word);
        return new ResponseEntity<List<KeyWord>>(words ,HttpStatus.OK);
    }

    @RequestMapping("/keyWord/count")
    public ResponseEntity<?> keyWordList(@RequestParam(required=false, defaultValue = "") String word, HttpServletRequest r1) throws IOException {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        System.out.println(currentUser.getId());
        int count = keyWordMapper.findCountByUserId(currentUser.getId(), word == "" ? null : word);
        JSONObject obj = new JSONObject();
        obj.put("count", count);
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }

    @PostMapping(value = "/warningSet/add")
    public ResponseEntity<?> addWarningSet(@RequestBody JSONObject obj, HttpServletRequest r1) {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        WarningSet warningSet = new WarningSet();
        warningSet.setUserId(currentUser.getId());
        warningSet.setCreated(new Date());
        if(obj.get("sourceId") == null || obj.get("sourceName") == null || obj.get("sourceUrl") == null){
            return new ResponseEntity<HttpError>(new HttpError(400, "没有数据源"), HttpStatus.BAD_REQUEST);
        }
        warningSet.setSourceId(obj.get("sourceId").toString());
        warningSet.setSourceName(obj.get("sourceName").toString());
        warningSet.setSourceUrl(obj.get("sourceUrl").toString());
        if(obj.get("readNum") != null){
            warningSet.setReadNum(Integer.parseInt(obj.get("readNum").toString()));
        }else{
            warningSet.setReadNum(0);
        }
        if(obj.get("discussNum") != null){
            warningSet.setDiscussNum(Integer.parseInt(obj.get("discussNum").toString()));
        }else{
            warningSet.setDiscussNum(0);
        }
        if(obj.get("shareNum") != null){
            warningSet.setShareNum(Integer.parseInt(obj.get("shareNum").toString()));
        }else{
            warningSet.setShareNum(0);
        }
        if(obj.get("negValue") != null){
            warningSet.setNegValue(Double.parseDouble(obj.get("readNum").toString()));
        }else{
            warningSet.setNegValue(0);
        }
        int result = warningSetMapper.insertWarningSet(warningSet);
        JSONObject r = new JSONObject();
        obj.put("count", result);
        return new ResponseEntity<JSONObject>(r ,HttpStatus.OK);
    }

    @PostMapping(value = "/warningSet/update" )
    public ResponseEntity<?> updateWarningSet(@RequestBody JSONObject obj, HttpServletRequest r1) {
        if(obj.get("id") == null){
            return new ResponseEntity<HttpError>(new HttpError(400, "数据错误"), HttpStatus.BAD_REQUEST);
        }
        WarningSet warningSet = new WarningSet();
        warningSet.setId(Long.parseLong(obj.get("id").toString()));
        if(obj.get("readNum") != null){
            warningSet.setReadNum(Integer.parseInt(obj.get("readNum").toString()));
        }else{
            warningSet.setReadNum(0);
        }
        if(obj.get("discussNum") != null){
            warningSet.setDiscussNum(Integer.parseInt(obj.get("discussNum").toString()));
        }else{
            warningSet.setDiscussNum(0);
        }
        if(obj.get("shareNum") != null){
            warningSet.setShareNum(Integer.parseInt(obj.get("shareNum").toString()));
        }else{
            warningSet.setShareNum(0);
        }
        if(obj.get("negValue") != null){
            warningSet.setNegValue(Double.parseDouble(obj.get("negValue").toString()));
        }else{
            warningSet.setNegValue(0);
        }
        int result = warningSetMapper.updateWarningSet(warningSet);
        JSONObject r = new JSONObject();
        r.put("count", result);
        return new ResponseEntity<JSONObject>(r ,HttpStatus.OK);
    }

    @RequestMapping("/warningSet/list")
    public ResponseEntity<?> warningSetList(@RequestParam(required=false, defaultValue = "") int page, @RequestParam(required=false, defaultValue = "") int size, HttpServletRequest r1) throws IOException {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        List<WarningSet> sets = warningSetMapper.findByUserId(currentUser.getId(), (page-1)*size, size);
        return new ResponseEntity<List<WarningSet>>(sets ,HttpStatus.OK);
    }

    @RequestMapping("/warningSet/count")
    public ResponseEntity<?> warningSetCount(HttpServletRequest r1) throws IOException {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        System.out.println(currentUser.getId());
        int count = warningSetMapper.findCountByUserId(currentUser.getId());
        JSONObject obj = new JSONObject();
        obj.put("count", count);
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }

    @PostMapping(value = "/warningSet/delete")
    public ResponseEntity<?> deleteWarningSet(@RequestBody JSONObject request, HttpServletRequest r1) {
        if(request.get("id") == null){
            return new ResponseEntity<HttpError>(new HttpError(400, "没有id"), HttpStatus.BAD_REQUEST);
        }
        int result = warningSetMapper.deleteById(Long.parseLong(request.get("id").toString()));
        JSONObject obj = new JSONObject();
        obj.put("count", result);
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }

    @PostMapping(value = "/dataSource/add")
    public ResponseEntity<?> addDataSource(@RequestBody JSONObject obj, HttpServletRequest r1) {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        if(obj.get("sourceId") == null || obj.get("sourceName") == null || obj.get("sourceUrl") == null){
            return new ResponseEntity<HttpError>(new HttpError(400, "没有数据源"), HttpStatus.BAD_REQUEST);
        }
        int exist = dataSourceMapper.findCountByUrl(obj.get("sourceUrl").toString(), currentUser.getId());
        if(exist > 0){
            return new ResponseEntity<HttpError>(new HttpError(400, "该数据源已存在，请不要重复添加"), HttpStatus.BAD_REQUEST);
        }
        DataSource dataSource = new DataSource();
        dataSource.setUserId(currentUser.getId());
        dataSource.setCreated(new Date());
        dataSource.setSourceId(obj.get("sourceId").toString());
        dataSource.setSourceName(obj.get("sourceName").toString());
        dataSource.setSourceUrl(obj.get("sourceUrl").toString());
        int result = dataSourceMapper.insertDataSource(dataSource);
        JSONObject r = new JSONObject();
        obj.put("count", result);
        return new ResponseEntity<JSONObject>(r ,HttpStatus.OK);
    }

    @RequestMapping("/dataSource/list")
    public ResponseEntity<?> dataSourceList(@RequestParam(required=false, defaultValue = "") int page, @RequestParam(required=false, defaultValue = "") int size, HttpServletRequest r1) throws IOException {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        List<DataSource> sets = dataSourceMapper.findByUserId(currentUser.getId(), (page-1)*size, size);
        return new ResponseEntity<List<DataSource>>(sets ,HttpStatus.OK);
    }

    @RequestMapping("/dataSource/count")
    public ResponseEntity<?> dataSourceCount(HttpServletRequest r1) throws IOException {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        };
        int count = dataSourceMapper.findCountByUserId(currentUser.getId());
        JSONObject obj = new JSONObject();
        obj.put("count", count);
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }

    @PostMapping(value = "/dataSource/delete")
    public ResponseEntity<?> deleteDataSource(@RequestBody JSONObject request, HttpServletRequest r1) {
        if(request.get("id") == null){
            return new ResponseEntity<HttpError>(new HttpError(400, "没有id"), HttpStatus.BAD_REQUEST);
        }
        int result = dataSourceMapper.deleteById(Long.parseLong(request.get("id").toString()));
        JSONObject obj = new JSONObject();
        obj.put("count", result);
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }

    private ResponseEntity<?> request(String url)  throws IOException{
        String result = HttpUtil.sendGet(url.replace(" ", "%20"));
        System.out.println(url.replace(" ", "%20"));
        if(result == "fail"){
            return new ResponseEntity<HttpError>(new HttpError(500, "数据请求失败"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        JSONObject obj = JSONObject.parseObject(result);
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }



}
