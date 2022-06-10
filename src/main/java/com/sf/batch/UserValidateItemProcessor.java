package com.sf.batch;

import com.sf.bean.UserBean;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.batch.item.validator.ValidationException;

/**
 * 校验器
 */
public class UserValidateItemProcessor extends ValidatingItemProcessor<UserBean> {

    @Override
    public UserBean process(UserBean item) throws ValidationException {
        //需要执行super.process(item)才会调用自定义校验器
        super.process(item);
        //对数据进行简单的处理
        Integer id= item.getId();
        if (id % 2 == 0) {
            item.setPhone("偶数ID");
        } else {
            item.setPhone("奇数ID");
        }
        return item;
    }
}