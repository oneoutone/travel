package travel.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import travel.auth.HttpError;
import travel.auth.UserService;
import travel.entity.Advice;
import travel.entity.DataRequest;
import travel.entity.Follow;
import travel.entity.User;
import travel.mapper.DataRequestMapper;
import travel.mapper.FollowMapper;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/follow")
public class FollowController {

    @Autowired
    private UserService userService;

    @Autowired
    private FollowMapper followMapper;

    @PostMapping(value = "")
    public ResponseEntity<?> addDataRequest(@RequestBody Follow follow, HttpServletRequest r1) {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        follow.setUserId(currentUser.getId());
        follow.setCreated(new Date());

        int result = followMapper.insertFollow(follow);
        JSONObject obj = new JSONObject();
        obj.put("count", result);
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }

    @RequestMapping("/countByUserId")
    public ResponseEntity<?> countById(HttpServletRequest r1) throws Exception {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        int count = followMapper.findByAllCount(currentUser.getId());
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
        List<Follow> follows = followMapper.findByAllList(currentUser.getId(), (page-1)*size, size);
        return new ResponseEntity<List<Follow>>(follows ,HttpStatus.OK);
    }

    @RequestMapping("/findByUserId")
    public ResponseEntity<?> listByUserId(@RequestParam(required=false, defaultValue = "") String articleId, HttpServletRequest r1) throws IOException {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        Follow follow = followMapper.findByArticleId(articleId, currentUser.getId());
        JSONObject obj = new JSONObject();
        if(follow == null){
            obj.put("result", false);
        }else{
            obj.put("result", true);
        }
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }

    @PostMapping(value = "/delete")
    public ResponseEntity<?> deleteById(@RequestBody JSONObject request, HttpServletRequest r1) {
        User currentUser = userService.getUserByHeader(r1);
        if(currentUser  == null){
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        int result = followMapper.deleteById(request.get("articleId").toString(), currentUser.getId());
        JSONObject obj = new JSONObject();
        obj.put("count", result);
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }

}
