package Test;
import Autowired.Cainsgl.ScopeMode;
import Autowired.Cainsgl.annotations.Autowired;
import Autowired.Cainsgl.annotations.Component;
import Autowired.Cainsgl.annotations.Lazy;
import Autowired.Cainsgl.annotations.proxy.EnableProxy;
import Autowired.Tools.AppInitializer;
import Test.beans.*;

@Component(Scope = ScopeMode.Singleton)
public class TestAutoWired
{
    @Lazy
    @Autowired
    TestAutoWired testAutoWired;

    @Autowired
    bean1 b1;
    @Autowired
    bean2 b2;
    @Autowired
    bean3 b3;
    @Autowired
    bean4 b4;
    @Autowired
    controller controller;
    //先来看测试，待会儿再看源码
    //不用单元测试的原因是因为每一个启动类都不同
    public static void main(String[] args) throws Exception
    {
        AppInitializer.launch(TestAutoWired.class);
        bean3 bean3 = AppInitializer.getBean(bean3.class);
        TestAutoWired testAutoWired=AppInitializer.getBean(TestAutoWired.class);
        System.out.println(testAutoWired==testAutoWired.testAutoWired);
        bean3.Say();
    }
}
