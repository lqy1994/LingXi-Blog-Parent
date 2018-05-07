package cn.edu.sdu.wh.lqy.lingxi.blog.mapper;

import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.Option;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.OptionVoExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface OptionMapper {
    long countByExample(OptionVoExample example);

    int deleteByExample(OptionVoExample example);

    int deleteByPrimaryKey(String name);

    int insert(Option record);

    int insertSelective(Option record);

    List<Option> selectByExample(OptionVoExample example);

    Option selectByPrimaryKey(String name);

    int updateByExampleSelective(@Param("record") Option record, @Param("example") OptionVoExample example);

    int updateByExample(@Param("record") Option record, @Param("example") OptionVoExample example);

    int updateByPrimaryKeySelective(Option record);

    int updateByPrimaryKey(Option record);

    /**
     * 批量保存
     * @param options list
     * @return 保存的个数
     */
    int insertOptions(List<Option> options);
}