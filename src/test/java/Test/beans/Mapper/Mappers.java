package Test.beans.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;



@Mapper
public interface Mappers
{
    @Select("SELECT COUNT(*) FROM biao")
    public int se(int hello);
}
