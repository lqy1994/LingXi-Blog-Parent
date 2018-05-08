package cn.edu.sdu.wh.lqy.lingxi.blog.mapper;

import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.Meta;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.MetaExample;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.dto.MetaDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface MetaMapper {

    long countByExample(MetaExample example);

    int deleteByExample(MetaExample example);

    int deleteByPrimaryKey(Integer mid);

    int insert(Meta record);

    int insertSelective(Meta record);

    List<Meta> selectByExample(MetaExample example);

    Meta selectByPrimaryKey(Integer mid);

    int updateByExampleSelective(@Param("record") Meta record, @Param("example") MetaExample example);

    int updateByExample(@Param("record") Meta record, @Param("example") MetaExample example);

    int updateByPrimaryKeySelective(Meta record);

    int updateByPrimaryKey(Meta record);

    List<MetaDTO> selectFromSql(Map<String, Object> paraMap);

    MetaDTO selectDtoByNameAndType(@Param("name") String name, @Param("type") String type);

    Integer countWithSql(Integer mid);
}