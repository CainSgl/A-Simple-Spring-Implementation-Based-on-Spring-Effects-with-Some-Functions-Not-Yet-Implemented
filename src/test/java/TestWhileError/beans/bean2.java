package TestWhileError.beans;

import Autowired.Cainsgl.annotations.Autowired;
import Autowired.Cainsgl.annotations.Component;

@Component
public class bean2
{
    @Autowired
    bean3 bean3;
}
