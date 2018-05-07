package cn.edu.sdu.wh.lqy.lingxi.blog.service.impl;

import cn.edu.sdu.wh.lqy.lingxi.blog.mapper.OptionMapper;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.Option;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.OptionVoExample;
import cn.edu.sdu.wh.lqy.lingxi.blog.service.IMetaService;
import cn.edu.sdu.wh.lqy.lingxi.blog.service.IOptionService;
import com.alibaba.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * options表的service
 */
//@Service(interfaceClass = IOptionService.class)
public class OptionServiceImpl implements IOptionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OptionServiceImpl.class);

    @Autowired
    private OptionMapper optionMapper;

    @Override
    public void insertOption(Option option) {
        LOGGER.debug("Enter insertOption method:option={}", option);
        optionMapper.insertSelective(option);
        LOGGER.debug("Exit insertOption method.");
    }

    @Override
    @Transactional
    public void insertOption(String name, String value) {
        LOGGER.debug("Enter insertOption method:name={},value={}", name, value);
        Option option = new Option();
        option.setName(name);
        option.setValue(value);
        if (optionMapper.selectByPrimaryKey(name) == null) {
            optionMapper.insertSelective(option);
        } else {
            optionMapper.updateByPrimaryKeySelective(option);
        }
        LOGGER.debug("Exit insertOption method.");
    }

    @Override
    @Transactional
    public void saveOptions(Map<String, String> options) {
        if (null != options && !options.isEmpty()) {
            options.forEach(this::insertOption);
        }
    }

    @Override
    public Option getOptionByName(String name) {
        return optionMapper.selectByPrimaryKey(name);
    }

    @Override
    public List<Option> getOptions() {
        return optionMapper.selectByExample(new OptionVoExample());
    }
}
