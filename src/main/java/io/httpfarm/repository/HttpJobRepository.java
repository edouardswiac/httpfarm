package io.httpfarm.repository;

import fc.cron.CronExpression;
import io.httpfarm.dao.HttpJobDefinitionDao;
import io.httpfarm.domain.HttpJobDefinition;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class HttpJobRepository {
  private final HttpJobDefinitionDao dao;
  private final Clock clock;

  public HttpJobRepository(HttpJobDefinitionDao dao,
                           Clock clock) {
    this.dao = dao;
    this.clock = clock;
  }

  public List<HttpJobDefinition> findDueForExecutionNow() {
    return dao.getAll().stream().filter((el) -> {
      ZonedDateTime now =  ZonedDateTime.now(clock);
      ZonedDateTime pastExec = now.minusMinutes(1);

      CronExpression c = CronExpression.createWithoutSeconds(el.getCronExpr());
      ZonedDateTime cronNextExec = c.nextTimeAfter(pastExec, now.plusMinutes(1));
      return equals(now, cronNextExec);
    }).collect(Collectors.toList());
  }

  public boolean equals(ZonedDateTime d1, ZonedDateTime d2) {
    return d1.getMonthValue() == d2.getMonthValue() &&
            d1.getDayOfMonth() ==  d2.getDayOfMonth() &&
            d1.getYear() == d2.getYear() &&
            d1.getHour() == d2.getHour() &&
            d1.getMinute() == d2.getMinute();
  }
}
