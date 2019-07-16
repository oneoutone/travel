package travel.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.mail.util.MailSSLSocketFactory;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import travel.auth.HttpError;
import travel.entity.*;
import travel.mapper.*;
import travel.util.HttpUtil;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class QuartzService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BatchMapper batchMapper;

    @Autowired
    private WarningSetMapper warningSetMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private DataMapper dataMapper;

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private DataRequestMapper dataRequestMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private DataNotificationMapper dataNotificationMapper;

    public void sendEmail(String email, String content) throws Exception{
        System.out.println("start send email");
        String myEmailAccount = "gangxiyuqing@hotmail.com";
        String myEmailPassword = "yuiop67890";

        // 发件人邮箱的 SMTP 服务器地址, 必须准确, 不同邮件服务器地址不同, 一般(只是一般, 绝非绝对)格式为: smtp.xxx.com
        // 网易126邮箱的 SMTP 服务器地址为: smtp.126.com
        String myEmailSMTPHost = "smtp.office365.com";

        // 收件人邮箱（替换为自己知道的有效邮箱）
        String receiveMailAccount = email;
        Properties props = new Properties();                    // 参数配置
        props.setProperty("mail.transport.protocol", "smtp");   // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.smtp.host", myEmailSMTPHost);   // 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.smtp.auth", "true");
        MailSSLSocketFactory sf = null;
        try {
            sf = new MailSSLSocketFactory();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        sf.setTrustAllHosts(true);
//        pro.put("mail.smtp.ssl.enable", "true");
//        pro.put("mail.smtp.ssl.trust", "*");     // 不影响
        props.put("mail.smtp.ssl.socketFactory", sf); //这句加上后才发送成功，不加的话就发送不成功
        props.put("mail.smtp.socketFactory.fallback", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props);
        session.setDebug(true);
        // 3. 创建MimeMessage一封邮件

        MimeMessage message = new MimeMessage(session);

//        InternetAddress [] toAddr = new InternetAddress[2];
//        toAddr[0] = new InternetAddress(myEmailAccount);//自己的邮箱
//        toAddr[1] = new InternetAddress(receiveMailAccount);//对方的邮箱
//
//        message.setRecipients(MimeMessage.RecipientType.TO, toAddr);

       // message.addRecipients(MimeMessage.RecipientType.TO , InternetAddress.parse(myEmailAccount));

        // 2. From: 发件人
        message.setFrom(new InternetAddress(myEmailAccount, "云平台", "UTF-8"));

        // 3. To: 收件人（可以增加多个收件人、抄送、密送）
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiveMailAccount, "", "UTF-8"));

        // 4. Subject: 邮件主题
        message.setSubject("舆情报警", "UTF-8");

        // 5. Content: 邮件正文（可以使用html标签）
        message.setContent(content, "text/html;charset=UTF-8");
        // 6. 设置发件时间
        message.setSentDate(new Date());

        // 7. 保存设置
        message.saveChanges();

        // 4. 根据 Session 获取邮件传输对象
        Transport transport = session.getTransport();

        transport.connect(myEmailAccount, myEmailPassword);

        // 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
        transport.sendMessage(message, message.getAllRecipients());

        // 7. 关闭连接
        transport.close();
    }

    @Scheduled(cron = "0 */5 * * * ?")
    public void sendNotification() throws Exception{
    //start batch and init batch record
        System.out.println("start notification batch");
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
        List<User> users = userMapper.findUsersWithCompany();
        System.out.println("find user info");
        System.out.println(users.size());
        System.out.println(users);
        for(int i=0; i<array.size(); i++){
            JSONObject obj = array.getJSONObject(i).getJSONObject("result");
            int readNum = 0;
            int discussNum = 0;
            int shareNum = 0;
            double postive = 1;
            double negtive = 1;
            String source_name = "";
            String path = "";
            String title = "";
            String content = "";
            String city = "";
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
            if(obj.get("city") != null){
                city = obj.get("city").toString();
            }
            System.out.println(readNum);
            System.out.println(discussNum);
            System.out.println(shareNum);
            System.out.println(postive);
            System.out.println(negtive);
            System.out.println(city);
            for(int j =0; j<users.size(); j++){
                System.out.println(users.get(j).isSpecify_warn_setting());
                Company company = companyMapper.findById(users.get(j).getCompanyId());
                if(company.getLocation() != null && city.indexOf(company.getLocation()) < 0){
                    continue;
                }
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
                                String u = "http://v.juhe.cn/sms/send.php?mobile="+phones[c]+"&tpl_id=151692&tpl_value=" +URLEncoder.encode("#title#="+title+"&#url#="+path)+"&key=e7ca3042db7bb9ef00e4ba86b6a61e71";
                                System.out.println(u);
                                HttpUtil.sendGet(u);
                            }
                        }
                    }
                    if(users.get(j).isEmail_warn() == true && users.get(j).getWarn_emails() != null){
                        String con = "消息标题="+title+", 消息地址："+path;
                        String[] emails = users.get(j).getWarn_emails().split(",");
                        for(int b=0; b<emails.length; b++){
                            if(!emails[b].equals("")){
                                sendEmail(emails[b], con);
                            }
                        }
                    }
                }
            }

            System.out.println("send notification2");
            List<DataRequest> requests = dataRequestMapper.findAll("运行中");
            for(int b=0; b<requests.size(); b++){
                if(!requests.get(b).getType().equals("即时发送")){
                    continue;
                }
                boolean send = false;
                System.out.println(title);
                String urls = requests.get(b).getUrls();
                if(urls.indexOf(source_name) >= 0){
                    List<String> words= Arrays.asList(requests.get(b).getWords().split(","));
                    for(int c=0; c<words.size(); c++){
                        if(content.indexOf(words.get(c)) >= 0){
                            send = true;
                            break;
                        }
                    }
                    int count = notificationMapper.findCount(requests.get(b).getId(), obj.get("article_id").toString());
                    if(send == true){
                        if(requests.get(b).getEmail() != null){
                            String con = "消息标题="+title+", 消息地址："+path;
                            sendEmail(requests.get(b).getEmail(), con);
                        }
                        if(requests.get(b).getPhone() != null){
                            String u = "http://v.juhe.cn/sms/send.php?mobile="+requests.get(b).getPhone()+"&tpl_id=151692&tpl_value=" +URLEncoder.encode("#title#="+title+"&#url#="+path)+"&key=e7ca3042db7bb9ef00e4ba86b6a61e71";
                            System.out.println(u);
                            HttpUtil.sendGet(u);
                        }
                    }
                }
            }
        }
    }






    @Scheduled(cron = "0 0 2 * * ?")
    public void fetchData() throws Exception{
        System.out.println("hello");
        Date now = new Date();
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(now);
        rightNow.add(Calendar.HOUR , -24);
        rightNow.set(Calendar.HOUR_OF_DAY, 0);
        rightNow.set(Calendar.MINUTE, 0);
        rightNow.set(Calendar.SECOND, 0);
        Date start = rightNow.getTime();
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        Date end = today.getTime();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        String st = format.format(start);
        String ed = format.format(end);
        String url = "http://travel.natapp1.cc/gxyq-web/article/newscount?article_type="+4+"&sentiment="+4+"&keywordlist=&website=&page_size=1000&starttime="+st+"&endtime="+ed;
        JSONObject r = request(url);
        int num = Integer.parseInt(r.get("allnum").toString());
        for(int i=1; i<num+1; i++){
            String u = "http://travel.natapp1.cc/gxyq-web/article/news?article_type="+4+"&sentiment="+4+"&keywordlist=&website=&pagenum="+i+"&page_size=1000&starttime="+st+"&endtime="+ed;
            JSONObject records = request(u);
            JSONArray array = records.getJSONArray("data");
            if(array != null && array.size() > 0){
                for(int j=0; j<array.size(); j++){
                    JSONObject obj = array.getJSONObject(j).getJSONObject("result");
                    Data data = new Data();
                    if(obj.get("article_id") != null){
                        String articleId = obj.get("article_id").toString();
                        System.out.println(articleId);
                        int c = dataMapper.findCountByArticleId(articleId);
                        if(c == 0) {
                            try {
                                data.setArticleId(obj.get("article_id") == null ? "" : obj.get("article_id").toString());
                                data.setContent(obj.get("content") == null ? "" : obj.get("content").toString());
                                data.setTitle(obj.get("title") == null ? "" : obj.get("title").toString());
                                data.setSource_name(obj.get("source_name") == null ? "" : obj.get("source_name").toString());
                                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0");
                                data.setPublish_time(obj.get("publish_time") == null ? now : format1.parse(obj.get("publish_time").toString()));
                                Float pos = obj.get("postive") == null ? new Float(0.5) : Float.parseFloat(obj.get("postive").toString());
                                Float neg = obj.get("negtive") == null ? new Float(0.5) : Float.parseFloat(obj.get("negative").toString());
                                data.setPostive(pos);
                                if(neg > 0.8){
                                    data.setType("1");
                                }else if(neg > 0.65){
                                    data.setType("2");
                                }else if(pos > 0.65){
                                    data.setType("4");
                                }else {
                                    data.setType("3");
                                }
                                dataMapper.insertData(data);
                            } catch (Exception e) {
                                continue;
                            }
                        }
                    }
                }
            }
        }
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void generateDailyRecord() throws Exception{
        Date now = new Date();
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(now);
        rightNow.add(Calendar.HOUR , -24);
        rightNow.set(Calendar.HOUR, 0);
        rightNow.set(Calendar.MINUTE, 0);
        rightNow.set(Calendar.SECOND, 0);
        Date start = rightNow.getTime();
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        Date end = today.getTime();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        String st = format.format(start);
        String ed = format.format(end);
        String url = "http://travel.natapp1.cc/gxyq-web/article/newscount?article_type="+4+"&sentiment="+4+"&keywordlist=&website=&page_size=1000&starttime="+st+"&endtime="+ed;
        JSONObject r = request(url);
        //String url = "http://travel.natapp1.cc/gxyq-web/article/news?article_type="+article_type+"&sentiment="+sentiment+"&keywordlist="+keyWords+"&website="+resourceString+"&pagenum="+page+"&page_size="+size+"&starttime=&endtime=";
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void generateDailyReport() throws Exception{
        Date now = new Date();
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(now);
        rightNow.add(Calendar.HOUR , -24);
        rightNow.set(Calendar.HOUR, 0);
        rightNow.set(Calendar.MINUTE, 0);
        rightNow.set(Calendar.SECOND, 0);
        Date start = rightNow.getTime();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        String st = format.format(start);
        String ed = format.format(start);
        String url = "http://travel.natapp1.cc/gxyq-web/resport/news?starttime="+st+"&endtime="+ed+"&page_size=1000&pagenum=1";
        JSONObject r = request(url);
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
            rows.createCell(1).setCellValue(obj.get("webside_url") != null ? obj.get("webside_url").toString() : "");
            rows.createCell(2).setCellValue(obj.get("newscount") != null ? obj.get("newscount").toString() : "");
        }
        File xlsFile = new File("/home/www/caiji/cloud/report/"+st+"-"+ed+".xls");
        FileOutputStream xlsStream = new FileOutputStream(xlsFile);
        workbook.write(xlsStream);
        String u = "http://hadupu.cn/cloud/report/"+st+"-"+ed+".xls";
        Report report = new Report();
        report.setType("daily");
        report.setStart(start);
        report.setEnd(now);
        report.setUrl(u);
        report.setCreated(new Date());
        reportMapper.insertReport(report);
    }

    @Scheduled(cron = "0 0 1 * * MON")
    public void generateWeeklyReport() throws Exception{
        Date now = new Date();
        Calendar lastMonday = Calendar.getInstance();
        lastMonday.setTime(now);
        lastMonday.add(Calendar.DATE, -7);
        lastMonday.set(Calendar.HOUR, 0);
        lastMonday.set(Calendar.MINUTE, 0);
        lastMonday.set(Calendar.SECOND, 0);
        Date start = lastMonday.getTime();

        Calendar lastSunday = Calendar.getInstance();
        lastSunday.setTime(now);
        lastSunday.add(Calendar.DATE, -1);
        lastSunday.set(Calendar.HOUR, 0);
        lastSunday.set(Calendar.MINUTE, 0);
        lastSunday.set(Calendar.SECOND, 0);
        Date end = lastSunday.getTime();

        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        String st = format.format(start);
        String ed = format.format(end);
        System.out.print(start);
        System.out.print(end);
        String url = "http://travel.natapp1.cc/gxyq-web/resport/news?starttime="+st+"&endtime="+ed+"&page_size=1000&pagenum=1";
        JSONObject r = request(url);
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

        File xlsFile = new File( "/home/www/caiji/cloud/report/"+st+"-"+ed+".xls");
        FileOutputStream xlsStream = new FileOutputStream(xlsFile);
        workbook.write(xlsStream);
        String u = "http://hadupu.cn/cloud/report/"+st+"-"+ed+".xls";
        Report report = new Report();
        report.setType("weekly");
        report.setStart(start);
        report.setEnd(now);
        report.setUrl(u);
        report.setCreated(new Date());
        reportMapper.insertReport(report);
    }

    @Scheduled(cron = "0 0 1 1 * ?")
    public void generateMonthlyReport() throws Exception{
        Date now = new Date();

        Calendar lastMonthStart = Calendar.getInstance();
        lastMonthStart.setTime(now);
        lastMonthStart.add(Calendar.MONTH, -1);
        lastMonthStart.set(Calendar.DAY_OF_MONTH,1);
        lastMonthStart.set(Calendar.HOUR, 0);
        lastMonthStart.set(Calendar.MINUTE, 0);
        lastMonthStart.set(Calendar.SECOND, 0);
        Date start = lastMonthStart.getTime();

        Calendar lastMonthEnd = Calendar.getInstance();
        lastMonthEnd.setTime(now);
        lastMonthEnd.add(Calendar.MONTH, -1);
        lastMonthEnd.set(Calendar.DAY_OF_MONTH,1);
        lastMonthEnd.set(Calendar.HOUR, 0);
        lastMonthEnd.set(Calendar.MINUTE, 0);
        lastMonthEnd.set(Calendar.SECOND, 0);
        Date end = lastMonthEnd.getTime();

        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        String st = format.format(start);
        String ed = format.format(end);
        System.out.print(start);
        System.out.print(end);
        String url = "http://travel.natapp1.cc/gxyq-web/resport/news?starttime="+st+"&endtime="+ed+"&page_size=1000&pagenum=1";
        JSONObject r = request(url);
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

        File xlsFile = new File( "/home/www/caiji/cloud/report/"+st+"-"+ed+".xls");
        FileOutputStream xlsStream = new FileOutputStream(xlsFile);
        workbook.write(xlsStream);
        String u = "http://hadupu.cn/cloud/report/"+st+"-"+ed+".xls";
        Report report = new Report();
        report.setType("monthly");
        report.setStart(start);
        report.setEnd(now);
        report.setUrl(u);
        report.setCreated(new Date());
        reportMapper.insertReport(report);
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

}
