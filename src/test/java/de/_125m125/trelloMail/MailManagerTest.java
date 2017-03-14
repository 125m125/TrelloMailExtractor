package de._125m125.trelloMail;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;

@RunWith(JUnitParamsRunner.class)
public class MailManagerTest {

    @Test
    @Parameters
    @TestCaseName("testExtractTodosStringDate_{0}")
    public void testExtractTodosStringDate(final String name, final String message, final Date d,
            final List<Todo> expected) throws Exception {
        final MailManager uut = new MailManager(null, null, null, null);

        final List<Todo> actual = uut.extractTodos(message, d);

        assertEquals(expected, actual);
    }

    public Object[] parametersForTestExtractTodosStringDate() {
        Date d = null;
        try {
            d = new SimpleDateFormat("YYYY-MM-DD HH:mm").parse("2017-01-01 06:01");
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        final List<Todo> singleTodo = Arrays.asList(new Todo("title", "content", d));
        final List<Todo> twoTodos = Arrays.asList(new Todo("title1", "content1", d), new Todo("title2", "content2", d));

        // @formatter:off
        return new Object[][]{
            {
                "todo:", "todo:\r\ntitle\r\ncontent", d, singleTodo
            },
            {
                "<todo>","<todo>\r\ntitle\r\ncontent", d, singleTodo
            },
            {
                "emptyLine","<todo>\r\ntitle1\r\ncontent1\r\n\r\ntitle2\r\ncontent2", d, twoTodos
            },
            {
                "endTodo","blabla\r\ntodo\r\ntitle\r\ncontent\r\nendtodo\r\nblabla", d, singleTodo
            },
            {
                "emptyLineTodo","<todo>\r\ntitle1\r\ncontent1\r\n\r\ntodo\r\ntitle2\r\ncontent2", d, twoTodos
            },
        };
        // @formatter:on
    }
}
