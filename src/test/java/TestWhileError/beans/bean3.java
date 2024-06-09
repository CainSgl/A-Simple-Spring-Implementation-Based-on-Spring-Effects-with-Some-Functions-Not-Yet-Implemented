package TestWhileError.beans;

import Autowired.Cainsgl.annotations.Autowired;
import Autowired.Cainsgl.annotations.Component;
import Autowired.Cainsgl.annotations.Lazy;

@Component
public class bean3
{
    @Autowired
    bean1 bean1;
}
