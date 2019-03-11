package travel.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import travel.auth.HttpError;
import travel.entity.AuthCode;
import travel.mapper.AuthCodeMapper;
import travel.mapper.UserMapper;
import travel.util.HttpUtil;

import java.io.IOException;
import java.util.Date;
import java.util.Random;

@RestController
@RequestMapping("/api/authCode")
public class AuthCodeController {

    @Autowired
    private AuthCodeMapper authCodeMapper;

    @PostMapping("/send")
    public ResponseEntity<?> sendAuthCode(@RequestBody JSONObject request) throws IOException {
        String phone = null;
        if(request.get("phone") != null){
            phone = request.get("phone").toString();
        }else{
            return new ResponseEntity<HttpError>(new HttpError(400, "没有手机号"), HttpStatus.BAD_REQUEST);

        }
        Random ran1 = new Random();
        String code = "";
        for(int i=0; i<6; i++){
            code += ran1.nextInt(10);
        }
        Date expire = new Date((new Date()).getTime()+60000);
        AuthCode authCode = new AuthCode();
        authCode.setCode(code);
        authCode.setPhone(phone);
        authCode.setChannel("web_register");
        authCode.setExpire(expire);
        authCode.setUsed(false);
        int r = authCodeMapper.insertAuthCode(authCode);
        if(r != 1){
            return new ResponseEntity<HttpError>(new HttpError(500, "系统内部错误"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        String url = "http://v.juhe.cn/sms/send.php?mobile="+phone+"&tpl_id=106635&tpl_value=%23code%23%3d"+code+"&key=e7ca3042db7bb9ef00e4ba86b6a61e71";
        String result = HttpUtil.sendGet(url);
        if(result == "fail"){
            return new ResponseEntity<HttpError>(new HttpError(500, "发送短信失败"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        JSONObject res = new JSONObject();
        res.put("result", "ok");
        return new ResponseEntity<JSONObject>(res, HttpStatus.OK);
    }

}
