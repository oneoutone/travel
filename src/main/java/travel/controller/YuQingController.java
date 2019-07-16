package travel.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import travel.auth.HttpError;
import travel.auth.UserService;
import travel.entity.*;
import travel.mapper.*;
import travel.util.DateUtil;
import travel.util.HttpUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.rmi.NoSuchObjectException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

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
    private DataSourceRequestMapper dataSourceRequestMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private DataMapper dataMapper;

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private ArticleDataMapper articleDataMapper;

    @Autowired
    private UserService userService;

//    @RequestMapping("/sync")
//    public ResponseEntity<?> sync() throws IOException {
//        System.out.println("hello");
//        Date now = new Date();
//        Calendar rightNow = Calendar.getInstance();
//        rightNow.setTime(now);
//        rightNow.add(Calendar.HOUR , -24);
//        rightNow.set(Calendar.HOUR_OF_DAY, 0);
//        rightNow.set(Calendar.MINUTE, 0);
//        rightNow.set(Calendar.SECOND, 0);
//        Date start = rightNow.getTime();
//        Calendar today = Calendar.getInstance();
//        today.set(Calendar.HOUR, 0);
//        today.set(Calendar.MINUTE, 0);
//        today.set(Calendar.SECOND, 0);
//        Date end = today.getTime();
//        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
//        String st = format.format(start);
//        String ed = format.format(end);
//        String url = "http://travel.natapp1.cc/gxyq-web/article/newscount?article_type="+4+"&sentiment="+4+"&keywordlist=&website=&page_size=1000&starttime=&endtime=";
//        JSONObject r = request(url);
//        int num = Integer.parseInt(r.get("allnum").toString());
//        for(int i=1; i<num+1; i++){
//            String u = "http://travel.natapp1.cc/gxyq-web/article/news?article_type="+4+"&sentiment="+4+"&keywordlist=&website=&pagenum="+i+"&page_size=1000&starttime=&endtime=";
//            JSONObject records = request(u);
//            JSONArray array = records.getJSONArray("data");
//            if(array != null && array.size() > 0){
//                for(int j=0; j<array.size(); j++){
//                    JSONObject obj = array.getJSONObject(j).getJSONObject("result");
//                    Data data = new Data();
//                    if(obj.get("article_id") != null){
//                        String articleId = obj.get("article_id").toString();
//                        System.out.println(articleId);
//                        int c = dataMapper.findCountByArticleId(articleId);
//                        if(c == 0) {
//                            try {
//                                data.setArticleId(obj.get("article_id") == null ? "" : obj.get("article_id").toString());
//                                data.setContent(obj.get("content") == null ? "" : obj.get("content").toString());
//                                data.setTitle(obj.get("title") == null ? "" : obj.get("title").toString());
//                                data.setSource_name(obj.get("source_name") == null ? "" : obj.get("source_name").toString());
//                                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0");
//                                data.setPublish_time(obj.get("publish_time") == null ? now : format1.parse(obj.get("publish_time").toString()));
//                                Float pos = obj.get("postive") == null ? new Float(0.5) : Float.parseFloat(obj.get("postive").toString());
//                                Float neg = obj.get("negtive") == null ? new Float(0.5) : Float.parseFloat(obj.get("negtive").toString());
//                                data.setPostive(pos);
//                                if(neg > 0.8){
//                                    data.setType("1");
//                                }else if(neg > 0.65){
//                                    data.setType("2");
//                                }else if(pos > 0.65){
//                                    data.setType("4");
//                                }else {
//                                    data.setType("3");
//                                }
//                                dataMapper.insertData(data);
//                            } catch (Exception e) {
//                                continue;
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }


    @RequestMapping("/report")
    public ResponseEntity<?> report(@RequestParam(required=false, defaultValue = "") String start, @RequestParam(required=false, defaultValue = "") String end, @RequestParam(required=false, defaultValue = "") String type,HttpServletRequest r1) throws Exception {
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date s = format.parse(start.concat(" 00:00:00"));
        Date e = format.parse(end.concat(" 00:00:00"));
        String url = "http://travel.natapp1.cc/gxyq-web/resport/news?starttime="+start+"&endtime="+end+"&page_size=1000&pagenum=1";
        String result = HttpUtil.sendGet(url.replace(" ", "%20"));
        System.out.println(url.replace(" ", "%20"));
        JSONObject r = JSONObject.parseObject(result);
        JSONArray data = r.getJSONArray("data");
        // 创建工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 创建工作表
        HSSFSheet sheet = workbook.createSheet("sheet1");
        HSSFRow row = sheet.createRow(0);
        row.createCell(0).setCellValue("网站名称");
        row.createCell(1).setCellValue("网站地址");
        row.createCell(2).setCellValue("采集数量");

        for (int i = 0; i < data.size(); i++)
        {
            JSONObject obj = data.getJSONObject(i).getJSONObject("result");
            HSSFRow rows = sheet.createRow(i+1);
            rows.createCell(0).setCellValue(obj.get("webside_name") != null ? obj.get("webside_name").toString() : "");
            rows.createCell(1).setCellValue(obj.get("website_url") != null ? obj.get("website_url").toString() : "");
            rows.createCell(2).setCellValue(obj.get("newscount") != null ? obj.get("newscount").toString() : "");
        }
        File xlsFile = new File("/home/www/caiji/cloud/report/"+start+"-"+end+".xls");
        FileOutputStream xlsStream = new FileOutputStream(xlsFile);
        workbook.write(xlsStream);
        String u = "http://hadupu.cn/cloud/report/"+start+"-"+end+".xls";
        Report report = new Report();
        report.setType(type);
        report.setStart(s);
        report.setEnd(e);
        report.setUrl(u);
        report.setCreated(new Date());
        reportMapper.insertReport(report);


        return new ResponseEntity<String>("ok" ,HttpStatus.OK);
    }


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

    @RequestMapping("/words")
    public ResponseEntity<?> words(@RequestParam(required=true, defaultValue = "") String type, @RequestParam(required=true, defaultValue = "") String keyword) throws IOException {
        String url = "http://travel.natapp1.cc/gxyq-web/keyword/reseach?keywordtype="+type+"&size=50&keyword="+keyword;
        return request(url);
    }

    @RequestMapping("/emotion_data")
    public ResponseEntity<?> emotion_data(@RequestParam(required=true, defaultValue = "") String start, @RequestParam(required=true, defaultValue = "") String end, HttpServletRequest r1) throws IOException {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        List<DataSource> sources = dataSourceMapper.findAll(currentUser.getId());
        String resourceString = "";
        if(currentUser.isSpecify_source() == true){
            if (sources != null && sources.size() > 0) {
                for(int i=0; i<sources.size(); i++){
                    resourceString += sources.get(i).getSourceName();
                    if(i < sources.size() - 1){
                        resourceString += ",";
                    }
                }
            }
        }
        String keyWords = "";
        String url = "http://travel.natapp1.cc/gxyq-web/count/mood?starttime="+start+"&endtime="+end+"&keywordlist="+keyWords+"&website="+resourceString;
        return request(url);
    }

    @RequestMapping("/leader_data")
    public ResponseEntity<?> leader_data(@RequestParam(required=true, defaultValue = "") String start, @RequestParam(required=true, defaultValue = "") String end, HttpServletRequest r1) throws IOException {
        String url = "http://travel.natapp1.cc/gxyq-web/count/mood?starttime="+start+"&endtime="+end+"&keywordlist=&website=";
        return request(url);
    }

    @RequestMapping("/data_page")
    public ResponseEntity<?> data_page(@RequestParam(required=false, defaultValue = "") String article_type, @RequestParam(required=false, defaultValue = "") String sentiment, @RequestParam(required=false, defaultValue = "") String keyword,@RequestParam(required=false, defaultValue = "") String filter, @RequestParam  int size, HttpServletRequest r1) throws IOException {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        System.out.println("start1");

        String resourceString = "";
        if(currentUser.isSpecify_source() == true){
            List<DataSource> sources = dataSourceMapper.findAll(currentUser.getId());
            if (sources != null && sources.size() > 0) {
                for(int i=0; i<sources.size(); i++){
                    resourceString += sources.get(i).getSourceName();
                    if(i < sources.size() - 1){
                        resourceString += ",";
                    }
                }
            }
        }
        System.out.println("start2");
//        List<KeyWord> words = keyWordMapper.findAll(currentUser.getId());
        String keyWords = "";
//        if(words != null && words.size() > 0){
//            for(int j=0; j<words.size(); j++){
//                keyWords += words.get(j).getWord();
//                if(j < words.size() - 1){
//                    keyWords += ",";
//                }
//            }
//        }
//        keyWords = "";
        String locations = "";
        if(currentUser.getCompanyId() > 0){
            Company company = companyMapper.findById(currentUser.getCompanyId());
            if(company != null){
                if(company.getLocation() != null){
                    locations = company.getLocation();
                }
            }
        }
        if(filter != null){
            keyWords = filter;
        }
        System.out.println("start3");
        String url = "http://travel.natapp1.cc/gxyq-web/article/newscount?city="+locations+"&article_type="+article_type+"&sentiment="+sentiment+"&keywordlist="+keyWords+"&website="+resourceString+"&page_size="+size+"&starttime=&endtime=";
        System.out.println(url);
        return request(url);
    }

    @RequestMapping("/local_page")
    public ResponseEntity<?> data_page(@RequestParam(required=false, defaultValue = "") String article_type, @RequestParam  int size, HttpServletRequest r1) throws IOException {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        String location = "";
        if(currentUser.getCompanyId() > 0){
            Company company = companyMapper.findById(currentUser.getCompanyId());
            if(company != null){
                location = company.getLocation();
            }
        }
        int count = articleDataMapper.findCount(location);
        JSONObject obj = new JSONObject();
        obj.put("count", count);
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }



    @RequestMapping("/data")
    public ResponseEntity<?> data(@RequestParam(required=false, defaultValue = "") String article_type, @RequestParam(required=false, defaultValue = "") String sentiment, @RequestParam(required=false, defaultValue = "") String keyword, @RequestParam(required=false, defaultValue = "") String filter, @RequestParam int page, @RequestParam  int size, HttpServletRequest r1) throws IOException {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }

        String resourceString = "";
        if(currentUser.isSpecify_source() == true){
            List<DataSource> sources = dataSourceMapper.findAll(currentUser.getId());
            if (sources != null && sources.size() > 0) {
                for(int i=0; i<sources.size(); i++){
                    resourceString += sources.get(i).getSourceName();
                    if(i < sources.size() - 1){
                        resourceString += ",";
                    }
                }
            }
        }

        //List<KeyWord> words = keyWordMapper.findAll(currentUser.getId());
        String keyWords = "";
