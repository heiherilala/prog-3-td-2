package app.foot.repository.entity.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class ExceptionTypt {
  private Date dateTime;
  private String message;
  private String concerns;
}