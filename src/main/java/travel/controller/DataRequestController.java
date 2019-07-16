package travel.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import travel.auth.HttpError;
import travel.auth.UserService;
import travel.entity.DataRequest;
import travel.entity.User;
import travel.entity.WarningSet;
import travel.mapper.DataRequestMapper;
import travel.mapper.UserMapper;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/dataRequest")
public class DataRequestController {

    @Autowired
    private UserService userService;

    @Autowired
    private DataRequestMapper dataRequestMapper;

    @PostMapping(value = "/add")
    public ResponseEntity<?> addDataRequest(@RequestBody DataRequest dataRequest, HttpServletRequest r1) {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        dataRequest.setUserId(currentUser.getId());
        dataRequest.setCreated(new Date());
        dataRequest.setStatus("待确认");
        int result = dataRequestMapper.insertDataRequest(dataRequest);
        JSONObject obj = new JSONObject();
        obj.put("count", result);
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }

    @PostMapping(value = "/update")
    public ResponseEntity<?> update(@RequestBody DataRequest dataRequest, HttpServletRequest r1) {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        if(dataRequest.getWords() != null){
            dataRequestMapper.updateWords(dataRequest.getWords(), dataRequest.getId());
        }
        if(dataRequest.getUrls() != null){
            dataRequestMapper.updateUrls(dataRequest.getUrls(), dataRequest.getId());
        }
        if(dataRequest.getStatus() != null){
            dataRequestMapper.updateStatus(dataRequest.getStatus(), dataRequest.getId());
        }
        JSONObject obj = new JSONObject();
        obj.put("result", "ok");
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }

    @RequestMapping("/record")
    public ResponseEntity<?> DataRequestList(@RequestParam(required=false, defaultValue = "") String id, HttpServletRequest r1) throws IOException {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        DataRequest req = dataRequestMapper.findById(Long.parseLong(id));
        return new ResponseEntity<DataRequest>(req ,HttpStatus.OK);
    }

    @RequestMapping("/list")
    public ResponseEntity<?> DataRequestList(@RequestParam(required=false, defaultValue = "") int page, @RequestParam(required=false, defaultValue = "") int size, HttpServletRequest r1) throws IOException {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        List<DataRequest> sets = dataRequestMapper.findByUserId(currentUser.getId(), (page-1)*size, size);
        return new ResponseEntity<List<DataRequest>>(sets ,HttpStatus.OK);
    }

    @RequestMapping("/count")
    public ResponseEntity<?> warningSetCount(@RequestParam(required=false, defaultValue = "") String word, HttpServletRequest r1) throws IOException {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        int count = dataRequestMapper.findCountByUserId(currentUser.getId());
        JSONObject obj = new JSONObject();
        obj.put("count", count);
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }

    @RequestMapping("/allList")
    public ResponseEntity<?> DataRequestAllList(@RequestParam(required=false, defaultValue = "") String name, @RequestParam(required=false, defaultValue = "") String status, @RequestParam(required=false, defaultValue = "") int page, @RequestParam(required=false, defaultValue = "") int size, HttpServletRequest r1) throws IOException {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        if(status.equals("全部")){
            status = "";
        }
        List<DataRequest> sets = dataRequestMapper.findByAllList(name, status,(page-1)*size, size);
        return new ResponseEntity<List<DataRequest>>(sets ,HttpStatus.OK);
    }

    @RequestMapping("/allCount")
    public ResponseEntity<?> warningSetAllCount(@RequestParam(required=false, defaultValue = "") String name, @RequestParam(required=false, defaultValue = "") String status, HttpServletRequest r1) throws IOException {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        if(status.equals("全部")){
            status = "";
        }
        int count = dataRequestMapper.findByAllCount(name, status);
        JSONObject obj = new JSONObject();
        obj.put("count", count);
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }
}
