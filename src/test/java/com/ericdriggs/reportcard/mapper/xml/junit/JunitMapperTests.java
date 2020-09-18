package com.ericdriggs.reportcard.mapper.xml.junit;


import com.ericdriggs.reportcard.model.TestStatus;
import org.junit.jupiter.api.Test;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import static org.junit.jupiter.api.Assertions.*;


public class JunitMapperTests {

    protected static ModelMapper modelMapper = JunitConvertsUtil.modelMapper;

    @Test
    public void testCaseTest() {

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
