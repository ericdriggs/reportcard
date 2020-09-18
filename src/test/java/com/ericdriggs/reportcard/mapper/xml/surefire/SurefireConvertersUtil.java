package com.ericdriggs.reportcard.mapper.xml.surefire;

import com.ericdriggs.reportcard.model.TestCase;
import com.ericdriggs.reportcard.model.TestStatus;
import com.ericdriggs.reportcard.xml.surefire.Testcase;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;

public class SurefireConvertersUtil {

    public static Converter<Testcase, TestCase> fromSurefireToModelTestCase = new AbstractConverter<>() {
        protected com.ericdriggs.reportcard.model.TestCase convert(com.ericdriggs.reportcard.xml.surefire.Testcase source) {
            com.ericdriggs.reportcard.model.TestCase modelTestCase = new com.ericdriggs.reportcard.model.TestCase();
            modelTestCase.setName(source.getName());
            modelTestCase.setClassName(source.getClassname());
            modelTestCase.setTime(new BigDecimal(source.getTime()));

            if (source.getSkipped() != null) {
                modelTestCase.setTestStatus(TestStatus.SKIPPED);
            }
            else if (source.getFailure() != null) {
                modelTestCase.setTestStatus(TestStatus.FAILURE);
            }
            else if (source.getError() != null) {
                modelTestCase.setTestStatus(TestStatus.ERROR);
            }
            else {
                modelTestCase.setTestStatus(TestStatus.SUCCESS);
            }
            return modelTestCase;
        }
    };

    public static ModelMapper modelMapper = new ModelMapper();
    static {
        modelMapper.addConverter(fromSurefireToModelTestCase);
    }
}
