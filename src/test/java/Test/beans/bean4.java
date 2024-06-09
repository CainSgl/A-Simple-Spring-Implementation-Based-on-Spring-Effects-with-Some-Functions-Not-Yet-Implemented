package Test.beans;

import Autowired.Cainsgl.annotations.Component;

@Component
public class bean4
{
    public void Say()
    {
        System.out.println("Hello" + this.hashCode());
    }
}
