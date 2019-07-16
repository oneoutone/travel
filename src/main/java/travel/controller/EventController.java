package travel.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import travel.auth.HttpError;
import travel.auth.UserService;
import travel.entity.*;
import travel.mapper.BidMapper;
import travel.mapper.EventMapper;
import travel.mapper.UserMapper;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/event")
public class EventController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EventMapper eventMapper;

    @PostMapping(value = "/upsert")
    public ResponseEntity<?> upsertEvent(@RequestBody JSONObject request, HttpServletRequest r1) throws Exception {
        Event event;
        if(request.get("id") == null){
            event = new Event();
            event.setCreated(new Date());
        }else{
            event = eventMapper.findEventById(Long.parseLong(request.get("id").toString()));
        }
        if (request.get("title") != null) {
            event.setTitle(request.get("title").toString());
        }
        if (request.get("publish_time") != null) {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
            event.setPublish_time(format1.parse(request.get("publish_time").toString()));
        }
        if (request.get("location") != null) {
            event.setLocation(request.get("location").toString());
        }
        if (request.get("industry") != null) {
            event.setIndustry(request.get("industry").toString());
        }
        if(request.get("id") == null){
            eventMapper.insertEvent(event);
        }
        JSONObject obj = new JSONObject();
        obj.put("result", "OK");
        return new ResponseEntity<JSONObject>(obj, HttpStatus.OK);
    }

    @RequestMapping("/eventList")
    public ResponseEntity<?> eventList(@RequestParam(required=false, defaultValue = "") String filter, @RequestParam(required=false, defaultValue = "") int page, @RequestParam(required=false, defaultValue = "") int size, HttpServletRequest r1) throws IOException {
        List<Event> events = eventMapper.findEventList(filter,(page-1)*size, size);
        return new ResponseEntity<List<Event>>(events ,HttpStatus.OK);
    }

    @RequestMapping("/eventById")
    public ResponseEntity<?> eventList(@RequestParam(required=false, defaultValue = "") long id,  HttpServletRequest r1) throws IOException {
        Event event = eventMapper.findEventById(id);
        return new ResponseEntity<Event>(event ,HttpStatus.OK);
    }

    @RequestMapping("/eventCount")
    public ResponseEntity<?> eventCount(@RequestParam(required=false, defaultValue = "") String filter, HttpServletRequest r1) throws IOException {
        int count = eventMapper.findEventCount(filter);
        JSONObject obj = new JSONObject();
        obj.put("count", count);
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }

    @PostMapping(value = "/upsertEventArticle")
    public ResponseEntity<?> upsertEventArticle(@RequestBody JSONObject request, HttpServletRequest r1) throws Exception {
        EventArticle eventArticle = new EventArticle();
        eventArticle.setCreated(new Date());
        if (request.get("articleId") != null) {
            eventArticle.setArticleId(request.get("articleId").toString());
        }
        if (request.get("title") != null) {
            eventArticle.setTitle(request.get("title").toString());
        }
        if (request.get("content") != null) {
            eventArticle.setContent(request.get("content").toString());
        }
        if (request.get("publish_time") != null) {
            eventArticle.setPublish_time(request.get("publish_time").toString());
        }
        if (request.get("source_name") != null) {
            eventArticle.setSource_name(request.get("source_name").toString());
        }
        if (request.get("postive") != null) {
            eventArticle.setPostive(Float.parseFloat(request.get("postive").toString()));
        }
        eventArticle.setEventId(Long.parseLong(request.get("eventId").toString()));
        eventMapper.insertEventArticle(eventArticle);
        JSONObject obj = new JSONObject();
        obj.put("result", "OK");
        return new ResponseEntity<JSONObject>(obj, HttpStatus.OK);
    }

    @RequestMapping("/eventArticleList")
    public ResponseEntity<?> eventArticleList(@RequestParam(required=false, defaultValue = "") long eventId, @RequestParam(required=false, defaultValue = "") int page, @RequestParam(required=false, defaultValue = "") int size, HttpServletRequest r1) throws IOException {
        List<EventArticle> eventArticles = eventMapper.findEventArticleList(eventId,(page-1)*size, size);
        return new ResponseEntity<List<EventArticle>>(eventArticles ,HttpStatus.OK);
    }

    @RequestMapping("/eventArticleCount")
    public ResponseEntity<?> eventArticleCount(@RequestParam(required=false, defaultValue = "") long eventId, HttpServletRequest r1) throws IOException {
        int count = eventMapper.findEventArticleCount(eventId);
        JSONObject obj = new JSONObject();
        obj.put("count", count);
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }

    @PostMapping(value = "/deleteEventArticleById")
    public ResponseEntity<?> deleteEventArticleById(@RequestBody JSONObject request, HttpServletRequest r1) throws Exception {
        eventMapper.deleteEventArticle(Long.parseLong(request.get("id").toString()));
        JSONObject obj = new JSONObject();
        obj.put("result", "OK");
        return new ResponseEntity<JSONObject>(obj, HttpStatus.OK);
    }
}
