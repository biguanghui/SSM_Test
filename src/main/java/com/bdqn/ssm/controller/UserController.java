package com.bdqn.ssm.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bdqn.ssm.entity.Role;
import com.bdqn.ssm.entity.User;
import com.bdqn.ssm.service.RoleService;
import com.bdqn.ssm.service.UserService;
import com.bdqn.ssm.util.Constants;
import com.bdqn.ssm.util.PageSupport;
import com.mysql.jdbc.StringUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.rmi.runtime.Log;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {
    private Logger logger= Logger.getLogger(UserController.class);
    @Resource
    private UserService us;
    @Resource
    private RoleService rs;

    public UserService getUs() {
        return us;
    }

    public void setUs(UserService us) {
        this.us = us;
    }

    public RoleService getRs() {
        return rs;
    }

    public void setRs(RoleService rs) {
        this.rs = rs;
    }

    //转入登录页面

    @RequestMapping(value = "/login.html",method = RequestMethod.GET)
    public String login(){
        logger.debug("进入登录页面");
        return "login";
    }

    /**
     * 通过用户名与密码实现登录
     * @param userCode
     * @param userPassword
     * @param request
     * @param session
     * @return
     */
    @RequestMapping(value="/dologin.html",method = RequestMethod.POST)
    public String doLogin(@RequestParam String userCode, @RequestParam String userPassword
            , HttpServletRequest request, HttpSession session) {
        User user=us.getLoginUser(userCode,userPassword);
        if(user==null){
            request.setAttribute("error","用户名或密码错误");
            return "login";
        }else{
            session.setAttribute(Constants.USER_SESSION,user);
            return "redirect:/user/main.html";
        }

    }

    /**
     * 进入系统主页面
     * @param session
     * @return
     */
    @RequestMapping("/main.html")
    public String main(HttpSession session){
        if(session.getAttribute(Constants.USER_SESSION)==null){
            return "redirect:/user/login.html";
        }else{
            return "frame";
        }

    }
    //退出登录
    @RequestMapping("/logout")
    public String logout(HttpSession session){
        session.removeAttribute(Constants.USER_SESSION);
        return "redirect:/user/login.html";
    }

    /**
     * 分页查询所有用户信息
     * @param model
     * @param queryUserName
     * @param queryUserRole
     * @param pageIndex
     * @return
     */
    @RequestMapping(value="/sys/userlist.html")
    public String getUserList(Model model,
                              @RequestParam(value="queryname",required=false) String queryUserName,
                              @RequestParam(value="queryUserRole",required=false) String queryUserRole,
                              @RequestParam(value="pageIndex",required=false) String pageIndex){
        logger.info("getUserList ---- > 用户名queryUserName: " + queryUserName);
        logger.info("getUserList ---- > 角色IDqueryUserRole: " + queryUserRole);
        logger.info("getUserList ---- > 当前页面pageIndex: " + pageIndex);
        Integer _queryUserRole = null;
        List<User> userList = null;
        List<Role> roleList = null;
        //设置页面容量
        int pageSize = Constants.pageSize;
        //当前页码
        int currentPageNo = 1;

        if(queryUserName == null){
            queryUserName = "";
        }
        if(queryUserRole != null && !queryUserRole.equals("")){
            _queryUserRole = Integer.parseInt(queryUserRole);
        }

        if(pageIndex != null){
            try{
                currentPageNo = Integer.valueOf(pageIndex);
            }catch(NumberFormatException e){
                return "redirect:/syserror.html";
            }
        }
        //总数量（表）
        int totalCount = 0;
        try {
            totalCount = us.getUserCount(queryUserName,_queryUserRole);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //总页数
        PageSupport pages=new PageSupport();
        pages.setCurrentPageNo(currentPageNo);
        System.out.println("输出："+currentPageNo);
        pages.setPageSize(pageSize);
        pages.setTotalCount(totalCount);
        int totalPageCount = pages.getTotalPageCount();
        //控制首页和尾页
        if(currentPageNo < 1){
            currentPageNo = 1;
        }else if(currentPageNo > totalPageCount){
            currentPageNo = totalPageCount;
        }
        try {
            userList = us.getUserList(queryUserName,_queryUserRole,currentPageNo,pageSize);

            roleList = rs.getRoleList();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        model.addAttribute("userList", userList);
        model.addAttribute("roleList", roleList);
        model.addAttribute("queryUserName", queryUserName);
        model.addAttribute("queryUserRole", queryUserRole);
        model.addAttribute("totalPageCount", totalPageCount);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("currentPageNo", currentPageNo);
        return "userlist";
    }

    //转到添加用户页面
   /* @RequestMapping(value="useradd.html",method = RequestMethod.GET)
    public String add(@ModelAttribute("user")User user){
        logger.debug("进入添加页面");
        return "useradd";
    }*/
   /* @RequestMapping(value="useradd.html",method = RequestMethod.GET)
    public String add(Model model,User user){
        model.addAttribute("user",user);
        return "useradd";
    }*/
    @RequestMapping(value="useradd.html",method = RequestMethod.GET)
    public String add(){

        return "useradd";
    }

    //多文件上传
    @RequestMapping(value="/useraddsave.html",method=RequestMethod.POST)
    public String addUserSave(User user,HttpSession session,HttpServletRequest request,
                              @RequestParam(value ="attachs", required = false) MultipartFile[] attachs){
        String idPicPath = null;
        String workPicPath = null;
        String errorInfo = null;
        boolean flag = true;
        String path = request.getSession().getServletContext().getRealPath("statics"+File.separator+"uploadfiles");
        logger.info("uploadFile path ============== > "+path);
        for(int i = 0;i < attachs.length ;i++){
            MultipartFile attach = attachs[i];
            if(!attach.isEmpty()){
                if(i == 0){
                    errorInfo = "uploadFileError";
                }else if(i == 1){
                    errorInfo = "uploadWpError";
                }
                String oldFileName = attach.getOriginalFilename();//原文件名
                logger.info("uploadFile oldFileName ============== > "+oldFileName);
                String prefix= FilenameUtils.getExtension(oldFileName);//原文件后缀
                logger.debug("uploadFile prefix============> " + prefix);
                int filesize = 500000;
                logger.debug("uploadFile size============> " + attach.getSize());
                if(attach.getSize() >  filesize){//上传大小不得超过 500k
                    request.setAttribute("e", " * 上传大小不得超过 500k");
                    flag = false;
                }else if(prefix.equalsIgnoreCase("jpg") || prefix.equalsIgnoreCase("png")
                        || prefix.equalsIgnoreCase("jpeg") || prefix.equalsIgnoreCase("pneg")){//上传图片格式不正确
                    String fileName = System.currentTimeMillis()+ RandomUtils.nextInt(1000000)+"_Personal.jpg";
                    logger.debug("new fileName======== " + attach.getName());
                    File targetFile = new File(path, fileName);
                    if(!targetFile.exists()){
                        targetFile.mkdirs();
                    }
                    //保存
                    try {
                        attach.transferTo(targetFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                        request.setAttribute(errorInfo, " * 上传失败！");
                        flag = false;
                    }
                    if(i == 0){
                        idPicPath = path+ File.separator+fileName;
                    }else if(i == 1){
                        workPicPath = path+File.separator+fileName;
                    }
                    logger.debug("idPicPath: " + idPicPath);
                    logger.debug("workPicPath: " + workPicPath);

                }else{
                    request.setAttribute(errorInfo, " * 上传图片格式不正确");
                    flag = false;
                }
            }
        }
        if(flag){

            user.setCreatedBy(((User)session.getAttribute(Constants.USER_SESSION)).getId());
            user.setCreationDate(new Date());
            user.setIdPicPath(idPicPath);
            user.setWorkPicPath(workPicPath);
            try {
                if(us.add(user)>0){
                    return "redirect:/user/sys/userlist.html";
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return "useradd";
    }

    @RequestMapping(value="/ucexist.html")
    //以下注解表示将方法的返回结果直接放入response响应数据区中
    @ResponseBody
    public Object userCodeIsExit(@RequestParam String userCode){
        logger.debug("userCodeIsExit userCode===================== "+userCode);
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if(StringUtils.isNullOrEmpty(userCode)){
            resultMap.put("userCode", "exist");
        }else{
            User user = us.getUserExist(userCode);
            if(null != user)
                resultMap.put("userCode", "exist");
            else
                resultMap.put("userCode", "noexist");
        }
        return JSONArray.toJSONString(resultMap);
    }

    /**
     * 使用json返回数据格式查询用户信息详情，produces = {"application/json;charset=UTF-8"}
     * 该配置处理返回json对象，页面中文乱码问题
     */
    @RequestMapping(value="/view",method=RequestMethod.GET,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public Object view(@RequestParam String id){
        logger.debug("view id===================== "+id);
        String cjson = "";
        if(null == id || "".equals(id)){
            return "nodata";
        }else{
            try {
                User user = us.getUserById(id);
                cjson = JSON.toJSONString(user);
                logger.debug("cjson ================== > " + cjson);
            } catch (Exception e) {

                e.printStackTrace();
                return "failed";
            }
            return cjson;
        }
    }

    //转到修改页面
    @RequestMapping(value="/usermodify.html",method=RequestMethod.GET)
    public String getUserById(@RequestParam String userid,Model model){
        logger.debug("getUserById uid===================== "+userid);
        User user = us.getUserById(userid);
        model.addAttribute(user);
        return "usermodify";
    }

}
