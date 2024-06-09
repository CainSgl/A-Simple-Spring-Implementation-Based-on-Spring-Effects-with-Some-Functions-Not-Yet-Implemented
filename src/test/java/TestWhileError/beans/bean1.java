package TestWhileError.beans;

import Autowired.Cainsgl.annotations.Autowired;
import Autowired.Cainsgl.annotations.Component;

@Component
public class bean1
{
    @Autowired
    bean2 bean2;
}
