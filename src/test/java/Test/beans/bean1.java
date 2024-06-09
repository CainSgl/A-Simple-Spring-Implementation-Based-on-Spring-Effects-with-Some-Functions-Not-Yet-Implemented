package Test.beans;

import Autowired.Cainsgl.annotations.Autowired;
import Autowired.Cainsgl.annotations.Component;
import Autowired.Cainsgl.annotations.proxy.EnableProxy;
@Component

public class bean1
{
    @Autowired
    bean2 bean2;
    public void Say()
    {
        System.out.println("bean1");
    }
}
