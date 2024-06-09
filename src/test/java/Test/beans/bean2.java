package Test.beans;

import Autowired.Cainsgl.annotations.Aop.After;
import Autowired.Cainsgl.annotations.Aop.AfterReturn;
import Autowired.Cainsgl.annotations.Aop.Before;
import Autowired.Cainsgl.annotations.Autowired;
import Autowired.Cainsgl.annotations.Component;
import Autowired.Cainsgl.annotations.proxy.EnableProxy;
@Component

public class bean2
{
    @Autowired
    bean3 bean3;

    @Before("Test.beans.controller.[a-zA-Z]+")
    void before()
    {
        System.out.println("before");
    }
    @After("Test.beans.controller.test")
    void After()
    {
        System.out.println("after");
    }
    @AfterReturn("Test.beans.controller.+")
    void AfterRetunr()
    {
        System.out.println("AfterRetunr");
    }
}
