package travel.controller;


import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import travel.auth.HttpError;
import travel.entity.Company;
import travel.entity.DataSource;
import travel.entity.User;
import travel.entity.WarningSet;
import travel.mapper.CompanyMapper;
import travel.mapper.JWTTokenMapper;
import travel.mapper.UserMapper;
import travel.mapper.WarningSetMapper;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/company")
public class CompanyController {
    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WarningSetMapper warningSetMapper;

    /**
     * add company client
     */
    @PostMapping(value = "")
    public ResponseEntity<?> addCompanyClient(@RequestBody JSONObject request) throws Exception {
        if (request.get("adminUserName") == null) {
            return new ResponseEntity<HttpError>(new HttpError(400, "没有管理员用户名"), HttpStatus.BAD_REQUEST);
        }
        User u = userMapper.findByUsername(request.get("adminUserName").toString());
        if (u != null) {
            return new ResponseEntity<HttpError>(new HttpError(400, "管理员用户名已经存在"), HttpStatus.BAD_REQUEST);
        }
        Company company = new Company();
        company.setCreated(new Date());
        if (request.get("location") != null) {
            company.setLocation(request.get("location").toString());
        }
        company.setName(request.get("name").toString());
        if (request.get("regNo") != null) {
            company.setRegNo(request.get("regNo").toString());
        }
        if (request.get("acNo") != null) {
            company.setAcNo(request.get("acNo").toString());
        }
        if (request.get("bank") != null) {
            company.setBank(request.get("bank").toString());
        }
        if (request.get("address") != null) {
            company.setAddress(request.get("address").toString());
        }
        if (request.get("phone") != null) {
            company.setPhone(request.get("phone").toString());
        }
        if (request.get("contactPeople") != null) {
            company.setContactPeople(request.get("contactPeople").toString());
        }
        company.setType("B");
        company.setStatus("normal");
        company.setManagerId(Long.parseLong(request.get("managerId").toString()));
        int i = companyMapper.insertCompany(company);
        System.out.println(company.getId());
        User admin = new User();
        admin.setUsername(request.get("adminUserName").toString());
        admin.setPassword(request.get("adminPassword").toString());
        if(request.get("adminName") != null) {
            admin.setName(request.get("adminName").toString());
        }
        admin.setType("B");
        admin.setCompanyId(company.getId());
        userMapper.insertUser(admin);
        WarningSet set = new WarningSet();
        set.setUserId(admin.getId());
        set.setType("default");
        set.setNegValue(0.3);
        set.setReadNum(0);
        set.setDiscussNum(0);
        set.setShareNum(0);
        set.setCreated(new Date());
        warningSetMapper.insertWarningSet(set);
        companyMapper.updateAdminId(admin.getId(), company.getId());
        return new ResponseEntity<Company>(company, HttpStatus.OK);
    }

    @PostMapping(value = "/update")
    public ResponseEntity<?> update(@RequestBody JSONObject request) throws Exception {
        if (request.get("id") == null) {
            return new ResponseEntity<HttpError>(new HttpError(400, "没有单位的id"), HttpStatus.BAD_REQUEST);
        }
        Company company = companyMapper.findById(Long.parseLong(request.get("id").toString()));
        if(request.get("name") != null){
            company.setName(request.get("name").toString());
        }
        if(request.get("location") != null){
            company.setLocation(request.get("location").toString());
        }
        if (request.get("regNo") != null) {
            company.setRegNo(request.get("regNo").toString());
        }
        if (request.get("acNo") != null) {
            company.setAcNo(request.get("acNo").toString());
        }
        if (request.get("bank") != null) {
            company.setBank(request.get("bank").toString());
        }
        if (request.get("address") != null) {
            company.setAddress(request.get("address").toString());
        }
        if (request.get("phone") != null) {
            company.setPhone(request.get("phone").toString());
        }
        if (request.get("contactPeople") != null) {
            company.setContactPeople(request.get("contactPeople").toString());
        }
        if (request.get("status") != null) {
            company.setStatus(request.get("status").toString());
        }
        if (request.get("managerId") != null) {
            company.setManagerId(Long.parseLong(request.get("managerId").toString()));
        }
        int i = companyMapper.updateCompany(company);
        return new ResponseEntity<Company>(company, HttpStatus.OK);
    }

    @GetMapping("/findById")
    public ResponseEntity<?> getUserById(@RequestParam(required=false, defaultValue = "") String id, HttpServletRequest r1) {
        Company company = companyMapper.findById(Long.parseLong(id));
        return new ResponseEntity<Company>(company ,HttpStatus.OK);
    }

    @RequestMapping("/allList")
    public ResponseEntity<?> allList(@RequestParam(required=false, defaultValue = "") String filter, @RequestParam(required=false, defaultValue = "") String status, @RequestParam(required=false, defaultValue = "") String managerId, @RequestParam(required=false, defaultValue = "") int page, @RequestParam(required=false, defaultValue = "") int size, HttpServletRequest r1) throws IOException {
        if(status.equals("全部")){
            status = "";
        }
        List<HashMap> list = userMapper.findCompanyList("B", filter, status, managerId .equals("") ? 0 : Long.parseLong(managerId),(page-1)*size, size);
        return new ResponseEntity<List<HashMap>>(list ,HttpStatus.OK);
    }

    @RequestMapping("/allCount")
    public ResponseEntity<?> allCount(@RequestParam(required=false, defaultValue = "") String filter,  @RequestParam(required=false, defaultValue = "") String status, @RequestParam(required=false, defaultValue = "") String managerId, HttpServletRequest r1) throws IOException {
        if(status.equals("全部")){
            status = "";
        }
        int count = userMapper.findCompanyCount("B", filter, status, managerId .equals("") ? 0 : Long.parseLong(managerId));
        JSONObject obj = new JSONObject();
        obj.put("count", count);
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }
}
