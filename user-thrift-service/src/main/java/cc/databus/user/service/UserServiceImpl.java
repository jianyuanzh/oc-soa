package cc.databus.user.service;

import cc.databus.thrift.user.UserInfo;
import cc.databus.thrift.user.UserService;
import cc.databus.thrift.user.dto.UserDTO;
import cc.databus.user.mapper.UserMapper;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService.Iface {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserInfo getUserById(int id) throws TException {
        return userMapper.getUserById(id);
    }

    @Override
    public UserInfo getTeacherById(int id) throws TException {
        return userMapper.getTeacherById(id);
    }

    @Override
    public UserInfo getUserByName(String name) throws TException {
        return userMapper.getUserByName(name);
    }

    @Override
    public void registerUser(UserInfo userinfo) throws TException {
        userMapper.registerUser(userinfo);
    }
}
