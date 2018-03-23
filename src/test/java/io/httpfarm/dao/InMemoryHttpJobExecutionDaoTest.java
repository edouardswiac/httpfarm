package io.httpfarm.dao;

import io.httpfarm.domain.HttpJobExecution;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

public class InMemoryHttpJobExecutionDaoTest {

  InMemoryHttpJobExecutionDao dao;

  @Before
  public void init() {
    dao = new InMemoryHttpJobExecutionDao();
  }

  @Test
  public void testGetAllByJobUuid() {
    HttpJobExecution exec1 = new HttpJobExecution("test.com", "1", 0L, 1L, Collections.emptyMap());
    HttpJobExecution exec2 = new HttpJobExecution("test.com", "2", 0L, 1L, Collections.emptyMap());
    HttpJobExecution exec3 = new HttpJobExecution("test.com", "3", 0L, 1L, Collections.emptyMap());
    dao.save(exec1);
    dao.save(exec2);
    dao.save(exec3);

    List<HttpJobExecution> res = dao.getAll("1");
    assertThat(res, hasItems(exec1));
  }
}
