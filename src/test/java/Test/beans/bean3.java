package Test.beans;

import Autowired.Cainsgl.annotations.Aop.After;
import Autowired.Cainsgl.annotations.Aop.Before;
import Autowired.Cainsgl.annotations.Autowired;
import Autowired.Cainsgl.annotations.Component;
import Autowired.Cainsgl.annotations.Lazy;
import Autowired.Cainsgl.annotations.proxy.EnableProxy;


@Component
public class bean3
{
    @Lazy
    @Autowired//测试循环注入
    bean1 bean1;

    public void Say()
    {
        System.out.println("Hello" + this.hashCode()+bean1);
    }
}
