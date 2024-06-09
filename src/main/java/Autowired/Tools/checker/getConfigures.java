package Autowired.Tools.checker;

public interface getConfigures
{

    int getPort();

    /**
     * <h3>配置文件存储路径，如果不需要请返回null</h3>
     *
     * @return 这里必须返回的是路径的绝对值
     * <a href="https://qm.qq.com/q/yIa89unkAM">有疑问点我加作者QQ</a>
     * @author Cainsgl
     **/
    String getFilePath();

    /**
     * <h3>SQL的账号</h3>
     *
     * @return 必须返回正确的账号
     * <a href="https://qm.qq.com/q/yIa89unkAM">有疑问点我加作者QQ</a>
     * @author Cainsgl
     **/
    String getSQLaccount();

    /**
     * <h3>SQL的密码</h3>
     *
     * @return 必须返回正确的密码
     * <a href="https://qm.qq.com/q/yIa89unkAM">有疑问点我加作者QQ</a>
     * @author Cainsgl
     **/
    String getSQLpassword();

    /**
     * <h3>SQL的访问路径</h3>
     *
     * @return 返回sql的访问路径
     * <a href="https://qm.qq.com/q/yIa89unkAM">有疑问点我加作者QQ</a>
     * @author Cainsgl
     **/
    String getDateBase();

    /**
     * <p>为了偷懒做的</p>
     */
    String getMybatisXML();

    default int getMaxFileSize()
    {
        return 2*1024 * 1024;
    }

    default int getBufferSize()
    {
        return 8192;
    }
}
