package io.httpfarm.repository;

import io.httpfarm.dao.HttpJobDefinitionDao;
import io.httpfarm.dao.InMemoryHttpJobDefinitionDao;
import io.httpfarm.domain.HttpJobDefinition;
import org.junit.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.junit.Assert.*;

public class HttpJobRepositoryTest {
  Clock clock = Clock.offset(Clock.fixed(Instant.EPOCH, ZoneId.of("UTC")), Duration.ofDays(43));

  @Test
  public void testFindExecutionDueNow() {
    HttpJobDefinitionDao dao = new InMemoryHttpJobDefinitionDao();
    HttpJobRepository repo = new HttpJobRepository(dao, clock);
    HttpJobDefinition job1 = new HttpJobDefinition("http://url1.com", "* * * * *");
    HttpJobDefinition job2 = new HttpJobDefinition("http://url2.com", "30 * * * *");
    dao.save(job1);
    dao.save(job2);
    List<HttpJobDefinition> res = repo.findDueForExecutionNow();
    assertEquals(1, res.size());
    assertEquals(job1, res.get(0));


  }
}
