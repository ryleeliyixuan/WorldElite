package com.worldelite.job.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.worldelite.job.constants.Bool;
import com.worldelite.job.constants.DictType;
import com.worldelite.job.entity.Dict;
import com.worldelite.job.exception.ServiceException;
import com.worldelite.job.form.DictForm;
import com.worldelite.job.form.DictListForm;
import com.worldelite.job.mapper.DictMapper;
import com.worldelite.job.util.AppUtils;
import com.worldelite.job.vo.ApiCode;
import com.worldelite.job.vo.DictVo;
import com.worldelite.job.vo.PageResult;
import com.worldelite.job.vo.SalaryRange;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author yeguozhong yedaxia.github.com
 */
@Service
public class DictService extends BaseService{

    @Autowired
    private DictMapper dictMapper;

    public DictVo getById(Integer id){
       Dict dict = dictMapper.selectByPrimaryKey(id);
       return dict == null? null: new DictVo().asVo(dict);
    }

    public Dict getDict(Integer id){
        return dictMapper.selectByPrimaryKey(id);
    }

    public List<DictVo> getDictList(DictListForm listForm){
        Dict options = new Dict();
        options.setName(listForm.getName());
        options.setType(listForm.getType());
        List<Dict> dictList = dictMapper.selectAndList(options);
        return AppUtils.asVoList(dictList, DictVo.class);
    }

    public PageResult<DictVo> getDictPage(DictListForm listForm){
        Dict options = new Dict();
        options.setName(listForm.getName());
        options.setType(listForm.getType());
        AppUtils.setPage(listForm);
        Page<Dict> dictPage = (Page<Dict>) dictMapper.selectAndList(options);
        PageResult<DictVo> pageResult = new PageResult<>(dictPage);
        pageResult.setList(AppUtils.asVoList(dictPage, DictVo.class));
        return pageResult;
    }

    public List<DictVo> getDictListByType(DictType type){
        Dict options = new Dict();
        options.setType(type.value);
        PageHelper.startPage(1,100, "value asc");
        List<Dict> dictList = dictMapper.selectAndList(options);
        return AppUtils.asVoList(dictList, DictVo.class);
    }

    /**
     * 保存字段
     *
     * @param dictForm
     * @return
     */
    public DictVo saveDict(DictForm dictForm){
        Dict dict = dictMapper.selectByPrimaryKey(dictForm.getId());
        if(dict == null){
            Dict options = new Dict();
            options.setName(dictForm.getName());
            options.setType(dictForm.getType());
            List<Dict> dictList = dictMapper.selectAndList(options);
            if(CollectionUtils.isNotEmpty(dictList)){
                throw new ServiceException("参数重复", ApiCode.INVALID_PARAM);
            }
            dict = new Dict();
        }

        dict.setValue(dictForm.getValue());
        dict.setType(dictForm.getType());
        dict.setName(dictForm.getName());
        if(dict.getId() == null){
            dictMapper.insertSelective(dict);
        }else{
            dictMapper.updateByPrimaryKeySelective(dict);
        }
        return new DictVo().asVo(dict);
    }

    /**
     * 删除字段 / 假删除
     *
     * @param id
     */
    public void deleteDict(Integer id){
        Dict dict = dictMapper.selectByPrimaryKey(id);
        if(dict != null){
            dict.setDelFlag(Bool.TRUE);
            dictMapper.updateByPrimaryKeySelective(dict);
        }
    }

    public SalaryRange getSalaryRange(Integer id){
        DictVo dictVo = getById(id);
        if(dictVo != null){
            String[] values =  dictVo.getValue().split("-");
            if(values.length == 2){
                SalaryRange salaryRange = new SalaryRange();
                salaryRange.setMin(NumberUtils.toInt(values[0]));
                salaryRange.setMax(NumberUtils.toInt(values[1]));
                return salaryRange;
            }
        }
        return null;
    }

    /**
     * 获取实例
     *
     * @return
     */
    public static DictService getInstance(){
        return AppUtils.getBean(DictService.class);
    }
}
