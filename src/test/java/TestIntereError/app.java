package TestIntereError;

import Autowired.Cainsgl.annotations.Autowired;
import Autowired.Tools.AppInitializer;

public class app
{
    //先看测试
    @Autowired
    testInterBeam testInterBeam;//null
    public static void main(String[] args)
    {
        AppInitializer.launch(testInterBeam.class);
    }
}
