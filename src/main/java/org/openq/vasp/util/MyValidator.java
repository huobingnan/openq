package org.openq.vasp.util;

import org.hibernate.validator.HibernateValidator;
import org.openq.vasp.bean.Channel;
import org.openq.vasp.bean.Frame;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class MyValidator
{

    private static final Validator validator =
             Validation.byProvider(HibernateValidator.class)
                .configure()
                .failFast(true) //有一个失败就停止检查
                .buildValidatorFactory()
                .getValidator();

    public static List<String> validate(Channel channel)
    {
        Set<ConstraintViolation<Channel>> validateResult = validator.validate(channel);
        List<String> errorMessage = new ArrayList<>();
        for (ConstraintViolation<Channel> constraintViolation : validateResult)
        {
            errorMessage.add(constraintViolation.getMessage());
        }
        return errorMessage;
    }

    public static List<String> validate(Frame frame)
    {
        Set<ConstraintViolation<Frame>> validateResult = validator.validate(frame);
        List<String> errorMessage = new ArrayList<>();
        for (ConstraintViolation<Frame> constraintViolation : validateResult)
        {
            errorMessage.add(constraintViolation.getMessage());
        }
        return errorMessage;
    }
}
