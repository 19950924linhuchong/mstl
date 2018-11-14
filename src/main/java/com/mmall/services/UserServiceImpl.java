package com.mmall.services;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.model.User;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements  IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int result =userMapper.checkUsername(username);
        if(result ==0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        //tudo 密码登录 MD5;
        String md5password = MD5Util.MD5EncodeUtf8(password);
        User user =userMapper.selectLogin(username,md5password);
        if(user == null){
            return ServerResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return  ServerResponse.createBySuccess("登录成功",user);
    }

    @Override
    public ServerResponse<User> register(User user) {
        ServerResponse vali =this.checkValid(user.getUsername(),Const.USERNAME);
        if(!vali.isSuccess()){
                return vali;
        }
        ServerResponse valiemail =this.checkValid(user.getEmail(),Const.EMAIL);
        if(!valiemail.isSuccess()){
            return valiemail;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int  result=userMapper.insert(user);
        if(result ==0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if(org.apache.commons.lang3.StringUtils.isNotBlank(type)){
            //开始
            if(Const.USERNAME.equals(type)){
                int result =userMapper.checkUsername(str);
                if(result>0){
                    return  ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if(Const.EMAIL.equals(type)) {
                int result = userMapper.checkEmail(str);
                if (result > 0) {
                    return ServerResponse.createByErrorMessage("邮箱已存在");
                }
            }
            }else {
                return  ServerResponse.createByErrorMessage("参数错误");
            }
            return  ServerResponse.createBySuccessMessage("校验成功");

    }

    @Override
    public ServerResponse<String> selectQuestion(String username) {
        ServerResponse valid=this.checkValid(username,Const.USERNAME);
        if(valid.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String question =userMapper.selectQuestionByUsername(username);
        if(StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccessMessage(question);
        }
        return ServerResponse.createByErrorMessage("找回密码的问题是空的");
    }

    public ServerResponse<String> checkAnswer(String username,String question,String answer){
        int result =userMapper.checkAnswer( username, question,answer);
        if(result>0){
            //说明正确
            String forgetToken= UUID.randomUUID().toString();
            TokenCache.setKey("token_"+username,forgetToken);
            return ServerResponse.createBySuccessMessage(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题的答案错误");
    }

    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
        if(org.apache.commons.lang3.StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误,token需要传递");
        }
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()){
            //用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if(org.apache.commons.lang3.StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或者过期");
        }

        if(org.apache.commons.lang3.StringUtils.equals(forgetToken,token)){
            String md5Password  = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username,md5Password);

            if(rowCount > 0){
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        }else{
            return ServerResponse.createByErrorMessage("token错误,请重新获取重置密码的token");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }


    public ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User user){
        //防止横向越权,要校验一下这个用户的旧密码,一定要指定是这个用户.因为我们会查询一个count(1),如果不指定id,那么结果就是true啦count>0;
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("旧密码错误");
        }

        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount > 0){
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }


    public ServerResponse<User> updateInformation(User user){
        //username是不能被更新的
        //email也要进行一个校验,校验新的email是不是已经存在,并且存在的email如果相同的话,不能是我们当前的这个用户的.
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount > 0){
            return ServerResponse.createByErrorMessage("email已存在,请更换email再尝试更新");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount > 0){
            return ServerResponse.createBySuccess("更新个人信息成功",updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }



    public ServerResponse<User> getInformation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);

    }




    //backend

    /**
     * 校验是否是管理员
     * @param user
     * @return
     */
    public ServerResponse checkAdminRole(User user){
        if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }



}