//        if(words != null && words.size() > 0){
//            for(int j=0; j<words.size(); j++){
//                keyWords += words.get(j).getWord();
//                if(j < words.size() - 1){
//                    keyWords += ",";
//                }
//            }
//        }
//        keyWords = "";

        String locations = "";
        if(currentUser.getCompanyId() > 0){
            Company company = companyMapper.findById(currentUser.getCompanyId());
            if(company != null){
                if(company.getLocation() != null){
                    locations = company.getLocation();
                }
            }
        }
        if(filter != null){
            keyWords = filter;
        }
        String url = "http://travel.natapp1.cc/gxyq-web/article/news?city="+locations+"&article_type="+article_type+"&sentiment="+sentiment+"&keywordlist="+keyWords+"&website="+resourceString+"&pagenum="+page+"&page_size="+size+"&starttime=&endtime=";
        System.out.println(url);
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

    @RequestMapping("/trend")
    public ResponseEntity<?> trend(@RequestParam(required=false, defaultValue = "") String articleId) throws IOException {
        String url = "http://travel.natapp1.cc/gxyq-web/warn/list?articleId="+articleId;
        String result = HttpUtil.sendGet(url.replace(" ", "%20"));
        if(result == "fail"){
            return new ResponseEntity<HttpError>(new HttpError(500, "数据请求失败"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        JSONArray obj = JSONArray.parseArray(result);
        return new ResponseEntity<JSONArray>(obj ,HttpStatus.OK);
    }

    @RequestMapping("/sources")
    public ResponseEntity<?> sources(@RequestParam(required=false, defaultValue = "") String website_name, @RequestParam(required=false, defaultValue = "") String website_url, @RequestParam(required=false, defaultValue = "") String type,@RequestParam(required=false, defaultValue = "") String channel,  @RequestParam(required=false) int pagenum) throws IOException {
        if(type.equals("all")){
            type = "";
        }
        if(channel.equals("all")){
            channel = "";
        }
        String url = "http://travel.natapp1.cc/gxyq-web/data/source?website_name="+website_name+"&website_url="+website_url+"&page_size=10&pagenum="+pagenum+"&website_type="+type+"&source_type="+channel;
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
    public ResponseEntity<?> addKeyWord(@RequestBody JSONObject request, HttpServletRequest r1)  throws IOException{
        if(request.get("word") == null){
            return new ResponseEntity<HttpError>(new HttpError(400, "没有关键词"), HttpStatus.BAD_REQUEST);
        }
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        String word = request.get("word").toString().replace("，", ",");
        String[] words = word.split(",");
        int count = 0;
        for(int i=0; i<words.length; i++){
            String s= words[i];
            int existNum = keyWordMapper.findCountByWordAndUserId(s, currentUser.getId());
            if(existNum > 0){
                continue;
            }
            Date now = new Date();
            KeyWord keyword = new KeyWord();
            keyword.setWord(s);
            keyword.setUserId(currentUser.getId());
            keyword.setStatus("waiting");
            keyword.setType(Integer.parseInt(request.get("type").toString()));
            keyword.setCreated(now);
            keyword.setModified(now);
            int result = keyWordMapper.inserKeyWord(keyword);
            count += result;
        }

        JSONObject obj = new JSONObject();
        obj.put("count", count);
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
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

    @PostMapping(value = "/keyWord/confirm")
    public ResponseEntity<?> confirmKeyWord( HttpServletRequest r1) throws IOException {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        List<KeyWord> words = keyWordMapper.findAllByStatus(currentUser.getId(), "waiting");
        for(int i=0; i<words.size(); i++){
            String url = "http://travel.natapp1.cc/gxyq-web/keyword/add?keywordtype="+words.get(i).getType()+"&keyword="+words.get(i).getWord();
            request(url);
        }
        int result = keyWordMapper.updateStatusByUserId("confirmed", new Date(), currentUser.getId());
        JSONObject obj = new JSONObject();
        obj.put("count", result);
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }

    @RequestMapping("/keyWord/requestList")
    public ResponseEntity<?> keyWordRequestList(@RequestParam(required=false, defaultValue = "") String status,HttpServletRequest r1) throws IOException {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        List<KeyWord> words = keyWordMapper.findByUserAndStatus(currentUser.getId(), status);
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

    @RequestMapping("/warningSet/detail")
    public ResponseEntity<?> warningSetDetail(@RequestParam(required=false, defaultValue = "") String id, HttpServletRequest r1) throws IOException {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        System.out.println(currentUser.getId());
        WarningSet set = warningSetMapper.findById(Long.parseLong(id));
        return new ResponseEntity<WarningSet>(set ,HttpStatus.OK);
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
            warningSet.setReadNum(100);
        }
        if(obj.get("discussNum") != null){
            warningSet.setDiscussNum(Integer.parseInt(obj.get("discussNum").toString()));
        }else{
            warningSet.setDiscussNum(100);
        }
        if(obj.get("shareNum") != null){
            warningSet.setShareNum(Integer.parseInt(obj.get("shareNum").toString()));
        }else{
            warningSet.setShareNum(100);
        }
        if(obj.get("negValue") != null){
            warningSet.setNegValue(Double.parseDouble(obj.get("negValue").toString()));
        }else{
            warningSet.setNegValue(0.2);
        }
        int result = warningSetMapper.insertWarningSet(warningSet);
        JSONObject r = new JSONObject();
        obj.put("count", result);
        return new ResponseEntity<JSONObject>(r ,HttpStatus.OK);
    }

    @PostMapping(value = "/warningSet/upsert" )
    public ResponseEntity<?> upsertWarningSet(@RequestBody JSONObject obj, HttpServletRequest r1) {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        WarningSet warningSet = new WarningSet();
        if(obj.get("readNum") != null){
            warningSet.setReadNum(Integer.parseInt(obj.get("readNum").toString()));
        }else{
            warningSet.setReadNum(100);
        }
        if(obj.get("discussNum") != null){
            warningSet.setDiscussNum(Integer.parseInt(obj.get("discussNum").toString()));
        }else{
            warningSet.setDiscussNum(100);
        }
        if(obj.get("shareNum") != null){
            warningSet.setShareNum(Integer.parseInt(obj.get("shareNum").toString()));
        }else{
            warningSet.setShareNum(100);
        }
        if(obj.get("negValue") != null){
            warningSet.setNegValue(Double.parseDouble(obj.get("negValue").toString()));
        }else{
            warningSet.setNegValue(0.2);
        }

        if(obj.get("id") == null){
            warningSet.setType(obj.get("type").toString());
            warningSet.setUserId(currentUser.getId());
            warningSet.setCreated(new Date());
            if(!obj.get("type").toString().equals("default")){
                if(obj.get("sourceName") == null || obj.get("sourceUrl") == null) {
                    return new ResponseEntity<HttpError>(new HttpError(400, "没有数据源"), HttpStatus.BAD_REQUEST);
                }
                    warningSet.setSourceName(obj.get("sourceName").toString());
                    warningSet.setSourceUrl(obj.get("sourceUrl").toString());
            }else{
                warningSet.setSourceId("");
                warningSet.setSourceName("");
                warningSet.setSourceUrl("");
            }
            int result = warningSetMapper.insertWarningSet(warningSet);
            if(obj.get("type").toString().equals("default")){
                List<WarningSet> res = warningSetMapper.findDefaultByUserId(currentUser.getId());
                return new ResponseEntity<WarningSet>(res.get(0) ,HttpStatus.OK);
            }else{
                List<WarningSet> res = warningSetMapper.findDefaultBySourceName(currentUser.getId(), obj.get("sourceName").toString());
                return new ResponseEntity<WarningSet>(res.get(0) ,HttpStatus.OK);
            }
        }else{
            warningSet.setId(Long.parseLong(obj.get("id").toString()));
            int result = warningSetMapper.updateWarningSet(warningSet);
            JSONObject r = new JSONObject();
            r.put("count", result);
            return new ResponseEntity<JSONObject>(r ,HttpStatus.OK);
        }
    }

    @PostMapping(value = "/warningSet/updateAll" )
    public ResponseEntity<?> updateAll(@RequestBody JSONObject obj, HttpServletRequest r1) {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        WarningSet warningSet = new WarningSet();
        if(obj.get("readNum") != null){
            warningSet.setReadNum(Integer.parseInt(obj.get("readNum").toString()));
        }else{
            warningSet.setReadNum(100);
        }
        if(obj.get("discussNum") != null){
            warningSet.setDiscussNum(Integer.parseInt(obj.get("discussNum").toString()));
        }else{
            warningSet.setDiscussNum(100);
        }
        if(obj.get("shareNum") != null){
            warningSet.setShareNum(Integer.parseInt(obj.get("shareNum").toString()));
        }else{
            warningSet.setShareNum(100);
        }
        if(obj.get("negValue") != null){
            warningSet.setNegValue(Double.parseDouble(obj.get("negValue").toString()));
        }else{
            warningSet.setNegValue(0.2);
        }
        warningSet.setUserId(currentUser.getId());
        int result = warningSetMapper.updateWarningSetAll(warningSet);
        JSONObject r = new JSONObject();
        r.put("count", result);
        return new ResponseEntity<JSONObject>(r ,HttpStatus.OK);
    }

    @RequestMapping("/warningSet/list")
    public ResponseEntity<?> warningSetList(@RequestParam(required=false, defaultValue = "") String type, @RequestParam(required=false, defaultValue = "") String filter, @RequestParam(required=false, defaultValue = "") int page, @RequestParam(required=false, defaultValue = "") int size, HttpServletRequest r1) throws IOException {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        List<WarningSet> sets = warningSetMapper.findByUserIdAndFilter(currentUser.getId(), (page-1)*size, size, filter, type);
        return new ResponseEntity<>(sets ,HttpStatus.OK);
    }

    @RequestMapping("/warningSet/count")
    public ResponseEntity<?> warningSetCount(@RequestParam(required=false, defaultValue = "") String type, @RequestParam(required=false, defaultValue = "") String filter, HttpServletRequest r1) throws IOException {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        System.out.println(currentUser.getId());
        int count = warningSetMapper.findCountByUserIdAndFilter(currentUser.getId(), filter, type);
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
        if(obj.get("sourceName") == null || obj.get("sourceUrl") == null){
            return new ResponseEntity<HttpError>(new HttpError(400, "没有数据源"), HttpStatus.BAD_REQUEST);
        }
        int exist = dataSourceMapper.findCountByUrl(obj.get("sourceUrl").toString(), currentUser.getId());
        if(exist > 0){
            return new ResponseEntity<HttpError>(new HttpError(400, "该数据源已存在，请不要重复添加"), HttpStatus.BAD_REQUEST);
        }
        DataSource dataSource = new DataSource();
        dataSource.setUserId(currentUser.getId());
        dataSource.setCreated(new Date());
        dataSource.setSourceName(obj.get("sourceName").toString());
        dataSource.setSourceUrl(obj.get("sourceUrl").toString());
        if(obj.get("type") != null){
            dataSource.setType(obj.get("type").toString());
        }
        if(obj.get("channel")!= null){
            dataSource.setChannel(obj.get("channel").toString());
        }
        int result = dataSourceMapper.insertDataSource(dataSource);
        List<DataSource> d = dataSourceMapper.findByUserAndUrl(currentUser.getId(), obj.get("sourceUrl").toString());
        return new ResponseEntity<DataSource>(d.get(0) ,HttpStatus.OK);
    }

    @RequestMapping("/dataSource/list")
    public ResponseEntity<?> dataSourceList(@RequestParam(required=false, defaultValue = "") String filter, @RequestParam(required=false, defaultValue = "") int page, @RequestParam(required=false, defaultValue = "") int size, HttpServletRequest r1) throws IOException {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        List<DataSource> sets = dataSourceMapper.findByUserId(currentUser.getId(), (page-1)*size, size, filter);
        return new ResponseEntity<List<DataSource>>(sets ,HttpStatus.OK);
    }

    @RequestMapping("/dataSource/count")
    public ResponseEntity<?> dataSourceCount(@RequestParam(required=false, defaultValue = "") String filter, HttpServletRequest r1) throws IOException {
        System.out.println("filter");
        System.out.println("dsfeagrrrrtrfgsgdsfgdsfddd");
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        };
        int count = dataSourceMapper.findCountByUserId(currentUser.getId(), filter);
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
        if(result == "fail"){
            return new ResponseEntity<HttpError>(new HttpError(500, "数据请求失败"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        JSONObject obj = JSONObject.parseObject(result);
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }

    @PostMapping(value = "/dataSourceRequest/add")
    public ResponseEntity<?> addDataSourceRequest(@RequestBody JSONObject obj, HttpServletRequest r1) {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        if(obj.get("sourceName") == null || obj.get("sourceUrl") == null){
            return new ResponseEntity<HttpError>(new HttpError(400, "没有数据源"), HttpStatus.BAD_REQUEST);
        }
        int exist = dataSourceRequestMapper.findCountByUrl(obj.get("sourceUrl").toString(), currentUser.getId());
        if(exist > 0){
            return new ResponseEntity<HttpError>(new HttpError(400, "该数据源请求已存在，请不要重复添加"), HttpStatus.BAD_REQUEST);
        }
        DataSourceRequest dataSourceRequest = new DataSourceRequest();
        dataSourceRequest.setUserId(currentUser.getId());
        dataSourceRequest.setCreated(new Date());
        dataSourceRequest.setSourceName(obj.get("sourceName").toString());
        dataSourceRequest.setSourceUrl(obj.get("sourceUrl").toString());
        dataSourceRequest.setChannel(obj.get("channel").toString());
        dataSourceRequest.setType(obj.get("type").toString());
        dataSourceRequest.setStatus("waiting");
        int result = dataSourceRequestMapper.insertDataSourceRequest(dataSourceRequest);
        JSONObject r = new JSONObject();
        obj.put("count", result);
        return new ResponseEntity<JSONObject>(r ,HttpStatus.OK);
    }

    @RequestMapping("/dataSourceRequest/list")
    public ResponseEntity<?> dataSourceRequestList(@RequestParam(required=false, defaultValue = "") int page, @RequestParam(required=false, defaultValue = "") int size, @RequestParam(required=false, defaultValue = "") String channel, @RequestParam(required=false, defaultValue = "") String type, @RequestParam(required=false, defaultValue = "") String filter,HttpServletRequest r1) throws IOException {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        if(type.equals("all")){
            type = "";
        }
        if(channel.equals("all")){
            channel = "";
        }
        List<DataSourceRequest> sets = dataSourceRequestMapper.findByUserId(currentUser.getId(), (page-1)*size, size,  type, channel, filter);
        return new ResponseEntity<List<DataSourceRequest>>(sets ,HttpStatus.OK);
    }

    @PostMapping(value = "/dataSourceRequest/deleteById")
    public ResponseEntity<?> deleteDataSourceRequestById(@RequestBody JSONObject request, HttpServletRequest r1) {
        dataSourceRequestMapper.deleteById(Long.parseLong(request.get("id").toString()));
        JSONObject obj = new JSONObject();
        obj.put("result", "ok");
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }

    @PostMapping(value = "/dataSourceRequest/update")
    public ResponseEntity<?> updateDataSourceRequestById(@RequestBody JSONObject request, HttpServletRequest r1) {
        dataSourceRequestMapper.update(request.get("status").toString(), Long.parseLong(request.get("id").toString()));
        JSONObject obj = new JSONObject();
        obj.put("result", "ok");
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }

    @PostMapping(value = "/article/read")
    public ResponseEntity<?> read(@RequestBody JSONObject obj, HttpServletRequest r1) {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        int result = articleMapper.updateStatus(obj.get("articleId").toString());
        JSONObject r = new JSONObject();
        obj.put("count", result);
        return new ResponseEntity<JSONObject>(r ,HttpStatus.OK);
    }

    @RequestMapping("/article/list")
    public ResponseEntity<?> articleList(HttpServletRequest r1) throws IOException {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }

        List<Article> sets = articleMapper.findByUserId(currentUser.getId());
        return new ResponseEntity<List<Article>>(sets ,HttpStatus.OK);
    }

    @RequestMapping("/dataSourceRequest/count")
    public ResponseEntity<?> dataSourceRequestCount(@RequestParam(required=false, defaultValue = "") String channel, @RequestParam(required=false, defaultValue = "") String type, @RequestParam(required=false, defaultValue = "") String filter, HttpServletRequest r1) throws IOException {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        if(type.equals("all")){
            type = "";
        }
        if(channel.equals("all")){
            channel = "";
        }
        int count = dataSourceRequestMapper.findCount(currentUser.getId(), type, channel, filter);
        JSONObject obj = new JSONObject();
        obj.put("count", count);
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }

    @RequestMapping("/report/list")
    public ResponseEntity<?> reportList(@RequestParam(required=false, defaultValue = "") String start, @RequestParam(required=false, defaultValue = "") String end, @RequestParam(required=false, defaultValue = "") String type, @RequestParam(required=false, defaultValue = "") int page, @RequestParam(required=false, defaultValue = "") int size, HttpServletRequest r1) throws Exception {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        Date s = null;
        Date e = null;
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(!start.equals("")){
            s = format.parse(start);
        }
        if(!end.equals("")){
            e = format.parse(end);
        }
        if(type.equals("all")){
            type = "";
        }
        List<Report> report = reportMapper.findReport(s, e, type, (page-1)*size, size);
        return new ResponseEntity<List<Report>>(report ,HttpStatus.OK);
    }

    @RequestMapping("/report/count")
    public ResponseEntity<?> reportCount(@RequestParam(required=false, defaultValue = "") String start, @RequestParam(required=false, defaultValue = "") String end, @RequestParam(required=false, defaultValue = "") String type, HttpServletRequest r1) throws Exception {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        Date s = null;
        Date e = null;
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(!start.equals("")){
            s = format.parse(start);
        }
        if(!end.equals("")){
            e = format.parse(end);
        }
        if(type.equals("all")){
            type = "";
        }
        int count = reportMapper.findCount(s, e, type);
        JSONObject obj = new JSONObject();
        obj.put("count", count);
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }

    @RequestMapping("/analysis")
    public ResponseEntity<?> analysis(@RequestParam(required=false, defaultValue = "") String content, HttpServletRequest r1) throws IOException {
        String r = sendPost("http://travel.natapp1.cc/gxyq-web/emotion/analysis?content=" +java.net.URLEncoder.encode(content,"utf-8"));
        r = r.replace("positive", "key_words");
        r = r.replace("seed_id", "key_words");
        r = r.replace("&quot;", "");
        JSONObject obj = new JSONObject();
        obj.put("data", r);
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }

    @RequestMapping("/reportData")
    public ResponseEntity<?> reportData(@RequestParam(required=true, defaultValue = "") String type, HttpServletRequest r1) throws IOException {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        Date start = new Date();
        System.out.println(type);
        Date end = DateUtil.getEndDayOfYesterDay();
        JSONObject result = new JSONObject();
        JSONArray array = new JSONArray();
        if(type.equals("daily")){
            Date now = new Date();
            Calendar rightNow = Calendar.getInstance();
            rightNow.setTime(now);
            rightNow.add(Calendar.HOUR , -24);
            rightNow.set(Calendar.HOUR_OF_DAY, 0);
            rightNow.set(Calendar.MINUTE, 0);
            rightNow.set(Calendar.SECOND, 0);
            start = rightNow.getTime();
            System.out.println(start);
            List<HashMap> r = dataMapper.findData(start, end);
            int danger = 0;
            int neg = 0;
            int middle = 0;
            int pos = 0;
            for(int a=0; a<r.size(); a++){
                String t = r.get(a).get("type").toString();
                if( t.equals("1")){
                    danger = Integer.parseInt(r.get(a).get("count").toString());
                }else if( t.equals("2")){
                    neg = Integer.parseInt(r.get(a).get("count").toString());
                }else if( t.equals("3")){
                    middle = Integer.parseInt(r.get(a).get("count").toString());
                }else if( t.equals("4")){
                    pos = Integer.parseInt(r.get(a).get("count").toString());
                }
            }
            System.out.println(12);
            result.put("all", danger + neg + middle + pos);
            result.put("pos", pos);
            result.put("middle", middle);
            result.put("neg", danger + neg);
            result.put("danger", danger);
            List<Article> articles =  articleMapper.findReadNum(currentUser.getId(), "2", start, end);
            result.put("read", articles.size());
            JSONArray detail = new JSONArray();
            System.out.println(11);
            for(Date s = start; s.before(end); s=DateUtil.getNextHour(s,1)){
                Date d1 = s;
                Date d2 = DateUtil.getNextHour(s,1);
                System.out.println(s);
                System.out.println(13);
                List<HashMap> r2 = dataMapper.findData(d1, d2);
                int danger1 = 0;
                int neg1 = 0;
                int middle1 = 0;
                int pos1 = 0;
                for(int a=0; a<r2.size(); a++){
                    String t1 = r2.get(a).get("type").toString();
                    if( t1.equals("1")){
                        danger1 = Integer.parseInt(r2.get(a).get("count").toString());
                    }else if( t1.equals("2")){
                        neg1 = Integer.parseInt(r2.get(a).get("count").toString());
                    }else if( t1.equals("3")){
                        middle1 = Integer.parseInt(r2.get(a).get("count").toString());
                    }else if( t1.equals("4")){
                        pos1 = Integer.parseInt(r2.get(a).get("count").toString());
                    }
                }
                JSONObject ob = new JSONObject();
                SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm");
                ob.put("date", format.format(s));
                ob.put("pos", pos1);
                ob.put("neg", neg1 + danger1);
                ob.put("middle", middle1);
                detail.add(ob);
                result.put("detail", detail);
            }
        }else if(type.equals("weekly")){
            start = DateUtil.getBeginDayOfLastWeek();
            end = DateUtil.getEndDayOfLastWeek();
            List<HashMap> r = dataMapper.findData(start, end);
            int danger = 0;
            int neg = 0;
            int middle = 0;
            int pos = 0;
            for(int a=0; a<r.size(); a++){
                System.out.println(r.get(a));
                System.out.println(r.get(a).get("count"));
                System.out.println(r.get(a).get("type"));
                String t = r.get(a).get("type").toString();
                if( t.equals("1")){
                    danger = Integer.parseInt(r.get(a).get("count").toString());
                }else if( t.equals("2")){
                    neg = Integer.parseInt(r.get(a).get("count").toString());
                }else if( t.equals("3")){
                    middle = Integer.parseInt(r.get(a).get("count").toString());
                }else if( t.equals("4")){
                    pos = Integer.parseInt(r.get(a).get("count").toString());
                }
            }
            result.put("all", danger + neg + middle + pos);
            result.put("pos", pos);
            result.put("middle", middle);
            result.put("neg", danger + neg);
            result.put("danger", danger);
            List<Article> articles =  articleMapper.findReadNum(currentUser.getId(), "2", start, end);
            result.put("read", articles.size());
            JSONArray detail = new JSONArray();
            for(Date s = start; s.before(end); s=DateUtil.getNextDay(s,1)){
                Date d1 = s;
                Date d2 = DateUtil.getNextDay(s,1);
                List<HashMap> r2 = dataMapper.findData(d1, d2);
                int danger1 = 0;
                int neg1 = 0;
                int middle1 = 0;
                int pos1 = 0;
                for(int a=0; a<r2.size(); a++){
                    String t1 = r2.get(a).get("type").toString();
                    if( t1.equals("1")){
                        danger1 = Integer.parseInt(r2.get(a).get("count").toString());
                    }else if( t1.equals("2")){
                        neg1 = Integer.parseInt(r2.get(a).get("count").toString());
                    }else if( t1.equals("3")){
                        middle1 = Integer.parseInt(r2.get(a).get("count").toString());
                    }else if( t1.equals("4")){
                        pos1 = Integer.parseInt(r2.get(a).get("count").toString());
                    }
                }
                JSONObject ob = new JSONObject();
                SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
                ob.put("date", format.format(s));
                ob.put("pos", pos1);
                ob.put("neg", neg1 + danger1);
                ob.put("middle", middle1);
                detail.add(ob);
                result.put("detail", detail);
            }
        }else if(type.equals("monthly")){
            start = DateUtil.getBeginDayOfMonth();
            List<HashMap> r = dataMapper.findData(start, end);
            int danger = 0;
            int neg = 0;
            int middle = 0;
            int pos = 0;
            for(int a=0; a<r.size(); a++){
                System.out.println(r.get(a));
                System.out.println(r.get(a).get("count"));
                System.out.println(r.get(a).get("type"));
                String t = r.get(a).get("type").toString();
                if( t.equals("1")){
                    danger = Integer.parseInt(r.get(a).get("count").toString());
                }else if( t.equals("2")){
                    neg = Integer.parseInt(r.get(a).get("count").toString());
                }else if( t.equals("3")){
                    middle = Integer.parseInt(r.get(a).get("count").toString());
                }else if( t.equals("4")){
                    pos = Integer.parseInt(r.get(a).get("count").toString());
                }
            }
            result.put("all", danger + neg + middle + pos);
            result.put("pos", pos);
            result.put("middle", middle);
            result.put("neg", danger + neg);
            result.put("danger", danger);
            List<Article> articles =  articleMapper.findReadNum(currentUser.getId(), "2", start, end);
            result.put("read", articles.size());
            JSONArray detail = new JSONArray();
            for(Date s = start; s.before(end); s=DateUtil.getNextDay(s,1)){
                Date d1 = s;
                Date d2 = DateUtil.getNextDay(s,1);
                List<HashMap> r2 = dataMapper.findData(d1, d2);
                int danger1 = 0;
                int neg1 = 0;
                int middle1 = 0;
                int pos1 = 0;
                for(int a=0; a<r2.size(); a++){
                    String t1 = r2.get(a).get("type").toString();
                    if( t1.equals("1")){
                        danger1 = Integer.parseInt(r2.get(a).get("count").toString());
                    }else if( t1.equals("2")){
                        neg1 = Integer.parseInt(r2.get(a).get("count").toString());
                    }else if( t1.equals("3")){
                        middle1 = Integer.parseInt(r2.get(a).get("count").toString());
                    }else if( t1.equals("4")){
                        pos1 = Integer.parseInt(r2.get(a).get("count").toString());
                    }
                }
                JSONObject ob = new JSONObject();
                SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
                ob.put("date", format.format(s));
                ob.put("pos", pos1);
                ob.put("neg", neg1 + danger1);
                ob.put("middle", middle1);
                detail.add(ob);
                result.put("detail", detail);
            }
        }else if(type.equals("seasonly")){
            start = DateUtil.getBeginDayOfLastMonth();
            result.put("all", 0);
            result.put("pos", 0);
            result.put("middle", 0);
            result.put("neg", 0);
            result.put("danger", 0);
            result.put("read", 0);
            result.put("detail", new JSONArray());
        }else{
            start = DateUtil.getBeginDayOfYear();
            result.put("all", 0);
            result.put("pos", 0);
            result.put("middle", 0);
            result.put("neg", 0);
            result.put("danger", 0);
            result.put("read", 0);
            result.put("detail", new JSONArray());
        }

        return new ResponseEntity<JSONObject>(result ,HttpStatus.OK);
    }

    /**
     * post请求传输json数据
     *
     * @param url
     * @param
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String sendPost(String url) throws ClientProtocolException, IOException {
        String result = "";
        // 创建httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // 创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);
        // 设置参数到请求对象中
       // StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
       // stringEntity.setContentEncoding("utf-8");
       // httpPost.setEntity(stringEntity);
        // 执行请求操作，并拿到结果（同步阻塞）
        CloseableHttpResponse response = httpClient.execute(httpPost);
        // 获取结果实体
        // 判断网络连接状态码是否正常(0--200都数正常)
        result = EntityUtils.toString(response.getEntity(), "utf-8");
        // 释放链接
        response.close();
        return result;
    }



}
