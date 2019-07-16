package travel.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import travel.auth.HttpError;
import travel.auth.UserService;
import travel.entity.Advice;
import travel.entity.Company;
import travel.entity.DataRequest;
import travel.entity.User;
import travel.mapper.AdviceMapper;
import travel.mapper.AuthCodeMapper;
import travel.mapper.CompanyMapper;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/advice")
public class AdviceController {

    @Autowired
    private AdviceMapper adviceMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyMapper companyMapper;

    @PostMapping(value = "")
    public ResponseEntity<?> addAdvice(@RequestBody JSONObject request, HttpServletRequest r1) throws Exception {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        Advice advice = new Advice();
        advice.setTitle(request.get("title").toString());
        advice.setContent(request.get("content").toString());
        advice.setCreated(new Date());
        advice.setUserId(currentUser.getId());
        Company company = companyMapper.findById(currentUser.getCompanyId());
        if(company != null){
            advice.setManagerId(company.getManagerId());
        }
        adviceMapper.insertAdvice(advice);
        return ResponseEntity.ok().build();
    }

    @RequestMapping("/countByUserId")
    public ResponseEntity<?> countById(HttpServletRequest r1) throws Exception {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        int count = adviceMapper.findAdviceCountByUser(currentUser.getId());
        JSONObject obj = new JSONObject();
        obj.put("count", count);
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }


    @RequestMapping("/listByUserId")
    public ResponseEntity<?> listByUserId(@RequestParam(required=false, defaultValue = "") int page, @RequestParam(required=false, defaultValue = "") int size, HttpServletRequest r1) throws IOException {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        List<Advice> advices = adviceMapper.findAdviceListByUserId(currentUser.getId(), (page-1)*size, size);
        return new ResponseEntity<List<Advice>>(advices ,HttpStatus.OK);
    }


}
