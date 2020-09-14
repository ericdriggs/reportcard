package com.ericdriggs.reportcard.mapper.xml.junit;


import com.ericdriggs.reportcard.model.TestStatus;
import org.junit.jupiter.api.Test;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import static org.junit.jupiter.api.Assertions.*;


public class JunitMapperTests {

    @Test
    public void testCaseTest() {



        Converter<com.ericdriggs.reportcard.xml.junit.Testcase, com.ericdriggs.reportcard.model.TestCase> fromJunitToModelTestCase = new AbstractConverter<>() {
            protected com.ericdriggs.reportcard.model.TestCase convert(com.ericdriggs.reportcard.xml.junit.Testcase source) {
                com.ericdriggs.reportcard.model.TestCase modelTestCase = new com.ericdriggs.reportcard.model.TestCase();
                modelTestCase.setName(source.getName());
                modelTestCase.setClassName(source.getClassname());
                modelTestCase.setTime(source.getTime());

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

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(fromJunitToModelTestCase);

        com.ericdriggs.reportcard.xml.junit.Testcase junitTestCase = JunitFactoryUtil.testcase(TestStatus.FAILURE);
        assertNotNull(junitTestCase.getFailure());
        assertNull(junitTestCase.getError());
        assertNull(junitTestCase.getSkipped());

        com.ericdriggs.reportcard.model.TestCase modelTestCase = modelMapper.map(junitTestCase, com.ericdriggs.reportcard.model.TestCase.class );
        assertEquals(junitTestCase.getClassname(), modelTestCase.getClassName());
        assertEquals(junitTestCase.getName(), modelTestCase.getName());
        assertEquals(junitTestCase.getTime(), modelTestCase.getTime());
        assertEquals(TestStatus.FAILURE, modelTestCase.getTestStatus());

    }


}
