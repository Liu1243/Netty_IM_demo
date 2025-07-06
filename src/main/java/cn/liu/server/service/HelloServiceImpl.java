package cn.liu.server.service;

/**
 * @Title: 1
 * @Author liu
 * @Package cn.liu.server.service
 * @Date 2025/7/6 11:56
 * @description:
 */
public class HelloServiceImpl implements HelloService{
    @Override
    public String sayHello(String name) {
        int i = 1/0;
        return "hello, " + name;
    }
}
