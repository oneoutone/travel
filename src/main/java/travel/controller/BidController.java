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
import travel.mapper.UserMapper;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/bid")
public class BidController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BidMapper bidMapper;

    @PostMapping(value = "/upsertBidInfo")
    public ResponseEntity<?> updateUserWarn(@RequestBody JSONObject request, HttpServletRequest r1) throws Exception {
        User currentUser = userService.getUserByHeader(r1);
        if (currentUser == null) {
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        BidsInfo bid;
        if(request.get("id") == null){
            bid = new BidsInfo();
            //bidMapper.insertBid(bid);
            bid.setUserId(currentUser.getId());
            bid.setName(request.get("name").toString());
        }else{
            bid = bidMapper.findBidById(Long.parseLong(request.get("id").toString()));
        }
        if (request.get("regNo") != null) {
            bid.setRegNo(request.get("regNo").toString());
        }
        if (request.get("acNo") != null) {
            bid.setAcNo(request.get("acNo").toString());
        }
        if (request.get("bank") != null) {
            bid.setBank(request.get("bank").toString());
        }
        if (request.get("address") != null) {
            bid.setAddress(request.get("address").toString());
        }
        if (request.get("phone") != null) {
            bid.setPhone(request.get("phone").toString());
        }
        if (request.get("contactPeople") != null) {
            bid.setContactPeople(request.get("contactPeople").toString());
        }
        if (request.get("productName") != null) {
            bid.setProductName(request.get("productName").toString());
        }
        if (request.get("industry") != null) {
            bid.setIndustry(request.get("industry").toString());
        }
        if (request.get("keyWords") != null) {
            bid.setKeyWords(request.get("keyWords").toString());
        }
        if (request.get("productAdvantage") != null) {
            bid.setProductAdvantage(request.get("productAdvantage").toString());
        }
        if (request.get("processAdvantage") != null) {
            bid.setProcessAdvantage(request.get("processAdvantage").toString());
        }
        if (request.get("url") != null) {
            bid.setUrl(request.get("url").toString());
        }
        if(request.get("id") == null){
            bidMapper.insertBid(bid);
        }else{
            bidMapper.updateBid(bid);
        }
        JSONObject obj = new JSONObject();
        obj.put("result", "OK");
        return new ResponseEntity<JSONObject>(obj, HttpStatus.OK);
    }

    @PostMapping(value = "/buyer")
    public ResponseEntity<?> createBuyer(HttpServletRequest r1) throws Exception {
        User currentUser = userService.getUserByHeader(r1);
        if (currentUser == null) {
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        Buyer buyer = new Buyer();
        buyer.setCreated(new Date());
        buyer.setUserId(currentUser.getId());
        buyer.setStatus("waiting");
        bidMapper.insertBuyer(buyer);
        JSONObject obj = new JSONObject();
        obj.put("result", "OK");
        return new ResponseEntity<JSONObject>(obj, HttpStatus.OK);
    }

    @PostMapping(value = "/updateBuyer")
    public ResponseEntity<?> updateBuyer(@RequestBody JSONObject request, HttpServletRequest r1) throws Exception {
        User currentUser = userService.getUserByHeader(r1);
        if (currentUser == null) {
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        if(request.get("id") == null){
            return new ResponseEntity<HttpError>(new HttpError(400, "错误的请求"), HttpStatus.BAD_REQUEST);
        }
        bidMapper.updateBuyer(request.get("status").toString(), Long.parseLong(request.get("id").toString()));
        JSONObject obj = new JSONObject();
        obj.put("result", "OK");
        return new ResponseEntity<JSONObject>(obj, HttpStatus.OK);
    }

    @PostMapping(value = "/supplier")
    public ResponseEntity<?> createSupplier( HttpServletRequest r1) throws Exception {
        User currentUser = userService.getUserByHeader(r1);
        if (currentUser == null) {
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        Supplier supplier = new Supplier();
        supplier.setCreated(new Date());
        supplier.setUserId(currentUser.getId());
        supplier.setStatus("waiting");
        bidMapper.insertSupplier(supplier);
        JSONObject obj = new JSONObject();
        obj.put("result", "OK");
        return new ResponseEntity<JSONObject>(obj, HttpStatus.OK);
    }

    @PostMapping(value = "/updateSupplier")
    public ResponseEntity<?> updateSupplier(@RequestBody JSONObject request, HttpServletRequest r1) throws Exception {
        User currentUser = userService.getUserByHeader(r1);
        if (currentUser == null) {
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        if(request.get("id") == null){
            return new ResponseEntity<HttpError>(new HttpError(400, "错误的请求"), HttpStatus.BAD_REQUEST);
        }
        bidMapper.updateSupplier(request.get("status").toString(), Long.parseLong(request.get("id").toString()));
        JSONObject obj = new JSONObject();
        obj.put("result", "OK");
        return new ResponseEntity<JSONObject>(obj, HttpStatus.OK);
    }

    @PostMapping(value = "/distributor")
    public ResponseEntity<?> createDistributor( HttpServletRequest r1) throws Exception {
        User currentUser = userService.getUserByHeader(r1);
        if (currentUser == null) {
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        Distributor distributor = new Distributor();
        distributor.setCreated(new Date());
        distributor.setUserId(currentUser.getId());
        distributor.setStatus("waiting");
        bidMapper.insertDistributor(distributor);
        JSONObject obj = new JSONObject();
        obj.put("result", "OK");
        return new ResponseEntity<JSONObject>(obj, HttpStatus.OK);
    }

    @PostMapping(value = "/updateDistributor")
    public ResponseEntity<?> updateDistributor(@RequestBody JSONObject request, HttpServletRequest r1) throws Exception {
        User currentUser = userService.getUserByHeader(r1);
        if (currentUser == null) {
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        if(request.get("id") == null){
            return new ResponseEntity<HttpError>(new HttpError(400, "错误的请求"), HttpStatus.BAD_REQUEST);
        }
        bidMapper.updateDistributor(request.get("status").toString(), Long.parseLong(request.get("id").toString()));
        JSONObject obj = new JSONObject();
        obj.put("result", "OK");
        return new ResponseEntity<JSONObject>(obj, HttpStatus.OK);
    }

    @RequestMapping("/bidsInfo")
    public ResponseEntity<?> getBidsInfo(HttpServletRequest r1) throws IOException {
        User currentUser = userService.getUserByHeader(r1);
        if (currentUser == null) {
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        BidsInfo info = bidMapper.findBid(currentUser.getId());
        JSONObject obj = new JSONObject();
        obj.put("result", info);
        return new ResponseEntity<JSONObject>(obj, HttpStatus.OK);
    }

    @RequestMapping("/permissions")
    public ResponseEntity<?> allList(HttpServletRequest r1) throws IOException {
        User currentUser = userService.getUserByHeader(r1);
        if (currentUser == null) {
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        Supplier supplier = bidMapper.findSupplier(currentUser.getId());
        Buyer buyer = bidMapper.findBuyer(currentUser.getId());
        Distributor distributor = bidMapper.findDistributor(currentUser.getId());
        JSONObject obj = new JSONObject();
        obj.put("buyer", buyer);
        obj.put("supplier", supplier);
        obj.put("distributor", distributor);
        return new ResponseEntity<JSONObject>(obj, HttpStatus.OK);
    }

    @RequestMapping("/buyerList")
    public ResponseEntity<?> buyerList(@RequestParam(required=false, defaultValue = "") String status, @RequestParam(required=false, defaultValue = "") String filter, @RequestParam(required=false, defaultValue = "") int page, @RequestParam(required=false, defaultValue = "") int size, HttpServletRequest r1) throws IOException {
        if(status.equals("全部")){
            status = "";
        }
        List<HashMap> buyers = bidMapper.findBuyerList ( (page-1)*size, size, filter, status);
        return new ResponseEntity<List<HashMap>>(buyers ,HttpStatus.OK);
    }

    @RequestMapping("/buyerCount")
    public ResponseEntity<?> buyerCount(@RequestParam(required=false, defaultValue = "") String status, @RequestParam(required=false, defaultValue = "") String filter, HttpServletRequest r1) throws IOException {
        if(status.equals("全部")){
            status = "";
        }
        int count = bidMapper.findBuyerCount( filter, status);
        JSONObject obj = new JSONObject();
        obj.put("count", count);
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }

    @RequestMapping("/supplierList")
    public ResponseEntity<?> supplierList(@RequestParam(required=false, defaultValue = "") String status, @RequestParam(required=false, defaultValue = "") String filter, @RequestParam(required=false, defaultValue = "") int page, @RequestParam(required=false, defaultValue = "") int size, HttpServletRequest r1) throws IOException {
        if(status.equals("全部")){
            status = "";
        }
        List<HashMap> Suppliers = bidMapper.findSupplierList( (page-1)*size, size, filter, status);
        return new ResponseEntity<>(Suppliers ,HttpStatus.OK);
    }

    @RequestMapping("/supplierCount")
    public ResponseEntity<?> supplierCount(@RequestParam(required=false, defaultValue = "") String status, @RequestParam(required=false, defaultValue = "") String filter, HttpServletRequest r1) throws IOException {
        if(status.equals("全部")){
            status = "";
        }
        int count = bidMapper.findSupplierCount( filter, status);
        JSONObject obj = new JSONObject();
        obj.put("count", count);
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }

    @RequestMapping("/distributorList")
    public ResponseEntity<?> distributorList(@RequestParam(required=false, defaultValue = "") String status, @RequestParam(required=false, defaultValue = "") String filter, @RequestParam(required=false, defaultValue = "") int page, @RequestParam(required=false, defaultValue = "") int size, HttpServletRequest r1) throws IOException {
        if(status.equals("全部")){
            status = "";
        }
        List<HashMap> distributors = bidMapper.findDistributorList ( (page-1)*size, size, filter, status);
        return new ResponseEntity<>(distributors ,HttpStatus.OK);
    }

    @RequestMapping("/distributorCount")
    public ResponseEntity<?> distributorCount(@RequestParam(required=false, defaultValue = "") String status, @RequestParam(required=false, defaultValue = "") String filter, HttpServletRequest r1) throws IOException {
        if(status.equals("全部")){
            status = "";
        }
        int count = bidMapper.findDistributorCount( filter, status);
        JSONObject obj = new JSONObject();
        obj.put("count", count);
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }

    @PostMapping(value = "/upsertPurchase")
    public ResponseEntity<?> upsertPurchase(@RequestBody JSONObject request, HttpServletRequest r1) throws Exception {
        User currentUser = userService.getUserByHeader(r1);
        if (currentUser == null) {
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        Purchase purchase;
        if(request.get("id") == null){
            purchase = new Purchase();
            purchase.setUserId(currentUser.getId());
        }else{
            purchase = bidMapper.findPurchaseById(Long.parseLong(request.get("id").toString()));
        }
        if (request.get("name") != null) {
            purchase.setName(request.get("name").toString());
        }
        if (request.get("detail") != null) {
            purchase.setDetail(request.get("detail").toString());
        }
        if (request.get("industry") != null) {
            purchase.setIndustry(request.get("industry").toString());
        }
        if (request.get("location") != null) {
            purchase.setLocation(request.get("location").toString());
        }
        if (request.get("status") != null) {
            purchase.setStatus(request.get("status").toString());
        }
        if (request.get("type") != null) {
            purchase.setType(request.get("type").toString());
        }
        if (request.get("price") != null) {
            purchase.setPrice(Double.parseDouble(request.get("price").toString()));
        }
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        if (request.get("start") != null) {
            purchase.setStart(format1.parse(request.get("start").toString()));
        }
        if (request.get("end") != null) {
            purchase.setEnd(format1.parse(request.get("end").toString()));
        }
        if(request.get("id") == null){
            bidMapper.insertPurchase(purchase);
        }else{
            bidMapper.updatePurchase(purchase);
        }
        JSONObject obj = new JSONObject();
        obj.put("result", "OK");
        return new ResponseEntity<JSONObject>(obj, HttpStatus.OK);
    }

    @RequestMapping("/purchaseList")
    public ResponseEntity<?> purchaseList(@RequestParam(required=false, defaultValue = "") String status, @RequestParam(required=false, defaultValue = "") String filter, @RequestParam(required=false, defaultValue = "") String type, @RequestParam(required=false, defaultValue = "") long userId,  @RequestParam(required=false, defaultValue = "") int page, @RequestParam(required=false, defaultValue = "") int size, HttpServletRequest r1) throws IOException {
        if(status.equals("全部")){
            status = "";
        }
        List<Purchase> purchases = bidMapper.findPurchaseList((page-1)*size, size, filter, status, type, userId);
        return new ResponseEntity<List<Purchase>>(purchases ,HttpStatus.OK);
    }

    @RequestMapping("/purchaseCount")
    public ResponseEntity<?> purchaseList(@RequestParam(required=false, defaultValue = "") String status, @RequestParam(required=false, defaultValue = "") String filter, @RequestParam(required=false, defaultValue = "") String type, @RequestParam(required=false, defaultValue = "") long userId, HttpServletRequest r1) throws IOException {
        if(status.equals("全部")){
            status = "";
        }
        int count = bidMapper.findPurchaseCount(filter, status, type, userId);
        JSONObject obj = new JSONObject();
        obj.put("count", count);
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }

    @RequestMapping("/purchaseBuyerList")
    public ResponseEntity<?> purchaseBuyerList(@RequestParam(required=false, defaultValue = "") String filter, @RequestParam(required=false, defaultValue = "") int page, @RequestParam(required=false, defaultValue = "") int size, HttpServletRequest r1) throws IOException {
        List<HashMap> purchases = bidMapper.findPurchaseBuyerList ((page-1)*size, size, filter);
        return new ResponseEntity<List<HashMap>>(purchases ,HttpStatus.OK);
    }

    @RequestMapping("/purchaseBuyerCount")
    public ResponseEntity<?> purchaseBuyerList( @RequestParam(required=false, defaultValue = "") String filter, HttpServletRequest r1) throws IOException {
        int count = bidMapper.findPurchaseBuyerCount(filter);
        JSONObject obj = new JSONObject();
        obj.put("count", count);
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }

    @PostMapping(value = "/upsertPurchaseApply")
    public ResponseEntity<?> upsertPurchaseApply(@RequestBody JSONObject request, HttpServletRequest r1) throws Exception {
        User currentUser = userService.getUserByHeader(r1);
        if (currentUser == null) {
            return new ResponseEntity<HttpError>(new HttpError(401, "当前用户登陆状态已过期，请重新登陆"), HttpStatus.UNAUTHORIZED);
        }
        PurchaseApply purchaseApply = new PurchaseApply();
        purchaseApply.setBidInfoId(Long.parseLong(request.get("bidInfoId").toString()));
        purchaseApply.setPurchaseId(Long.parseLong(request.get("purchaseId").toString()));
        purchaseApply.setType(request.get("type").toString());
        bidMapper.insertPurchaseApply(purchaseApply);
        JSONObject obj = new JSONObject();
        obj.put("result", "OK");
        return new ResponseEntity<JSONObject>(obj, HttpStatus.OK);
    }

    @RequestMapping("/findPurchaseApply")
    public ResponseEntity<?> purchaseApply(@RequestParam(required=false, defaultValue = "") long purchaseId, @RequestParam(required=false, defaultValue = "") long bidInfoId,  HttpServletRequest r1) throws IOException {
        PurchaseApply result = bidMapper.findPurchaseApply(purchaseId, bidInfoId);
        JSONObject obj = new JSONObject();
        obj.put("result", result);
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }

    @RequestMapping("/offerList")
    public ResponseEntity<?> offerList(@RequestParam(required=false, defaultValue = "") String filter, @RequestParam(required=false, defaultValue = "") int page, @RequestParam(required=false, defaultValue = "") int size, HttpServletRequest r1) throws IOException {
        List<HashMap> offers = bidMapper.findOfferList(filter,(page-1)*size, size);
        return new ResponseEntity<List<HashMap>>(offers ,HttpStatus.OK);
    }

    @RequestMapping("/offerCount")
    public ResponseEntity<?> offerCount(@RequestParam(required=false, defaultValue = "") String filter, HttpServletRequest r1) throws IOException {
        int count = bidMapper.findOfferCount(filter);
        JSONObject obj = new JSONObject();
        obj.put("count", count);
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }

    @RequestMapping("/applyList")
    public ResponseEntity<?> applierList(@RequestParam(required=false, defaultValue = "") long purchaseId, @RequestParam(required=false, defaultValue = "") String type, @RequestParam(required=false, defaultValue = "") String filter, @RequestParam(required=false, defaultValue = "") int page, @RequestParam(required=false, defaultValue = "") int size, HttpServletRequest r1) throws IOException {
        List<HashMap> appliers = bidMapper.findApplierList(purchaseId, type, filter,(page-1)*size, size);
        return new ResponseEntity<List<HashMap>>(appliers ,HttpStatus.OK);
    }

    @RequestMapping("/applyCount")
    public ResponseEntity<?> applierCount(@RequestParam(required=false, defaultValue = "") long purchaseId, @RequestParam(required=false, defaultValue = "") String type, @RequestParam(required=false, defaultValue = "") String filter, HttpServletRequest r1) throws IOException {
        int count = bidMapper.findApplierCount(purchaseId, type, filter);
        JSONObject obj = new JSONObject();
        obj.put("count", count);
        return new ResponseEntity<JSONObject>(obj ,HttpStatus.OK);
    }
}
